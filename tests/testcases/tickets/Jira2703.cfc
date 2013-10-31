<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		
	}
	
	public void function testScopeCascadingModernStandard(){
		assertEquals(
		"standard->true;true;true;true;true;",
		call("modern","standard"));
	}
	public void function testScopeCascadingModernSmall(){
		assertEquals(
		"small->true;true;true;false;false;",
		call("modern","small"));
	}
	public void function testScopeCascadingModernStrict(){
		assertEquals(
		"strict->true;false;false;false;false;",
		call("modern","strict"));
	}
	
	public void function testScopeCascadingClassicStandard(){
		assertEquals(
		"standard->true;true;true;true;true;",
		call("classic","standard"));
	}
	public void function testScopeCascadingClassicSmall(){
		assertEquals(
		"small->true;true;true;false;false;",
		call("classic","small"));
	}
	public void function testScopeCascadingClassicStrict(){
		assertEquals(
		"strict->true;false;false;false;false;",
		call("classic","strict"));
	}
	
	
	private function call(type,casc){
		http method="get" result="local.result" url="#createURL("Jira2703/"&type&"/index.cfm?scopecascading="&casc)#" addtoken="false" {
		}
		return trim(result.filecontent);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>