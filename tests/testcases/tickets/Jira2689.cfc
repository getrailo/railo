<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		
	}

	public void function testApplication_cfc(){
		http method="get" result="local.result" url="#createURL("Jira2689/modern/index.cfm")#" addtoken="false" {
			
		}
		settings=evaluate(trim(result.filecontent));
		assertEquals("UTF-16",settings.charset.web);
		assertEquals("UTF-16",settings.charset.resource);
		
	}
	public void function testApplication_cfm(){
		http method="get" result="local.result" url="#createURL("Jira2689/classic/index.cfm")#" addtoken="false" {
			
		}
		settings=evaluate(trim(result.filecontent));
		assertEquals("UTF-16",settings.charset.web);
		assertEquals("UTF-16",settings.charset.resource);
		
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>