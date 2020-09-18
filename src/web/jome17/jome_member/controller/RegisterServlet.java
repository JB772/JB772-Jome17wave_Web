package web.jome17.jome_member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import web.jome17.jome_member.bean.JomeMember;
import web.jome17.jome_member.service.JomeMemberService;


@WebServlet("/jome_member/RegisterServlet")
public class RegisterServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			req.setCharacterEncoding("UTF-8");
			jsonIn = json2In(req);
			System.out.println("jsonIn:" + jsonIn.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String action = jsonIn.get("action").getAsString();
		JsonObject jsonOut = new JsonObject();
		String outStr = "";
		int resultCode = 0;
		JomeMember member;
		switch(action){
			case "register":
				member = GSON.fromJson(jsonIn.get("jomeMember").getAsString(), JomeMember.class);
				resultCode = new JomeMemberService().register(member);
				jsonOut.addProperty("resultCode", resultCode);
				outStr = jsonOut.toString();
				System.out.println("jsonOut:" + outStr);
				writeJson(resp, outStr);
				break;
		}
	}
	
	private void writeJson(HttpServletResponse resp, String outStr) {
		resp.setContentType(CONTENT_TYPE);
		try(PrintWriter pw = resp.getWriter()) {
			pw.print(outStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private JsonObject json2In(HttpServletRequest req) {
		StringBuilder json = new StringBuilder();
		try (BufferedReader br = req.getReader()){
			String line;
			while ((line = br.readLine()) != null) {
				json.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return GSON.fromJson(json.toString(), JsonObject.class);
	}
}
