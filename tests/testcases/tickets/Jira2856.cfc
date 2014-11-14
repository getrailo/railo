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

	public void function testSingleString(){
		http method="get" result="local.result" url="#createURL("Jira2856/singlestring/index.cfm")#" addtoken="false";
		assertEquals(200,result.status_code);
		assertEquals('',trim(result.filecontent));
	}
	
	public void function testListString(){
		http method="get" result="local.result" url="#createURL("Jira2856/liststring/index.cfm")#" addtoken="false";
		//echo(result.filecontent);abort;
		assertEquals(200,result.status_code);
		assertEquals('',trim(result.filecontent));
	}
	
	public void function testArray(){
		http method="get" result="local.result" url="#createURL("Jira2856/array/index.cfm")#" addtoken="false";
		//echo(result.filecontent);abort;
		assertEquals(200,result.status_code);
		assertEquals('',trim(result.filecontent));
	}
	
	public void function testStruct(){
		http method="get" result="local.result" url="#createURL("Jira2856/struct/index.cfm")#" addtoken="false";
		//echo(result.filecontent);abort;
		assertEquals(200,result.status_code);
		assertEquals('',trim(result.filecontent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>