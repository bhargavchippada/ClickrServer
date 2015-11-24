package dataclasses;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
	public ConcurrentHashMap<String, JsonArray> usersList = new ConcurrentHashMap<String, JsonArray>();
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

	public Integer numOfResponses = 0;
	public Integer numOfCorrect = 0;

	HashMap<String, Integer> option_stats = new HashMap<String, Integer>();
	HashMap<String, Integer> response_stats = new HashMap<String, Integer>();

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
		numOfResponses = 0;
		numOfCorrect = 0;
		option_stats.clear();
		response_stats.clear();
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
		updateStats(answer, correct);
		this.updatedon = System.currentTimeMillis() / 1000;
	}

	public void updateStats(JsonArray answer,  Boolean correct) {
		numOfResponses++;
		if(correct) numOfCorrect++;
		if (!qtype.equals("short")) {
			if (!qtype.equals("multiple")) {
				String key = answer.getAsString();
				Integer value = option_stats.get(key);
				if (value == null) value = 0;
				option_stats.put(key, value + 1);
			} else {
				String rkey = answer.toString();
				Integer rvalue = response_stats.get(rkey);
				if (rvalue == null) rvalue = 0;
				response_stats.put(rkey, rvalue + 1);
				for (int i = 0; i < answer.size(); i++) {
					String key = answer.get(i).getAsString();
					Integer value = option_stats.get(key);
					if (value == null) value = 0;
					option_stats.put(key, value + 1);
				}
			}
		}
	}

	public JsonArray getOptionWiseStats() {
		JsonArray optionwise = new JsonArray();

		JsonArray bargraph = new JsonArray();
		int total = usersList.size();
		if(total==0) return optionwise;
		
		int percent = (numOfResponses * 100) / total;
		String bartitle = "Attempts (" + numOfResponses + "/" + total + ")";
		bargraph.add(gson.toJsonTree(bartitle, new TypeToken<String>() {
		}.getType()));
		bargraph.add(gson.toJsonTree(percent, new TypeToken<Integer>() {
		}.getType()));
		optionwise.add(bargraph);

		if(qkind == null || qkind.equals("question")){
			bargraph = new JsonArray();
			percent = (numOfCorrect*100)/total;
			bartitle = "Corrects (" + numOfCorrect + "/" + total + ")";
			bargraph.add(gson.toJsonTree(bartitle, new TypeToken<String>() {
			}.getType()));
			bargraph.add(gson.toJsonTree(percent, new TypeToken<Integer>() {
			}.getType()));
			optionwise.add(bargraph);
		}
		
		List option_list = sortByValues(option_stats);

		for (Iterator it = option_list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			if (qtype.equals("single") || qtype.equals("multiple")) {
				Integer op = Integer.parseInt((String) entry.getKey());
				JsonObject option = options.get(op-1).getAsJsonObject();
				Integer count = (Integer) entry.getValue();
				percent = (count * 100) / total;
				bartitle = op + ")" + " " + option.get("optext").getAsString() + " (" + count + "/"
						+ total + ")";
			} else {
				Integer count = (Integer) entry.getValue();
				percent = (count * 100) / total;
				bartitle = entry.getKey() + " (" + count + "/" + total + ")";
			}

			bargraph = new JsonArray();
			bargraph.add(gson.toJsonTree(bartitle, new TypeToken<String>() {
			}.getType()));
			bargraph.add(gson.toJsonTree(percent, new TypeToken<Integer>() {
			}.getType()));
			optionwise.add(bargraph);
		}
		return optionwise;
	}
	
	public JsonArray getResponseWiseStats() {
		JsonArray responsewise = new JsonArray();
		JsonArray piegraph = new JsonArray();
		int total = usersList.size();
		if(total==0) return responsewise;
		
		List response_list = sortByValues(response_stats);
		
		Integer count;
		String bartitle;
		JsonArray bargraph;
		
		int max = 4;
		int counter = 0;
		for (Iterator it = response_list.iterator(); it.hasNext() && max>=1; max--) {
			Map.Entry entry = (Map.Entry) it.next();
			
			count = (Integer) entry.getValue();
			counter+=count;
			bartitle = entry.getKey().toString();

			bargraph = new JsonArray();
			bargraph.add(gson.toJsonTree(bartitle, new TypeToken<String>() {
			}.getType()));
			bargraph.add(gson.toJsonTree(count, new TypeToken<Integer>() {
			}.getType()));
			responsewise.add(bargraph);
		}
		
		bargraph = new JsonArray();
		bargraph.add(gson.toJsonTree("Others", new TypeToken<String>() {
		}.getType()));
		bargraph.add(gson.toJsonTree(numOfResponses-counter, new TypeToken<Integer>() {
		}.getType()));
		responsewise.add(bargraph);
		
		return responsewise;
	}

	private static List sortByValues(HashMap map) {
		List list = new LinkedList(map.entrySet());

		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Entry<String, Integer>) (o2)).getValue())
						.compareTo(((Entry<String, Integer>) (o1)).getValue());
			}
		});

		return list;
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
