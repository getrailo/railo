<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testCreateODBCdate() {
		var date=createDatetime(2000,1,2,3,4,5,6,"UTC");
		assertEquals("{d '2000-01-02'}",createODBCdate(date)&"");
		assertEquals("{d '2000-01-02'}",createODBCdate(date).toString()&"");
		assertEquals("{d '2000-01-02'}",evaluate('createODBCdate(date)')&"");
	}
} 
</cfscript>