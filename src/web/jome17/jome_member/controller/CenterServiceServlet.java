package web.jome17.jome_member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.MemberBean;
import web.jome17.jome_member.service.FriendShipService;
import web.jome17.jome_member.service.JomeMemberService;

@WebServlet("/jome_member/CenterServiceServlet")
public class CenterServiceServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			req.setCharacterEncoding("UTF-8");
			jsonIn = json2In(req);
			System.out.println("jsonIn:"+jsonIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String action = jsonIn.get("action").getAsString();
		String outStr = "";
		JsonObject jsonOut = new JsonObject();
		switch(action) {
				//搜尋帳號
			case "searchMember":
				MemberBean searchMember = null;
				JomeMemberService jMemberService = new JomeMemberService();
				searchMember = jMemberService.selectMemberOne(jsonIn.get("account").getAsString());
				int searchResult = -1;		//查無此帳號
				if(searchMember != null) {
					searchResult = 1;		//找到此帳號
					jsonOut.addProperty("searchMember", GSON.toJson(searchMember));
				}
				jsonOut.addProperty("searchResult", searchResult);
				outStr = jsonOut.toString();
				resp.setContentType(CONTENT_TYPE);
				writeJson(resp, outStr);
				break;
				
				//取得朋友列表
			case "getFriendList":
				List<MemberBean> friends = null;
				String memberId = jsonIn.get("memberId").getAsString();
				FriendShipService fsService = new FriendShipService();
				friends = fsService.selectMyFriend(memberId);
				int friendsResult = -1;		//沒有朋友
				if(friends != null) {
					jsonOut.addProperty("friends", GSON.toJson(friends));
					friendsResult = 1;			//有朋友
				}
				jsonOut.addProperty("friendsResult", friendsResult);
				outStr = jsonOut.toString();
				System.out.println("outStr: " + outStr);
				resp.setContentType(CONTENT_TYPE);
				writeJson(resp, outStr);
				break;
				
				//確認兩id關係
			case "checkRelation":
				String mainId = jsonIn.get("mainId").getAsString();
				String friendId = jsonIn.get("friendId").getAsString();
				int resultCode = new FriendShipService().identifyRelation(mainId, friendId);
				jsonOut.addProperty("resultCode", resultCode);
				outStr = jsonOut.toString();
				resp.setContentType(CONTENT_TYPE);
				writeJson(resp, outStr);
				break;
				
			case "getAllMember":
				List<MemberBean> members = null;
				String mId = jsonIn.get("ID").getAsString();
				JomeMemberService memberService = new JomeMemberService();
				int membersResult = -1;		//沒有member
				members = memberService.searchNearBy(mId);
				if(members != null) {
					List<MemberBean> users = new ArrayList<MemberBean>();
					for(MemberBean member : members) {
						if(!member.getMember_id().equals(mId)) {
							users.add(member);
						}
					}
					jsonOut.addProperty("users", GSON.toJson(users));
					membersResult = 1;			//有member
				}
				jsonOut.addProperty("membersResult", membersResult);
				outStr = jsonOut.toString();
				System.out.println(outStr);
				resp.setContentType(CONTENT_TYPE);
				writeJson(resp, outStr);
				break;
				
			default:
				break;
		}
	}
	
	private void writeJson(HttpServletResponse resp, String outStr) {
		try(PrintWriter pw = resp.getWriter()) {
			pw.print(outStr);
		} catch (Exception e) {
			e.printStackTrace();
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

}
