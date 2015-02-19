package webconnectionjdbc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import support.ByteBuffer;
import support.MIMETypeConstantsIF;
import support.Utils;
import datahandler.Question;

public class PushQuiz extends HttpServlet{
	
	String classname = "PushQuiz";
	
	@Override 
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		    throws ServletException, IOException
	{
	  ServletOutputStream sos = res.getOutputStream();

	  res.setContentType(MIMETypeConstantsIF.PLAIN_TEXT_TYPE);
	  sos.write("This is the PushQuiz servlet".getBytes());
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
			Utils.logv(classname, "trying to extract hashtable from request");
	
		    ObjectInputStream ois = new ObjectInputStream(inputBB.getInputStream());
		    HashMap<String, String> input = (HashMap<String, String>) ois.readObject();
		    
		    Utils.logv(classname, "got the uid/pwd from the client:" + input);
	
		    Object retval = _processInput(input);
		    Utils.logv(classname, "created response hashtable, sending it back");
	
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
	private HashMap<String, Serializable> _processInput(HashMap<String, String> input) {
		
		HashMap<String, Serializable> output = new HashMap<String, Serializable>();
		output.put("questionContent",Question.questionContent);
		output.put("quesType",Question.quesType+"");
		output.put("options",Question.options);
		output.put("status","1");
		return output;
	}
}
