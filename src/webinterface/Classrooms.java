package webinterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import dataclasses.Admin;

public class Classrooms extends WebHttpServlet {

	private static final long serialVersionUID = -4803387961711005076L;

	String classrooms_of_admin = "select classid,classname,student_count,created_time from classroom where adminid=? order by created_time desc";
	String students_of_class = "select rollnumber,name from student where classid=?";
	String students_of_class_2 = "select studentid,rollnumber,name from student where classid=?";
	String delete_class = "delete from classroom where classid=?";
	String upload_class = "insert into classroom (adminid, classname) values(?, ?)";
	String insert_student = "insert into student (classid, rollnumber, password, name) values(?, ?, ?, ?)";
	String last_insert_rowid = "select last_insert_rowid()";

	@Override
	protected void _processInput(HttpServletRequest request, Connection sqliteconn,
			JsonObject responseJson) {
		try {
			String action = request.getParameter("action");
			responseJson.addProperty("action", action);
			LOGGER.info("action: " + action);
			if (action.equals("classroomslist")) classroomsOfAdmin(sqliteconn, responseJson);
			else if (action.equals("classusers")) {
				Integer classid = Integer.parseInt(request.getParameter("classid"));
				studentsOfClass(classid, sqliteconn, responseJson);
			} else if (action.equals("deleteclass")) {
				Integer classid = Integer.parseInt(request.getParameter("classid"));
				deleteClass(classid, sqliteconn, responseJson);
			} else if (action.equals("uploadnewclass")) {
				String classusers = request.getParameter("classusers");
				String classname = request.getParameter("classname");
				uploadClass(classusers, classname, sqliteconn, responseJson);
			} else if (action.equals("changeserverstate")) {
				changeServerState(request, sqliteconn, responseJson);
			} else if (action.equals("fetch")) {
				fetchClassSettings(responseJson);
			} else {
				responseJson.addProperty("error_msg", "Unidentified action request!");
				responseJson.addProperty("status", FAIL);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			responseJson.addProperty("error_msg", "Database query error!");
			responseJson.addProperty("status", FAIL);
		}
	}

	private void classroomsOfAdmin(Connection sqliteconn, JsonObject responseJson)
			throws SQLException {
		PreparedStatement pstmt = sqliteconn.prepareStatement(classrooms_of_admin);
		pstmt.setInt(1, getAdminId());
		executePreparedStmt(pstmt, "classroomslist", "Fetching classrooms",
				WebHttpServlet.ARRAYOFOBJ, responseJson);
	}

	private void studentsOfClass(int classid, Connection sqliteconn, JsonObject responseJson)
			throws SQLException {
		PreparedStatement pstmt = sqliteconn.prepareStatement(students_of_class);
		pstmt.setInt(1, classid);
		executePreparedStmt(pstmt, "classusers", "Fetching students of classroom",
				WebHttpServlet.ARRAYOFARRAY, responseJson);
	}

	private void deleteClass(int classid, Connection sqliteconn, JsonObject responseJson)
			throws SQLException {
		PreparedStatement pstmt = sqliteconn.prepareStatement(delete_class);
		pstmt.setInt(1, classid);
		executePStmtUpdate(pstmt, "Delete Classroom", responseJson);
	}

	private void uploadClass(String classusers, String classname, Connection sqliteconn,
			JsonObject responseJson) {
		int adminid = getAdminId();
		try {
			sqliteconn.setAutoCommit(false);
			PreparedStatement pstmt_class = sqliteconn.prepareStatement(upload_class);
			pstmt_class.setInt(1, adminid);
			pstmt_class.setString(2, classname);

			pstmt_class.executeUpdate();

			PreparedStatement pstmt_cid = sqliteconn.prepareStatement(last_insert_rowid);
			ResultSet rs_classid = pstmt_cid.executeQuery();
			rs_classid.next();
			int classid = rs_classid.getInt(1);
			LOGGER.info("Classid: " + classid);
			PreparedStatement pstmt_student = sqliteconn.prepareStatement(insert_student);

			Scanner sc = new Scanner(classusers);
			while (sc.hasNext()) {
				String line = sc.next();
				StringTokenizer tokens = new StringTokenizer(line, ",");
				pstmt_student.setInt(1, classid);
				pstmt_student.setString(2, tokens.nextToken());
				pstmt_student.setString(3, tokens.nextToken());
				pstmt_student.setString(4, tokens.nextToken());
				pstmt_student.addBatch();
			}
			sc.close();
			LOGGER.info("Users File parsing successful");
			pstmt_student.executeBatch();

			sqliteconn.commit();

			responseJson.addProperty("status", SUCCESS);
		} catch (SQLException e) {
			try {
				sqliteconn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			LOGGER.info("Classroom Insertion into database has failed!!");
			responseJson.addProperty("status", FAIL);
			responseJson.addProperty("error_msg", "Classroom Insertion into database has failed!!");
			e.printStackTrace();
		} finally {
			try {
				sqliteconn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void changeServerState(HttpServletRequest request, Connection sqliteconn,
			JsonObject responseJson) {
		try {
			Boolean serverstate = Boolean.valueOf(request.getParameter("serverstate"));
			responseJson.addProperty("serverstate", serverstate);
			Admin admin = getAdminProfile();
			if (!serverstate) {
				admin.setClassSettings(null, null, "classonly",
						new ConcurrentHashMap<String, JsonArray>(), serverstate);
				admin.clearUserResponses();
				responseJson.addProperty("status", SUCCESS);
			} else {
				String classtype = (String) request.getParameter("classtype");
				String classname = null;
				Integer classid = null;
				ConcurrentHashMap<String, JsonArray> usersList = new ConcurrentHashMap<String, JsonArray>();
				admin.clearStats();
				if (classtype.equals("classonly")) {
					classid = Integer.parseInt(request.getParameter("classid"));
					classname = request.getParameter("classname");
					PreparedStatement pstmt = sqliteconn.prepareStatement(students_of_class_2);
					pstmt.setInt(1, classid);
					JsonArray jarr = executePreparedStmt(pstmt, "Fetching students of classroom",
							WebHttpServlet.ARRAYOFARRAY);

					Date date = new Date();
					String formattedDate = sdf.format(date);
					for (int i = 0; i < jarr.size(); i++) {
						JsonArray user = jarr.get(i).getAsJsonArray();
						user.add(gson.toJsonTree(null, new TypeToken<JsonArray>() {
						}.getType()));// answer
						user.add(gson.toJsonTree("Disconnected", new TypeToken<String>() {
						}.getType()));// status
						user.add(gson.toJsonTree(formattedDate, new TypeToken<String>() {
						}.getType()));// LastUpdate
						user.add(gson.toJsonTree(-1, new TypeToken<Integer>() {
						}.getType()));// Timetook
						user.add(gson.toJsonTree(false, new TypeToken<Boolean>() {
						}.getType()));// correct or not

						usersList.put(user.get(1).getAsString(), user);
					}
				}else{
					responseJson.addProperty("status", FAIL);
					responseJson.addProperty("error_msg", "Anonymous and mixed class features not yet available!!");
					return;
				}
				admin.setClassSettings(classname, classid, classtype, usersList, serverstate);
				responseJson.addProperty("status", SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseJson.addProperty("status", FAIL);
			responseJson.addProperty("error_msg", "Start or Stop Server has Failed!!");
		}
	}

	private void fetchClassSettings(JsonObject responseJson) {
		try {
			Admin admin = getAdminProfile();
			if (admin.classid != null) {
				responseJson.addProperty("classid", admin.classid);
				responseJson.addProperty("classname", admin.classname);
			}
			responseJson.addProperty("classtype", admin.classtype);
			responseJson.addProperty("serverstate", admin.serverstate);
			responseJson.addProperty("status", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			responseJson.addProperty("status", FAIL);
			responseJson.addProperty("error_msg", "Fetching classroom settings has Failed!!");
		}
	}
}