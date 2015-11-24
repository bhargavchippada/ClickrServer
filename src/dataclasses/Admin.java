package dataclasses;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class Admin {

	protected final Logger LOGGER = Logger.getLogger(getClass().getName());

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

	public String feedanswer = null;
	public String qkind = null;
	public String qtype = null;

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

	public void clearUserResponses() {
		Date date = new Date();
		String formattedDate = sdf.format(date);
		for (Entry<String, JsonArray> entry : usersList.entrySet()) {
			JsonArray student = entry.getValue();
			student.set(3, gson.toJsonTree(null, new TypeToken<String>() {
			}.getType()));// answer
			student.set(4, gson.toJsonTree("Disconnected", new TypeToken<String>() {
			}.getType()));// status
			student.set(5, gson.toJsonTree(formattedDate, new TypeToken<String>() {
			}.getType()));// LastUpdate
			student.set(6, gson.toJsonTree(-1, new TypeToken<Integer>() {
			}.getType()));// TimeTook
			student.set(7, gson.toJsonTree(0, new TypeToken<Integer>() {
			}.getType()));// correct or not

		}
		this.updatedon = System.currentTimeMillis() / 1000;
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

	public void setStatus(String uid, String status) {
		JsonArray user = usersList.get(uid);
		Date date = new Date();
		String formattedDate = sdf.format(date);
		user.set(4, gson.toJsonTree(status, new TypeToken<String>() {
		}.getType()));
		user.set(5, gson.toJsonTree(formattedDate, new TypeToken<String>() {
		}.getType()));
		this.updatedon = System.currentTimeMillis() / 1000;
	}

	public void setAnswer(String uid, JsonArray answer, Integer timetook, Boolean correct) {
		JsonArray user = usersList.get(uid);
		user.set(3, gson.toJsonTree(answer, new TypeToken<JsonArray>() {
		}.getType()));
		user.set(6, gson.toJsonTree(timetook, new TypeToken<Integer>() {
		}.getType()));
		user.set(7, gson.toJsonTree(correct, new TypeToken<Boolean>() {
		}.getType()));
		this.updatedon = System.currentTimeMillis() / 1000;
	}

	public void setFeedAnswer() {

		qtype = question.get("qtype").getAsString();
		qkind = question.get("qkind").getAsString();
		LOGGER.info("qkind is: " + qkind + " and qtype is: " + qtype);
		feedanswer = "<b>Answer:</b> ";
		for (int i = 0; i < options.size(); i++) {
			JsonObject option = (JsonObject) options.get(i);
			if (qkind.equals("poll")) feedanswer = "";
			else {
				if (qtype.equals("single")) {
					if (option.get("answer").getAsBoolean()) feedanswer += (i + 1);
				} else if (qtype.equals("multiple")) {
					if (option.get("answer").getAsBoolean()) {
						if (i != options.size() - 1) feedanswer += (i + 1) + ",";
						else feedanswer += (i + 1);
					}
				} else if (qtype.equals("float")) {
					if (i == 0) feedanswer += (option.get("optext").getAsString() + " &le; Answer &le; ");
					else feedanswer += option.get("optext").getAsString();
				} else {
					feedanswer += option.get("optext").getAsString();
				}
			}
		}

		LOGGER.info("feedanswer: " + feedanswer);
	}

	public String getFeedBack(Boolean correct) {
		if (qkind.equals("poll")) return "Thank you for participating!!";
		else {
			if (correct) return feedanswer + "<br>" + "Your Answer is Correct.";
			else if (!qtype.equals("short")) return feedanswer + "<br>" + "Your Answer is Wrong.";
			else return "";
		}
	}

	public boolean verifyAnswer(JsonArray myanswer) {
		if (qkind.equals("poll")) return false;
		else {
			if (qtype.equals("single")) {
				JsonObject option = (JsonObject) options.get(myanswer.getAsInt() - 1);
				if (option.get("answer").getAsBoolean()) return true;
				else return false;
			} else if (qtype.equals("multiple")) {
				int i = 0, pos = 0;
				for (; i < options.size() && pos < myanswer.size(); i++) {
					JsonObject option = (JsonObject) options.get(i);
					if (option.get("answer").getAsBoolean()
							&& myanswer.get(pos).getAsInt() == i + 1) continue;
					else return false;
				}
				if (i == options.size() && pos == myanswer.size()) return true;
				else return false;
			} else if (qtype.equals("short")) return false;
			else if (qtype.equals("word")) {
				JsonObject option = (JsonObject) options.get(0);
				if (option.get("optext").getAsString().equals(myanswer.getAsString())) return true;
				else return false;
			} else if (qtype.equals("integer")) {
				JsonObject option = (JsonObject) options.get(0);
				if (option.get("optext").getAsInt() == myanswer.getAsInt()) return true;
				else return false;
			} else if (qtype.equals("float")) {
				JsonObject option1 = (JsonObject) options.get(0);
				JsonObject option2 = (JsonObject) options.get(1);
				if (option1.get("optext").getAsDouble() <= myanswer.getAsDouble()
						&& option2.get("optext").getAsDouble() >= myanswer.getAsDouble()) return true;
				else return false;
			}
		}

		return false;
	}
}
