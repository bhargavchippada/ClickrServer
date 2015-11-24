package webinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import dataclasses.Admin;
import dataclasses.Admins;
import dataclasses.SQLConn;

public abstract class WebHttpServlet extends SQLConn {

	private static final long serialVersionUID = 7776160967313029069L;

	private String adminUserName = null;
	private Integer adminId = null;
	private Admin adminProfile = null;

	protected String getAdminUserName() {
		return adminUserName;
	}

	protected Integer getAdminId() {
		return adminId;
	}

	protected Admin getAdminProfile() {
		return adminProfile;
	}

	protected boolean authenticateAdmin(HttpServletRequest request, JsonObject responseJson) {
		HttpSession mySession = request.getSession(false);
		if (mySession == null) {
			responseJson.addProperty("status", FAIL); // no session
			responseJson.addProperty("error_msg", "Authentication failed!!");
		} else {
			Object usernameObj = mySession.getAttribute("username");
			Object passwordObj = mySession.getAttribute("password");
			Object adminidObj = mySession.getAttribute("adminid");

			if (usernameObj != null && passwordObj != null && adminidObj != null) {
				adminId = (Integer) adminidObj;
				adminUserName = (String) usernameObj;
				adminProfile = Admins.getAdmin(adminUserName);
				if (adminProfile != null) return true;
			}

			responseJson.addProperty("status", FAIL); // no session
			responseJson.addProperty("error_msg", "Authentication failed!!");
		}
		return false;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("servlet task - start");
		long startTime = System.currentTimeMillis();

		response.setContentType("application/json");
		response.setHeader("Cache-Control", "nocache");
		JsonObject responseJson = new JsonObject();

		if (authenticateAdmin(request, responseJson)) {
			Connection sqliteconn = (Connection) getServletContext().getAttribute("sqliteconn");
			_processInput(request, sqliteconn, responseJson);
		}

		PrintWriter out = response.getWriter();
		out.print(responseJson.toString());
		LOGGER.info("response: " + responseJson.toString());
		out.flush();
		out.close();
		LOGGER.info("servlet task - end");
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		LOGGER.info(elapsedTime + "ms");
	}

	protected abstract void _processInput(HttpServletRequest request, Connection sqliteconn,
			JsonObject responseJson);

}
