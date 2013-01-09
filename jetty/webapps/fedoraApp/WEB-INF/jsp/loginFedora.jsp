<%--
/*
 * Copyright 2012 University of Denver
 * Author Chet Rebman
 * 
 * This file is part of FedoraApp.
 * 
 * FedoraApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FedoraApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FedoraApp.  If not, see <http://www.gnu.org/licenses/>.
*/
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>

<HEAD>
<TITLE>Login</TITLE>
</HEAD>
<BODY> 

<form:form commandName="loginFedoraCmd">
<table   align="center">
  <tr>
    <td width="100%" align="center">
		<p align="center" >
    		<i><font color="#6699CC" size="6"><b><spring:message code="applicationTitle" text="resource NOT found" /></b></font></i> 
    		<br>
			<br><br>
		</p>
	</td>
  </tr>
  <tr>
    <td width="100%">
      <table border="0" width="100%">
        <tr>
          <td   colspan="1"><A HREF="hello.htm">Home</A></td>
        </tr>
      </table>    
     </td>
  </tr>
  <tr>
    <td width="100%">
      <div align="center">
        <center>
        
        <table border="1" width="100%" bgcolor="#F9FBFD"  CELLPADDING="4" CELLSPACING="1" >
          <tr>
            <td align="center"  colspan="2"><b><font color="#660099">Please Enter
              the Following Details</font></b></td>
          </tr>
          <tr>
            <td align="left" ><b><font color="#FF0000">*</font>Protocol: &nbsp; </b>           </td>
           <td align="left" >
                  <b><form:input path="protocol" size="30" /><FONT COLOR="red"> 
                  <form:errors	path="protocol" /></FONT></b>           </td>
          </tr>
          <tr>
            <td align="left" ><b><font color="#FF0000">*</font>Port: &nbsp; </b>           </td>
           <td align="left" >
                  <b><form:input path="port" size="30" /><FONT COLOR="red"> 
                  <form:errors	path="port" /></FONT></b>           </td>
          </tr>
          <tr>
            <td align="left" ><b><font color="#FF0000">*</font>Server: &nbsp; </b>           </td>
           <td align="left" >
                  <b><form:input path="host" size="30" /><FONT COLOR="red"> 
                  <form:errors	path="host" /></FONT></b>           </td>
          </tr>
          <tr>
            <td align="left" ><b><font color="#FF0000">*</font>User Name: &nbsp; </b>           </td>
           <td align="left" >
                  <b><form:input path="username" size="30" /><FONT COLOR="red"> 
                  <form:errors	path="username" /></FONT></b>           </td>
          </tr>
          <tr>
            <td align="left" ><b><font color="#FF0000">*</font>Password: &nbsp;</b>            </td>
           <td align="left" >
                  <b><form:password path="password" size="30" /><FONT COLOR="red"> 
                  <form:errors	path="password" /></FONT></b>           </td>
          </tr>
          <tr>
            <td align = "center"colspan="2"><b><font color="#FF0000">* Means Required Field</font></b>            </td>
          </tr>
          <tr>
            <td colspan="2" align="center" ><INPUT TYPE="submit" VALUE="Login"></td>
          </tr>
        </table>     
        </center>
      </div>    </td>
  </tr>

</table>

</form:form>
</BODY>
</html>
