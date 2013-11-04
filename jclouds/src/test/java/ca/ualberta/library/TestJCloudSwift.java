package ca.ualberta.library;

import java.util.Set;

import org.jclouds.openstack.swift.domain.ContainerMetadata;

import junit.framework.TestCase;

import static com.google.common.io.Closeables.closeQuietly;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.List;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;

import static org.jclouds.blobstore.options.PutOptions.Builder.multipart;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.BlobStoreContext;
//import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.io.Payload;
import org.jclouds.logging.config.ConsoleLoggingModule;
import org.jclouds.openstack.swift.*;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.rest.*;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.jclouds.openstack.swift.CopyObjectException;
import org.junit.Test;

import com.google.common.io.Closeables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class TestJCloudSwift extends TestCase {
	
	   private BlobStore storage;
	   private RestContext<CommonSwiftClient, CommonSwiftAsyncClient> swift;
	   private String container = "junit";
	   private String destinationContainer = "copy";
	   private String object = "test-11.pdf";
	   private String directoryPath = "test/";
	   String prefix = "test-1";
	   List<String> filenames = new ArrayList<String>();
	   long size = 0;

	   protected void setUp() {
		      JCloudSwift jCloudsSwift = new JCloudSwift();
		      
		      Iterable<Module> modules = ImmutableSet.<Module> of(
		            new ConsoleLoggingModule());

	    	  filenames.add("test-11.pdf");
	    	  filenames.add("test-12.pdf");
	    	  filenames.add("test-21.pdf");
	    	  filenames.add("test-22.pdf");
	   	   
	          Properties swiftProperties = new Properties();
	    	  try {
	    		  swiftProperties.load(new FileInputStream("swift.properties"));
				  
				  String provider = swiftProperties.getProperty("provider");
				  String identity = swiftProperties.getProperty("identity");
				  String password = swiftProperties.getProperty("password");
				  String endpoint = swiftProperties.getProperty("endpoint");
				  
			      Properties overrides = new Properties();
			      // This property controls the number of parts being uploaded in parallel, the default is 4
			      overrides.setProperty("jclouds.mpu.parallel.degree", "5");
			      // This property controls the size (in bytes) of parts being uploaded in parallel, the default is 33554432 bytes = 32 MB
			      overrides.setProperty("jclouds.mpu.parts.size", "67108864"); // 64 MB
	
	
			      BlobStoreContext context = ContextBuilder.newBuilder(provider)
			            .endpoint(endpoint)
			            .credentials(identity, password)
			            .overrides(overrides)
			            .modules(modules)
			            .buildView(BlobStoreContext.class);
			      storage = context.getBlobStore();
			      swift = context.unwrap();
	    	  }    
		      catch (IOException e) {
		    	  e.printStackTrace();
		      }
			      
	   }

       @Test	   
	   public void testListContainers() {
		   
		   Set<ContainerMetadata> containers = swift.getApi().listContainers();

		   assertNotNull(containers);
		   
	   }
       
       @Test
	   public void testListObjects() {
		   
   		   testUploadObjectFromFile();
    	   
		   if (swift.getApi().containerExists(container)) {
			   Set<ObjectInfo> objects = swift.getApi().listObjects(container);

			   int index =0;
			   for (ObjectInfo objectInfo: objects) {
				   assertEquals(container, objectInfo.getContainer());
				   assertEquals(filenames.get(index), objectInfo.getName());
				   assertSame(size, objectInfo.getBytes());
				   
				   index++;
			   }   
		   }
       }	   

       @Test
       public void testListObjectsWithFiltering() {
			
   		   testUploadObjectFromFile();
   		   
		   if (swift.getApi().containerExists(container)) {
				ListContainerOptions filter = ListContainerOptions.Builder.withPrefix(prefix);
				Set<ObjectInfo> objects = swift.getApi().listObjects(container, filter);

				int index = 0;
			    for (ObjectInfo objectInfo: objects) {
				    assertEquals(container, objectInfo.getContainer());
				    assertEquals(filenames.get(index), objectInfo.getName());
				    assertSame(size, objectInfo.getBytes());
				    
				    index++;
			    } 	 
			}
    			  
    	}   
    		      
       @Test
       public void testCreateContainer() {

    	   testDeleteContainer();
    	   
    	   if (!swift.getApi().containerExists(container)) {
        	   assertTrue(swift.getApi().createContainer(container));
    	   }
    	   
    	   assertTrue(swift.getApi().containerExists(container));
       }
       
       @Test
       public void testDeleteContainer() {
    	   
    	  if (swift.getApi().containerExists(container)) {
               Set<ObjectInfo> objects = swift.getApi().listObjects(container);

               for (ObjectInfo object: objects) {
                  swift.getApi().removeObject(container, object.getName());
               }
               
               assertTrue(swift.getApi().deleteContainerIfEmpty(container));
    	  }     
           
          assertFalse(swift.getApi().containerExists(container));
       }
       
       @Test
       public void deleteObject() {
    	   
    	   testUploadObjectFromFile();
    	   
		   for (String filename: filenames) {
			   if (swift.getApi().objectExists(container, filename)) {
				   swift.getApi().removeObject(container, filename);
			   }
			   
			   assertFalse(swift.getApi().objectExists(filename, container));
		   }	   
       }

       @Test
       public void testGetObject() {

    	  testUploadObjectFromFile();
    	   
		  for (String filename: filenames) {
	    	  File testfile = new File(directoryPath + filename);
			  if (testfile.exists()) {
				  testfile.delete();
			  }
	 	  		  
		 	  if (swift.getApi().objectExists(container, filename)) {
		  		  SwiftObject swiftObject = swift.getApi().getObject(container, filename);
		  		  Payload payload = swiftObject.getPayload();
		  		  InputStream is = payload.getInput();
		  		  
		  		  try {
					  File testFile = new File(directoryPath + filename);
					  
		  			  FileOutputStream output = new FileOutputStream(directoryPath + filename);
		  			  pipeStream(is, output, 4096);
		  			  
			   		  assertTrue(testFile.exists());
		  		  }
		 		  catch (NoSuchAlgorithmException e) {
		 	          e.printStackTrace();
		 	      }
		 		  catch (IOException e) {
		 	          e.printStackTrace();
		 	      }
		 	  }
		  }	  
	 	   
      }

      @Test 
      public void testUploadObjectFromFile() {

    	   testCreateContainer();

		   for (String filename: filenames) {
	    	   if (!swift.getApi().objectExists(container, filename)) {
	    		   try {
			    	   File testFile = new File(filename);
			    	   if(!testFile.exists()) {
			    		   testFile.createNewFile();
			    	   }
			    	   
			    	   SwiftObject swiftObject = swift.getApi().newSwiftObject();
		    		   swiftObject.getInfo().setName(testFile.getName());
		    		   swiftObject.setPayload(testFile);
		
		    		   swift.getApi().putObject(container, swiftObject);
		    		   
					   assertTrue(swift.getApi().objectExists(container, filename));
				   }	   
	        	   catch (IOException e) {
	        		   e.printStackTrace();
	        	   }
	    	   }
		   }   
    	   
   	   }
      
       @Test
       public void testCopyObject() {
     	   
     	   testUploadObjectFromFile();
     	   
		    for (String filename: filenames) {
	    	   if (swift.getApi().containerExists(destinationContainer)) {
		     	   if (swift.getApi().objectExists(destinationContainer, filename)) {
		     		   swift.getApi().removeObject(destinationContainer, filename);
		     	   }	   
	     	   }
		       else {
		    	   swift.getApi().createContainer(destinationContainer);
		       }
	
	 		   try {
	 			   swift.getApi().copyObject(container, filename, destinationContainer, filename);
	 		   }
	 		   catch (CopyObjectException e) {
	 			   e.printStackTrace();
	 		   }
	 	   
	     	   assertTrue(swift.getApi().objectExists(destinationContainer, filename));
		    }   
	     	   
        }
      
       
		public void pipeStream(InputStream in, FileOutputStream out, int bufSize) throws IOException, NoSuchAlgorithmException {

			try {
				byte[] buf = new byte[bufSize];
				int len;
				
				while ( ( len = in.read( buf ) ) > 0 ) {
					out.write( buf, 0, len );
				}
			} 
			finally {
				try {
					in.close();
					out.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    		

}
