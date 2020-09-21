package web.jome17.jome_group;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import web.jome17.jome_map.Map;
import web.jome17.jome_map.MapDaoMySqlImpl;
import web.jome17.main.ImageUtil;

@WebServlet("/JOIN_GROUPServlet")
public class GroupServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private final static String CONTENT_TYPE = "text/html; charset=utf-8";
	GroupDao groupDao = null;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		BufferedReader br = req.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}
		
		System.out.println("input: " + jsonIn);
		
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		if (groupDao == null) {
			groupDao = new GroupDaoMySqlImpl();
		}
		
		String action = jsonObject.get("action").getAsString();
		
		if (action.equals("getAll")) {
			List<Group> groups = groupDao.getAll();
			writeText(resp, gson.toJson(groups).toString());
		} else if (action.equals("getImage")) {
			OutputStream os = resp.getOutputStream();
			int id = jsonObject.get("GROUP_ID").getAsInt();
			int imageSize = jsonObject.get("imageSize").getAsInt();
			byte[] image = groupDao.getImage(id);
			if (image != null) {
				image = ImageUtil.shrink(image, imageSize);
				resp.setContentType("image/jpeg");
				resp.setContentLength(image.length);
				os.write(image);
			}
		} else if (action.equals("groupInsert") || action.equals("groupUpdate")) {
			String groupJson = jsonObject.get("group").getAsString();
			System.out.println("groupJson = " + groupJson);
			Group group = gson.fromJson(groupJson, Group.class);
			byte[] image = null;
			if (jsonObject.get("imageBase64") != null) {
				String imageBase64 = jsonObject.get("imageBase64").getAsString();
				if (imageBase64 != null && !imageBase64.isEmpty()) {
					image = Base64.getMimeDecoder().decode(imageBase64);
				}
			}
			int count = 0;
			if (action.equals("groupInsert")) {
				count = groupDao.inster(group, image);
			} else if (action.equals("groupUpdate")) {
				count = groupDao.update(group, image);
			}
			writeText(resp, String.valueOf(count));
		} else if (action.equals("groupDelete")) {
			int groupId = jsonObject.get("groupId").getAsInt();
			int count = groupDao.delete(groupId);
			writeText(resp, String.valueOf(count));
		} else if (action.equals("findById")) {
			int id = jsonObject.get("id").getAsInt();
			Group group = groupDao.findById(id);
			writeText(resp, gson.toJson(group));
		} else {
			writeText(resp, "");
		}
	}

	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.print(outText);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (groupDao == null) {
			groupDao = new GroupDaoMySqlImpl();
		}
		List<Group> group = groupDao.getAll();
		writeText(resp, new Gson().toJson(group));	}
}
