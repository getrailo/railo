<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testParseDateTime() {
		var tz=getTimeZone();
		setTimeZone("UTC");
		assertEquals("{ts '1997-01-01 00:00:00'}",parseDateTime("1997")&"");
		assertEquals("{ts '1997-07-01 00:00:00'}",parseDateTime("1997-07")&"");
		assertEquals("{ts '1997-07-16 00:00:00'}",parseDateTime("1997-07-16")&"");
		assertEquals("{ts '1997-07-16 18:20:00'}",parseDateTime("1997-07-16T19:20+01:00")&"");
		assertEquals("{ts '1997-07-16 18:20:30'}",parseDateTime("1997-07-16T19:20:30+01:00")&"");
		assertEquals("{ts '1997-07-16 18:20:30'}",parseDateTime("1997-07-16T19:20:30.45+01:00")&"");
		setTimeZone(tz);
	}
} 
</cfscript>