package com.gmall.redis.cluster.factory;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @version V1.0
 * @Modified By:James Created in 23:07 2019/1/1
 */
public class JedisClusterFactory implements FactoryBean<JedisCluster>, InitializingBean {
    private Resource addressConfig;
    private String addressKeyPrefix;
    private JedisCluster jedisCluster;
    private Integer timeout;
    private Integer maxRedirections;
    private GenericObjectPoolConfig genericObjectPoolConfig;
    private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

    public JedisClusterFactory() {
    }

    public JedisCluster getObject() throws Exception {
        return this.jedisCluster;
    }

    public Class<? extends JedisCluster> getObjectType() {
        return this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class;
    }


    public boolean isSingleton() {
        return true;
    }

    private Set<HostAndPort> parseHostAndPort() throws Exception {
        try {
            Properties ex = new Properties();
            ex.load(this.addressConfig.getInputStream());
            HashSet haps = new HashSet();
            Iterator i$ = ex.keySet().iterator();

            while (i$.hasNext()) {
                Object key = i$.next();
                if (((String) key).startsWith(this.addressKeyPrefix)) {
                    String val = (String) ex.get(key);
                    boolean isIpPort = this.p.matcher(val).matches();
                    if (!isIpPort) {
                        throw new IllegalArgumentException("ip or port error!!");
                    }

                    String[] ipAndPort = val.split(":");
                    HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                    haps.add(hap);
                }
            }

            return haps;
        } catch (IllegalArgumentException var9) {
            throw var9;
        } catch (Exception var10) {
            throw new Exception("Exceptions", var10);
        }
    }

    public void afterPropertiesSet() throws Exception {
        Set haps = this.parseHostAndPort();

        this.jedisCluster = new JedisCluster(haps, this.timeout.intValue(), this.maxRedirections.intValue(), this.genericObjectPoolConfig);


    }

    public void setAddressConfig(Resource addressConfig) {
        this.addressConfig = addressConfig;
    }

    public void setTimeout(int timeout) {
        this.timeout = Integer.valueOf(timeout);
    }

    public void setMaxRedirections(int maxRedirections) {
        this.maxRedirections = Integer.valueOf(maxRedirections);
    }

    public void setAddressKeyPrefix(String addressKeyPrefix) {
        this.addressKeyPrefix = addressKeyPrefix;
    }

    public void setGenericObjectPoolConfig(GenericObjectPoolConfig genericObjectPoolConfig) {
        this.genericObjectPoolConfig = genericObjectPoolConfig;
    }
}
