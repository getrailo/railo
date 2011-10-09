package railo.transformer.library.tag;

import java.util.Iterator;

import railo.commons.lang.StringUtil;

public final class TagLibTagScript {

	public static final short TYPE_NONE = 0;
	public static final short TYPE_SINGLE = 1;
	public static final short TYPE_MULTIPLE = 2;
	
	public static final short CTX_OTHER = -1;
	public static final short CTX_NONE = 0;
	public static final short CTX_IF = 1;
	public static final short CTX_ELSE_IF = 2;
	public static final short CTX_ELSE = 3;
	public static final short CTX_FOR = 4;
	public static final short CTX_WHILE = 5;
	public static final short CTX_DO_WHILE = 6;
	public static final short CTX_CFC = 7;
	public static final short CTX_INTERFACE = 8;
	public static final short CTX_FUNCTION = 9;
	public static final short CTX_BLOCK = 10;
	public static final short CTX_FINALLY = 11;
	public static final short CTX_SWITCH = 12;
	public static final short CTX_TRY = 13;
	public static final short CTX_CATCH = 14;
	public static final short CTX_TRANSACTION = 15;
	public static final short CTX_THREAD = 16;
	public static final short CTX_SAVECONTENT = 17;
	public static final short CTX_LOCK = 18;
	public static final short CTX_LOOP = 19;
	public static final short CTX_QUERY = 20;
	public static final short CTX_ZIP = 21;
	
	
	

	private final static TagLibTagAttr UNDEFINED=new TagLibTagAttr(null);
	
	private TagLibTag tag;
	private boolean rtexpr;
	private short type=TYPE_NONE;
	private TagLibTagAttr singleAttr=UNDEFINED;
	private short context=CTX_OTHER;


	public TagLibTagScript(TagLibTag tag) {
		this.tag=tag;
	}

	public void setType(String type) {
		if(!StringUtil.isEmpty(type,true))  {
			type=type.trim().toLowerCase();
			if("single".equals(type)) this.type=TYPE_SINGLE;
			else if("multiple".equals(type)) this.type=TYPE_MULTIPLE;
		}
	}

	public void setRtexpr(boolean rtexpr) {
		this.rtexpr=rtexpr;
	}

	/**
	 * @return the tag
	 */
	public TagLibTag getTag() {
		return tag;
	}

	/**
	 * @return the rtexpr
	 */
	public boolean getRtexpr() {
		return rtexpr;
	}

	/**
	 * @return the type
	 */
	public short getType() {
		return type;
	}


	public String getTypeAsString() {
		if(type==TYPE_MULTIPLE) return "multiple";
		if(type==TYPE_SINGLE) return "single";
		return "none";
	}
	
	public TagLibTagAttr getSingleAttr() {
		if(singleAttr==UNDEFINED) {
			singleAttr=null;
			Iterator<TagLibTagAttr> it = tag.getAttributes().values().iterator();
			TagLibTagAttr attr;
			while(it.hasNext()){
				attr=it.next();
				if(attr.getScriptSupport()!=TagLibTagAttr.SCRIPT_SUPPORT_NONE){
					singleAttr=attr;
					break;
				}	
			}
		}
		return singleAttr;
	}

	public void setContext(String str) {
		if(!StringUtil.isEmpty(str,true))  {
			str=str.trim().toLowerCase();
			if("none".equals(str)) this.context=CTX_NONE;
			else if("if".equals(str)) this.context=CTX_IF;
			else if("elseif".equals(str)) this.context=CTX_ELSE_IF;
			else if("else".equals(str)) this.context=CTX_ELSE;
			else if("for".equals(str)) this.context=CTX_FOR;
			else if("while".equals(str)) this.context=CTX_WHILE;
			else if("dowhile".equals(str)) this.context=CTX_DO_WHILE;
			else if("cfc".equals(str)) this.context=CTX_CFC;
			else if("component".equals(str)) this.context=CTX_CFC;
			else if("interface".equals(str)) this.context=CTX_INTERFACE;
			else if("function".equals(str)) this.context=CTX_FUNCTION;
			else if("block".equals(str)) this.context=CTX_BLOCK;
			else if("finally".equals(str)) this.context=CTX_FINALLY;
			else if("switch".equals(str)) this.context=CTX_SWITCH;
			else if("try".equals(str)) this.context=CTX_TRY;
			else if("catch".equals(str)) this.context=CTX_CATCH;
			else if("transaction".equals(str)) this.context=CTX_TRANSACTION;
			else if("thread".equals(str)) this.context=CTX_THREAD;
			else if("savecontent".equals(str)) this.context=CTX_SAVECONTENT;
			else if("lock".equals(str)) this.context=CTX_LOCK;
			else if("loop".equals(str)) this.context=CTX_LOOP;
			else if("query".equals(str)) this.context=CTX_QUERY;
			else if("zip".equals(str)) this.context=CTX_ZIP;
		}
	}
	
	/**
	 * @return the context
	 */
	public short getContext() {
		return context;
	}

	
}
