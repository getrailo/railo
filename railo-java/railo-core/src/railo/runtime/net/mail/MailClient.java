package railo.runtime.net.mail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import railo.commons.io.CharsetUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.Md5;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.net.imap.ImapClient;
import railo.runtime.net.pop.PopClient;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ListUtil;

public abstract class MailClient {
	
	/**
	 * Simple authenicator implmentation
	 */
	private final class _Authenticator extends Authenticator {

		private String _fldif = null;
		private String a = null;

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(_fldif, a);
		}

		public _Authenticator(String s, String s1) {
			_fldif = s;
			a = s1;
		}
	}
	

	private static final Collection.Key DATE = KeyImpl.init("date");
	private static final Collection.Key SUBJECT = KeyImpl.init("subject");
	private static final Collection.Key SIZE = KeyImpl.init("size");
	private static final Collection.Key FROM = KeyImpl.init("from");
	private static final Collection.Key MESSAGE_NUMBER = KeyImpl.init("messagenumber");
	private static final Collection.Key MESSAGE_ID = KeyImpl.init("messageid");
	private static final Collection.Key REPLYTO = KeyImpl.init("replyto");
	private static final Collection.Key CC = KeyImpl.init("cc");
	private static final Collection.Key BCC = KeyImpl.init("bcc");
	private static final Collection.Key TO = KeyImpl.init("to");
	private static final Collection.Key UID = KeyImpl.init("uid");
	private static final Collection.Key HEADER = KeyImpl.init("header");
	private static final Collection.Key BODY = KeyImpl.init("body");
	private static final Collection.Key CIDS = KeyImpl.init("cids");
	private static final Collection.Key TEXT_BODY = KeyImpl.init("textBody");
	private static final Collection.Key HTML_BODY = KeyImpl.init("HTMLBody");
	private static final Collection.Key ATTACHMENTS = KeyImpl.init("attachments");
	private static final Collection.Key ATTACHMENT_FILES = KeyImpl.init("attachmentfiles");


	public static final int TYPE_POP3 = 0;
	public static final int TYPE_IMAP = 1;


	private String _flddo[] = {"date", "from", "messagenumber", "messageid", "replyto", "subject", "cc", "to", "size", "header", "uid"};
	private String _fldnew[] = {"date", "from", "messagenumber", "messageid", "replyto", "subject", "cc", "to", "size", "header", "uid", "body", "textBody", "HTMLBody", "attachments", "attachmentfiles","cids"};
	private String server = null;
	private String username = null;
	private String password = null;
	private Session _fldtry = null;
	private Store _fldelse = null;
	private int port = 0;
	private int timeout = 0;
	private int startrow = 0;
	private int maxrows = 0;
	private boolean uniqueFilenames = false;
	private Resource attachmentDirectory = null;

	
	public static MailClient getInstance(int type,String server, int port, String username, String password){
		if(TYPE_POP3==type)
			return new PopClient(server,port,username,password);
		if(TYPE_IMAP==type)
			return new ImapClient(server,port,username,password);
		return null;
	}
	
	/**
	 * constructor of the class
	 * @param server
	 * @param port
	 * @param username
	 * @param password
	 */
	public MailClient(String server, int port, String username, String password) {
		timeout = 60000;
		startrow = 0;
		maxrows = -1;
		uniqueFilenames = false;
		this.server = server;
		this.port = port;
		this.username = username;
		this.password = password;
	}


    /**
     * @param maxrows The maxrows to set.
     */
    public void setMaxrows(int maxrows) {
        this.maxrows = maxrows;
    }

    /**
     * @param startrow The startrow to set.
     */
    public void setStartrow(int startrow) {
        this.startrow = startrow;
    }


    /**
     * @param timeout The timeout to set.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @param uniqueFilenames The uniqueFilenames to set.
     */
    public void setUniqueFilenames(boolean uniqueFilenames) {
        this.uniqueFilenames = uniqueFilenames;
    }

    /**
     * @param attachmentDirectory The attachmentDirectory to set.
     */
    public void setAttachmentDirectory(Resource attachmentDirectory) {
        this.attachmentDirectory = attachmentDirectory;
    }

    /**
     * connects to pop server
     * @throws MessagingException
     */
    public void connect() throws MessagingException {
		Properties properties = new Properties();
		String type=getTypeAsString();
		properties.put("mail."+type+".host", server);
		properties.put("mail."+type+".port", new Double(port));
		properties.put("mail."+type+".connectiontimeout", String.valueOf(timeout));
		properties.put("mail."+type+".timeout", String.valueOf(timeout));
		//properties.put("mail.mime.charset", "UTF-8");
	        
		
		if(TYPE_IMAP==getType())properties.put("mail.imap.partialfetch", "false" );
		
		_fldtry = username != null ? Session.getInstance(properties, new _Authenticator(username, password)) : Session.getInstance(properties);
		_fldelse = _fldtry.getStore(type);
		if(!StringUtil.isEmpty(username))_fldelse.connect(server,username,password);
		else _fldelse.connect();
	}

    protected abstract String getTypeAsString();
    protected abstract int getType();


	/**
     * delete all message in ibox that match given criteria
     * @param messageNumbers
     * @param uIds
     * @throws MessagingException
	 * @throws IOException 
     */
    public void deleteMails(String as[], String as1[]) throws MessagingException, IOException {
		Folder folder;
		Message amessage[];
		folder = _fldelse.getFolder("INBOX");
		folder.open(2);
		Map<String, Message> map = getMessages(null,folder, as1, as, startrow, maxrows,false);
		Iterator<String> iterator = map.keySet().iterator();
		amessage = new Message[map.size()];
		int i = 0;
		while(iterator.hasNext()) {
			amessage[i++] =  map.get(iterator.next());
		}
		try {
			folder.setFlags(amessage, new Flags(javax.mail.Flags.Flag.DELETED), true);
		}
		finally {
			folder.close(true);
		}
	}

    /**
     * return all messages from inbox
     * @param messageNumbers all messages with this ids
     * @param uIds all messages with this uids
     * @param withBody also return body
     * @return all messages from inbox
     * @throws MessagingException
     * @throws IOException
     */
    public Query getMails(String[] messageNumbers, String[] uids, boolean all) throws MessagingException, IOException {
		Query qry = new QueryImpl(all ? _fldnew : _flddo, 0, "query");
		Folder folder = _fldelse.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		try {
			getMessages(qry,folder, uids, messageNumbers, startrow, maxrows,all);
		}
		finally {
			folder.close(false);
		}
		return qry;
	}

    private void toQuery(Query qry, Message message, Object uid, boolean all) 
     {
		int row = qry.addRow();
    	// date
		try {
			qry.setAtEL(DATE, row, Caster.toDate(message.getSentDate(), true,null,null));
		} 
		catch (MessagingException e) {}
		
		// subject
		try {
			qry.setAtEL(SUBJECT, row, message.getSubject());
		} catch (MessagingException e) {
			qry.setAtEL(SUBJECT, row, "MessagingException:"+e.getMessage());
		}
		
		// size
		try {
			qry.setAtEL(SIZE, row, new Double(message.getSize()));
		} 
		catch (MessagingException e) {}
		
		qry.setAtEL(FROM, row, toList(getHeaderEL(message,"from")));
		qry.setAtEL(MESSAGE_NUMBER, row, new Double(message.getMessageNumber()));
		qry.setAtEL(MESSAGE_ID, row, toList(getHeaderEL(message,"Message-ID")));
		String s = toList(getHeaderEL(message,"reply-to"));
		if(s.length() == 0) {
			s = Caster.toString(qry.getAt(FROM, row,null), "");
		}
		qry.setAtEL(REPLYTO, row, s);
		qry.setAtEL(CC, row, toList(getHeaderEL(message,"cc")));
		qry.setAtEL(BCC, row, toList(getHeaderEL(message,"bcc")));
		qry.setAtEL(TO, row, toList(getHeaderEL(message,"to")));
		qry.setAtEL(UID, row, uid);
		StringBuffer content = new StringBuffer();
		try {
			for(Enumeration enumeration = message.getAllHeaders(); enumeration.hasMoreElements(); content.append('\n')){
				Header header = (Header) enumeration.nextElement();
				content.append(header.getName());
				content.append(": ");
				content.append(header.getValue());
			}
		} 
		catch (MessagingException e) {}
		qry.setAtEL(HEADER, row, content.toString());
		
		if(all) {
			getContentEL(qry, message, row);
		}
	}

	private String[] getHeaderEL(Message message, String key) {
		try {
			return message.getHeader(key);
		} catch (MessagingException e) {
			return null;
		}
	}

	/**
     * gets all messages from given Folder that match given criteria
	 * @param qry 
     * @param folder
     * @param uIds
     * @param messageNumbers
	 * @param all 
     * @param startrow
     * @param maxrows
	 * @return 
     * @return matching Messages
	 * @throws MessagingException 
     */
    private Map<String,Message> getMessages(Query qry, Folder folder, String[] uids, String[] messageNumbers, int startRow, int maxRow, boolean all) throws MessagingException 
     {
		
    	Message[] messages = folder.getMessages();
		Map<String,Message> map = qry==null?new HashMap<String,Message>():null;
		int k = 0;
		if(uids != null || messageNumbers != null) {
			startRow = 0;
			maxRow = -1;
		}
		Message message;
		for(int l = startRow; l < messages.length; l++) {
			if(maxRow != -1 && k == maxRow) {
				break;
			}
			message = messages[l];
			int messageNumber = message.getMessageNumber();
			String id = getId(folder,message);
			
			if(uids == null ? messageNumbers == null || contains(messageNumbers, messageNumber) : contains(uids, id)) {
				k++;
				if(qry!=null){
					toQuery(qry,message,id,all);
				}
				else map.put(id, message);
			}
		}
		return map;
	}
    protected abstract String getId(Folder folder,Message message) throws MessagingException ;


    private void getContentEL(Query query, Message message, int row) {
    	try {
			getContent(query, message, row);
		} catch (Exception e) {
			String st = ExceptionUtil.getStacktrace(e,true);
			
			query.setAtEL(BODY, row, st);
		}
    }

	/**
     * write content data to query
     * @param qry
     * @param content
     * @param row
     * @throws MessagingException
     * @throws IOException
     */
    private void getContent(Query query, Message message, int row) throws MessagingException, IOException {
		StringBuffer body = new StringBuffer();
		Struct cids=new StructImpl();
		query.setAtEL(CIDS, row, cids);
		if(message.isMimeType("text/plain")) {
			String content=getConent(message);
	    	query.setAtEL(TEXT_BODY,row,content);
	    	body.append(content);
		}
		else if(message.isMimeType("text/html")) {
			String content=getConent(message);
	    	query.setAtEL(HTML_BODY,row,content);
	    	body.append(content);
		}
		else {
			Object content = message.getContent();
			if(content instanceof MimeMultipart) {
				Array attachments = new ArrayImpl();
				Array attachmentFiles = new ArrayImpl();
				getMultiPart(query, row, attachments, attachmentFiles,cids, (MimeMultipart) content, body);
	
				if(attachments.size() > 0) {
					try {
						query.setAtEL(ATTACHMENTS, row, ListUtil.arrayToList(attachments, "\t"));
					}
					catch(PageException pageexception) {
					}
				}
				if(attachmentFiles.size() > 0) {
					try {
						query.setAtEL(ATTACHMENT_FILES, row, ListUtil.arrayToList(attachmentFiles, "\t"));
					}
					catch(PageException pageexception1) {
					}
				}
				
				
			}
		}
		query.setAtEL(BODY, row, body.toString());
	}

	private void getMultiPart(Query query, int row, Array attachments, Array attachmentFiles,Struct cids, Multipart multiPart, StringBuffer body) throws MessagingException, IOException {
		int j = multiPart.getCount();

		for(int k = 0; k < j; k++) {
			BodyPart bodypart = multiPart.getBodyPart(k);
			Object content;
			

			if(bodypart.getFileName() != null) {
				String filename = bodypart.getFileName();
				try{
					filename=Normalizer.normalize(MimeUtility.decodeText(filename),Normalizer.Form.NFC);
				}
				catch(Throwable t){}
				
				if(bodypart.getHeader("Content-ID") != null) {
					String[] ids = bodypart.getHeader("Content-ID");
					String cid=ids[0].substring(1, ids[0].length() - 1);
					cids.setEL(KeyImpl.init(filename), cid);
				}
				
				if(filename != null && ArrayUtil.find(attachments, filename)==0) {
					
					attachments.appendEL(filename);
					if(attachmentDirectory != null) {
						Resource file = attachmentDirectory.getRealResource(filename);
						int l = 1;
						String s2;
						for(; uniqueFilenames && file.exists(); file = attachmentDirectory.getRealResource(s2)) {
							String as[] = ResourceUtil.splitFileName(filename);
							s2 = as.length != 1 ? as[0] + l++ + '.' + as[1] : as[0] + l++;
						}

						IOUtil.copy(bodypart.getInputStream(), file, true);
						attachmentFiles.appendEL(file.getAbsolutePath());
					}
				}
			}
			else if(bodypart.isMimeType("text/plain")) {
				content=getConent(bodypart);
		    	query.setAtEL(TEXT_BODY,row,content);
		    	if(body.length()==0)body.append(content);
			}
			else if(bodypart.isMimeType("text/html")) {
				content=getConent(bodypart);
		    	query.setAtEL(HTML_BODY,row,content);
		    	if(body.length()==0)body.append(content);
			}
			else if((content=bodypart.getContent()) instanceof Multipart) {
				getMultiPart(query, row, attachments, attachmentFiles,cids, (Multipart) content, body);
			}
			else if(bodypart.getHeader("Content-ID") != null) {
				String[] ids = bodypart.getHeader("Content-ID");
				String cid=ids[0].substring(1, ids[0].length() - 1);
				String filename = "cid:" + cid;
				
					attachments.appendEL(filename);
					if(attachmentDirectory != null) {
						filename = "_" + Md5.getDigestAsString(filename);
						Resource file = attachmentDirectory.getRealResource(filename);
						int l = 1;
						String s2;
						for(; uniqueFilenames && file.exists(); file = attachmentDirectory.getRealResource(s2)) {
							String as[] = ResourceUtil.splitFileName(filename);
							s2 = as.length != 1 ? as[0] + l++ + '.' + as[1] : as[0] + l++;
						}

						IOUtil.copy(bodypart.getInputStream(), file, true);
						attachmentFiles.appendEL(file.getAbsolutePath());
					}
				
				cids.setEL(KeyImpl.init(filename), cid);
			}
		}
	}

	/* *
     * writes BodyTag data to query, if there is a problem with encoding, encoding will removed a do it again
     * @param qry
     * @param columnName
     * @param row
     * @param bp
     * @param body
     * @throws IOException
     * @throws MessagingException
     * /
    private void setBody(Query qry, String columnName, int row, BodyPart bp, StringBuffer body) throws IOException, MessagingException {
        String content = getConent(bp);
    	
    	qry.setAtEL(columnName,row,content);
        if(body.length()==0)body.append(content);
        
    }*/
    
    private String getConent(Part bp) throws MessagingException {
    	InputStream is=null;
    	
    	try {
    		return getContent(is=bp.getInputStream(), CharsetUtil.toCharset(getCharsetFromContentType(bp.getContentType())));
        }
        catch(IOException mie) {
        	IOUtil.closeEL(is);
        	try {
				return getContent(is=bp.getInputStream(), SystemUtil.getCharset());
			} 
        	catch (IOException e) {
        		return "Can't read body of this message:"+e.getMessage();
			} 
        }
        finally {
        	IOUtil.closeEL(is);
        }
    }
    
    private String getContent(InputStream is,Charset charset) throws IOException {
		return MailUtil.decode(IOUtil.toString(is, charset));
    }
    
    
	private static String getCharsetFromContentType(String contentType) {
		Array arr=ListUtil.listToArrayRemoveEmpty(contentType,"; ");
		
		for(int i=1;i<=arr.size();i++) {
			Array inner = ListUtil.listToArray((String)arr.get(i,null),"= ");
			if(inner.size()==2 && ((String)inner.get(1,"")).trim().equalsIgnoreCase("charset")) {
				String charset = (String) inner.get(2,"");
				charset=charset.trim();
				if(!StringUtil.isEmpty(charset)) {
					if(StringUtil.startsWith(charset, '"') && StringUtil.endsWith(charset, '"')) {
						charset=charset.substring(1,charset.length()-1);
					}
					if(StringUtil.startsWith(charset, '\'') && StringUtil.endsWith(charset, '\'')) {
						charset=charset.substring(1,charset.length()-1);
					}
				}
				return charset;
			}
		}
		return "us-ascii";
	}


	/**
     * checks if a String Array (ids) has one element that is equal to id
     * @param ids
     * @param id
     * @return has element found or not
     */
	private boolean contains(String ids[], String id) {
		for(int i = 0; i < ids.length; i++) {
			if(Operator.compare(ids[i], id) == 0)	return true;
		}
		return false;
	}

	/**
     * checks if a String Array (ids) has one element that is equal to id
     * @param ids
     * @param id
     * @return has element found or not
     */
	private boolean contains(String ids[], int id) {
		for(int i = 0; i < ids.length; i++) {
			if(Operator.compare(ids[i], id) == 0) return true;
		}
		return false;
	}

	/**
     * translate a String Array to String List
     * @param arr Array to translate
     * @return List from Array
     */
	private String toList(String ids[]) {
		if(ids == null)	return "";
		return ListUtil.arrayToList(ids, ",");
	}

	/**
     * disconnect without a exception
     */
	public void disconnectEL() {
		try {
			if(_fldelse != null)_fldelse.close();
		}
		catch(Exception exception) {}
	}
}