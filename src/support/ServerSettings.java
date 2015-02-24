package support;

public class ServerSettings {
	public static final String clsnm = "RnD Class";
	public static final String globalpath = "/home/bhargav/";
	public static final String userlist = "users.txt";
	public static final String admin_username = "bhargav";
	public static final String admin_password = "chippada";

	public static int serveronline = 0;
	public static int quizonline = 0;
	public static String JSON_TYPE = "application/json";
	
	public static synchronized void setServerStatus(int status){
		serveronline = status;
	}
	
	public static synchronized int getServerStatus(){
		return serveronline;
	}
	
	public static synchronized void setQuizsStatus(int status){
		quizonline = status;
	}
}
