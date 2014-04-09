<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		variables.serviceURL =createURL("Jira2884/Json.cfc");
		
	}

	public void function testHTTPWebservice() localMode="modern" {
		
		variables.service = CreateObject("http", serviceURL);
		json=variables.service.getJson();
		assertEquals("{'S':'#chr(223)#','U':'#chr(252)#','A':'#chr(228)#','O':'#chr(246)#'}",serialize(json));
		

	}

	public void function testSOAPWebservice() localMode="modern" {
		
		variables.service = CreateObject("webservice", serviceURL&"?wsdl");
		json=variables.service.getJson();
		assertEquals("{'S':'#chr(223)#','U':'#chr(252)#','A':'#chr(228)#','O':'#chr(246)#'}",serialize(json));
		

	}
	public void function testSerializeJSonDirectly() localMode="modern" {
		assertEquals("#chr(228)#",deserializeJson(serializeJson(var:"#chr(228)#",charset:'us-ascii')));
	}

	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}

} 
</cfscript>