component extends="org.railo.cfml.test.RailoTestCase" {
	
	function setup() {
		variables.ws = CreateObject( 'webservice', createURL("Jira2965/service.cfc?wsdl"));
		
	}
	
	function giveIntegerPropertyAsIntegerTest () {
		local.accept = variables.ws.giveIntegerPropertyAsInteger({name:"ii", id: 7}  );
	}
	function giveIntegerPropertyAsStringTest () {
		local.accept = variables.ws.giveIntegerPropertyAsString( {name:"is", id: "7"});
	}
	function giveStringPropertyAsIntegerTest () {
		local.accept = variables.ws.giveStringPropertyAsInteger( {name:"si", id: 7}  );
	}
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
}