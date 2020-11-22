package web.jome17.jome_member.service;

import java.util.ArrayList;
import java.util.List;

import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_member.dao.AttenderDaoimpl;
import web.jome17.jome_member.dao.CommonDao;
import web.jome17.jome_member.dao.GroupActivityDaoimpl;
import web.jome17.jome_member.dao.ScoreDaoimpl;
import web.jome17.jome_notify.bean.AttenderBean;
import web.jome17.jome_notify.bean.Notify;

public class GroupService {
	private CommonDao<PersonalGroup, String> dao;

	public GroupService() {
		dao = new GroupActivityDaoimpl();
	}
	
	//建立新的揪團
	public int creatGroup(PersonalGroup pGroup) {
		return dao.insert(pGroup);
	}	
	
	//更新揪團資訊
	public int updateGroup(PersonalGroup pGroup, byte[] image) {
		if(image != null) {
			pGroup.setgImage(image);
		}
		return dao.update(pGroup);
	}
	
	//入團
	public int joinGroup(PersonalGroup pGroup) {
		AttenderDaoimpl attenderDao = new AttenderDaoimpl();
		pGroup.setRole(2);
		pGroup.setAttenderStatus(3);
		AttenderBean joinAttender = new AttenderBean(-1, pGroup.getAttenderStatus(), pGroup.getRole(), pGroup.getMemberId(), pGroup.getGroupId());
		String gHeadId = attenderDao.selectGroupHeadId(pGroup.getGroupId()).getMemberId();
		int insertAttenderBean = -1;
		int hasJoinDeleteResult = attenderDao.hasJoinDelete(joinAttender, gHeadId);
		if(hasJoinDeleteResult == 1 || hasJoinDeleteResult ==-1) {
			insertAttenderBean = attenderDao.insertAtenderBean(joinAttender, gHeadId);
		}
		return insertAttenderBean;
	}
	
	//退團
	public int dropGroup(PersonalGroup pGroup) {
		if(pGroup.getRole() == 1) {
			/*
			 * 是主揪：	1.Update Table Group，該GroupId的Group_status，都改為0，
			 * 		  	2.Update Table Attenders，該GroupId的所有Attedn_status = 0
			 * 			3.Update Table Notify，所有Attenders的body_status都改為0
			 * 			
			 * 			交易控制 1, 2, 3
			 */
			String disGroupId = pGroup.getGroupId();
			List<PersonalGroup> allAttenders = getAllAttenders(disGroupId);
			List<Integer> AllAttenderNo = new ArrayList<Integer>();
			for(PersonalGroup attender: allAttenders) {
				AllAttenderNo.add(attender.getAttenderId());
			}
			return new GroupActivityDaoimpl().updateGroupAttenderNotify(disGroupId, AllAttenderNo);
		}else {
			/*
			 * 是團員：改ATTEND_STATUS = 0;
			 */
			AttenderDaoimpl attenderDao = new AttenderDaoimpl();
			AttenderBean memberAttender = new AttenderBean(pGroup.getAttenderId(), pGroup.getAttenderStatus(), pGroup.getRole(), pGroup.getMemberId(), pGroup.getGroupId());
			String groupHeadId = attenderDao.selectGroupHeadId(pGroup.getGroupId()).getMemberId();
			return attenderDao.updateAtender(memberAttender, groupHeadId);
		}
	}
	
	public int cancelGroupIOS(String groupId) {
		return new GroupActivityDaoimpl().deletaByKey(groupId, groupId);
	}
	
	//審核(更新團員狀態)
	public int auditAttender(PersonalGroup auditGroup) {
		AttenderDaoimpl attenderDao = new AttenderDaoimpl();
		AttenderBean auditAttender = new AttenderBean(auditGroup.getAttenderId(), auditGroup.getAttenderStatus(), auditGroup.getRole(), auditGroup.getMemberId(), auditGroup.getGroupId());
		String gHeadId = attenderDao.selectGroupHeadId(auditGroup.getGroupId()).getMemberId();
		return attenderDao.updateAtender(auditAttender, gHeadId);
	}
	
	//拿到該團團長的id
	public String getGroupHeadId(String groupId) {
		AttenderDaoimpl attenderDao = new AttenderDaoimpl();
		String groupHeadId = attenderDao.selectGroupHeadId(groupId).getMemberId();
		return groupHeadId;
	}
	
	//搜尋所有的揪團資料(含主揪人的mId及nickname)
	public List<PersonalGroup> getAllGroups(){
		return dao.selectAllNoKey();
	}
	
	//搜尋某一groupId揪團資訊(單筆)
	public PersonalGroup searchAGroup(String groupId) {
		return dao.selectByKey("GROUP_ID", groupId);
	}
	//搜尋某一memberId揪團狀態(單筆)
	public PersonalGroup inquirePerGroups(String memberId, String groupId) {
		AttenderDaoimpl attenderDao = new AttenderDaoimpl();

		return attenderDao.selectByKey(memberId, groupId);
	}
	//搜尋某一groupId的所有成員(多筆)
	public List<PersonalGroup> getAllAttenders(String groupId) {
		return dao.selectAll(groupId);
	}
	//搜尋某一memberId的所有揪團資訊(多筆)
	public List<PersonalGroup> getMemberAllGroups(String memberId){
		AttenderDaoimpl attenderDao = new AttenderDaoimpl();
		return attenderDao.selectAll(memberId);
	}
	
	//拿圖
	public byte[] groupImage(String groupId) {
		byte[] image = null;
		image = new GroupActivityDaoimpl().getImage(groupId);
		return image;
	}
	
	//取得該團目前參加人數
	public int getGroupCount(String groupId) {
		int groupAttenderCount = -1;
		groupAttenderCount = Integer.valueOf(dao.getCount(groupId));
;		return groupAttenderCount;
	}
	public void createScore(String groupId) {
		
	}
	
	//用attender_NO取 單筆資料
	public PersonalGroup getDataByAttenderNo(int attenderNO) {
		PersonalGroup attenderTable = new PersonalGroup();
		attenderTable.setAttenderId(attenderNO);
		AttenderDaoimpl attenderDao = new AttenderDaoimpl();
//		PersonalGroup myMyGroup = attenderDao.selectRelation(attenderTable);
		List<PersonalGroup> getAllGroups = dao.selectAllNoKey();
		for(PersonalGroup group: getAllGroups) {
			if (group.getGroupId().equals(attenderDao.selectRelation(attenderTable).getGroupId())) {
				return group;
			}
		}
//		return attenderDao.selectRelation(attenderTable);
		return null;
	}
	
	/*
	 * 傳入該groupId所有的attender名單，建立評分資料，回傳成功建立資料筆數
	 */
	public int createScoreTable(List<PersonalGroup> attenders) {
		CommonDao<ScoreBean, String> ScoreDao = new ScoreDaoimpl();
		int inserCount = 0 ;
		for(PersonalGroup beRatedAttender: attenders) {
			String beRatedId = beRatedAttender.getMemberId();
			
			for (PersonalGroup member: attenders) {
				String memberId = member.getMemberId();
				if(!beRatedId.equals(memberId)) {
					ScoreBean newScoreData = new ScoreBean(member.getGroupId(), beRatedId, memberId, -1);
					inserCount += ScoreDao.insert(newScoreData);
				}
			}
		}
		return inserCount;
	}
	
	//取得某揪團的所有評分資料
	public List<ScoreBean> getTheGroupScore(String groupId){
		CommonDao<ScoreBean, String> ScoreDao = new ScoreDaoimpl();
		return ScoreDao.selectAll(groupId);
	}
	
	//更新某人某揪團的評分列表，並刪除先前的評分通知
	public int updateScoreListAndDeleteNoti(List<ScoreBean> ratingResults, Notify notify) {
		int successedCount = new ScoreDaoimpl().updateAndDelete(ratingResults, notify);
		if (successedCount > 0) {
			return 1;
		}
		return -1;
	}
	
	//取得某人對某揪團的大家評分列表
	public List<ScoreBean> ratingList(String groupID, String memberId) {
		return new ScoreDaoimpl().selectRatingList(groupID, memberId);
	}
	 
}
