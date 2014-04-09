<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function test() localmode="true"{
		http method="get" result="local.result" url="#createURL("Jira2982/index.cfm")#" addtoken="false";
		
		loop query="#result.cookies#" {
			if(result.cookies.name=="testa")
				assertEquals("Pk9XxWRrr+4JoNaxifc",result.cookies.value);
			if(result.cookies.name=="testb")
				assertEquals("Pk9XxWRrr+4Jo Naxifc",result.cookies.value);
		}
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>