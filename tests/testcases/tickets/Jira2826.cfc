component extends="org.railo.cfml.test.RailoTestCase"	{

	
	function testNoFilter() {

		directory action="list" directory="#getDir()#" name="qDir";
		assertTrue(qDir.recordCount == 5);
	} 
	
	function testSimpleFilter() {
		
		directory action="list" directory="#getDir()#" name="qDir" filter="*.html";
		assertTrue(qDir.recordCount == 1);
	} 
	
	function testDefaultDelimiter() {
		
		directory action="list" directory="#getDir()#" name="qDir" filter="*.html|*.txt";
		assertTrue(qDir.recordCount == 4);
	} 
	
	function testBadDelimiter() {
		
		directory action="list" directory="#getDir()#" name="qDir" filter="*.html;*.txt";
		assertTrue(qDir.recordCount == 0);
	} 

	function testPassedDelimiter() {
		
		directory action="list" directory="#getDir()#" name="qDir" filter="*.html;*.txt" filterDelimiters="|;,";
		assertTrue(qDir.recordCount == 4);
	} 


	function testUDFFilter() {		
		
		directory action="list" directory="#getDir()#" name="qDir" filter="#function (path) { return listLast(arguments.path, '.') == "txt"; }#";
		assertTrue(qDir.recordCount == 3);
	} 
	

	private function getDir() {

		return listFirst( getCurrentTemplatePath(), '.' ) & "/src";
	}

}