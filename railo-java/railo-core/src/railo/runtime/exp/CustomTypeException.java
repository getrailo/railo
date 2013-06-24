package railo.runtime.exp;

import java.util.ArrayList;
import java.util.List;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.err.ErrorPage;
import railo.runtime.type.Struct;
import railo.runtime.type.util.KeyConstants;


/**
 * Exception throwed by CFML Code
 */
public final class CustomTypeException extends PageExceptionImpl {
	
	private static final long serialVersionUID = 949287085391895177L;
	
	private int entryLevel;
	
	/**
	 * constructor of the Exception 
	 * @param message Exception Message
	 * @param detail Detailed Exception Message
	 * @param errorCode Error Code
	 * @param customType Type of the Exception
	 * @param entryLevel 
	 */
	public CustomTypeException(String message, String detail, String errorCode, String customType,String extendedinfo) {
		this(message, detail, errorCode, customType, extendedinfo, 1);
	}
	
	public CustomTypeException(String message, String detail, String errorCode, String customType,String extendedinfo, int entryLevel) {
		super(message,"custom_type",customType);
		setDetail(detail);
		setErrorCode(errorCode);
		if(extendedinfo!=null)setExtendedInfo(extendedinfo);
		this.entryLevel=entryLevel;
	}

	

	@Override
	public StackTraceElement[] getStackTrace() {
		List<StackTraceElement> list=new ArrayList<StackTraceElement>();
		StackTraceElement[] elements = super.getStackTrace();
		StackTraceElement element;
		String template;
		int level=0;
		for(int i=0;i<elements.length;i++){
			element=elements[i];
			template = element.getFileName();
			if(level<entryLevel && (element.getLineNumber()<=0 || template==null || ResourceUtil.getExtension(template,"").equals("java"))) continue;
			if(++level>=entryLevel) {
				list.add(element);
			}
		}
		if(list.size()==0) return elements;
		return list.toArray(new StackTraceElement[list.size()]);
	}

	@Override
	public CatchBlock getCatchBlock(Config config) {
		CatchBlock cb=super.getCatchBlock(config);
		cb.setEL(KeyConstants._code,cb.get(KeyConstants._errorcode,null));
		cb.setEL(KeyConstants._type,getCustomTypeAsString());
		String ei=getExtendedInfo();
		if(ei!=null)cb.setEL(KeyConstants._extended_info,ei);
		//cb.setEL("ErrorCode","");
		return cb;
	}

	@Override
	public Struct getErrorBlock(PageContext pc, ErrorPage ep) {
		Struct eb = super.getErrorBlock(pc, ep);
		eb.setEL(KeyConstants._type,getCustomTypeAsString());
		return eb;
	}

    @Override
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