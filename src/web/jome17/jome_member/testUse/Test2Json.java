package web.jome17.jome_member.testUse;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_member.service.GroupService;
import web.jome17.main.DateUtil;

public class Test2Json {

	public static void main(String[] args) {
		
		Date nowDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nowDate);
		calendar.add(Calendar.MINUTE, 3);
		Date groupEndUpTime = calendar.getTime();
		
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(nowDate);		
		calendar1.add(Calendar.MINUTE, 2);
		Date assembleTime = calendar1.getTime();
		
		PersonalGroup testGroup = null;
		testGroup = new PersonalGroup(1+"", new DateUtil().getDateTimeId(), "testTimeTask", new DateUtil().date2StrHm(assembleTime), new DateUtil().date2StrHm(calendar.getTime()), new DateUtil().date2StrHm(nowDate), 10, 5, "testTesttest");
		testGroup.setAttenderStatus(1);
		testGroup.setRole(1);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("action", "creatAGroup");
		jsonObject.addProperty("inGroup", new Gson().toJson(testGroup));
		System.out.println(jsonObject.toString());
		
	}

}
