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

import support.ByteBuffer;
import support.MIMETypeConstantsIF;
import support.ServerSettings;
import support.Utils;

public class Ping extends HttpServlet{
	/*
	 * This class returns some response to tell the user that the server is online
	 */
	String classname = "Ping";
	
	@Override 
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		    throws ServletException, IOException
	{

	  ServletOutputStream sos = res.getOutputStream();

	  res.setContentType(MIMETypeConstantsIF.PLAIN_TEXT_TYPE);
	  sos.write("This is the ping servlet".getBytes());
	  Utils.logi(classname, "WebClient doGet Method was called...");
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
			Utils.logi(classname, "trying to extract hashmap from request");
	
		    ObjectInputStream ois = new ObjectInputStream(inputBB.getInputStream());
		    HashMap<String, String> input = (HashMap<String, String>) ois.readObject();
		    
		    Utils.logi(classname, "got the ping from the client:" + input);
	
		    Object retval = _processInput(input);
		    Utils.logi(classname, "created response hashmap, sending it back");
	
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(baos);
		    oos.writeObject(retval);
		    
		    outputBB = new ByteBuffer(baos.toByteArray());		    
	
		    Utils.logi(classname,"sent response back...");
	
		  }
		  catch (Exception e) {
			Utils.logi(classname,"Error processing response");
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
			output.put("ping","0");
			outputBB = Utils.convertToBB(output);
		    response.setContentType(MIMETypeConstantsIF.BINARY_TYPE);
		    response.setContentLength(outputBB.getSize());
		    sos.write(outputBB.getBytes());
		  }
	
		  sos.flush();
		  sos.close();
	}
	
	/** put ping = 1 meaning the server is responding */
	private HashMap<String, String> _processInput(HashMap<String, String> input) {
		HashMap<String, String> output = new HashMap<String, String>();
		if(ServerSettings.serveronline==1){
			output.put("ping","1");
		}else{
			output.put("ping","0");
		}
		return output;
	}
}
