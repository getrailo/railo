<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	variables.serverAdminPassword="server";
	variables.webAdminPassword="server";
	
	
	public function beforeTests(){
		// store existing api-key
		admin type="web" password="#variables.webAdminPassword#" action="getAPIKey" returnvariable="variables.oldWebAPIKey";
		admin type="server" password="#variables.serverAdminPassword#" action="getAPIKey" returnvariable="variables.oldServerAPIKey";
		
	}
	
	public function afterTests(){
		
		// reset old api-key
		if(isNull(variables.oldServerAPIKey))
			admin type="server" password="#variables.serverAdminPassword#" action="removeAPIKey";
		else 
			admin type="server" password="#variables.serverAdminPassword#" action="updateAPIKey" key="#variables.oldServerAPIKey#";
			
		if(isNull(variables.oldWebAPIKey))
			admin type="web" password="#variables.webAdminPassword#" action="removeAPIKey";
		else 
			admin type="web" password="#variables.webAdminPassword#" action="updateAPIKey" key="#variables.oldWebAPIKey#";
	}
	
	//public function setUp(){}

	public void function testGetAPIKey(){
		local.serverKey=createUUid();
		local.webKey=createUUid();
		
		// set and read
		admin type="server" password="#variables.serverAdminPassword#" action="updateAPIKey" key="#serverKey#";
		admin type="web" password="#variables.webAdminPassword#" action="updateAPIKey" key="#webKey#";

		admin type="server" password="#variables.serverAdminPassword#" action="getAPIKey" returnvariable="local.k";
		assertEquals(serverKey,k);
		admin type="web" password="#variables.webAdminPassword#" action="getAPIKey" returnvariable="local.k";
		assertEquals(webKey,k);
		
		local.serverKey=createUUid();
		local.webKey=createUUid();
		
		// reset and read
		admin type="server" password="#variables.serverAdminPassword#" action="updateAPIKey" key="#serverKey#";
		admin type="web" password="#variables.webAdminPassword#" action="updateAPIKey" key="#webKey#";

		admin type="server" password="#variables.serverAdminPassword#" action="getAPIKey" returnvariable="local.k";
		assertEquals(serverKey,k);
		admin type="web" password="#variables.webAdminPassword#" action="getAPIKey" returnvariable="local.k";
		assertEquals(webKey,k);
		
		
		
		
		// when no web api key is defined server api key is used
		admin type="web" password="#variables.webAdminPassword#" action="removeAPIKey";
		admin type="web" password="#variables.webAdminPassword#" action="getAPIKey" returnvariable="local.kk";
		assertEquals(serverKey,kk);
		
		// when no api key exists null is returned
		admin type="server" password="#variables.serverAdminPassword#" action="removeAPIKey";
		admin type="server" password="#variables.serverAdminPassword#" action="getAPIKey" returnvariable="local.k";
		assertEquals(true,isNull(k));
		admin type="web" password="#variables.webAdminPassword#" action="getAPIKey" returnvariable="local.k";
		assertEquals(true,isNull(k));
	}
} 
</cfscript>