package support;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;

import com.google.gson.Gson;

public class Utils {
	public static String JSON_TYPE = "application/json";
	private static SecureRandom random = new SecureRandom();
	public static SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
	public static Gson gson = new Gson();

	/**
	 * @return unique sessionID
	 */
	public static String nextSessionId() {
		return new BigInteger(130, random).toString(32);
	}
	
	/**Log messages for debugging
	 * @param classname
	 * @param msg
	 */
	public static void logv(String classname, String msg){
		System.out.println(classname+" : "+msg);
	}

}
