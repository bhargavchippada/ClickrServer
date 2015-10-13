package dataclasses;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Admin {
	
	private String username;
	private Integer adminid;
	
	public String classname = null;
	public Integer classid = null;
	public String classtype = "classonly";
	public Boolean serverstate = false;
	
	public Integer questionid = null;
	public JsonObject question = null;
	public JsonArray options = null;
	public Boolean feedback = null;
	public Boolean timedquiz = null;
	public Integer quiztime = null;
	public Boolean quizstatus = false;
	public Integer quizid = null;
	
	Admin(String name, Integer id){
		username = name;
		adminid = id;
	}
	
	public String getUsername(){
		return username;
	}
	
	public Integer getAdminId(){
		return adminid;
	}
	
	public void setClassSettings(String classname, Integer classid, String classtype, Boolean serverstate){
		this.classname = classname;
		this.classid = classid;
		this.classtype = classtype;
		this.serverstate = serverstate;
	}
	
	public void clearQuizSettings(){
		feedback = null;
		timedquiz = null;
		quiztime = null;
		quizstatus = false;
	}
}
