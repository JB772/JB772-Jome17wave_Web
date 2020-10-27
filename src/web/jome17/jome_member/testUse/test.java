package web.jome17.jome_member.testUse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.MemberBean;

public class test {

	public static void main(String[] args) {
		
		List<FriendListBean> friends = new ArrayList<FriendListBean>();
		FriendListBean firend = new FriendListBean("test", "test1");
		
		//register
		Foo1 foo1 = new Foo1("register", new MemberBean("PeterPan", "1234"));
		System.out.println(new Gson().toJson(foo1));
		
		
		FriendListBean firend1 = new FriendListBean("123", "456");
		friends.add(firend);
		friends.add(firend1);
		
		//原本寫法
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("action", "register");
		jsonObject.addProperty("friends", new Gson().toJson(friends));
//		System.out.println(jsonObject);
		//印完後轉回
		String action = jsonObject.get("action").getAsString();
		String friendsStr = jsonObject.get("friends").getAsString();
//		Type listType = new TypeToken<List<JomeMember>>(){}.getType(); 
//		List<FriendListBean> returnFriends = new Gson().fromJson(friendsStr, List<FriendListBean>.class);
		
		
		Foo foo = new Foo("register", friends);
		foo.setFriends(friends);
		String gsonFoo = new Gson().toJson(foo);
//		System.out.println("gsonFoo: " + gsonFoo);
		Foo fooFromGson = new Gson().fromJson(gsonFoo, Foo.class);
		
	}

}

class Foo {
	private String action;
	private List<FriendListBean> friends;
	

	public List<FriendListBean> getFriends() {
		return friends;
	}

	public void setFriends(List<FriendListBean> friends) {
		this.friends = friends;
	}

	public Foo(String action, List<FriendListBean> friends) {
		super();
		this.action = action;
		this.friends = friends;
	}	
}

class Foo1{
	private String action = "register";
	private MemberBean member;
	
	public Foo1(String action, MemberBean member) {
		super();
		this.action = action;
		this.member = member;
	}
	
	
}
