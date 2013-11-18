<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testMimetypeParser(){
		var MimeType=createObject('java','railo.commons.lang.mimetype.MimeType');
		
		// some regular cases
		assertEquals("application/json",MimeType.getInstance("application/json").toString());
		assertEquals("text/html; charset=utf-8",MimeType.getInstance("text/html;charset=utf-8").toString());
		assertEquals("text/html; charset=utf-8",MimeType.getInstance("text/html;charset=utf-8;").toString());
		assertEquals("text/html; charset=utf-8; susi=sorglos",MimeType.getInstance("text/html;charset=utf-8;susi=sorglos").toString());
		
		
		// some special cases
		assertEquals("application/*",MimeType.getInstance("application").toString());
		assertEquals("*/*",MimeType.getInstance("*").toString());
		assertEquals("*/*",MimeType.getInstance("").toString());
		
		// some invalid cases (that make the functionality break)
		assertEquals("*/*",MimeType.getInstance("*/").toString());
		assertEquals("*/*",MimeType.getInstance("/").toString());
		assertEquals("*/*",MimeType.getInstance("/;").toString());
		assertEquals("*/*",MimeType.getInstance("*;*").toString());
		assertEquals("*/*",MimeType.getInstance("/;=").toString());
		assertEquals("*/*",MimeType.getInstance("*;=").toString());
		assertEquals("*/*",MimeType.getInstance(";*").toString());
		
		
		/*try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}
} 
</cfscript>