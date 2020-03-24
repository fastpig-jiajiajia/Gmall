package com.gmall.user.job.runner;

import com.gmall.user.job.scheduler.CronSyncUserScheluerJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class CronRunner implements CommandLineRunner {
    @Autowired
    public CronSyncUserScheluerJob cronSyncUserScheluerJob;

    @Override
    public void run(String... args) throws Exception {
    //    cronSyncUserScheluerJob.scheduleJobs();
        System.out.println(">>>>>>>>>>>>>>>定时任务开始执行<<<<<<<<<<<<<");
    }
}

