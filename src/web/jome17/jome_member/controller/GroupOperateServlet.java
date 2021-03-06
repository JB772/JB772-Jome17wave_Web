package web.jome17.jome_member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
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
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import java.io.InputStream;
import com.google.gson.reflect.TypeToken;

import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_member.service.GroupService;
import web.jome17.jome_notify.bean.Notify;
import web.jome17.jome_notify.service.NotifyService;
import web.jome17.main.DateUtil;
import web.jome17.main.ImageUtil;


@WebServlet("/jome_member/GroupOperateServlet")
public class GroupOperateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;
	
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
				List<PersonalGroup> pGroups = gService.getAllGroups();
				DateUtil dateUtil = new DateUtil();
				int getAllResult = 0;
				if(pGroups != null) {
					getAllResult = 1;
					for(int i= 0; i < pGroups.size(); i++) {
						if(pGroups.get(i).getGroupStatus() != 3  && pGroups.get(i).getGroupStatus() != 0) {
System.out.println("i:~~~~~~~~" + i);
							String groupId = pGroups.get(i).getGroupId();
							PersonalGroup pGroup = pGroups.get(i);
							if(dateUtil.str2Date(pGroup.getAssembleTime()).before(new Date())) {
//System.out.println(new Date() + pGroup.getGroupName() + " :" + pGroup.getAssembleTime());
								if(pGroup.getGroupStatus() != 3) {
									pGroup.setGroupStatus(3);
									gService.updateGroup(pGroup, null);
								}
							}else {
//System.out.println(new Date() + pGroup.getGroupName() + " :" + pGroup.getAssembleTime());
//System.out.println(pGroup.getGroupName()+" surfPoint: "+ pGroup.getSurfPointId());
								pGroup.setJoinCountNow(gService.getGroupCount(groupId));
								if(pGroup.getGroupLimit() <= pGroup.getJoinCountNow()) {
									if(pGroup.getGroupStatus() != 2) {
										pGroup.setGroupStatus(2);
										gService.updateGroup(pGroup, null);
									}
								}else if(pGroups.get(i).getGroupLimit() > pGroups.get(i).getJoinCountNow()) {
									if(pGroups.get(i).getGroupStatus() != 1) {
										pGroups.get(i).setGroupStatus(1);
										gService.updateGroup(pGroup, null);
									}
								}
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
System.out.println("pGroup.getGroupName(): " + pGroup.getGroupName());
				if (pGroup != null) {
System.out.println("pGroup != null");
					jsonOut.addProperty("group", GSON.toJson(pGroup));
					getAResult = 1;
				}
				jsonOut.addProperty("getAResult", getAResult);
System.out.println("getAResult: " + getAResult);
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
				 * 集合時間到：groupStatus == 3
				 */
				TimerTask assembleTime = new TimerTask() {
					@Override
					public void run() {
						PersonalGroup assembleGroup = gService.searchAGroup(createGroupId);
						if(assembleGroup.getGroupStatus() != 0) {
							inPGroup.setGroupStatus(3);
							gService.updateGroup(inPGroup, null);
						}
					}
				};
				Timer assembleTimer = new Timer();
				assembleTimer.schedule(assembleTime, new DateUtil().str2Date(inPGroup.getAssembleTime()));
				/*
				 * 結束時間到：1.建立評分表，2.建立notify
				 */
				TimerTask groupEndTimeTask = new TimerTask() {
					@Override
					public void run() {
						// 1.建立評分表
						PersonalGroup endUpGroup = gService.searchAGroup(createGroupId);
						if(endUpGroup.getGroupStatus() == 0) {
							System.out.println("Group has been canceled, table Score couldn't build.");
						}else {
							List<PersonalGroup> attenders = gService.getAllAttenders(createGroupId);
							int scoreTableCount = gService.createScoreTable(attenders);
							for(PersonalGroup attender: attenders) {
								System.out.println(attender.getNickname());
							}
							System.out.println("insertScoreTableCount: " + scoreTableCount+ "\t" + new Date());
							
							// 2.建立notify
							int notifyTableCount = new NotifyService().insertNotiForRating(attenders);
							System.out.println("insertScoreTableCount: " + notifyTableCount+ "\t" + new Date());
						}
						System.gc();
						cancel();
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
				String groupHeadId = gService.getGroupHeadId(joinGroup.getGroupId());
				jsonOut.addProperty("joinResult", jointResult);
				jsonOut.addProperty("groupHeadId", groupHeadId);
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
				
			//	審核團員
			case "auditAttender":
				int auditResult = -1;
				PersonalGroup auditAttender = null;
				auditAttender = GSON.fromJson(jsonIn.get("auditAttender").getAsString(), PersonalGroup.class);
				auditResult = gService.auditAttender(auditAttender);
				if(auditResult > 0) {
					List<PersonalGroup> afterAttenders = gService.getAllAttenders(auditAttender.getGroupId());
					jsonOut.addProperty("auditResult", auditResult);
					jsonOut.addProperty("afterAttenders", GSON.toJson(afterAttenders));
					jsonOut.addProperty("dropResult", -1);
				}
				break;
				
			// 退團(一般團員)
			case "dropOutGroup":
				int dropResult = -1;
				PersonalGroup groupDrop = null;
System.out.println(248 + jsonIn.get("auditAttender").getAsString());
				groupDrop = GSON.fromJson(jsonIn.get("auditAttender").getAsString(), PersonalGroup.class);
				dropResult = gService.dropGroup(groupDrop);
				if(dropResult > 0) {
					List<PersonalGroup> afterAttenders = gService.getAllAttenders(groupDrop.getGroupId());
					jsonOut.addProperty("dropResult", dropResult);
					jsonOut.addProperty("afterAttenders", GSON.toJson(afterAttenders));
					jsonOut.addProperty("auditResult", -1);
				}
				break;
				
			case "dismissGroup":
				int dismissResult = -1;
				PersonalGroup groupDismiss = null;
				groupDrop = GSON.fromJson(jsonIn.get("auditAttender").getAsString(), PersonalGroup.class);
				break;
			
			// 拿自己的揪團記錄
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
			//取得該揪團所有成員(1, 2, 3)
			case "getAllAttenders":
				String attenderGroupId = jsonIn.get("groupId").getAsString();
				List<PersonalGroup> allAttenders = new ArrayList<PersonalGroup>();
				allAttenders = gService.getAllAttenders(attenderGroupId);
				int allAttendersResult = -1;
				if( allAttenders != null) {
					allAttendersResult = 1;
					jsonOut.addProperty("allAttenders", GSON.toJson(allAttenders));
				}
				jsonOut.addProperty("myGroupsResult", allAttendersResult);
				break;

//			 拿評分列表
			case "getRatings":
				String myselfId = jsonIn.get("memberId").getAsString();
				String thisGroupId = jsonIn.get("groupId").getAsString();
				List<ScoreBean> ratings = new ArrayList<>();
				ratings = new GroupService().ratingList(thisGroupId, myselfId);
				if (ratings != null) {
					jsonOut.addProperty("ratings", GSON.toJson(ratings));
				}
				break;
				
			// 送出評分
			case "updateScoreList":
				String ratingResultsStr = jsonIn.get("ratingResults").getAsString();
				Notify notify = new Gson().fromJson(jsonIn.get("notify").getAsString(), Notify.class);
				Type listType = new TypeToken<List<ScoreBean>>(){}.getType();
				List<ScoreBean> ratingResults = new Gson().fromJson(ratingResultsStr, listType);
				int resultCode = gService.updateScoreListAndDeleteNoti(ratingResults, notify);
				jsonOut.addProperty("resultCode", resultCode);
				break;
			case "creatAGroupForIOS":
				//處理Base64 轉 byte[]
				byte[] imageIOS = null;
				String imageCreateIOSStr = jsonIn.get("imageBase64").getAsString();
				imageIOS = Base64.getMimeDecoder().decode(imageCreateIOSStr);
				
				PersonalGroup newGroup = new Gson().fromJson(jsonIn.get("insertGroup").getAsString(), PersonalGroup.class);
				newGroup.setGroupEndTime(new DateUtil().getGroupEndTime(newGroup.getAssembleTime()));
				newGroup.setSignUpEnd(new DateUtil().getSignUpEnd(newGroup.getAssembleTime()));
				newGroup.setgImage(imageIOS);
				int resultCodeIOS = gService.creatGroup(newGroup);
				
				/*
				 * 集合時間到：groupStatus == 3
				 */
				TimerTask assembleTimeIOS = new TimerTask() {
					@Override
					public void run() {
						PersonalGroup assembleGroup = gService.searchAGroup(newGroup.getGroupId());
						if(assembleGroup.getGroupStatus() != 0) {
							newGroup.setGroupStatus(3);
							gService.updateGroup(newGroup, null);
						}
					}
				};
				Timer assembleTimerIOS = new Timer();
				assembleTimerIOS.schedule(assembleTimeIOS, new DateUtil().str2Date(newGroup.getAssembleTime()));
				
				/*
				 * 結束時間到：1.建立評分表，2.建立notify
				 */
				TimerTask groupEndTimeTaskIOS = new TimerTask() {
					@Override
					public void run() {
						// 1.建立評分表
						PersonalGroup endUpGroup = gService.searchAGroup(newGroup.getGroupId());
						if(endUpGroup.getGroupStatus() == 0) {
							System.out.println("Group has been canceled, table Score couldn't build.");
						}else {
							List<PersonalGroup> attenders = gService.getAllAttenders(newGroup.getGroupId());
							int scoreTableCount = gService.createScoreTable(attenders);
							for(PersonalGroup attender: attenders) {
								System.out.println(attender.getNickname());
							}
							System.out.println("insertScoreTableCount: " + scoreTableCount+ "\t" + new Date());
							
							// 2.建立notify
							int notifyTableCount = new NotifyService().insertNotiForRating(attenders);
							System.out.println("insertScoreTableCount: " + notifyTableCount+ "\t" + new Date());
						}
						System.gc();
						cancel();
					}
				};
				Timer groupEndTimerIOS = new Timer();
				groupEndTimerIOS.schedule(groupEndTimeTaskIOS, new DateUtil().str2Date(newGroup.getGroupEndTime()));
	
				jsonOut.addProperty("resultCode", resultCodeIOS);
				break;
			// 修改揪團
			case "updateGroupForIOS":
				//處理Base64 轉 byte[]
				byte[] imageUpdateIOS = null;
				String imageUpdateIOSStr = jsonIn.get("imageBase64").getAsString();
				imageUpdateIOS = Base64.getMimeDecoder().decode(imageUpdateIOSStr);
				
				PersonalGroup newUpdateGroup = new Gson().fromJson(jsonIn.get("updateGroup").getAsString(), PersonalGroup.class);
				newUpdateGroup.setGroupEndTime(new DateUtil().getGroupEndTime(newUpdateGroup.getAssembleTime()));
				newUpdateGroup.setSignUpEnd(new DateUtil().getSignUpEnd(newUpdateGroup.getAssembleTime()));
				newUpdateGroup.setgImage(imageUpdateIOS);

				int updateResultCodeIOS = gService.updateGroup(newUpdateGroup, imageUpdateIOS);
//	System.out.println("updateResultCodeIOS: " + updateResultCodeIOS);
				jsonOut.addProperty("resultCode", updateResultCodeIOS);
				break;
			case "cancelGroup":
				String cancelGroupId = jsonIn.get("cancelGroupId").getAsString();
System.out.println("cancelGroupId: " + cancelGroupId);
				int cancelResultCode = gService.cancelGroupIOS(cancelGroupId);
System.out.println("cancelResultCode: " + cancelResultCode);
				jsonOut.addProperty("resultCode", cancelResultCode);
				break;
			default:
				break;
			}
			
			outStr = jsonOut.toString();
System.out.println(outStr);
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
