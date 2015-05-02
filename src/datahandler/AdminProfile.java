package datahandler;

import support.ParseUsers;


/**Singleton Class to store admin information
 * @author bhargav
 *
 */
public class AdminProfile {
	public static String classname = "AdminProfile";
	
	public final static String username="clickrdemo";
	public final static String password="clickr@iitb";
	
	/**
	 * @param username
	 * @param password
	 * @return true if authentication was successful else false
	 */
	public static boolean adminAuthentication(String username, String password){
		return AdminProfile.username.equals(username) && AdminProfile.password.equals(password);
	}
	
	/**Parse the userlist file ans set classroom name.
	 * @param String clsnm
	 * @param String filepath
	 */
	public static void createClassroom(String clsnm, String file){
		new ParseUsers().parseFile(file);
		ClassRoom.clsname = clsnm;
	}
}
