package support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import datahandler.AdminProfile;

public class Utils {
	public static void println(String s){
		System.out.println(s);
	}
	
	public static void logi(String classname, String msg){
		System.out.println(classname+" : "+msg);
	}
	
	public static ByteBuffer convertToBB(Object ob) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(ob);
	    
	    return new ByteBuffer(baos.toByteArray());
	}
	
	public static boolean adminAuthentication(String username, String password){
		return AdminProfile.username.equals(username) && AdminProfile.password.equals(password);
		//return true;
	}
	
}
