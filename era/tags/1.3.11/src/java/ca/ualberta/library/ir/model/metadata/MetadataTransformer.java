/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: MetadataTransformer.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.metadata;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;
import org.xml.sax.SAXException;

import ca.ualberta.library.ir.servlet.ApplicationContextListener;

/**
 * The ModelTransformer class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class MetadataTransformer {
	private static final Log log = LogFactory.getLog(MetadataTransformer.class);

	private static Templates dcqTranslet;
	private static Templates metadataTranslet;
	private static Schema schema = getSchema("schema1.xsd");
	static {
		try {

			// create Dcq2Metadata translet (expicitly instantiate using xalan api)
			TransformerFactory dcqFactory = new TransformerFactoryImpl();
			dcqFactory.setAttribute("use-classpath", true);
			dcqFactory.setAttribute("package-name", "ca.ualberta.library.era.translet");
			dcqTranslet = dcqFactory.newTemplates(new StreamSource("Dcq2Metadata"));

			// create Metadata2Dcq translet (expicitly instantiate using xalan api)
			TransformerFactory metadataFactory = new TransformerFactoryImpl();
			metadataFactory.setAttribute("use-classpath", true);
			metadataFactory.setAttribute("package-name", "ca.ualberta.library.era.translet");
			metadataTranslet = metadataFactory.newTemplates(new StreamSource("Metadata2Dcq"));

		} catch (TransformerConfigurationException e) {
			log.error("Could not create translet!", e);
		}
	}

	public static Metadata datastream2metadata(final InputStream in) throws Exception {
		// log.trace("transforming datastream2metadata...");

		// transform Datastram to Metadata XML
		ByteArrayOutputStream mos = new ByteArrayOutputStream();
		StreamSource source = new StreamSource(in);
		Transformer transformer = dcqTranslet.newTransformer();
		transformer.transform(source, new StreamResult(mos));

		// unmarshal Metadata XML to Metadata Object
		Unmarshaller um = ApplicationContextListener.metadataContext.createUnmarshaller();
		// um.setSchema(getSchema("schema1.xsd"));
		um.setSchema(schema);
		// log.debug("metadata: " + mos);
		Metadata metadata = (Metadata) um.unmarshal(IOUtils.toInputStream(mos.toString()));
		return metadata;
	}

	public static byte[] metadata2datastream(final Metadata metadata) throws Exception {
		// log.trace("transforming metadata2dcq...");

		// marshal Metadata Object to Metadata XML
		ByteArrayOutputStream mos = new ByteArrayOutputStream();
		Marshaller m = ApplicationContextListener.metadataContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		m.marshal(metadata, mos);

		// transform Metadata XML to Datastream
		ByteArrayOutputStream dos = new ByteArrayOutputStream();
		Transformer transformer = metadataTranslet.newTransformer();
		transformer.transform(new StreamSource(IOUtils.toInputStream(mos.toString())), new StreamResult(dos));
		return dos.toByteArray();
	}

	public static Schema getSchema(String schemaResourceName) /* throws SAXException */{
		SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
		try {
			URL schemaURL = MetadataTransformer.class.getResource(schemaResourceName);
			return sf.newSchema(schemaURL);
		} catch (SAXException se) {
			// throw se;
			log.error("Could not get JAXB schema!", se);
			return null;
		}
	}
}