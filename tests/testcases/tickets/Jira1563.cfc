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