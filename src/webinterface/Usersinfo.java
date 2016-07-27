package webinterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import dataclasses.Admin;

public class Usersinfo extends WebHttpServlet {

	private static final long serialVersionUID = -6712827331294670533L;
	private String quiz_info = "select quizid,classid,questionid,classtype,feedback,timedquiz,quiztime,created_time from quiz where adminid = ? order by created_time desc";
	private String question_info = "select title,qtype,qkind from question where questionid = ?";
	private String class_info = "select classname from classroom where classid = ?";

	String get_question = "select questionid,title,qtext,qtype,qkind,option_count	 from question where questionid = ?";
	String get_options = "select opindex,optext,answer from qoption where questionid = ? order by opindex asc";

	String get_responses = "select studentid,rollnumber,name,answer,status,last_update,time_took,correct from response where quizid = ?";

	String delete_quiz = "delete from quiz where quizid=?";

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
			} else if (action.equals("quizzeslist")) {
				getQuizzesList(responseJson, sqliteconn);
			} else if (action.equals("selectquiz")) {
				LOGGER.info(request.getParameter("quizinfo"));
				JsonArray quiz = jparser.parse(request.getParameter("quizinfo")).getAsJsonArray();
				selectQuizId(quiz, responseJson, sqliteconn);
			} else if (action.equals("deletequiz")) {
				Integer quizid = Integer.parseInt(request.getParameter("quizid"));
				deleteQuiz(quizid, sqliteconn, responseJson);
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
			responseJson.addProperty("usersinfo", admin.getUsersInfoArray().toString());
			JsonArray stats = admin.getOptionWiseStats();
			responseJson.addProperty("optionwise", stats.toString());
			if (admin.qtype != null && stats.size() > 0) {
				if (!admin.qtype.equals("multiple")) {
					stats.remove(0);
					if(admin.qkind.equals("question")) stats.remove(0);
					responseJson.addProperty("responsewise", stats.toString());
				} else {
					responseJson.addProperty("responsewise", admin.getResponseWiseStats()
							.toString());
				}
			} else responseJson.addProperty("responsewise", new JsonArray().toString());
			responseJson.addProperty("updatedon", admin.updatedon);
		}
		responseJson.addProperty("status", SUCCESS);
	}

	public void getQuizzesList(JsonObject responseJson, Connection sqliteconn) {
		int adminid = getAdminId();
		try {
			// sqliteconn.setAutoCommit(false);
			PreparedStatement pstmt_quiz = sqliteconn.prepareStatement(quiz_info);
			pstmt_quiz.setInt(1, adminid);
			JsonArray quizzes = executePreparedStmt(pstmt_quiz, "Fetching Quizzes", ARRAYOFARRAY);

			LOGGER.info("#quizzes: " + quizzes.size());
			for (int i = 0; i < quizzes.size(); i++) {
				PreparedStatement pstmt_question = sqliteconn.prepareStatement(question_info);
				pstmt_question.setInt(1, quizzes.get(i).getAsJsonArray().get(2).getAsInt());
				JsonArray question = executePreparedStmt(pstmt_question,
						"Fetching Question of quiz", WebHttpServlet.ARRAYOFARRAY);

				quizzes.get(i).getAsJsonArray().addAll(question.get(0).getAsJsonArray());

				PreparedStatement pstmt_class = sqliteconn.prepareStatement(class_info);
				pstmt_class.setInt(1, quizzes.get(i).getAsJsonArray().get(1).getAsInt());
				JsonArray classroom = executePreparedStmt(pstmt_class,
						"Fetching classname of quiz", WebHttpServlet.ARRAYOFARRAY);

				quizzes.get(i).getAsJsonArray().addAll(classroom.get(0).getAsJsonArray());
			}

			// sqliteconn.commit();

			responseJson.addProperty("quizzeslist", quizzes.toString());

			responseJson.addProperty("status", SUCCESS);
		} catch (SQLException e) {
			/*
			 * try { sqliteconn.rollback(); } catch (SQLException e1) {
			 * e1.printStackTrace(); }
			 */
			LOGGER.info("Fetching quizzes from database has failed!!");
			responseJson.addProperty("status", FAIL);
			responseJson.addProperty("error_msg", "Fetching quizzes from database has failed!!");
			e.printStackTrace();
		} finally {
			try {
				sqliteconn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void selectQuizId(JsonArray quiz, JsonObject responseJson, Connection sqliteconn)
			throws SQLException {
		Admin admin = getAdminProfile();
		if (admin.serverstate) {
			LOGGER.info("Server is On!!");
			responseJson.addProperty("status", FAIL);
			responseJson.addProperty("error_msg", "Turn off the server and quiz!!");
		} else {
			admin.classname = quiz.get(11).getAsString();
			admin.classid = quiz.get(1).getAsInt();
			admin.classtype = quiz.get(3).getAsString();
			admin.serverstate = false;
			admin.updatedon = System.currentTimeMillis() / 1000;

			admin.questionid = quiz.get(2).getAsInt();
			admin.feedback = quiz.get(4).getAsBoolean();
			admin.timedquiz = quiz.get(5).getAsBoolean();
			admin.quiztime = quiz.get(6).getAsInt();

			admin.quizstatus = false;
			admin.quizid = quiz.get(0).getAsInt();

			admin.qkind = quiz.get(10).getAsString();
			admin.qtype = quiz.get(9).getAsString();

			PreparedStatement pstmt_question = sqliteconn.prepareStatement(get_question);
			pstmt_question.setInt(1, admin.questionid);
			JsonArray question = executePreparedStmt(pstmt_question, "Fetching Question",
					ARRAYOFOBJ);

			PreparedStatement pstmt_options = sqliteconn.prepareStatement(get_options);
			pstmt_options.setInt(1, admin.questionid);
			JsonArray options = executePreparedStmt(pstmt_options, "Fetching options", ARRAYOFOBJ);

			admin.question = question.get(0).getAsJsonObject();
			admin.options = options;

			admin.setFeedAnswer();

			PreparedStatement pstmt_responses = sqliteconn.prepareStatement(get_responses);
			pstmt_responses.setInt(1, admin.quizid);
			JsonArray responses = executePreparedStmt(pstmt_responses, "Fetching responses",
					ARRAYOFARRAY);

			admin.clearUserResponses();
			for (int i = 0; i < responses.size(); i++) {
				JsonArray response = responses.get(i).getAsJsonArray();
				admin.usersList.put(response.get(1).getAsString(), response);
				JsonElement answer = response.get(3);
				if (!(answer instanceof JsonNull)) {
					admin.updateStats(jparser.parse(answer.getAsString()).getAsJsonArray(),
							response.get(7).getAsBoolean());
				}
			}

			responseJson.addProperty("status", SUCCESS);
		}
	}

	private void deleteQuiz(int quizid, Connection sqliteconn, JsonObject responseJson)
			throws SQLException {
		PreparedStatement pstmt = sqliteconn.prepareStatement(delete_quiz);
		pstmt.setInt(1, quizid);
		executePStmtUpdate(pstmt, "Delete Quiz", responseJson);
	}
}
