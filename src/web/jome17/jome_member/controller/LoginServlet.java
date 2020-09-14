package web.jome17.jome_member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
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
import web.jome17.main.ImageUtil;

@WebServlet("/")
public class LoginServlet extends HttpServlet{


	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;
	private JomeMember member;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			req.setCharacterEncoding("UTF-8");
			jsonIn = json2In(req);
			System.out.println("jsonIn:"+jsonIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String action = jsonIn.get("action").getAsString();
		String outStr = "";
		JsonObject jsonOut = new JsonObject();
		JomeMemberService mService = new JomeMemberService();
		switch (action) {
		
		case "login":
			member = GSON.fromJson(jsonIn.get("member").getAsString(), JomeMember.class);
			//System.out.println(member.getAccount()+member.getPassword());
			int resultCode = mService.login(member.getAccount(), member.getPassword());
//			int resultCode =memService.login(member.getAccount(), member.getPassword());
			jsonOut.addProperty("resultCode", resultCode);
			outStr = jsonOut.toString();
			System.out.println("jsonOut:" + outStr);
			resp.setContentType(CONTENT_TYPE);
			writeJson(resp, outStr);
			break;
		case "loginGet":
			member = new JomeMemberService().selectMemberOne(member.getAccount());
//			jsonOut.addProperty("FragmentCode", 1);
//			jsonOut.addProperty("member", GSON.toJson(member));
			outStr = GSON.toJson(member);
			System.out.println("jsonOut:" + outStr);
			resp.setContentType(CONTENT_TYPE);
			writeJson(resp, outStr);
			break;
		case "getImage":
			byte[] image = null;
			OutputStream ops = resp.getOutputStream();
			String account =jsonIn.get("account").getAsString();
			int imageSize = jsonIn.get("imageSize").getAsInt();
			image = mService.getImage(account);
			System.out.println("image:"+image);
			if(image != null) {
				image = ImageUtil.shrink(image, imageSize);
				//						image/jpeg
				resp.setContentType("image/*");
				resp.setContentLength(image.length);
				ops.write(image);
			}
			break;
		default:
			break;
		}
	}
	
	
	
	private JsonObject json2In(HttpServletRequest req) {
		StringBuilder jsonIn = new StringBuilder();
		try (BufferedReader br = req.getReader()){
			String line;
			while ((line = br.readLine()) != null) {
				jsonIn.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return GSON.fromJson(jsonIn.toString(), JsonObject.class);
	}
	
	private void writeJson(HttpServletResponse resp, String outStr) {
		try(PrintWriter pw = resp.getWriter()) {
			pw.print(outStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
