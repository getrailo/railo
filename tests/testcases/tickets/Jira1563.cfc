<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		variables.serviceURL =createURL("Jira1563/TestService.cfc?wsdl");
		variables.service = CreateObject("webservice", serviceURL);
	}

	public void function testSendComponent() localMode="modern" {
		bd=now();
		version = new Jira1563.Version();
		version.application = 'railo';
		version.version = '76';
		version.build = '21';
		version.builddate = bd;

		data=service.returnVersion(version:version);
		assertEquals("railo",data.application);
		assertEquals("76",data.version);
		assertEquals("21",data.build);
		assertEquals(dateTimeFormat(bd),dateTimeFormat(data.builddate));
		
	}
	public void function testSendStruct() localMode="modern" {
		// send a struct
		bd=now();
		sct={application:'railo',version:'76',build:'21',builddate:bd};
		data=service.returnVersion(version:sct);
		assertEquals("railo",data.application);
		assertEquals("76",data.version);
		assertEquals("21",data.build);
		assertEquals(dateTimeFormat(bd),dateTimeFormat(data.builddate));
		
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}

} 
</cfscript>