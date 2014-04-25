package railo.transformer.cfml.evaluator;

import railo.runtime.config.Config;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.Data;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;




/**
 * Die Klasse EvaluatorSupport hat die Aufgabe, 
 * Zugriffe auf die CFXD zu vereinfachen. 
 * Dazu stellt die Klasse mehrere Methoden zur Verfuegung die verschiedene, immer wieder verwendete Abfragen 
 * abbilden. 
 * Die Klasse implementiert das Interface Evaluator.
 * Desweiteren splittet diese Klasse auch die Methode evaluate in drei Methoden auf so, 
 * das man eine hoehere flexibilitaet beim Einstiegspunkt einer konkreten Implementation hat.
 * 
 */
public class EvaluatorSupport implements Evaluator {


	/**
	 * Die Methode execute wird aufgerufen, wenn der Context eines Tags geprueft werden soll.
	 * Diese Methode ueberschreibt, jene des Interface Evaluator.
	 * Falls diese Methode durch eine Implementation nicht ueberschrieben wird, ruft sie wiederere, 
	 * allenfalls implementierte evaluate Methoden auf.
	 * Mit Hilfe dieses Konstrukt ist es moeglich drei evaluate methoden anzubieten.
	 * @param cfxdTag Das konkrete Tag innerhalb der kompletten CFXD.
	 * @param libTag Die Definition des Tag aus der TLD.
	 * @param flibs Saemtliche Function Library Deskriptoren des aktuellen Tag Libray Deskriptors.
	 * @param srcCode
	 * @return TagLib
	 * @throws TemplateException
	*/
	public TagLib execute(Config config,Tag tag, TagLibTag libTag, FunctionLib[] flibs,Data data) 
        throws TemplateException {
	    
	    return null;
	}
	
	

	/**
	 * Die Methode evaluate wird aufgerufen, wenn der Context eines Tags geprueft werden soll.
	 * Diese Methode ueberschreibt, jene des Interface Evaluator.
	 * Falls diese Methode durch eine Implementation nicht ueberschrieben wird, ruft sie wiederere, 
	 * allenfalls implementierte evaluate Methoden auf.
	 * Mit Hilfe dieses Konstrukt ist es moeglich drei evaluate methoden anzubieten.
	 * @param cfxdTag Das konkrete Tag innerhalb der kompletten CFXD.
	 * @param libTag Die Definition des Tag aus der TLD.
	 * @param flibs Saemtliche Function Library Deskriptoren des aktuellen Tag Libray Deskriptors.
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