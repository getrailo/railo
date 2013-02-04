package railo.runtime.tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.ClassUtil;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

// TODO kann man nicht auf context ebene

/**
 * Pool to Handle Tags
 */
public final class TagHandlerPool {
	private Map<String,Stack<Tag>> map=new HashMap<String,Stack<Tag>>();
	private ConfigWeb config;
	
	public TagHandlerPool(ConfigWeb config) { 
		this.config=config;
	}

	/**
	 * return a tag to use from a class
	 * @param tagClass
	 * @return Tag
	 * @throws PageException
	 */
	public Tag use(String tagClass) throws PageException {
		Stack<Tag> stack = getStack(tagClass);
		synchronized (stack) {
			if(!stack.isEmpty()){
	        	Tag tag=stack.pop();
	        	if(tag!=null) return tag;
	        }
		}
		return loadTag(tagClass);
	}

	/**
	 * free a tag for reusing
	 * @param tag
	 * @throws ExpressionException
	 */
	public synchronized void reuse(Tag tag) {
		tag.release();
		Stack<Tag> stack = getStack(tag.getClass().getName());
		synchronized (stack) {
			stack.add(tag);
		}
	}
	
	
	private Tag loadTag(String tagClass) throws PageException {
		try {
			Class<Tag> clazz = ClassUtil.loadClass(config.getClassLoader(),tagClass);
			return clazz.newInstance();
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

	private Stack<Tag> getStack(String tagClass) {
		synchronized (map) {
			Stack<Tag> stack = map.get(tagClass);
	        if(stack==null) {
				stack=new Stack<Tag>();
				map.put(tagClass,stack);
			}
	        return stack;
		}
	}

	public void reset() {
		map.clear();
	}
}