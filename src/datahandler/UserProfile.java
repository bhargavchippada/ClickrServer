package datahandler;

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
	
	public void print(){
		Utils.logv(classname, username+","+password+","+name+","+ipaddress+","+status);
	}
}
