<%@tag body-content="empty" %>
<%@tag import="javax.servlet.http.HttpServletRequest" %>

<%@ attribute name="httpRequest" required="true" fragment="false" rtexprvalue="true" type="javax.servlet.http.HttpServletRequest" %>

          
<%
   HttpServletRequest result = request = (HttpServletRequest) jspContext.getAttribute("httpRequest");
%>

<% int statusCode; 
   String StatusCodeMessage = null;
   Integer status_code = (Integer) request.getAttribute("javax.servlet.error.status_code"); 
   if ( status_code != null ) {

		switch ( status_code.intValue() ) {
		case 404: StatusCodeMessage = "Server Error - Page not found.";
		} // switch
 		
 	} // if not null
%>

<% if ( StatusCodeMessage != null ) { %>
 	 	<font color="red"><b><%=StatusCodeMessage%></b></font>
<% } %>
 	