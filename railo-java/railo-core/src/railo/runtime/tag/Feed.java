package railo.runtime.tag;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.type.http.HTTPResource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.Pair;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.functions.dateTime.GetHttpTimeString;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.proxy.ProxyDataImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.text.feed.FeedHandler;
import railo.runtime.text.feed.FeedQuery;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Array;
import railo.runtime.type.List;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;

public final class Feed extends TagImpl	{

	private static final int ACTION_READ = 0;
	private static final int ACTION_CREATE = 1;
	
	private static final int TYPE_AUTO = 0;
	private static final int TYPE_RSS = 1;
	private static final int TYPE_ATOM = 2;
	
	
	
	private int action=ACTION_READ;
	private Struct columnMap=null;
	private Resource enclosureDir=null;
	private boolean ignoreEnclosureError=false;
	private Object name=null;
	private Resource outputFile=null;
	private boolean overwrite=false;
	private boolean overwriteEnclosure=false;
	private Object properties=null;
	private Object query=null;
	private Resource source=null;
	private int timeout=-1;
	private int type=TYPE_AUTO;
	private String userAgent=null;
	private String xmlVar=null;

	private String proxyPassword=null;
	private int proxyPort=80;
	private String proxyServer=null;
	private String proxyUser=null;
	private String charset=null;
	
	/**
	 * @see railo.runtime.ext.tag.TagImpl#release()
	 */
	public void release() {
		charset=null;
		action=ACTION_READ;
		columnMap=null;
		enclosureDir=null;
		ignoreEnclosureError=false;
		name=null;
		outputFile=null;
		overwrite=false;
		overwriteEnclosure=false;
		properties=null;
		query=null;
		source=null;
		timeout=-1;
		userAgent=null;
		xmlVar=null;

		proxyPassword=null;
		proxyPort=80;
		proxyServer=null;
		proxyUser=null;
		
		type=TYPE_AUTO;
		super.release();
	}
	/** set the value charset
	*  Character set name for the file contents.
	* @param charset value to set
	**/
	public void setCharset(String charset)	{
		this.charset=charset.trim();
	}
	
	/**
	 * @param action the action to set
	 * @throws ApplicationException 
	 */
	public void setAction(String strAction) throws ApplicationException {

		strAction=StringUtil.toLowerCase(strAction.trim());
		if("read".equals(strAction))				action=ACTION_READ;
		else if("create".equals(strAction))				action=ACTION_CREATE;
		
		else throw new ApplicationException("invalid action definition ["+strAction+"], valid action definitions are " +
				"[create,read]");
	}
	
	public void setType(String strType) throws ApplicationException {

		strType=StringUtil.toLowerCase(strType.trim());
		if("rss".equals(strType))				type=TYPE_RSS;
		else if("atom".equals(strType))			type=TYPE_ATOM;
		
		else throw new ApplicationException("invalid type definition ["+strType+"], valid type definitions are " +
				"[atom,rss]");
	}
	/**
	 * @param columnMap the columnMap to set
	 */
	public void setColumnmap(Struct columnMap) {
		this.columnMap = columnMap;
	}
	/**
	 * @param enclosureDir the enclosureDir to set
	 * @throws ExpressionException 
	 */
	public void setEnclosuredir(String strEnclosureDir) throws ExpressionException {
		this.enclosureDir = ResourceUtil.toResourceExisting(pageContext, strEnclosureDir);
	}
	/**
	 * @param ignoreEnclosureError the ignoreEnclosureError to set
	 */
	public void setIgnoreenclosureerror(boolean ignoreEnclosureError) {
		this.ignoreEnclosureError = ignoreEnclosureError;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(Object name) {
		this.name = name;
	}
	/**
	 * @param outputFile the outputFile to set
	 * @throws ExpressionException 
	 */
	public void setOutputfile(String strOutputFile) throws ExpressionException {
		this.outputFile = ResourceUtil.toResourceExistingParent(pageContext, strOutputFile);
	}
	/**
	 * @param overwrite the overwrite to set
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	/**
	 * @param overwriteEnclosure the overwriteEnclosure to set
	 */
	public void setOverwriteenclosure(boolean overwriteEnclosure) {
		this.overwriteEnclosure = overwriteEnclosure;
	}
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Object properties) {
		this.properties = properties;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(Object query) {
		this.query = query;
	}
	/**
	 * @param source the source to set
	 * @throws ExpressionException 
	 */
	public void setSource(String strSource) throws ExpressionException {
		// when using  toExistingResource execution fails because proxy is missed at this time
		this.source = ResourceUtil.toResourceNotExisting(pageContext, strSource);
	}
	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(double timeout) {
		this.timeout = (int) timeout;
	}
	/**
	 * @param userAgent the userAgent to set
	 */
	public void setUseragent(String userAgent) {
		this.userAgent = userAgent;
	}
	/**
	 * @param xmlVar the xmlVar to set
	 */
	public void setXmlvar(String xmlVar) {
		this.xmlVar = xmlVar;
	}
	/**
	 * @param proxyPassword the proxyPassword to set
	 */
	public void setProxypassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
	
	/**
	 * @param proxyPort the proxyPort to set
	 */
	public void setProxyport(double proxyPort) {
		this.proxyPort = (int) proxyPort;
	}
	
	/**
	 * @param proxyServer the proxyServer to set
	 */
	public void setProxyserver(String proxyServer) {
		this.proxyServer = proxyServer;
	}
	/**
	 * @param proxyUser the proxyUser to set
	 */
	public void setProxyuser(String proxyUser) {
		this.proxyUser = proxyUser;
	}
	

	
	/**
	 * @see railo.runtime.ext.tag.TagImpl#doStartTag()
	 */
	public int doStartTag() throws PageException {
		if(source instanceof HTTPResource) {
			HTTPResource httpSource = (HTTPResource)source;
			if(!StringUtil.isEmpty(proxyServer,true)){
				ProxyData data=new ProxyDataImpl(proxyServer,proxyPort,proxyUser,proxyPassword);
				httpSource.setProxyData(data);
			}
			if(!StringUtil.isEmpty(userAgent))
				httpSource.setUserAgent(userAgent);
			if(timeout>-1)httpSource.setTimeout(timeout*1000);
		}
		
		try {
			if(ACTION_CREATE==action)		doActionCreate();
			else if(ACTION_READ==action)	doActionRead();
		}
		catch(Exception e) {
			throw Caster.toPageException(e);
		}
		return SKIP_BODY;
	}
	
	private void doActionCreate() throws PageException {
		
		// name
		Query qry;
		Struct props;
		boolean splitString=true;
		if(name!=null)	{
			Struct data;
			if(name instanceof String){
				data=Caster.toStruct(pageContext.getVariable(Caster.toString(name)));
			}
			else data = Caster.toStruct(name,false);
			
			qry = FeedQuery.toQuery(data,false);
			props = FeedProperties.toProperties(data);
			splitString=false;
		}
		else if(query!=null && properties!=null)	{
			qry = FeedQuery.toQuery(Caster.toQuery(query));
			props = FeedProperties.toProperties(Caster.toStruct(properties,false));
		}
		else {
			throw new ApplicationException("missing attribute [name] or attributes [query] and [properties]");
		}
		
		StringBuffer xml=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		
		if(type==TYPE_AUTO){
			String version=Caster.toString(props.get("version","rss"),"rss");
			type=StringUtil.startsWithIgnoreCase(version, "rss")?TYPE_RSS:TYPE_ATOM;
		}
		if(type==TYPE_RSS) {
			createRSS(xml,qry,props,splitString);
		}
		else {
			createAtom(xml, qry, props, splitString);
		}
		
		// variable
		if(!StringUtil.isEmpty(xmlVar)) {
			pageContext.setVariable(xmlVar, xml);
		}
		//file
		if(outputFile!=null){
			if(outputFile.exists() && !overwrite)
				throw new ApplicationException("destiniation file ["+outputFile+"] already exist");
			
			

			if(StringUtil.isEmpty(charset)) charset=pageContext.getConfig().getResourceCharset();
			
			try {
				IOUtil.write(outputFile, xml.toString(),charset,false);
			} catch (IOException e) {
				throw Caster.toPageException(e);
			}
		}
		
		
		
		
		
		/*
<cffeed
    action = "create"
    name = "#structure#"
        One or both of the following:
    outputFile = "path"
    xmlVar = "variable name"
    optional 
    overwrite = "no|yes">
 
    <cffeed
    action = "create"
    properties = "#metadata structure#"
    query = "#items/entries query name#"
        One or both of the following:
    outputFile = "path"
    xmlVar = "variable name"
    optional
    columnMap = "mapping structure"
    overwrite = "no|yes"> 
		 */
		
	}
	
	private void createAtom(StringBuffer xml, Query query, Struct props,boolean splitString) throws PageException {
		int rows=query.getRowCount();
		
		append(xml,0,"<feed xmlns=\"http://www.w3.org/2005/Atom\">");

		propTag(props,xml,1,new String[]{"title"},"title",
				new String[][]{
					new String[]{"type","type"}
				}
		);
		propTag(props,xml,1,new String[]{"subtitle"},"subtitle",
				new String[][]{
					new String[]{"type","type"}
				}
		);
		propTag(props,xml,1,new String[]{"updated"},"updated",null);
		propTag(props,xml,1,new String[]{"id"},"id",null);
		propTag(props,xml,1,new String[]{"link"},"link",
				new String[][]{
				new String[]{"rel","rel"},
				new String[]{"type","type"},
				new String[]{"hreflang","hreflang"},
				new String[]{"href","href"}
				}
		);
		propTag(props,xml,1,new String[]{"rights"},"rights",null);
		propTag(props,xml,1,new String[]{"generator"},"generator",
				new String[][]{
				new String[]{"uri","uri"},
				new String[]{"version","version"}
				}
		);
		
		// items
		for(int row=1;row<=rows;row++) {
			append(xml,1,"<entry>");

			tag(xml,2,
					new Pair("title",query.getAt(FeedQuery.TITLE, row, null)),
					new Pair[]{
						new Pair("type",query.getAt(FeedQuery.TITLETYPE, row, null))
					},false,splitString
			);
			tag(xml,2,
					new Pair("link",null),
					new Pair[]{
						new Pair("href",query.getAt(FeedQuery.LINKHREF, row, null)),
						new Pair("hreflang",query.getAt(FeedQuery.LINKHREFLANG, row, null)),
						new Pair("length",query.getAt(FeedQuery.LINKLENGTH, row, null)),
						new Pair("rel",query.getAt(FeedQuery.LINKREL, row, null)),
						new Pair("title",query.getAt(FeedQuery.LINKTITLE, row, null)),
						new Pair("type",query.getAt(FeedQuery.LINKTYPE, row, null))
					},false,splitString
			);
			tag(xml,2,new Pair("id",query.getAt(FeedQuery.ID, row, null)),null,true,false);
			tag(xml,2,new Pair("updated",query.getAt(FeedQuery.UPDATEDDATE, row, null)),null,true,false);
			tag(xml,2,new Pair("published",query.getAt(FeedQuery.PUBLISHEDDATE, row, null)),null,true,false);
			tag(xml,2,
					new Pair("author",null),
					new Pair[]{
						new Pair("email",query.getAt(FeedQuery.AUTHOREMAIL, row, null)),
						new Pair("name",query.getAt(FeedQuery.AUTHORNAME, row, null)),
						new Pair("uri",query.getAt(FeedQuery.AUTHORURI, row, null))
					},false,splitString
			);
			tag(xml,2,
					new Pair("category",null),
					new Pair[]{
						new Pair("label",query.getAt(FeedQuery.CATEGORYLABEL, row, null)),
						new Pair("scheme",query.getAt(FeedQuery.CATEGORYSCHEME, row, null)),
						new Pair("term",query.getAt(FeedQuery.CATEGORYTERM, row, null)),
					},false,splitString
			);
			tag(xml,2,
					new Pair("contributor",null),
					new Pair[]{
						new Pair("email",query.getAt(FeedQuery.CONTRIBUTOREMAIL, row, null)),
						new Pair("name",query.getAt(FeedQuery.CONTRIBUTORNAME, row, null)),
						new Pair("uri",query.getAt(FeedQuery.CONTRIBUTORURI, row, null))
					},false,splitString
			);
			tag(xml,2,
					new Pair("content",query.getAt(FeedQuery.CONTENT, row, null)),
					new Pair[]{
						new Pair("src",query.getAt(FeedQuery.CONTENTSRC, row, null)),
						new Pair("type",query.getAt(FeedQuery.CONTENTTYPE, row, null)),
						new Pair("type",query.getAt(FeedQuery.CONTENTTYPE, row, null))
					},false,splitString
			);
			tag(xml,2,new Pair("rights",query.getAt(FeedQuery.RIGHTS, row, null)),null,true,false);
			tag(xml,2,
					new Pair("summary",query.getAt(FeedQuery.SUMMARY, row, null)),
					new Pair[]{
						new Pair("type",query.getAt(FeedQuery.SUMMARYTYPE, row, null))
					},false,splitString
			);
			
			append(xml,1,"</entry>");
		}
		
		
		
		
		append(xml,0,"</feed>");
		
	}
	private void createRSS(StringBuffer xml, Query query, Struct props, boolean splitString) throws PageException {
		int rows=query.getRowCount();
		
		append(xml,0,"<rss xmlns:taxo=\"http://purl.org/rss/1.0/modules/taxonomy/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"2.0\">");
		append(xml,1,"<channel>");
		
		// title
		propTag(props,xml,2,new String[]{"title"},"title",null);
		propTag(props,xml,2,new String[]{"link"},"link",null);
		propTag(props,xml,2,new String[]{"description","subtitle"},"description",null);
		propTag(props,xml,2,new String[]{"language"},"language",null);
		propTag(props,xml,2,new String[]{"copyright"},"copyright",null);
		propTag(props,xml,2,new String[]{"managingEditor"},"managingEditor",null);
		propTag(props,xml,2,new String[]{"webMaster"},"webMaster",null);
		propTag(props,xml,2,new String[]{"pubDate"},"pubDate",null);
		propTag(props,xml,2,new String[]{"lastBuildDate"},"lastBuildDate",null);
		propTag(props,xml,2,new String[]{"category"},"category",
				new String[][]{
					new String[]{"domain","domain"}
				}
		);
		propTag(props,xml,2,new String[]{"generator"},"generator",null);
		propTag(props,xml,2,new String[]{"docs"},"docs",null);
		propTag(props,xml,2,new String[]{"cloud"},"cloud",
				new String[][]{
				new String[]{"domain","domain"},
				new String[]{"port","port"},
				new String[]{"path","path"},
				new String[]{"registerProcedure","registerProcedure"},
				new String[]{"protocol","protocol"}
				}
		);
		propTag(props,xml,2,new String[]{"ttl"},"ttl",null);
		propTag(props,xml,2,new String[]{"image"},"image",
				new String[][]{
				new String[]{"url","url"},
				new String[]{"title","title"},
				new String[]{"link","link"},
				new String[]{"width","width"},
				new String[]{"height","height"},
				new String[]{"description","description"}
				}
		,true);
		propTag(props,xml,2,new String[]{"textInput"},"textInput",
				new String[][]{
				new String[]{"title","title"},
				new String[]{"description","description"},
				new String[]{"name","name"},
				new String[]{"link","link"}
				}
		,true);
		propTag(props,xml,2,new String[]{"skipHours"},"skipHours",null);
		propTag(props,xml,2,new String[]{"skipDays"},"skipDays",null);
		
		// items
		for(int row=1;row<=rows;row++) {
			append(xml,2,"<item>");
			
			tag(xml,3,new Pair("title",(query.getAt(FeedQuery.TITLE, row, null))),null,true,false);
			tag(xml,3,new Pair("description",(query.getAt(FeedQuery.CONTENT, row, null))),null,true,false);
			tag(xml,3,new Pair("link",query.getAt(FeedQuery.RSSLINK, row, null)),null,false,false);
			tag(xml,3,new Pair("author",query.getAt(FeedQuery.AUTHOREMAIL, row, null)),null,false,false);
			tag(xml,3,
					new Pair("category",query.getAt(FeedQuery.CATEGORYLABEL, row, null)),
					new Pair[]{
						new Pair("domain",query.getAt(FeedQuery.CATEGORYSCHEME, row, null))
					},false,splitString
			);
			tag(xml,3,new Pair("comments",query.getAt(FeedQuery.COMMENTS, row, null)),null,false,false);
			tag(xml,3,
					new Pair("enclosure",null),
					new Pair[]{
						new Pair("url",query.getAt(FeedQuery.LINKHREF, row, null)),
						new Pair("length",query.getAt(FeedQuery.LINKLENGTH, row, null)),
						new Pair("type",query.getAt(FeedQuery.LINKTYPE, row, null))
					},false,splitString
			);
			tag(xml,3,
					new Pair("guid",query.getAt(FeedQuery.ID, row, null)),
					new Pair[]{
						new Pair("isPermaLink",query.getAt(FeedQuery.IDPERMALINK, row, null))
					},false,splitString
			);
			tag(xml,3,new Pair("pubDate",query.getAt(FeedQuery.PUBLISHEDDATE, row, null)),null,false,splitString);
			tag(xml,3,
					new Pair("source",query.getAt(FeedQuery.SOURCE, row, null)),
					new Pair[]{
						new Pair("url",query.getAt(FeedQuery.SOURCEURL, row, null))
					},false,false
			);
			
			
			
			
			
			append(xml,2,"</item>");
		}
		
		append(xml,1,"</channel>");
		append(xml,0,"</rss>");
		
	}
	
	private void propTag(Struct props,StringBuffer xml, int count,String[] srcNames,String trgName,String[][] attrNames) throws PageException {
		propTag(props, xml, count, srcNames, trgName, attrNames,false);
	}
		
	
	private void propTag(Struct props,StringBuffer xml, int count,String[] srcNames,String trgName,String[][] attrNames, boolean childrenAsTag) throws PageException {
		Object value;
		for(int i=0;i<srcNames.length;i++) {
			value=props.get(srcNames[i], null);
			
			if(value instanceof Array){
				Array arr = (Array)value;
				int size = arr.size();
				for(int y=1;y<=size;y++) {
					propTag(xml, count, arr.get(y, null), trgName, attrNames, childrenAsTag);
				}
				break;
			}
			if(value !=null)	{
				propTag(xml, count, value, trgName, attrNames, childrenAsTag);
				break;
			}
		}
	}
	
	private boolean propTag(StringBuffer xml, int count,Object value,String trgName,String[][] attrNames, boolean childrenAsTag) throws PageException {
		if(!StringUtil.isEmpty(value)){
			Pair[] attrs;
			if(value instanceof Struct && attrNames!=null) {
				Struct sct = (Struct)value;
				Object attrValue;
				ArrayList al=new ArrayList();
				for(int i=0;i<attrNames.length;i++) {
					attrValue=sct.get(attrNames[i][0], null);
					if(attrValue!=null) {
						al.add(new Pair(attrNames[i][1],attrValue));
					}
				}
				attrs=(Pair[]) al.toArray(new Pair[al.size()]);
			}
			else attrs=null;
			tag(xml,count,new Pair(trgName,FeedQuery.getValue(value)),attrs,false,false,childrenAsTag);
			return true;
		}
		return false;
		
	}
	private void tag(StringBuffer xml, int count, Pair tag, Pair[] attrs,boolean required,boolean splitString) throws PageException {
		tag(xml, count, tag, attrs, required, splitString,false);
	}
	
	private void tag(StringBuffer xml, int count, Pair tag, Pair[] attrs,boolean required,boolean splitString, boolean childrenAsTag) throws PageException {
		if(!required && StringUtil.isEmpty(tag.getValue())){
			if(attrs==null || attrs.length==0)return;
			int c=0;
			for(int i=0;i<attrs.length;i++){
				if(!StringUtil.isEmpty(attrs[i].getValue())) c++;
				
			}
			if(c==0)return;
		}
		
		if(tag.getValue() instanceof Array){
			Array arr=(Array) tag.getValue();
			int len=arr.size();
			for(int i=1;i<=len;i++) {
				_tag(xml, tag.getName(),arr.get(i, null), attrs, count, i,false,childrenAsTag);
			}
			return;
			
		}
		if(splitString && tag.getValue() instanceof String)	{
			String strValue=(String) tag.getValue();
			Array arr = List.listToArray(strValue, ',');
			if(arr.size()>1){
				int len=arr.size();
				for(int i=1;i<=len;i++) {
					_tag(xml, tag.getName(),arr.get(i, null), attrs, count, i,true,childrenAsTag);
				}
				return;
			}
		}
		_tag(xml, tag.getName(), tag.getValue(), attrs, count, 0,false,childrenAsTag);
		
		
	}
	
	private void _tag(StringBuffer xml, String tagName,Object tagValue, Pair[] attrs,int count, int index, boolean splitString, boolean childrenAsTag) throws PageException {
		for(int i=0;i<count;i++)xml.append("\t");
		xml.append('<');
		xml.append(tagName);
		
		Object attrValue;
		if(attrs!=null && !childrenAsTag){
			for(int i=0;i<attrs.length;i++){
				attrValue=attrs[i].getValue();
				if(index>0){
					if(attrValue instanceof Array)attrValue=((Array)attrValue).get(index,null);
					else if(splitString && attrValue instanceof String){
						Array arr = List.listToArray((String)attrValue, ',');
						attrValue=arr.get(index,null);
					}
				}
				if(StringUtil.isEmpty(attrValue)) continue;
				
				xml.append(' ');
				xml.append(attrs[i].getName());
				xml.append("=\"");
				xml.append(XMLUtil.escapeXMLString(toString(attrValue)));
				xml.append("\"");
			}
		}
		
		xml.append('>');
			xml.append(toString(tagValue));
			if(attrs!=null && attrs.length>0 && childrenAsTag){
				xml.append('\n');
				for(int i=0;i<attrs.length;i++){
					attrValue=attrs[i].getValue();
					if(index>0){
						if(attrValue instanceof Array)attrValue=((Array)attrValue).get(index,null);
						else if(splitString && attrValue instanceof String){
							Array arr = List.listToArray((String)attrValue, ',');
							attrValue=arr.get(index,null);
						}
					}
					if(StringUtil.isEmpty(attrValue)) continue;

					for(int y=0;y<count+1;y++)xml.append("\t");
					xml.append('<');
					xml.append(attrs[i].getName());
					xml.append('>');
					//xml.append(XMLUtil.escapeXMLString(toString(attrValue)));
					xml.append(toString(attrValue));
					xml.append("</");
					xml.append(attrs[i].getName());
					xml.append(">\n");
				}
				for(int y=0;y<count;y++)xml.append("\t");
			}
			
			
			
			
		xml.append("</");
		xml.append(tagName);
		xml.append(">\n");	

	}
	
	
	private String toString(Object value) throws PageException {
		if(Decision.isDateAdvanced(value, false)) return GetHttpTimeString.invoke(Caster.toDatetime(value, pageContext.getTimeZone()));
		 return XMLUtil.escapeXMLString(Caster.toString(value));
	}
	
	private static void append(StringBuffer xml, int count, String value) {
		for(int i=0;i<count;i++)xml.append("\t");
		xml.append(value);
		xml.append("\n");
	}
	
	private void doActionRead() throws IOException, SAXException, PageException {
		required("Feed", "read", "source", source);

		if(outputFile!=null && outputFile.exists() && !overwrite)
			throw new ApplicationException("outputFile file ["+outputFile+"] already exists");
		
		
		String charset=null;
		
// plain output
		// xmlVar
		if(outputFile!=null) {
			IOUtil.copy(source, outputFile);
		}
		// outputFile
		String strFeed=null;
		if(!StringUtil.isEmpty(xmlVar)) {
			strFeed = IOUtil.toString(outputFile!=null?outputFile:source,charset);
			pageContext.setVariable(xmlVar, strFeed);
		}
		// Input Source
		InputSource is=null;
		Reader r=null;
		if(strFeed!=null)is=new InputSource(new StringReader(strFeed));
		else if(outputFile!=null)is=new InputSource(r=IOUtil.getReader(outputFile, charset));
		else is=new InputSource(r=IOUtil.getReader(source, charset));
		is.setSystemId(source.getPath());
		
		try{
			FeedHandler feed=new FeedHandler(source);
			Struct data = feed.getData();
			//print.e(data.keys());
			//print.e(data);
			// properties
			if(properties!=null) {
				String strProp = Caster.toString(properties,null);
				if(strProp==null)throw new ApplicationException("attribute [properties] should be of type string");
				pageContext.setVariable(strProp, FeedProperties.toProperties(data));
			}
			
			// query or enclosure
			railo.runtime.type.Query qry=null;
			if(query!=null || enclosureDir!=null){
				qry=FeedQuery.toQuery(data,feed.hasDC());
			}
			
			// query
			if(query!=null) {
				String strQuery = Caster.toString(query,null);
				if(strQuery==null)throw new ApplicationException("attribute [query] should be of type string");
				pageContext.setVariable(strQuery, qry);
			}
			// enclosure
			
			if(enclosureDir!=null) {
				int rows=qry.getRowCount();
				String strUrl=null;
				Resource src,dest;
				for(int row=1;row<=rows;row++) {
					strUrl=Caster.toString(qry.getAt(FeedQuery.LINKHREF, row, null),null);
					if(!StringUtil.isEmpty(strUrl)){
						src=ResourceUtil.toResourceNotExisting(pageContext, strUrl);
						dest=enclosureDir.getRealResource(src.getName());
						if(!ignoreEnclosureError && !overwriteEnclosure && dest.exists())
							throw new ApplicationException("enclosure file ["+dest+"] already exists");
						try {
						IOUtil.copy(src, dest);
						}
						catch(IOException ioe) {
							if(!ignoreEnclosureError)throw ioe;
						}
					}
				}
			}
			
			// name
			if(name!=null) {
				String strName = Caster.toString(name,null);
				if(strName==null)throw new ApplicationException("attribute [name] should be of type string");
				pageContext.setVariable(strName, data);
			}
			
			
			
		}
		finally {
			IOUtil.closeEL(r);
		}
	}
}
