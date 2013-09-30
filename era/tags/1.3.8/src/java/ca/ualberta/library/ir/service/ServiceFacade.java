/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ServiceFacade.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import net.handle.hdllib.AbstractResponse;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.velocity.app.VelocityEngine;
import org.openarchives.oai.x20.oaiDc.DcDocument;

import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;
import fedora.server.types.gen.ObjectProfile;
import fedora.server.types.gen.RelationshipTuple;

import ca.ualberta.library.ir.domain.Author;
import ca.ualberta.library.ir.domain.AuthorProfile;
import ca.ualberta.library.ir.domain.Bookmark;
import ca.ualberta.library.ir.domain.Download;
import ca.ualberta.library.ir.domain.Favorite;
import ca.ualberta.library.ir.domain.Group;
import ca.ualberta.library.ir.domain.GroupPermission;
import ca.ualberta.library.ir.domain.Handle;
import ca.ualberta.library.ir.domain.License;
import ca.ualberta.library.ir.domain.Message;
import ca.ualberta.library.ir.domain.Proquest;
import ca.ualberta.library.ir.domain.Register;
import ca.ualberta.library.ir.domain.Scheduler;
import ca.ualberta.library.ir.domain.Subscription;
import ca.ualberta.library.ir.domain.Tag;
import ca.ualberta.library.ir.domain.Transaction;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.domain.UserPermission;
import ca.ualberta.library.ir.exception.FedoraAPIException;
import ca.ualberta.library.ir.exception.HandleClientException;
import ca.ualberta.library.ir.exception.ServiceException;
import ca.ualberta.library.ir.exception.UnauthorizedException;
import ca.ualberta.library.ir.facebook.FacebookService;
import ca.ualberta.library.ir.mail.MailServiceManager;
import ca.ualberta.library.ir.model.fedora.ContentModel;
import ca.ualberta.library.ir.model.fedora.Item;
import ca.ualberta.library.ir.model.fedora.Properties;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.thumbnail.ThumbnailGenerator;

/**
 * The ServiceFacade class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public interface ServiceFacade {

	public void setServletContext(ServletContext servletContext);

	public ServletContext getServletContext();

	public String select(String query) throws SolrServerException;

	public String query(String q, String fq, String sort, String order, int start, int rows) throws SolrServerException;

	public List<Community> getCommunitiesByFormName(String formName) throws SolrServerException;

	public byte[] findItemXml(String pid) throws SolrServerException;

	public QueryResponse getSubmittedDepartments() throws SolrServerException, IOException;

	public List<Message> getMessageHistory();

	public List<Message> getAllMessages();

	public String ingest(String ns, String state, String label, String ownerId, String message)
		throws FedoraAPIException;

	public boolean isUserPermissionAllowed(User user, String... roles) throws UnauthorizedException;

	public boolean isGroupPermissionAllowed(Group group, String... roles);

	public Message getMessage(int id);

	public void saveOrUpdateMessage(Message message);

	public List<Message> getApplicationMessages();

	public java.util.List<Download> getAllDownloads();

	public long getDownloadCountByCollection(String pid);

	public QueryResponse getItemCount() throws SolrServerException, IOException;

	public long getDownloadCount();

	public QueryResponse findManualApprovalItemsByTitleAuthor(String q, int start, int rows) throws SolrServerException;

	public QueryResponse findCollectionsByName(int start, int rows, String name) throws SolrServerException;

	public QueryResponse findCommunitiesByName(int start, int rows, String name) throws SolrServerException;

	public QueryResponse findManualApprovalItems(int start, int rows, String sort, String state, String department)
		throws SolrServerException;

	public QueryResponse findEmbargoedItemsBySubscription(int start, int rows, String sort, User user)
		throws SolrServerException;

	public int deleteFavoriteByPid(String pid);

	public int deleteBookmarkByPid(String pid);

	public QueryResponse getRandomPids(int n) throws SolrServerException;

	public int getNoOfTransactions(String pid);

	public InputStream getDatastreamContent(String pid, String dsId, String createdDate) throws FedoraAPIException;

	public void updateTransaction(Item item, User user) throws ServiceException;

	public void updateTransaction(ca.ualberta.library.ir.model.solr.Item item, User user) throws ServiceException;

	public Datastream[] getDatastreamHistory(String pid, String dsId) throws FedoraAPIException;

	public void saveOrUpdateTransaction(Transaction transaction);

	public List<Transaction> getTransactionsByPid(String pid);

	public void purgeTransactionsByDate(Date date);

	public Handle getRandomHandle();

	public FacebookService getFacebookService();

	public byte[] getObjectXML(String pid) throws FedoraAPIException;

	public void addBookmarkIndex(Bookmark bookmark) throws SolrServerException, IOException;

	public void addBookmarkIndex(Bookmark bookmark, boolean commit) throws SolrServerException, IOException;

	public void addContentModel(String pid, String object) throws FedoraAPIException;

	public String addDatastream(ca.ualberta.library.ir.model.fedora.Datastream datastream, String messsage)
		throws FedoraAPIException;

	public String addDatastream(String pid, String dsId, String label, byte[] xmlData, String message)
		throws FedoraAPIException;

	public String addDatastream(String pid, String label, String mimeType, InputStream data, String message)
		throws FedoraAPIException;

	public String addDatastream(String pid, String dsId, String label, String mimeType, InputStream data, String message)
		throws FedoraAPIException;

	public String addDatastream(String pid, String dsId, String label, String mimeType, String url, String message)
		throws FedoraAPIException;

	public void addFavoriteIndex(Favorite favorite) throws SolrServerException, IOException;

	public void addFavoriteIndex(Favorite favorite, boolean commit) throws SolrServerException, IOException;

	public void addObjectRelationships(String pid, String label, List<String> memberOfPids,
		List<String> memberOfCollectionPids, List<String> partOfPids, String message) throws FedoraAPIException;

	public void addObjectRelationships(String pid, String label, String relationship, List<String> pids, String message)
		throws FedoraAPIException;

	boolean addRelationship(java.lang.String pid, java.lang.String relationship, java.lang.String object,
		boolean isLiteral, java.lang.String datatype) throws FedoraAPIException;

	public void addSubscriptionIndex(Subscription subscription) throws SolrServerException, IOException;

	public void addSubscriptionIndex(Subscription subscription, boolean commit) throws SolrServerException, IOException;

	public void authenticate(String uid, String pwd) throws NamingException, AuthenticationException;

	public QueryResponse browse(String fields, int limit) throws SolrServerException;

	public QueryResponse browseAlphabet() throws SolrServerException;

	public QueryResponse browseCollection(String pid, int limit) throws SolrServerException;

	public QueryResponse browseCollectionByAlphabet(String pid) throws SolrServerException;

	public QueryResponse browseCommunity(int limit) throws SolrServerException;

	public QueryResponse browseCommunityByAlphabet() throws SolrServerException;

	public QueryResponse browseCommunityItem(int limit) throws SolrServerException;

	public QueryResponse browseCommunityItemCount(String pid, int limit) throws SolrServerException;

	public QueryResponse browseInitial(String field, String prefix, int limit, int offset) throws SolrServerException;

	public void commit() throws IOException, SolrServerException;

	public void commit(boolean background) throws IOException, SolrServerException;

	public void createHandle(String url, String handle) throws HandleClientException;

	public void deleteBookmark(int id);

	public void deleteBookmarkIndex(Bookmark bookmark) throws SolrServerException, IOException;

	public void deleteByQuery(String query) throws SolrServerException, IOException;

	public void deleteFavorite(int id);

	public void deleteFavoriteIndex(Favorite favorite) throws SolrServerException, IOException;

	public void deleteHandle(int id);

	public void deleteHandle(String handle) throws HandleClientException;

	public int deleteHandleByPid(String pid);

	public void deleteIndexByNamespace(String ns) throws SolrServerException, IOException;

	public void deleteRegister(int id);

	public void deleteSubscription(int id);

	public void deleteSubscription(Subscription subscription) throws SolrServerException, IOException;

	public int deleteSubscriptionByPid(String pid);

	public void deleteTags(Set<Tag> tags);

	public void destroy();

	public QueryResponse findAllCollections(int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findAllCommunities(int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findCollectionNewItems(String pid, int rows) throws SolrServerException;

	public QueryResponse findCollections(String pid, int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findCommunities(int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findCommunityNewItems(String pid, int rows) throws SolrServerException;

	public QueryResponse findDarkRepositoryItems(int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findEmbargoedEndingItems() throws SolrServerException;

	public QueryResponse findEmbargoedItems(int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findItemsByState(String state, int start, int rows, String sort) throws SolrServerException;

	public User findLdapUser(String uid) throws NamingException;

	public QueryResponse findManualApprovalItems(int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findMemberObjects(String pid, String relationship) throws SolrServerException;

	public QueryResponse findMemberObjects(String pid, String relationship, int limit) throws SolrServerException;

	public QueryResponse findMyBookmarks(String userId, int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findMyCollections(String userId, int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findMyCommunities(String userId, int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findMyFavorites(String userId, int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findMyItems(String userId, int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findMyItemsByState(String userId, String state, int start, int rows, String sort)
		throws SolrServerException;

	public QueryResponse findMyPendingItems(String userId, int start, int rows, String sort) throws SolrServerException;

	public QueryResponse findMySavedItems(String username, int start, int rows, String sortBy)
		throws SolrServerException;

	public QueryResponse findMySubscriptions(String userId, int start, int rows, String sort)
		throws SolrServerException;

	public QueryResponse findNewCollections(int rows) throws SolrServerException;

	public QueryResponse findNewCommunities(int rows) throws SolrServerException;

	public QueryResponse findNewItems(int rows) throws SolrServerException;

	public QueryResponse findNewSubscriptions(String userId, int rows) throws SolrServerException;

	public QueryResponse findNewSubscriptionsByDate(String ownerId, int rows, Date date, Boolean notify)
		throws SolrServerException;

	public FieldSearchResult findNextResult(String sessionToken) throws FedoraAPIException;

	public QueryResponse findObject(String pid, String contentModel) throws SolrServerException;

	public QueryResponse findObjectByPid(String pid) throws SolrServerException;

	public ObjectFields findObjectByPid(String[] resultFields, String pid) throws FedoraAPIException;

	public ObjectFields[] findObjects(String[] resultFields, int maxResults, String query) throws FedoraAPIException;

	public ObjectFields[] findObjectsByContentModel(String[] resultFields, String contentModel)
		throws FedoraAPIException;

	public ObjectFields[] findObjectsByContentModelOwnerId(String[] resultFields, String contentModel, String ownerId)
		throws FedoraAPIException;

	public ObjectFields[] findObjectsByOwnerId(String[] resultFields, String ownerId) throws FedoraAPIException;

	public QueryResponse findObjectsByPids(List<String> pids) throws SolrServerException;

	public FieldSearchResult findObjectsByTerms(String[] resultFields, int maxResults, String query)
		throws FedoraAPIException;

	public QueryResponse findReviewItems(String username, int start, int rows, String sort) throws SolrServerException;

	public List<User> findUsers(String name, int start, int rows);

	public File generateThumbnail(String contentType, int thumbWidth, int thumbHeight, final File input)
		throws Exception;

	public List<Author> getAllAuthors();

	public List<Bookmark> getAllBookmarks();

	public List<Collection> getAllCollections() throws SolrServerException;

	public QueryResponse getAllCollectionsCount() throws SolrServerException, IOException;

	public List<Community> getAllCommunities() throws SolrServerException;

	public QueryResponse getAllCommunitiesCount() throws SolrServerException, IOException;

	public List<ContentModel> getAllContentModels();

	public List<Favorite> getAllFavorites();

	public List<Group> getAllGroups();

	public List<License> getAllLicenses();

	public QueryResponse getAllPids() throws SolrServerException;

	public List<Subscription> getAllSubscriptions();

	public List<Count> getAllTypes() throws SolrServerException;

	public Author getAuthor(int id);

	public Bookmark getBookmark(int id);

	public QueryResponse getBookmarkCountByOwnerId(String ownerId) throws SolrServerException, IOException;

	public int getBookmarkCountByPid(String pid);

	public QueryResponse getBookmarkUserStats(String filter) throws SolrServerException, IOException;

	public Collection getCollection(String pid) throws ServiceException;

	public QueryResponse getCollectionCountByOwnerId(String ownerId) throws SolrServerException, IOException;

	public QueryResponse getCollectionsByPids(List<String> pids) throws SolrServerException;

	public List<Collection> getCollectionsByUserId(String userId) throws FedoraAPIException;

	public QueryResponse getCommunitiesByPids(List<String> pids) throws SolrServerException;

	public List<Community> getCommunitiesByUserId(String userId) throws FedoraAPIException;

	public Community getCommunity(String pid) throws ServiceException;

	public QueryResponse getCommunityCountByOwnerId(String ownerId) throws SolrServerException, IOException;

	public List<Collection> getCommunityMemberCollections(String pid) throws ServiceException;

	public List<ca.ualberta.library.ir.model.solr.ContentModel> getContentModelsByType(String type)
		throws SolrServerException;

	public QueryResponse getDarkItemCount() throws SolrServerException, IOException;

	public Datastream getDatastream(String pid, String dsId) throws FedoraAPIException;

	public InputStream getDatastreamContent(String pid, String dsId) throws FedoraAPIException;

	public Datastream[] getDatastreams(String pid) throws FedoraAPIException;

	public long getDownloadCountByDsId(String pid, String dsId);

	public long getDownloadCountByPid(String pid);

	public long getDownloadCountByUserId(int userId);

	public DcDocument getDublinCore(String pid) throws FedoraAPIException;

	public QueryResponse getEmbargoedItemCount() throws SolrServerException, IOException;

	public Favorite getFavorite(int id);

	public Favorite getFavoriteByPid(String pid, int userId);

	public QueryResponse getFavoriteCountByOwnerId(String ownerId) throws SolrServerException, IOException;

	public int getFavoriteCountByPid(String pid);

	public QueryResponse getFavoriteUserStats(String filter) throws SolrServerException, IOException;

	public Group getGroup(int id);

	public long getGroupCount();

	public GroupPermission getGroupPermissionByGroupId(int groupId, String permission);

	public List<GroupPermission> getGroupPermissionsByGroupId(int groupId);

	public Handle getHandle(int id);

	public Handle getHandleByPid(String pid);

	public long getInactiveUserCount();

	public List<User> getInactiveUsers(int start, int rows);

	public QueryResponse getItemCountByOwnerId(String ownerId) throws SolrServerException, IOException;

	public QueryResponse getItemCountByState(String state) throws SolrServerException, IOException;

	public QueryResponse getItemPidsByOwnerId(String ownerId) throws SolrServerException, IOException;

	public License getLicense(int id);

	public ca.ualberta.library.ir.model.solr.License getLicenseById(String pid) throws ServiceException;

	public List<ca.ualberta.library.ir.model.solr.License> getLicensesByFromName(String formName)
		throws ServiceException;

	public MailServiceManager getMailServiceManager();

	public QueryResponse getManualApprovalItemCount() throws SolrServerException, IOException;

	public List<Collection> getMemberCollections(String communityId) throws FedoraAPIException, SolrServerException;

	public List<Collection> getMemberOfCollections(String pid) throws SolrServerException;

	public List<Community> getMemberOfCommunities(String pid) throws SolrServerException;

	public QueryResponse getMoreLikeThis(String pid, int count) throws SolrServerException;

	public QueryResponse getNarrowSearch(String terms, String filters, boolean sort, int limit)
		throws SolrServerException;

	public int getNoOfSubscribers(String pid);

	public int getNoOfSubscribers(String pid, int type);

	public String getObjectContentModel(String pid) throws SolrServerException;

	public ObjectProfile getObjectProfile(String pid) throws FedoraAPIException;

	public List<ObjectFields> getObjectsByContentModels(String... contentModelPids) throws FedoraAPIException;

	public QueryResponse getPendingItemCountByOwnerId(String ownerId) throws SolrServerException, IOException;

	public List<Proquest> getProguests(int state);

	public Proquest getProquest(String pid);

	public List<Collection> getPublicCollections() throws SolrServerException;

	public List<Community> getPublicCommunities() throws SolrServerException;

	public List<Collection> getPublicCommunityMemberCollections(String pid) throws ServiceException;

	public Register getRegister(int id);

	public Register getRegisterByActivationKey(String key);

	public QueryResponse getRelatedTags(String tag, boolean sort, int limit) throws SolrServerException;

	public List<String> getRelationshipPids(String pid, String rel) throws SolrServerException;

	public RelationshipTuple[] getRelationships(String pid, String relationship) throws FedoraAPIException;

	public String getRestServiceUrl();

	public long getReviewItemCount(String userId) throws ServiceException;

	public long getSavedItemCount(String userId) throws ServiceException;

	public Scheduler getSchedulerByName(String name);

	public Subscription getSubscription(int id);

	public QueryResponse getSubscriptionCountByOwnerId(String ownerId) throws SolrServerException, IOException;

	public int getSubscriptionCountByPid(String pid);

	public List<User> getSubscriptionUsers();

	public List<User> getSubscriptionUsers(String pid, int type);

	public QueryResponse getSubscriptionUserStats(String ownerId) throws SolrServerException, IOException;

	public QueryResponse getSuggestions(String query, int limit) throws SolrServerException;

	public QueryResponse getTagCloud(boolean sort, int limit) throws SolrServerException;

	public QueryResponse getTagCountByPid(String pid) throws SolrServerException;

	public QueryResponse getTagsByOwnerId(String ownerId, List<String> filters, boolean sort, int limit)
		throws SolrServerException;

	public QueryResponse getTagsByPid(String pid, boolean sort, int limit) throws SolrServerException;

	public java.util.Properties getTemplates();

	public ThumbnailGenerator getThumbnailGenerator();

	public User getUser(int id);

	public User getUser(String username);

	public Bookmark getUserBookmarkByPid(String pid, int userId);

	public User getUserByCcid(String ccid);

	public User getUserByEmail(String email);

	public long getUserCount();

	public long getUserCount(String name);

	public List<User> getUsers(int start, int rows);

	public List<User> getUsersByGroupPermission(String permission);

	public List<User> getUsersByName(String name);

	public Subscription getUserSubscriptionByPid(String pid, int userId, int type);

	public VelocityEngine getVelocityEngine();

	public String ingest(Properties properties, String message) throws FedoraAPIException;

	public void init();

	public boolean isFavorite(String pid, int userId);

	public boolean isGroupPermissionAllowed(int groupId, List<String> roles);

	public boolean isUserPermissionAllowed(int userId, List<String> roles) throws UnauthorizedException;

	public DatastreamDef[] listDatastreams(String pid) throws FedoraAPIException;

	public String modifyDatastreamByReference(ca.ualberta.library.ir.model.fedora.Datastream datastream, String message)
		throws FedoraAPIException;

	public String modifyDatastreamByReference(String pid, String dsId, String[] altIds, String label, String mimeType,
		String formatUri, InputStream data, String checksumType, String checksum, String message, boolean force)
		throws FedoraAPIException;

	public String modifyDatastreamByValue(ca.ualberta.library.ir.model.fedora.Datastream datastream, String message)
		throws FedoraAPIException;

	public String modifyDatastreamByValue(String pid, String dsId, String label, byte[] xmlData, String message)
		throws FedoraAPIException;

	public String modifyDublinCore(String pid, String label, String dcString, String message) throws FedoraAPIException;

	public String modifyObject(Properties properties, String message) throws FedoraAPIException;

	public String modifyObject(String pid, String state, String label, String ownerId, String message)
		throws FedoraAPIException;

	public void modifyObjectRelationships(String pid, String label, List<String> memberOfPids,
		List<String> memberOfCollectionPids, List<String> partOfPids, String message) throws FedoraAPIException;

	public void modifyObjectRelationships(String pid, String label, String relationship, List<String> pids,
		String message) throws FedoraAPIException;

	public QueryResponse moreNarrowSearch(String query, String filters, String field, boolean sort, int limit,
		int offset) throws SolrServerException;

	public void openSession();

	public void optimize() throws IOException, SolrServerException;

	public void purgeContentModel(String pid, String object) throws FedoraAPIException;

	public String[] purgeDatastream(String pid, String dsId, String message) throws FedoraAPIException;

	public String purgeObject(String pid, String message) throws FedoraAPIException;

	public boolean purgeRelationship(java.lang.String pid, java.lang.String relationship, java.lang.String object,
		boolean isLiteral, java.lang.String datatype) throws FedoraAPIException;

	public QueryResponse query(String type, String query, String filters, int start, int rows, String sort)
		throws SolrServerException;

	public AbstractResponse resolve(String handle);

	public void saveOrUpdateAuthor(Author author);

	public void deleteAuthor(Author author);

	public void saveOrUpdateAuthorProfile(AuthorProfile authorProfile);

	public void saveOrUpdateBookmark(Bookmark bookmark);

	public void saveOrUpdateDownload(Download download);

	public void saveOrUpdateFavorite(Favorite favorite);

	public void saveOrUpdateGroup(Group group);

	public void saveOrUpdateGroupPermission(GroupPermission groupPermission);

	public void saveOrUpdateHandle(Handle handle);

	public void saveOrUpdateProquest(Proquest proquest);

	public void saveOrUpdateRegister(Register register);

	public void saveOrUpdateScheduler(Scheduler scheduler);

	public void saveOrUpdateSubscription(Subscription subscription);

	public void saveOrUpdateUser(User user);

	public void saveOrUpdateUserPermission(UserPermission userPermission);

	public QueryResponse search(String terms, String filters, int start, int rows, String sort)
		throws SolrServerException;

	public void setTemplates(java.util.Properties templates);

	public void setThumbnailGenerator(ThumbnailGenerator thumbnailGenerator);

	public void setVelocityEngine(VelocityEngine velocityEngine);

	public void version() throws FedoraAPIException;
}
