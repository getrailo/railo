<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function setUp(){
		//Create a new query recordset to test
		variables.q = queryNew("id,test");
		//Test that queryAddRow works
		queryAddRow(q,[1,"test1"]);
	}

	public void function testBIF() localmode="modern" {
		
		//Fails for 'Can't cast Complex Object Type Struct to String'
		queryaddRow(q,
		{id:2,test:"test2"}
		);
		//Fails for 'Can't cast Complex Object Type Array to String'
		queryaddRow(q,[3,"test3"]);

		assertEquals(2,q.id[2]);
		assertEquals("test2",q.test[2]);
		assertEquals(3,q.id[3]);
		assertEquals("test3",q.test[3]);
	}

	public void function testMemberFunction() localmode="modern" {
		
		//Fails for 'Can't cast Complex Object Type Struct to String'
		q.addRow(
		{id:2,test:"test2"}
		);
		//Fails for 'Can't cast Complex Object Type Array to String'
		q.addRow([3,"test3"]);

		assertEquals(2,q.id[2]);
		assertEquals("test2",q.test[2]);
		assertEquals(3,q.id[3]);
		assertEquals("test3",q.test[3]);


		/*assertEquals("","");*/
	}
} 
</cfscript>