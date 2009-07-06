package railo.transformer.cfml.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibException;
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
	
    private static short TAG_LIB_GLOBAL=0;
    private static short TAG_LIB_PAGE=1;
    
    
	private TagLib[][] tlibs=new TagLib[][]{null,new TagLib[0]};
	//private TagLib[] pageTlibs=new TagLib[0];
	private FunctionLib[] flibs;
	
	private CFMLString cfml;
	private EvaluatorPool ep=new EvaluatorPool();
	private SimpleExprTransformer set;
    private Config config;
	
	
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
		try {
			return transform(config,new CFMLString(sf,config.getTemplateCharset()),tlibs,flibs,sf.getFile().lastModified());
			//return transform(config,new CFMLString(sf,SystemUtil.get Charset()),tlibs,flibs,sf.getFile().lastModified());
		}
		catch(ProcessingDirectiveException pde) {
			return transform(config,new CFMLString(sf,pde.getTargetCharset()),tlibs,flibs,sf.getFile().lastModified());
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
		
		this.flibs=flibs;
		this.cfml=cfml;
		this.config=config;
		
		this.tlibs[TAG_LIB_GLOBAL]=tlibs;
		// reset page tlds
		if(this.tlibs[TAG_LIB_PAGE].length>0) {
		    this.tlibs[TAG_LIB_PAGE]=new TagLib[0];
		}
		

		SourceFile source=cfml.getSourceFile(); 
		
		Page page=new Page(source.getPhyscalFile().getAbsolutePath(),source.getFullClassName(),Info.getFullVersionInfo(),sourceLastModified);
		//Body body=page;
		try {
			do {
				
				body(page,false,null);
				
				if(cfml.isAfterLast()) break;
				if(cfml.forwardIfCurrent("</")){
					TagLib tagLib=this.nameSpace();
					if(tagLib==null){
						page.addPrintOut("</", cfml.getLine());
					}
					else {
						throw new TemplateException(cfml,"no matching start tag for end tag ["+tagLib.getNameSpaceAndSeparator()+identifier(true)+"]");
			
					}
				}
				else 
					throw new TemplateException(cfml,"Error while transforming CFML File");
			}while(true);
			
			// call-back of evaluators
			ep.run();
			
			return page;
		}
		catch(TemplateException e) {
		    ep.clear();
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
	private void body(Body body, boolean parseExpression, ExprTransformer transformer) throws TemplateException {
		boolean parseLiteral=true;
		
	// Comment 
        comment(false);
	// Tag
		// is Tag Beginning
		if(cfml.isCurrent('<'))	{
			// return if end tag and inside tag
			if(cfml.isNext('/'))	{
			    //railo.print.ln("early return");
				return;
			}
			parseLiteral=!tag(body,parseExpression);
		}
		// no Tag
		if(parseLiteral) {
			literal(body, parseExpression, transformer);
		}
		// not at the end 
		if(cfml.isValidIndex()) 
			body(body,parseExpression, transformer);
	}
	
	/**
	 * Liest einen Kommentar ein, Kommentare werden nicht in die CFXD ￼bertragen sondern verworfen.
	 * Komentare k￶nnen auch Kommentare enthalten.
	 * <br />
	 * EBNF:<br />
	 * <code>"<!---" {?-"--->"} "--->";</code>
	 * @throws TemplateException
	 */

	private void comment(boolean removeSpace) throws TemplateException {
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
	private void literal(Body parent,boolean parseExpression, ExprTransformer transformer) throws TemplateException {
		// with expression
		if(parseExpression) {
			if(cfml.isAfterLast())return;
            // cfml.getCurrent()
			StringBuffer text=new StringBuffer();
			int count=0;
			while(cfml.isValidIndex()) {
				count++;
				// #
				if(cfml.isCurrent('#')) {
					cfml.next();
					if(cfml.isCurrent('#')) {
						text.append('#');
					}
					else {
						if(text.length()>0)	{
							parent.addPrintOut(text.toString(),-1);
							text=new StringBuffer();
						}
                        int line=cfml.getLine();
						parent.addStatement(new PrintOut(transformer.transform(flibs,cfml),line));
							
						if(!cfml.isCurrent('#'))
							throw new TemplateException(cfml,"missing terminating [#] for expression");
					}
				}
				else if(cfml.isCurrent('<') && count>1) {
					break;
				}
				else
					text.append(cfml.getCurrent());
			cfml.next();
			}
			if(text.length()>0)parent.addPrintOut(text.toString(), -1);
		}
		// no expression
		else {
			int start=cfml.getPos();
			cfml.next();
			int end=cfml.indexOfNext('<');
			String text;
			if(end==-1) {
				text=cfml.substring(start);
				cfml.setPos(cfml.length());
			}
			else {
				text=cfml.substring(start,end-start);
				cfml.setPos(end);
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
	private boolean tag(Body parent,boolean parseExpression) throws TemplateException {
	    //railo.print.ln("--->"+cfml.getCurrent());
	    boolean hasBody=false;
		
		int line=cfml.getLine();
		//int column=cfml.getColumn();
		int start=cfml.getPos();
		cfml.next();
		
		// read in namesapce of tag
		TagLib tagLib=nameSpace();
		
		// return if no matching tag lib
		if(tagLib==null)	{
			cfml.previous();
			return false;
		}
				
		// Get matching tag from tag lib
		String strNameNormal=identifier(false);
		if(strNameNormal==null) {
			cfml.setPos((cfml.getPos()-tagLib.getNameSpaceAndSeparator().length())-1);
			return false;
		}
		
		String strName=strNameNormal.toLowerCase();
		String appendix=null;
		TagLibTag tagLibTag=tagLib.getTag(strName);
		
		// get taglib
		if(tagLibTag==null)	{
			tagLibTag=tagLib.getAppendixTag(strName);
			 if(tagLibTag==null)
				 throw new TemplateException(cfml,"undefined tag ["+tagLib.getNameSpaceAndSeparator()+strName+"]");
			appendix=StringUtil.removeStartingIgnoreCase(strNameNormal,tagLibTag.getName());
		 }
		
		// CFXD Element 
		Tag tag;
		try {
			tag = tagLibTag.getTag(line,cfml.getLine());
		} 
		catch (Exception e) {
			throw new TemplateException(cfml,e);
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
		comment(true);
		
		// Tag Translator Evaluator
		if(tagLibTag.hasTteClass())	{
			ep.add(tagLibTag,tag,flibs,cfml);
		}
		
		//get Attributes
		attributes(tagLibTag,tag);
		
		if(tagLibTag.hasAttributeEvaluator()) {
			try {
				tagLibTag.getAttributeEvaluator().evaluate(tagLibTag,tag);
			} catch (AttributeEvaluatorException e) {
				
				throw new TemplateException(cfml, e);
			}
		}
		
		
		
		// End of begin Tag
//		TODO muss erlaubt sein
		if(cfml.forwardIfCurrent('>'))	{
			hasBody=tagLibTag.getHasBody();
		}
		else if(cfml.forwardIfCurrent('/','>')) {
			if(tagLibTag.getHasBody())tag.setBody(new BodyBase());
		}
		else {
			throw new TemplateException(cfml, "tag ["+tagLibTag.getFullName()+"] is not closed");
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
					throw new TemplateException(cfml,e);
				}
				if(tdbt==null) throw new TemplateException(cfml,"Tag dependent body Transformer is invalid for Tag ["+tagLibTag.getFullName()+"]");
				tdbt.transform(this,flibs,tag,tagLibTag,cfml);
				
				
				//	get TagLib of end Tag
				if(!cfml.forwardIfCurrent("</"))
					throw new TemplateException(cfml,"invalid construct");
				
				TagLib tagLibEnd=nameSpace();
				// same NameSpace
				if(!(tagLibEnd!=null && tagLibEnd.getNameSpaceAndSeparator().equals(tagLib.getNameSpaceAndSeparator())))
					throw new TemplateException(cfml,"invalid construct");
				// get end Tag
				String strNameEnd=identifier(true).toLowerCase();

				// not the same name Tag
				if(!strName.equals(strNameEnd)) {
					cfml.setPos(start);
					throw new TemplateException(cfml,"Start and End Tag has not the same Name ["+tagLib.getNameSpaceAndSeparator()+strName+"-"+tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"]");				
				 }
				 cfml.removeSpace();
				 if(!cfml.forwardIfCurrent('>'))
					 throw new TemplateException(cfml,"End Tag ["+tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"] not closed");
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
							throw new TemplateException(cfml,e);
						}
					}
					

					// call body
				    body(body,parseExpression,transfomer);
				    
				    // no End Tag
					if(cfml.isAfterLast()) {
					    
						if(tagLibTag.isBodyReq()) {
						    cfml.setPos(start);
							throw new TemplateException(cfml,"No matching end tag found for tag ["+tagLibTag.getFullName()+"]");
						}
						body.moveStatmentsTo(parent);
						return executeEvaluator(tagLibTag, tag);
					}
					
					// Invalid Construct
					int posBeforeEndTag=cfml.getPos();
					if(!cfml.forwardIfCurrent('<','/'))
						throw new TemplateException(cfml,"Missing end tag for ["+tagLibTag.getFullName()+"]");
					
					// get TagLib of end Tag
					TagLib tagLibEnd=nameSpace();
					
					// same NameSpace
					if(tagLibEnd!=null)	{
					    String strNameEnd="";
					    //railo.print.ln(cfml.getLine()+" - "+cfml.getColumn()+" - "+tagLibEnd.getNameSpaceAndSeperator()+".equals("+tagLib.getNameSpaceAndSeperator()+")");
					    if(tagLibEnd.getNameSpaceAndSeparator().equals(tagLib.getNameSpaceAndSeparator())) {
					        
						    // get end Tag
							strNameEnd=identifier(true).toLowerCase();
							// not the same name Tag
							
							// new part
							cfml.removeSpace();
							if(strName.equals(strNameEnd)) {
							    if(!cfml.forwardIfCurrent('>'))
									throw new TemplateException(cfml,"End Tag ["+tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"] not closed");
								break;
							}
							
					    }
					    // new part
					    if(tagLibTag.isBodyReq()) {
							TagLibTag endTag = tagLibEnd.getTag(strNameEnd);
							if(endTag!=null && !endTag.getHasBody())
								throw new TemplateException(cfml,
										"End Tag ["+
										tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"] is not allowed, for this tag only a Start Tag is allowed");
							cfml.setPos(start);
							
							throw new TemplateException(cfml,
									"Start and End Tag has not the same Name ["+
									tagLib.getNameSpaceAndSeparator()+strName+"-"+tagLibEnd.getNameSpaceAndSeparator()+strNameEnd+"]");
						}
						body.moveStatmentsTo(parent);
						cfml.setPos(posBeforeEndTag);
						return executeEvaluator(tagLibTag, tag);
					    /// new part	
					}
					body.addPrintOut("</",cfml.getLine());
					
				}
				tag.setBody(body);
				
			}
		}
		if(tag instanceof StatementBase)
			((StatementBase)tag).setEndLine(cfml.getLine());
		// Tag Translator Evaluator
		
		return executeEvaluator(tagLibTag, tag);
        
	}
	
	private boolean executeEvaluator(TagLibTag tagLibTag, Tag tag) throws TemplateException {
		if(tagLibTag.hasTteClass())	{
            try {	    
                TagLib lib=tagLibTag.getEvaluator().execute(config,tag,tagLibTag,flibs,cfml);
                if(lib!=null) {
                    TagLib[] newTlibs=new TagLib[tlibs[TAG_LIB_PAGE].length+1]; 
                    for(int i=0;i<tlibs[TAG_LIB_PAGE].length;i++) {
                        newTlibs[i]=tlibs[TAG_LIB_PAGE][i];
                    }
                    newTlibs[tlibs[TAG_LIB_PAGE].length]=lib;
                    tlibs[TAG_LIB_PAGE]=newTlibs;
                }
                
            } catch (EvaluatorException e) {
                throw new TemplateException(cfml,e);
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
	private TagLib nameSpace() {
		boolean hasTag=false;
		int start = cfml.getPos();
		TagLib tagLib=null;
		
		// loop over NameSpaces
		for(int i=0;i<2;i++)	{
			for(int ii=0;ii<tlibs[i].length;ii++)	{
				tagLib= tlibs[i][ii];
				char[] c=tagLib.getNameSpaceAndSeperatorAsCharArray();
				// Loop over char of NameSpace and Sepearator
				hasTag=true;
				for(int y=0;y<c.length;y++)	{
					if(!(cfml.isValidIndex() && c[y]==cfml.getCurrentLower())) {
						//hasTag=true;
					//} else {
						hasTag=false;
						cfml.setPos(start);
						break;
					}
					cfml.next();
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
	private void attributes(TagLibTag tag, Tag parent) throws TemplateException {
		int type=tag.getAttributeType();
		
	// Tag with attribute names
		if(	type!=TagLibTag.ATTRIBUTE_TYPE_NONAME)	{
			int min=tag.getMin();
			int max=tag.getMax();
			int count=0;
			ArrayList args=new ArrayList();
			while(cfml.isValidIndex())	{
				cfml.removeSpace();
				// if no more attributes break
				if(cfml.isCurrent('/') || cfml.isCurrent('>')) break;
				
				parent.addAttribute(attribute(tag,args));
				count++;		
			} 
            
			// set default values
			if(tag.hasDefaultValue()) {
			    Map hash=tag.getAttributes();
				Iterator it=hash.keySet().iterator();
			
				while(it.hasNext())	{
					TagLibTagAttr att=(TagLibTagAttr) hash.get(it.next());
					//print.out(att.getName());
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
				throw new TemplateException(cfml,"the tag "+tag.getFullName()+" must have "+min+" attributes at least");
			
			// to much attributes
			if(!hasAttributeCollection && max>0 && max<count)
				throw new TemplateException(cfml,"the tag "+tag.getFullName()+" can have "+max+" attributes maximal");
			
			// not defined attributes
			if(type==TagLibTag.ATTRIBUTE_TYPE_FIXED || type==TagLibTag.ATTRIBUTE_TYPE_MIXED)	{
				Map hash=tag.getAttributes();
				Iterator it=hash.keySet().iterator();
				
				while(it.hasNext())	{
					TagLibTagAttr att=(TagLibTagAttr) hash.get(it.next());
					if(att.isRequired() && !args.contains(att.getName()) && att.getDefaultValue()==null)	{
						if(!hasAttributeCollection)throw new TemplateException(cfml,"attribute "+att.getName()+" is required for tag "+tag.getFullName());
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
			Attribute att=new Attribute(false,strName,attributeValue(tag,strType,pe,true,NullExpression.NULL_EXPRESSION),strType);
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
    private Attribute attribute(TagLibTag tag, ArrayList args) throws TemplateException {
    	
    	// Name
    	StringBuffer sbType=new StringBuffer();
    	boolean[] parseExpression=new boolean[2];
    	parseExpression[0]=true;
    	parseExpression[1]=false;
    	RefBoolean dynamic=new RefBooleanImpl(false);
    	String name=attributeName(dynamic,args,tag,sbType,parseExpression);
    	Expression value=null;
    	
    	comment(true);
    	
    	if(cfml.forwardIfCurrent('='))	{
    		comment(true);
    		// Value
    		value=attributeValue(tag,sbType.toString(),parseExpression[0],false,LitString.toExprString("",-1));	
    	}
    	// default value boolean true
    	else {
    		value=LitBoolean.toExprBoolean(true, -1);
    		if(sbType.toString().length()>0) {
    			value=Cast.toExpression(value, sbType.toString());
    		}
    	}		
    	comment(true);
    	
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
	private String attributeName(RefBoolean dynamic, ArrayList args, TagLibTag tag, StringBuffer sbType, boolean[] parseExpression) throws TemplateException {
		
		int typeDef=tag.getAttributeType();
		String id=StringUtil.toLowerCase(identifier(true));
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
	private Expression attributeValue(TagLibTag tag, String type,boolean parseExpression,boolean isNonName, Expression noExpression) throws TemplateException {
		Expression expr;
		try {
			ExprTransformer transfomer=null;
			if(parseExpression){
			    transfomer = tag.getTagLib().getExprTransfomer();
			}
			else  {
				if(set==null) {
					set=new SimpleExprTransformer();
					set.setSpecialChar('#');
				}
				transfomer=set;				
			}
			if(isNonName) {
			    int pos=cfml.getPos();
			    try {
			    expr=transfomer.transform(flibs,cfml);
			    }
			    catch(TemplateException ete) {
			       if(cfml.getPos()==pos)expr=noExpression;
			       else throw ete;
			    }
			}
			else expr=transfomer.transformAsString(flibs,cfml,true);
			if(type.length()>0) {
				expr=Cast.toExpression(expr, type);
			}
		} catch (TagLibException e) {
			throw new TemplateException(cfml,e);
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
	private String identifier(boolean throwError) throws TemplateException  {
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








