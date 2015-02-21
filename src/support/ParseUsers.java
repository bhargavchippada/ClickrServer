package support;

import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

import datahandler.ClassRoom;
import datahandler.UserProfile;


public class ParseUsers {
	String classname = "ParseUsers";
	
	public void parseFile(String path){
		ClassRoom.users_map.clear();
		Utils.logv(classname, path);
		File file = new File(path);
	
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNext()){
				String line = sc.next();
				StringTokenizer tokens = new StringTokenizer(line,",");
				UserProfile user = new UserProfile();
				user.username = tokens.nextToken();
				user.password = tokens.nextToken();
				user.name = tokens.nextToken();
				ClassRoom.addUser(user);
			}
			sc.close();
			Utils.logv(classname, "File parsing successful");
		} catch (Exception e) {
			Utils.logv(classname, "File parsing failed");
			e.printStackTrace();
		}
	}
}
