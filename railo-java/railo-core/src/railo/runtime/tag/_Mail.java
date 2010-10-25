package railo.runtime.tag;

import railo.commons.io.res.Resource;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.net.mail.MailClient;
import railo.runtime.op.Caster;
import railo.runtime.type.List;
import railo.runtime.type.util.ArrayUtil;

/**
 * Retrieves and deletes e-mail messages from a POP mail server.
 */
public abstract class _Mail extends TagImpl {
    
    private String server;
    private int port=-1;

    private String username;
    private String password;
    private String action="getheaderonly";
    private String name;
    private String[] messageNumber;
    private String[] uid;
    private Resource attachmentPath;
    private int timeout=60;
    private int startrow=1;
    private int maxrows=-1;
    private boolean generateUniqueFilenames=false;
    private boolean debug=false;
    
    /**
     * @see railo.runtime.ext.tag.TagImpl#release()
     */
    public void release() {
        port=-1;
        username=null;
        password=null;
        action="getheaderonly";
        name=null;
        messageNumber=null;
        uid=null;
        attachmentPath=null;
        timeout=60;
        startrow=1;
        maxrows=-1;
        generateUniqueFilenames=false;
        debug=false;
        super.release();
        
    }

    /**
     * @param server The server to set.
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @param port The port to set.
     */
    public void setPort(double port) {
        this.port = (int)port;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param action The action to set.
     */
    public void setAction(String action) {
        this.action = action.trim().toLowerCase();
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param messageNumber The messageNumber to set.
     * @throws PageException 
     */
    public void setMessagenumber(String messageNumber) throws PageException {
        this.messageNumber = ArrayUtil.trim(List.toStringArray(List.listToArrayRemoveEmpty(messageNumber,',')));
        if(this.messageNumber.length==0)this.messageNumber=null;
    }

    /**
     * @param uid The uid to set.
     * @throws PageException 
     */
    public void setUid(String uid) throws PageException {
        this.uid = ArrayUtil.trim(List.toStringArray(List.listToArrayRemoveEmpty(uid,',')));
        if(this.uid.length==0)this.uid=null;
    }

    /**
     * @param attachmentPath The attachmentPath to set.
     * @throws PageException 
     */
    public void setAttachmentpath(String attachmentPath) throws PageException {
        //try {
        	Resource attachmentDir=pageContext.getConfig().getResource(attachmentPath);
            if(!attachmentDir.exists() && !attachmentDir.mkdir()) {
                attachmentDir=pageContext.getConfig().getTempDirectory().getRealResource(attachmentPath);
                if(!attachmentDir.exists() && !attachmentDir.mkdir())
                    throw new ApplicationException("directory ["+attachmentPath+"] doesent exist and can't created");
            }
            if(!attachmentDir.isDirectory())throw new ApplicationException("file ["+attachmentPath+"] is not a directory");
            pageContext.getConfig().getSecurityManager().checkFileLocation(attachmentDir);
            this.attachmentPath = attachmentDir;
        /*}
        catch(IOException ioe) {
            throw Caster.toPageException(ioe);
        }*/
    }

    /**
     * @param maxrows The maxrows to set.
     */
    public void setMaxrows(double maxrows) {
        this.maxrows = (int)maxrows;
    }

    /**
     * @param startrow The startrow to set.
     */
    public void setStartrow(double startrow) {
        this.startrow = (int)startrow;
    }

    /**
     * @param timeout The timeout to set.
     */
    public void setTimeout(double timeout) {
        this.timeout = (int)timeout;
    }

    /**
     * @param generateUniqueFilenames The generateUniqueFilenames to set.
     */
    public void setGenerateuniquefilenames(boolean generateUniqueFilenames) {
        this.generateUniqueFilenames = generateUniqueFilenames;
    }

    /**
     * @param debug The debug to set.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws PageException {
    	
    	// check attrs
    	if(port==-1)port=getDefaultPort();
    	
    	//PopClient client = new PopClient(server,port,username,password);
    	MailClient client = MailClient.getInstance(getType(),server,port,username,password);
        client.setTimeout(timeout*1000);
        client.setMaxrows(maxrows);
        if(startrow>1)client.setStartrow(startrow-1);
        client.setUniqueFilenames(generateUniqueFilenames);
        if(attachmentPath!=null)client.setAttachmentDirectory(attachmentPath);
        
        if(uid!=null)messageNumber=null;
        
        try {
            client.connect();
            
            if(action.equals("getheaderonly")) {
                required(getTagName(),action,"name",name);
                pageContext.setVariable(name,client.getMails(messageNumber,uid,false));
            }
            else if(action.equals("getall")) {
                required(getTagName(),action,"name",name);
                pageContext.setVariable(name,client.getMails(messageNumber,uid,true));
            }
            else if(action.equals("delete")) {
                client.deleteMails(messageNumber,uid);
            }
            else throw new ApplicationException("invalid value for attribute action, valid values are [getHeaderOnly,getAll,delete]");
        }
        catch(Exception e) {
            throw Caster.toPageException(e);
        }
        finally{
            client.disconnectEL();
        }
        return SKIP_BODY;
    }

	protected abstract int getType();
	protected abstract int getDefaultPort();
	protected abstract String getTagName();
}