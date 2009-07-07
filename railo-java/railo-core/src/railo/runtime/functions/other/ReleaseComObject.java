package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.com.COMObject;
import railo.runtime.ext.function.Function;

public final class ReleaseComObject implements Function {

    public static Object call(PageContext pc, Object obj) {
        if(obj instanceof COMObject) ((COMObject)obj).release();
        return null;
    }

}