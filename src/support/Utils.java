package support;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Utils {
	public static String JSON_TYPE = "application/json";
	private static SecureRandom random = new SecureRandom();

	public static String nextSessionId() {
		return new BigInteger(130, random).toString(32);
	}
	public static void logv(String classname, String msg){
		System.out.println(classname+" : "+msg);
	}

}
