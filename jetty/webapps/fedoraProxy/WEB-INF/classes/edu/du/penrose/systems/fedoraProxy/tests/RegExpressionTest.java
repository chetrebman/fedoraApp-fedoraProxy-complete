package edu.du.penrose.systems.fedoraProxy.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraProxy.FedoraProxyConstants;

import edu.du.penrose.systems.util.FileUtil;

public class RegExpressionTest {



	@Test
	public void testRegExpression() throws Exception 
	{	
		String testString = " <datestamp>2010-12-07T18:37:29Z</datestamp>lllllllllllllllll<datestamp>2010-12-07T18:37:29Z</datestamp>";

		System.out.println( "\n\n"+testString+"\n" );
		testString = testString.replaceAll("<datestamp>\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\dZ</datestamp>", "<datestamp>2011-08-04T00:00:00Z</datestamp>" );

		System.out.println( testString );
	}


} // batchCmdFileName
