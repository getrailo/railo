<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test(){
		savecontent variable="local.c" {
			iterations = 0;
			numbers = [1,2,3,4];
			numbers.each(function(v,i){
				iterations++;
				writeOutput("#iterations#-");
			});
		}
		assertEquals("1-2-3-4-",c);
	}
} 
</cfscript>