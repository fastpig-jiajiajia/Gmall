package com.gmall.user.job.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CronSyncUserJob implements Job {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        System.out.println("CRON ----> schedule job1 is running ... + " + name + "  ---->  " + dateFormat.format(new Date()));
    }
}
