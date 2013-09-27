<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testBeforeAdvice(){
		local.meta = getComponentMetadata("Jira1668.MethodBeforeAdvice");
		
		assertEquals('MethodBeforeAdvice',listLast(meta.fullname,'.'));
		assertEquals('BeforeAdvice',listLast(meta.extends.BeforeAdvice.fullname,'.'));
		assertEquals('Advice',listLast(meta.extends.BeforeAdvice.extends.Advice.fullname,'.'));
		
		local.extends=ListSort(structKeyList(meta.extends),'textnocase');
		assertEquals('BeforeAdvice',extends);
	}
	public void function testTest(){
		
		local.meta = getComponentMetadata("Jira1668.test");
		assertEquals('Test',listLast(meta.fullname,'.'));
		assertEquals('MethodBeforeAdvice',listLast(meta.IMPLEMENTS.MethodBeforeAdvice.fullname,'.'));
		assertEquals('BeforeAdvice',listLast(meta.IMPLEMENTS.MethodBeforeAdvice.extends.BeforeAdvice.fullname,'.'));
		assertEquals('Advice',listLast(meta.IMPLEMENTS.MethodBeforeAdvice.extends.BeforeAdvice.extends.Advice.fullname,'.'));
		
		local.implements=ListSort(structKeyList(meta.implements),'textnocase');
		assertEquals('MethodBeforeAdvice',implements);
		
	}
} 
</cfscript>