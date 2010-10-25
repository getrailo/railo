package railo.transformer.cfml.evaluator;

import java.util.Vector;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

/**
 *
 * Wenn der CFML Transformer w�hrend des �bersetzungsprozess auf einen Tag st�sst, 
 * pr�ft er mithilfe der passenden TagLib, 
 * ob dieses Tag eine Evaluator definiert hat. 
 * Wenn ein Evaluator definiert ist, kann der CFML Transformer diesen aber nicht sofort aufrufen, 
 * da zuerst das komplette Dokument �bersetzt werden muss, 
 * bevor ein Evaluator aufgerufen werden kann.
 * Hier kommt der EvaluatorPool zum Einsatz, 
 * der CFMLTransfomer �bergibt den Evaluator den er von der TagLib erhalten hat, 
 * an den EvaluatorPool weiter. 
 * Sobald der CFMLTransfomer den �bersetzungsprozess abgeschlossen hat, 
 * ruft er dann den EvaluatorPool auf und dieser ruft dann alle Evaluatoren auf die im �bergeben wurden. 

 */
public final class EvaluatorPool {
	
	Vector v=new Vector();
	
	/**
	 * Diese Methode wird aufgerufen um eine neue Methode in den Pool zu spielen.
	 * @param libTag  Die Definition des Tag aus der TLD.
	 * @param cfxdTag Das konkrete Tag innerhalb der kompletten CFXD.
	 * @param flibs S�mtliche Function Library Deskriptoren des aktuellen Tag Libray Deskriptors.
	 * @param cfml CFMLString des aktuellen �bersetzungsprozess.
	 */
	public void add(TagLibTag libTag,Tag tag, FunctionLib[] flibs, CFMLString cfml) {
		v.add(new EvaluatorData(libTag,tag,flibs,cfml));
	}

	/**
	 * Die Methode run wird aufgerufen sobald, der CFML Transformer den �bersetzungsprozess angeschlossen hat.
	 * Die metode run rauft darauf alle Evaluatoren auf die intern gespeicher wurden und l�scht den internen Speicher.
	 * @throws TemplateException
	 */
	public void run() throws TemplateException  {
		int size=v.size();
		for(int i=0;i<size;i++) {
			EvaluatorData ec=(EvaluatorData)v.elementAt(i);
			CFMLString cfml=ec.getCfml();
			cfml.setPos(ec.getPos());
			try {
				if(ec.getLibTag().getEvaluator()!=null)ec.getLibTag().getEvaluator().evaluate(
						ec.getTag(),
						ec.getLibTag(),
						ec.getFlibs());
			} catch (EvaluatorException e) {
			    v.clear();//print.printST(e);
				throw new TemplateException(cfml,e);
			}catch (Throwable e) {
			    v.clear();
				throw new TemplateException(cfml,e);
			}
			
		}
		v.clear();
	}

	/**
	 *
	 *
	 * Die interne Klasse EvaluatorData dient zum Zwischenspeichern aller Daten 
	 * die ben�tigt werden einen einzelnen Evaluator aufzurufen. 
	 */
	class EvaluatorData {
		TagLibTag libTag;
		Tag tag; 
		FunctionLib[] flibs; 
		CFMLString cfml;
		int pos;
		
		/**
		* Konstruktor von EvaluatorData.
		* @param libTag  Die Definition des Tag aus der TLD.
	 	* @param cfxdTag Das konkrete Tag innerhalb der kompletten CFXD.
	 	* @param flibs S�mtliche Function Library Deskriptoren des aktuellen Tag Libray Deskriptors.
	 	* @param cfml CFMLString des aktuellen �bersetzungsprozess.
	 	*/
		public EvaluatorData(TagLibTag libTag,Tag tag, FunctionLib[] flibs, CFMLString cfml) {
			this.libTag=libTag;
			this.tag=tag;
			this.flibs=flibs;
			this.cfml=cfml;
			this.pos=cfml.getPos();
		}
		
		/**
		 * Gibt den aktuellen CFMLString zur�ck.
		 * @return CFMLString des aktuellen �bersetzungsprozess.
		 */
		public CFMLString getCfml() {
			return cfml;
		}

		/**
		 * Gibt den zu verarbeitenden Tag zur�ck.
		 * @return Das konkrete Tag innerhalb der kompletten CFXD.
		 */
		public Tag getTag() {
			return tag;
		}

		/**
		 * Gibt s�mtliche Function Library Deskriptoren des aktuellen Tag Libray Deskriptors zur�ck. 
		 * @return S�mtliche Function Library Deskriptoren.
		 */
		public FunctionLib[] getFlibs() {
			return flibs;
		}

		/**
		 * Die Definition des aktuellen tags aus der TLD
		 * @return den aktuellen TagLibTag.
		 */
		public TagLibTag getLibTag() {
			return libTag;
		}

		/**
		 * Die Position des zu verarbeitenden Tag innerhalb der CFML Seite.
		 * @return Position des Tag.
		 */
		public int getPos() {
			return pos;
		}

	}

    /**
     * clears the ppol
     */
    public void clear() {
        v.clear();
    }

	/*public static void getPool() {
		// TODO Auto-generated method stub
		
	}*/

}