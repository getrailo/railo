package railo.runtime.writer;

import java.io.IOException;

import javax.servlet.jsp.tagext.BodyContent;

import railo.runtime.PageContext;

public class BodyContentUtil {

	public static void clearAndPop(PageContext pc,BodyContent bc) {
		if(bc!=null){
        	bc.clearBody();
        	pc.popBody();
        }
	}
	public static void clear(BodyContent bc) {
		if(bc!=null){
        	bc.clearBody();
        }
	}

	public static void flushAndPop(PageContext pc, BodyContent bc) {
		if(bc!=null){
    		try {
				bc.flush();
			} catch (IOException e) {}
			pc.popBody();
    	}
	}

	public static void flush(BodyContent bc) {
		if(bc!=null){
    		try {
				bc.flush();
			} catch (IOException e) {}
    	}
	}

}
