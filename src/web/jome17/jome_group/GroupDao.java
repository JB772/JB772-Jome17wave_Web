package web.jome17.jome_group;

import java.util.List;

import web.jome17.jome_map.Map;

public interface GroupDao {
	
	int inster(Group group, byte[] image);
	
	int update(Group group, byte[] image);
	
	int delete(int id);

	Group findById(int id);
	
	List<Group> getAll();
	
	byte[] getImage(int id);
}
