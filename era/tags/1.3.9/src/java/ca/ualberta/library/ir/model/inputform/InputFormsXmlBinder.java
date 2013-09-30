/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: InputFormsXmlBinder.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import ca.ualberta.library.ir.servlet.ApplicationContextListener;

/**
 * The InputFormsUnmarshaller class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class InputFormsXmlBinder {
	private static final Log log = LogFactory.getLog(InputFormsXmlBinder.class);

	private static final File inputFormsXml = new File(InputFormsXmlBinder.class.getResource("/input-forms.xml")
		.getPath());
	private static final Schema schema = getSchema("schema1.xsd");

	public static InputForms unmarshal() throws Exception {
		// log.trace("unmarshalling: " + inputFormsXml.getPath() + "...");
		Unmarshaller um = ApplicationContextListener.inputFormsContext.createUnmarshaller();
		um.setSchema(schema);
		InputForms inputForms = (InputForms) um.unmarshal(inputFormsXml);
		return inputForms;
	}

	public static byte[] marshal(final InputForms inputForms) throws Exception {
		// log.trace("marshalling InputForms object...");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Marshaller m = ApplicationContextListener.inputFormsContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		m.marshal(inputForms, bos);
		return bos.toByteArray();
	}

	public static void write(byte[] bytes) throws Exception {
		// log.trace("writing to file: " + inputFormsXml.getPath() + "...");
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		FileOutputStream fos = new FileOutputStream(inputFormsXml);
		IOUtils.copy(bis, fos);
		fos.flush();
		fos.close();
		bis.close();
	}

	public static final Schema getSchema(String schemaResourceName)/* throws SAXException */{
		SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
		try {
			URL schemaURL = InputFormsXmlBinder.class.getResource(schemaResourceName);
			return sf.newSchema(schemaURL);
		} catch (SAXException se) {
			log.error("Could not get schema!", se);
			return null;
		}
	}
}
