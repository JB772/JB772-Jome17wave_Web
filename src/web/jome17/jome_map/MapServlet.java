package web.jome17.jome_map;

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
import web.jome17.main.ImageUtil;




@WebServlet("/SURF_POINTServlet")
public class MapServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private final static String CONTENT_TYPE = "text/html; charset=utf-8";
	MapDao mapDao = null;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		BufferedReader br = request.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonIn.append(line);
		}
		// 將輸入資料列印出來除錯用
		System.out.println("input: " + jsonIn);
		
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		if (mapDao == null) {
			mapDao = new MapDaoMySqlImpl();
		}
		
		String action = jsonObject.get("action").getAsString();
		
		if (action.equals("getAll")) {
			List<Map> maps = mapDao.getAll();
			System.out.println("jsonOut:"+gson.toJson(maps).toString());
			writeText(response, gson.toJson(maps).toString());
		} else if (action.equals("getImage")) {
			// 要輸出byte使用OutputStream
			OutputStream os = response.getOutputStream();
			int id = jsonObject.get("SURF_POINT_ID").getAsInt();
			int imageSize = jsonObject.get("imageSize").getAsInt();
			byte[] image = mapDao.getImage(id);
			if (image != null) {
				image = ImageUtil.shrink(image, imageSize);
				response.setContentType("image/jpeg");
				response.setContentLength(image.length);
				os.write(image);
			}
		// 新增與修改寫一起
		} else if (action.equals("mapInsert") || action.equals("mapUpdate")) {
			String mapJson = jsonObject.get("map").getAsString();
			System.out.println("mapJson = " + mapJson);
			Map map = gson.fromJson(mapJson, Map.class);
			byte[] image = null;
			// 檢查是否有上傳圖片
			if (jsonObject.get("imageBase64") != null) {
				String imageBase64 = jsonObject.get("imageBase64").getAsString();
				if (imageBase64 != null && !imageBase64.isEmpty()) {
					image = Base64.getMimeDecoder().decode(imageBase64);
				}
			}
			int count = 0;
			if (action.equals("mapInsert")) {
				count = mapDao.inster(map, image);
			} else if (action.equals("mapUpdate")) {
				count = mapDao.update(map, image);
			}
			writeText(response, String.valueOf(count));
		} else if (action.equals("mapDelete")) {
			int mapId = jsonObject.get("mapId").getAsInt();
			int count = mapDao.delete(mapId);
			writeText(response, String.valueOf(count));
		} else if (action.equals("findById")) {
			int id = jsonObject.get("id").getAsInt();
			Map map = mapDao.findById(id);
			writeText(response, gson.toJson(map));
		} else {
			writeText(response, "");
		}
	}
	
	private void writeText(HttpServletResponse response, String outText) throws IOException {
		response.setContentType(CONTENT_TYPE);
		PrintWriter out = response.getWriter();
		out.print(outText);
		// 將輸出資料列印出來除錯用
		// System.out.println("output: " + outText);
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (mapDao == null) {
			mapDao = new MapDaoMySqlImpl();
		}
		List<Map> map = mapDao.getAll();
		writeText(resp, new Gson().toJson(map));
	}
}

	
