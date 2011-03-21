package railo.runtime.exp;

import railo.runtime.PageContext;
import railo.runtime.err.ErrorPage;
import railo.runtime.type.Struct;


/**
 * Exception throwed by Cold Fusion Code
 */
public final class CustomTypeException extends PageExceptionImpl {
	
	/**
	 * constructor of the Exception 
	 * @param message Exception Message
	 * @param detail Detailed Exception Message
	 * @param errorCode Error Code
	 * @param customType Type of the Exception
	 */
	public CustomTypeException(String message, String detail, String errorCode, String customType,String extendedinfo) {
		super(message,"custom_type",customType);
		setDetail(detail);
		setErrorCode(errorCode);
		if(extendedinfo!=null)setExtendedInfo(extendedinfo);
	}

	/**
	 *
	 * @see railo.runtime.exp.PageExceptionImpl#getCatchBlock(railo.runtime.PageContext)
	 */
	public Struct getCatchBlock(PageContext pc) {
		Struct cb=super.getCatchBlock(pc);
		cb.setEL("code",cb.get("errorcode",null));
		cb.setEL("type",getCustomTypeAsString());
		String ei=getExtendedInfo();
		if(ei!=null)cb.setEL("extended_info",ei);
		//cb.setEL("ErrorCode","");
		return cb;
	}

	/**
	 * @see railo.runtime.exp.IPageException#getErrorBlock(railo.runtime.PageContext, railo.runtime.err.ErrorPage)
	 */
	public Struct getErrorBlock(PageContext pc, ErrorPage ep) {
		Struct eb = super.getErrorBlock(pc, ep);
		eb.setEL("type",getCustomTypeAsString());
		return eb;
	}

    /**
     * @see railo.runtime.exp.IPageException#typeEqual(java.lang.String)
     */
    public boolean typeEqual(String type) {
    	if(type==null) return true;
        type=type.toLowerCase().trim();
        if(type.equals("any")) return true;
        
        // Custom Type
        if(getTypeAsString().equals("custom_type") || getTypeAsString().equals("customtype")) {
            return compareCustomType(type,getCustomTypeAsString().toLowerCase().trim());
        }
        return super.typeEqual(type);
    }
    
    /**
     * @param leftType
     * @param rightType
     * @return is same custom type
     */
    private boolean compareCustomType(String leftType, String rightType) {
        int left=leftType.length();
        int right=rightType.length();
        if(left>right) return false;
        if(left==right) return leftType.equals(rightType);
        
        for(int i=0;i<left;i++) {
            if(leftType.charAt(i)!=rightType.charAt(i)) return false;
        }
        return rightType.charAt(left)=='.';
    }
}