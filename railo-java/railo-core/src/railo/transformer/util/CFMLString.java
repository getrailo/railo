package railo.transformer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import railo.commons.io.IOUtil;
import railo.commons.lang.ClassUtil;
import railo.runtime.SourceFile;

/**
 	Der CFMLString ist eine Hilfe f￼r die Transformer, 
 	er reprﾊsentiert den CFML Code und bietet Methoden an, 
 	um alle nﾚtigen Informationen auszulesen und Manipulationen durchzuf￼hren. 
 	Dies um, innerhalb des Transformer, wiederkehrende Zeichenketten-Manipulationen zu abstrahieren.
 *
 */
public final class CFMLString {

	/**
	 * Mindestens einen Space
	 */
	public static final short AT_LEAST_ONE_SPACE=0;
	
	/**
	 * Mindestens ein Space
	 */
	public static final short ZERO_OR_MORE_SPACE=1;
	
	/**
	 * Field <code>pos</code>
	 */
	protected int pos=0;
	/**
	 * Field <code>text</code>
	 */
	protected char[] text;
	/**
	 * Field <code>lcText</code>
	 */
	protected char[] lcText;
	/**
	 * Field <code>lines</code>
	 */
	protected Integer[] lines;// TODO to int[]
	/**
	 * Field <code>file</code>
	 */
	protected SourceFile sf;

	private String charset;

	private boolean writeLog;

	private String source;
	
	private static final String NL=System.getProperty("line.separator");

	
	
	public CFMLString(SourceFile sf,String charset,boolean writeLog) throws IOException {
		this.writeLog=writeLog;
		this.charset=charset;
		this.sf=sf;
		this.source=sf.getPhyscalFile().getAbsolutePath();
		String content;
		InputStream is=null;
		try {
			is = IOUtil.toBufferedInputStream(sf.getPhyscalFile().getInputStream());
			if(ClassUtil.isBytecodeStream(is))throw new AlreadyClassException(sf.getPhyscalFile());
			content=IOUtil.toString(is,charset);
			
		}
		finally {
			IOUtil.closeEL(is);
		}
		init(content.toString().toCharArray());
	}

	/**
	 * Constructor of the class
	 * in this case source file is just for information
	 * @param text
	 * @param charset
	 * @param writeLog
	 * @param sf
	 */
	public CFMLString(String text,String charset,boolean writeLog,SourceFile sf) {
		init(text.toCharArray());
		this.charset=charset;
		this.writeLog=writeLog;
		this.sf=sf;
	}


	/**
	 * Diesen Konstruktor kann er CFML Code als Zeichenkette ￼bergeben werden.
	 * @param text CFML Code
	 */
	public CFMLString(String text,String charset) {
		init(text.toCharArray());
		this.charset=charset;
		this.writeLog=false;
	}
	
	/**
	 * Gemeinsame Initialmethode der drei Konstruktoren, diese erh￤lt den CFML Code als 
	 * char[] und ￼bertr￤gt ihn, in die interen Datenhaltung. 
	 * @param text
	 */
	protected void init(char[] text) {
		this.text=text;
		lcText=new char[text.length];
		
		ArrayList<Integer> arr=new ArrayList<Integer>();
		
		for(int i=0;i<text.length;i++) {
			pos=i;
			if(text[i]=='\n') {
				arr.add(new Integer(i));
				lcText[i]=' ';
			}
			else if(text[i]=='\r') {
				if(isNextRaw('\n')){
					lcText[i++]=' ';
				}
				arr.add(new Integer(i));
				lcText[i]=' ';
			}
			else if(text[i]=='\t') lcText[i]=' ';
			else lcText[i]=Character.toLowerCase(text[i]);
		}
		pos=0;
		arr.add(new Integer(text.length));
		lines=(Integer[])arr.toArray(new Integer[arr.size()]);
	}
	
	/**
	 * Privater Konstruktor der direkt die innere Struktur als Eingabe erh￤lt
	 * und diese nicht mehr, interpretieren muss.
	 * @param pos Positio0n des Zeigers
	 * @param text CFML Code
	 * @param lcText CFML Code in Kleinbuchstaben
	 * @param lines Declaration der Zeilenumbr￼che
	 
	private CFMLString(int pos, char[] text, char[] lcText, Integer[] lines) {
		this.pos=pos;
		this.text=text;
		this.lcText=lcText;
		this.lines=lines;
	}*/

	/**
	 * Gibt zur￼ck ob, 
	 * ausgehend von der aktuellen Position des internen Zeigers im Text,
	 * noch ein Zeichen vorangestellt ist.
	 * @return boolean Existiert ein weieters Zeichen nach dem Zeiger.
	 */
	public boolean hasNext()  {
		return pos+1<text.length;
	}

	/**
	 * Stellt den internen Zeiger auf die n￤chste Position. 
	 * ￜberlappungen ausserhalb des Index des Textes werden ignoriert.
	*/
	public void next(){
		pos++;
	}
	/**
	 * Stellt den internen Zeiger auf die vorhergehnde Position. 
	 * ￜberlappungen ausserhalb des Index des Textes werden ignoriert.
	 */
	public void previous(){
		pos--;
	}

	/**
	 * Gibt das Zeichen (Character) an der aktuellen Position des Zeigers aus.
	 * @return char Das Zeichen auf dem der Zeiger steht.
	 */
	public char getCurrent() {
		return text[pos];
	}
	
	/**
	 * Gibt das Zeichen, als Kleinbuchstaben, an der aktuellen Position des Zeigers aus.
	 * @return char Das Zeichen auf dem der Zeiger steht als Kleinbuchstaben.
	 */
	public char getCurrentLower() {
		return lcText[pos];
	}

	/**
	 * Gibt das Zeichen an der angegebenen Position zur￼ck.
	 * @param pos Position des auszugebenen Zeichen.
	 * @return char Das Zeichen an der angegebenen Position.
	 */
	public char charAt(int pos) {
		return text[pos];
	}
	
	/**
	 * Gibt das Zeichen, als Kleinbuchstaben, an der angegebenen Position zur￼ck.
	 * @param pos Position des auszugebenen Zeichen.
	 * @return char Das Zeichen an der angegebenen Position als Kleinbuchstaben.
	 */
	public char charAtLower(int pos) {
		return lcText[pos];
	}

	/**
	 * Gibt zur￼ck ob das n￤chste Zeichen das selbe ist wie das Eingegebene.
	 * @param c Zeichen zum Vergleich.
	 * @return boolean 
	 */
	public boolean isNext(char c) {
		if(!hasNext()) return false;
		return lcText[pos+1]==c;
	}
	private boolean isNextRaw(char c) {
		if(!hasNext()) return false;
		return text[pos+1]==c;
	}
	
	
	/**
	 * Gibt zur￼ck ob das aktuelle Zeichen zwischen den Angegebenen liegt.
	 * @param left Linker (unterer) Wert.
	 * @param right Rechter (oberer) Wert.
	 * @return Gibt zur￼ck ob das aktuelle Zeichen zwischen den Angegebenen liegt.
	 */
	public boolean isCurrentBetween(char left, char right) {
		if(!isValidIndex()) return false;
		return lcText[pos]>=left && lcText[pos]<=right;
	}

    /**
     * Gibt zur￼ck ob das aktuelle Zeichen ein Buchstabe ist.
     * @return Gibt zur￼ck ob das aktuelle Zeichen ein Buchstabe ist.
     */
    public boolean isCurrentLetter() {
        if(!isValidIndex()) return false;
        return lcText[pos]>='a' && lcText[pos]<='z';
    }
    /**
     * Gibt zur￼ck ob das aktuelle Zeichen ein Special Buchstabe ist (_,euro,$,pound).
     * @return Gibt zur￼ck ob das aktuelle Zeichen ein Buchstabe ist.
     */
    public boolean isCurrentSpecial() {
        if(!isValidIndex()) return false;
        return lcText[pos]=='_' || lcText[pos]=='$' || lcText[pos]=='�' || lcText[pos]=='�';
    }
	
	/**
	 * Gibt zur￼ck ob das aktuelle Zeichen das selbe ist wie das Eingegebene.
	 * @param c char Zeichen zum Vergleich.
	 * @return boolean
	 */
	public boolean isCurrent(char c) {
		if(!isValidIndex()) return false;
		return lcText[pos]==c;
	}
	
	/**
	 * Stellt den Zeiger eins nach vorn, wenn das aktuelle Zeichen das selbe ist wie das Eingegebene, 
	 * gibt zur￼ck ob es das selbe Zeichen war oder nicht.
	 * @param c char Zeichen zum Vergleich.
	 * @return boolean
	 */
	public boolean forwardIfCurrent(char c) {
		if(isCurrent(c)) {
			pos++;
			return true;
		} 
		return false;
	}
	
	/**
	 * Gibt zur￼ck ob das aktuelle und die folgenden Zeichen die selben sind,
	 * wie in der angegebenen Zeichenkette.
	 * @param str String Zeichen zum Vergleich.
	 * @return boolean
	 */
	public boolean isCurrent(String str) {
		if(pos+str.length()>text.length) return false;
		for(int i=str.length()-1;i>=0;i--)	{
			if(str.charAt(i)!=lcText[pos+i]) return false;
		}
		return true;
		/*char[] c=str.toCharArray();
		// @ todo not shure for length
		if(pos+c.length>text.length) return false;
		for(int i=c.length-1;i>=0;i--)	{
			if(c[i]!=lcText[pos+i]) return false;
		}
		return true;*/			
	}
	
	/**
	 * Gibt zur￼ck ob das aktuelle und die folgenden Zeichen die selben sind, 
	 * wie in der angegebenen Zeichenkette, 
	 * wenn ja wird der Zeiger um die L￤nge des String nach vorne gesetzt.
	 * @param str String Zeichen zum Vergleich.
	 * @return boolean
	 */
	public boolean forwardIfCurrent(String str) {
		boolean is=isCurrent(str);
		if(is)pos+=str.length();
		return is;
	}
	

	public boolean forwardIfCurrent(String str, boolean startWithSpace) {
		if(!startWithSpace) return forwardIfCurrent(str);
		
		int start=pos;
		if(!removeSpace())return false;
		
		if(!forwardIfCurrent(str)){
			pos=start;
			return false;
		}
		return true;
	}
	
	

	/**
	 * Gibt zur￼ck ob das aktuelle und die folgenden Zeichen die selben sind gefolgt nicht von einem word character, 
	 * wenn ja wird der Zeiger um die L￤nge des String nach vorne gesetzt.
	 * @param str String Zeichen zum Vergleich.
	 * @return boolean
	 */
	public boolean forwardIfCurrentAndNoWordAfter(String str) {
		int c=pos;
		if(forwardIfCurrent(str)) {
			if(!isCurrentBetween('a','z') && !isCurrent('_'))return true;
		}
		pos=c;
		return false;
	}
	
	/**
	 * Gibt zur￼ck ob das aktuelle und die folgenden Zeichen die selben sind gefolgt nicht von einem word character oder einer Zahl, 
	 * wenn ja wird der Zeiger um die L￤nge des String nach vorne gesetzt.
	 * @param str String Zeichen zum Vergleich.
	 * @return boolean
	 */
	public boolean forwardIfCurrentAndNoVarExt(String str) {
		int c=pos;
		if(forwardIfCurrent(str)) {
			if(!isCurrentBetween('a','z') &&!isCurrentBetween('0','9') && !isCurrent('_'))return true;
		}
		pos=c;
		return false;
	}
	
	/**
	 * Gibt zur￼ck ob first den folgenden Zeichen entspricht, gefolgt von Leerzeichen und second.
	 * @param first Erste Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweite Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur￼ck ob die eingegebenen Werte dem Inhalt beim aktuellen Stand des Zeigers entsprechen.
	 */
	public boolean isCurrent(String first,char second) {
		int start=pos;
		if(!forwardIfCurrent(first)) return false; 
		removeSpace();
		boolean rtn=isCurrent(second); 
		pos=start;
		return rtn;			
	}
	
	/**
	 * Gibt zur￼ck ob first den folgenden Zeichen entspricht, gefolgt von Leerzeichen und second.
	 * @param first Erstes Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweites Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur￼ck ob die eingegebenen Werte dem Inhalt beim aktuellen Stand des Zeigers entsprechen.
	 */
	public boolean isCurrent(char first,char second) {
		int start=pos;
		if(!forwardIfCurrent(first)) return false; 
		removeSpace();
		boolean rtn=isCurrent(second); 
		pos=start;
		return rtn;			
	}
	
	/**
	 * Gibt zur￼ck ob first den folgenden Zeichen entspricht, 
	 * gefolgt von Leerzeichen und second,
	 * wenn ja wird der Zeiger um die L￤nge der ￜbereinstimmung nach vorne gestellt.
	 * @param first Erste Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweite Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur￼ck ob der Zeiger vorw￤rts geschoben wurde oder nicht.
	 */
	public boolean forwardIfCurrent(String first,char second) {
		int start=pos;
		if(!forwardIfCurrent(first)) return false; 
		removeSpace();
		boolean rtn=forwardIfCurrent(second); 
		if(!rtn)pos=start;
		return rtn;	
	}
	
	/**
	 * Gibt zur￼ck ob ein Wert folgt und vor und hinterher Leerzeichen folgen.
	 * @param before Definition der Leerzeichen vorher.
	 * @param val Gefolgter Wert der erartet wird.
	 * @param after Definition der Leerzeichen nach dem Wert.
	 * @return Gibt zur￼ck ob der Zeiger vorw￤rts geschoben wurde oder nicht.
	 */
	public boolean forwardIfCurrent(short before, String val,short after) {
		int start=pos;
		// space before
		if(before==AT_LEAST_ONE_SPACE) {
			if(!removeSpace()) return false;
		}
		else removeSpace();
		
		// value
		if(!forwardIfCurrent(val)) {
			setPos(start);
			return false;
		}
		
		// space after
		if(after==AT_LEAST_ONE_SPACE) {
			if(!removeSpace()) { 
				setPos(start);
				return false; 
			} 
		}
		else removeSpace();
		return true;
	}
	
	/**
	 * Gibt zur￼ck ob first den folgenden Zeichen entspricht, 
	 * gefolgt von Leerzeichen und second,
	 * wenn ja wird der Zeiger um die L￤nge der ￜbereinstimmung nach vorne gestellt.
	 * @param first Erste Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweite Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur￼ck ob der Zeiger vorw￤rts geschoben wurde oder nicht.
	 */
	public boolean forwardIfCurrent(char first,char second) {
		int start=pos;
		if(!forwardIfCurrent(first)) return false; 
		removeSpace();
		boolean rtn=forwardIfCurrent(second); 
		if(!rtn)pos=start;
		return rtn;	
	}
	
	/**
	 * Gibt zur￼ck ob first den folgenden Zeichen entspricht, gefolgt von Leerzeichen und second.
	 * @param first Erste Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweite Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur￼ck ob die eingegebenen Werte dem Inhalt beim aktuellen Stand des Zeigers entsprechen.
	 */
	public boolean isCurrent(String first,String second) {
		int start=pos;
		if(!forwardIfCurrent(first)) return false; 
		removeSpace();
		boolean rtn=isCurrent(second); 
		pos=start;
		return rtn;			
	}
	
	/**
	 * Gibt zur￼ck ob first den folgenden Zeichen entspricht, 
	 * gefolgt von Leerzeichen und second,
	 * wenn ja wird der Zeiger um die L￤nge der ￜbereinstimmung nach vorne gestellt.
	 * @param first Erste Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweite Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur￼ck ob der Zeiger vorw￤rts geschoben wurde oder nicht.
	 */
	public boolean forwardIfCurrent(String first,String second) {
		int start=pos;
		if(!forwardIfCurrent(first)) return false; 
		if(!removeSpace()){
			pos=start;
			return false;
		}
		boolean rtn=forwardIfCurrent(second); 
		if(!rtn)pos=start;
		return rtn;	
	}
	
	public boolean forwardIfCurrent(String first,String second,String third) {
		int start=pos;
		if(!forwardIfCurrent(first)) return false; 
		
		if(!removeSpace()){
			pos=start;
			return false;
		}
		
		if(!forwardIfCurrent(second)){
			pos=start;
			return false;
		}
		
		if(!removeSpace()){
			pos=start;
			return false;
		}
		
		boolean rtn=forwardIfCurrent(third); 
		if(!rtn)pos=start;
		return rtn;	
	}
	
	public boolean forwardIfCurrent(String first,String second,String third, boolean startWithSpace) {
		if(!startWithSpace) return forwardIfCurrent(first, second, third);
		int start=pos;
		
		if(!removeSpace())return false;
		
		if(!forwardIfCurrent(first,second,third)){
			pos=start;
			return false;
		}
		return true;	
	}
	
	
	
	public boolean forwardIfCurrent(String first,String second,String third, String forth) {
		int start=pos;
		if(!forwardIfCurrent(first)) return false; 
		
		if(!removeSpace()){
			pos=start;
			return false;
		}
		
		if(!forwardIfCurrent(second)){
			pos=start;
			return false;
		}
		
		if(!removeSpace()){
			pos=start;
			return false;
		}
		
		
		if(!forwardIfCurrent(third)){
			pos=start;
			return false;
		}
		
		if(!removeSpace()){
			pos=start;
			return false;
		}
		
		boolean rtn=forwardIfCurrent(forth); 
		if(!rtn)pos=start;
		return rtn;	
		
	}
	
	
	/**
	 * Gibt zur￼ck ob sich vor dem aktuellen Zeichen Leerzeichen befinden.
	 * @return Gibt zur￼ck ob sich vor dem aktuellen Zeichen Leerzeichen befinden.
	 */
	public boolean hasSpaceBefore() {
		return pos > 0 && lcText[pos - 1] == ' ';
	}
	
	public boolean hasNLBefore() {
		int index=0;
		while(pos-(++index) >= 0){
			if(text[pos - index] == '\n')return true;
			if(text[pos - index] == '\r')return true;
			if(lcText[pos - index] != ' ') return false;
		}
		return false;
	}
	
	/**
	 * Stellt den Zeiger nach vorne, wenn er sich innerhalb von Leerzeichen befindet, 
	 * bis die Leerzeichen fertig sind. 
	 * @return Gibt zur￼ck ob der Zeiger innerhalb von Leerzeichen war oder nicht.
	 */
	public boolean removeSpace() {
		int start=pos;
		while(pos<text.length && lcText[pos]==' ') {
			pos++;
		}
		return (start<pos);
	}
	public String removeAndGetSpace() {
		int start=pos;
		while(pos<text.length && lcText[pos]==' ') {
			pos++;
		}
		return substring(start,pos-start);
	}
	
	/**
	 * Stellt den internen Zeiger an den Anfang der n￤chsten Zeile, 
	 * gibt zur￼ck ob eine weitere Zeile existiert oder ob es bereits die letzte Zeile war.
	 * @return Existiert eine weitere Zeile.
	 */
	public boolean nextLine() {
		while(isValidIndex() && text[pos]!='\n' && text[pos]!='\r') {
			next();
		}
		if(!isValidIndex()) return false;
		
		if(text[pos]=='\n') {
			next();
			return isValidIndex();
		}
		if(text[pos]=='\r') {
			next();
			if(isValidIndex() && text[pos]=='\n') {
				next();
			}
			return isValidIndex();
		}
		return false;
	}
	
	/**
	 * Gibt eine Untermenge des CFMLString als Zeichenkette zur￼ck, 
	 * ausgehend von start bis zum Ende des CFMLString.
	 * @param start Von wo aus die Untermege ausgegeben werden soll.
	 * @return Untermenge als Zeichenkette
	 */
	public String substring(int start) {
		return substring(start,text.length-start);
	}

	/**
	 * Gibt eine Untermenge des CFMLString als Zeichenkette zur￼ck, 
	 * ausgehend von start mit einer maximalen L￤nge count.
	 * @param start Von wo aus die Untermenge ausgegeben werden soll.
	 * @param count Wie lange die zur￼ckgegebene Zeichenkette maximal sein darf.
	 * @return Untermenge als Zeichenkette.
	 */
	public String substring(int start, int count) {
		return String.valueOf(text,start,count);
	}

	/**
	 * Gibt eine Untermenge des CFMLString als Zeichenkette in Kleinbuchstaben zur￼ck, 
	 * ausgehend von start bis zum Ende des CFMLString.
	 * @param start Von wo aus die Untermenge ausgegeben werden soll.
	 * @return  Untermenge als Zeichenkette in Kleinbuchstaben.
	 */
	public String substringLower(int start) {
		return substringLower(start,text.length-start);
	}

	/**
	 * Gibt eine Untermenge des CFMLString als Zeichenkette in Kleinbuchstaben zur￼ck, 
	 * ausgehend von start mit einer maximalen L￤nge count.
	 * @param start Von wo aus die Untermenge ausgegeben werden soll.
	 * @param count Wie lange die zur￼ckgegebene Zeichenkette maximal sein darf.
	 * @return  Untermenge als Zeichenkette in Kleinbuchstaben.
	 */
	public String substringLower(int start, int count) {
		return String.valueOf(lcText,start,count);
	}
	
	/**
	 * Gibt eine Untermenge des CFMLString als CFMLString zur￼ck, 
	 * ausgehend von start bis zum Ende des CFMLString.
	 * @param start Von wo aus die Untermenge ausgegeben werden soll.
	 * @return Untermenge als CFMLString
	 */
	public CFMLString subCFMLString(int start) {
		return subCFMLString(start,text.length-start);
	}
	
	/**
	* Gibt eine Untermenge des CFMLString als CFMLString zur￼ck, 
	* ausgehend von start mit einer maximalen L￤nge count.
	* @param start Von wo aus die Untermenge ausgegeben werden soll.
	* @param count Wie lange die zur￼ckgegebene Zeichenkette maximal sein darf.
	* @return Untermenge als CFMLString
	*/
   public CFMLString subCFMLString(int start, int count) {
   		return new CFMLString(String.valueOf(text,start,count),charset,writeLog,sf);
   		
   }
	
	/** Gibt den CFMLString als String zur￼ck.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String(this.text);
	}
	
	/**
	 * Gibt die aktuelle Position des Zeigers innerhalb des CFMLString zur￼ck.
	 * @return Position des Zeigers
	 */
	public int getPos() {
		return pos;
	}
	
	/**
	 * Setzt die Position des Zeigers innerhalb des CFMLString, ein ung￼ltiger index wird ignoriert.
	  * @param pos Position an die der Zeiger gestellt werde soll.
	 */
	public void setPos(int pos) {
		this.pos= pos;
	}
	
	/**
	 * Gibt die aktuelle Zeile zur￼ck in der der Zeiger des CFMLString steht.
	 * @return Zeilennummer
	 */
	public int getLine() {
		return getLine(pos);
	}
	
	/**
	 * Gibt zur￼ck in welcher Zeile die angegebene Position ist.
	 * @param pos Position von welcher die Zeile erfragt wird
	 * @return Zeilennummer
	 */
	public int getLine(int pos) {
		for(int i=0;i<lines.length;i++) {
			if(pos<=lines[i].intValue())
			return i+1;
		}
		return lines.length;
	}
	
	/**
	 * Gibt die Stelle in der aktuelle Zeile zur￼ck, in welcher der Zeiger steht.
	 * @return Position innerhalb der Zeile.
	 */
	public int getColumn() {
		return getColumn(pos);
	}
	
	/**
	 * Gibt die Stelle in der Zeile auf die pos zeigt zur￼ck.
	 * @param pos Position von welcher die Zeile erfragt wird
	 * @return Position innerhalb der Zeile.
	 */
	public int getColumn(int pos) {
		int line=getLine(pos)-1;
		if(line==0) return pos+1;
		return pos-lines[line-1].intValue();
	}
	
	/**
	 * Gibt die Zeile auf welcher der Zeiger steht als String zur￼ck.
	 * @return Zeile als Zeichenkette
	 */
	public String getLineAsString() {
		return getLineAsString(getLine(pos));
	}
	/**
	 * Gibt die angegebene Zeile als String zur￼ck.
	 * @param line Zeile die zur￼ck gegeben werden soll
	 * @return Zeile als Zeichenkette
	 */
	public String getLineAsString(int line) {
		int index=line-1;
		if(lines.length<=index) return null;
		int max=lines[index].intValue();
		int min=0;
		if(index!=0)
			min=lines[index-1].intValue()+1;
		
		if(min<max && max-1<text.length)
			return this.substring(min, max-min);
		return "";
	}

	/**
	 * Gibt zur￼ck ob der Zeiger auf dem letzten Zeichen steht.
	 * @return Gibt zur￼ck ob der Zeiger auf dem letzten Zeichen steht.
	 */
	public boolean isLast() {
		return pos==text.length-1;
	}
	
	/**
	 * Gibt zur￼ck ob der Zeiger nach dem letzten Zeichen steht.
	 * @return Gibt zur￼ck ob der Zeiger nach dem letzten Zeichen steht.
	 */
	public boolean isAfterLast() {
		return pos>=text.length;
	}
	/**
	 * Gibt zur￼ck ob der Zeiger einen korrekten Index hat.
	 * @return Gibt zur￼ck ob der Zeiger einen korrekten Index hat.
	 */
	public boolean isValidIndex() {
		return pos<text.length && pos>-1;
	}
	
	/**
	 * Gibt zur￼ck, ausgehend von der aktuellen Position, 
	 * wann das n￤chste Zeichen folgt das gleich ist wie die Eingabe, 
	 * falls keines folgt wird ﾖ1 zur￼ck gegeben. 
	 * Gross- und Kleinschreibung der Zeichen werden igoriert.
	 * @param c gesuchtes Zeichen
	 * @return Zeichen das gesucht werden soll.
	 */
	public int indexOfNext(char c) {
		for(int i=pos;i<lcText.length;i++) {
			if(lcText[i]==c) return i;
		}
		return -1;
	}
	
	/**
	 * Gibt das letzte Wort das sich vor dem aktuellen Zeigerstand befindet zur￼ck, 
	 * falls keines existiert wird null zur￼ck gegeben.
	 * @return Word vor dem aktuellen Zeigerstand.
	 */
	public String lastWord() {
		int size = 1;
		while (pos - size > 0 && lcText[pos - size] == ' ') {
			size++;
		}
		while (pos - size > 0
			&& lcText[pos - size] != ' '
			&& lcText[pos - size] != ';') {
			size++;
		}
		return this.substring((pos - size + 1), (pos - 1));
	}

	/**
	 * Gibt die L￤nge des CFMLString zur￼ck.
	 * @return L￤nge des CFMLString.
	 */
	public int length() {
		return text.length;
	}

	/**
	 * Gibt die Quelle aus dem der CFML Code stammt als File Objekt zur￼ck, 
	 * falls dies nicht aud einem File stammt wird null zur￼ck gegeben.
	 * @return source Quelle des CFML Code.
	 */
	public SourceFile getSourceFile() {
		return sf;
	}

	/**
	 * Pr￼ft ob das ￼bergebene Objekt diesem Objekt entspricht.
	 * @param o Object zum vergleichen.
	 * @return Ist das ￼bergebene Objekt das selbe wie dieses.
	 */
	public boolean equals(Object o) {
		if(!(o instanceof CFMLString))return false;
		return o.toString().equals(this.toString());
	}

	public String getCharset() {
		return charset;
	}


	public boolean getWriteLog() {
		return writeLog;
	}


	public String getText() {
		return new String(text);
	}
	


	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}
}