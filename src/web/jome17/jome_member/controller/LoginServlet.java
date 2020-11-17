package web.jome17.jome_member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import web.jome17.jome_member.bean.MemberBean;
import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_member.service.FriendShipService;
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
				jsonOut.addProperty("loginMember", GSON.toJson(member));
			}
			jsonOut.addProperty("loginResultCode", loginResultCode);
			outStr = jsonOut.toString();
			System.out.println(outStr);
			resp.setContentType(CONTENT_TYPE);
			writeJson(resp, outStr);
			break;
			
//			account得到member物件
		case "loginGet":
			member = new JomeMemberService().selectMemberOne(member.getAccount());
			outStr = GSON.toJson(member);
			System.out.println("jsonOut:" + outStr);
			resp.setContentType(CONTENT_TYPE);
			writeJson(resp, outStr);
			break;
			
//			id得到member物件(進入好友或陌生人頁面)
		case "idGet":
			int idGetResult = -1;
			String friendId = jsonIn.get("friendId").getAsString();
			String mainId = jsonIn.get("mainId").getAsString();
			member = new JomeMemberService().selectMemberById(friendId);
			if(member != null) {
				idGetResult = 1;
				int relationCode = new FriendShipService().identifyRelation(mainId, friendId);
//				member.setFriendCount(mService.getFriendCount(member.getMember_id())); 
//				member.setScoreAverage(mService.getScoreAverage(member.getMember_id()));
				jsonOut.addProperty("relationCode", relationCode);
				jsonOut.addProperty("idMember", GSON.toJson(member));
			}
			jsonOut.addProperty("idGetResult", idGetResult);
			outStr = jsonOut.toString();
			System.out.println(outStr);
			resp.setContentType(CONTENT_TYPE);
			writeJson(resp, outStr);
			break;
			
//			拿大頭圖
		case "getImage":
			byte[] image = null;
			String memberId = jsonIn.get("memberId").getAsString();
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
			
		case "update":
			byte[] imageUpdate = null;
			String imageBase64 = jsonIn.get("imageBase64").getAsString();
			if(!imageBase64.equals("noImage")) {
				if (!imageBase64.equals("noImage")) {
					imageUpdate = Base64.getMimeDecoder().decode(imageBase64);
				}
			}
			member = GSON.fromJson(jsonIn.get("memberUp").getAsString(), MemberBean.class);
			int resultCode = -1;
			resultCode = new JomeMemberService().updateMember(member, imageUpdate);
			jsonOut.addProperty("resultCode", resultCode);
			outStr = jsonOut.toString();
			System.out.println("jsonOut:" + outStr);
			writeJson(resp, outStr);
			break;
		
		case "scoreCounts":
			String memId = jsonIn.get("memberId").getAsString();
			outStr = new JomeMemberService().getScoreCounts(memId);
			System.out.println("scoreCountsStr :" + outStr);
			writeJson(resp, outStr);
			break;
			
		case "getAll":
			List<MemberBean> members = new JomeMemberService().getAllMember();
			outStr = GSON.toJson(members);
			System.out.println("membersStr :" + outStr);
			writeJson(resp, outStr);
			break;
			
		default:
			member = new MemberBean();
			outStr = GSON.toJson(member);
			System.out.println(outStr);
			resp.setContentType(CONTENT_TYPE);
			writeJson(resp, outStr);
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
