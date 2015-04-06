package support;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;

public class Utils {
	public static String JSON_TYPE = "application/json";
	private static SecureRandom random = new SecureRandom();
	public static SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");

	public static String nextSessionId() {
		return new BigInteger(130, random).toString(32);
	}
	public static void logv(String classname, String msg){
		System.out.println(classname+" : "+msg);
	}

}
