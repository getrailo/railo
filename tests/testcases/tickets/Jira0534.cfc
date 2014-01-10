<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
	}

	public void function test(){
		qry=queryNew('a');
		QueryAddRow(qry);
		QuerySetCell(qry,'a','x');
		QueryAddRow(qry);
		QuerySetCell(qry,'a','y');
		col="a";
  
		assertEquals("x,y",ArrayToList(qry.a));
		assertEquals("x,y",ArrayToList(qry[col])); 
	}
} 
</cfscript>