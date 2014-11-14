/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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