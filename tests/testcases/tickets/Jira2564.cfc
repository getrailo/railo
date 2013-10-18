<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		variables.qry=queryNew("str,nbr,dat","varchar,integer,date");
		queryAddrow(variables.qry);
	}

	public void function test(){
		setlocale("English (US)");

		var a = "1,5";
		assertEquals(false,IsNumeric(a)); // false
		assertEquals(true,LSIsNumeric(a)); // true ??
		assertEquals(15,LSParseNumber(a)); // 15 ??!
		try{
			assertEquals(ParseNumber(a)); // error
			fail("ParseNumber(a) should fail");
		}
		catch(local.exp){}
		
	}
} 

</cfscript>