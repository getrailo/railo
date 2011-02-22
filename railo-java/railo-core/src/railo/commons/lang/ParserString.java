package railo.commons.lang;



/**
 	Der CFMLString ist eine Hilfe f�r die Transformer, 
 	er repr�sentiert den CFML Code und bietet Methoden an, 
 	um alle n�tigen Informationen auszulesen und Manipulationen durchzuf�hren. 
 	Dies um, innerhalb des Transformer, wiederkehrende Zeichenketten-Manipulationen zu abstrahieren.
 *
 */
public final class ParserString {

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
	 * Diesen Konstruktor kann er CFML Code als Zeichenkette �bergeben werden.
	 * @param text CFML Code
	 */
	public ParserString(String text) {
		init(text);
	}
	
	/**
	 * Gemeinsame Initialmethode der drei Konstruktoren, diese erh�lt den CFML Code als 
	 * char[] und �bertr�gt ihn, in die interen Datenhaltung. 
	 * @param str
	 */
	protected void init(String str) {
		int len=str.length();
		text=new char[len];
		lcText=new char[len];

		for(int i=0;i<len;i++) {
			char c=str.charAt(i);
			text[i]=c;
			if(c=='\n' || c=='\r' || c=='\t') {
				lcText[i]=' ';
			}
			else lcText[i]=((c>='a' && c<='z') || (c>='0' && c<='9'))?c:Character.toLowerCase(c);
		}
	}

	/**
	 * Gibt zur�ck ob, 
	 * ausgehend von der aktuellen Position des internen Zeigers im Text,
	 * noch ein Zeichen vorangestellt ist.
	 * @return boolean Existiert ein weieters Zeichen nach dem Zeiger.
	 */
	public boolean hasNext()  {
		return pos+1<text.length;
	}
	public boolean hasNextNext()  {
		return pos+2<text.length;
	}

	public boolean hasPrevious()  {
		return pos-1>=0;
	}
	public boolean hasPreviousPrevious()  {
		return pos-2>=0;
	}

	/**
	 * Stellt den internen Zeiger auf die n�chste Position. 
	 * �berlappungen ausserhalb des Index des Textes werden ignoriert.
	*/
	public void next(){
		pos++;
	}
	/**
	 * Stellt den internen Zeiger auf die vorhergehnde Position. 
	 * �berlappungen ausserhalb des Index des Textes werden ignoriert.
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
	 * Gibt das Zeichen (Character) an der n�chsten Position des Zeigers aus.
	 * @return char Das Zeichen auf dem der Zeiger steht plus 1.
	 */
	public char getNext() {
		return text[pos+1];
	}
	
	/**
	 * Gibt das Zeichen, als Kleinbuchstaben, an der aktuellen Position des Zeigers aus.
	 * @return char Das Zeichen auf dem der Zeiger steht als Kleinbuchstaben.
	 */
	public char getCurrentLower() {
		return lcText[pos];
	}
	
	/**
	 * Gibt das Zeichen, als Grossbuchstaben, an der aktuellen Position des Zeigers aus.
	 * @return char Das Zeichen auf dem der Zeiger steht als Grossbuchstaben.
	 */
	public char getCurrentUpper() {
		return Character.toUpperCase(text[pos]);
	}
	
	/**
	 * Gibt das Zeichen, als Kleinbuchstaben, an der n�chsten Position des Zeigers aus.
	 * @return char Das Zeichen auf dem der Zeiger steht plus 1 als Kleinbuchstaben.
	 */
	public char getNextLower() {
		return lcText[pos];
	}

	/**
	 * Gibt das Zeichen an der angegebenen Position zur�ck.
	 * @param pos Position des auszugebenen Zeichen.
	 * @return char Das Zeichen an der angegebenen Position.
	 */
	public char charAt(int pos) {
		return text[pos];
	}
	
	/**
	 * Gibt das Zeichen, als Kleinbuchstaben, an der angegebenen Position zur�ck.
	 * @param pos Position des auszugebenen Zeichen.
	 * @return char Das Zeichen an der angegebenen Position als Kleinbuchstaben.
	 */
	public char charAtLower(int pos) {
		return lcText[pos];
	}

	/**
	 * Gibt zur�ck ob das n�chste Zeichen das selbe ist wie das Eingegebene.
	 * @param c Zeichen zum Vergleich.
	 * @return boolean 
	 */
	public boolean isNext(char c) {
		if(!hasNext()) return false;
		return lcText[pos+1]==c;
	}
	
	public boolean isPrevious(char c) {
		if(!hasPrevious()) return false;
		return lcText[pos-1]==c;
	}

	/**
	 * Gibt zur�ck ob das n�chste Zeichen das selbe ist wie das Eingegebene.
	 * @param c Zeichen zum Vergleich.
	 * @return boolean 
	 */
	public boolean isCurrentIgnoreSpace(char c) {
		if(!hasNext()) return false;
		int start=getPos();
		removeSpace();
		
		boolean is=isCurrent(c);
		setPos(start);
		return is;
	}

	/**
	 * Gibt zur�ck ob das n�chste Zeichen das selbe ist wie das Eingegebene.
	 * @param c Zeichen zum Vergleich.
	 * @return boolean 
	 */
	public boolean isCurrentIgnoreSpace(String str) {
		if(!hasNext()) return false;
		int start=getPos();
		removeSpace();
		
		boolean is=isCurrent(str);
		setPos(start);
		return is;
	}
	
	/**
	 * Gibt zur�ck ob das aktuelle Zeichen zwischen den Angegebenen liegt.
	 * @param left Linker (unterer) Wert.
	 * @param right Rechter (oberer) Wert.
	 * @return Gibt zur�ck ob das aktuelle Zeichen zwischen den Angegebenen liegt.
	 */
	public boolean isCurrentBetween(char left, char right) {
		if(!isValidIndex()) return false;
		return lcText[pos]>=left && lcText[pos]<=right;
	}
	
	/**
	 * Gibt zur�ck ob das aktuelle Zeichen eine Zahl ist.
	 * @return Gibt zur�ck ob das aktuelle Zeichen eine Zahl ist.
	 */
	public boolean isCurrentDigit() {
		if(!isValidIndex()) return false;
		return (lcText[pos]>='0' && lcText[pos]<='9');
	}
	
	/**
	 * Gibt zur�ck ob das aktuelle Zeichen eine Zahl ist.
	 * @return Gibt zur�ck ob das aktuelle Zeichen eine Zahl ist.
	 */
	public boolean isCurrentQuoter() {
		if(!isValidIndex()) return false;
		return lcText[pos]=='"' || lcText[pos]=='\'';
	}
	
	/**
	 * Gibt zur�ck ob das aktuelle Zeichen ein Buchstabe ist.
	 * @return Gibt zur�ck ob das aktuelle Zeichen ein Buchstabe ist.
	 */
	public boolean isCurrentLetter() {
		if(!isValidIndex()) return false;
		return lcText[pos]>='a' && lcText[pos]<='z';
	}
	public boolean isCurrentNumber() {
		if(!isValidIndex()) return false;
		return lcText[pos]>='0' && lcText[pos]<='9';
	}
	
	public boolean isCurrentWhiteSpace() {
		if(!isValidIndex()) return false;
		return (lcText[pos]==' ' || lcText[pos]=='\t' || lcText[pos]=='\b' || lcText[pos]=='\r' || lcText[pos]=='\n');
		// return lcText[pos]>='a' && lcText[pos]<='z';
	}
	
	public boolean forwardIfCurrentWhiteSpace() {
		boolean rtn=false;
		while(isCurrentWhiteSpace()) {
			pos++;
			rtn=true;
		} 
		return rtn;
	}
	
	public boolean isNextWhiteSpace() {
		if(!hasNext()) return false;
		return (lcText[pos+1]==' ' || lcText[pos+1]=='\t' || lcText[pos+1]=='\b' || lcText[pos+1]=='\r' || lcText[pos+1]=='\n');
	}
	
	public boolean isNextNextWhiteSpace() {
		if(!hasNextNext()) return false;
		return (lcText[pos+2]==' ' || lcText[pos+2]=='\t' || lcText[pos+2]=='\b' || lcText[pos+2]=='\r' || lcText[pos+2]=='\n');
	}
	
	public boolean isPreviousWhiteSpace() {
		if(!hasPrevious()) return false;
		return (lcText[pos-1]==' ' || lcText[pos-1]=='\t' || lcText[pos-1]=='\b' || lcText[pos-1]=='\r' || lcText[pos-1]=='\n');
	}
	
	public boolean isPreviousPreviousWhiteSpace() {
		if(!hasPreviousPrevious()) return false;
		return (lcText[pos-2]==' ' || lcText[pos-2]=='\t' || lcText[pos-2]=='\b' || lcText[pos-2]=='\r' || lcText[pos-2]=='\n');
	}
	
    /**
     * Gibt zur�ck ob das aktuelle Zeichen ein Special Buchstabe ist (_,�,$,�).
     * @return Gibt zur�ck ob das aktuelle Zeichen ein Buchstabe ist.
     */
    public boolean isCurrentSpecial() {
        if(!isValidIndex()) return false;
        return lcText[pos]=='_' || lcText[pos]=='$' || lcText[pos]=='�' || lcText[pos]=='�';
    }
	
	/**
	 * Gibt zur�ck ob das aktuelle Zeichen das selbe ist wie das Eingegebene.
	 * @param c char Zeichen zum Vergleich.
	 * @return boolean
	 */
	public boolean isCurrent(char c) {
		if(!isValidIndex()) return false;
		return lcText[pos]==c;
	}

	public boolean isLast(char c) {
		if(lcText.length==0) return false;
		return lcText[lcText.length-1]==c;
	}
	
	/**
	 * Stellt den Zeiger eins nach vorn, wenn das aktuelle Zeichen das selbe ist wie das Eingegebene, 
	 * gibt zur�ck ob es das selbe Zeichen war oder nicht.
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
	 * Gibt zur�ck ob das aktuelle und die folgenden Zeichen die selben sind,
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
	}
	
	/**
	 * Gibt zur�ck ob das aktuelle und die folgenden Zeichen die selben sind, 
	 * wie in der angegebenen Zeichenkette, 
	 * wenn ja wird der Zeiger um die L�nge des String nach vorne gesetzt.
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
	

	/**
	 * Gibt zur�ck ob das aktuelle und die folgenden Zeichen die selben sind gefolgt nicht von einem word character, 
	 * wenn ja wird der Zeiger um die L�nge des String nach vorne gesetzt.
	 * @param str String Zeichen zum Vergleich.
	 * @return boolean
	 */
	public boolean forwardIfCurrentAndNoWordAfter(String str) {
		int c=pos;
		if(forwardIfCurrent(str)) {
			if(!isCurrentLetter() && !isCurrent('_'))return true;
		}
		pos=c;
		return false;
	}
	public boolean forwardIfCurrentAndNoWordNumberAfter(String str) {
		int c=pos;
		if(forwardIfCurrent(str)) {
			if(!isCurrentLetter() && !isCurrentLetter() && !isCurrent('_'))return true;
		}
		pos=c;
		return false;
	}
	
	/**
	 * Gibt zur�ck ob first den folgenden Zeichen entspricht, gefolgt von Leerzeichen und second.
	 * @param first Erste Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweite Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur�ck ob die eingegebenen Werte dem Inhalt beim aktuellen Stand des Zeigers entsprechen.
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
	 * Gibt zur�ck ob first den folgenden Zeichen entspricht, gefolgt von Leerzeichen und second.
	 * @param first Erstes Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweites Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur�ck ob die eingegebenen Werte dem Inhalt beim aktuellen Stand des Zeigers entsprechen.
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
	 * Gibt zur�ck ob first den folgenden Zeichen entspricht, 
	 * gefolgt von Leerzeichen und second,
	 * wenn ja wird der Zeiger um die L�nge der �bereinstimmung nach vorne gestellt.
	 * @param first Erste Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweite Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur�ck ob der Zeiger vorw�rts geschoben wurde oder nicht.
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
	 * Gibt zur�ck ob ein Wert folgt und vor und hinterher Leerzeichen folgen.
	 * @param before Definition der Leerzeichen vorher.
	 * @param val Gefolgter Wert der erartet wird.
	 * @param after Definition der Leerzeichen nach dem Wert.
	 * @return Gibt zur�ck ob der Zeiger vorw�rts geschoben wurde oder nicht.
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
	 * Gibt zur�ck ob first den folgenden Zeichen entspricht, 
	 * gefolgt von Leerzeichen und second,
	 * wenn ja wird der Zeiger um die L�nge der �bereinstimmung nach vorne gestellt.
	 * @param first Erste Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweite Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur�ck ob der Zeiger vorw�rts geschoben wurde oder nicht.
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
	 * Gibt zur�ck ob first den folgenden Zeichen entspricht, gefolgt von Leerzeichen und second.
	 * @param first Erste Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweite Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur�ck ob die eingegebenen Werte dem Inhalt beim aktuellen Stand des Zeigers entsprechen.
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
	 * Gibt zur�ck ob first den folgenden Zeichen entspricht, 
	 * gefolgt von Leerzeichen und second,
	 * wenn ja wird der Zeiger um die L�nge der �bereinstimmung nach vorne gestellt.
	 * @param first Erste Zeichen zum Vergleich (Vor den Leerzeichen).
	 * @param second Zweite Zeichen zum Vergleich (Nach den Leerzeichen).
	 * @return Gibt zur�ck ob der Zeiger vorw�rts geschoben wurde oder nicht.
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
	 * Gibt zur�ck ob sich vor dem aktuellen Zeichen Leerzeichen befinden.
	 * @return Gibt zur�ck ob sich vor dem aktuellen Zeichen Leerzeichen befinden.
	 */
	public boolean hasSpaceBefore() {
		return pos > 0 && lcText[pos - 1] == ' ';
	}
	
	/**
	 * Stellt den Zeiger nach vorne, wenn er sich innerhalb von Leerzeichen befindet, 
	 * bis die Leerzeichen fertig sind. 
	 * @return Gibt zur�ck ob der Zeiger innerhalb von Leerzeichen war oder nicht.
	 */
	public boolean removeSpace() {
		int start=pos;
		while(pos<text.length && lcText[pos]==' ') {
			pos++;
		}
		return (start<pos);
	}
	
	/**
	 * Stellt den internen Zeiger an den Anfang der n�chsten Zeile, 
	 * gibt zur�ck ob eine weitere Zeile existiert oder ob es bereits die letzte Zeile war.
	 * @return Existiert eine weitere Zeile.
	 */
	public boolean nextLine() {
		while(isValidIndex() && text[pos]!='\n') {
			next();
		}
		if(isValidIndex() && text[pos]=='\n') {
			next();
			return isValidIndex();
		}
		return false;
	}
	
	/**
	 * Gibt eine Untermenge des CFMLString als Zeichenkette zur�ck, 
	 * ausgehend von start bis zum Ende des CFMLString.
	 * @param start Von wo aus die Untermege ausgegeben werden soll.
	 * @return Untermenge als Zeichenkette
	 */
	public String substring(int start) {
		return substring(start,text.length-start);
	}

	/**
	 * Gibt eine Untermenge des CFMLString als Zeichenkette zur�ck, 
	 * ausgehend von start mit einer maximalen L�nge count.
	 * @param start Von wo aus die Untermenge ausgegeben werden soll.
	 * @param count Wie lange die zur�ckgegebene Zeichenkette maximal sein darf.
	 * @return Untermenge als Zeichenkette.
	 */
	public String substring(int start, int count) {
		return String.valueOf(text,start,count);
	}

	/**
	 * Gibt eine Untermenge des CFMLString als Zeichenkette in Kleinbuchstaben zur�ck, 
	 * ausgehend von start bis zum Ende des CFMLString.
	 * @param start Von wo aus die Untermenge ausgegeben werden soll.
	 * @return  Untermenge als Zeichenkette in Kleinbuchstaben.
	 */
	public String substringLower(int start) {
		return substringLower(start,text.length-start);
	}

	/**
	 * Gibt eine Untermenge des CFMLString als Zeichenkette in Kleinbuchstaben zur�ck, 
	 * ausgehend von start mit einer maximalen L�nge count.
	 * @param start Von wo aus die Untermenge ausgegeben werden soll.
	 * @param count Wie lange die zur�ckgegebene Zeichenkette maximal sein darf.
	 * @return  Untermenge als Zeichenkette in Kleinbuchstaben.
	 */
	public String substringLower(int start, int count) {
		return String.valueOf(lcText,start,count);
	}
	
	/**
	 * Gibt eine Untermenge des CFMLString als CFMLString zur�ck, 
	 * ausgehend von start bis zum Ende des CFMLString.
	 * @param start Von wo aus die Untermenge ausgegeben werden soll.
	 * @return Untermenge als CFMLString
	 */
	public ParserString subCFMLString(int start) {
		return subCFMLString(start,text.length-start);
	}
	
	/**
	* Gibt eine Untermenge des CFMLString als CFMLString zur�ck, 
	* ausgehend von start mit einer maximalen L�nge count.
	* @param start Von wo aus die Untermenge ausgegeben werden soll.
	* @param count Wie lange die zur�ckgegebene Zeichenkette maximal sein darf.
	* @return Untermenge als CFMLString
	*/
   public ParserString subCFMLString(int start, int count) {
   		return new ParserString(String.valueOf(text,start,count));
   		/*
   		 NICE die untermenge direkter ermiiteln, das problem hierbei sind die lines
   		
   		int endPos=start+count;
		int LineFrom=-1;
		int LineTo=-1;
   		for(int i=0;i<lines.length;i++) {
   			if()
   		}
   	
		return new CFMLString(
		0, 
		String.valueOf(text,start,count).toCharArray(), 
		String.valueOf(lcText,start,count).toCharArray(), 
		lines);
		*/
   }
	
	/** Gibt den CFMLString als String zur�ck.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String(this.text);
	}
	
	/**
	 * Gibt die aktuelle Position des Zeigers innerhalb des CFMLString zur�ck.
	 * @return Position des Zeigers
	 */
	public int getPos() {
		return pos;
	}
	
	/**
	 * Setzt die Position des Zeigers innerhalb des CFMLString, ein ung�ltiger index wird ignoriert.
	  * @param pos Position an die der Zeiger gestellt werde soll.
	 */
	public void setPos(int pos) {
		this.pos= pos;
	}

	/**
	 * Gibt zur�ck ob der Zeiger auf dem letzten Zeichen steht.
	 * @return Gibt zur�ck ob der Zeiger auf dem letzten Zeichen steht.
	 */
	public boolean isLast() {
		return pos==text.length-1;
	}
	
	/**
	 * Gibt zur�ck ob der Zeiger nach dem letzten Zeichen steht.
	 * @return Gibt zur�ck ob der Zeiger nach dem letzten Zeichen steht.
	 */
	public boolean isAfterLast() {
		return pos>=text.length;
	}
	/**
	 * Gibt zur�ck ob der Zeiger einen korrekten Index hat.
	 * @return Gibt zur�ck ob der Zeiger einen korrekten Index hat.
	 */
	public boolean isValidIndex() {
		return pos<text.length;
	}
	
	/**
	 * Gibt zur�ck, ausgehend von der aktuellen Position, 
	 * wann das n�chste Zeichen folgt das gleich ist wie die Eingabe, 
	 * falls keines folgt wird �1 zur�ck gegeben. 
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
	 * Gibt das letzte Wort das sich vor dem aktuellen Zeigerstand befindet zur�ck, 
	 * falls keines existiert wird null zur�ck gegeben.
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
	 * Gibt die L�nge des CFMLString zur�ck.
	 * @return L�nge des CFMLString.
	 */
	public int length() {
		return text.length;
	}

	/**
	 * Pr�ft ob das �bergebene Objekt diesem Objekt entspricht.
	 * @param o Object zum vergleichen.
	 * @return Ist das �bergebene Objekt das selbe wie dieses.
	 */
	public boolean equals(Object o) {
		if(!(o instanceof ParserString))return false;
		return o.toString().equals(this.toString());
	}

}