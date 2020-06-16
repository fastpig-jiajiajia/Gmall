package com.gmall.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;



@Component
@Configuration
public class SendSMSUtil {

    @Value("${aliyun.regionId : cn-hangzhou}")
    private static String regionId = "";
    @Value("${aliyun.accessKeyId : LTAI4GHC33gdfaBMFq1jp8jR}")
    private static String accessKeyId = "";
    @Value("${aliyun.accessSeceret : S6ja1X3pwM0GJFEI2YCc6vsAKdod6G}")
    private static String accessSeceret = "";
    @Value("${aliyun.SignName : 苏州城市生活}")
    private static String SignName = "";
    @Value("${aliyun.TemplateCode : SMS_188620002}")
    private static String TemplateCode = "";


    @Bean
    public SendSMSUtil getSendSMSUtil(){
        return new SendSMSUtil();
    }


    public String sendSMS(Map<String, String> params) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessSeceret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        String phone = params.get("phone");
        String templateParam = params.get("templateParam");

        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", SignName);
        request.putQueryParameter("TemplateCode", TemplateCode);
        request.putQueryParameter("TemplateParam", templateParam);
        try {
            CommonResponse response = client.getCommonResponse(request);
            return response.getData();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

}






