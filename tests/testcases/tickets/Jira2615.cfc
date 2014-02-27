<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test() localMode="modern" {

		serviceURL =createURL("Jira2615/TestService.cfc?wsdl");
		keyValuePairs = [{ name: "Item One" },{ name: "Item Two" }];
		keyValuePairs = [new Jira2615.MyItem()];
		
		service = CreateObject("webservice", serviceURL);
		service.myFunction2(new Jira2615.MyItem("Susi"));
		abort;


		/*assertEquals("","");
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}

	
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}

} 
</cfscript>