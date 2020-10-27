package web.jome17.jome_member.testUse;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class testTimer {
	
	
	public testTimer() {
		super();
	}


	TimerTask testTask = new TimerTask() {
		
		@Override
		public void run() {
			System.out.println("TaskTime: " + new Date());
			System.gc();
			cancel();
		}
	};
	
	
	public static void main(String[] args) {
		Timer timer = new Timer();
		Date nowTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nowTime);
		calendar.add(Calendar.SECOND, 10);
		
		System.out.println("beforeTask: " + nowTime);
		timer.schedule(new testTimer().testTask,calendar.getTime());
		
		for(int i=0; i<=10; i++) {
			System.out.println(i);
		}
	}
}


