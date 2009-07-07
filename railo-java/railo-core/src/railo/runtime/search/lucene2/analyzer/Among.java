package railo.runtime.search.lucene2.analyzer;

import java.lang.reflect.Method;

import net.sf.snowball.SnowballProgram;


public final class Among {
    public Among (String s, int substring_i, int result,
		  String methodname, SnowballProgram methodobject) {
        this.s_size = s.length();
        this.s = s;
        this.substring_i = substring_i;
	this.result = result;
	this.methodobject = methodobject;
	if (methodname.length() == 0) {
	    this.method = null;
	} else {
	    try {
		this.method = methodobject.getClass().
		getDeclaredMethod(methodname, new Class[0]);
	    } catch (NoSuchMethodException e) {
		// FI XME - debug message
		this.method = null;
	    }
	}
    }

    public int s_size; /* search string */
    public String s; /* search string */
    public int substring_i; /* index to longest matching substring */
    public int result;      /* result of the lookup */
    public Method method; /* method to use if substring matches */
    public SnowballProgram methodobject; /* object to invoke method on */
   
};