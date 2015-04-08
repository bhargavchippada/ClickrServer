package webconnectionjdbc;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import support.Utils;

import com.google.gson.JsonObject;

import datahandler.ClassRoom;
import datahandler.UserProfile;

public class Authentication extends JSONHttpServlet {
	private static final long serialVersionUID = 8842827877500138557L;
	private static String classname = "Authentication";

	@Override
	protected JsonObject _processInput(JsonObject input, HttpServletRequest request, HttpServletResponse response) {
		HttpSession mySession = request.getSession(true);

		Utils.logv(classname, "Protocol: "+request.getProtocol());
		Utils.logv(classname, "Ip: "+request.getRemoteHost());
		Utils.logv(classname, "port: "+request.getRemotePort());

		JsonObject output = new JsonObject();
		String uid = input.get("uid").getAsString();
		String pwd = input.get("pwd").getAsString();
		if(ClassRoom.serveronline){
			UserProfile user = ClassRoom.users_map.get(uid);
			if(user!=null && user.password.equals(pwd)){
				mySession.setAttribute("username", uid);
				mySession.setAttribute("password", pwd);

				user.ipaddress = request.getRemoteHost();
				user.SESSIONID = mySession.getId();
				if(user.status==0) {
					user.status = 1; // logged in
					user.updateTime = Utils.timeformat.format(new Date());
				}

				output.addProperty("status",2); // authentication success
				output.addProperty("name", user.name);
				output.addProperty("clsnm", ClassRoom.clsname);
				output.addProperty("ip", user.ipaddress);
			}else{
				output.addProperty("status", 1); // authentication failed
			}
		}else{
			output.addProperty("status",0); //server is not ready
		}
		return output;
	}

	@Override
	public String getClassname() {
		return classname;
	}
}
