package ca.ualberta.library.fedora.handle;

import net.handle.hdllib.*;
import net.handle.security.*;
import java.security.PrivateKey;

import java.io.*;
import java.util.*;
import java.math.BigInteger;
import java.security.*;
import ca.ualberta.library.fedora.mysql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

	public class CreateHandle {
	  
		private static final Log log = LogFactory.getLog(CreateHandle.class);
		
//		String uuid= null;
		protected static PrivateKey privkey;
		
//		public static void main(String[] args) {
		public String createHandle(String uuid) {    
		  
			
			String host = null;
			String handle = null;
			String prefixHandle = null;
			String index = null;
			String keyFile = null;
			String secKey = null;
			String handleID = null;
			String url = null;
			
			Properties props = new Properties();
			manageHandle sql = new manageHandle();
		  
			try {
				props.load(new FileInputStream("handle.properties"));
			  
				host = props.getProperty("host");
				handle = props.getProperty("handle");
				prefixHandle = props.getProperty("prefix-handle");
				index = props.getProperty("index");
				keyFile  = props.getProperty("keyFile");
				secKey  = props.getProperty("secKey");
				url  = props.getProperty("url");
			}	  
			catch (IOException e) {
				log.error(e.getMessage());
			}
		  
			byte[] key = null;
			try {
				File f = new File(keyFile);
				FileInputStream fs = new FileInputStream(f);
				key = new byte[(int)f.length()];
				int n=0;
				while(n<key.length) key[n++] = (byte)fs.read();
					fs.read(key);
			}
			catch (Throwable t){
				log.error("Cannot read private key " + keyFile + ": " + t);
				System.exit(-1);
			}

			HandleResolver resolver = new HandleResolver();

			PrivateKey privkey = null;
			try {
				key = Util.decrypt(key, secKey.getBytes("UTF8"));
				privkey = Util.getPrivateKeyFromBytes(key, 0);
			}
			catch (Throwable t){
				log.error("Can't load private key in " + key + ": " +t);
				System.exit(-1);
			}

			try {
				PublicKeyAuthenticationInfo auth = new PublicKeyAuthenticationInfo(handle.getBytes("UTF8"), 
	                                        									   Integer.valueOf(index), 
	                                        									   privkey);
	    
				AdminRecord admin = new AdminRecord(handle.getBytes("UTF8"), 300,
													true, true , true, true, true, true,
													true, true, true, true, true, true);
	                                          
				int timestamp = (int)(System.currentTimeMillis()/1000);

//				String uuid = "uuid:8d223b78-a160-43ff-896c-4e9abda85668";
				
				HandleValue[] val = { new HandleValue(100, "URL".getBytes("UTF8"),
	                                    Encoder.encodeAdminRecord(admin),
	                                    HandleValue.TTL_TYPE_RELATIVE, 0,
	                                    timestamp, null, true, true, true, false),
	                                  new HandleValue(1, "URL".getBytes("UTF8"),
	                                	Util.encodeString(url + uuid),
	                                    HandleValue.TTL_TYPE_RELATIVE, 0,
	                                    timestamp, null, true, true, true, false)};
	            
/*				HandleValue[] val = {new HandleValue(1, "URL".getBytes("UTF8"),
            	Util.encodeString(url + uuid),
                HandleValue.TTL_TYPE_RELATIVE, 0,
                timestamp, null, true, true, true, false)};*/
				
				sql.insertHandle(uuid);
				String suffixID = sql.readHandle(uuid);
				handleID = "10402/era." + suffixID;
				
				CreateHandleRequest req = new CreateHandleRequest(Util.encodeString(handleID), val, auth);
//				ModifyValueRequest req = new ModifyValueRequest((Util.encodeString("10402/era.26915")), val, auth);
//				RemoveValueRequest req = new RemoveValueRequest(Util.encodeString("10402/era.27022"), timestamp, auth); 
			
//				resolver.traceMessages = true;

				AbstractResponse response = resolver.processRequest(req);

				if (response.responseCode == AbstractMessage.RC_SUCCESS){
					log.info("Handle created: " + handleID);
					log.error("\nGot Response: \n"+response);
				}
				else {
					log.error("\nGot Error: \n"+response);
					System.exit(-1);
				}
			} catch (Throwable t) {
				log.error("\nError: "+t);
				System.exit(-1);
			}
			
			return(host + handleID);
		}
		
}