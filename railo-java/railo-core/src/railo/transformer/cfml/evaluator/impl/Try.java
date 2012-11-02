package railo.transformer.cfml.evaluator.impl;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;

/**
 * Prueft den Kontext des Tag <code>try</code>.
 * Innerhalb des Tag try muss sich am Schluss 1 bis n Tags vom Typ catch befinden.
 */
public final class Try extends EvaluatorSupport {
	
	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(Element)
	 */
	public void evaluate(Tag tag) throws EvaluatorException {
		Body body=tag.getBody();
        int catchCount=0;
        int noCatchCount=0;
        int finallyCount=0;
        
        // count catch tag and other in body
        if(body!=null) {
        	List stats = body.getStatements();
    		Iterator it = stats.iterator();
        	Statement stat;
        	Tag t;
            String name;
            while(it.hasNext()) {
            	stat=(Statement) it.next();
            	if(stat instanceof Tag) {
                	t=(Tag) stat;
                	name=t.getTagLibTag().getName();
                	if(name.equals("finally")) {
                		finallyCount++;
                		noCatchCount++;
                	}
                	else if(name.equals("catch"))catchCount++;
                    else noCatchCount++;
                }
                else noCatchCount++;
            }
        }
        // check if has Content
        if(catchCount==0 && finallyCount==0)
            throw new EvaluatorException("Wrong Context, tag cftry must have at least one tag cfcatch inside or a cffinally tag.");
        if(finallyCount>1)
            throw new EvaluatorException("Wrong Context, tag cftry can have only one tag cffinally inside.");
        // check if no has Content
        if(noCatchCount==0) {
        	ASMUtil.remove(tag);
        }
		
	}
}