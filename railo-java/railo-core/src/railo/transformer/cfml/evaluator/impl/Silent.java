package railo.transformer.cfml.evaluator.impl;

import org.w3c.dom.Element;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;

/**
 * Prüft den Kontext des Tag <code>try</code>.
 * Innerhalb des Tag try muss sich am Schluss 1 bis n Tags vom Typ catch befinden.
 */
public final class Silent extends EvaluatorSupport {
	
	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(Element)
	 */
	public void evaluate(Tag tag) throws EvaluatorException {
		ASMUtil.removeLiterlChildren(tag,true);
		/*Body body=tag.getBody();
        
		//tag.getTagLibTag().
        // count catch tag and other in body
        if(body!=null) {
        	List stats = body.getStatements();
    		Iterator it = stats.iterator();
        	Statement stat;
        	Tag t;
            
            while(it.hasNext()) {
            	stat=(Statement) it.next();
            	if(stat instanceof Tag) {
                	t=(Tag) stat;
                	if(t.getTagLibTag().getName().equals("catch"))catchCount++;
                    else noCatchCount++;
                }
                else noCatchCount++;
            }
        }*/
        
        
		
	}
}