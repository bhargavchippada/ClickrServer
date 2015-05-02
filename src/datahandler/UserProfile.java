package datahandler;

import java.util.Date;

import support.Utils;

import com.google.gson.JsonArray;


/**Class representing the user details
 * @author bhargav
 *
 */
public class UserProfile {
	public static String classname = "UserProfile";
	
	public String username;
	public String password;
	public String name; //user's name
	public String ipaddress="-1"; //user's device ip-address
	public String SESSIONID="-1"; //user session id
	public int status = 0; //0 means hasn't logged in (disconnected), 1 means logged in (connected)
	// 2 means started quiz (attempting), 3 means finished quiz (completed)
	// 4 means didn't finish the quiz
	public String updateTime = Utils.timeformat.format(new Date()); //last update time
	
	/**Print user info
	 * 
	 */
	public void print(){
		Utils.logv(classname, username+","+password+","+name+","+ipaddress+","+status+","+updateTime);
	}
	
	/**Reset the user profile, clear ip-address, session id, status of the user and 
	 *set updateTime
	 * 
	 */
	public void reset(){
		ipaddress="-1";
		SESSIONID="-1";
		status=0;
		updateTime = Utils.timeformat.format(new Date());
	}
	
	/**Change the status to 0 if the present status is > 1 and update updateTime
	 * 
	 */
	public void quizreset(){
		if(status>1) {
			status=0;
			updateTime = Utils.timeformat.format(new Date());
		}
	}
	
	/**jsonarray of the user's info
	 * @return jsonarray
	 */
	public JsonArray getJson(){
		JsonArray jobj = new JsonArray();
		jobj.add(Utils.gson.toJsonTree(username));
		jobj.add(Utils.gson.toJsonTree(name));
		jobj.add(Utils.gson.toJsonTree(status));
		jobj.add(Utils.gson.toJsonTree(updateTime));
		UserResponse userresp = ClassRoom.users_responsemap.get(username);
		if(userresp!=null){
			jobj.add(Utils.gson.toJsonTree(userresp.correct));
			jobj.add(Utils.gson.toJsonTree(userresp.getAnswer()));
			jobj.add(Utils.gson.toJsonTree(userresp.timeTook));
		}else{
			jobj.add(Utils.gson.toJsonTree(""));
			jobj.add(Utils.gson.toJsonTree(""));
			jobj.add(Utils.gson.toJsonTree(0));
		}
		return jobj;
	}
}
