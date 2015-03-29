package datahandler;

public class ServerSettings {
	public static final String admin_username = "bhargav";
	public static final String admin_password = "chippada";

	public static int quizonline = 0;
	public static String JSON_TYPE = "application/json";
	
	public static synchronized void setQuizsStatus(int status){
		quizonline = status;
	}
	
	public static synchronized int getQuizsStatus(){
		return quizonline;
	}
}
