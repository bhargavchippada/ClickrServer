package datahandler;

import java.util.ArrayList;

import support.Utils;


public class UserProfile {
	public static String classname = "UserProfile";
	
	String rollnumber;
	String password;
	public String name;
	public String ipaddress;
	int status = 0; //0 means hasn't logged in yet, 1 means connected, 2 means logged in
	// 3 means started quiz, 4 means finished quiz, 5 means disconnected after logged in
	public ArrayList<String> answers = new ArrayList<String>();
	
	void print(){
		Utils.logv(classname, rollnumber+","+password+","+name);
	}
	
	public String getPassword(){
		return password;
	}
}
