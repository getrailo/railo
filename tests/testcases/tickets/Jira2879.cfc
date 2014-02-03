<!--- !!!!!!!!!!!!!! make sure not changing the encoding of this template --->
<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	processingdirective pageEncoding="UTF-8";
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testUTF8(){
		assertEquals('"öäü"',serializejson(var:"öäü",charset:'utf-8'));
	}
	public void function testISO8859_1(){
		//assertEquals('"öäü"',serializejson(var:"öäü",charset:'iso-8859-1'));
	}
	public void function testUS_ASCII(){
		assertEquals('"\u00f6\u00e4\u00fc-\u00e9"',serializejson(var:"öäü-é",charset:'us-ascii'));
	}
} 
</cfscript>