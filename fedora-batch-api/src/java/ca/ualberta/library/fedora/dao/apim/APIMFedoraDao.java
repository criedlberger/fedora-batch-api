/**
 * University of Alberta Libraries
 * Information Technology and Services
 * Project: fedora
 * $Id: RepositoryAPIMFedoraDao.java 3587 2009-01-21 17:31:58Z pcharoen $
 */
package ca.ualberta.library.fedora.dao.apim;

import static ca.ualberta.library.fedora.domain.Datastream.RELS_EXT_ID;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
//import java.io.OutputStream;
import java.net.URL;
//import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import javax.activation.*;
import javax.mail.util.*;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.AxisFault;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.xmlbeans.XmlOptions;
//import org.openarchives.oai.x20.oaiDc.DcDocument;


import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Namespace;
import org.dom4j.DocumentException;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import org.fcrepo.common.Constants;

import ca.ualberta.library.fedora.dao.APIMDao;
import ca.ualberta.library.fedora.domain.Properties;
import ca.ualberta.library.fedora.enums.ControlGroup;
import ca.ualberta.library.fedora.enums.Relationship;
import ca.ualberta.library.fedora.enums.State;
import ca.ualberta.library.fedora.exception.FedoraAPIException;
import ca.ualberta.library.fedora.client.Uploader;
import ca.ualberta.library.fedora.utils.FileHandler;
import ca.ualberta.library.fedora.handle.CreateHandle;
import org.fcrepo.client.utility.ingest.Ingest;
import org.fcrepo.client.FedoraClient;
//import org.fcrepo.utility.ingest.IngestCounter;
import org.fcrepo.server.management.FedoraAPIM;
//import org.fcrepo.server.management.FedoraAPIMServiceLocator;
import org.fcrepo.server.access.FedoraAPIA;
//import org.fcrepo.server.access.FedoraAPIAServiceLocator;
import org.fcrepo.server.types.gen.ArrayOfString;
import org.fcrepo.server.types.gen.Datastream;
import org.fcrepo.server.utilities.StreamUtility;
import org.fcrepo.server.access.FedoraAPIAMTOM;
import org.fcrepo.server.management.FedoraAPIMMTOM;

/**
 * The FedoraAPIMFedoraDao class.
 *
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 3587 $ $Date: 2009-01-21 10:31:58 -0700 (Wed, 21 Jan 2009) $
 */
public class APIMFedoraDao extends FedoraDao implements APIMDao, Constants {
	private static final Log log = LogFactory.getLog(APIMFedoraDao.class);

	protected HttpClient http;
	protected Uploader uploader;
	protected String pidNamespace;
	protected String restServicePath;
	protected CreateHandle handle;
    public static FedoraAPIAMTOM apia = null;
    public static FedoraAPIMMTOM apim = null;

	/**
	 * The FedoraAPIMFedoraDao class constructor.
	 *
	 * @throws FedoraAPIException
	 */
	public APIMFedoraDao() {
		super();
	}

	@Override
	public void init() {
		try {
			super.init();
//			FedoraClient client = new FedoraClient(protocol + "://" + host + ":" + port, username, password);
//			FedoraClient client = new FedoraClient("http://dundee.library.ualberta.ca:8080/", "fedoraAdmin", "fedoraAdmin");
			FedoraClient client = new FedoraClient("http://era.library.ualberta.ca:8180/", "fedoraAdmin", "QZqRu4WFfVrpDdCfzkiL");
			log.debug("Initializing fedoraAPIM...");
			apia = client.getAPIAMTOM();
			apim = client.getAPIMMTOM();

			log.debug("Initializing httpClient...");

			// create httpClient
			http = new HttpClient();
//			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("fedoraAdmin", "fedoraAdmin");
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("fedoraAdmin", "QZqRu4WFfVrpDdCfzkiL");
			AuthScope authScope = new AuthScope(host, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
			http.getState().setCredentials(authScope, credentials);
			http.getParams().setAuthenticationPreemptive(true);
			http.getParams().setHttpElementCharset("utf-8");

			// create uploader
//			uploader = new Uploader("http", "http://dundee.library.ualberta.ca", 8080, "fedoraAdmin", "fedoraAdmin");
			uploader = new Uploader("http", "http://dundee.library.ualberta.ca", 8080, "fedoraAdmin", "QZqRu4WFfVrpDdCfzkiL");

		} catch (Exception e) {
			log.error("Could not connect to FedoraAPIM Service!", e);
			// throw new FedoraAPIException("Could not connect to FedoraAPIM Service!", e);
		}
	}

	public InputStream getDatastreamContent(String pid, String dsId) throws FedoraAPIException {

		// get datastream as inputstream
		String url = protocol + "://" + host + ":" + port + restServicePath + "/get/" + pid + "/" + dsId;
		log.debug("url: " + url);
		GetMethod get = new GetMethod(url);
		get.setDoAuthentication(true);
		try {
			int status = http.executeMethod(get);
			if (status != 200) {
				throw new FedoraAPIException("Get Datastream Content Error Status: " + status);
			}
			return get.getResponseBodyAsStream();
		} catch (Exception e) {
			log.error("Could not get datastream content!", e);
			throw new FedoraAPIException("Could not get datastream context!", e);
		}
	}

	public List<Datastream> getDatastreams(String pid) throws FedoraAPIException {
		List<Datastream> dstms = null;
		try {
			dstms = apim.getDatastreams(pid, null, null);
		} catch (Exception e) {
			log.error("Could not get Datastreams!", e);
			throw new FedoraAPIException("Could not get Datastrams!", e);
		}
		return dstms;
	}

	public Datastream getDatastream(String pid, String dsId) throws FedoraAPIException {
		Datastream dstm = null;
		try {
			dstm = apim.getDatastream(pid, dsId, null);
		} catch (Exception e) {
			log.error("Could not get Datastream!", e);
			throw new FedoraAPIException("Could not get Datastream!", e);
		}
		return dstm;
	}

	public String ingest(String ns, String state, String label, String cModel, String ownerId, String message)
		throws FedoraAPIException {
		DataHandler dataHandler = null;
		String pid = getNextPID(ns);
		log.debug("ingesting pid: " + pid);
		try {
			StringBuffer xml = new StringBuffer();
			xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			xml.append("<foxml:digitalObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
			xml.append("           xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"\n");
			xml
				.append("           xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-0.xsd\"");
			if (pid != null) {
				xml.append("\n           PID=\"").append(StreamUtility.enc(pid)).append("\">\n");
			} else {
				xml.append(">\n");
			}
			xml.append("  <foxml:objectProperties>\n");
			xml.append("    <foxml:property NAME=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\" VALUE=\"").append(
				Properties.OBJECT_TYPE).append("\"/>\n");
			xml.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\""
				+ StreamUtility.enc(state) + "\"/>\n");
			xml.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\""
				+ StreamUtility.enc(label) + "\"/>\n");
			xml.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#contentModel\" VALUE=\""
				+ StreamUtility.enc(cModel) + "\"/>\n");
			xml.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"" + ownerId
				+ "\"/>\n");
			xml.append("  </foxml:objectProperties>\n");
			xml.append("</foxml:digitalObject>");
			String objXML = xml.toString();
  
			DataSource ds = new ByteArrayDataSource(objXML.getBytes(), "text/plain; charset=UTF-8");
			dataHandler = new DataHandler(ds); 
			pid = apim.ingest(dataHandler, "foxml1.0", message);
			log.debug("pid: " + pid);
		} catch (Exception e) {
			log.error("Could not ingest the object!", e);
			throw new FedoraAPIException("Could not ingest the object!", e);
		}
		return pid;
	}

    public static void main(String[] args) {
		try {
//			IngestCounter ingestCounter;
			
//			System.setProperty("javax.net.ssl.trustStore", "C:\\Fedora\\client\\truststore");
//			System.setProperty("javax.net.ssl.trustStorePassword", "tomcat");
			
			APIMFedoraDao apim = new APIMFedoraDao();
/* 		  	apim.setProtocol("http");
 		  	apim.setHost("dundee.library.ualberta.ca");
 		  	apim.setPort(new Integer(8080));
 		  	apim.setUsername("fedoraAdmin");
 		  	apim.setPassword("fedoraAdmin");  
 		  	apim.setAPIMServicePath("/fedora/services/management");
 		  	apim.setAPIAServicePath("/fedora/services/access");*/

	 		apim.init();

			System.out.println("\nIngest......................................................");

// 			apim.ingest("chris", "A", "test", null, "fedoraAdmin", "ingest");
			apim.ingest("C:\\Projects\\peel-migration-batch\\foxml");
//			ingestCounter = apim.ingest("H:\\ut\\foxml", "H:\\ut\\data\\ingestLog.txt");
//			System.out.println("Objects loaded: " + ingestCounter.successes);
//			System.out.println("Objects rejected: " + ingestCounter.failures);
		} catch (Exception e) {
		    System.out.println("Exception in main: " +  e.getMessage());
		    e.printStackTrace();
		}
	}        
	
/*	public IngestCounter ingest(String ingestDirectory, String logFile)
		throws FedoraAPIException {

		FileOutputStream outputStream = null;
		
		try {
			outputStream = new FileOutputStream(logFile);
		}
		catch (IOException e) {
			log.error("Could not find log file!", e);
			throw new FedoraAPIException("Could not find log file!", e);
		}
		
		PrintStream ingestLog = new PrintStream(outputStream);
	
		IngestCounter ingestCounter = new IngestCounter();
		File directory = new File(ingestDirectory);
		
		try {
			Ingest.multiFromDirectory(directory, FOXML1_1.uri, apia, apim, "ingest", ingestLog, ingestCounter);
		}
		catch (Exception e) {
			log.error("Could not ingest objects!", e);
			throw new FedoraAPIException("Could not ingest objects!", e);
		}  
	
		return ingestCounter;
	}*/

	public void ingest(String inputDirectory)
		throws IOException, FedoraAPIException, AxisFault {

		String pid = null;
		String objXML = null;
		FileInputStream inStream=null;
		
/*		StringBuffer dir = new StringBuffer(inputDirectory);
		dir.append("/");
		int dirLength = dir.length();
		File dirList = new File(dir.toString());
		String[] xmlFiles = dirList.list();
		if (xmlFiles == null) {
			log.error("No files to process on ingest on directory!");
			throw new FedoraAPIException("No files to process on ingest on directory!");
		}
		else {
			for (int fileIndex = 0; fileIndex < xmlFiles.length; fileIndex++) {*/
			File input = new File(inputDirectory);
		
			IOFileFilter fileFilter = new SuffixFileFilter(".xml");
			Collection files = FileUtils.listFiles(input, fileFilter, TrueFileFilter.INSTANCE);

			for (Iterator iterator = files.iterator(); iterator.hasNext();) {
				File ingestFile = (File) iterator.next();
        
				String DATE_FORMAT  = "yyyy-MM-ddk:m:s.S";
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
				Calendar currentDate = Calendar.getInstance();
				String fileDate = sdf.format(currentDate.getTime());
				fileDate = fileDate.substring(0, 10) + "T" + fileDate.substring(10) + "Z";
				
//				dir.append(xmlFiles[fileIndex]);
//				String filename = dir.toString();*/
//				File ingestFile=new File(filename);
				try {
					log.info("Processing file: " + ingestFile.toString());
					pid = getNextPID("uuid");
					
					inStream=new FileInputStream(ingestFile);
//					dir.delete(dirLength, dir.length());
					
					Document document = null;
			        SAXReader reader = new SAXReader();
			        document = reader.read(inStream);
			        
  			        Element rootElement = document.getRootElement();
			        rootElement.addAttribute("PID", pid);
			        
		            boolean fileFound = false;
		            Node node = null;
		            String datastreamID = null;
		            
	            	Iterator<Element> elementIterator = rootElement.elementIterator("datastream");
//	            	rootElement.addNamespace("dcterms","http://purl.org/dc/terms/" );
	            	while (elementIterator.hasNext()) {
	            		Element datastream = elementIterator.next();

	            		datastreamID = datastream.attributeValue("ID");
	            		if (datastreamID.equals("RELS-EXT")) {
	            			fileFound = true;
	            			
	            			node = (Node) datastream;
	            			Element description = (Element) node.selectSingleNode("foxml:datastreamVersion/foxml:xmlContent/rdf:RDF/rdf:Description");
	            			description.addAttribute("rdf:about", "info:fedora/" + pid);
  	            			Element itemID = description.addElement("itemID");
  	            			itemID.addNamespace("", "http://www.openarchives.org/OAI/2.0/");
  	            			itemID.addText("oai:era.library.ualberta.ca:" + pid);

 	            			Element workflowDate = description.addElement("workflowDate");
  	            			workflowDate.addAttribute("rdf:datatype", "http://www.w3.org/2001/XMLSchema#dateTime");
  	            			workflowDate.addNamespace("", "http://era.library.ualberta.ca/schema/definitions.xsd#");
  	            			workflowDate.addText(fileDate);
	            			
/*	            			Element datastreamVersion = datastream.element("datastreamVersion");
	            			Element xmlContent = datastreamVersion.element("xmlContent");
	            			Element rdf = xmlContent.element("RDF");
	            			Element description = rdf.element("Description");
	            			description.addAttribute("rdf:about", "info:fedora/" + pid);*/
	            		}
	            		
	            		if (datastreamID.equals("DCQ")) {
	            			node = (Node) datastream;
/*	            			Element dc = (Element) node.selectSingleNode("foxml:datastreamVersion/foxml:xmlContent/dc");
	    	            	Iterator<Element> identifierIterator = dc.elementIterator("identifier");
	    	            	while (identifierIterator.hasNext()) {
	    	            		Element identifier = elementIterator.next();
		            			String handleID = identifier.getText();
		            			handleID = handleID.replaceAll("http://hdl.handle.net/", "");
		    					CreateHandle handle = new CreateHandle();
		    					handleID = handle.createHandle(pid, handleID);
	    	            	}*/
	    	            	
	            			Element dc = (Element) node.selectSingleNode("foxml:datastreamVersion/foxml:xmlContent/dc");
	    					dc.addNamespace("dcterms","http://purl.org/dc/terms/" );

 	            			Element uuid = dc.addElement("dcterms:identifier");
  	            			uuid.addAttribute("xsi:type", "eraterms:local");
  	            			uuid.addText(pid);
	    					
	    					node = (Node) dc;
	            			Element identifier = (Element) node.selectSingleNode("dcterms:identifier");
	            			String handleID = identifier.getText();
	            			handleID = handleID.replaceAll("http://hdl.handle.net/", "");
	            			
/*	    					CreateHandle handle = new CreateHandle();
	    					handleID = handle.createHandle(pid, handleID);*/
	            			
/*  	            		Element newIdentifier = identifier.addElement("identifier");
  	            			newIdentifier.addText(handleID);*/
	            		}
	            	}
            		
			        objXML = document.asXML();
				} catch (IOException e) {
					log.error("Could not create file input stream!");
					throw new FedoraAPIException("Could not create file input stream!", e);
				} catch (DocumentException e) {
					log.error("Could not create XML document!");
					throw new FedoraAPIException("Could not create XML document!", e);
				}

	
//				ByteArrayOutputStream out=new ByteArrayOutputStream();
//				FileHandler.pipeStream(inStream, out, 4096);
//				String objXML = out.toString();
				
				log.debug("Ingesting PID: " + pid);
				try {
					DataHandler dataHandler = null;
					DataSource ds = new ByteArrayDataSource(objXML.getBytes(), "text/plain; charset=UTF-8");
					dataHandler = new DataHandler(ds); 
					pid = apim.ingest(dataHandler, FOXML1_1.uri, "Ingest Objects");
					log.debug("PID: " + pid);
					log.info("Fedora object created: " + pid);
				}
				catch (Exception e) {
					log.error(e.getMessage());
					throw new FedoraAPIException(e.getMessage());
				}
			}
//		}
		
	}
	
	public String getNextPID(String ns) throws FedoraAPIException {
		List<String> pids = null;
		String pid = null;
		try {
			pids = apim.getNextPID(new NonNegativeInteger("1"), ns);
			pid = pids.iterator().next(); 
			log.debug("Next PID: " + pid);
		} catch (Exception e) {
			AxisFault axisFault = (AxisFault) e;
			log.error(axisFault.getFaultReason());
			log.error("Could not get next PID!", e);
			throw new FedoraAPIException("Could not get net PID!", e);
		}
		return pid;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ca.ualberta.library.ir.dao.FedoraAPIMDao#addDatastream(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public String addDatastream(String pid, String label, String mimeType, InputStream data, String message)
		throws FedoraAPIException {
		return addDatastream(pid, null, label, mimeType, data, message);
	}

	public String addDatastream(String pid, String dsId, ArrayOfString altIds, String label, String mimeType, String formatUri, File file, String controlGroup, 
			                    String dsState, String checksumType, String checksum, String message)
		throws FedoraAPIException, AxisFault, Exception {
		try {
			String uploadId = uploader.upload(file);
			log.debug("uploadId: " + uploadId);
			dsId = apim.addDatastream(pid, dsId, altIds, label, true, mimeType, formatUri, uploadId, controlGroup, dsState,
					null, null, message);
			log.debug("dsId: " + dsId);
		} catch (Exception e) {
			AxisFault axisFault = (AxisFault) e;
			log.error(axisFault.getFaultReason());
			throw new FedoraAPIException(axisFault.getFaultReason());
		}
		return dsId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ca.ualberta.library.ir.dao.FedoraAPIMDao#ingest(ca.ualberta.library.ir.domain.Properties, java.lang.String)
	 */
	public String ingest(Properties properties, String message) throws FedoraAPIException {
		return ingest(pidNamespace, properties.getState(), properties.getLabel(), properties.getContentModel(),
			properties.getOwnerId(), message);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ca.ualberta.library.ir.dao.FedoraAPIMDao#addDatastream(ca.ualberta.library.ir.domain.Datastream,
	 *      java.lang.String)
	 */
	public String addDatastream(ca.ualberta.library.fedora.domain.Datastream datastream, String message)
		throws FedoraAPIException {
		try {
			return addDatastream(datastream.getPid(), datastream.getLabel(), datastream.getMimeType(), datastream
				.getData(), message);
		} catch (Exception e) {
			log.error("Could not add datastream!", e);
			throw new FedoraAPIException("Could not add datastream!", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ca.ualberta.library.ir.dao.FedoraAPIMDao#modifyObject(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public String modifyObject(String pid, String state, String label, String ownerId, String message)
		throws FedoraAPIException {
		try {
			return apim.modifyObject(pid, state, label, ownerId, message);
		} catch (Exception e) {
			log.error("Could not modify the object!", e);
			throw new FedoraAPIException("Could not modify the object!", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ca.ualberta.library.ir.dao.FedoraAPIMDao#modifyDatastreamByValue(java.lang.String, java.lang.String,
	 *      java.lang.String[], java.lang.String, java.lang.String, java.lang.String, byte[], java.lang.String,
	 *      java.lang.String, java.lang.String, boolean)
	 */
	public String modifyDatastreamByValue(String pid, String dsId, ArrayOfString altIds, String label, String mimeType,
		String formatUri, byte[] content, String checksumType, String checksum, String message, boolean force)
		throws FedoraAPIException {
		try {
			DataHandler dataHandler = null;
			DataSource ds = new ByteArrayDataSource(content, "text/plain; charset=UTF-8");
			dataHandler = new DataHandler(ds); 
			return apim.modifyDatastreamByValue(pid, dsId, altIds, label, mimeType, formatUri, dataHandler, checksumType,
				checksum, message, force);
		} catch (Exception e) {
			log.error("Could not modify the object!", e);
			throw new FedoraAPIException("Could not modify the object!", e);
		}
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see ca.ualberta.library.ir.dao.FedoraAPIMDao#modifyObject(ca.ualberta.library.ir.domain.Properties,
	 *      java.lang.String)
	 */
	public String modifyObject(Properties properties, String message) throws FedoraAPIException {
		return modifyObject(properties.getPid(), properties.getState(), properties.getLabel(), null, message);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ca.ualberta.library.ir.dao.FedoraAPIMDao#modifyDatastreamByReference(java.lang.String, java.lang.String,
	 *      java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public String modifyDatastreamByReference(String pid, String dsId, ArrayOfString altIds, String label, String mimeType,
		String formatUri, InputStream data, String checksumType, String checksum, String message, boolean force)
		throws FedoraAPIException {
		try {
			String uploadId = null;
			if (data != null) {
				uploadId = uploader.upload(data);
				log.debug("uploadId: " + uploadId);
			}
			return apim.modifyDatastreamByReference(pid, dsId, altIds, label, mimeType, formatUri, uploadId,
				checksumType, checksum, message, force);
		} catch (Exception e) {
			log.error("Could not modify the object!", e);
			throw new FedoraAPIException("Could not modify the object!", e);
		}
	}

	public String modifyDatastream(String pid, String dsId, ArrayOfString altIds, String label, String mimeType,
			String formatUri, File file, String checksumType, String checksum, String message, boolean force)
			throws FedoraAPIException {
			try {
				FileInputStream input = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(input);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				for (int b = bis.read(); b != -1; b = bis.read()) {
					bos.write(b);
				}
				bos.flush();
				bos.close();
				bis.close();
				
				DataHandler dataHandler = null;
				DataSource ds = new ByteArrayDataSource(bos.toByteArray(), "text/plain; charset=UTF-8");
				dataHandler = new DataHandler(ds); 
				return apim.modifyDatastreamByValue(pid, dsId, altIds, label, mimeType, formatUri, dataHandler,
					checksumType, checksum, message, force);
			} catch (Exception e) {
				log.error("Could not modify the object!", e);
				throw new FedoraAPIException("Could not modify the object!", e);
			}
		}

	/*
	 * (non-Javadoc)
	 * @see ca.ualberta.library.ir.dao.FedoraAPIMDao#modifyDublicCore(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public String modifyDublinCore(String pid, String label, String dcString, String message) throws FedoraAPIException {
		try {
			DataHandler dataHandler = null;
			DataSource ds = new ByteArrayDataSource(dcString.getBytes("UTF-8"), "text/plain; charset=UTF-8");
			dataHandler = new DataHandler(ds); 
			return apim.modifyDatastreamByValue(pid, "DC", null, label, null, null, dataHandler, null,
				null, message, false);
		} catch (Exception e) {
			log.error("Could not modify the Dublin Core!", e);
			throw new FedoraAPIException("Could not modify the Dublin Core!", e);
		}
	}

	private DataHandler buildObjectRelationshipsContent(String pid, String relationship, List<String> pids) {
		StringBuffer content = new StringBuffer();
		content
			.append(
				"<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">\n")
			.append("    <rdf:Description rdf:about=\"info:fedora/").append(pid).append("\">\n");
		if (pids != null) {
			for (String mpid : pids) {
				content.append("    <rel:" + relationship + " rdf:resource=\"info:fedora/").append(mpid).append(
					"\"/>\n");
			}
		}
		content.append("    </rdf:Description>\n").append("</rdf:RDF>");
		log.debug(content.toString());
		
		DataHandler dataHandler = null;
		DataSource ds = new ByteArrayDataSource(content.toString().getBytes(), "text/plain; charset=UTF-8");
		dataHandler = new DataHandler(ds);
		
		return dataHandler;
	}

	private DataHandler buildMemberOfRelationshipsContent(String pid, List<String> memberOfPids) {
		StringBuffer content = new StringBuffer();
		content
			.append(
				"<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">\n")
			.append("    <rdf:Description rdf:about=\"info:fedora/").append(pid).append("\">\n");
		if (memberOfPids != null) {
			for (String mpid : memberOfPids) {
				content.append("    <rel:isMemberOf rdf:resource=\"info:fedora/").append(mpid).append("\"/>\n");
			}
		}
		content.append("    </rdf:Description>\n").append("</rdf:RDF>");
		log.debug(content.toString());
		
		DataHandler dataHandler = null;
		DataSource ds = new ByteArrayDataSource(content.toString().getBytes(), "text/plain; charset=UTF-8");
		dataHandler = new DataHandler(ds);
		
		return dataHandler;
	}


	private byte[] buildPartOfRelationshipsContent(String pid, List<String> memberOfPids) {
		StringBuffer content = new StringBuffer();
		content
			.append(
				"<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">\n")
			.append("    <rdf:Description rdf:about=\"info:fedora/").append(pid).append("\">\n");
		if (memberOfPids != null) {
			for (String mpid : memberOfPids) {
				content.append("    <rel:isPartOf rdf:resource=\"info:fedora/").append(mpid).append("\"/>\n");
			}
		}
		content.append("    </rdf:Description>\n").append("</rdf:RDF>");
		log.debug(content.toString());
		return content.toString().getBytes();
	}

	public void modifyMemberOfRelationships(String pid, String label, List<String> memberOfPids, String message)
		throws FedoraAPIException {
		try {
			apim.modifyDatastreamByValue(pid, RELS_EXT_ID, null, label, "text/xml", null,
				buildMemberOfRelationshipsContent(pid, memberOfPids), null, null, message, false);
		} catch (Exception e) {
			log.error("Could not add relationship datastream!", e);
			throw new FedoraAPIException("Could not modify relationship datastream!", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ca.ualberta.library.ir.dao.RepositoryAPIMDao#modifyObjectRelationships(java.lang.String, java.lang.String,
	 *      java.lang.String, java.util.List, java.lang.String)
	 */
	public void modifyObjectRelationships(String pid, String label, String relationship, List<String> pids,
		String message) throws FedoraAPIException {
		try {
			apim.modifyDatastreamByValue(pid, RELS_EXT_ID, null, label, "text/xml", null,
				buildObjectRelationshipsContent(pid, relationship, pids), null, null, message, false);
		} catch (Exception e) {
			log.error("Could not add relationship datastream!", e);
			throw new FedoraAPIException("Could not modify relationship datastream!", e);
		}
	}

	public void modifyObjectRelationships(String pid, String label, List<String> memberOfPids,
		List<String> memberOfCollectionPids, List<String> partOfPids, String message) throws FedoraAPIException {
		try {
			apim.modifyDatastreamByValue(pid, RELS_EXT_ID, null, label, "text/xml", null, buildObjectRelationships(pid,
				memberOfPids, memberOfCollectionPids, partOfPids), null, null, message, false);
		} catch (Exception e) {
			log.error("Could not add relationship datastream!", e);
			throw new FedoraAPIException("Could not modify relationships datastream!", e);
		}
	}

	/**
	 * The buildObjectRelationships method.
	 * @param pid
	 * @param memberOfPids
	 * @param memberOfCollectionPids
	 * @param partOfPids
	 * @return
	 */
	private DataHandler buildObjectRelationships(String pid, List<String> memberOfPids, List<String> memberOfCollectionPids,
		List<String> partOfPids) {
		StringBuilder content = new StringBuilder();
		content
			.append(
				"<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">\n")
			.append("    <rdf:Description rdf:about=\"info:fedora/").append(pid).append("\">\n");
		if (memberOfPids != null) {
			for (String objPid : memberOfPids) {
				content.append("    <rel:").append(Relationship.IS_MEMBER_OF.getId()).append(
					" rdf:resource=\"info:fedora/").append(objPid).append("\"/>\n");
			}
		}
		if (memberOfCollectionPids != null) {
			for (String objPid : memberOfCollectionPids) {
				content.append("    <rel:").append(Relationship.IS_MEMBER_OF_COLLECTION.getId()).append(
					" rdf:resource=\"info:fedora/").append(objPid).append("\"/>\n");
			}
		}
		if (partOfPids != null) {
			for (String objPid : partOfPids) {
				content.append("    <rel:").append(Relationship.IS_PART_OF.getId()).append(
					" rdf:resource=\"info:fedora/").append(objPid).append("\"/>\n");
			}
		}
		content.append("    </rdf:Description>\n").append("</rdf:RDF>");
		log.debug(content.toString());
		
		DataHandler dataHandler = null;
		DataSource ds = new ByteArrayDataSource(content.toString().getBytes(), "text/plain; charset=UTF-8");
		dataHandler = new DataHandler(ds);
		
		return dataHandler;
	}

	/*
	 * (non-Javadoc)
	 * @see ca.ualberta.library.ir.dao.RepositoryAPIMDao#purgeDatastream(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public List<String> purgeDatastream(String pid, String dsId, String message) throws FedoraAPIException {
		try {
			return apim.purgeDatastream(pid, dsId, null, null, message, false);
		} catch (Exception e) {
			log.error("Could not purge this datastream!", e);
			throw new FedoraAPIException("Could not purge this datastream!", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ca.ualberta.library.ir.dao.RepositoryAPIMDao#purgeObject(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public String purgeObject(String pid, String message) throws FedoraAPIException {
		try {
			return apim.purgeObject(pid, message, false);
		} catch (Exception e) {
			log.error("Could not purge this datastream!", e);
			throw new FedoraAPIException("Could not purge this datastream!", e);
		}
	}

	/**
	 * The getPidNamespace getter method.
	 * @return the pidNamespace
	 */
	public String getPidNamespace() {
		return pidNamespace;
	}

	/**
	 * The setPidNamespace setter method.
	 * @param pidNamespace the pidNamespace to set
	 */
	public void setPidNamespace(String pidNamespace) {
		this.pidNamespace = pidNamespace;
	}

	/**
	 * The getRestServicePath getter method.
	 * @return the restServicePath
	 */
	@Override
	public String getRestServicePath() {
		return restServicePath;
	}

	/**
	 * The setRestServicePath setter method.
	 * @param restServicePath the restServicePath to set
	 */
	@Override
	public void setRestServicePath(String restServicePath) {
		this.restServicePath = restServicePath;
	}

	@Override
	public String modifyDatastreamByValue(String pid, String dsId,
			String[] altIds, String label, String mimeType, String formatUri,
			byte[] content, String checksumType, String checksum,
			String message, boolean force) throws FedoraAPIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String modifyDatastreamByReference(String pid, String dsId,
			String[] altIds, String label, String mimeType, String formatUri,
			InputStream data, String checksumType, String checksum,
			String message, boolean force) throws FedoraAPIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addObjectRelationships(String pid, String label,
			List<String> memberOfPids, List<String> memberOfCollectionPids,
			List<String> partOfPids, String message) throws FedoraAPIException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String addDatastream(String pid, String dsId, String label,
			String mimeType, InputStream data, String message)
			throws FedoraAPIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String modifyDatastreamByValue(
			ca.ualberta.library.fedora.domain.Datastream datastream,
			String message) throws FedoraAPIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String modifyDatastreamByReference(
			ca.ualberta.library.fedora.domain.Datastream datastream,
			String message) throws FedoraAPIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addObjectRelationships(String pid, String label,
			String relationship, List<String> pids, String message)
			throws FedoraAPIException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMemberOfRelationships(String pid, String label,
			List<String> memberOfPids, String message)
			throws FedoraAPIException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPartOfRelationships(String pid, String label,
			List<String> memberOfPids, String message)
			throws FedoraAPIException {
		// TODO Auto-generated method stub
		
	}
}
