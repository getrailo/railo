<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		http method="get" result="result" url="#createURL("JiraXXXX/index.cfm")#" addtoken="false" {
			// Body httpparam type="body" name="test" value="";
			// form field httpparam type="formfield" name="test" value="";
		}
	}

	public void function test(){
		/*
		assertEquals("",result.filecontent);
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
	}
	
} 
</cfscript>