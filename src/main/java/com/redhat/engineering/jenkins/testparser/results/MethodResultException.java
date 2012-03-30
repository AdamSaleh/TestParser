/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.engineering.jenkins.testparser.results;

/**
 *
 * @author Jan Rusnacko (jrusnack at redhat.com)
 */
public class MethodResultException {
    
    private String exceptionName;
    private String message;
    private String stackTrace;
   
    public MethodResultException(String message, String shortStackTrace, 
	    String FullStackTrace){
	
	this.message = message == null ? null : message.trim();
	setData(message, shortStackTrace, FullStackTrace);
    }
    
    // TODO: test functionality
    private void setData(String message, String shortStackTrace, 
	    String fullStackTrace){
	
	String tmpStackTrace = shortStackTrace;
	if (((shortStackTrace == null) || "".equals(shortStackTrace)) && (fullStackTrace != null)) {
	    // overwrite short st with full st, if available
	    tmpStackTrace = fullStackTrace;
	}

	stackTrace = tmpStackTrace.trim();
	int index = -1;

	if (message == null) {
	    //no message means first line will only show exception class name
	    index = stackTrace.indexOf("\n");
	    if (index != -1) {
		exceptionName = stackTrace.substring(0, index);
		stackTrace = stackTrace.substring(index + 1, stackTrace.length());
	    }
	} else {
	    message = message.trim();
	    //message being present means first line will be of type
	    //<exception class name>: <message>
	    index = stackTrace.indexOf(": ");
	    if (index != -1) {
		exceptionName = stackTrace.substring(0, index);
		stackTrace = stackTrace.substring(index + 2, stackTrace.length()).replace(message, "");
	    }
	}
	
    }
    
    public String getMessage() {
	return message;
    }

    public String getStackTrace() {
	return stackTrace;
    }
    public String getExceptionName() {
	return exceptionName;
    }
    
    @Override
    public String toString() {
	StringBuffer str = new StringBuffer();
	str.append(exceptionName).append(": ");
	if (message != null) {
	    str.append(message);
	}
	str.append("\n");
	str.append(stackTrace);
	return str.toString();
    }
}
