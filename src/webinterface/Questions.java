package webinterface;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import dataclasses.Admin;

public class Questions extends WebHttpServlet {

	private static final long serialVersionUID = 734797539096515654L;

	String questions_of_admin = "select questionid,title,qtext,qtype,qkind,created_time from question where adminid=? order by created_time desc";
	String delete_question = "delete from question where questionid=?";
	String upload_question = "insert into question (adminid, title, qtext, qtype, qkind) values(?, ?, ?, ?, ?)";
	String last_insert_rowid = "select last_insert_rowid()";
	String insert_option = "insert into qoption (questionid, opindex, optext, answer) values(?, ?, ?, ?)";
	String get_question = "select questionid,title,qtext,qtype,qkind,option_count	 from question where questionid = ?";
	String get_options = "select opindex,optext,answer from qoption where questionid = ? order by opindex asc";
	String new_quiz = "insert into quiz (adminid, questionid, classid, classtype, feedback, timedquiz, quiztime) values(?, ?, ?, ?, ?, ?, ?)";
	String student_response = "insert into response (quizid, studentid, rollnumber, name, status, last_update) values(?,?,?,?,?,?)";

	@Override
	protected void _processInput(HttpServletRequest request, Connection sqliteconn,
			JsonObject responseJson) {
		try {
			String action = request.getParameter("action");
			responseJson.addProperty("action", action);
			LOGGER.info("action: " + action);
			if (action.equals("questionslist")) questionsOfAdmin(sqliteconn, responseJson);
			else if (action.equals("deletequestion")) {
				Integer questionid = Integer.parseInt(request.getParameter("questionid"));
				deleteQuestion(questionid, sqliteconn, responseJson);
			} else if (action.equals("newquestion")) {
				JsonObject question = (new JsonParser().parse(request.getReader()))
						.getAsJsonObject();
				uploadQuestion(question, sqliteconn, responseJson);
			} else if (action.equals("copyquestion")) {
				Integer questionid = Integer.parseInt(request.getParameter("questionid"));
				getQuestion(questionid, sqliteconn, responseJson);
			} else if (action.equals("getoptions")) {
				Integer questionid = Integer.parseInt(request.getParameter("questionid"));
				getOptions(questionid, sqliteconn, responseJson);
			} else if (action.equals("selectquestion")) {
				Integer questionid = Integer.parseInt(request.getParameter("questionid"));
				selectQuestion(questionid, sqliteconn, responseJson);
			} else if (action.equals("fetch")) {
				fetchQuestionSettings(responseJson);
			} else if (action.equals("startquiz")) {
				startQuiz(request, sqliteconn, responseJson);
			} else if (action.equals("stopquiz")) {
				Admin admin = getAdminProfile();
				admin.clearQuizSettings();
				responseJson.addProperty("status", SUCCESS);
				responseJson.addProperty("quizstatus", false);
			} else {
				responseJson.addProperty("error_msg", "Unidentified action request!");
				responseJson.addProperty("status", FAIL);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			responseJson.addProperty("error_msg", "Database query error!");
			responseJson.addProperty("status", FAIL);
		} catch (IOException e) {
			responseJson.addProperty("error_msg", "Query Parse error!");
			responseJson.addProperty("status", FAIL);
			e.printStackTrace();
		}

	}

	private void questionsOfAdmin(Connection sqliteconn, JsonObject responseJson)
			throws SQLException {
		PreparedStatement pstmt = sqliteconn.prepareStatement(questions_of_admin);
		pstmt.setInt(1, getAdminId());
		executePreparedStmt(pstmt, "questionslist", "Fetching Questions",
				WebHttpServlet.ARRAYOFOBJ, responseJson);
	}

	private void deleteQuestion(int questionid, Connection sqliteconn, JsonObject responseJson)
			throws SQLException {
		PreparedStatement pstmt = sqliteconn.prepareStatement(delete_question);
		pstmt.setInt(1, questionid);
		executePStmtUpdate(pstmt, "Delete Question", responseJson);
	}

	private void uploadQuestion(JsonObject question, Connection sqliteconn, JsonObject responseJson) {
		int adminid = getAdminId();
		try {
			sqliteconn.setAutoCommit(false);
			PreparedStatement pstmt_question = sqliteconn.prepareStatement(upload_question);
			pstmt_question.setInt(1, adminid);
			pstmt_question.setString(2, question.get("title").getAsString());
			pstmt_question.setString(3, question.get("qtext").getAsString());
			pstmt_question.setString(4, question.get("qtype").getAsString());
			pstmt_question.setString(5, question.get("qkind").getAsString());
			pstmt_question.executeUpdate();

			PreparedStatement pstmt_cid = sqliteconn.prepareStatement(last_insert_rowid);
			ResultSet rs_qid = pstmt_cid.executeQuery();
			rs_qid.next();
			int questionid = rs_qid.getInt(1);
			LOGGER.info("Questionid: " + questionid);
			PreparedStatement pstmt_option = sqliteconn.prepareStatement(insert_option);

			JsonArray options = question.getAsJsonArray("options");
			for (int i = 0; i < options.size(); i++) {
				JsonObject option = options.get(i).getAsJsonObject();
				pstmt_option.setInt(1, questionid);
				pstmt_option.setInt(2, option.get("opindex").getAsInt());
				pstmt_option.setString(3, option.get("optext").getAsString());
				pstmt_option.setInt(4, option.get("answer").getAsInt());
				pstmt_option.addBatch();
			}

			LOGGER.info("Question options parsing successful");
			pstmt_option.executeBatch();

			sqliteconn.commit();

			responseJson.addProperty("status", SUCCESS);
		} catch (SQLException e) {
			try {
				sqliteconn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			LOGGER.info("Question Insertion into database has failed!!");
			responseJson.addProperty("status", FAIL);
			responseJson.addProperty("error_msg", "Question Insertion into database has failed!!");
			e.printStackTrace();
		} finally {
			try {
				sqliteconn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void getQuestion(int questionid, Connection sqliteconn, JsonObject responseJson)
			throws SQLException {
		PreparedStatement pstmt_question = sqliteconn.prepareStatement(get_question);
		pstmt_question.setInt(1, questionid);
		executePreparedStmt(pstmt_question, "question", "Fetching Question",
				WebHttpServlet.ARRAYOFOBJ, responseJson);

		PreparedStatement pstmt_options = sqliteconn.prepareStatement(get_options);
		pstmt_options.setInt(1, questionid);
		executePreparedStmt(pstmt_options, "options", "Fetching options",
				WebHttpServlet.ARRAYOFOBJ, responseJson);
	}

	private void getOptions(int questionid, Connection sqliteconn, JsonObject responseJson)
			throws SQLException {
		PreparedStatement pstmt_options = sqliteconn.prepareStatement(get_options);
		pstmt_options.setInt(1, questionid);
		executePreparedStmt(pstmt_options, "options", "Fetching options",
				WebHttpServlet.ARRAYOFOBJ, responseJson);
	}

	private void selectQuestion(int questionid, Connection sqliteconn, JsonObject responseJson)
			throws SQLException {
		getQuestion(questionid, sqliteconn, responseJson);

		if (responseJson.get("status").getAsString().equals(SUCCESS)) {
			Admin admin = getAdminProfile();
			admin.questionid = questionid;
			JsonArray arr = jparser.parse(responseJson.get("question").getAsString())
					.getAsJsonArray();
			admin.question = arr.get(0).getAsJsonObject();
			admin.options = jparser.parse(responseJson.get("options").getAsString())
					.getAsJsonArray();
			admin.setFeedAnswer();
		}
	}

	private void fetchQuestionSettings(JsonObject responseJson) {
		try {
			Admin admin = getAdminProfile();
			if (admin.questionid != null) {
				responseJson.addProperty("questionid", admin.questionid);
				responseJson.addProperty("quizstatus", admin.quizstatus);
				responseJson.addProperty("feedback", admin.feedback);
				responseJson.addProperty("timedquiz", admin.timedquiz);
				responseJson.addProperty("quiztime", admin.quiztime);
			}
			responseJson.addProperty("status", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			responseJson.addProperty("status", FAIL);
			responseJson.addProperty("error_msg", "Fetching question settings has Failed!!");
		}
	}

	private void startQuiz(HttpServletRequest request, Connection sqliteconn,
			JsonObject responseJson) {
		Admin admin = getAdminProfile();
		if (!admin.serverstate) {
			responseJson.addProperty("status", FAIL);
			responseJson.addProperty("error_msg", "Select classroom and start server!!");
		} else {
			try {
				admin.feedback = Boolean.parseBoolean(request.getParameter("feedback"));
				admin.timedquiz = Boolean.parseBoolean(request.getParameter("timedquiz"));
				if (admin.timedquiz) {
					admin.quiztime = Integer.parseInt(request.getParameter("quiztime"));
				} else admin.quiztime = -1;

				sqliteconn.setAutoCommit(false);
				int adminid = getAdminId();
				PreparedStatement pstmt_newquiz = sqliteconn.prepareStatement(new_quiz);
				pstmt_newquiz.setInt(1, adminid);
				pstmt_newquiz.setInt(2, admin.questionid);
				pstmt_newquiz.setInt(3, admin.classid);
				pstmt_newquiz.setString(4, admin.classtype);
				pstmt_newquiz.setBoolean(5, admin.feedback);
				pstmt_newquiz.setBoolean(6, admin.timedquiz);
				pstmt_newquiz.setInt(7, admin.quiztime);
				pstmt_newquiz.executeUpdate();

				PreparedStatement pstmt_quizid = sqliteconn.prepareStatement(last_insert_rowid);
				ResultSet rs_qid = pstmt_quizid.executeQuery();
				rs_qid.next();
				int quizid = rs_qid.getInt(1);
				admin.quizid = quizid;

				Date date = new Date();
				String formattedDate = sdf.format(date);

				admin.clearUserResponses();

				PreparedStatement pstmt_studresp = sqliteconn.prepareStatement(student_response);
				for (Entry<String, JsonArray> entry : admin.usersList.entrySet()) {
					JsonArray student = entry.getValue();
					pstmt_studresp.setInt(1, admin.quizid);
					pstmt_studresp.setInt(2, student.get(0).getAsInt());
					pstmt_studresp.setString(3, student.get(1).getAsString());
					pstmt_studresp.setString(4, student.get(2).getAsString());
					pstmt_studresp.setString(5, student.get(4).getAsString());
					student.set(5, gson.toJsonTree(formattedDate, new TypeToken<String>() {
					}.getType()));
					pstmt_studresp.setString(6, student.get(5).getAsString());
					pstmt_studresp.addBatch();
				}
				pstmt_studresp.executeBatch();

				sqliteconn.commit();

				admin.quizstatus = true;
				responseJson.addProperty("quizstatus", true);
				responseJson.addProperty("status", SUCCESS);
			} catch (SQLException e) {
				try {
					sqliteconn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				admin.clearQuizSettings();
				LOGGER.info("Quiz Insertion into database has failed!!");
				responseJson.addProperty("status", FAIL);
				responseJson.addProperty("error_msg",
						"Question Insertion into database has failed!!");
			} catch (Exception e) {
				e.printStackTrace();
				admin.clearQuizSettings();
				responseJson.addProperty("status", FAIL);
				responseJson.addProperty("error_msg", "Starting quiz has Failed!!");
			} finally {
				try {
					sqliteconn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
