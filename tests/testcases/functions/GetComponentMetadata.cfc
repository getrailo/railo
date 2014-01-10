<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	public function setUp(){
		variables.meta = getComponentMetadata("GetComponentMetadata.Test");
	}

	public void function testFunctions(){
		
		
		
		assertEquals(true,structKeyExists(meta,'functions'));
		assertEquals(3,arrayLen(meta.functions));
		
		assertEquals(true,isDefined('meta.extends.functions'));
		assertEquals(1,arrayLen(meta.extends.functions));
		
		var func=meta.extends.functions[1];
		assertEquals('public',func.access);
		assertEquals(false,func.closure);
		assertEquals('',func.description);
		assertEquals('hintAComponentPublic',func.hint);
		assertEquals('AComponentPublic',func.name);
		assertEquals(false,func.output);
		assertEquals('AComponent.cfc',ListLast(func.owner,'\/'));
		assertEquals('wddx',func.returnFormat);
		assertEquals('void',func.returntype);
		
		assertEquals(1,arraylen(func.parameters));
		
		var param=func.parameters[1];
		assertEquals('abc',param.default);
		assertEquals('info',param.hint);
		assertEquals('a',param.name);
		assertEquals(true,param.required);
		assertEquals('string',param.type);
		
	}
} 
</cfscript>