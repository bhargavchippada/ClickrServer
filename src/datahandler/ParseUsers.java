package datahandler;

import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

import support.Utils;


public class ParseUsers {
	String classname = "ParseUsers";
	
	void parseFile(String path){
		ClassRoom.users_map.clear();
		Utils.logi(classname, path);
		File file = new File(path);
	
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNext()){
				String line = sc.next();
				StringTokenizer tokens = new StringTokenizer(line,",");
				UserProfile user = new UserProfile();
				user.rollnumber = tokens.nextToken();
				user.password = tokens.nextToken();
				user.name = tokens.nextToken();
				ClassRoom.addUser(user);
			}
			sc.close();
			Utils.logi(classname, "File parsing successful");
		} catch (Exception e) {
			Utils.logi(classname, "File parsing failed");
			e.printStackTrace();
		}
		
		//ClassRoom.print();

	}
	
	public static void main(String args[]){
		new ParseUsers().parseFile("users.txt");
	}
	
	
}
