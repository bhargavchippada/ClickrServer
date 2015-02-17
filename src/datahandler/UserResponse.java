package datahandler;

import java.util.ArrayList;

public class UserResponse {
	public int correct;
	public ArrayList<String> answer;
	
	public String responseString(){
		String output;
		if(correct==1) output="correct: ";
		else output="wrong: ";
		for(int i=0; i<answer.size();i++){
			output+=answer.get(i)+",";
		}
		return output;
	}
}
