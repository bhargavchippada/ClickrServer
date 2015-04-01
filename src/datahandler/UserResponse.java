package datahandler;

import com.google.gson.JsonArray;

public class UserResponse {
	public String username;
	public String QID;
	public Boolean correct;
	public int timeTook=-1; // in secs
	public int submitTime=-1;
	public JsonArray answers = new JsonArray();

	public String responseString(){
		String output;
		if(correct) output="correct\n";
		else output="wrong\n";
		return output;
	}
}
