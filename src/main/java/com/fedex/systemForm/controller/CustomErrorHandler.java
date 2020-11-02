package com.fedex.systemForm.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomErrorHandler implements ErrorController{

	@RequestMapping("/error")
	@ResponseBody
	public String handleError(HttpServletRequest request) {
	    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	    Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");

	    if (status != null) {
	        Integer statusCode = Integer.valueOf(status.toString());
	     
	        if(statusCode == HttpStatus.NOT_FOUND.value()) {	        	        	
	        	return String.format("<html><body><Center><h1>Page Not Found</h1><div><h2>Status code: <b>%s</b></h2></div>"
	                      			+ "<div><h2>Exception Message: <b>%s</b></h2></div></Center><body></html>",
	              statusCode, exception==null? "N/A": exception.getMessage());
	        }
	        else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {	        	        	
	        	return String.format("<html><body><Center><h1>Internal Server Error</h1><div><h2>Status code: <b>%s</b></h2></div>"
              			+ "<div><h2>Exception Message: <b>%s</b></h2></div></Center><body></html>",
	              statusCode, exception==null? "N/A": exception.getMessage());	        	
	        }
	        else if(statusCode == HttpStatus.UNAUTHORIZED.value()) {
	        	return String.format("<html><body><Center><h1>Missing HTTP Header X-CSR-SECURITY_TOKEN</h1><div><h2>Status code: <b>%s</b></h2></div>"
              			+ "<div><h2>Exception Message: <b>%s</b></h2></div></Center><body></html>",
	              statusCode, exception==null? "N/A": exception.getMessage());
	        }
	    }
	    	    
	    return String.format("<html><body><Center><h1>Somthing went Wrong..</h1></Center><body></html>");
      		
	}
	

	  @Override
	  public String getErrorPath() {
	      return "/error";
	  }
}
