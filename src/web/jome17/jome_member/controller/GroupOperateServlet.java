package web.jome17.jome_member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_member.service.GroupService;
import web.jome17.main.ImageUtil;

public class GroupOperateServlet extends HttpServlet {
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		jsonIn = json2In(req);
		System.out.println("jsonIn:" + jsonIn);

		String action = jsonIn.get("action").getAsString();
		String outStr = "";
		JsonObject jsonOut = new JsonObject();
		GroupService gService = new GroupService();

		if (action.equals("getImage")) {
			//拿圖
			byte[] image = null;
			String groupId = jsonIn.get("groupId").getAsString();
			int imageSize = jsonIn.get("imageSize").getAsInt();
			image = gService.groupImage(groupId);
			if(image != null) {
				OutputStream ops = resp.getOutputStream();
				image = ImageUtil.shrink(image, imageSize);
				resp.setContentType("image/*");
				resp.setContentLength(image.length);
				ops.write(image);
			}
			
		} else {
			switch (action) {
			// 查所有揪團
			case "getAll":
				int getAllResult = -1;
				List<PersonalGroup> pGroups = gService.getAllGroups();
				if (pGroups != null) {
					jsonOut.addProperty("allGroup", GSON.toJson(pGroups));
					getAllResult = 1;
				}
				jsonOut.addProperty("getAllResult", getAllResult);
				break;

			// 查單筆揪團
			case "getAGroup":
				int getAResult = -1;
				String groupId = jsonIn.get("groupId").getAsString();
				PersonalGroup pGroup = gService.searchAGroup(groupId);
				if (pGroup != null) {
					jsonOut.addProperty("group", GSON.toJson(pGroup));
					getAResult = 1;
				}
				jsonOut.addProperty("getAResult", getAResult);
				break;

			// 建立揪團
			case "creatAGroup":
				int creatResult = -1;
				PersonalGroup inPGroup = GSON.fromJson(jsonIn.get("inGroup").getAsString(), PersonalGroup.class);
				creatResult = gService.creatGroup(inPGroup);
				jsonOut.addProperty("creatResult", creatResult);
				break;

			// 修改揪團
			case "updateGroup":
				int updateResult = -1;
				byte[] imageUpdate = null;
				PersonalGroup groupUp = null;
				String imageBase64 = jsonIn.get("imageBase64").getAsString();
				if (imageBase64 != null) {
					imageUpdate = Base64.getMimeDecoder().decode(imageBase64);
				}
				groupUp = GSON.fromJson(jsonIn.get("gruopUp").getAsString(), PersonalGroup.class);
				updateResult = gService.updateGroup(groupUp, imageUpdate);
				jsonOut.addProperty("updateResult", updateResult);
				break;
				
			// 退團
			case "dropOutGroup":
				PersonalGroup groupDrop = null;
				groupDrop = GSON.fromJson(jsonIn.get("gruopUp").getAsString(), PersonalGroup.class);
				int dropResult = gService.dropGroup(groupDrop);
				jsonOut.addProperty("dropResult", dropResult);
				break;

			default:
				break;
			}
			outStr = jsonOut.toString();
			resp.setContentType(CONTENT_TYPE);
			writeJson(resp, outStr);
		}

	}

	private void writeJson(HttpServletResponse resp, String outStr) {
		try (PrintWriter pw = resp.getWriter();) {
			pw.print(outStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JsonObject json2In(HttpServletRequest req) {
		StringBuilder jsonIn = new StringBuilder();
		try (BufferedReader bf = req.getReader();) {
			String line;
			while ((line = bf.readLine()) != null) {
				jsonIn.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return GSON.fromJson(jsonIn.toString(), JsonObject.class);
	}
}
