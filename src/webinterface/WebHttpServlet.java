package webinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import dataclasses.Admin;
import dataclasses.Admins;

public abstract class WebHttpServlet extends HttpServlet {

	private static final long serialVersionUID = 7776160967313029069L;
	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private String CLASSNAME = "WebHttpServlet";
	public static final Gson gson = new Gson();
	public static final String SUCCESS = "SUCCESS";
	public static final String FAIL = "FAIL";
	public static final int ARRAYOFOBJ = 0;
	public static final int ARRAYOFARRAY = 1;

	private String adminUserName = null;
	private Integer adminId = null;
	private Admin adminProfile = null;

	protected String getClassname() {
		return CLASSNAME;
	}

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
				return true;
			} else {
				responseJson.addProperty("status", FAIL); // no session
				responseJson.addProperty("error_msg", "Authentication failed!!");
			}
		}
		return false;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info(getClassname() + " servlet task - start");
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
		LOGGER.info(getClassname() + " response: " + responseJson.toString());
		out.flush();
		out.close();
		LOGGER.info(getClassname() + " servlet task - end");
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		LOGGER.info(getClassname() + " " + elapsedTime + "ms");
	}

	protected void executePreparedStmt(PreparedStatement pstmt, String resultKey, String logTitle,
			int resultType, JsonObject responseJson) throws SQLException {

		ResultSet sqlresp = pstmt.executeQuery();
		if (sqlresp == null) {
			LOGGER.info(logTitle + " failed!!");
			responseJson.addProperty("error_msg", logTitle + " failed!!");
			responseJson.addProperty("status", FAIL);
		} else {
			LOGGER.info(logTitle + " success!!");
			responseJson.addProperty("status", SUCCESS);

			switch (resultType) {
			case ARRAYOFOBJ:
				responseJson.addProperty(resultKey, resultSetToArrayOfJsonObj(sqlresp).toString());
				break;
			case ARRAYOFARRAY:
				responseJson
						.addProperty(resultKey, resultSetToArrayOfJsonArray(sqlresp).toString());
				break;
			default:
				LOGGER.severe(logTitle + " fail!! due to invalid resultType");
				break;

			}
		}

	}
	
	protected void executePStmtUpdate(PreparedStatement pstmt, String logTitle, JsonObject responseJson) throws SQLException {

		int sqlret = pstmt.executeUpdate();
		LOGGER.info(logTitle + " success!!");
		responseJson.addProperty("status", SUCCESS);
		responseJson.addProperty("affectedRows", sqlret);
	}

	public static JsonArray resultSetToArrayOfJsonObj(ResultSet rs) throws SQLException {
		JsonArray jarray = new JsonArray();
		ResultSetMetaData rsmd = rs.getMetaData();

		while (rs.next()) {
			int numColumns = rsmd.getColumnCount();
			JsonObject obj = new JsonObject();

			for (int i = 1; i < numColumns + 1; i++) {
				String columnName = rsmd.getColumnName(i);

				switch (rsmd.getColumnType(i)) {
				case java.sql.Types.BIGINT:
					obj.addProperty(columnName, rs.getInt(i));
					break;
				case java.sql.Types.BOOLEAN:
					obj.addProperty(columnName, rs.getBoolean(i));
					break;
				case java.sql.Types.DOUBLE:
					obj.addProperty(columnName, rs.getDouble(i));
					break;
				case java.sql.Types.FLOAT:
					obj.addProperty(columnName, rs.getFloat(i));
					break;
				case java.sql.Types.INTEGER:
					obj.addProperty(columnName, rs.getInt(i));
					break;
				case java.sql.Types.NVARCHAR:
					obj.addProperty(columnName, rs.getNString(i));
					break;
				case java.sql.Types.VARCHAR:
					obj.addProperty(columnName, rs.getString(i));
					break;
				case java.sql.Types.TINYINT:
					obj.addProperty(columnName, rs.getInt(i));
					break;
				case java.sql.Types.SMALLINT:
					obj.addProperty(columnName, rs.getInt(i));
					break;
				case java.sql.Types.DATE:
					obj.addProperty(columnName, rs.getDate(i).toString());
					break;
				case java.sql.Types.TIMESTAMP:
					obj.addProperty(columnName, rs.getTimestamp(i).toString());
					break;
				default:
					obj.addProperty(columnName, rs.getObject(i).toString());
					break;
				}
			}

			jarray.add(obj);
		}

		return jarray;
	}

	public static JsonArray resultSetToArrayOfJsonArray(ResultSet rs) throws SQLException {
		JsonArray jarray = new JsonArray();
		ResultSetMetaData rsmd = rs.getMetaData();

		while (rs.next()) {
			int numColumns = rsmd.getColumnCount();
			JsonArray arr = new JsonArray();

			for (int i = 1; i < numColumns + 1; i++) {

				switch (rsmd.getColumnType(i)) {
				case java.sql.Types.BIGINT:
					arr.add(gson.toJsonTree(rs.getInt(i), new TypeToken<Integer>() {
					}.getType()));
					break;
				case java.sql.Types.BOOLEAN:
					arr.add(gson.toJsonTree(rs.getBoolean(i), new TypeToken<Boolean>() {
					}.getType()));
					break;
				case java.sql.Types.DOUBLE:
					arr.add(gson.toJsonTree(rs.getDouble(i), new TypeToken<Double>() {
					}.getType()));
					break;
				case java.sql.Types.FLOAT:
					arr.add(gson.toJsonTree(rs.getFloat(i), new TypeToken<Float>() {
					}.getType()));
					break;
				case java.sql.Types.INTEGER:
					arr.add(gson.toJsonTree(rs.getInt(i), new TypeToken<Integer>() {
					}.getType()));
					break;
				case java.sql.Types.NVARCHAR:
					arr.add(gson.toJsonTree(rs.getNString(i), new TypeToken<String>() {
					}.getType()));
					break;
				case java.sql.Types.VARCHAR:
					arr.add(gson.toJsonTree(rs.getString(i), new TypeToken<String>() {
					}.getType()));
					break;
				case java.sql.Types.TINYINT:
					arr.add(gson.toJsonTree(rs.getInt(i), new TypeToken<Integer>() {
					}.getType()));
					break;
				case java.sql.Types.SMALLINT:
					arr.add(gson.toJsonTree(rs.getInt(i), new TypeToken<Integer>() {
					}.getType()));
					break;
				case java.sql.Types.DATE:
					arr.add(gson.toJsonTree(rs.getDate(i).toString(), new TypeToken<String>() {
					}.getType()));
					break;
				case java.sql.Types.TIMESTAMP:
					arr.add(gson.toJsonTree(rs.getTimestamp(i).toString(), new TypeToken<String>() {
					}.getType()));
					break;
				default:
					arr.add(gson.toJsonTree(rs.getObject(i).toString(), new TypeToken<Object>() {
					}.getType()));
					break;
				}
			}

			jarray.add(arr);
		}

		return jarray;
	}

	protected abstract void _processInput(HttpServletRequest request, Connection sqliteconn,
			JsonObject responseJson);

}
