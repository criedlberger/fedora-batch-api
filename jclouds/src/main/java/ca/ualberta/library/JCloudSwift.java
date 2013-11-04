package ca.ualberta.library;
import static com.google.common.io.Closeables.closeQuietly;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;

import static org.jclouds.blobstore.options.PutOptions.Builder.multipart;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.BlobStoreContext;
//import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.io.Payload;
import org.jclouds.logging.config.ConsoleLoggingModule;
import org.jclouds.openstack.swift.*;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.rest.*;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.openstack.swift.options.CreateContainerOptions;
import org.jclouds.openstack.swift.CopyObjectException;

import com.google.common.io.Closeables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;

import java.io.*;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JCloudSwift implements Closeable {
   private BlobStore storage;
   private RestContext<CommonSwiftClient, CommonSwiftAsyncClient> swift;
   private static final Log log = LogFactory.getLog(JCloudSwift.class);

   private void init() {
      Iterable<Module> modules = ImmutableSet.<Module> of(
            new ConsoleLoggingModule());

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

    /**
    * List the Cloud Files containers associated with your account using Swift API.
    *  
    * @author Chris Riedlberger
    */
   private void listContainers() {
	   
	   log.info("List Containers");
	   Set<ContainerMetadata> containers = swift.getApi().listContainers();
	   
	   for (ContainerMetadata container: containers) {
	         System.out.format("Container: %s%n", container);
	   }
   }

   /**
   * Create container using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void createContainer(String container) {
	   log.info("Create Container");
	   
	   if (!swift.getApi().containerExists(container)) {
		   if (!swift.getApi().createContainer(container)) {
			   System.out.format("Error creating container: %s%n", container);
		   }
		   else {
			   System.out.format("Container created: %s%n", container);
		   }
	   }
	   else {
		   System.out.format("Container exists already: %s%n", container);
	   }

   }

   /**
   * Get container metadata using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void getContainerMetadata(String container) {
	   log.info("Get Container Metadata");
	   
	   ContainerMetadata metadata = null;
	   
	   if (swift.getApi().containerExists(container)) {
		   metadata = swift.getApi().getContainerMetadata(container);
		   
		   if (metadata == null) {
			   System.out.format("Error getting container metadata: %s%n", container);
		   }
		   else {
			   System.out.format("Count: %s%n Bytes: %s%n", metadata.getCount(), metadata.getBytes());
		   }
	   }
	   else {
		   System.out.format("Container does not exist: %s%n", container);
	   }

   }

   /**
   * Set container metadata using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void setContainerMetadata(String container, Map<String, String> metadata) {
	   log.info("Get Container Metadata");
	   
	   if (swift.getApi().containerExists(container)) {
		   if(!swift.getApi().setContainerMetadata(container, metadata)) {
			   System.out.format("Error setting container metadata: %s%n", container);
		   }
	   }
	   else {
		   System.out.format("Container does not exist: %s%n", container);
	   }

   }

   /**
   * Set container metadata using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void deleteContainerMetadata(String container, Iterable<String> metadata) {
	   log.info("Delete Container Metadata");
	   
	   if (swift.getApi().containerExists(container)) {
		   if(!swift.getApi().deleteContainerMetadata(container, metadata)) {
			   System.out.format("Error setting container metadata: %s%n", container);
		   }
	   }
	   else {
		   System.out.format("Container does not exist: %s%n", container);
	   }

   }

   /**
   * Create container with a set of metadata using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void createContainer(String container, String key1, ImmutableMap <String, String> metadata) {
	   log.info("Create Container");
	   
	   if (!swift.getApi().containerExists(container)) {
		   CreateContainerOptions options = CreateContainerOptions.Builder
	              .withMetadata(metadata);

		   if (!swift.getApi().createContainer(container, options)) {
			   System.out.format("Error creating container: %s%n", container);
		   }
		   else {
			   System.out.format("Container created: %s%n", container);
		   }
	   }
	   else {
		   System.out.format("Container exists already: %s%n", container);
	   }

   }

   /**
   * Delete container using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void deleteContainer(String container) {
	   log.info("Delete Container");
	   
       Set<ContainerMetadata> containers = swift.getApi()
              .listContainers(ListContainerOptions.Builder.withPrefix(container));

       for (ContainerMetadata containerMetadata: containers) {
           System.out.format("  %s%n", containerMetadata.getName());

           Set<ObjectInfo> objects = swift.getApi().listObjects(containerMetadata.getName());

           for (ObjectInfo object: objects) {
              System.out.format("    %s%n", object.getName());

              swift.getApi().removeObject(containerMetadata.getName(), object.getName());
           }
           
           swift.getApi().deleteContainerIfEmpty(containerMetadata.getName());
      }
   }

   /**
   * List the Cloud Files objects in a container using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void listObjects(String container) {
	   log.info("List Objects");
	   
	   if (swift.getApi().containerExists(container)) {
		   Set<ObjectInfo> objects = swift.getApi().listObjects(container);

		   for (ObjectInfo objectInfo: objects) {
		       System.out.format("  %s%n", objectInfo);
		   }
	   }
	   else {
		   System.out.format("Container does not exist: %s%n", container);
	   }
   }

   /**
   * List the Cloud Files objects with a given prefix in a container using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void listObjectsWithFiltering(String container, String prefix) {
      log.info("List Objects With Filtering");

	  if (swift.getApi().containerExists(container)) {
		  ListContainerOptions filter = ListContainerOptions.Builder.withPrefix(prefix);
		  Set<ObjectInfo> objects = swift.getApi().listObjects(container, filter);

		  for (ObjectInfo objectInfo: objects) {
			  System.out.format("  %s%n", objectInfo);
		  }
	  }
	  else {
		   System.out.format("Container does not exist: %s%n", container);
	  }
		  
   }   
	      
   /**
   * List the Cloud Files objects under a given path in a container using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void listObjectsUnderPath(String container, String path) {
      log.info("List Objects Under Path");

	  if (swift.getApi().containerExists(container)) {
		  ListContainerOptions filter = ListContainerOptions.Builder.underPath(path);
		  Set<ObjectInfo> objects = swift.getApi().listObjects(container, filter);

		  for (ObjectInfo objectInfo: objects) {
			  System.out.format("  %s%n", objectInfo);
		  }
	  }
	  else {
		   System.out.format("Container does not exist: %s%n", container);
	  }
		  
   }   
	      
   /**
   * List the Cloud Files objects after a given container marker using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void listObjectsAfterMarker(String container, String marker) {
      log.info("List Objects With Under Path");

	  if (swift.getApi().containerExists(container)) {
		  ListContainerOptions filter = ListContainerOptions.Builder.afterMarker(marker);
		  Set<ObjectInfo> objects = swift.getApi().listObjects(container, filter);

		  for (ObjectInfo objectInfo: objects) {
			  System.out.format("  %s%n", objectInfo);
		  }
	  }
	  else {
		   System.out.format("Container does not exist: %s%n", container);
	  }
		  
   }   
	      
   /**
   * Delete single object from container using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void deleteObject(String container, String filename) {
	   log.info("Delete Object");
	   
	   try {
		   if (swift.getApi().objectExists(container, filename)) {
			   swift.getApi().removeObject(container, filename);
		   }
		   else {
			   System.out.format("File does not exist: %s%n", filename);
			   String message = "File does not exist: " + filename;
			   log.info(message);
		   }
	   }
	   catch (ContainerNotFoundException e) {
		   e.printStackTrace();
		   log.error(e.getMessage());
	   }
   }

   /**
   * Set object info using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void setObjectInfo(String container, String filename, Map <String, String> metadata) {
	   log.info("Set Object Info");
	   
	   try {
		   if (swift.getApi().objectExists(container, filename)) {
			   swift.getApi().setObjectInfo(container, filename, metadata);
		   }
		   else {
			   System.out.format("File does not exist: %s%n", filename);
			   String message = "File does not exist: " + filename;
			   log.info(message);
		   }
	   }
	   catch (ContainerNotFoundException e) {
		   e.printStackTrace();
		   log.error(e.getMessage());
	   }
   }

   /**
   * Get object info using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private Map<String,String> getObjectInfo(String container, String filename) {
	   log.info("Get Object Info");
	   
	   try {
		   if (swift.getApi().objectExists(container, filename)) {
			 MutableObjectInfoWithMetadata metadata = swift.getApi().getObjectInfo(container, filename);
			 return metadata.getMetadata();
		   }
		   else {
			   System.out.format("File does not exist: %s%n", filename);
			   String message = "File does not exist: " + filename;
			   log.info(message);
		   }
	   }
	   catch (ContainerNotFoundException e) {
		   e.printStackTrace();
		   log.error(e.getMessage());
	   }
	   
	   return null;
   }

   /**
   * Delete the Cloud Files objects under a given path in a container using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void DeleteObjectsUnderPath(String container, String path) {
      log.info("Delete Objects Under Path");

	  if (swift.getApi().containerExists(container)) {
		  ListContainerOptions filter = ListContainerOptions.Builder.underPath(path);
		  Set<ObjectInfo> objects = swift.getApi().listObjects(container, filter);

		  for (ObjectInfo objectInfo: objects) {
			  swift.getApi().removeObject(container, objectInfo.getName());
		  }
	  }
	  else {
		   System.out.format("Container does not exist: %s%n", container);
	  }
		  
   }   
	      
   /**
   * Delete the Cloud Files objects with a given prefix in a container using Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void DeleteObjectsWithFiltering(String container, String prefix) {
      log.info("List Objects With Filtering");

	  if (swift.getApi().containerExists(container)) {
		  ListContainerOptions filter = ListContainerOptions.Builder.withPrefix(prefix);
		  Set<ObjectInfo> objects = swift.getApi().listObjects(container, filter);

		  for (ObjectInfo objectInfo: objects) {
			  swift.getApi().removeObject(container, objectInfo.getName());
		  }
	  }
	  else {
		   System.out.format("Container does not exist: %s%n", container);
	  }
		  
   }   
	      
   /**
   * Upload an object from a File using the Swift API.
   *  
   * @author Chris Riedlberger
   */
   private void uploadObjectFromFile(String container, File filename) throws IOException {
	   log.info("Upload Object From File");

	   if (!swift.getApi().objectExists(container, filename.getName())) {
		   SwiftObject object = swift.getApi().newSwiftObject();
		   object.getInfo().setName(filename.getName());
		   object.setPayload(filename);

		   swift.getApi().putObject(container, object);

		   System.out.format("  %s%n", filename);
	   }
	   else {
		   System.out.format("File exists already: %s%n", filename);
		   String message = "File exists already: " + filename;
		   log.info(message);
	   }
	   
	}

   /**
    * Upload an object from a String with metadata using the BlobStore API.
    *  
    * @author Chris Riedlberger
    */
	private void uploadObjectFromFile(String container, File filename, Map<String, String> userMetadata) {
		
		log.info("Upload Object From File With Metadata");

		if (!swift.getApi().objectExists(container, filename.getName())) {
//		    Map<String, String> userMetadata = new HashMap<String, String>();
//		    userMetadata.put(key1, value1);
		
		    Blob blob = storage.blobBuilder(filename.getName())
		           .payload(filename)
		           .userMetadata(userMetadata)
		           .build();
		
		    storage.putBlob(container, blob);
		
		    System.out.format("  %s%n", filename.getName());
		}
		else {
			System.out.format("File exists already: %s%n", filename.getName());
			String message = "File exists already: " + filename.getName();
			log.info(message);
		}
		
	}
	   
   /**
    * Upload a large object from a File using the Swift API. 
    * @throws ExecutionException 
    * @throws InterruptedException
    *  
    * @author Chris Riedlberger
    */
    private void uploadLargeObjectFromFile(String container, File largeFile) throws InterruptedException, ExecutionException {
    	
    	log.info("Upload Large Object From File");

		if (!swift.getApi().objectExists(container, largeFile.getName())) {
			
	    	Blob blob = storage.blobBuilder(largeFile.getName())
	    			.payload(largeFile)
	    			.contentDisposition(largeFile.getName())
	    			.build();
	      
	    	String eTag = storage.putBlob(container, blob, multipart());
	
	    	System.out.format("  Uploaded %s eTag=%s", largeFile.getName(), eTag);
		}
		else {
			System.out.format("File exists already: %s%n", largeFile.getName());
			String message = "File exists already: " + largeFile.getName();
			log.info(message);
		}
		
    }
	   
    /**
     * Download an object from using the Swift API.
     *  
     * @author Chris Riedlberger
     */
    private void getObject(String container, String filename) {
    	log.info("Get Object");
 	   
 	   if (swift.getApi().objectExists(container, filename)) {
 		  SwiftObject object = swift.getApi().getObject(container, filename);
 		  Payload payload = object.getPayload();
 		  InputStream is = payload.getInput();
 		  
 		  try {
 			  FileOutputStream output = new FileOutputStream(object.getInfo().getName());
 			  pipeStream(is, output, 4096);
 		  }
		  catch (NoSuchAlgorithmException e) {
	          e.printStackTrace();
 			   log.error(e.getMessage());
	      }
		  catch (IOException e) {
	          e.printStackTrace();
 			   log.error(e.getMessage());
	      }
	   }
 	   else {
 		   System.out.format("File does not exist: %s%n", filename);
 	   }
    }

    /**
     * Get the Cloud Files objects under a given path in a container using Swift API.
     *  
     * @author Chris Riedlberger
     */
     private void getObjectsUnderPath(String container, String path) {
        log.info("Get Objects Under Path");

  	  if (swift.getApi().containerExists(container)) {
  		  ListContainerOptions filter = ListContainerOptions.Builder.underPath(path);
  		  Set<ObjectInfo> objects = swift.getApi().listObjects(container, filter);

  		  for (ObjectInfo objectInfo: objects) {
  	 		  SwiftObject object = swift.getApi().getObject(container, objectInfo.getName());
  	 		  Payload payload = object.getPayload();
  	 		  InputStream is = payload.getInput();
  	 		  
  	 		  try {
  	 			  FileOutputStream output = new FileOutputStream(object.getInfo().getName());
  	 			  pipeStream(is, output, 4096);
  	 		  }
  			  catch (NoSuchAlgorithmException e) {
  		          e.printStackTrace();
  	 			   log.error(e.getMessage());
  		      }
  			  catch (IOException e) {
  		          e.printStackTrace();
  	 			   log.error(e.getMessage());
  		      }
  		  }
  	  }
  	  else {
  		   System.out.format("Container does not exist: %s%n", container);
  	  }
  		  
     }   
  	      
     /**
      * Get the Cloud Files objects with a given prefix in a container using Swift API.
      *  
      * @author Chris Riedlberger
      */
      private void GetObjectsWithFiltering(String container, String prefix) {
         log.info("List Objects With Filtering");

   	  if (swift.getApi().containerExists(container)) {
   		  ListContainerOptions filter = ListContainerOptions.Builder.withPrefix(prefix);
   		  Set<ObjectInfo> objects = swift.getApi().listObjects(container, filter);

   		  for (ObjectInfo objectInfo: objects) {
  	 		  SwiftObject object = swift.getApi().getObject(container, objectInfo.getName());
  	 		  Payload payload = object.getPayload();
  	 		  InputStream is = payload.getInput();
  	 		  
  	 		  try {
  	 			  FileOutputStream output = new FileOutputStream(object.getInfo().getName());
  	 			  pipeStream(is, output, 4096);
  	 		  }
  			  catch (NoSuchAlgorithmException e) {
  		          e.printStackTrace();
  	 			   log.error(e.getMessage());
  		      }
  			  catch (IOException e) {
  		          e.printStackTrace();
  	 			   log.error(e.getMessage());
  		      }
   		  }
   	  }
   	  else {
   		   System.out.format("Container does not exist: %s%n", container);
   	  }
   		  
      }   
   	      
   /**
     * Copy an object from one container to another using the Swift API.
     *  
     * @author Chris Riedlberger
     */
    private void copyObject(String sourceContainer, String sourceObject, String destinationContainer, String destinationObject) {
  	   log.info("Get Object");
  	   
  	   if (!swift.getApi().objectExists(destinationContainer, destinationObject)) {
  		   try {
  			   swift.getApi().copyObject(sourceContainer, sourceObject, destinationContainer, destinationObject);
  		   }
  		   catch (CopyObjectException e) {
  			   e.printStackTrace();
  			   log.error(e.getMessage());
  		   }
 	   }
  	   else {
  		   System.out.format("File exists already: %s%n", destinationObject);
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
				log.error("WARNING: Could not close stream.");
			}
		}
	}
	
    public void close() throws IOException {
	   Closeables.close(storage.getContext(), true);
    }
}

