package railo.runtime.functions.displayFormatting;

import java.nio.charset.Charset;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import railo.commons.digest.MD5;
import railo.commons.io.CharsetUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public class HMAC implements Function {

	private static final long serialVersionUID = -1999122154087043893L;

	public static String call(PageContext pc,Object oMessage, Object oKey) throws PageException {
		return call(pc, oMessage, oKey, null, null);
	}
	
	public static String call(PageContext pc,Object oMessage, Object oKey, String algorithm) throws PageException {
		return call(pc, oMessage, oKey, algorithm, null);
	}
	
	public static String call(PageContext pc,Object oMessage, Object oKey, String algorithm, String charset) throws PageException {
		// charset
        if(StringUtil.isEmpty(charset,true))
            charset=pc.getConfig().getWebCharset();
		Charset cs = CharsetUtil.toCharset(charset);

        // message
		byte[] msg=toBinary(oMessage,cs);
		
        // message
		byte[] key=toBinary(oKey,cs);
		
		// algorithm
        if(StringUtil.isEmpty(algorithm,true)) algorithm = "HmacMD5";
        
        SecretKey sk = new SecretKeySpec(key, algorithm);
	    try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(sk);
            mac.reset();
            mac.update(msg);
            msg = mac.doFinal();
            return MD5.stringify(msg).toUpperCase();
        }
        catch(Exception e) {
            throw Caster.toPageException(e);
        }
	}
	
	private static byte[] toBinary(Object obj, Charset cs) throws PageException {
		if(Decision.isBinary(obj)){
			return Caster.toBinary(obj);
		}
		return Caster.toString(obj).getBytes(cs);
	}
}
