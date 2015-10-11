package webinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import dataclasses.Admins;

/**
 * Administrator's authentication servlet, a HttpSession is created upon
 * success.<br>
 * An appropriate response is sent back
 * 
 * @author bhargav
 *
 */
public class AdminLogin extends HttpServlet {

	private static final long serialVersionUID = 8316579451970438895L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	Connection sqliteconn = null;
	JsonObject responseJson = new JsonObject();

	String admin_authentication = "select adminid from admin where username = ? and password = ?";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String database_path = request.getParameter("database_path");

		responseJson.addProperty("redirectURL", false); // default
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "nocache");

		Object conn = getServletContext().getAttribute("sqliteconn");

		if (conn != null) sqliteconn = (Connection) conn;
		else {
			if (database_path.isEmpty()) responseJson.addProperty("error_msg",
					"Database connection error due to incorrect path!");
			else {
				ServletConfig.establishDatabaseConnection(database_path, getServletContext());
				conn = getServletContext().getAttribute("sqliteconn");
				if (conn != null) sqliteconn = (Connection) conn;
				else responseJson.addProperty("error_msg",
						"Database connection error due to incorrect path!");
			}
		}

		if (sqliteconn != null) authentication(request);

		PrintWriter out = response.getWriter();
		out.print(responseJson.toString());
		LOGGER.info("response: " + responseJson.toString());
		out.flush();
		out.close();
	}

	private void authentication(HttpServletRequest request) {
		String username = request.getParameter("login_username");
		String password = request.getParameter("login_password");

		try {
			PreparedStatement pstmt = sqliteconn.prepareStatement(admin_authentication);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			ResultSet sqlresp = pstmt.executeQuery();

			if (sqlresp == null || !sqlresp.next()) {
				LOGGER.info("Admin login has failed! " + username + " " + password);
				responseJson.addProperty("error_msg", "Username/Password doesn't match!");
			} else {
				LOGGER.info("Admin login is success!");
				HttpSession mySession = request.getSession(true);
				mySession.setMaxInactiveInterval(3600);
				mySession.setAttribute("username", username);
				mySession.setAttribute("password", password);
				mySession.setAttribute("adminid", sqlresp.getInt(1));

				Admins.addAdmin(username, sqlresp.getInt(1));

				LOGGER.info("SESSIONID: " + mySession.getId());
				LOGGER.info("Username: " + mySession.getAttribute("username"));
				LOGGER.info("AdminId: " + mySession.getAttribute("adminid"));

				responseJson.addProperty("url", "/ClickrServer/home.html");
				responseJson.addProperty("redirectURL", true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			responseJson.addProperty("error_msg", "Database connection error!");
		}
	}
}