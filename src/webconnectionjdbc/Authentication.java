package webconnectionjdbc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import datahandler.ClassRoom;
import datahandler.UserProfile;
import support.ByteBuffer;
import support.MIMETypeConstantsIF;
import support.ServerSettings;
import support.Utils;

public class Authentication extends HttpServlet{
	
	String classname = "Authentication";
	
	@Override 
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		    throws ServletException, IOException
	{


	  ServletOutputStream sos = res.getOutputStream();

	  res.setContentType(MIMETypeConstantsIF.PLAIN_TEXT_TYPE);
	  sos.write("This is the Authentication servlet".getBytes());
	  Utils.println("Authentication doGet Method was called...");
	  sos.flush();
	  sos.close();

	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		ByteBuffer inputBB = new ByteBuffer(request.getInputStream());
		ByteBuffer outputBB = null;
		  try {
	
		    // extract the hashmap
			Utils.logi(classname, "trying to extract hashtable from request");
	
		    ObjectInputStream ois = new ObjectInputStream(inputBB.getInputStream());
		    HashMap<String, String> input = (HashMap<String, String>) ois.readObject();
		    
		    Utils.logi(classname, "got the uid/pwd from the client:" + input);
	
		    Object retval = _processInput(input);
		    Utils.logi(classname, "created response hashtable, sending it back");
	
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(baos);
		    oos.writeObject(retval);
		    
		    outputBB = new ByteBuffer(baos.toByteArray());		    
	
		    Utils.println("sent response back...");
	
		  }
		  catch (Exception e) {
			Utils.println("Error processing response");
		    System.out.println(e);
		    e.printStackTrace();
		  }
	
		  ServletOutputStream sos = response.getOutputStream();
	
		  if (outputBB != null) {
		    response.setContentType(MIMETypeConstantsIF.BINARY_TYPE);
		    response.setContentLength(outputBB.getSize());
		    sos.write(outputBB.getBytes());
		  }
		  else {
			HashMap<String, String> output = new HashMap<String, String>();
			output.put("status","0");
			outputBB = Utils.convertToBB(output);
		    response.setContentType(MIMETypeConstantsIF.BINARY_TYPE);
		    response.setContentLength(outputBB.getSize());
		    sos.write(outputBB.getBytes());
		  }
	
		  sos.flush();
		  sos.close();
	}
	
	/** actually get user profile object from service and send it back */
	private HashMap<String, String> _processInput(HashMap<String, String> input) {
		
		HashMap<String, String> output = new HashMap<String, String>();
		String uid = input.get("uid");
		String pwd = input.get("pwd");
		UserProfile user = ClassRoom.users_map.get(uid);
		if(ServerSettings.serveronline==1 && user!=null && user.getPassword().equals(pwd)){
	    	output.put("status","1");
	    	output.put("name", user.name);
	    	output.put("clsnm", ClassRoom.clsnm);
	    }else{
	    	output.put("status","0");
	    }
		return output;

	}
}
