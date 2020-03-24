package com.gmall.user.job.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 *spring boot 内置的定时 Job
 */

@Component
public class SyncUserJob {
    private String name = "1";


//    @Scheduled(cron = "*/6 * * * * ?")  // 每六秒执行一次
//    @Scheduled(fixedRate = 6000)   // 每六秒执行一次
    public void job1(){
        System.out.println(name);
    }

}
