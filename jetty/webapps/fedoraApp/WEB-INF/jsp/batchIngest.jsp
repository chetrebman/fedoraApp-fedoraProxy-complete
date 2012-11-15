<%--
/*
 * Copyright 2011 University of Denver
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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ page import="edu.du.penrose.systems.fedoraApp.ProgramProperties" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.FedoraAppConstants" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestThreadManager" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.tasks.WorkerTimer" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil" %>

<%
	ProgramProperties progProp = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() );

		// batchSetName is only set when the controller is ready to display the submit button.
    String	batchSetName = (String) request.getSession().getAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE );
	
    if ( batchSetName == null ){
    	batchSetName = "NOT_SET"; // so we can test it for 'mixed' later without getting a null pointer exception.
    }
    String inputSize="10";
	
    String allowManualAndRemoteIngestCB = progProp.getProperty( FedoraAppConstants.BATCH_INGEST_ENABLE_SIMULTANEOUS_MANUAL_AND_REMOTE_INGESTS_CHECKBOX_PROPERTY );
    String disableManualRemoteIngestCB = "true";
    if ( allowManualAndRemoteIngestCB != null && allowManualAndRemoteIngestCB.toLowerCase().contains( "true" ) )
    {
    	disableManualRemoteIngestCB = "false";
    }

    boolean islandoraIngest = false;
    if ( FedoraAppUtil.getIngestType() == FedoraAppUtil.INGEST_TYPE.ISLANDORA ){
    	islandoraIngest = true;
    }
%>



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Fedora Batch Ingest</title>
</head>
<body>

<script language="javascript">
	// this passes the batch set name to the gwt code. see gwt/batchIngest/client/BatchIngestStatus.java
    var sessionAttributes = {
        batchSetName: "<%=batchSetName%>"
     };
</script>
       
<% // Display title and fedoraApp Version %>

<p align="center" >
    <i><font color="#6699CC" size="6"><b><spring:message code="applicationTitle" text="resource NOT found" /></b></font></i> 
    <br>
    <a href="versions.htm">
    	<i><font color="#6699CC" size="6"><b><spring:message code="version" text="version NOT found" /></b></font></i> 
    </a>
	<br><br> 
</p>
		
<% // display main ingest form %>

<script type="text/javascript">
var test ="asfsadf";
function setIdAndSubmit(id) {
	document.frmBatchIngest.submitId.value = id;
	document.frmBatchIngest.submit();
}
</script>


<form:form name="frmBatchIngest" commandName="batchIngestOptions">

	<form:hidden path="submitId" />
	
	<table align="center">
		<tr>
			<td>
				<a href="index.htm">Home</a>&nbsp;|&nbsp;<a href="viewRunningIngests.htm">View running ingest tasks.</a>
			</td>
		</tr>
		<tr>
		<td>
			<table align="center" border="1" bgcolor="#F9FBFD"  CELLPADDING="4" CELLSPACING="1" >
		    <tr>
		        <td style="min-width: 800px;" align="center"  colspan="2"><b><font color="#660099"><spring:message code="batchIngest.formTitle" text="resource NOT found"      /></font></b></td>
		    </tr>
		    
		    <tr>
		    	<td colspan="2" align="left">
		    		<b><font color="#FF0000">*</font><spring:message code="batchIngest.institutionDropdown" text="resource NOT found" /> &nbsp; </b>           
					&nbsp;
		        	<b>
		        	    <!-- < %=xxx%> won't work here -->
		        		<form:select path="institution" multiple="false" onchange="setIdAndSubmit( \"institution\" );"  >
                            <form:option value="<%=FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE%>"  label="<%=FedoraAppConstants.FORM_DEFAULT_SELECT_LABEL%>"/>
                            <form:options items="${ batchIngestOptions.institutionMap }"/>
                  		</form:select>  
		        	
		        		<FONT COLOR="red"> <form:errors	path="institution" /> </FONT></b>           
		       </td>
		    </tr>  
		     
		    <tr>
		    	<td colspan="2" align="left">
		    		<b><font color="#FF0000">*</font><spring:message code="batchIngest.batchSetDropdown" text="resource NOT found" /> &nbsp;&nbsp; </b>           
					&nbsp;
		        	<b>
		        		<form:select path="batchSet" multiple="false" onchange="setIdAndSubmit( \"batchSet\" );">
                            <form:option value="<%=FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE%>" label="<%=FedoraAppConstants.FORM_DEFAULT_SELECT_LABEL%>"/>
                            <form:options items="${ batchIngestOptions.batchSetMap }"/>      
                  		</form:select>  
		        	
		        		<FONT COLOR="red"> <form:errors	path="batchSet" /> </FONT></b>           
		       </td>
		    </tr>
		    
	<% if ( islandoraIngest  && ! batchSetName.contains( FedoraAppConstants.MIXED_CONTENT_DIRECTORY )  ) { %>	         
		    <tr>
		    	<td colspan="2" align="left">
		    		<b><spring:message code="batchIngest.collectionSetDropdown" text="resource NOT found" /> &nbsp;&nbsp; </b>           
					&nbsp;
		        	<b>
		        		<form:select path="fedoraCollection" multiple="false" onchange="setIdAndSubmit( \"fedoraCollection\" );">
		        				<!-- batchIngestOptions.fedoraCollectionMap[ batchIngestOptions.fedoraCollection ] map must be loaded for this to work -->
                            <form:option value="${ batchIngestOptions.fedoraCollection }" label="${ batchIngestOptions.fedoraCollection }"/>
                            
                            
                            	<!-- the default select is what the controller is going to see and is needed so it will download the map contents -->
                        
		                     <spring:bind path="fedoraCollection" >	
				    			<c:choose>	
					    			<c:when test="${status.value ne 'Please Select' }" >
					    			      <form:option value="<%=FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE%>" label="<%=FedoraAppConstants.FORM_DEFAULT_SELECT_LABEL%>"/>   
					    			</c:when>
					    		</c:choose>
					    	</spring:bind>           
               
                            <form:options items="${ batchIngestOptions.fedoraCollectionMap }"/>
                  		</form:select>  
		        		<form:select path="fedoraContentModel" multiple="false" onchange="setIdAndSubmit( \"contentModel\" ); ">
                            <form:option value="${ batchIngestOptions.fedoraContentModel }" label="${ batchIngestOptions.fedoraContentModel }"/>
                            
                             <spring:bind path="fedoraContentModel" >	
				    			<c:choose>	
					    			<c:when test="${status.value ne 'Please Select' }" >
					    			      <form:option value="<%=FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE%>" label="<%=FedoraAppConstants.FORM_DEFAULT_SELECT_LABEL%>"/>  
					    			</c:when>
					    		</c:choose>
					    	</spring:bind>       
                         
                         
                            <form:options items="${ batchIngestOptions.fedoraContentModelMap }"/>
                  		</form:select>  
                
		        		<FONT COLOR="red"> <form:errors	path="fedoraContentModel" /> </FONT></b>           
		       </td>
		    </tr>  
	<% } %>   
	 
		    <tr>
		        <td>
		        	<b><form:checkbox path="stopOnError" />
		        		<FONT COLOR="red"> <form:errors	path="stopOnError" /> </FONT></b>           
		       </td>
		    	<td>
		    		<b><spring:message code="batchIngest.stopOnErrorCheckbox" text="resource NOT found" /> &nbsp; </b>           
		    	</td> 
		    </tr>    
		    <tr>
		        <td>
		        	<b><form:checkbox path="clearLogFile" />
		        		<FONT COLOR="red"> <form:errors	path="clearLogFile" /> </FONT></b>           
		       </td>
		    	<td>
		    		<b><spring:message code="batchIngest.clearLogCheckbox" text="resource NOT found" /> &nbsp; </b>           
		    	</td> 
		    </tr> 
		    
		    <tr>
		        <td>
		        	<b><form:checkbox path="clearFailedFiles" />
		        		<FONT COLOR="red"> <form:errors	path="clearFailedFiles" /> </FONT></b>           
		       </td>
		    	<td>
		    		<b><spring:message code="batchIngest.clearFailedFilesCheckbox" text="resource NOT found" /> &nbsp; </b>           
		    	</td> 
		    </tr>    
		    
		    <tr>
		        <td>
		        	<b><form:checkbox path="clearCompletedFiles" />
		        		<FONT COLOR="red"> <form:errors	path="clearCompletedFiles" /> </FONT></b>           
		       </td>
		    	<td>
		    		<b><spring:message code="batchIngest.clearCompletedFilesCheckbox" text="resource NOT found" /> &nbsp; </b>           
		    	</td> 
		    </tr>    
		    
		    <tr>
		        <td>
		        	<b><form:checkbox path="moveIngestedPCOsToCompleted" />
		        		<FONT COLOR="red"> <form:errors	path="moveIngestedPCOsToCompleted" /> </FONT></b>           
		       </td>
		    	<td>
		    		<b><spring:message code="batchIngest.moveIngestedPCOsToCompleted" text="resource NOT found" /> &nbsp; </b>           
		    	</td> 
		    </tr>
		    
		    <tr>
		        <td>
		        	<b><form:checkbox path="validatePCOchecksums" />
		        		<FONT COLOR="red"> <form:errors	path="validatePCOchecksums" /> </FONT></b>           
		       </td>
		    	<td>
		    		<b><spring:message code="batchIngest.validatePCOchecksums" text="resource NOT found" /> &nbsp; </b>           
		    	</td> 
		    </tr>
<!--		 	    -->
<!--		    <tr> -->
<!--		        <td>-->
<!--		        	<b><form:checkbox path="strictUpdates" disabled="true" />-->
<!--		        		<FONT COLOR="red"> <form:errors	path="strictUpdates" /> </FONT></b>           -->
<!--		       </td>-->
<!--		    	<td> -->
<!--		    		<b><spring:message code="batchIndexer.strictUpdates" text="resource NOT found" /> &nbsp; </b>           -->
<!--		    	</td> -->
<!--		    </tr>-->
		    
		   
		   <c:if test="${batchIngestOptions.haveRemotePropertiesFile}" >
		    	<c:choose>    
			    	 <c:when test="${batchIngestOptions.remoteEnabled}" >
			    		<tr><td colspan="2"> <spring:message code="batchIndexer.remoteEnabled" text="resource NOT found" /> </td></tr>
			    	</c:when>
			    	<c:otherwise>
			    		<tr><td colspan="2"> <spring:message code="batchIndexer.remoteDisabled" text="resource NOT found" /> </td></tr>
			    	</c:otherwise>
		    	</c:choose>
		    	
			    <tr> 
			        <td>
			        	<b><form:checkbox path="allowSimultaneousManualAndRemoteIngest" onchange="setIdAndSubmit( \"manualAndRemoteCheckbox\" );" disabled="<%=disableManualRemoteIngestCB%>" />
			        		<FONT COLOR="red"> <form:errors	path="allowSimultaneousManualAndRemoteIngest" /> </FONT></b>    
			        		     
			       </td>
			    	<td> 
			    		<b><spring:message code="batchIndexer.allowSimultaneousManualAndRemoteIngest" text="resource NOT found" /> &nbsp; </b> 	
			        		</br> &nbsp;&nbsp;<spring:message code="batchIndexer.simultaneousCBmessageForRemoteTask" text="resource NOT found" />           
			    	</td> 
			    </tr>
			    
		    </c:if> 
		    
		    
		   <c:if test="${batchIngestOptions.haveTaskPropertiesFile}" >
		    	<c:choose>    
			    	 <c:when test="${batchIngestOptions.backgroundTaskEnabled}" >
			    		<tr><td colspan="2"> Background Task is currently Enabled </td></tr>
			    	</c:when>
			    	<c:otherwise>
			    		<tr><td colspan="2"> Background Task is currently Disabled </td></tr>
			    	</c:otherwise>
		    	</c:choose>
		    	
			    <tr> 
			        <td>
			        	<b><form:checkbox path="allowSimultaneousManualAndRemoteIngest" onchange="setIdAndSubmit( \"manualAndRemoteCheckbox\" );" disabled="<%=disableManualRemoteIngestCB%>" />
			        		<FONT COLOR="red"> <form:errors	path="allowSimultaneousManualAndRemoteIngest" /> </FONT></b>    
			        		     
			       </td>
			    	<td> 
			    		<b><spring:message code="batchIndexer.allowSimultaneousManualAndBackgroundIngest" text="resource NOT found" /> &nbsp; </b> 	
			        		</br> &nbsp;&nbsp;<spring:message code="batchIndexer.simultaneousCBmessageForBackgroundTask" text="resource NOT found" />           
			    	</td> 
			    </tr>
		    </c:if> 
		    
		<tr>  	
	        <td> 
	        	<b>
	        	
	        		<form:checkbox path="splitXMLinWorkDirToMets"  onchange="setIdAndSubmit( \"splitCheckBox\" )" /> 
	        		
	        		<FONT COLOR="red"> <form:errors	path="splitXMLinWorkDirToMets" /> </FONT></b>    
	  		
	       </td>  
	       
	       <td align="left">
	       			      
	    		<b>Split <font color="#660099">work/        
	           
	        		<form:select path="workFile" multiple="false" id="test"   onchange="setIdAndSubmit( \"workFile\" )" >
                           <form:option value="<%=FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE%>" label="<%=FedoraAppConstants.FORM_DEFAULT_SELECT_LABEL%>"/>
                           <form:options items="${ batchIngestOptions.workFileMap }"/>
                 		</form:select>  
	        	</font> to mets directory prior to ingest.</b> 
	        	
	        	<b><FONT COLOR="red"> <form:errors	path="workFile" /> </FONT></b>   
	        	 <spring:bind path="workFile" >	
	        		 <input name="initialWorkFileValue" type="hidden" value="${status.value}" />
	        	 </spring:bind>        
	       </td>
	    </tr> 
   
		  
		    
<!--		    <spring:bind path="splitXMLinWorkDirToMets" >	-->
<!--		    <c:if test="${status.value ne true}" >-->
<!--			    <tr>-->
<!--			        <td>-->
<!--			        	<form:checkbox path="batchIsUpdates" />      -->
<!--			       </td>-->
<!--			    	<td>-->
<!--			    		<b><spring:message code="batchIndexer.batchIsUpdates" text="resource NOT found" /> &nbsp; </b>           -->
<!--			    	</td> -->
<!--			    </tr>	 -->
<!--			</c:if> 	-->
<!--		    </spring:bind>-->
		    
		    	
		    <tr>
		        <td>
		        	<form:checkbox path="setObjectInactive" />      
		       </td>
		    	<td>
		    		<b><spring:message code="batchIndexer.setObjectInactive" text="resource NOT found" /> &nbsp; </b>           
		    	</td> 
			</tr>	

			
			<% if ( batchSetName.equals("NOT_SET") || ! BatchIngestThreadManager.isBatchSetThreadExists( batchSetName ) ) { %>
				<% if ( batchSetName.equals("NOT_SET") ) { %>
				    <tr >
				       	<td align="center"  colspan="2" >&nbsp;       		
				       		<font color="red"><b>Please select an Institution and Batch Set and Collection</b></font>     		
				       	</td>
				    </tr>
				<% } else { %>
				    <tr >
				       	<td align="center"  colspan="2" >&nbsp;
				       	
				       		<% boolean displaySubmit = true; %>
				       	
									 <c:if test="${batchIngestOptions.haveRemotePropertiesFile}" >
									 	<% displaySubmit=false; %>
								    	<c:choose>    
									    	 <c:when test="${ batchIngestOptions.allowSimultaneousManualAndRemoteIngest }" >
									    		<INPUT TYPE="submit" name="ingestAll" VALUE="Ingest All" >
									    	</c:when>
									    	<c:otherwise>
									    		<INPUT TYPE="submit" name="saveSettings" VALUE="Save Settings" >
									    	</c:otherwise>
								    	</c:choose>
								    </c:if> 
				   
									 <c:if test="${batchIngestOptions.haveTaskPropertiesFile}" >
									 	<% displaySubmit=false; %>
								    	<c:choose>    
									    	 <c:when test="${ batchIngestOptions.allowSimultaneousManualAndRemoteIngest }" >
									    		<INPUT TYPE="submit" name="ingestAll" VALUE="Ingest All" >
									    	</c:when>
									    	<c:otherwise>
									    		<INPUT TYPE="submit" name="saveSettings" VALUE="Save Settings" >
									    	</c:otherwise>
								    	</c:choose>
								    </c:if> 
								    
								    
							<% if (displaySubmit) { %>	
								<INPUT TYPE="submit" name="ingestAll" VALUE="Ingest All" >
							<% } %>    
						    
				       	</td>
				    </tr>		
				<% } %>
			<% } else { %>
				<tr >
			       	<td align="center"  colspan="2" >&nbsp;
			       		
			       			<INPUT TYPE="submit" name="viewStatus" VALUE="Running: View Status" >
			       		
			       	</td>
			    </tr>
			<% } %>
		    
		    </table>  
  		</td>
  		</tr>
  	</table>
</form:form>

<br>
<br>

<table align="center" >
	<tr>
		<td>
			<b>Fedora Context = </b>
		</td>
		<td>
		    <spring:bind path="batchIngestOptions.institution" >
				<b>Fedora context=<c:out value="${status.value}"></c:out></b>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td>
			<b>Batch Ingest Top Folder URL = </b>
		</td>
		<td> <b>
			<%= progProp.getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY ) %> </b>
		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<% if ( ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty(FedoraAppConstants.BATCH_INGEST_DISABLE_GET_HANDLE_PROPERTY) != null ) { %>
		<tr>
			<td  align="center"  colspan="2" >
				<b><blink><font color="red">WARNING: Access to Handle Server is DISABLED</font><blink></b>
			</td>
		</tr>
	<% } %>
	<tr>
		<td>
			<b>World Handle Server = </b>
		</td>
		<td> <b>
			<%= ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_WORLD_HANDLE_SERVER_PROPERTY ) %> </b>
		</td>
	</tr>
	<tr>
		<td>
			<b>Handler Server = </b>
		</td>
		<td> <b>
			<%= progProp.getProperty( FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_PROPERTY ) %> </b>
		</td>
	</tr>
	<tr>
		<td>
			<b>Handle Server Port = </b>
		</td>
		<td> <b>
			<%= progProp.getProperty( FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_PORT_PROPERTY ) %> </b>
		</td>
	</tr>
	<tr>
		<td>
			<b>Handle Server Application = </b>
		</td>
		<td> <b>
			<%= progProp.getProperty( FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_APP_PROPERTY) %></b>
		</td>
	</tr>
	
		
</table>

</body>
</html>