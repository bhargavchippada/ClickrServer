package support;

import java.util.Scanner;
import java.util.StringTokenizer;

import datahandler.ClassRoom;
import datahandler.UserProfile;


/**Helper class to parse the userlist file and populate Singleton ClassRoom class 
 * @author bhargav
 *
 */
public class ParseUsers {
	String classname = "ParseUsers";
	
	public void parseFile(String file){
		ClassRoom.users_map.clear();
	
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNext()){
				String line = sc.next();
				StringTokenizer tokens = new StringTokenizer(line,",");
				UserProfile user = new UserProfile();
				user.username = tokens.nextToken();
				user.password = tokens.nextToken();
				user.name = tokens.nextToken();
				ClassRoom.users_map.put(user.username, user);
			}
			sc.close();
			Utils.logv(classname, "Users File parsing successful");
		} catch (Exception e) {
			Utils.logv(classname, "Users File parsing failed");
			e.printStackTrace();
		}
	}
}
