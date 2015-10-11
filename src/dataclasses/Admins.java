package dataclasses;

import java.util.concurrent.ConcurrentHashMap;

public class Admins {
	private static ConcurrentHashMap<String, Admin> AdminsList = new ConcurrentHashMap<String, Admin>();

	public static void addAdmin(String username, Integer adminid) {
		if (!adminExists(username)) AdminsList.put(username, new Admin(username, adminid));
	}

	public static Admin getAdmin(String username) {
		return AdminsList.get(username);
	}

	public static boolean adminExists(String username) {
		return AdminsList.containsKey(username);
	}
}
