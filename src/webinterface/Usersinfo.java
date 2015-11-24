package webinterface;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;

import dataclasses.Admin;

public class Usersinfo extends WebHttpServlet {

	private static final long serialVersionUID = -6712827331294670533L;
	private String CLASSNAME = "Usersinfo";

	@Override
	protected void _processInput(HttpServletRequest request, Connection sqliteconn,
			JsonObject responseJson) {
		try {
			String action = request.getParameter("action");
			responseJson.addProperty("action", action);
			LOGGER.info("action: " + action);
			if (action.equals("getusersinfo")) {
				long updatedon = Long.parseLong(request.getParameter("updatedon"));
				getUsersInfo(updatedon, responseJson);
			} else {
				responseJson.addProperty("error_msg", "Unidentified action request!");
				responseJson.addProperty("status", FAIL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseJson.addProperty("error_msg", "Database query error!");
			responseJson.addProperty("status", FAIL);
		}
	}

	private void getUsersInfo(long updatedon, JsonObject responseJson) {
		Admin admin = getAdminProfile();
		if (updatedon != admin.updatedon) {
			responseJson.addProperty("usersinfo", getAdminProfile().getUsersInfoArray().toString());
			responseJson.addProperty("updatedon", admin.updatedon);
			if (admin.timedquiz == null) responseJson.addProperty("timed", false);
			else responseJson.addProperty("timed", admin.timedquiz);
		}
		responseJson.addProperty("status", SUCCESS);
	}
}
