package com.gmall.user.controller;

import com.gmall.entity.UmsMember;
import com.gmall.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/userService")
public class UserServiceController {

    /**
     * 日志
     */
    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Resource(name="userServiceImpl")
    private UserService userService;

    @RequestMapping("/getAllUser")
    public List<UmsMember> getAllUser(){
        System.out.println(userService);
        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }

    /**
     * @CacheEvict是用来标注在需要清除缓存元素的方法或类上的。当标记在一个类上时表示其中所有的方法的执行都会触发缓存的清除操作。
     * @CacheEvict可以指定的属性有value、key、condition、allEntries和beforeInvocation。
        其中value、key和condition的语义与@Cacheable对应的属性类似；allEntries是boolean类型，表示是否需要清除缓存中的所有元素。
        默认为false，表示不需要。
        当指定了allEntries为true时，Spring Cache将忽略指定的key。有的时候我们需要Cache一下清除所有的元素，这比一个一个清除元素更有效率。
     */
    @RequestMapping("clearECache")
    public void clearECache(){
        log.info("clear Ecache");
        userService.clearEhCache();
    }
}
