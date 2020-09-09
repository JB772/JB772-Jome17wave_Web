package web.jome17.jome_notify;

import java.util.List;

public interface NotifyDao {
	int insert(Notify notify);
	int update(Notify notify);
	List <Notify> getAll();
}
