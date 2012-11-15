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
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraProxy.FedoraProxyConstants;

import edu.du.penrose.systems.util.FileUtil;

public class BatchCmdFileNameTest {



	@Test
	public void testGetNewEctdBatchFileName() throws Exception 
	{	
		String fileName = FileUtil.getBatchUniqueFileName( "testFile", new BatchIngestOptions() );

		System.out.println( fileName );
	}


} // batchCmdFileName
