package railo.transformer.cfml.tag;

import railo.commons.lang.StringUtil;
import railo.runtime.PageSource;
import railo.runtime.op.Caster;
import railo.transformer.util.SourceCode;

/**
 * Die Klasse TemplateException wird durch den CFMLTransformer geworfen, 
 * wenn dieser auf einen grammatikalischen Fehler in dem zu verarbeitenden CFML Code stoesst 
 * oder wenn ein Tag oder eine Funktion von der 
 * Definition innerhalb der Tag- bzw. der Funktions- Library abweicht.
 */
public final class CFMLTransformerException extends Exception {
	private SourceCode cfml;
	//private String htmlMessage;
	
	/**
	 * Konstruktor mit einem CFMLString und einer anderen Exception.
	 * @param cfml
	 * @param e
	 */
	public CFMLTransformerException(SourceCode cfml, Exception e) {
		this(
				cfml,
				StringUtil.isEmpty(e.getMessage())?
						(Caster.toClassName(e)):
						e.getMessage());
	}
	
	/**
	 * Konstruktor ohne Message, nur mit CFMLString.
	 * @param cfml
	 
	public TemplateException(CFMLString cfml) {
		this(cfml,"Error while transforming CFML File");
	}*/
	
	/**
	 * Hauptkonstruktor, mit CFMLString und message.
	 * @param cfml CFMLString
	 * @param message Fehlermeldung
	 */
	public CFMLTransformerException(SourceCode cfml,String message) {
		super(message);
		this.cfml=cfml;
		
	}

	/**
	 * Gibt eine detaillierte Fehlermeldung zurueck.
	 * Überschreibt toString Methode von java.lang.Objekt, alias fuer getMessage().
	 * @return Fehlermeldung als Plain Text Ausgabe
	 */
	public String toString()	{
		boolean hasCFML=cfml!=null;
		StringBuffer sb=new StringBuffer();
		sb.append("Error\n");
		sb.append("----------------------------------\n");
		if(hasCFML && cfml.getPageSource()!=null) {
			sb.append("File: "+cfml.getPageSource().getDisplayPath()+"\n");
		}
		if(hasCFML) {
			int line=cfml.getLine();
			
			int counter=0;
			sb.append("Line: "+line+"\n");
			sb.append("Column: "+cfml.getColumn()+"\n");
			sb.append("Type: Syntax\n");
			sb.append("Code Outprint: \n");
			line=(line-2<1)?1:line-2;
			int lineDescLen=(((line+5)+"").length());
			for(int i=line;;i++) {
				if(i>0) {
					String strLine=cfml.getLineAsString(i);
					if(strLine==null)break;
					String desc=((""+i).length()<lineDescLen)?"0"+i:""+i;
					sb.append(desc+": "+strLine+"\n");
					counter++;
				}
				if(counter==5) break;
			}
			sb.append("\n");
		}
		sb.append("Message:\n");
		sb.append(""+super.getMessage()+"\n");
		return sb.toString();
	}

	/**
	 * Gibt die Zeilennummer zurueck
	 * @return Zeilennummer
	 */
	public int getLine() {
		return cfml.getLine();
	}
	
	/**
	 * Gibt die Column der aktuellen Zeile zurueck
	 * @return Column der Zeile
	 */
	public int getColumn() {
		return cfml.getColumn();
	}
	
	/**
	 * Source Dokument
	 * @return Source Dokument
	 */
	public PageSource getSource() {
		return cfml.getPageSource();
	}

    /**
     * Returns the value of cfml.
     * @return value cfml
     */
    public SourceCode getCfml() {
        return cfml;
    }



}