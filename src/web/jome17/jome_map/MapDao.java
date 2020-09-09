package web.jome17.jome_map;

import java.util.List;

public interface MapDao {
	
	int inster(Map map, byte[] image);
	
	int update(Map map, byte[] image);

	int delete(int id);

	Map findById(int id);

	List<Map> getAll();

	byte[] getImage(int id);

}
