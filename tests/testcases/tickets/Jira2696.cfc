<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testStringColumnNames(){
		var q=queryNew("a,b");
		var meta=getMetaData(q);
		
		assertEquals(2,arrayLen(meta));
		assertEquals("a",meta[1].name);
		assertEquals("b",meta[2].name);
		
	}
	public void function testArrayColumnNames(){
		var q=queryNew(["a","b,c"]);
		var meta=getMetaData(q);
		
		assertEquals(2,arrayLen(meta));
		assertEquals("a",meta[1].name);
		assertEquals("b,c",meta[2].name);
		
	}
	
	public void function testStringColumnNamesStringColumnTypes (){
		var q=queryNew("a,b","varchar,varchar");
		var meta=getMetaData(q);
		assertEquals(2,arrayLen(meta));
		assertEquals("a",meta[1].name);
		assertEquals("b",meta[2].name);
		
		assertEquals("varchar",meta[1].typeName);
		assertEquals("varchar",meta[2].typeName);
	}
	
	public void function testArrayColumnNamesStringColumnTypes (){
		var q=queryNew(["a","b,c"],"varchar,varchar");
		var meta=getMetaData(q);
		assertEquals(2,arrayLen(meta));
		assertEquals("a",meta[1].name);
		assertEquals("b,c",meta[2].name);
		
		assertEquals("varchar",meta[1].typeName);
		assertEquals("varchar",meta[2].typeName);
	}
	
	public void function testArrayColumnNamesArrayColumnTypes (){
		var q=queryNew(["a","b,c"],["varchar","varchar"]);
		var meta=getMetaData(q);
		assertEquals(2,arrayLen(meta));
		assertEquals("a",meta[1].name);
		assertEquals("b,c",meta[2].name);
		
		assertEquals("varchar",meta[1].typeName);
		assertEquals("varchar",meta[2].typeName);
	}
	
	public void function testStringColumnNamesStringColumnTypesData (){
		var q=queryNew("a,b","varchar,numeric",[["Susi",20],["Urs",24]]);
		var meta=getMetaData(q);
		
		assertEquals(2,arrayLen(meta));
		assertEquals("a",meta[1].name);
		assertEquals("b",meta[2].name);
		
		assertEquals("varchar",meta[1].typeName);
		assertEquals("numeric",meta[2].typeName);
		
		assertEquals(2,q.recordcount);
		assertEquals("Susi",q.a[1]);
		assertEquals("Urs",q.a[2]);
		assertEquals(20,q.b[1]);
		assertEquals(24,q.b[2]);
	}
} 
</cfscript>