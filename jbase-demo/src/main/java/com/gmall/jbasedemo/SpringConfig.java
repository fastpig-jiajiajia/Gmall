package com.gmall.jbasedemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author rui.xu
 * @date 2020/10/15 17:26
 * @description
 **/
@Component
public class SpringConfig {

    @Value("#{'${authority.excludeUri:}'.empty ? null : '${authority.excludeUri:}'.split(',')}")
    public Set<String> excludeUri;
}
