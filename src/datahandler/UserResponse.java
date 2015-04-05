package datahandler;

import support.Utils;

import com.google.gson.JsonArray;

public class UserResponse {
	
	private static String classname = "UserResponse";
	
	public String username="NULL";
	public String QID="NULL";
	public Boolean correct=false;
	public long startTime=-1;
	public long timeTook=-1; // in secs
	public long submitTime=-1;
	public JsonArray answers = new JsonArray();

	public String responseString(){
		String output;
		if(correct) output="correct answer!\n";
		else output="wrong answer!\n";
		return output;
	}
	
	public void print(){
		Utils.logv(classname, "username: "+username);
		Utils.logv(classname, "QID: "+QID);
		Utils.logv(classname, "correct: "+correct);
		Utils.logv(classname, "startTime: "+startTime);
		Utils.logv(classname, "submitTime: "+submitTime);
		Utils.logv(classname, "timeTook: "+timeTook);
		Utils.logv(classname, "answers: "+answers);
	}
}
