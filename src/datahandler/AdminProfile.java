package datahandler;

import support.ParseUsers;


public class AdminProfile {
	public static String classname = "AdminProfile";
	
	public final static String username=ServerSettings.admin_username;
	public final static String password=ServerSettings.admin_password;
	
	public static boolean adminAuthentication(String username, String password){
		return AdminProfile.username.equals(username) && AdminProfile.password.equals(password);
	}
	
	public static void createClassroom(String clsnm, String filepath){
		new ParseUsers().parseFile(ServerSettings.globalpath+filepath);
		if(ClassRoom.users_map.size()!=0) ClassRoom.clsnm = clsnm;
	}
}
