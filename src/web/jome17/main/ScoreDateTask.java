package web.jome17.main;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import web.jome17.jome_member.dao.CommonDao;
import web.jome17.jome_member.dao.ScoreDaoimpl;
import web.jome17.jome_member.service.GroupService;

public class ScoreDateTask extends TimerTask{

	private GroupService groupService;
	
	
	
	@Override
	public void run() {
		System.out.println("Task執行時間: " + new Date());
//		groupService.createScoreTable(attenders);
	}

}
