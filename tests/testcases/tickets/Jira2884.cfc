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