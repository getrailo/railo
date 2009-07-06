package railo.transformer.cfml.evaluator;

import railo.runtime.config.Config;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;




/**
 * Die Klasse EvaluatorSupport hat die Aufgabe, 
 * Zugriffe auf die CFXD zu vereinfachen. 
 * Dazu stellt die Klasse mehrere Methoden zur Verfügung die verschiedene, immer wieder verwendete Abfragen 
 * abbilden. 
 * Die Klasse implementiert das Interface Evaluator.
 * Desweiteren splittet diese Klasse auch die Methode evaluate in drei Methoden auf so, 
 * das man eine höhere flexibilität beim Einstiegspunkt einer konkreten Implementation hat.
 * 
 */
public class EvaluatorSupport implements Evaluator {


	/**
	 * Die Methode execute wird aufgerufen, wenn der Context eines Tags geprüft werden soll.
	 * Diese Methode überschreibt, jene des Interface Evaluator.
	 * Falls diese Methode durch eine Implementation nicht überschrieben wird, ruft sie wiederere, 
	 * allenfalls implementierte evaluate Methoden auf.
	 * Mit Hilfe dieses Konstrukt ist es möglich drei evaluate methoden anzubieten.
	 * @param cfxdTag Das konkrete Tag innerhalb der kompletten CFXD.
	 * @param libTag Die Definition des Tag aus der TLD.
	 * @param flibs Sämtliche Function Library Deskriptoren des aktuellen Tag Libray Deskriptors.
	 * @param cfml
	 * @return TagLib
	 * @throws TemplateException
	*/
	public TagLib execute(Config config,Tag tag, TagLibTag libTag, FunctionLib[] flibs,CFMLString cfml) 
        throws TemplateException {
	    
	    return null;
	}
	
	

	/**
	 * Die Methode evaluate wird aufgerufen, wenn der Context eines Tags geprüft werden soll.
	 * Diese Methode überschreibt, jene des Interface Evaluator.
	 * Falls diese Methode durch eine Implementation nicht überschrieben wird, ruft sie wiederere, 
	 * allenfalls implementierte evaluate Methoden auf.
	 * Mit Hilfe dieses Konstrukt ist es möglich drei evaluate methoden anzubieten.
	 * @param cfxdTag Das konkrete Tag innerhalb der kompletten CFXD.
	 * @param libTag Die Definition des Tag aus der TLD.
	 * @param flibs Sämtliche Function Library Deskriptoren des aktuellen Tag Libray Deskriptors.
	 * @throws EvaluatorException
	*/
	public void evaluate(Tag tag, TagLibTag libTag, FunctionLib[] flibs) throws EvaluatorException {
		evaluate(tag);
		evaluate(tag,libTag);
	}
	
	/**
	 * Überladene evaluate Methode nur mit einem CFXD Element.
	 * @param cfxdTag
	 * @throws EvaluatorException
	 */
	public void evaluate(Tag tag) throws EvaluatorException {
		
	}
	
	/**
	 * Überladene evaluate Methode mit einem CFXD Element und einem TagLibTag.
	 * @param cfxdTag
	 * @param libTag
	 * @throws EvaluatorException
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException {
	}
	
}