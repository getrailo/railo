package railo.runtime.interpreter;

import railo.commons.lang.NumberUtil;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.literal.LStringBuffer;

public class JSONExpressionInterpreter extends CFMLExpressionInterpreter {
	public JSONExpressionInterpreter(){
		this(false);
		allowNullConstant=true;
    }
	
	public JSONExpressionInterpreter(boolean strict){// strict is set to true, it should not be compatible with CFMLExpressionInterpreter
		allowNullConstant=true;
		
    }
        
	@Override
    protected Ref string() throws PageException {
        
        // Init Parameter
        char quoter = cfml.getCurrentLower();
        //String str="";
        LStringBuffer str=new LStringBuffer();
        
        while(cfml.hasNext()) {
            cfml.next();
            // check sharp
            if(cfml.isCurrent('\\')) {
            	if(cfml.isNext(quoter)){
                    cfml.next();
                    str.append(quoter);
                }
            	else if(cfml.isNext('\\')){
                    cfml.next();
                    str.append('\\');
                }
            	else if(cfml.isNext('"')){
                    cfml.next();
                    str.append('"');
                }
            	else if(cfml.isNext('\'')){
                    cfml.next();
                    str.append('\'');
                }
            	else if(cfml.isNext('t')){
                    cfml.next();
                    str.append('\t');
                }
            	else if(cfml.isNext('n')){
                    cfml.next();
                    str.append('\n');
                }
            	else if(cfml.isNext('b')){
                    cfml.next();
                    str.append('\b');
                }
            	else if(cfml.isNext('f')){
                    cfml.next();
                    str.append('\f');
                }
            	else if(cfml.isNext('r')){
                    cfml.next();
                    str.append('\r');
                }
            	else if(cfml.isNext('u')){
                    cfml.next();
                    StringBuffer sb=new StringBuffer();
                    int i=0;
                    
                    for(;i<4 && cfml.hasNext();i++){
                    	cfml.next();
                    	sb.append(cfml.getCurrent());
                    }
                    if(i<4){
                    	str.append("\\u");
                    	str.append(sb.toString());
                    }
                    else{
                    	int asc = NumberUtil.hexToInt(sb.toString(),-1);
                    	if(asc!=-1)str.append((char)asc);
                    	else {
                    		str.append("\\u");
                        	str.append(sb.toString());
                    	}
                    }   
                    
                }
            	else if(cfml.isNext('/')){
                    cfml.next();
                    str.append('/');
                }
                else {
                	str.append('\\');
                }     
            }
            else if(cfml.isCurrent(quoter)) {
                break;          
            }
            // all other character
            else {
                str.append(cfml.getCurrent());
            }
        }
        if(!cfml.forwardIfCurrent(quoter))
            throw new InterpreterException("Invalid String Literal Syntax Closing ["+quoter+"] not found");
        
        cfml.removeSpace();
        mode=STATIC;
        /*Ref value=null;
        if(value!=null) {
            if(str.isEmpty()) return value;
            return new Concat(pc,value,str);
        }*/
        return str;
    }

}
