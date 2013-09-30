/**
 * University of Alberta Libraries
 * Information Technology and Services
 * Project: fedora
 * $Id: RepositoryAPIMDao.java 3587 2009-01-21 17:31:58Z pcharoen $
 */
package ca.ualberta.library.fedora.dao;

import java.io.InputStream;
import java.util.List;

//import org.openarchives.oai.x20.oaiDc.DcDocument;

import ca.ualberta.library.fedora.domain.Properties;
import ca.ualberta.library.fedora.exception.FedoraAPIException;
import org.fcrepo.server.types.gen.Datastream;

/**
 * The FedoraAPIMDao class.
 *
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 3587 $ $Date: 2009-01-21 10:31:58 -0700 (Wed, 21 Jan 2009) $
 */
public interface APIMDao extends BaseDao {

	public void addObjectRelationships(String pid, String label, List<String> memberOfPids,
		List<String> memberOfCollectionPids, List<String> partOfPids, String message) throws FedoraAPIException;

	public void modifyObjectRelationships(String pid, String label, List<String> memberOfPids,
		List<String> memberOfCollectionPids, List<String> partOfPids, String message) throws FedoraAPIException;

	public InputStream getDatastreamContent(String pid, String dsId) throws FedoraAPIException;

	public String getNextPID(String ns) throws FedoraAPIException;

	public String ingest(Properties properties, String message) throws FedoraAPIException;

	public String ingest(String ns, String state, String label, String cModel, String ownerId, String message)
		throws FedoraAPIException;

	public String addDatastream(ca.ualberta.library.fedora.domain.Datastream datastream, String message)
		throws FedoraAPIException;

	public String addDatastream(String pid, String label, String mimeType, InputStream data, String message)
		throws FedoraAPIException;

	public String addDatastream(String pid, String dsId, String label, String mimeType, InputStream data, String message)
		throws FedoraAPIException;

	public List<org.fcrepo.server.types.gen.Datastream> getDatastreams(String pid) throws FedoraAPIException;

	public org.fcrepo.server.types.gen.Datastream getDatastream(String pid, String dsId) throws FedoraAPIException;

//	public DcDocument getDublinCore(String pid) throws FedoraAPIException;

	public String modifyObject(String pid, String state, String label, String ownerId, String message)
		throws FedoraAPIException;

	public String modifyDatastreamByValue(String pid, String dsId, String[] altIds, String label, String mimeType,
		String formatUri, byte[] content, String checksumType, String checksum, String message, boolean force)
		throws FedoraAPIException;

	public String modifyDatastreamByValue(ca.ualberta.library.fedora.domain.Datastream datastream, String message)
		throws FedoraAPIException;

	public String modifyDatastreamByReference(String pid, String dsId, String[] altIds, String label, String mimeType,
		String formatUri, InputStream data, String checksumType, String checksum, String message, boolean force)
		throws FedoraAPIException;

	public String modifyDatastreamByReference(ca.ualberta.library.fedora.domain.Datastream datastream, String message)
		throws FedoraAPIException;

	public String modifyDublinCore(String pid, String label, String dcString, String message) throws FedoraAPIException;

	public String modifyObject(Properties properties, String message) throws FedoraAPIException;

	public void addObjectRelationships(String pid, String label, String relationship, List<String> pids, String message)
		throws FedoraAPIException;

	public void addMemberOfRelationships(String pid, String label, List<String> memberOfPids, String message)
		throws FedoraAPIException;

	public void addPartOfRelationships(String pid, String label, List<String> memberOfPids, String message)
		throws FedoraAPIException;

	public void modifyMemberOfRelationships(String pid, String label, List<String> memberOfPids, String message)
		throws FedoraAPIException;

	public void modifyObjectRelationships(String pid, String label, String relationship, List<String> pids,
		String message) throws FedoraAPIException;

	public List<String> purgeDatastream(String pid, String dsId, String message) throws FedoraAPIException;

	public String purgeObject(String pid, String message) throws FedoraAPIException;
}
