package datahandler;

import support.ServerSettings;


public class AdminProfile {
	public static String classname = "AdminProfile";
	
	public static String username=ServerSettings.admin_username;
	public static String password=ServerSettings.admin_password;
	
	public static void createClassroom(String clsnm, String filepath){
		new ParseUsers().parseFile(filepath);
		if(ClassRoom.users_map.size()!=0) ClassRoom.clsnm = clsnm;
	}
}
