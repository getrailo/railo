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

	// calling via http is necessary because we have a template exception
	public void function testTagTryCatchFinally(){
		http method="get" result="local.result" url="#createURL("Jira3132/index.cfm?tagtcf=true")#" addtoken="false";
		assertEquals("tag:in finally;",trim(result.filecontent));
	}
	public void function testTagTryFinally(){
		http method="get" result="local.result" url="#createURL("Jira3132/index.cfm?tagtf=true")#" addtoken="false";
		assertEquals("tag:in finally;",trim(result.filecontent));
	}
	public void function testScriptTryCatchFinally(){
		http method="get" result="local.result" url="#createURL("Jira3132/index.cfm?scripttcf=true")#" addtoken="false";
		assertEquals("script:in finally;",trim(result.filecontent));
	}
	public void function testScriptTryFinally(){
		http method="get" result="local.result" url="#createURL("Jira3132/index.cfm?scripttf=true")#" addtoken="false";
		assertEquals("script:in finally;",trim(result.filecontent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>