package railo.transformer.cfml;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.util.CFMLString;

/**
 * Innerhalb einer TLD (Tag Library Descriptor) kann eine Klasse angemeldet werden, 
 * welche das Interface ExprTransfomer implementiert, 
 * um Ausdr�cke die innerhalb von Attributen und dem Body von Tags vorkommen zu transformieren. 
 * Die Idee dieses Interface ist es die M�glichkeit zu bieten, 
 * weitere ExprTransfomer zu erstellen zu k�nnen, 
 * um f�r verschiedene TLD, verschiedene Ausdrucksarten zu bieten. 
 *
 */
public interface ExprTransformer {

	/**
	* Wird aufgerufen um aus dem �bergebenen CFMLString einen Ausdruck auszulesen 
	 * und diesen in ein CFXD Element zu �bersetzten.
	 * <br>
	 * Beispiel eines �bergebenen String:<br>
	 * "session.firstName" oder "trim(left('test'&var1,3))"
	 * 
	 * @param fld Array von Function Libraries, 
	 * Mithilfe dieser Function Libraries kann der Transfomer buil-in Funktionen innerhalb des CFML Codes erkennen 
	 * und validieren.
	 * @param doc XML Document des aktuellen zu erstellenden CFXD
	 * @param cfml Text der transfomiert werden soll.
	 * @return Element CFXD Element
	 * @throws railo.runtime.exp.TemplateException 
	 * @throws TemplateException
	 */
	public Expression transform(EvaluatorPool ep,FunctionLib[] fld,CFMLString cfml) throws TemplateException;
	
	/**
	* Wird aufgerufen um aus dem �bergebenen CFMLString einen Ausdruck auszulesen 
	 * und diesen in ein CFXD Element zu �bersetzten. Es wird aber davon ausgegangen das es sich um einen String handelt.
	 * <br>
	 * Beispiel eines �bergebenen String:<br>
	 * "session.firstName" oder "trim(left('test'&var1,3))"
	 * 
	 * @param fld Array von Function Libraries, 
	 * Mithilfe dieser Function Libraries kann der Transfomer buil-in Funktionen innerhalb des CFML Codes erkennen 
	 * und validieren.
	 * @param doc XML Document des aktuellen zu erstellenden CFXD
	 * @param cfml Text der transfomiert werden soll.
	 * @return Element CFXD Element
	 * @throws TemplateException
	 */
	public Expression transformAsString(EvaluatorPool ep,FunctionLib[] fld,CFMLString cfml, boolean allowLowerThan) throws TemplateException;
}