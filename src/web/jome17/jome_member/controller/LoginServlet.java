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

import web.jome17.jome_member.bean.MemberBean;
import web.jome17.jome_member.service.JomeMemberService;
import web.jome17.main.ImageUtil;

@WebServlet("/jome_member/LoginServlet")
public class LoginServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;
	private MemberBean member;
	
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
		

//			確認account, password
		case "checkIsValid":
			int loginResultCode = -1;
			String accountLogin = jsonIn.get("account").getAsString();
			String passwordLogin = jsonIn.get("password").getAsString();
			member = mService.login(accountLogin, passwordLogin);
			if(member != null) {
				loginResultCode = 1;		//account & password correct
				member.setFriendCount(mService.getFriendCount(member.getMember_id())); 
				member.setScoreAverage(mService.getScoreAverage(member.getMember_id()));
				jsonOut.addProperty("loginMember", GSON.toJson(member));
			}
			jsonOut.addProperty("loginResultCode", loginResultCode);
			outStr = jsonOut.toString();
			System.out.println(outStr);
			resp.setContentType(CONTENT_TYPE);
			writeJson(resp, outStr);
			break;
			
//			得到member物件
		case "loginGet":
			member = new JomeMemberService().selectMemberOne(member.getAccount());
			outStr = GSON.toJson(member);
			System.out.println("jsonOut:" + outStr);
			resp.setContentType(CONTENT_TYPE);
			writeJson(resp, outStr);
			break;
			
//			拿大頭圖
		case "getImage":
			byte[] image = null;
			String memberId = jsonIn.get("MEMBER_ID").getAsString();
			int imageSize = jsonIn.get("imageSize").getAsInt();
			image = mService.getImage(memberId);
			System.out.println("image:"+image);
			
			if(image != null) {
				OutputStream ops = resp.getOutputStream();
				image = ImageUtil.shrink(image, imageSize);
				//						image/jpeg
				resp.setContentType("image/*");
				resp.setContentLength(image.length);
				ops.write(image);
			}else {
				
				writeJson(resp, outStr);
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
