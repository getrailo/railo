component extends="org.railo.cfml.test.RailoTestCase" {
	
	function setup() {
		variables.ws = CreateObject( 'webservice', createURL("Jira2965/service.cfc?wsdl"));
		
	}
	

	function testGiveIntegerPropertyAsInteger () {
		var item=new Jira2965.MyItem();
		item.name="Susi";
		item.id=1;
		local.accept = variables.ws.giveIntegerPropertyAsInteger(item);
	}

	function testGiveIntegerPropertyAsInteger2 () {
		var item=new Jira2965.MyItemWithString();
		//dump(ws);abort;
		item.name="Susi";
		item.id=1;
		local.accept = variables.ws.giveIntegerPropertyAsInteger(item);
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
	function giveStringPropertyAsStringTest () {
		local.accept = variables.ws.giveStringPropertyAsString( {name:"si", id: "7"}  );
	}

	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
}