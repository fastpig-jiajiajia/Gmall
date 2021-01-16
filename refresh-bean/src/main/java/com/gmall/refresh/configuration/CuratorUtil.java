package com.gmall.refresh.configuration;

import com.gmall.refresh.config.RefreshConfig;
import com.gmall.refresh.constant.RefreshConstant;
import com.gmall.refresh.scope.RefreshScope;
import com.gmall.refresh.scope.RefreshScopeRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class CuratorUtil implements ApplicationContextAware {

    @Autowired
    private Environment environment;

    @Autowired
    private RefreshConfig refreshConfig;

    @Autowired
    private CuratorFramework client;

    @Value(("${refresh.enable:false}"))
    private boolean enbale;

    private static ConfigurableApplicationContext applicationContext;

    private static OriginTrackedMapPropertySource propertySource;

    private ConcurrentHashMap map = new ConcurrentHashMap();

    private BeanDefinitionRegistry beanDefinitionRegistry;

    /**
     * 初始化 Zookeeper节点，并监听
     */
    @PostConstruct
    public void init() {
        if (!enbale) {
            return;
        }
        RefreshScopeRegistry refreshScopeRegistry = (RefreshScopeRegistry) applicationContext.getBean("refreshScopeRegistry");
        beanDefinitionRegistry = refreshScopeRegistry.getBeanDefinitionRegistry();

        client.start();
        try {
            Stat stat = client.checkExists().forPath(refreshConfig.getPath());
            if (stat != null) {
                client.delete().deletingChildrenIfNeeded().withVersion(stat.getVersion()).forPath(refreshConfig.getPath());
            }
            addConfig2zk();
            createZookeeperSpringProperty();
            // 把config下面的子节点加载到spring容器的属性对象中
            addChildToSpringProperty(client, refreshConfig.getPath());
//            nodeCache(client,path);
            // 监听节点
            childNodeCache(client, refreshConfig.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将需要动态刷新的配置添加进 Zookpper中
     */
    private void addConfig2zk() throws Exception {
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                forPath(refreshConfig.getPath(), "zookeeper refresh config".getBytes());
        for (Map.Entry<String, String> entry : RefreshConfig.refreshConfigMap.entrySet()) {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                    forPath(entry.getKey(), entry.getValue().getBytes());
        }
    }

    /**
     * 获取Zookeeper节点的配置信息
     * 并将这些配置信息添加到 Spring ConfigurableApplicationContext
     *
     * @param client zookeeper 客户端
     * @param path   zookper 配置节点路径
     */
    private void addChildToSpringProperty(CuratorFramework client, String path) {
        if (!checkExistsSpringProperty()) {
            // 如果不存在zookeeper的配置属性对象则创建
            createZookeeperSpringProperty();
        }
        //把config目录下的子节点添加到 zk的PropertySource对象中
        Map zkmap = (ConcurrentHashMap) propertySource.getSource();
        try {
            List<String> childPaths = client.getChildren().forPath(path);
            for (String childPath : childPaths) {
                zkmap.put(childPath, client.getData().forPath(path + RefreshConstant.PREFIX + childPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向 ConfigurableApplicationContext 容器中添加自定义的配置文件信息
     */
    private void createZookeeperSpringProperty() {
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        OriginTrackedMapPropertySource zookeeperSource = new OriginTrackedMapPropertySource(RefreshConstant.ZK_PROPERTY_NAME, map);
        propertySources.addLast(zookeeperSource);
        propertySource = zookeeperSource;
    }

    /**
     * 查看 Spring ApplicationConext中是否存在自定义的配置文件
     *
     * @return
     */
    private boolean checkExistsSpringProperty() {
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (RefreshConstant.ZK_PROPERTY_NAME.equals(propertySource.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 监听 Zookeeper配置信息节点，并对自定义的Bean缓存容器进行增删改查操作，实现刷新Bean得功能
     *
     * @param client
     * @param path
     */
    private void childNodeCache(CuratorFramework client, String path) {
        try {
            final PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, false);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            System.out.println("增加了节点");
                            addEnv(event.getData(), client);
                            break;
                        case CHILD_REMOVED:
                            System.out.println("删除了节点");
                            delEnv(event.getData());
                            break;
                        case CHILD_UPDATED:
                            System.out.println("更新了节点");
                            addEnv(event.getData(), client);
                            break;
                        default:
                            break;
                    }
                    //对refresh作用域的实例进行刷新
                    if (RefreshConstant.RERESH_TYPES.contains(event.getType())) {
                        refreshBean();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对配置了自定义作用域的 Bean删除，并重新实例化
     * //先删除,,,,思考，如果这时候删除了bean，有没有问题
     * // 删除逻辑走的是自定义作用域的删除方法 RefreshScope remove 方法
     * // 再实例化，实例化时会走自定义作用域的 RefreshScope get 方法进行实例化Bean操作
     */
    private void refreshBean() {
        List<String> beanNames = new ArrayList<>();
        synchronized (RefreshScope.beanMap) {
            Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(beanDefinitionName -> {
                BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);
                if (RefreshConstant.SCOPE_NAME.equals(beanDefinition.getScope())) {
                    applicationContext.getBeanFactory().destroyScopedBean(beanDefinitionName);
                    beanNames.add(beanDefinitionName);
                }
            });
        }
        beanNames.forEach(applicationContext::getBean);
    }

    /**
     * 删除自定义配置文件的配置信息
     *
     * @param childData
     */
    private void delEnv(ChildData childData) {
        ChildData next = childData;
        String childpath = next.getPath();
        ConcurrentHashMap chm = (ConcurrentHashMap) propertySource.getSource();
        chm.remove(childpath.substring(refreshConfig.getPath().length() + 1));
    }

    /**
     * 获取 Zookeeper的节点信息，并刷新到自定义的 properties配置文件中
     *
     * @param childData
     * @param client
     */
    private void addEnv(ChildData childData, CuratorFramework client) {
        ChildData next = childData;
        String childPath = next.getPath();
        String data = null;
        try {
            data = new String(client.getData().forPath(childPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConcurrentHashMap chm = (ConcurrentHashMap) propertySource.getSource();
        chm.put(childPath.substring(refreshConfig.getPath().length() + 1), data);
    }

    /**
     * @param client
     * @param path
     */
    private void nodeCache(final CuratorFramework client, final String path) {

        try {
            // 第三个参数是是否压缩
            // 就是对path节点进行监控，是一个事件模板
            final NodeCache nodeCache = new NodeCache(client, path, false);
            nodeCache.start();

            // 这个就是事件注册
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    byte[] data = nodeCache.getCurrentData().getData();
                    String path1 = nodeCache.getCurrentData().getPath();

                    Object put = map.put(path1.replace("/", ""), new String(data));
                    MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
                    OriginTrackedMapPropertySource zookeeperSource = new OriginTrackedMapPropertySource("zookeeper source", map);
                    propertySources.addLast(zookeeperSource);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 ConfigurableApplicationContext
     *
     * @param context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        CuratorUtil.applicationContext = (ConfigurableApplicationContext) context;
    }
}
