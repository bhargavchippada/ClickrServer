package appconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dataclasses.Admin;

/**
 * Process the user response received and send an appropriate response back.
 * state of the server/quiz and authorization of the user are checked.
 * 
 * @author bhargav
 *
 */
public class ReceiveAnswer extends JSONHttpServlet {
	private static final long serialVersionUID = 5627352069489872384L;

	private String update_status = "update response set status = ? , last_update = ?, correct = ?, time_took = ?, answer = ? where quizid = ? and studentid = ?";

	private static final int SERVEROFF = 0;
	private static final int LOGGEDOFF = 1;
	private static final int QUIZOFF = 2;
	private static final int ATTEMPTED = 3;
	private static final int SUBMITTED = 4;

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
				Integer quizid = input.get("quizid").getAsInt();
				if (admin == null) {
					LOGGER.info("Incorrect servername!!");
					output.addProperty("statuscode", LOGGEDOFF);
				} else if (!admin.serverstate) {
					output.addProperty("statuscode", SERVEROFF);
					LOGGER.info("Server is in stopped state now!!");
				} else if (!admin.quizstatus || quizid != admin.quizid) {
					output.addProperty("statuscode", QUIZOFF);
					LOGGER.info("Quiz was stopped!!");
				} else {
					Integer classid = input.get("classid").getAsInt();
					if (classid != admin.classid) {
						LOGGER.info("Incorrect classid!!");
						output.addProperty("statuscode", LOGGEDOFF);
					} else {
						String status = admin.usersList.get(uid).get(4).getAsString();
						LOGGER.info(uid + " status is " + status);
						if (status.equals("Completed")) {
							LOGGER.info("User already completed the quiz!!");
							output.addProperty("statuscode", ATTEMPTED);
						} else if (status.equals("Attempting")) {
							LOGGER.info("Retrieving answer from user!!");
							JsonArray answer = (JsonArray) input.get("myanswer");
							if (answer.size() != 0) {
								Integer timetook = input.get("timetook").getAsInt();
								Boolean correct = admin.verifyAnswer(answer);
								updateStudentResponse(admin, uid, "Completed", answer, timetook,
										correct);

								if (admin.feedback) {
									output.addProperty("feedback", admin.getFeedBack(correct));
								}
							} else {
								Boolean correct = false;
								Integer timetook = input.get("timetook").getAsInt();
								updateStudentResponse(admin, uid, "Stopped", answer, timetook,
										correct);

								if (admin.feedback) {
									output.addProperty("feedback", admin.getFeedBack(correct));
								}
							}

							output.addProperty("statuscode", SUBMITTED);
						} else {
							LOGGER.info("User not authorized!!");
							output.addProperty("statuscode", LOGGEDOFF);
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

	private void updateStudentResponse(Admin admin, String uid, String status, JsonArray answer,
			Integer timetook, Boolean correct) throws SQLException {
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
			pstmt.setBoolean(3, correct);
			pstmt.setInt(4, timetook);
			pstmt.setString(5, answer.toString());
			pstmt.setInt(6, admin.quizid);
			pstmt.setInt(7, studentid);
			pstmt.executeUpdate();

			admin.setStatus(uid, status);
			admin.setAnswer(uid, answer, timetook, correct);
		}
	}
}
