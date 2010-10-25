package railo.runtime.tag;


import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.pdf.PDFDocument;
import railo.commons.pdf.PDFException;
import railo.commons.pdf.PDFPageMark;
import railo.runtime.Info;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.TemplateException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.op.Caster;
import railo.runtime.text.pdf.PDFUtil;
import railo.runtime.type.ReadOnlyStruct;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

public final class Document extends BodyTagImpl {

	
	private static final String STYLE_BG_INVISIBLE = "background-color: transparent; background-image: none;";
	

	
	private Resource filename=null;
	private boolean overwrite=false;
	private String name=null;
	private Dimension pagetype=PDFDocument.PAGETYPE_LETTER;
	private double pageheight=0;
	private double pagewidth=0;
	private boolean isLandscape=false;

	
	private double unitFactor=PDFDocument.UNIT_FACTOR_IN;
	private int encryption=PDFDocument.ENC_NONE;

	private String ownerpassword=null;
	private String userpassword="empty";
	private int scale=-1;

	// TODO impl. tag Document backgroundvisible,fontembed,scale
	private boolean backgroundvisible;
	private int fontembed=PDFDocument.FONT_EMBED_YES;
	
	private int permissions=0;
	private PDFDocument _document;



	private ArrayList documents=new ArrayList(); 
	
	public Document() {
		this._document=null;
	}
	
	/**
	 * @see railo.runtime.ext.tag.BodyTagImpl#release()
	 */
	public void release() {
		super.release();
		filename=null;
		overwrite=false;
		name=null;
		pagetype=PDFDocument.PAGETYPE_LETTER;
		pageheight=0;
		pagewidth=0;
		isLandscape=false;
		unitFactor=PDFDocument.UNIT_FACTOR_IN;
		encryption=PDFDocument.ENC_NONE;
		ownerpassword=null;
		userpassword="empty";
		permissions=0;
		scale=-1;
		documents.clear();
		_document=null;
		backgroundvisible=false;
		fontembed=PDFDocument.FONT_EMBED_YES;

		
		
	}
	
	private PDFDocument getDocument() {
		//SerialNumber sn = pageContext.getConfig().getSerialNumber();
		if(_document==null){
			_document=new PDFDocument();
		}
		return _document;
	}
	
	

	/** set the value proxyserver
	*  Host name or IP address of a proxy server.
	* @param proxyserver value to set
	**/
	public void setProxyserver(String proxyserver)	{
		getDocument().setProxyserver(proxyserver);
	}
	public void setProxyhost(String proxyserver)	{
		getDocument().setProxyserver(proxyserver);
	}
	
	/** set the value proxyport
	*  The port number on the proxy server from which the object is requested. Default is 80. When 
	* 	used with resolveURL, the URLs of retrieved documents that specify a port number are automatically 
	* 	resolved to preserve links in the retrieved document.
	* @param proxyport value to set
	**/
	public void setProxyport(double proxyport)	{
		getDocument().setProxyport((int)proxyport);
	}

	/** set the value username
	*  When required by a proxy server, a valid username.
	* @param proxyuser value to set
	**/
	public void setProxyuser(String proxyuser)	{
		getDocument().setProxyuser(proxyuser);
	}

	/** set the value password
	*  When required by a proxy server, a valid password.
	* @param proxypassword value to set
	**/
	public void setProxypassword(String proxypassword)	{
		getDocument().setProxypassword(proxypassword);
	}

	public void setSaveasname(String saveAsName) {
		// TODO impl
	}
	
	/**
	 * @param authUser the authUser to set
	 */
	public void setAuthuser(String authUser) {
		getDocument().setAuthUser(authUser);
	}

	/**
	 * @param authPassword the authPassword to set
	 */
	public void setAuthpassword(String authPassword) {
		getDocument().setAuthPassword(authPassword);
	}

	/**
	 * @param userAgent the userAgent to set
	 */
	public void setUseragent(String userAgent) {
		getDocument().setUserAgent(userAgent);
	}
	
	/**
	 * @param format the format to set
	 * @throws ApplicationException 
	 */
	public void setFormat(String format) throws ApplicationException {
		format = StringUtil.toLowerCase(format.trim());
		if(!"pdf".equals(format))
			throw new ApplicationException("invalid format ["+format+"], only the following format is supported [pdf]");
	}

	/**
	 * @param filename the filename to set
	 * @throws PageException 
	 */
	public void setFilename(String filename) throws PageException {
		this.filename = ResourceUtil.toResourceNotExisting(pageContext, filename);
		pageContext.getConfig().getSecurityManager().checkFileLocation(this.filename);
	}

	/**
	 * @param overwrite the overwrite to set
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param pagetype the pagetype to set
	 * @throws ApplicationException 
	 */
	public void setPagetype(String strPagetype) throws ApplicationException {
		strPagetype=StringUtil.toLowerCase(strPagetype.trim());
		if("legal".equals(strPagetype))			pagetype=PDFDocument.PAGETYPE_LEGAL;
		else if("letter".equals(strPagetype))	pagetype=PDFDocument.PAGETYPE_LETTER;
		else if("a4".equals(strPagetype))		pagetype=PDFDocument.PAGETYPE_A4;
		else if("a5".equals(strPagetype))		pagetype=PDFDocument.PAGETYPE_A5;
		else if("b4".equals(strPagetype))		pagetype=PDFDocument.PAGETYPE_B4;
		else if("b5".equals(strPagetype))		pagetype=PDFDocument.PAGETYPE_B5;
		else if("b4-jis".equals(strPagetype))	pagetype=PDFDocument.PAGETYPE_B4_JIS;
		else if("b4 jis".equals(strPagetype))	pagetype=PDFDocument.PAGETYPE_B4_JIS;
		else if("b4_jis".equals(strPagetype))	pagetype=PDFDocument.PAGETYPE_B4_JIS;
		else if("b4jis".equals(strPagetype))	pagetype=PDFDocument.PAGETYPE_B4_JIS;
		else if("b5-jis".equals(strPagetype))	pagetype=PDFDocument.PAGETYPE_B5_JIS;
		else if("b5 jis".equals(strPagetype))	pagetype=PDFDocument.PAGETYPE_B5_JIS;
		else if("b5_jis".equals(strPagetype))	pagetype=PDFDocument.PAGETYPE_B5_JIS;
		else if("b5jis".equals(strPagetype))	pagetype=PDFDocument.PAGETYPE_B5_JIS;
		else if("custom".equals(strPagetype))	pagetype=PDFDocument.PAGETYPE_CUSTOM;
		else throw new ApplicationException("invalid page type ["+strPagetype+"], valid page types are [legal,letter,a4,a5,b4,b5,b4-jis,b5-jis,custom]");
		
		
	}

	/**
	 * @param pageheight the pageheight to set
	 * @throws ApplicationException 
	 */
	public void setPageheight(double pageheight) throws ApplicationException {
		if(pageheight<1) throw new ApplicationException("pageheight must be a positive number");
		this.pageheight = pageheight;
	}

	/**
	 * @param pagewidth the pagewidth to set
	 * @throws ApplicationException 
	 */
	public void setPagewidth(double pagewidth) throws ApplicationException {
		if(pagewidth<1) throw new ApplicationException("pagewidth must be a positive number");
		this.pagewidth = pagewidth;
	}

	/**
	 * @param orientation the orientation to set
	 * @throws ApplicationException 
	 */
	public void setOrientation(String strOrientation) throws ApplicationException {
		strOrientation=StringUtil.toLowerCase(strOrientation.trim());
		if("portrait".equals(strOrientation))		isLandscape=false;
		else if("landscape".equals(strOrientation))	isLandscape=true;
		else throw new ApplicationException("invalid orientation ["+strOrientation+"], valid orientations are [portrait,landscape]");
		
	}

	/**
	 * @param marginbottom the marginbottom to set
	 */
	public void setMarginbottom(double marginbottom) {
		getDocument().setMarginbottom(marginbottom);
	}

	/**
	 * @param marginleft the marginleft to set
	 */
	public void setMarginleft(double marginleft) {
		getDocument().setMarginleft(marginleft);
	}

	/**
	 * @param marginright the marginright to set
	 */
	public void setMarginright(double marginright) {
		getDocument().setMarginright(marginright);
	}

	/**
	 * @param margintop the margintop to set
	 */
	public void setMargintop(double margintop) {
		getDocument().setMargintop(margintop);
	}

	/**
	 * @param bookmark the bookmark to set
	 */
	public void setBookmark(boolean bookmark) {
		getDocument().setBookmark(bookmark);
	}

	public void setHtmlbookmark(boolean bookmark) {
		getDocument().setHtmlBookmark(bookmark);
	}

	/**
	 * @param localUrl the localUrl to set
	 */
	public void setLocalurl(boolean localUrl) {
		getDocument().setLocalUrl(localUrl);
	}

	/**
	 * @param unitFactor the unit to set
	 * @throws ApplicationException 
	 */
	public void setUnit(String strUnit) throws ApplicationException {
		strUnit=StringUtil.toLowerCase(strUnit.trim());
		if("in".equals(strUnit))		unitFactor=PDFDocument.UNIT_FACTOR_IN;
		else if("cm".equals(strUnit))	unitFactor=PDFDocument.UNIT_FACTOR_CM;
		else if("point".equals(strUnit))	unitFactor=PDFDocument.UNIT_FACTOR_POINT;
		else throw new ApplicationException("invalid unit ["+strUnit+"], valid units are [cm,in,point]");
	}

	/**
	 * @param encryption the encryption to set
	 * @throws ApplicationException 
	 */
	public void setEncryption(String strEncryption) throws ApplicationException {
		strEncryption=StringUtil.toLowerCase(strEncryption.trim());
		if("none".equals(strEncryption))			encryption=PDFDocument.ENC_NONE;
		else if("40-bit".equals(strEncryption))		encryption=PDFDocument.ENC_40BIT;
		else if("40bit".equals(strEncryption))		encryption=PDFDocument.ENC_40BIT;
		else if("40 bit".equals(strEncryption))		encryption=PDFDocument.ENC_40BIT;
		else if("40_bit".equals(strEncryption))		encryption=PDFDocument.ENC_40BIT;
		else if("128-bit".equals(strEncryption))	encryption=PDFDocument.ENC_128BIT;
		else if("128bit".equals(strEncryption))		encryption=PDFDocument.ENC_128BIT;
		else if("128 bit".equals(strEncryption))	encryption=PDFDocument.ENC_128BIT;
		else if("128_bit".equals(strEncryption))	encryption=PDFDocument.ENC_128BIT;
		else throw new ApplicationException("invalid encryption ["+strEncryption+"], valid encryption values are [none, 40-bit, 128-bit]");
	}

	/**
	 * @param ownerpassword the ownerpassword to set
	 * @throws ApplicationException 
	 */
	public void setOwnerpassword(String ownerpassword) {
		this.ownerpassword = ownerpassword;
	}

	/**
	 * @param userpassword the userpassword to set
	 */
	public void setUserpassword(String userpassword) {
		this.userpassword = userpassword;
	}

	/**
	 * @param permissions the permissions to set
	 * @throws PageException 
	 */
	public void setPermissions(String strPermissions) throws PageException {
		permissions=PDFUtil.toPermissions(strPermissions);
	}

	/**
	 * @param scale the scale to set
	 * @throws ApplicationException 
	 */
	public void setScale(double scale) throws ApplicationException {
		if(scale<0) throw new ApplicationException("scale must be a positive number");
		if(scale>100) throw new ApplicationException("scale must be a number less or equal than 100");
		this.scale = (int) scale;
	}

	/**
	 * @param src the src to set
	 * @throws ApplicationException 
	 */
	public void setSrc(String src) throws ApplicationException {
		try {
			getDocument().setSrc(src);
		} catch (PDFException e) {
			throw new ApplicationException(e.getMessage());
		}
	}

	/**
	 * @param srcfile the srcfile to set
	 * @throws PageException 
	 * @throws  
	 */
	public void setSrcfile(String strSrcfile) throws PageException {
		Resource srcfile = ResourceUtil.toResourceExisting(pageContext, strSrcfile);
		pageContext.getConfig().getSecurityManager().checkFileLocation(srcfile);
		try {
			getDocument().setSrcfile(srcfile);
		} catch (PDFException e) {
			throw new ApplicationException(e.getMessage());
		}
	}

	/**
	 * @param mimetype the mimetype to set
	 */
	public void setMimetype(String strMimetype) {
		getDocument().setMimetype(strMimetype);
		strMimetype = strMimetype.toLowerCase().trim();
	}
	

	public void setHeader(PDFPageMark header) {
		getDocument().setHeader(header);
	}

	public void setFooter(PDFPageMark footer) {
		getDocument().setFooter(footer);
	}
	

	public void setBackgroundvisible(boolean backgroundvisible) {
		this.backgroundvisible=backgroundvisible;
	}

	public void setFontembed(String fontembed) throws PDFException {
		Boolean fe=Caster.toBoolean(fontembed,null);
		if(fe==null) {
			fontembed=StringUtil.toLowerCase(fontembed.trim());
			if("selective".equals(fontembed))
				this.fontembed=PDFDocument.FONT_EMBED_SELECCTIVE;
			else throw new PDFException("invalid value for fontembed ["+fontembed+"], valid values for fontembed are [yes,no,selective]");
			
		}
		else if(fe.booleanValue())this.fontembed=PDFDocument.FONT_EMBED_YES;
		else this.fontembed=PDFDocument.FONT_EMBED_NO;
		getDocument().setFontembed(this.fontembed);
	}

	public void addPDFDocument(PDFDocument document) {
		// set proxy settings
		if(_document!=null)	{
			if(_document.hasProxy()) {
				document.setProxyserver(_document.getProxyserver());
				document.setProxyport(_document.getProxyport());
				document.setProxyuser(_document.getProxyuser());
				document.setProxypassword(_document.getProxypassword());
			}
			document.setBookmark(_document.getBookmark());
			document.setLocalUrl(_document.getLocalUrl());
		}
		
		
		documents.add(document);
	}

    /**
	* @throws PageException 
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		// SerialNumber sn = pageContext.getConfig().getSerialNumber();
	    //if(sn.getVersion()==SerialNumber.VERSION_COMMUNITY)
	    //    throw new SecurityException("no access to this functionality with the "+sn.getStringVersion()+" version of railo");

	    
	    ReadOnlyStruct cfdoc=new ReadOnlyStruct();
	    cfdoc.setEL("currentpagenumber", "{currentpagenumber}");
	    cfdoc.setEL("totalpagecount", "{totalpagecount}");
	    cfdoc.setEL("totalsectionpagecount", "{totalsectionpagecount}");
	    cfdoc.setEL("currentsectionpagenumber", "{currentsectionpagenumber}");
	    cfdoc.setReadOnly(true);
	    pageContext.variablesScope().setEL("cfdocument", cfdoc);

	    return EVAL_BODY_BUFFERED;
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	*/
	public void doInitBody()	{
		
	}
	
	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doAfterBody()
	*/
	public int doAfterBody()	{
		getDocument().setBody(bodyContent.getString());
		
		return SKIP_BODY;
	}
	
	/**
	 *
	 * @throws IOException 
	 * @throws InvalidParameterException 
	 * @see railo.runtime.ext.tag.TagImpl#doEndTag()
	 */
	public int doEndTag() throws PageException {
		try {
			_doEndTag();
		}
		catch (Exception e) {
			throw Caster.toPageException(e);
		}	
		return EVAL_PAGE;
	}
	
	public void _doEndTag() throws JspException, IOException, DocumentException { 
		// set root header/footer to sections
		boolean doBookmarks=false;
		boolean doHtmlBookmarks=false;
		if(_document!=null){
			PDFPageMark header = _document.getHeader();
			PDFPageMark footer = _document.getFooter();
			boolean hasHeader=header!=null;
			boolean hasFooter=footer!=null;
			if(hasFooter || hasHeader) {
				Iterator it = documents.iterator();
				PDFDocument doc;
				while(it.hasNext()){
					doc=(PDFDocument) it.next();
					if(hasHeader && doc.getHeader()==null) doc.setHeader(header);
					if(hasFooter && doc.getFooter()==null) doc.setFooter(footer);
				}
			}
			doBookmarks=_document.getBookmark();
			doHtmlBookmarks=_document.getHtmlBookmark();
		}
		
		
		if(filename!=null)  {
			if(filename.exists() && !overwrite)
	    		throw new ApplicationException("file ["+filename+"] already exist","to allow overwrite the resource, set attribute [overwrite] to [true]");
	    	
	    	OutputStream os= null;
	    	try {
	    		os= filename.getOutputStream();
				render(os,doBookmarks,doHtmlBookmarks);
			} 
	    	finally {
				IOUtil.closeEL(os);
			}
	    	
	    }
		else if(!StringUtil.isEmpty(name)) {
			render(null,doBookmarks,doHtmlBookmarks);
		}
	    else  {
	    	HttpServletResponse rsp = pageContext. getHttpServletResponse();
		    if(rsp.isCommitted())
	            throw new ApplicationException("content ist already flushed","you can't rewrite head of response after the page is flushed");
	        rsp.setContentType("application/pdf");
	    
	    	
	    	OutputStream os=getOutputStream();   
		    try {
		    	render(os,doBookmarks,doHtmlBookmarks);
		    } 
		    finally {
		    	IOUtil.flushEL(os);
		        IOUtil.closeEL(os);
		        ((PageContextImpl)pageContext).getRootOut().setClosed(true);
		    }
		    throw new railo.runtime.exp.Abort(railo.runtime.exp.Abort.SCOPE_REQUEST);
	    }
       
	}



	private void render(OutputStream os, boolean doBookmarks, boolean doHtmlBookmarks) throws IOException, PageException, DocumentException {
		byte[] pdf=null;
		// merge multiple docs to 1
		if(documents.size()>1) {
			PDFDocument[] pdfDocs=new PDFDocument[documents.size()];
			PdfReader[] pdfReaders = new PdfReader[pdfDocs.length];
			Iterator it = documents.iterator();
			int index=0;
			// generate pdf with pd4ml
			while(it.hasNext()) {
				pdfDocs[index]=(PDFDocument)it.next();
				pdfReaders[index]=
					new PdfReader(pdfDocs[index].render(getDimension(),unitFactor,pageContext,doHtmlBookmarks));
				index++;
			}
			
			// collect together
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			com.lowagie.text.Document document = 
				new com.lowagie.text.Document(pdfReaders[0].getPageSizeWithRotation(1));
			PdfCopy copy = new PdfCopy(document,baos);
			document.open();
			String name;
			ArrayList bookmarks=doBookmarks?new ArrayList():null;
			try {
				int size,totalPage=0;
				Map parent;
				for(int doc=0;doc<pdfReaders.length;doc++) {
					size=pdfReaders[doc].getNumberOfPages();
					
					PdfImportedPage ip;
					
					// bookmarks
					if(doBookmarks) {
						name=pdfDocs[doc].getName();
						if(!StringUtil.isEmpty(name)) {
							bookmarks.add(parent=PDFUtil.generateGoToBookMark(name, totalPage+1));
						}
						else parent=null;
						
						if(doHtmlBookmarks) {
							java.util.List pageBM = SimpleBookmark.getBookmark(pdfReaders[doc]);
							if(pageBM!=null) {
								if(totalPage>0)SimpleBookmark.shiftPageNumbers(pageBM, totalPage, null);
								if(parent!=null)PDFUtil.setChildBookmarks(parent,pageBM);
								else bookmarks.addAll(pageBM);
							}
						}
					}
					
					totalPage++;
					for(int page=1;page<=size;page++) {
						if(page>1)totalPage++;
						ip = copy.getImportedPage(pdfReaders[doc], page);
						
						//ip.getPdfDocument().setHeader(arg0);
						//ip.getPdfDocument().setFooter(arg0);
						copy.addPage(ip);
					}
				}
				if (doBookmarks && !bookmarks.isEmpty())copy.setOutlines(bookmarks);
			}
			finally {
				document.close();
			}
			pdf=baos.toByteArray();
		}
		else if(documents.size()==1){
			pdf=((PDFDocument)documents.get(0)).render(getDimension(),unitFactor,pageContext,doHtmlBookmarks);
		}
		else {
			pdf=getDocument().render(getDimension(),unitFactor,pageContext,doHtmlBookmarks);
		}
		
		// permission/encryption
		if(PDFDocument.ENC_NONE!=encryption) {
			PdfReader reader = new PdfReader(pdf);
			com.lowagie.text.Document document = new com.lowagie.text.Document(reader.getPageSize(1));
			document.addCreator("Railo "+Info.getVersionAsString()+" "+Info.getStateAsString());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfCopy copy = new PdfCopy(document,baos);
			//PdfWriter writer = PdfWriter.getInstance(document, pdfOut);
			copy.setEncryption(PDFDocument.ENC_128BIT==encryption , userpassword , ownerpassword , permissions);
			document.open();
			int size=reader.getNumberOfPages();
			for(int page=1;page<=size;page++) {
				copy.addPage(copy.getImportedPage(reader, page));
			}
			document.close();
			pdf=baos.toByteArray();
		}
		
		// write out
		if(os!=null)IOUtil.copy(new ByteArrayInputStream(pdf), os,true,false);
		if(!StringUtil.isEmpty(name)) {
			pageContext.setVariable(name,pdf);
		}
	}

	private OutputStream getOutputStream() throws PageException, IOException {
        try {
        	return ((PageContextImpl)pageContext).getServletOutputStream();
        } 
        catch(IllegalStateException ise) {
            throw new TemplateException("content is already send to user, flush");
        }
    }
	

	private Dimension getDimension() throws ApplicationException {
		// page size custom
		if(isCustom(pagetype)) {
			if(pageheight==0 || pagewidth==0)
				throw new ApplicationException("when attribute pagetype has value [custom], the attributes [pageheight, pagewidth] must have a positive numeric value");
			pagetype=new Dimension(PDFDocument.toPoint(pagewidth,unitFactor),PDFDocument.toPoint(pageheight,unitFactor));
		}
		// page orientation
		if(isLandscape)pagetype=new Dimension(pagetype.height, pagetype.width);
		return pagetype;
	}
	

	private boolean isCustom(Dimension d) throws ApplicationException {
		if(d.height<=0 || d.width<=0)
			throw new ApplicationException("if you define pagetype as custom, you have to define attribute pageheight and pagewith with a positive numeric value");
		
		
		return (d.width+d.height)==2;
	}
	
	/**
	 * sets if has body or not
	 * @param hasBody
	 */
	public void hasBody(boolean hasBody) {
	    
	}



}
	

