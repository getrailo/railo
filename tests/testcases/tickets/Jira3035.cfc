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

	//public function setUp(){}

	public void function testWithoutAbort(){
		http method="get" result="local.result" url="#createURL("Jira3035/XMLService.cfc?method=returnXMLWithoutAbort")#" addtoken="false";
		assertEquals("application/xml; charset=UTF-8",result.responseheader["Content-Type"]);
	}
	public void function testWithAbort(){
		http method="get" result="local.result" url="#createURL("Jira3035/XMLService.cfc?method=returnXMLWithAbort")#" addtoken="false";
		assertEquals("application/xml; charset=UTF-8",result.responseheader["Content-Type"]);
	}


	public void function testWithReturnFormat(){
		http method="get" result="local.result" url="#createURL("Jira3035/XMLService.cfc?method=returnXMLWithReturnFormat")#" addtoken="false";
		assertEquals("application/xml; charset=UTF-8",result.responseheader["Content-Type"]);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>