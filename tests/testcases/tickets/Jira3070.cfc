<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function testCFM(){

		http method="get" result="local.result" url="#createURL("Jira3070/index.cfm")#" addtoken="false";

		//echo(result.filecontent);
		assertEquals("",result.filecontent);
	}

	public void function testCFC(){
		variables.test = nullValue();
		assertEquals(false,structKeyExists(variables, "test")); 

	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>