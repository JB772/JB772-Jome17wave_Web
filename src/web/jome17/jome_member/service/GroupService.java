package web.jome17.jome_member.service;

import java.util.List;

import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_member.dao.AttenderDaoimpl;
import web.jome17.jome_member.dao.CommonDao;
import web.jome17.jome_member.dao.GroupActivityDaoimpl;

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
	
	//退團
	public int dropGroup(PersonalGroup pGroup) {
		if(pGroup.getRole() == 1) {
			//是主揪就刪除該GroupId的所有記錄含兩張Table
			return dao.update(pGroup);
		}else {
			//是團員就改ATTEND_STATUS = 0;
			AttenderDaoimpl attenderDao = new AttenderDaoimpl();
			return attenderDao.update(pGroup);
		}
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
	public PersonalGroup inquirePerGroups(String memberId) {
		AttenderDaoimpl attenderDao = new AttenderDaoimpl();
		return attenderDao.selectByKey("MEMBER_ID", memberId);
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
	
	public void createScore(String groupId) {
		
	}
}
