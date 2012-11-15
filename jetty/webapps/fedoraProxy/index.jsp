<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>fedoraProxy test page.</title>
</head>
<body>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>    
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>    


<% // Display title and Version %>

<p align="center" >
    <i><font color="#6699CC" size="6"><b><spring:message code="applicationTitle" text="resource NOT found" /></b></font></i> 
    <br>
    <a href="versions.txt">
    	<i><font color="#6699CC" size="6"><b><spring:message code="version" text="version NOT found" /></b></font></i> 
    </a>
	<br><br> 
</p>

<table align="left">
<tr><td>


<font color="red"><b>EXAMPLES</b></font>

 
<ul>
 <p align="left"><font color="red">Remote Ingest.</font></p>
<dl>
  <dt> <b>Send multipart post with attached BatchIngest.xml command and PCOs (Primary content objects ie. PDFs) to....</b></dt>  
       <dd> <br><font color="blue">http://localhost:9080/fedoraProxy/du/demo/fedoraAppDemoCollection/ingest.it</font><br></br>&nbsp; </dd>
</dl>
 <p align="left"><font color="red">Access datastream's directly, using the EXAMPLES below (substitute your institusion PID and datastream name).</font></p>
<dl>  
  <dt> <b>DC...</b></dt>
      <dd><font color="blue"><a href="http://localhost:9080/fedoraProxy/du/fedoraAppDemoCollection/datastream.get/demo:207/DC">http://localhost:9080/fedoraProxy/du/fedoraAppDemoCollection/datastream.get/demo:207/DC</a></font></dd> 
  <dt> <b>MODS...</b></dt>
      <dd><font color="blue"><a href="http://localhost:9080/fedoraProxy/du/fedoraAppDemoCollection/datastream.get/demo:207/MODS">http://localhost:9080/fedoraProxy/du/fedoraAppDemoCollection/datastream.get/demo:207/MODS</a></font></dd>    
  <dt> <b>IMAGE...</b></dt>
      <dd><font color="blue"><a href="http://localhost:9080/fedoraProxy/du/fedoraAppDemoCollection/datastream.get/demo:207/TN">http://localhost:9080/fedoraProxy/du/fedoraAppDemoCollection/datastream.get/demo:207/TN</a></font></dd>    
      
</dl>
<br>
 <p align="left"><font color="red">Solr proxy (substitute your institution and webSite name).</font></p>
<dl>
	<dt> <b> SOLR Select </b> </dt>
		<dd> <dd><font color="blue"><a href="http://localhost:9080/fedoraProxy/du/nation/solr/select?q=dc.subject:maps">http://localhost:9080/fedoraProxy/du/nation/solr/select?q=dc.subject:maps</a></font></dd>
		
</dl>
</ul>



</td>
</tr>
</table>
</body>
</html>
