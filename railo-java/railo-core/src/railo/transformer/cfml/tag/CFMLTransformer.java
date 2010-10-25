package railo.transformer.cfml.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.Info;
import railo.runtime.SourceFile;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.cast.Cast;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.NullExpression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.PrintOut;
import railo.transformer.bytecode.statement.StatementBase;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.ExprTransformer;
import railo.transformer.cfml.attributes.AttributeEvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.evaluator.impl.ProcessingDirectiveException;
import railo.transformer.cfml.expression.SimpleExprTransformer;
import railo.transformer.cfml.script.CFMLScriptTransformer.ComponentBodyException;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.CustomTagLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibException;
import railo.transformer.library.tag.TagLibFactory;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;
import railo.transformer.util.CFMLString;


/**
 * Die Klasse CFMLTransformer ist das Herzst￼ck des ￜbersetzungsprozess, 
 * es liest die ￼bergebene CFML Datei ein und ￼bersetzt diese in ein valid (CFXD) XML Dokument 
 * in der Form eines org.w3c.dom.Document Object, 
 * die dann als weitere Vorlage zum ￼bersetzten in PHP dient.
 * Der CFMLTransformer ￼bersetzt nur die Tags die innerhalb einer CFML Seite vorkommen, 
 * nicht die Ausdr￼cke die innerhalb von Attributen und dem Body eines Tag vorkommen k￶nnen, 
 * f￼r dies ist der ExprTransformer zust￤ndig, 
 * der in der jeweiligen Tag Library definiert ist.
 * Der CFMLTransformer kann zwar durch seine Grammatik, 
 * Tags erkennen aber nicht validieren. 
 * Erst mithilfe der im zugeteilten Tag Libraries kann er vergleichen ob ein Tag nur 
 * ein normaler HTML Tag ist, das er einfach als literale Zeichenkette aufnimmt, 
 * oder ob es sich um einen Tag handelt der eine konkrete Anweisung implementiert. 
 * Die Tag Library definiert alle in CFML vorhanden Tags, 
 * deren individuelle Grammatik und deren Aufbau und Verhalten. 

 * <pre>
		Parser Grammatik nach EBNF (Extended Backus-Naur Form) 
		
		transform	= {body}
		body		= [comment] ("</" | "<" tag body | literal body);
		comment		= "<!---" {?-"--->"} "--->";
		literal		= ("<" | {?-"#"-"<"} "<" | {"#" expression "#"} "<" ) | ({?-"<"} "<")
				  (* Welcher Teil der "oder" Bedingung ausgef￼hrt wird, ist abh￤ngig was die Tag-Lib vorgibt, 
				     dass Expression geparst werden sollen oder nicht. *)
		tag		= name-space identifier spaces attributes ("/>" | ">" [body "</" identifier spaces ">"]);
				  (* Ob dem Tag ein Body und ein End-Tag folgt ist abh￤ngig von Definition des body-content in Tag-Lib, gleices gilt f￼r appendix *)
		name-space	= < tagLib[].getNameSpaceAndSeperator() >;
			          (* Vergleicht Zeichen mit den Namespacedefinitionen der Tag Libraries. *)
		attributes	= ({spaces attribute} "/>" | {spaces attribute} ">") | attribute-value;
				  (* Welcher Teil der "oder" Bedingung ausgef￼hrt wird, ist abh￤ngig von der Tag Attribute Definition in der Tag Lib. *)
		attribute	= attribute-name  spaces "=" spaces attribute-value;
		attribute-name	= ("expression"|'expression'|expression) | identifier;
			          (* Ruft identifier oder den Expression Transformer auf je nach Attribute Definition in der Tag Lib. *)
		attribute-value	= expression; 
		identifier     	= (letter | "_") {letter | "_"|digit};
		letter			= "a".."z"|"A".."Z";
		digit			= "0".."9";
		expression      = <ExprTransfomer.expression()>; (* Ruft den Expression Transformer auf. *)
		spaces         = {space};
		space          = "\s"|"\t"|"\f"|"\t"|"\n";
		
		{"x"}= 0 bis n mal "x"
		["x"]= 0 bis 1 mal "x"
		("x" | "y")"z" = "xz" oder "yz"
</pre>
 * 
 *
 */
public final class CFMLTransformer {
	
    public static short TAG_LIB_GLOBAL=0;
    public static short TAG_LIB_PAGE=1;
    
    public class Data {
		
		private TagLib[][] tlibs;//=new TagLib[][]{null,new TagLib[0]};
		private FunctionLib[] flibs;
		private CFMLString cfml;
		private EvaluatorPool ep=new EvaluatorPool();
		private SimpleExprTransformer set;
	    private Config config;
	 
	    public Data(TagLib[][] tlibs, FunctionLib[] flibs, CFMLString cfml,Config config) {
			super();
			this.tlibs = tlibs;
			this.flibs = flibs;
			this.cfml = cfml;
			this.config = config;
		}
    }
	
	/**
	 * Startmethode zum transfomieren einer CFML Datei.
	 * <br />
	 * EBNF:<br />
	 * <code>{body}</code>
	 * @param config
	 * @param sf CFML File
	 * @param tlibs Tag Library Deskriptoren, nach denen innerhalb der CFML Datei gepr￼ft werden soll.
	 * @param flibs Function Library Deskriptoren, nach denen innerhalb der Expressions der CFML Datei gepr￼ft werden soll.
	 * @return ￜbersetztes CFXD Dokument Element.
	 * @throws TemplateException
	 * @throws IOException
	 */
	public Page transform(ConfigImpl config,SourceFile sf, TagLib[] tlibs, FunctionLib[] flibs) throws TemplateException, IOException	{
		Page p;
		CFMLString cfml;
		String charset;
		boolean writeLog;
		
		writeLog=config.getExecutionLogEnabled();
		charset=config.getTemplateCharset();
		
		
		
		while(true){
			try {
				cfml=new CFMLString(sf,charset,writeLog);
				p = transform(config,cfml,tlibs,flibs,sf.getFile().lastModified());
				break;
			}
			catch(ProcessingDirectiveException pde) {
				writeLog=pde.getWriteLog();
				charset=pde.getCharset();
			}
		}
		
		// if cfc has no component tag or is script without cfscript
		if(p.isPage() && ResourceUtil.getExtension(sf.getFile()).equalsIgnoreCase(config.getCFCExtension())){
			cfml.setPos(0);
			TagLibTag tlt;
			CFMLString original = cfml; 
			
			// try inside a cfscript
			tlt = CFMLTransformer.getTLT(original,"script");
			String text="<"+tlt.getFullName()+">"+original.getText()+"</"+tlt.getFullName()+">";
			cfml=new CFMLString(text,charset,writeLog,sf);
			
			try {
				while(true){
					if(cfml==null){
						cfml=new CFMLString(sf,charset,writeLog);
						text="<"+tlt.getFullName()+">"+cfml.getText()+"</"+tlt.getFullName()+">";
						cfml=new CFMLString(text,charset,writeLog,sf);
					}
					try {
						p= transform(config,cfml,tlibs,flibs,sf.getFile().lastModified());
						break;
					}
					catch(ProcessingDirectiveException pde) {
						writeLog=pde.getWriteLog();
						charset=pde.getCharset();
						cfml=null;
					}
				}
			}
			catch (ComponentBodyException e) {
				throw e.getTemplateException();
			}
			catch (TemplateException e) {
				//print.printST(e);
			}
			
			
			
			
			// try inside a component
			if(p.isPage()){
				tlt = CFMLTransformer.getTLT(original,"component");
				text="<"+tlt.getFullName()+">"+original.getText()+"</"+tlt.getFullName()+">";
				cfml=new CFMLString(text,charset,writeLog,sf);
						
				while(true){
					if(cfml==null){
						cfml=new CFMLString(sf,charset,writeLog);
						text="<"+tlt.getFullName()+">"+cfml.getText()+"</"+tlt.getFullName()+">";
						cfml=new CFMLString(text,charset,writeLog,sf);
					}
					try {
						p= transform(config,cfml,tlibs,flibs,sf.getFile().lastModified());
						break;
					}
					catch(ProcessingDirectiveException pde) {
						writeLog=pde.getWriteLog();
						charset=pde.getCharset();
						cfml=null;
					}
				}
			}
			
		}
		
		
		return p;
	}
	

	public static TagLibTag getTLT(CFMLString cfml,String name) throws TemplateException {
		TagLib tl;
		try {
			// this is already loaded, oherwise we where not here
			tl = TagLibFactory.loadFromSystem();
			return tl.getTag(name);
		} 
		catch (TagLibException e) {
			throw new TemplateException(cfml,e);
		}
	}
	

	/**
	 * Startmethode zum transfomieren einer CFMLString.
	 * <br />
	 * EBNF:<br />
	 * <code>{body}</code>
	 * @param config
	 * @param cfml CFMLString
	 * @param tlibs Tag Library Deskriptoren, nach denen innerhalb der CFML Datei gepr￼ft werden soll.
	 * @param flibs Function Library Deskriptoren, nach denen innerhalb der Expressions der CFML Datei gepr￼ft werden soll.
	 * @param sourceLastModified 
	 * @return ￜbersetztes CFXD Dokument Element.
	 * @throws TemplateException
	 */
	public Page transform(ConfigImpl config,CFMLString cfml,TagLib[] tlibs,FunctionLib[] flibs, long sourceLastModified) throws TemplateException {
		
		TagLib[][] _tlibs=new TagLib[][]{null,new TagLib[0]};
		_tlibs[TAG_LIB_GLOBAL]=tlibs;
		// reset page tlds
		if(_tlibs[TAG_LIB_PAGE].length>0) {
			_tlibs[TAG_LIB_PAGE]=new TagLib[0];
		}
		Data data = new Data(_tlibs,flibs,cfml,config);
		
		

		SourceFile source=data.cfml.getSourceFile(); 
		
		Page page=new Page(source.getPhyscalFile().getAbsolutePath(),source.getFullClassName(),Info.getFullVersionInfo(),sourceLastModified,cfml.getWriteLog());
		//Body body=page;
		try {
			do {
				
				body(data,page,false,null);
				
				if(data.cfml.isAfterLast()) break;
				if(data.cfml.forwardIfCurrent("</")){
					TagLib tagLib=nameSpace(data);
					if(tagLib==null){
						page.addPrintOut("</", data.cfml.getLine());
					}
					else {
						throw new TemplateException(cfml,"no matching start tag for end tag ["+tagLib.getNameSpaceAndSeparator()+identifier(data.cfml,true)+"]");
			
					}
				}
				else 
					throw new TemplateException(cfml,"Error while transforming CFML File");
			}while(true);
			
			// call-back of evaluators
			data.ep.run();
			
			return page;
		}
		catch(TemplateException e) {
		    data.ep.clear();
		    /*if(e instanceof ProcessingDirectiveException) throw e;
		    throw new TemplateException(
		    		"\n-----------------------------------------------------\n"+
		    		"line:"+e.getLine()+"\n"+
		    		"message:"+e.getMessage()+"\n"+
		    		data.cfml.toString()+"\n-----------------------------------------------------\n");
		    */throw e;
		}
	}

	/**
	 * Liest den Body eines Tag ein. Kommentare, Tags und Literale inkl. Expressions.
	 * <br />
	 * EBNF:<br />
	 * <code>[comment] ("</" | "<" tag body | literal body);</code>
	 * @param body CFXD Body Element dem der Inhalt zugeteilt werden soll.
	 * @param parseExpression Definiert ob Expressions innerhalb von Literalen ￼bersetzt werden sollen oder nicht.
	 * @param transformer Expression Transfomer zum ￼bersetzten von Expression.
	 * @throws TemplateException
	 */
	private void body(Data data,Body body, boolean parseExpression, ExprTransformer transformer) throws TemplateException {
		boolean parseLiteral=true;
		
	// Comment 
        comment(data.cfml,false);
	// Tag
		// is Tag Beginning
		if(data.cfml.isCurrent('<'))	{
			// return if end tag and inside tag
			if(data.cfml.isNext('/'))	{
			    //railo.print.ln("early return");
				return;
			}
			parseLiteral=!tag(data,body,parseExpression);
		}
		// no Tag
		if(parseLiteral) {
			literal(data,body, parseExpression, transformer);
		}
		// not at the end 
		if(data.cfml.isValidIndex()) 
			body(data,body,parseExpression, transformer);
	}
	
	/**
	 * Liest einen Kommentar ein, Kommentare werden nicht in die CFXD ￼bertragen sondern verworfen.
	 * Komentare k￶nnen auch Kommentare enthalten.
	 * <br />
	 * EBNF:<br />
	 * <code>"<!---" {?-"--->"} "--->";</code>
	 * @throws TemplateException
	 */

	public static void comment(CFMLString cfml,boolean removeSpace) throws TemplateException {
		if(!removeSpace) {
			comment(cfml);
		}
		else {
			cfml.removeSpace();
			if(comment(cfml))cfml.removeSpace();
		}
		
	}
	
	public static boolean comment(CFMLString cfml) throws TemplateException {
		if(!cfml.forwardIfCurrent("<!---"))
			return false;
		
		int start=cfml.getPos();
		short counter=1;
		while(true) {
			if(cfml.isAfterLast()) {
				cfml.setPos(start);
				throw new TemplateException(cfml,"no end comment found");
			}
			else if(cfml.forwardIfCurrent("<!---")) {
				counter++;
			}
			else if(cfml.forwardIfCurrent("--->")) {
				if(--counter==0) {
					comment(cfml);
					return true;
				}
			}
			else {
				cfml.next();
			}
		}
	}



	/**
	 * Liest Literale Zeichenketten ein die sich innerhalb und auserhalb von tgas befinden, 
	 * beim Einlesen wird unterschieden ob Expression geparsst werden m￼ssen oder nicht, 
	 * dies ist abh￤ngig, von der Definition des Tag in dem man sich allenfalls befindet, innerhalb der TLD.
	 * @param parent ￼bergeordnetes Element.
	 * @param parseExpression Definiert on Expressions geparset werden sollen oder nicht.
	 * @param transformer Expression Transfomer zum ￼bersetzen der Expressions innerhalb des Literals.
	 * @throws TemplateException
	 * 
	 * <br />
	 * EBNF:<br />
	 * <code>("<" | {?-"#"-"<"} "<" | {"#" expression "#"} "<" ) | ({?-"<"} "<")
			(* Welcher Teil der "oder" Bedingung ausgef￼hrt wird, ist abh￤ngig ob die Tag-Lib vorgibt, 
			 dass Expression geparst werden sollen oder nicht. *)</code>
	 */
	private void literal(Data data,Body parent,boolean parseExpression, ExprTransformer transformer) throws TemplateException {
		// with expression
		if(parseExpression) {
			if(data.cfml.isAfterLast())return;
            // data.cfml.getCurrent()
			StringBuffer text=new StringBuffer();
			int count=0;
			while(data.cfml.isValidIndex()) {
				count++;
				// #
				if(data.cfml.isCurrent('#')) {
					data.cfml.next();
					if(data.cfml.isCurrent('#')) {
						text.append('#');
					}
					else {
						if(text.length()>0)	{
							parent.addPrintOut(text.toString(),-1);
							text=new StringBuffer();
						}
                        int line=data.cfml.getLine();
						parent.addStatement(new PrintOut(transformer.transform(data.ep,data.flibs,data.cfml),line));
							
						if(!data.cfml.isCurrent('#'))
							throw new TemplateException(data.cfml,"missing terminating [#] for expression");
					}
				}
				else if(data.cfml.isCurrent('<') && count>1) {
					break;
				}
				else
					text.append(data.cfml.getCurrent());
			data.cfml.next();
			}
			if(text.length()>0)parent.addPrintOut(text.toString(), -1);
		}
		// no expression
		else {
			int start=data.cfml.getPos();
			data.cfml.next();
			int end=data.cfml.indexOfNext('<');
			String text;
			if(end==-1) {
				text=data.cfml.substring(start);
				data.cfml.setPos(data.cfml.length());
			}
			else {
				text=data.cfml.substring(start,end-start);
				data.cfml.setPos(end);
			}
			parent.addPrintOut(text, -1);
			
		}
	}

	/**
	 * Liest einen Tag ein, pr￼ft hierbei ob das Tag innerhalb einer der geladenen Tag-Lib existiert, 
	 * ansonsten wird ein Tag einfach als literal-string aufgenommen.
	 * <br />
	 * EBNF:<br />
	 * <code>name-space identifier spaces attributes ("/>" | ">" [body "</" identifier spaces ">"]);(* Ob dem Tag ein Body und ein End-Tag folgt ist abh￤ngig von Definition des body-content in Tag-Lib, gleices gilt f￼r appendix *)</code>
	 * @param parent ￼bergeornetes Tag
	 * @param parseExpression sollen Expresson innerhalb des Body geparste werden oder nicht.
	 * @return Gibt zur￼ck ob es sich um ein Tag as einer Tag-Lib handelte oder nicht.
	 * @throws TemplateException
	 */
	private boolean tag(Data data,Body parent,boolean parseExpression) throws TemplateException {
	    //railo.print.ln("--->"+data.cfml.getCurrent());
	    boolean hasBody=false;
		
		int line=data.cfml.getLine();
		//int column=data.cfml.getColumn();
		int start=data.cfml.getPos();
		data.cfml.next();
		
		// read in namesapce of tag
		TagLib tagLib=nameSpace(data);
		
		// return if no matching tag lib
		if(tagLib==null)	{
			data.cfml.previous();
			return false;
		}
				
		// Get matching tag from tag lib
		String strNameNormal=identifier(data.cfml,false);
		if(strNameNormal==null) {
			data.cfml.setPos((data.cfml.getPos()-tagLib.getNameSpaceAndSeparator().length())-1);
			return false;
		}
		
		String strName=strNameNormal.toLowerCase();
		String appendix=null;
		TagLibTag tagLibTag=tagLib.getTag(strName);
		
		// get taglib
		if(tagLibTag==null)	{
			tagLibTag=tagLib.getAppendixTag(strName);
			 if(tagLibTag==null)
				 throw new TemplateException(data.cfml,"undefined tag ["+tagLib.getNameSpaceAndSeparator()+strName+"]");
			appendix=StringUtil.removeStartingIgnoreCase(strNameNormal,tagLibTag.getName());
		 }
		
		// CFXD Element 
		Tag tag;
		try {
			tag = tagLibTag.getTag(line,data.cfml.getLine());
		} 
		catch (Exception e) {
			throw new TemplateException(data.cfml,e);
		}
		parent.addStatement(tag);
		
		// get tag from tag library
		 if(appendix!=null)	{
			tag.setAppendix(appendix);
			tag.setFullname(tagLibTag.getFullName().concat(appendix));
		 }
		 else {
			 tag.setFullname(tagLibTag.getFullName());		 
		 }
		 
		 
		tag.setTagLibTag(tagLibTag);
		comment(data.cfml,true);
		
		// Tag Translator Evaluator
		if(tagLibTag.hasTteClass())	{
			data.ep.add(tagLibTag,tag,data.flibs,data.cfml);
		}
		
		//get Attributes
		attributes(data,tagLibTag,tag);
		
		if(tagLibTag.hasAttributeEvaluator()) {
			try {
				tagLibTag.getAttributeEvaluator().evaluate(tagLibTag,tag);
			} catch (AttributeEvaluatorException e) {
				
				throw new TemplateException(data.cfml, e);
			}
		}
		
		
		
		// End of begin Tag
//		TODO muss erlaubt sein
		if(data.cfml.forwardIfCurrent('>'))	{
			hasBody=tagLibTag.getHasBody();
		}
		else if(data.cfml.forwardIfCurrent('/','>')) {
			if(tagLibTag.getHasBody())tag.setBody(new BodyBase());
		}
		else {
			throw new TemplateException(data.cfml, "tag ["+tagLibTag.getFullName()+"] is not closed");
		}
		

		// Body
		if(hasBody)	{

		    
			// get Body
			if(tagLibTag.isTagDependent()) {
				// get TagDependentBodyTransformer
				TagDependentBodyTransformer tdbt=null;
				try {
					tdbt=tagLibTag.getBodyTransformer();
				} catch (TagLibException e) {
					throw new TemplateException(data.cfml,e);
				}
				if(tdbt==null) throw new TemplateException(data.cfml,"Tag dependent body Transformer is invalid for Tag ["+tagLibTag.getFullName()+"]");
				tdbt.transform(this,data.ep,data.flibs,tag,tagLibTag,data.cfml);
				
				
				//	get TagLib of end Tag
				if(!data.cfml.forwardIfCurrent("</"))
					throw new TemplateException(data.cfml,"invalid construct");
				
				TagLib tagLibEnd=nameSpace(data);
				// same NameSpace
				if(!(tagLibEnd!=null && tagLibEnd.getNameSpaceAndSeparator().equals(tagLib.getNameSpaceAndSeparator())))
					throw new TemplateException(data.cfml,"invalid construct");
				// get end Tag
				String strNameEnd=identifier(data.cfml,true).toLowerCase();

				// not the same name Tag
				if(!strName.equals(strNameEnd)) {
					data.cfml.setPos(start);
					throw new TemplateException(data.cfml,"Start and End Tag has not the same Name ["+tagLib.getNameSpaceAndSeparator()+strName+"-"+tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"]");				
				 }
				 data.cfml.removeSpace();
				 if(!data.cfml.forwardIfCurrent('>'))
					 throw new TemplateException(data.cfml,"End Tag ["+tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"] not closed");
			}
			else {
				// get body of Tag
				Body body=new BodyBase();
					//parseExpression=(tagLibTag.getParseBody())?true:parseExpression;
				if(tagLibTag.getParseBody())parseExpression=true;
				
				while(true)	{
					
					// Load Expession Transformer from TagLib
					ExprTransformer transfomer=null;
					if(parseExpression) {
						try {
							transfomer = tagLibTag.getTagLib().getExprTransfomer();
						} catch (TagLibException e) {
							throw new TemplateException(data.cfml,e);
						}
					}
					

					// call body
				    body(data,body,parseExpression,transfomer);
				    
				    // no End Tag
					if(data.cfml.isAfterLast()) {
					    
						if(tagLibTag.isBodyReq()) {
						    data.cfml.setPos(start);
							throw new TemplateException(data.cfml,"No matching end tag found for tag ["+tagLibTag.getFullName()+"]");
						}
						body.moveStatmentsTo(parent);
						return executeEvaluator(data,tagLibTag, tag);
					}
					
					// Invalid Construct
					int posBeforeEndTag=data.cfml.getPos();
					if(!data.cfml.forwardIfCurrent('<','/'))
						throw new TemplateException(data.cfml,"Missing end tag for ["+tagLibTag.getFullName()+"]");
					
					// get TagLib of end Tag
					TagLib tagLibEnd=nameSpace(data);
					
					// same NameSpace
					if(tagLibEnd!=null)	{
					    String strNameEnd="";
					    //railo.print.ln(data.cfml.getLine()+" - "+data.cfml.getColumn()+" - "+tagLibEnd.getNameSpaceAndSeperator()+".equals("+tagLib.getNameSpaceAndSeperator()+")");
					    if(tagLibEnd.getNameSpaceAndSeparator().equals(tagLib.getNameSpaceAndSeparator())) {
					        
						    // get end Tag
							strNameEnd=identifier(data.cfml,true).toLowerCase();
							// not the same name Tag
							
							// new part
							data.cfml.removeSpace();
							if(strName.equals(strNameEnd)) {
							    if(!data.cfml.forwardIfCurrent('>'))
									throw new TemplateException(data.cfml,"End Tag ["+tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"] not closed");
								break;
							}
							
					    }
					    // new part
					    if(tagLibTag.isBodyReq()) {
							TagLibTag endTag = tagLibEnd.getTag(strNameEnd);
							if(endTag!=null && !endTag.getHasBody())
								throw new TemplateException(data.cfml,
										"End Tag ["+
										tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"] is not allowed, for this tag only a Start Tag is allowed");
							data.cfml.setPos(start);
							
							throw new TemplateException(data.cfml,
									"Start and End Tag has not the same Name ["+
									tagLib.getNameSpaceAndSeparator()+strName+"-"+tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"]");
						}
						body.moveStatmentsTo(parent);
						data.cfml.setPos(posBeforeEndTag);
						return executeEvaluator(data,tagLibTag, tag);
					    /// new part	
					}
					body.addPrintOut("</",data.cfml.getLine());
					
				}
				tag.setBody(body);
				
			}
		}
		if(tag instanceof StatementBase)
			((StatementBase)tag).setEndLine(data.cfml.getLine());
		// Tag Translator Evaluator
		
		return executeEvaluator(data,tagLibTag, tag);
        
	}
	
	private boolean executeEvaluator(Data data,TagLibTag tagLibTag, Tag tag) throws TemplateException {
		if(tagLibTag.hasTteClass())	{
			try {
				TagLib lib=tagLibTag.getEvaluator().execute(data.config,tag,tagLibTag,data.flibs,data.cfml);
				if(lib!=null) {
					// set
					for(int i=0;i<data.tlibs[TAG_LIB_PAGE].length;i++) {
		                if(data.tlibs[TAG_LIB_PAGE][i].getNameSpaceAndSeparator().equalsIgnoreCase(lib.getNameSpaceAndSeparator())){
		                	boolean extIsCustom=data.tlibs[TAG_LIB_PAGE][i] instanceof CustomTagLib;
		                	boolean newIsCustom=lib instanceof CustomTagLib;
		                	// TagLib + CustomTagLib (visa/versa)
		                	if(extIsCustom){
		                		((CustomTagLib)data.tlibs[TAG_LIB_PAGE][i]).append(lib);
		                		return true;
		                	}
		                	else if(newIsCustom){
		                		((CustomTagLib)lib).append(data.tlibs[TAG_LIB_PAGE][i]);
		                		data.tlibs[TAG_LIB_PAGE][i]=lib;
		                		return true;
		                	}
		                }
		            }
					
					// insert
		            TagLib[] newTlibs=new TagLib[data.tlibs[TAG_LIB_PAGE].length+1]; 
                    for(int i=0;i<data.tlibs[TAG_LIB_PAGE].length;i++) {
                        newTlibs[i]=data.tlibs[TAG_LIB_PAGE][i];
                    }
                    newTlibs[data.tlibs[TAG_LIB_PAGE].length]=lib;
                    data.tlibs[TAG_LIB_PAGE]=newTlibs;    
				}
			} 
            catch (EvaluatorException e) {
                throw new TemplateException(data.cfml,e);
            }
		}
		return true;
	}
	
	/**
	 * Vergleicht folgende Zeichen mit den Namespacedefinitionen der Tag Libraries,
	 * gibt eine Tag-Lib zur￼ck falls eine passt, ansonsten null. 
	 * <br />
	 * EBNF:<br />
	 * <code>< tagLib[].getNameSpaceAndSeperator() >(* Vergleicht Zeichen mit den Namespacedefinitionen der Tag Libraries. *) </code>
	 * @return TagLib Passende Tag Lirary oder null.
	 */
	private TagLib nameSpace(Data data) {
		boolean hasTag=false;
		int start = data.cfml.getPos();
		TagLib tagLib=null;
		
		// loop over NameSpaces
		for(int i=0;i<2;i++)	{
			for(int ii=0;ii<data.tlibs[i].length;ii++)	{
				tagLib= data.tlibs[i][ii];
				char[] c=tagLib.getNameSpaceAndSeperatorAsCharArray();
				// Loop over char of NameSpace and Sepearator
				hasTag=true;
				for(int y=0;y<c.length;y++)	{
					if(!(data.cfml.isValidIndex() && c[y]==data.cfml.getCurrentLower())) {
						//hasTag=true;
					//} else {
						hasTag=false;
						data.cfml.setPos(start);
						break;
					}
					data.cfml.next();
				}
				if(hasTag)return tagLib;//break;
			}
			//if(hasTag) return tagLib;
		}
		return null;
	}

	/**
	 * Liest die Attribute eines Tags ein, dies Abh￤ngig von der Definition innerhalb der Tag-Lib.
	 * Hierbei unterscheiden wir vier verschiedene Arten von Attributen:<br>
	 * <ul>
	 * <li>FIX: Definierte Attribute Fix, f￼r jedes Attribut ist definiert ob es required ist oder nicht (gleich wie JSP). </li>
	 * <li>DYNAMIC: Die Attribute des Tag sind frei, keine Namen sind vorgegeben. 
	 * Es kann aber definiert sein wieviele Attribute maximal und minimal verwendetet werden d￼rfen.</li>
	 * <li>FULLDYNAMIC: Gleich wie DYNAMIC, jedoch kann der Name des Attribut auch ein dynamischer Wert sein (wie bei cfset).</li>
	 * <li>NONAME: Ein Tag welches nur ein Attribut besitzt ohne Name, sondern einfach nur mit einem Attribut Wert</li>
	 * </ul>
	 * <br />
	 * EBNF:<br />
	 * <code>({spaces attribute} "/>" | {spaces attribute} ">") | attribute-value;(* Welcher Teil der "oder" Bedingung ausgef￼hrt wird, ist abh￤ngig von der Tag Attribute Definition in der Tag Lib. *)</code>
	 * @param tag
	 * @param parent
	 * @throws TemplateException
	 */
	public static void attributes(Data data,TagLibTag tag, Tag parent) throws TemplateException {
		int type=tag.getAttributeType();
		
	// Tag with attribute names
		if(	type!=TagLibTag.ATTRIBUTE_TYPE_NONAME)	{
			int min=tag.getMin();
			int max=tag.getMax();
			int count=0;
			ArrayList<String> args=new ArrayList<String>();
			RefBoolean allowDefaultValue=new RefBooleanImpl(tag.getDefaultAttribute()!=null);
			while(data.cfml.isValidIndex())	{
				data.cfml.removeSpace();
				// if no more attributes break
				if(data.cfml.isCurrent('/') || data.cfml.isCurrent('>')) break;
				
				parent.addAttribute(attribute(data,tag,args,allowDefaultValue));
				count++;		
			} 
            
			// set default values
			if(tag.hasDefaultValue()) {
			    Map hash=tag.getAttributes();
				Iterator it=hash.keySet().iterator();
			
				while(it.hasNext())	{
					TagLibTagAttr att=(TagLibTagAttr) hash.get(it.next());
					if(!parent.containsAttribute(att.getName()) && att.hasDefaultValue())	{
				    	
						Attribute attr=new Attribute(tag.getAttributeType()==TagLibTag.ATTRIBUTE_TYPE_DYNAMIC,
				    			att.getName(),
				    			Cast.toExpression(LitString.toExprString(att.getDefaultValue(), -1),att.getType()),att.getType()
				    	);
				    	parent.addAttribute(attr);
					}
				}
			}
			
			boolean hasAttributeCollection=args.contains("attributecollection");
			
			// to less attributes
			if(!hasAttributeCollection && min>count)
				throw new TemplateException(data.cfml,"the tag "+tag.getFullName()+" must have "+min+" attributes at least");
			
			// to much attributes
			if(!hasAttributeCollection && max>0 && max<count)
				throw new TemplateException(data.cfml,"the tag "+tag.getFullName()+" can have "+max+" attributes maximal");
			
			// not defined attributes
			if(type==TagLibTag.ATTRIBUTE_TYPE_FIXED || type==TagLibTag.ATTRIBUTE_TYPE_MIXED)	{
				Map hash=tag.getAttributes();
				Iterator it=hash.keySet().iterator();
				
				while(it.hasNext())	{
					TagLibTagAttr att=(TagLibTagAttr) hash.get(it.next());
					if(att.isRequired() && !args.contains(att.getName()) && att.getDefaultValue()==null)	{
						if(!hasAttributeCollection)throw new TemplateException(data.cfml,"attribute "+att.getName()+" is required for tag "+tag.getFullName());
						parent.addMissingAttribute(att.getName(),att.getType());
					}
				}
			}
		}
	// tag without attributes name
		else	{
			TagLibTagAttr attr=tag.getFirstAttribute();
			String strName="noname";
			String strType="any";
			boolean pe=true;
			if(attr!=null) {
				strName=attr.getName();
				strType=attr.getType();	
				pe=attr.getRtexpr();
			}
			//LitString.toExprString("",-1);
			Attribute att=new Attribute(false,strName,attributeValue(data,tag,strType,pe,true,NullExpression.NULL_EXPRESSION),strType);
			parent.addAttribute(att);
		}
	}

    /**
     * Liest ein einzelnes Atribut eines tag ein (nicht NONAME).
     * <br />
     * EBNF:<br />
     * <code>attribute-name  spaces "=" spaces attribute-value;</code>
     * @param tag Definition des Tag das dieses Attribut enth￤lt.
     * @param args Container zum Speichern einzelner Attribute Namen zum nachtr￤glichen Prufen gegen die Tag-Lib.
     * @return Element Attribute Element.
     * @throws TemplateException
     */
    private static Attribute attribute(Data data,TagLibTag tag, ArrayList<String> args,RefBoolean allowDefaultValue) throws TemplateException {
    	Expression value=null;
    	
    	// Name
    	StringBuffer sbType=new StringBuffer();
    	RefBoolean dynamic=new RefBooleanImpl(false);
    	boolean isDefaultValue=false;
    	boolean[] parseExpression=new boolean[2];
    	parseExpression[0]=true;
    	parseExpression[1]=false;
    	String name=attributeName(data.cfml,dynamic,args,tag,sbType,parseExpression,allowDefaultValue.toBooleanValue());
    	
    	// mixed in a noname attribute
    	if(StringUtil.isEmpty(name)){
    		allowDefaultValue.setValue(false);
    		TagLibTagAttr attr = tag.getDefaultAttribute();
    		if(attr==null)
    			throw new TemplateException(data.cfml,"Invalid Identifer.");
    		name=attr.getName();
    		sbType.append(attr.getType());
    		isDefaultValue=true;
    	}
    	
    	
    	comment(data.cfml,true);
    	
    	if(isDefaultValue || data.cfml.forwardIfCurrent('='))	{
    		comment(data.cfml,true);
    		// Value
    		value=attributeValue(data,tag,sbType.toString(),parseExpression[0],false,LitString.toExprString("",-1));	
    	}
    	// default value boolean true
    	else {
    		value=LitBoolean.toExprBoolean(true, -1);
    		if(sbType.toString().length()>0) {
    			value=Cast.toExpression(value, sbType.toString());
    		}
    	}		
    	comment(data.cfml,true);
    	
    	return new Attribute(dynamic.toBooleanValue(),name,value,sbType.toString());
    }

	/**
	 * Liest den Namen eines Attribut ein, je nach Attribut-Definition innerhalb der Tag-Lib, 
	 * wird der Name ￼ber den identifier oder den Expression Transformer eingelesen.
	 * <ul>
	 * <li>FIX und DYNAMIC --> identifier </li>
	 * <li>FULLDYNAMIC --> Expression Transformer </li>
	 * </ul>
	 * <br />
	 * EBNF:<br />
	 * <code>("expression"|'expression'|expression) | identifier;(* Ruft identifier oder den Expression Transformer auf je nach Attribute Definition in der Tag Lib. *)</code>
	 * @param dynamic 
	 * @param args Container zum Speichern einzelner Attribute Namen zum nachtr￤glichen Prufen gegen die Tag-Lib.
	 * @param tag Aktuelles tag aus der Tag-Lib
	 * @param sbType Die Methode speichert innerhalb von sbType den Typ des Tags, zur Interpretation in der attribute Methode.
	 * @param parseExpression Soll der Wert des Attributes geparst werden
	 * @return Attribute Name
	 * @throws TemplateException
	 */
	private static String attributeName(CFMLString cfml,RefBoolean dynamic, ArrayList<String> args, TagLibTag tag, 
			StringBuffer sbType, boolean[] parseExpression,boolean allowDefaultValue) throws TemplateException {
		
		String _id = identifier(cfml,!allowDefaultValue);
		if(StringUtil.isEmpty(_id)){
			return null;
		}
		
		int typeDef=tag.getAttributeType();
		String id=StringUtil.toLowerCase(_id);
        if(args.contains(id)) throw new TemplateException(cfml,"you can't use the same tag attribute ["+id+"] twice");
		args.add(id);
		
		if("attributecollection".equals(id)){
			dynamic.setValue(tag.getAttribute(id)==null);
			sbType.append("struct");
			parseExpression[0]=true;
			parseExpression[1]=true;
		}
		else if(typeDef==TagLibTag.ATTRIBUTE_TYPE_FIXED || typeDef==TagLibTag.ATTRIBUTE_TYPE_MIXED) {
			TagLibTagAttr attr=tag.getAttribute(id);
			if(attr==null) {
				if(typeDef==TagLibTag.ATTRIBUTE_TYPE_FIXED) {
					String names=tag.getAttributeNames();
					if(StringUtil.isEmpty(names))
						throw new TemplateException(cfml,
								"Attribute "+id+" is not allowed for tag "+tag.getFullName());
					
						throw new TemplateException(cfml,
							"Attribute "+id+" is not allowed for tag "+tag.getFullName(),
							"valid attribute names are ["+names+"]");
				}
				dynamic.setValue(true);
			}
			else {
				sbType.append(attr.getType());
				parseExpression[0]=attr.getRtexpr();
			}
		}
		else if(typeDef==TagLibTag.ATTRIBUTE_TYPE_DYNAMIC){
			dynamic.setValue(true);
		}
		return id;
	}
	
	
	
	
	/**
	 * Liest den Wert eines Attribut, mithilfe des innerhalb der Tag-Lib definierten Expression Transformer, ein.
	  * <br />
	 * EBNF:<br />
	 * <code>expression;</code>
	 * @param tag
	 * @param type
	 * @param parseExpression
	 * @param isNonName
	 * @return Element Eingelesener ￼bersetzer Wert des Attributes.
	 * @throws TemplateException
	 */
	public static Expression attributeValue(Data data,TagLibTag tag, String type,boolean parseExpression,boolean isNonName, Expression noExpression) throws TemplateException {
		Expression expr;
		try {
			ExprTransformer transfomer=null;
			if(parseExpression){
			    transfomer = tag.getTagLib().getExprTransfomer();
			}
			else  {
				if(data.set==null) {
					data.set=new SimpleExprTransformer('#');
					//set.setSpecialChar();
				}
				transfomer=data.set;				
			}
			if(isNonName) {
			    int pos=data.cfml.getPos();
			    try {
			    expr=transfomer.transform(data.ep,data.flibs,data.cfml);
			    }
			    catch(TemplateException ete) {
			       if(data.cfml.getPos()==pos)expr=noExpression;
			       else throw ete;
			    }
			}
			else expr=transfomer.transformAsString(data.ep,data.flibs,data.cfml,true);
			if(type.length()>0) {
				expr=Cast.toExpression(expr, type);
			}
		} catch (TagLibException e) {
			throw new TemplateException(data.cfml,e);
		} 
		return expr;
	}
	
	/**
	 * Liest einen Identifier ein und gibt diesen als String zur￼ck.
	  * <br />
	 * EBNF:<br />
	 * <code>(letter | "_") {letter | "_"|digit};</code>
	 * @param throwError throw error or return null if name is invalid
	 * @return Identifier String.
	 * @throws TemplateException
	 */
	public static String identifier(CFMLString cfml,boolean throwError) throws TemplateException  {
		int start = cfml.getPos();
		if(!cfml.isCurrentBetween('a','z') && !cfml.isCurrent('_')) {
			if(throwError)throw new TemplateException(cfml,"Invalid Identifer.");
			return null;
		}
		do {
			cfml.next();
			if(!(cfml.isCurrentBetween('a','z')
				|| cfml.isCurrentBetween('0','9')
				|| cfml.isCurrent('_')
				|| cfml.isCurrent(':')
				|| cfml.isCurrent('-'))) {
					break;
				}
		}
		while (cfml.isValidIndex());
		return cfml.substring(start,cfml.getPos()-start);
	}
}








