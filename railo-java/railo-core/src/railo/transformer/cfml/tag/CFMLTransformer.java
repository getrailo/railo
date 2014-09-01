package railo.transformer.cfml.tag;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.io.CharsetUtil;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.Info;
import railo.runtime.MappingImpl;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.exp.TemplateException;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.cast.CastOther;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.NullExpression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.PrintOut;
import railo.transformer.bytecode.statement.StatementBase;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.Data;
import railo.transformer.cfml.ExprTransformer;
import railo.transformer.cfml.attributes.AttributeEvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.impl.ProcessingDirectiveException;
import railo.transformer.cfml.expression.SimpleExprTransformer;
import railo.transformer.cfml.script.AbstrCFMLScriptTransformer.ComponentTemplateException;
import railo.transformer.cfml.script.CFMLScriptTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.CustomTagLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibException;
import railo.transformer.library.tag.TagLibFactory;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;
import railo.transformer.util.CFMLString;


/**
 * <pre>
		EBNF (Extended Backus-Naur Form) 
		
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
    
    
	
	/**
	 * Startmethode zum transfomieren einer CFML Datei.
	 * <br />
	 * EBNF:<br />
	 * <code>{body}</code>
	 * @param config
	 * @param ps CFML File
	 * @param tlibs Tag Library Deskriptoren, nach denen innerhalb der CFML Datei gepr￼ft werden soll.
	 * @param flibs Function Library Deskriptoren, nach denen innerhalb der Expressions der CFML Datei gepr￼ft werden soll.
	 * @return ￜbersetztes CFXD Dokument Element.
	 * @throws TemplateException
	 * @throws IOException
	 */
	public Page transform(ConfigImpl config,PageSource ps, TagLib[] tlibs, FunctionLib[] flibs) throws TemplateException, IOException	{
		Page p;
		CFMLString cfml;
		
		boolean writeLog=config.getExecutionLogEnabled();
		Charset charset = config._getTemplateCharset();
		boolean dotUpper = ((MappingImpl)ps.getMapping()).getDotNotationUpperCase();
		
		
		while(true){
			try {
				cfml=new CFMLString(ps,charset,writeLog);
				p = transform(config,cfml,tlibs,flibs,ps.getResource().lastModified(),dotUpper);
				break;
			}
			catch(ProcessingDirectiveException pde) {
				if(pde.getWriteLog()!=null)writeLog=pde.getWriteLog().booleanValue();
				if(pde.getDotNotationUpperCase()!=null)dotUpper=pde.getDotNotationUpperCase().booleanValue();
				if(!StringUtil.isEmpty(pde.getCharset()))charset=CharsetUtil.toCharset(pde.getCharset());
			}
		}
		
		// if cfc has no component tag or is script without cfscript
		if(p.isPage() && ResourceUtil.getExtension(ps.getResource(),"").equalsIgnoreCase(config.getCFCExtension())){
			cfml.setPos(0);
			TagLibTag tlt;
			CFMLString original = cfml; 
			
			// try inside a cfscript
			tlt = CFMLTransformer.getTLT(original,"script");
			String text="<"+tlt.getFullName()+">"+original.getText()+"</"+tlt.getFullName()+">";
			cfml=new CFMLString(text,charset,writeLog,ps);
			
			try {
				while(true){
					if(cfml==null){
						cfml=new CFMLString(ps,charset,writeLog);
						text="<"+tlt.getFullName()+">"+cfml.getText()+"</"+tlt.getFullName()+">";
						cfml=new CFMLString(text,charset,writeLog,ps);
					}
					try {
						p= transform(config,cfml,tlibs,flibs,ps.getResource().lastModified(),dotUpper);
						break;
					}
					catch(ProcessingDirectiveException pde) {
						if(pde.getWriteLog()!=null)writeLog=pde.getWriteLog().booleanValue();
						if(pde.getDotNotationUpperCase()!=null)dotUpper=pde.getDotNotationUpperCase().booleanValue();
						if(!StringUtil.isEmpty(pde.getCharset()))charset=CharsetUtil.toCharset(pde.getCharset());
						cfml=null;
					}
				}
			}
			catch (ComponentTemplateException e) {
				throw e.getTemplateException();
			}
			catch (TemplateException e) {
				//print.printST(e);
			}
			
			
			
			
			// try inside a component
			if(p.isPage()){
				tlt = CFMLTransformer.getTLT(original,"component");
				text="<"+tlt.getFullName()+">"+original.getText()+"</"+tlt.getFullName()+">";
				cfml=new CFMLString(text,charset,writeLog,ps);
						
				while(true){
					if(cfml==null){
						cfml=new CFMLString(ps,charset,writeLog);
						text="<"+tlt.getFullName()+">"+cfml.getText()+"</"+tlt.getFullName()+">";
						cfml=new CFMLString(text,charset,writeLog,ps);
					}
					try {
						p= transform(config,cfml,tlibs,flibs,ps.getResource().lastModified(),dotUpper);
						break;
					}
					catch(ProcessingDirectiveException pde) {
						if(pde.getWriteLog()!=null)writeLog=pde.getWriteLog().booleanValue();
						if(pde.getDotNotationUpperCase()!=null)dotUpper=pde.getDotNotationUpperCase().booleanValue();
						if(!StringUtil.isEmpty(pde.getCharset()))charset=CharsetUtil.toCharset(pde.getCharset());
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
	public Page transform(ConfigImpl config,CFMLString cfml,TagLib[] tlibs,FunctionLib[] flibs, long sourceLastModified, Boolean dotNotationUpperCase) throws TemplateException {
		
		TagLib[][] _tlibs=new TagLib[][]{null,new TagLib[0]};
		_tlibs[TAG_LIB_GLOBAL]=tlibs;
		// reset page tlds
		if(_tlibs[TAG_LIB_PAGE].length>0) {
			_tlibs[TAG_LIB_PAGE]=new TagLib[0];
		}
		
		
		

		PageSource source=cfml.getPageSource(); 
		
		Page page=new Page(source,source.getPhyscalFile(),source.getFullClassName(),Info.getFullVersionInfo(),sourceLastModified,cfml.getWriteLog(),config.getSuppressWSBeforeArg());
		TagData data = new TagData(_tlibs,flibs,config.getCoreTagLib().getScriptTags(),cfml,dotNotationUpperCase,page);
		
		//Body body=page;
		try {
			do {
				
				body(data,page,false,null);
				
				if(data.cfml.isAfterLast()) break;
				if(data.cfml.forwardIfCurrent("</")){
					int pos = data.cfml.getPos();
					TagLib tagLib=nameSpace(data);
					if(tagLib==null){
						page.addPrintOut("</", null,null);
					}
					else {
						String name = identifier(data.cfml,true,true);
						if(tagLib.getIgnoreUnknowTags()) {
							TagLibTag tlt = tagLib.getTag(name);
							if(tlt==null) {
								data.cfml.setPos(pos);
								page.addPrintOut("</", null,null);
							}
						}
						else throw new TemplateException(cfml,"no matching start tag for end tag ["+tagLib.getNameSpaceAndSeparator()+name+"]");
			
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
		    throw e;
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
	private void body(TagData data,Body body, boolean parseExpression, ExprTransformer transformer) throws TemplateException {
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
	 * Komentare koennen auch Kommentare enthalten.
	 * <br />
	 * EBNF:<br />
	 * <code>"<!---" {?-"--->"} "--->";</code>
	 * @throws TemplateException
	 */

	private static void comment(CFMLString cfml,boolean removeSpace) throws TemplateException {
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
	private void literal(TagData data,Body parent,boolean parseExpression, ExprTransformer transformer) throws TemplateException {
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
							Position end = data.cfml.getPosition();
							Position start = data.cfml.getPosition(end.pos-text.length());
							
							
							parent.addPrintOut(text.toString(),start,end);
							start=end;
							text=new StringBuffer();
						}
						Position end = data.cfml.getPosition();
						Position start = data.cfml.getPosition(end.pos-text.length());
						
						PrintOut po;
						parent.addStatement(po=new PrintOut(transformer.transform(data.page,data.ep,data.tlibs,data.flibs,data.scriptTags,data.cfml,data.settings),start,end));
						po.setEnd(data.cfml.getPosition());
						
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
			if(text.length()>0){
				Position end = data.cfml.getPosition();
				Position start = data.cfml.getPosition(end.pos-text.length());
				
				parent.addPrintOut(text.toString(), start,end);
			}
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
			Position e = data.cfml.getPosition();
			Position s = data.cfml.getPosition(start);
			
			parent.addPrintOut(text, s,e);
			
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
	private boolean tag(TagData data,Body parent,boolean parseExpression) throws TemplateException {
	    //railo.print.ln("--->"+data.cfml.getCurrent());
	    boolean hasBody=false;
		
		Position line = data.cfml.getPosition();
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
		String strNameNormal=identifier(data.cfml,false,true);
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
			 if(tagLibTag==null) {
				 if(tagLib.getIgnoreUnknowTags()){
					 data.cfml.setPos(start);
					 return false;
				 } 
				 throw new TemplateException(data.cfml,"undefined tag ["+tagLib.getNameSpaceAndSeparator()+strName+"]");
			 }
			appendix=StringUtil.removeStartingIgnoreCase(strNameNormal,tagLibTag.getName());
		 }
		
		// CFXD Element 
		Tag tag;
		try {
			tag = tagLibTag.getTag(line,data.cfml.getPosition());
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
		 if(tag.getFullname().equalsIgnoreCase("cfcomponent"))data.page.setIsComponent(true);	// MUST to hardcoded, to better
		 else if(tag.getFullname().equalsIgnoreCase("cfinterface"))data.page.setIsInterface(true);	// MUST to hardcoded, to better
			 
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
				tagLibTag=tagLibTag.getAttributeEvaluator().evaluate(tagLibTag,tag);
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
			throw createTemplateException(data.cfml, "tag ["+tagLibTag.getFullName()+"] is not closed",tagLibTag);
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
				if(tdbt==null) throw createTemplateException(data.cfml,"Tag dependent body Transformer is invalid for Tag ["+tagLibTag.getFullName()+"]",tagLibTag);
				tdbt.transform(data.page,this,data.ep,data.tlibs,data.flibs,tag,tagLibTag,data.scriptTags,data.cfml,data.settings);
				
				//	get TagLib of end Tag
				if(!data.cfml.forwardIfCurrent("</")) {
					// MUST this is a patch, do a more proper implementation
					TemplateException te = new TemplateException(data.cfml,"invalid construct");
					if(tdbt instanceof CFMLScriptTransformer && ASMUtil.containsComponent(tag.getBody())) {
						throw new CFMLScriptTransformer.ComponentTemplateException(te);
					}
					throw te;
				}
				
				TagLib tagLibEnd=nameSpace(data);
				// same NameSpace
				if(!(tagLibEnd!=null && tagLibEnd.getNameSpaceAndSeparator().equals(tagLib.getNameSpaceAndSeparator())))
					throw new TemplateException(data.cfml,"invalid construct");
				// get end Tag
				String strNameEnd=identifier(data.cfml,true,true).toLowerCase();

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
				BodyBase body=new BodyBase();
				body.setParent(tag);
				//tag.setBody(body);
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
							throw createTemplateException(data.cfml,"No matching end tag found for tag ["+tagLibTag.getFullName()+"]",tagLibTag);
						}
						body.moveStatmentsTo(parent);
						return executeEvaluator(data,tagLibTag, tag);
					}
					
					// Invalid Construct
					int posBeforeEndTag=data.cfml.getPos();
					if(!data.cfml.forwardIfCurrent('<','/'))
						throw createTemplateException(data.cfml,"Missing end tag for ["+tagLibTag.getFullName()+"]",tagLibTag);
					
					// get TagLib of end Tag
					int _start = data.cfml.getPos();
					TagLib tagLibEnd=nameSpace(data);
					
					// same NameSpace
					if(tagLibEnd!=null)	{
					    String strNameEnd="";
					    //railo.print.ln(data.cfml.getLine()+" - "+data.cfml.getColumn()+" - "+tagLibEnd.getNameSpaceAndSeperator()+".equals("+tagLib.getNameSpaceAndSeperator()+")");
					    if(tagLibEnd.getNameSpaceAndSeparator().equals(tagLib.getNameSpaceAndSeparator())) {
					        
						    // get end Tag
							strNameEnd=identifier(data.cfml,true,true).toLowerCase();
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
							if(tagLibEnd.getIgnoreUnknowTags() && (tagLibEnd.getTag(strNameEnd))==null){
								data.cfml.setPos(_start);
							}
							else throw new TemplateException(data.cfml,
									"Start and End Tag has not the same Name ["+
									tagLib.getNameSpaceAndSeparator()+strName+"-"+tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"]");
						}
					    else {
							body.moveStatmentsTo(parent);
							data.cfml.setPos(posBeforeEndTag);
							return executeEvaluator(data,tagLibTag, tag);
					    }
					    /// new part	
					}
					body.addPrintOut("</",null,null);
					
				}
				tag.setBody(body);
				
			}
		}
		if(tag instanceof StatementBase)
			((StatementBase)tag).setEnd(data.cfml.getPosition());
		// Tag Translator Evaluator
		
		return executeEvaluator(data,tagLibTag, tag);
        
	}
	
	private boolean executeEvaluator(TagData data,TagLibTag tagLibTag, Tag tag) throws TemplateException {
		if(tagLibTag.hasTteClass())	{
			try {
				TagLib lib=tagLibTag.getEvaluator().execute(data.config,tag,tagLibTag,data.flibs,data);
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
					// TODO make sure longer namespace ar checked firts to support subsets, same for core libs
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
	public static TagLib nameSpace(Data data) {
		boolean hasTag=false;
		int start = data.cfml.getPos();
		TagLib tagLib=null;
		
		// loop over NameSpaces
		for(int i=1;i>=0;i--)	{
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
	public static void attributes(TagData data,TagLibTag tag, Tag parent) throws TemplateException {
		int type=tag.getAttributeType();
		int start = data.cfml.getPos();
	// Tag with attribute names
		if(	type!=TagLibTag.ATTRIBUTE_TYPE_NONAME)	{
			try{
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
				Map<String, TagLibTagAttr> hash = tag.getAttributes();
				Iterator<Entry<String, TagLibTagAttr>> it = hash.entrySet().iterator();
				Entry<String, TagLibTagAttr> e;
				TagLibTagAttr att;
				while(it.hasNext())	{
					e = it.next();
					att=e.getValue();
					if(!parent.containsAttribute(att.getName()) && att.hasDefaultValue())	{
				    	
						Attribute attr=new Attribute(tag.getAttributeType()==TagLibTag.ATTRIBUTE_TYPE_DYNAMIC,
				    		att.getName(),
				    		CastOther.toExpression(LitString.toExprString(Caster.toString(att.getDefaultValue(),null)),att.getType()),att.getType()
				    	);
						attr.setDefaultAttribute(true);
						parent.addAttribute(attr);
					}
				}
			}
			
			boolean hasAttributeCollection=args.contains("attributecollection");
			
			// to less attributes
			if(!hasAttributeCollection && min>count)
				throw createTemplateException(data.cfml,"the tag "+tag.getFullName()+" must have at least "+min+" attributes",tag);
			
			// too much attributes
			if(!hasAttributeCollection && max>0 && max<count)
				throw createTemplateException(data.cfml,"the tag "+tag.getFullName()+" can have a maximum of "+max+" attributes",tag);
			
			// not defined attributes
			if(type==TagLibTag.ATTRIBUTE_TYPE_FIXED || type==TagLibTag.ATTRIBUTE_TYPE_MIXED)	{
				//Map<String, TagLibTagAttr> hash = tag.getAttributes();
				Iterator<TagLibTagAttr> it = tag.getAttributes().values().iterator();
				
				while(it.hasNext())	{
					
					TagLibTagAttr att=it.next();
					if(att.isRequired() && !contains(args,att) && att.getDefaultValue()==null)	{
						if(!hasAttributeCollection)throw createTemplateException(data.cfml,"attribute "+att.getName()+" is required for tag "+tag.getFullName(),tag);
						parent.addMissingAttribute(att);
					}
				}
			}
			}
			catch(TemplateException te){
				data.cfml.setPos(start);
				// if the tag supports a non name attribute try this
				TagLibTagAttr sa = tag.getSingleAttr();
				if(sa!=null) attrNoName(parent,tag,data,sa);
				else throw te;
			}
		}
	// tag without attributes name
		else	{
			attrNoName(parent,tag,data,null);
		}
	}

    private static boolean contains(ArrayList<String> names, TagLibTagAttr attr) {
		
		Iterator<String> it = names.iterator();
		String name;
		String[] alias;
		while(it.hasNext()){
			name=it.next();
			
			// check name
			if(name.equals(attr.getName())) return true;
			
			// and aliases
			alias = attr.getAlias();
			if(!ArrayUtil.isEmpty(alias)) for(int i=0;i<alias.length;i++){
				if(alias[i].equals(attr.getName())) return true;
			}
		}
		return false;
	}


	private static void attrNoName(Tag parent, TagLibTag tag, TagData data,TagLibTagAttr attr) throws TemplateException {
    	if(attr==null)attr=tag.getFirstAttribute();
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
    private static Attribute attribute(TagData data,TagLibTag tag, ArrayList<String> args,RefBoolean allowDefaultValue) throws TemplateException {
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
    			throw new TemplateException(data.cfml,"Invalid Identifier.");
    		name=attr.getName();
    		sbType.append(attr.getType());
    		isDefaultValue=true;
    	}
    	
    	
    	comment(data.cfml,true);
    	
    	if(isDefaultValue || data.cfml.forwardIfCurrent('='))	{
    		comment(data.cfml,true);
    		// Value
    		value=attributeValue(data,tag,sbType.toString(),parseExpression[0],false,LitString.toExprString(""));	
    	}
    	// default value boolean true
    	else {
    		value=tag.getAttributeDefaultValue();
    		if(sbType.toString().length()>0) {
    			value=CastOther.toExpression(value, sbType.toString());
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
		
		String _id = identifier(cfml,!allowDefaultValue,true);
		if(StringUtil.isEmpty(_id)){
			return null;
		}
		
		int typeDef=tag.getAttributeType();
		String id=StringUtil.toLowerCase(_id);
        if(args.contains(id)) throw createTemplateException(cfml,"you can't use the same tag attribute ["+id+"] twice",tag);
		args.add(id);
		
		if("attributecollection".equals(id)){
			dynamic.setValue(tag.getAttribute(id,true)==null);
			sbType.append("struct");
			parseExpression[0]=true;
			parseExpression[1]=true;
		}
		else if(typeDef==TagLibTag.ATTRIBUTE_TYPE_FIXED || typeDef==TagLibTag.ATTRIBUTE_TYPE_MIXED) {
			TagLibTagAttr attr=tag.getAttribute(id,true);
			if(attr==null) {
				if(typeDef==TagLibTag.ATTRIBUTE_TYPE_FIXED) {
					String names=tag.getAttributeNames();
					if(StringUtil.isEmpty(names))
						throw createTemplateException(cfml,
								"Attribute "+id+" is not allowed for tag "+tag.getFullName(),tag);
					
					try{
						names=ListUtil.sort(names, "textnocase",null, null);
					}
					catch(Throwable t){}
					throw createTemplateException(cfml,
							"Attribute "+id+" is not allowed for tag "+tag.getFullName(),
							"valid attribute names are ["+names+"]",tag);
				}
				dynamic.setValue(true);
			}
			else {
				id=attr.getName();
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
	public static Expression attributeValue(TagData data,TagLibTag tag, String type,boolean parseExpression,boolean isNonName, Expression noExpression) throws TemplateException {
		Expression expr;
		try {
			ExprTransformer transfomer=null;
			if(parseExpression){
			    transfomer = tag.getTagLib().getExprTransfomer();
			}
			else  {
				if(data.getSimpleExprTransformer()==null) {
					data.setSimpleExprTransformer(new SimpleExprTransformer('#'));
					//set.setSpecialChar();
				}
				transfomer=data.getSimpleExprTransformer();
			}
			if(isNonName) {
			    int pos=data.cfml.getPos();
			    try {
			    expr=transfomer.transform(data.page,data.ep,data.tlibs,data.flibs,data.scriptTags,data.cfml,data.settings);
			    }
			    catch(TemplateException ete) {
			       if(data.cfml.getPos()==pos)expr=noExpression;
			       else throw ete;
			    }
			}
			else expr=transfomer.transformAsString(data.page,data.ep,data.tlibs,data.flibs,data.scriptTags,data.cfml,data.settings,true);
			if(type.length()>0) {
				expr=CastOther.toExpression(expr, type);
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
	public static String identifier(CFMLString cfml,boolean throwError, boolean allowColon) throws TemplateException  {
		int start = cfml.getPos();
		
		if(!cfml.isCurrentBetween('a','z') && !cfml.isCurrent('_')) {
			if(throwError)throw new TemplateException(cfml,"Invalid Identifier, the following character cannot be part of a identifier ["+cfml.getCurrent()+"]");
			return null;
		}
		do {
			cfml.next();
			if(!(cfml.isCurrentBetween('a','z')
				|| cfml.isCurrentBetween('0','9')
				|| cfml.isCurrent('_')
				|| (allowColon && cfml.isCurrent(':'))
				|| cfml.isCurrent('-'))) {
					break;
				}
		}
		while (cfml.isValidIndex());
		return cfml.substring(start,cfml.getPos()-start);
	}
	
	
	public static TemplateException createTemplateException(CFMLString cfml,String msg, String detail,TagLibTag tag) {
		TemplateException te = new TemplateException(cfml,msg,detail);
		setAddional(te,tag);
		return te;
	}
	public static TemplateException createTemplateException(CFMLString cfml,String msg, TagLibTag tag) {
		TemplateException te = new TemplateException(cfml,msg);
		setAddional(te,tag);
		return te;
	}
	
	public static TemplateException setAddional(TemplateException te, TagLibTag tlt) {
		setAddional((PageExceptionImpl)te, tlt);
		return te;
	}
	
	public static ApplicationException setAddional(ApplicationException ae, TagLibTag tlt) {
		setAddional((PageExceptionImpl)ae, tlt);
		return ae;
	}


	private static void setAddional(PageExceptionImpl pe, TagLibTag tlt) {
		Map<String, TagLibTagAttr> attrs = tlt.getAttributes();
		Iterator<Entry<String, TagLibTagAttr>> it = attrs.entrySet().iterator();
		Entry<String, TagLibTagAttr> entry;
		TagLibTagAttr attr;
		
		// Pattern
		StringBuilder pattern=new StringBuilder("<");
		pattern.append((tlt.getFullName()));
		StringBuilder req=new StringBuilder();
		StringBuilder opt=new StringBuilder();
		StringBuilder tmp;
		
		
		pattern.append(" ");
		int c=0;
		while(it.hasNext()){
			entry = it.next();
			attr=entry.getValue();
			tmp=attr.isRequired()?req:opt;
			
			tmp.append(" ");
			if(!attr.isRequired()) tmp.append("[");
			if(c++>0)pattern.append(" ");
			tmp.append(attr.getName());
			tmp.append("=\"");
			tmp.append(attr.getType());
			tmp.append("\"");
			if(!attr.isRequired()) tmp.append("]");
		}

		if(req.length()>0)pattern.append(req);
		if(opt.length()>0)pattern.append(opt);
		
		if(tlt.getAttributeType()==TagLibTag.ATTRIBUTE_TYPE_MIXED || tlt.getAttributeType()==TagLibTag.ATTRIBUTE_TYPE_DYNAMIC)
			pattern.append(" ...");
		pattern.append(">");
		if(tlt.getHasBody()) {
			if(tlt.isBodyReq()){
				pattern.append("</");
				pattern.append(tlt.getFullName());
				pattern.append(">");
			}
			else if(tlt.isBodyFree()){
				pattern.append("[</");
				pattern.append(tlt.getFullName());
				pattern.append(">]");
			}
		}
		
		pe.setAdditional(KeyConstants._Pattern, pattern);
		
		// Documentation
		StringBuilder doc=new StringBuilder(tlt.getDescription());
		req=new StringBuilder();
		opt=new StringBuilder();
		
		doc.append("\n");
		
		it = attrs.entrySet().iterator();
		while(it.hasNext()){
			entry = it.next();
			attr=entry.getValue();
			tmp=attr.isRequired()?req:opt;
			
			tmp.append("* ");
			tmp.append(attr.getName());
			tmp.append(" (");
			tmp.append(attr.getType());
			tmp.append("): ");
			tmp.append(attr.getDescription());
			tmp.append("\n");
		}

		if(req.length()>0)doc.append("\nRequired:\n").append(req);
		if(opt.length()>0)doc.append("\nOptional:\n").append(opt);
		
		pe.setAdditional(KeyImpl.init("Documentation"), doc);
	}
	
}








