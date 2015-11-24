package appconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import dataclasses.Admin;

/**
 * Servlet for authenticating user login credentials, send an appropriate
 * response according to the state of the server and whether or not
 * authorization was successful<br>
 * A HttpSession is created for successful authentications
 * 
 * @author bhargav
 *
 */
public class Authentication extends JSONHttpServlet {
	private static final long serialVersionUID = 8842827877500138557L;

	private String update_status = "update response set status = ? , last_update = ? where quizid = ? and studentid = ?";

	private static final int SERVEROFF = 0;
	private static final int LOGINSUCCESS = 1;
	private static final int LOGINFAIL = 2;
	private static final int INVALIDSERVERNAME = 3;
	
	private Connection sqliteconn;

	String user_authentication = "select studentid,name from student where classid = ? and rollnumber = ? and password = ?";

	@Override
	protected JsonObject _processInput(JsonObject input, HttpServletRequest request,
			HttpServletResponse response) {
		LOGGER.info("Protocol: " + request.getProtocol());
		LOGGER.info("Ip: " + request.getRemoteHost());
		LOGGER.info("port: " + request.getRemotePort());

		JsonObject output = new JsonObject();
		String servername = input.get("servername").getAsString();
		Admin admin = getAdminProfile(servername);
		if (admin != null && admin.serverstate != null && admin.serverstate) {
			Object conn = getServletContext().getAttribute("sqliteconn");

			if (conn != null) authentication(admin, input, output, (Connection) conn, request);
			else return null;
		} else if (admin == null) {
			output.addProperty("statuscode", INVALIDSERVERNAME);
		} else {
			output.addProperty("statuscode", SERVEROFF); // server is not ready
		}
		return output;
	}

	private void authentication(Admin admin, JsonObject input, JsonObject output,
			Connection sqliteconn, HttpServletRequest request) {

		int classid = admin.classid;
		String uid = input.get("uid").getAsString();
		String pwd = input.get("pwd").getAsString();

		try {
			PreparedStatement pstmt = sqliteconn.prepareStatement(user_authentication);
			pstmt.setInt(1, classid);
			pstmt.setString(2, uid);
			pstmt.setString(3, pwd);
			ResultSet sqlresp = pstmt.executeQuery();

			if (sqlresp == null || !sqlresp.next()) {
				LOGGER.info("User login has failed! " + uid + " " + pwd + " " + classid);
				output.addProperty("statuscode", LOGINFAIL);
			} else {
				LOGGER.info("User login is success!");
				HttpSession mySession = request.getSession(true);
				mySession.setMaxInactiveInterval(3600 * 3);
				mySession.setAttribute("rollnumber", uid);
				mySession.setAttribute("studentid", sqlresp.getInt(1));
				mySession.setAttribute("name", sqlresp.getString(2));
				mySession.setAttribute("servername", admin.username);
				
				LOGGER.info("SESSIONID: " + mySession.getId());
				LOGGER.info("Rollnumber: " + mySession.getAttribute("rollnumber"));
				LOGGER.info("UserId: " + mySession.getAttribute("studentid"));

				String status = admin.usersList.get(uid).get(4).getAsString();
				if(status.equals("Disconnected")) updateStudentStatus(admin,uid,"Connected");
				output.addProperty("ip", request.getRemoteHost());
				output.addProperty("name", sqlresp.getString(2));
				output.addProperty("classname", admin.classname);
				output.addProperty("servername", admin.username);
				output.addProperty("classid", admin.classid);
				output.addProperty("statuscode", LOGINSUCCESS);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			output = null;
		}
	}
	
	private void updateStudentStatus(Admin admin, String uid, String status) throws SQLException {
		int studentid = admin.usersList.get(uid).get(0).getAsInt();
		Object conn = getServletContext().getAttribute("sqliteconn");
		if (conn == null) throw new SQLException();
		else {
			sqliteconn = (Connection) conn;
			Date date = new Date();
			String formattedDate = sdf.format(date);
			PreparedStatement pstmt = sqliteconn.prepareStatement(update_status);
			pstmt.setString(1, status);
			pstmt.setString(2, formattedDate);
			pstmt.setInt(3, admin.quizid);
			pstmt.setInt(4, studentid);
			pstmt.executeUpdate();

			admin.setStatus(uid, status);
		}
	}
}
