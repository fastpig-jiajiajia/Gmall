package com.gmall.user.job.scheduler;

import com.gmall.user.job.job.CronSyncUserJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class CronSyncUserScheluerJob {


    private SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

    /**
     * 任务配置
     * @param scheduler
     * @throws SchedulerException
     */
    private void scheduleJob1(Scheduler scheduler) throws SchedulerException {
        // 构建 job 信息
        JobDetail jobDetail = JobBuilder.newJob(CronSyncUserJob.class).withIdentity("user-web-cronSyncUserJob1", "group1").build();
        // 触发时间，6的倍数秒执行 也就是 6 12 18 24 30 36 42 ....
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0/6 * * * * ?");
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("user-web-cronSyncUserJob1", "group1")
                .usingJobData("name","徐锐").withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    /**
     * 启动任务
     * @throws SchedulerException
     */
    public void scheduleJobs() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduleJob1(scheduler);
    }
}
