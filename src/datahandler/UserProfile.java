package datahandler;

import java.util.Date;

import support.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;


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
	public String updateTime = Utils.timeformat.format(new Date());
	
	public void print(){
		Utils.logv(classname, username+","+password+","+name+","+ipaddress+","+status+","+updateTime);
	}
	
	public void reset(){
		ipaddress="-1";
		SESSIONID="-1";
		status=0;
		updateTime = Utils.timeformat.format(new Date());
	}
	
	public JsonArray getJson(){
		JsonArray jobj = new JsonArray();
		Gson gson = new Gson();
		jobj.add(gson.toJsonTree(username));
		jobj.add(gson.toJsonTree(name));
		jobj.add(gson.toJsonTree(status));
		jobj.add(gson.toJsonTree(updateTime));
		UserResponse userresp = ClassRoom.users_responsemap.get(username);
		if(userresp!=null){
			jobj.add(gson.toJsonTree(userresp.correct));
			jobj.add(gson.toJsonTree(userresp.getAnswer()));
			jobj.add(gson.toJsonTree(userresp.timeTook));
		}else{
			jobj.add(gson.toJsonTree(""));
			jobj.add(gson.toJsonTree(""));
			jobj.add(gson.toJsonTree(0));
		}
		return jobj;
	}
}
