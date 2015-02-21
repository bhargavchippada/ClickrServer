package support;

import datahandler.AdminProfile;

public class Utils {
	
	public static void logv(String classname, String msg){
		System.out.println(classname+" : "+msg);
	}
	
	public static boolean adminAuthentication(String username, String password){
		return AdminProfile.username.equals(username) && AdminProfile.password.equals(password);
	}
	
}
