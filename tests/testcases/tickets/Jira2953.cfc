<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	public function afterTests(){
		file action="touch" file="#getCurrenttemplatePath()#";
		
	}
	
	//public function setUp(){}

	public void function test() localmode="true" {
		str = '
';
	ascs="";
	loop from="1" to="#len(str)#" index="i"{
		ascs&=asc(mid(str,i,1))&";";
	}


		assertEquals("10;",ascs);
	}
} 
</cfscript>