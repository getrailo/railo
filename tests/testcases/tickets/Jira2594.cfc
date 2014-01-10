<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		qry=query(a:[10,20,30]);
	}

	public void function testValueAccess(){
		
		assertEquals(10,qry.a[1]);
		assertEquals(20,qry.a[2]);
		assertEquals(30,qry.a[3]);
	}
	public void function testCurrentRow(){
		assertEquals(false,isNull(qry.a));
	}

	public void function testDefinedRow(){
		assertEquals(false,isNull(qry.a[1]));
		assertEquals(false,isNull(qry.a[2]));
		assertEquals(false,isNull(qry.a[3]));
	}
} 
</cfscript>