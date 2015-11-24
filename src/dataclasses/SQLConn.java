package dataclasses;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public abstract class SQLConn extends HttpServlet {

	private static final long serialVersionUID = 4695068329593599570L;
	protected final Logger LOGGER = Logger.getLogger(getClass().getName());
	public static final Gson gson = new Gson();
	public static final String SUCCESS = "SUCCESS";
	public static final String FAIL = "FAIL";
	public static final int ARRAYOFOBJ = 0;
	public static final int ARRAYOFARRAY = 1;
	public static final JsonParser jparser = new JsonParser();
	public static final SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");

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

	protected JsonArray executePreparedStmt(PreparedStatement pstmt, String logTitle, int resultType)
			throws SQLException {

		ResultSet sqlresp = pstmt.executeQuery();
		if (sqlresp == null) {
			LOGGER.info(logTitle + " failed!!");
			return null;
		} else {
			LOGGER.info(logTitle + " success!!");
			switch (resultType) {
			case ARRAYOFOBJ:
				return resultSetToArrayOfJsonObj(sqlresp);
			case ARRAYOFARRAY:
				return resultSetToArrayOfJsonArray(sqlresp);
			default:
				LOGGER.severe(logTitle + " fail!! due to invalid resultType");
				return null;
			}
		}
	}

	protected void executePStmtUpdate(PreparedStatement pstmt, String logTitle,
			JsonObject responseJson) throws SQLException {

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
}
