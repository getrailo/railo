package railo.transformer.cfml.tag;

import railo.commons.lang.StringUtil;
import railo.runtime.SourceFile;
import railo.runtime.op.Caster;
import railo.transformer.util.CFMLString;

/**
 * Die Klasse TemplateException wird durch den CFMLTransformer geworfen, 
 * wenn dieser auf einen grammatikalischen Fehler in dem zu verarbeitenden CFML Code stösst 
 * oder wenn ein Tag oder eine Funktion von der 
 * Definition innerhalb der Tag- bzw. der Funktions- Library abweicht.
 */
public final class CFMLTransformerException extends Exception {
	private CFMLString cfml;
	//private String htmlMessage;
	
	/**
	 * Konstruktor mit einem CFMLString und einer anderen Exception.
	 * @param cfml
	 * @param e
	 */
	public CFMLTransformerException(CFMLString cfml, Exception e) {
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
	public CFMLTransformerException(CFMLString cfml,String message) {
		super(message);
		this.cfml=cfml;
		
	}

	/**
	 * Gibt eine detaillierte Fehlermeldung zurück.
	 * Überschreibt toString Methode von java.lang.Objekt, alias für getMessage().
	 * @return Fehlermeldung als Plain Text Ausgabe
	 */
	public String toString()	{
		boolean hasCFML=cfml!=null;
		StringBuffer sb=new StringBuffer();
		sb.append("Error\n");
		sb.append("----------------------------------\n");
		if(hasCFML && cfml.getSourceFile()!=null) {
			sb.append("File: "+cfml.getSourceFile().getDisplayPath()+"\n");
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
	
	/* *
	 * Gibt eine detaillierte Fehlermeldung als HTML Ausgabe zurück.
	 * @return Fehlermeldung als HTML Ausgabe.
	 * /
	public String getMessageAsHTML()	{
		boolean hasCFML=cfml!=null;
		
		String str=HTMLOutput.getStyle("fnf","#ff4400","#ff954f","#4f1500");

		str+=HTMLOutput.getHead("fnf","Railo - ParserException");
		if(hasCFML && cfml.getSourceFile()!=null) {
			str+=HTMLOutput.getItem("fnf","File",cfml.getSourceFile().getDisplayPath());
		}
		if(hasCFML) {
			int line=cfml.getLine();
			str+=HTMLOutput.getItem("fnf","Line", line+"");
			str+=HTMLOutput.getItem("fnf","Column", cfml.getColumn()+"");
			str+=HTMLOutput.getItem("fnf","Type", "Syntax");
			int failureLine=line;
			line=(line-2<1)?1:line-2;
			int lineDescLen=(((line+5)+"").length());
			int counter=0;
			StringBuffer sb=new StringBuffer();
			for(int i=line;;i++) {
				if(i>0) {
					String strLine=cfml.getLineAsString(i);
					if(strLine==null)break;
					String desc=((""+i).length()<lineDescLen)?"0"+i:""+i;
					sb.append(desc+": ");
					if(i==failureLine)sb.append("<b>");
					sb.append((strLine.replaceAll("<","&lt;").replaceAll(">","&gt;")));
					if(i==failureLine)sb.append("</b>");
					sb.append("\n");
					counter++;
				}
				if(counter==5) break;
			}
			str+=HTMLOutput.getItem("fnf","Code","<pre>"+sb+"</pre>");
		}
		str+=HTMLOutput.getItem("fnf","Message",StringUtil.replace(super.getMessage(),"\n","<br>",false));
		str+=HTMLOutput.getBottom();
		
		return str;
	}*/

	/**
	 * Gibt die Zeilennummer zurück
	 * @return Zeilennummer
	 */
	public int getLine() {
		return cfml.getLine();
	}
	
	/**
	 * Gibt die Column der aktuellen Zeile zurück
	 * @return Column der Zeile
	 */
	public int getColumn() {
		return cfml.getColumn();
	}
	
	/**
	 * Source Dokument
	 * @return Source Dokument
	 */
	public SourceFile getSource() {
		return cfml.getSourceFile();
	}

    /**
     * Returns the value of cfml.
     * @return value cfml
     */
    public CFMLString getCfml() {
        return cfml;
    }



}