package datahandler;

import support.Utils;


public class UserProfile {
	public static String classname = "UserProfile";
	
	public String username;
	public String password;
	public String name;
	public String ipaddress;
	public String SESSIONID;
	int status = 0; //0 means hasn't logged in yet, 1 means connected, 2 means logged in
	// 3 means started quiz, 4 means finished quiz, 5 means disconnected after logged in
	
	void print(){
		Utils.logv(classname, username+","+password+","+name);
	}
	
	public String getPassword(){
		return password;
	}
}
