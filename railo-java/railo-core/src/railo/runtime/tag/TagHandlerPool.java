package railo.runtime.tag;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.tagext.Tag;

import java.util.Stack;

import railo.commons.lang.ClassUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

// TODO kann man nicht auf context ebene

/**
 * Pool to Handle Tags
 */
public final class TagHandlerPool {
	//private Map<String,Data> map=new HashMap<String,Data>();
	private Map<String,Stack<Tag>> map=new HashMap<String,Stack<Tag>>();
	
	
	
	
	//private static Data[] datas=new Data[100];
	
	/**
	 * return a tag to use from a class
	 * @param tagClass
	 * @return Tag
	 * @throws PageException
	 */
	public synchronized Tag use(String tagClass) throws PageException {
		Stack<Tag> stack = getStack(tagClass);
		Tag tag=null;
        if(!stack.isEmpty())tag=stack.pop();
		if(tag!=null) return tag;
        
		return loadTag(tagClass);
	}

	/**
	 * free a tag for reusing
	 * @param tag
	 * @throws ExpressionException
	 */
	public synchronized void reuse(Tag tag) {
		tag.release();
		getStack(tag.getClass().getName()).add(tag);
	}
	
	
	private Tag loadTag(String tagClass) throws PageException {
		try {
			Class<Tag> clazz = ClassUtil.loadClass(tagClass);
			return clazz.newInstance();
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

	private Stack<Tag> getStack(String tagClass) {
		Stack<Tag> stack = map.get(tagClass);
        if(stack==null) {
			stack=new Stack<Tag>();
			map.put(tagClass,stack);
		}
        return stack;
	}

	public void reset() {
		map.clear();
	}
}