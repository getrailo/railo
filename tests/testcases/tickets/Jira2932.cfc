<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test() localmode="true" {
		qry= QueryNew("ID","integer");
		queryAddrow(qry);
		querySetCell(qry,"ID",1);

		x = structnew("linked");
		x.a = valueArray(qry.id);
		x.b = QueryColumnData(qry,"id");

		assertEquals('{"A":[1],"B":[1]}',serializeJSON(x, true));
	}
} 
</cfscript>