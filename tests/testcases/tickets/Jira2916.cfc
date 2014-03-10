<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testScriptTagWithNoBody(){
		cfparam (name="local.a", default="AAA");
		assertEquals("AAA",a);
		
		// no delimiter between attributes
		cfparam (name="local.b" default="BBB");
		assertEquals("BBB",b);

		
		// use : instead of =
		cfparam (name:"local.c", default:"CCC");
		assertEquals("CCC",c);

		// and again without comma 
		cfparam (name:"local.d" default:"DDD");
		assertEquals("DDD",d);

		// used variable as value
		local.f="123";
		cfparam (name="local.g", default=f);
		assertEquals("123",g);
	}

	public void function testScriptTagWithBody(){
		local.q=query(a:[1,2,3]);

		cfquery (dbtype="query", name="local.q2") {
			echo("select * from q");
		}
		assertEquals("3",q2.recordcount);
	}


	public void function testScriptTagWithNoBodyOldStyle(){
		param name="local.a",default="AAA";
		assertEquals("AAA",a);
		
		// no delimiter between attributes
		param name="local.b" default="BBB";
		assertEquals("BBB",b);
	}

	public void function testScriptTagWithBodyOldSytle(){
		local.q=query(a:[1,2,3]);

		query dbtype="query" name="local.q2" {
			echo("select * from q");
		}
		assertEquals("3",q2.recordcount);
	}
		
} 
</cfscript>