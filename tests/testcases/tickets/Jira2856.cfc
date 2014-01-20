<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function testSingleString(){
		http method="get" result="local.result" url="#createURL("Jira2856/singlestring/index.cfm")#" addtoken="false";
		assertEquals(200,result.status_code);
		assertEquals('',trim(result.filecontent));
	}
	
	public void function testListString(){
		http method="get" result="local.result" url="#createURL("Jira2856/liststring/index.cfm")#" addtoken="false";
		//echo(result.filecontent);abort;
		assertEquals(200,result.status_code);
		assertEquals('',trim(result.filecontent));
	}
	
	public void function testArray(){
		http method="get" result="local.result" url="#createURL("Jira2856/array/index.cfm")#" addtoken="false";
		//echo(result.filecontent);abort;
		assertEquals(200,result.status_code);
		assertEquals('',trim(result.filecontent));
	}
	
	public void function testStruct(){
		http method="get" result="local.result" url="#createURL("Jira2856/struct/index.cfm")#" addtoken="false";
		//echo(result.filecontent);abort;
		assertEquals(200,result.status_code);
		assertEquals('',trim(result.filecontent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>