package web.jome17.jome_member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.org.apache.bcel.internal.generic.NEW;

import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_member.service.GroupService;
import web.jome17.jome_notify.service.NotifyService;
import web.jome17.main.DateUtil;
import web.jome17.main.ImageUtil;


@WebServlet("/jome_member/GroupOperateServlet")
public class GroupOperateServlet extends HttpServlet {
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		jsonIn = json2In(req);
		System.out.println("jsonIn:" + jsonIn);

		String action = jsonIn.get("action").getAsString();
		String outStr = "";
		JsonObject jsonOut = new JsonObject();
		GroupService gService = new GroupService();
		int notiInsertResult;

		if (action.equals("getImage")) {
			//拿圖
			byte[] image = null;
		
			String groupId = jsonIn.get("groupId").getAsString();
			
			int imageSize = jsonIn.get("imageSize").getAsInt();
			image = gService.groupImage(groupId);
			if(image != null) {
				OutputStream ops = resp.getOutputStream();
				image = ImageUtil.shrink(image, imageSize);
				resp.setContentType("image/*");
				resp.setContentLength(image.length);
				ops.write(image);
			}
			
		} else {
			switch (action) {
			// 查所有揪團
			case "getAll":
//				String myId = jsonIn.get("memberId").getAsString();
//				List<PersonalGroup> mGroups = gService.getMemberAllGroups(myId);
				List<PersonalGroup> pGroups = gService.getAllGroups();
				int getAllResult = 0;
				if(pGroups != null) {
					getAllResult = 1;
					for(int i= 0; i < pGroups.size(); i++) {
						if(pGroups.get(i).getGroupStatus() == 1) {
							String groupId = pGroups.get(i).getGroupId();
							pGroups.get(i).setJoinCountNow(gService.getGroupCount(groupId));
							if(pGroups.get(i).getGroupLimit() == pGroups.get(i).getJoinCountNow()) {
								pGroups.get(i).setGroupStatus(2);
								gService.updateGroup(pGroups.get(i), null);
							}
						}
					}
					jsonOut.addProperty("allGroup", GSON.toJson(pGroups));
				}
				jsonOut.addProperty("getAllResult", getAllResult);
				break;

			// 查單筆揪團
			case "getAGroup":
				int getAResult = -1;
				String groupId = jsonIn.get("groupId").getAsString();
				PersonalGroup pGroup = gService.searchAGroup(groupId);
				if (pGroup != null) {
					jsonOut.addProperty("group", GSON.toJson(pGroup));
					getAResult = 1;
				}
				jsonOut.addProperty("getAResult", getAResult);
				break;
			
			//查我與揪團的關係
			case "getMyGroup":
				int myGroupResult = -1;
				String myGroupId = jsonIn.get("groupId").getAsString();
				String myId = jsonIn.get("memberId").getAsString();
				PersonalGroup myGroup = gService.inquirePerGroups(myId, myGroupId);
				
				if(myGroup != null) {
					myGroupResult = gService.getGroupCount(myGroupId);
					myGroup.setJoinCountNow(myGroupResult);
					jsonOut.addProperty("myGroup", GSON.toJson(myGroup));
					myGroupResult = 1;
				}
				break;
				
			// 建立揪團
			case "creatAGroup":
				int creatResult = -1;
				//處理Base64 轉 byte[]
				byte[] imageCreate = null;
				String imageCreateStr = jsonIn.get("imageBase64").getAsString();
				imageCreate = Base64.getMimeDecoder().decode(imageCreateStr);
				
				//處理 PersonalGroup資料
				PersonalGroup inPGroup = GSON.fromJson(jsonIn.get("inGroup").getAsString(), PersonalGroup.class);
				inPGroup.setgImage(imageCreate);
				String createGroupId = inPGroup.getGroupId();	//client端已包裝完成，Server新增就好
				creatResult = gService.creatGroup(inPGroup);

				/*
				 * 集合時間到：groupStatus = 2
				 */
				TimerTask assembleTime = new TimerTask() {
					@Override
					public void run() {
						inPGroup.setGroupStatus(3);
						gService.updateGroup(inPGroup, null);
					}
				};
				Timer assembleTimer = new Timer();
				assembleTimer.schedule(assembleTime, new DateUtil().str2Date(inPGroup.getAssembleTime()));
				/*
				 * 結束時間到：1.建立評分表，2.請團員評分
				 */
				TimerTask groupEndTimeTask = new TimerTask() {
					@Override
					public void run() {
						// 1.建立評分表
//						GroupService groupService = new GroupService();
						List<PersonalGroup> attenders = gService.getAllAttenders(createGroupId);
						if(attenders == null) {
							System.out.println("TimerTask is fail.");
							System.gc();
							cancel();
						}else {
							int scoreTableCount = gService.createScoreTable(attenders);
							for(PersonalGroup attender: attenders) {
								System.out.println(attender.getNickname());
							}
							System.out.println("insertScoreTableCount: " + scoreTableCount+ "\t" + new Date());
							System.gc();
							cancel();
						}

					}
				};
				Timer groupEndTimer = new Timer();
				groupEndTimer.schedule(groupEndTimeTask, new DateUtil().str2Date(inPGroup.getGroupEndTime()));
				
				jsonOut.addProperty("creatResult", creatResult);
				break;
				
			// 加入揪團	
			case "joinGroup":
				int jointResult = -1;
				PersonalGroup joinGroup = GSON.fromJson(jsonIn.get("groupBean").getAsString(), PersonalGroup.class);
				jointResult = gService.joinGroup(joinGroup);
				//新增通知訊息
				notiInsertResult = new NotifyService().insertGroupNoti(joinGroup);
				if (notiInsertResult == 1) {
					System.out.println("Group Notification Inserted Successfully");
				}else {
					System.out.println("Group Notification Insert Failed");
				}
				jsonOut.addProperty("jointResult", jointResult);
				break;
				
			// 修改揪團
			case "updateGroup":
				int updateResult = -1;
				byte[] imageUpdate = null;
				PersonalGroup groupUp = null;
				String imageBase64 = jsonIn.get("imageBase64").getAsString();
				if (imageBase64 != null) {
					imageUpdate = Base64.getMimeDecoder().decode(imageBase64);
				}
				groupUp = GSON.fromJson(jsonIn.get("gruopUp").getAsString(), PersonalGroup.class);
				updateResult = gService.updateGroup(groupUp, imageUpdate);
				jsonOut.addProperty("updateResult", updateResult);
				break;
				
			// 退團
			case "dropOutGroup":
				PersonalGroup groupDrop = null;
				groupDrop = GSON.fromJson(jsonIn.get("gruopUp").getAsString(), PersonalGroup.class);
				int dropResult = gService.dropGroup(groupDrop);
				//新增通知訊息
				notiInsertResult = new NotifyService().insertGroupNoti(groupDrop);
				if (notiInsertResult == 1) {
					System.out.println("Group Notification Inserted Successfully");
				}else {
					System.out.println("Group Notification Insert Failed");
				}
				jsonOut.addProperty("dropResult", dropResult);
				break;
			
			case "getSelfRecord":
				String memberId = jsonIn.get("memberId").getAsString();
				List<PersonalGroup> myGroups = new ArrayList<PersonalGroup>();
				myGroups = gService.getMemberAllGroups(memberId);
				int myGroupsResult = -1;
				if(myGroups != null) {
					myGroupsResult = 1;
					jsonOut.addProperty("myGroups", GSON.toJson(myGroups));
				}
				jsonOut.addProperty("myGroupsResult", myGroupsResult);
				break;
				
			default:
				break;
			}
			outStr = jsonOut.toString();
			resp.setContentType(CONTENT_TYPE);
			writeJson(resp, outStr);
		}

	}

	private void writeJson(HttpServletResponse resp, String outStr) {
		try (PrintWriter pw = resp.getWriter();) {
			pw.print(outStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JsonObject json2In(HttpServletRequest req) {
		StringBuilder jsonIn = new StringBuilder();
		try (BufferedReader bf = req.getReader();) {
			String line;
			while ((line = bf.readLine()) != null) {
				jsonIn.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return GSON.fromJson(jsonIn.toString(), JsonObject.class);
	}
}
