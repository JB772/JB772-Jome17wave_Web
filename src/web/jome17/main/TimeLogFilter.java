package web.jome17.main;

import java.sql.Time;

import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * [21-3: 過濾器] 此範例示範HttpFilter用法
 * 定義一個HttpFilter的子實作類別TimeLogFilter
 * 此過濾器會攔截所有請求，並在Console印出進入時間、離開時間、共耗時多久
 * @author William
 */
@WebFilter("/*")// ←"/*"表示攔截所有網址
public class TimeLogFilter extends HttpFilter {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
		try {
			long startTime = System.currentTimeMillis();
			chain.doFilter(req, resp); // ←一定撰寫此行，Servlet才會接續處理!
			long endTime = System.currentTimeMillis();
			System.out.print(req.getRequestURI() + " → ");
			System.out.print("進入時間: " + new Time(startTime));
			System.out.print(", 離開時間: " + new Time(endTime));
			System.out.println(", 共耗時: " + (endTime - startTime) + "毫秒");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
