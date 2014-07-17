<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function testForm(){
		// FALSE
		http method="post" result="local.result" url="#createURL("Jira1923/index.cfm?sameFormFieldsAsArray=false")#" addtoken="false" {
			httpparam type="formfield" name="test" value="1";
			httpparam type="formfield" name="test" value="2";
		}
		assertEquals("form:'1,2'->false;",trim(result.filecontent));
		
		// TRUE
		http method="post" result="local.result" url="#createURL("Jira1923/index.cfm?sameFormFieldsAsArray=true")#" addtoken="false" {
			httpparam type="formfield" name="test" value="1";
			httpparam type="formfield" name="test" value="2";
		}
		assertEquals("form:['1','2']->true;",trim(result.filecontent));
	}


	public void function testURL(){
		// FALSE
		http method="get" result="local.result" url="#createURL("Jira1923/index.cfm?sameURLFieldsAsArray=false")#" addtoken="false" {
			httpparam type="url" name="test" value="1";
			httpparam type="url" name="test" value="2";
		}
		assertEquals("url:'1,2'->false;",trim(result.filecontent));
		
		// TRUE
		http method="get" result="local.result" url="#createURL("Jira1923/index.cfm?sameURLFieldsAsArray=true")#" addtoken="false" {
			httpparam type="url" name="test" value="1";
			httpparam type="url" name="test" value="2";
		}
		assertEquals("url:['1','2']->true;",trim(result.filecontent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>