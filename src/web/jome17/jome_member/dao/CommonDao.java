package web.jome17.jome_member.dao;

import java.util.List;

public interface CommonDao<B, K> {
	
	//新增
	int insert(B bean);
	
	//查詢 by key
	B selectByKey(K keyword, K key);
	B selectRelation(B bean);
	
	//查詢ALL
	List<B>selectAll(K key);
	List<B>selectAllNoKey();
	//修改
	int update(B bean);
	//刪除
	int deletaByKey(K key, K key1);
	
	//查圖片
	byte[] getImage(K acconut);
	
	//記算count(*)
	K getCount(K memberId);
	
}
