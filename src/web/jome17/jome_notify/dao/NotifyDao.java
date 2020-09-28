package web.jome17.jome_notify.dao;

import java.util.List;

import web.jome17.jome_notify.bean.Notify;

public interface NotifyDao {
	int insert(Notify notify);
	int update(Notify notify);
	List <Notify> getAll();
}
