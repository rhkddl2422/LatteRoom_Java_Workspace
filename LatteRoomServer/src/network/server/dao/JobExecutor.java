package network.server.dao;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import network.server.service.ServerService;

public class JobExecutor implements Job {
	
	ServerService service = ServerService.getInstance();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
//		System.out.println("[" + "Execute / " + new Date() + "]" + context.getJobDetail().toString());
		// Arduino로 신호 보내는 Service Method 실행
		service.triggerAlert();
	}
	
}
