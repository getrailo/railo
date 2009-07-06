package railo.transformer.cfml.evaluator;

import railo.runtime.config.Config;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

/**
 * Jede Klasse die als Evaluator verwendet werden soll, 
 * muss das Interface Evaluator implementieren. 
 * Das Interface Evaluator definiert also die gemeinsame 
 * Schnittstelle für alle Evaluatoren. 
 */
public interface Evaluator {

	/**
	 * Die Methode evaluate wird aufgerufen, wenn der Context eines Tags geprüft werden soll.
	 * @param config 
	 * @param cfxdTag Das konkrete Tag innerhalb der kompletten CFXD.
	 * @param libTag Die Definition des Tag aus der TLD.
	 * @param flibs Sämtliche Function Library Deskriptoren des aktuellen Tag Libray Deskriptors.
	 * @param cfml
	 * @return changed talib
	 * @throws TemplateException
	*/
	public TagLib execute(Config config,Tag tag, TagLibTag libTag, FunctionLib[] flibs,CFMLString cfml) throws TemplateException;

	/**
	 * Die Methode evaluate wird aufgerufen, wenn der Context eines Tags geprüft werden soll,
	 * nachdem die komplette Seite uebersetzt wurde.
	 * @param cfxdTag Das konkrete Tag innerhalb der kompletten CFXD.
	 * @param libTag Die Definition des Tag aus der TLD.
	 * @param flibs Sämtliche Function Library Deskriptoren des aktuellen Tag Libray Deskriptors.
	 * @throws EvaluatorException
	*/
	public void evaluate(Tag tag, TagLibTag libTag, FunctionLib[] flibs) throws EvaluatorException;
	
	
}