<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		variables.serviceURL =createURL("Jira3049/index.cfm");
	}

	public void function test() localMode="modern" {
		
		http url=variables.serviceURL result="myVar";
		assertEquals("",trim(myVar.filecontent));
		assertEquals(200,myVar.status_code);
	}



	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}

} 
</cfscript>