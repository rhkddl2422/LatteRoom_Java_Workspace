package network.server.dao;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import network.server.vo.Alert;

public class AlertScheduler {
	
	private String settedTime;
	private JobDetail job;
	private Trigger trigger;
	
	private SchedulerFactory factory = new StdSchedulerFactory();
	private Scheduler scheduler;
	
	public AlertScheduler() {
		
	}
	
	public void set(Alert alert) {
		try {
			scheduler = factory.getScheduler();
			if(alert.isFlag() == false) {
				if(scheduler.isStarted()) {
					scheduler.shutdown();
				}
				return;
			}
			settedTime = "0 " + alert.getMin() + " " + alert.getHour() + 
					" ? * " + alert.getWeeks();
			
//			BasicConfigurator.configure();
			
			job = newJob(JobExecutor.class)
					.withIdentity("job", Scheduler.DEFAULT_GROUP)
					.build();
			trigger = newTrigger()
					.withIdentity("trigger", Scheduler.DEFAULT_GROUP)
					.startNow()
					.withSchedule(cronSchedule(settedTime))
					.build();
			
			if(scheduler.isStarted()) {
				scheduler.clear();
				scheduler.scheduleJob(job, trigger);
				
			} else {
				scheduler.start();
				scheduler.scheduleJob(job, trigger);
			}
		} catch (SchedulerException e1) {
			e1.printStackTrace();
		}
		
	}
	
}

