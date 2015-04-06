package datahandler;

import java.util.Date;

import com.google.gson.JsonObject;

import support.Utils;


public class UserProfile {
	public static String classname = "UserProfile";
	
	public String username;
	public String password;
	public String name;
	public String ipaddress="-1";
	public String SESSIONID="-1";
	public int status = 0; //0 means hasn't logged in (disconnected), 1 means logged in (connected)
	// 2 means started quiz (attempting), 3 means finished quiz (completed)
	// 4 means didn't finish the quiz
	public String updateTime;
	
	public void print(){
		Utils.logv(classname, username+","+password+","+name+","+ipaddress+","+status+","+updateTime);
	}
	
	public void reset(){
		ipaddress="-1";
		SESSIONID="-1";
		status=0;
		updateTime = Utils.timeformat.format(new Date());
	}
	
	public JsonObject getJson(){
		JsonObject jobj = new JsonObject();
		jobj.addProperty("username", username);
		jobj.addProperty("name", name);
		jobj.addProperty("status", status);
		jobj.addProperty("updateTime", updateTime);
		UserResponse userresp = ClassRoom.users_responsemap.get(username);
		if(userresp!=null){
			jobj.addProperty("correct", userresp.correct);
			jobj.addProperty("answer", userresp.getAnswer());
			jobj.addProperty("timeTook", userresp.timeTook);
		}
		return jobj;
	}
}
