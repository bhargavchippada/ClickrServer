package dataclasses;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class Admin {

	public String username;
	private Integer adminid;

	public String classname = null;
	public Integer classid = null;
	public String classtype = "classonly";
	public Boolean serverstate = false;
	public ConcurrentHashMap<String, JsonArray> usersList = null;
	public long updatedon = 0;

	public Integer questionid = null;
	public JsonObject question = null;
	public JsonArray options = null;
	public Boolean feedback = null;
	public Boolean timedquiz = null;
	public Integer quiztime = null;
	public Boolean quizstatus = false;
	public Integer quizid = null;
	
	public static final Gson gson = new Gson();
	public static final SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");

	Admin(String name, Integer id) {
		username = name;
		adminid = id;
	}

	public String getUsername() {
		return username;
	}

	public Integer getAdminId() {
		return adminid;
	}

	public void setClassSettings(String classname, Integer classid, String classtype,
			ConcurrentHashMap<String, JsonArray> usersList, Boolean serverstate) {
		this.classname = classname;
		this.classid = classid;
		this.classtype = classtype;
		this.usersList = usersList;
		this.serverstate = serverstate;
		this.updatedon = System.currentTimeMillis() / 1000;
	}

	public void clearQuizSettings() {
		feedback = null;
		timedquiz = null;
		quiztime = null;
		quizstatus = false;
	}

	public JsonArray getUsersInfoArray() {
		JsonArray jarr = new JsonArray();
		if (usersList != null) {
			Iterator<Entry<String, JsonArray>> it = usersList.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, JsonArray> pair = it.next();
				jarr.add((JsonArray) pair.getValue());
			}
		}
		return jarr;
	}
	
	public void setStatus(String uid, String status){
		JsonArray user = usersList.get(uid);
		Date date = new Date();
		String formattedDate = sdf.format(date);
		user.set(4, gson.toJsonTree(status, new TypeToken<String>() {
					}.getType()));
		user.set(5, gson.toJsonTree(formattedDate, new TypeToken<String>() {
		}.getType()));
		this.updatedon = System.currentTimeMillis() / 1000;
	}
}
