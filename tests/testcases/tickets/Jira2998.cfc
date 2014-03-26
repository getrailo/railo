<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public void function testUCase(){
		assertEquals(asc("A"),asc("a".ucase()));
	}

	public void function testLCase(){
		assertEquals(asc("a"),asc("A".lcase()));
	}

	public void function testTrim(){
		assertEquals("a"," a ".trim());
	}
	public void function testLTrim(){
		assertEquals("a "," a ".ltrim());
	}
	public void function testRTrim(){
		assertEquals(" a"," a ".rtrim());
	}

	public void function testLeft(){
		assertEquals("abc","abcdefg".left(3));
	}
	public void function testRight(){
		assertEquals("efg","abcdefg".right(3));
	}

	public void function testLJustify(){
		assertEquals("abc       ","abc".lJustify(10));
	}
	public void function testCJustify(){
		assertEquals("   abc    ","abc".cJustify(10));
	}
	public void function testRJustify(){
		assertEquals("       abc","abc".rJustify(10));
	}

	public void function testMid(){
		assertEquals("bcdefghijkl","abcdefghijkl".mid(2));
		assertEquals("bcd","abcdefghijkl".mid(2,3));
	}
	public void function testLen(){
		assertEquals(3,"abc".len());
	}
	public void function testRemoveChars(){
		assertEquals("aefghijklm","abcdefghijklm".RemoveChars(2,3));
	}

	public void function testCompare(){
		assertEquals(0,"abc".compare("abc"));
		assertEquals(1,"abc".compare("ABC"));
		assertEquals(-1,"abb".compare("abc"));
		assertEquals(1,"abd".compare("abc"));
	}

	public void function testCompareNoCase(){
		assertEquals(0,"abc".CompareNoCase("ABC"));
		assertEquals(-1,"abb".CompareNoCase("abc"));
		assertEquals(1,"abd".CompareNoCase("abc"));
	}

	public void function testRepeatString(){
		assertEquals("abcabcabc","abc".RepeatString(3));
	}

	public void function testReplace(){
		assertEquals("12cabc","abcabc".replace("ab","12"));
		assertEquals("abcabc","abcabc".replace("AB","12"));
		assertEquals("12c12c","abcabc".replace("ab","12","all"));
		assertEquals("123123","abcabc".replace({'abc':'123'}));
	}

	public void function testReplaceNoCase(){
		assertEquals("12cabc","abcabc".replaceNoCase("ab","12"));
		assertEquals("12cabc","abcabc".replaceNoCase("AB","12"));
		assertEquals("12c12c","abcabc".replaceNoCase("ab","12","all"));
		assertEquals("123123","abcabc".replaceNoCase({'abc':'123'}));
	}

	public void function testWrap(){
		assertEquals("ab
c","abc".wrap(2));
	}

	public void function testSpanExcluding(){
		assertEquals("yyy","yyysss".spanExcluding("s"));
	}

	public void function testSpanIncluding(){
		assertEquals("mystr","mystring".spanIncluding("mystery"));
	}

	public void function testReverse(){
		assertEquals("cba","abc".reverse());
	}

	public void function testStripCR(){
		assertEquals("abc","a#chr(13)#b#chr(13)#c".stripCR());
	}

	public void function testFind(){
		assertEquals(3,"abcdefabcdef".find("cd"));
		assertEquals(9,"ABCDEFabcdef".find("cd"));
		assertEquals(9,"abcdefabcdef".find("cd",4));
	}

	public void function testFindNoCase(){
		assertEquals(3,"abcdefabcdef".findNoCase("cD"));
		assertEquals(9,"abcdefabcdef".findNoCase("cD",4));
	}

	public void function testREFind(){
		assertEquals(4,"abcaaccdd".REFind("a+c+"));
		assertEquals(1,"abcaaccdd".REFind("a+c*"));
		assertEquals(0,"abcaaccdd".REFind("A+C+"));
	}

	public void function testREFindNoCase(){
		assertEquals(4,"abcaaccdd".REFindNoCase("a+c+"));
		assertEquals(1,"abcaaccdd".REFindNoCase("a+c*"));
		assertEquals(4,"abcaaccdd".REFindNoCase("A+C+"));
	}

	public void function testInsert(){
		assertEquals("aabbbbaaa","aaaaa".insert("bbbb",2));
	}


	public void function testGetToken(){
		assertEquals("b","a b c".getToken(2));
		assertEquals("b","a b c".getToken(2,' '));
	}


	public void function testREMatch(){
		var string="Hallo https://www.getrailo.org Susi";
		var regex="https?://([-\w\.]+)+(:\d+)?(/([\w/_\.]*(\?\S+)?)?)?";
		assertEquals(REMatch(regex, string),string.REMatch(regex));
	}
	public void function testREMatchNoCase(){
		var string="Hallo https://www.getrailo.org Susi";
		var regex="https?://([-\w\.]+)+(:\d+)?(/([\w/_\.]*(\?\S+)?)?)?";
		assertEquals(REMatchNoCase(regex, string),string.REMatchNoCase(regex));
	}


	public void function testREReplace(){
		assertEquals("GAGARET","CABARET".REReplace("C|B","G","ALL"));
	}
	public void function testREReplaceNoCase(){
		assertEquals("GAGARET","CABARET".REReplaceNoCase("C|B","G","ALL"));
	}
	public void function testUCFirst(){
		assertEqualsCase("Susi","susi".ucFirst());
		assertEqualsCase("Susi Sorglos","susi sorglos".ucFirst(true));
		assertEqualsCase("Susi sorglos","susi sorglos".ucFirst(false));
		assertEqualsCase("SORGLOS","SORGLOS".ucFirst(true,false));
		assertEqualsCase("Sorglos","SORGLOS".ucFirst(true,true));
	}
	public void function testASC(){
		assertEqualsCase(97,"a".asc());
		assertEqualsCase(97,"abc".asc(1));
		assertEqualsCase(99,"abc".asc(3));
	}
	public void function testAppend(){
		assertEqualsCase("a,b","a".append('b'));
	}


	
} 
</cfscript>