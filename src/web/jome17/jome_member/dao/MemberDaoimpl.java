package web.jome17.jome_member.dao;

import java.util.List;

import web.jome17.jome_member.bean.JomeMember;

public class MemberDaoimpl implements JomeMemberDao<JomeMember, String>{
	public JomeMember jomeMember ;
	
	
	
	@Override
	public int insert(JomeMember bean) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JomeMember selectByKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JomeMember> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(JomeMember bean) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deletaByKey(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JomeMember login(String account, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getImage(String acconut) {
		// TODO Auto-generated method stub
		return null;
	}

}
