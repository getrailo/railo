<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testCallStackGetStaticNames(){
		var res=a();
		loop from ="1" to="#res.len()-1#" index="local.i" {
		     assertEquals(true,len(res[i].function)>0);	
		}
	}
	
	private array function a(){return b();}
	private array function b(){return c();}
	private array function c(){return CallStackGet();}
	
} 
</cfscript>