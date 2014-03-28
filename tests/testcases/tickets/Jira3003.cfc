<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		variables.serviceURL =createURL("Jira3003/wstest01.cfc?wsdl");
		variables.service = CreateObject("webservice", serviceURL);
	}

	public void function testSendComponent() localMode="modern" {
		bd=now();
		param = new Jira3003.wstest01Request();
		param.idSocieta = '...';

		data=service.run(reqParams:param);
		
		assertEquals("RUN OK",data);
		
	}



	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}

} 
</cfscript>