package appconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

import dataclasses.Admin;

/**
 * Send question to the user if he was authenticated and quiz has started
 * 
 * @author bhargav
 *
 */
public class PushQuiz extends JSONHttpServlet {
	private static final long serialVersionUID = 3622456660944045304L;

	private String update_status = "update response set status = ? , last_update = ? where quizid = ? and studentid = ?";

	private static final int SERVEROFF = 0;
	private static final int LOGGEDOFF = 1;
	private static final int QUIZOFF = 2;
	private static final int ATTEMPTED = 3;
	private static final int RETRIEVED = 4;

	private Connection sqliteconn;

	@Override
	protected JsonObject _processInput(JsonObject input, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession mySession = request.getSession(false);

		String uid = input.get("uid").getAsString();

		JsonObject output = new JsonObject();
		try {
			if (mySession == null) {
				LOGGER.info("No session available!!");
				output.addProperty("statuscode", LOGGEDOFF); // not authorized
			} else {
				String servername = input.get("servername").getAsString();
				Admin admin = getAdminProfile(servername);
				if (admin == null) {
					LOGGER.info("Incorrect servername!!");
					output.addProperty("statuscode", LOGGEDOFF);
				} else if (!admin.serverstate) {
					output.addProperty("statuscode", SERVEROFF);
					LOGGER.info("Server is in stopped state now!!");
				} else if (!admin.quizstatus) {
					output.addProperty("statuscode", QUIZOFF);
					LOGGER.info("Quiz hasn't started yet!!");
				} else {
					Integer classid = input.get("classid").getAsInt();
					if (classid != admin.classid) {
						LOGGER.info("Incorrect classid!!");
						output.addProperty("statuscode", LOGGEDOFF);
					} else {
						String status = admin.usersList.get(uid).get(4).getAsString();
						LOGGER.info(uid + " status is " + status);
						if (status.equals("Connected") || status.equals("Disconnected")) {
							LOGGER.info("Sending quiz to user!!");
							updateStudentStatus(admin, uid, "Attempting");
							output.addProperty("quizid", admin.quizid);
							output.add("question", admin.question);
							output.add("options", admin.options);
							output.addProperty("feedback", admin.feedback);
							output.addProperty("timedquiz", admin.timedquiz);
							output.addProperty("quiztime", admin.quiztime);
							output.addProperty("statuscode", RETRIEVED);
						} else {
							LOGGER.info("User already got the quiz!!");
							if (status.equals("Attempting")) updateStudentStatus(admin, uid,
									"Stopped");
							output.addProperty("statuscode", ATTEMPTED);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			output = null;
		}

		return output;
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
