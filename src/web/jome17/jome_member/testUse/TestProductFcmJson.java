package web.jome17.jome_member.testUse;

import com.google.gson.JsonObject;

public class TestProductFcmJson {
	
	public static void main(String[] args) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("action", "singleFcm");
		jsonObject.addProperty("title", "TestTitle");
		jsonObject.addProperty("body", "TestBody");
		jsonObject.addProperty("data", "TestData");
		System.out.println(jsonObject.toString());
	}
}
