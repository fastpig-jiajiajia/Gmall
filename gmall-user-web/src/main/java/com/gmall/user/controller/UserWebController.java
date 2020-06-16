package com.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.gmall.entity.UmsMember;
import com.gmall.entity.UmsMemberReceiveAddress;
import com.gmall.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.DeclareRoles;
import java.util.List;

@RestController
public class UserWebController {

    @Value("${SECRETKEY}")
    private String SECRETKEY;
    @Value("${token}")
    private String token;

    @Reference
    private UserService userService;

    @RequestMapping("getReceiveAddressByMemberId")
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId){

        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = userService.getReceiveAddressByMemberId(memberId);

        return umsMemberReceiveAddresses;
    }

    @HystrixCommand(fallbackMethod = "getAllUserCallBack")
    @RequestMapping("/getAllUser")
    public List<UmsMember> getAllUser(){

        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }

    @RequestMapping("/index")
    public String index(){
        return "hello user";
    }


    @RequestMapping("/videoCall")
    public ModelAndView videoCall(){
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/getSecretKey", method = RequestMethod.POST)
    public String getSecretKey(@RequestBody  String param){
        JSONObject paramJson = JSONObject.parseObject(param);
        String token = paramJson.getString("token");

        JSONObject resultJson = new JSONObject();
        resultJson.put("secretKey", this.SECRETKEY);
        if(this.token.equals(token)){
            return resultJson.toJSONString();
        }
        return null;
    }


    /**
     * 熔断方法的返回类型要一致
     * @return
     */
    public List<UmsMember> getAllUserCallBack(){
        return null;
    }

}
