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

	public void function testSuppressOnAppCFC(){
		http method="get" result="local.result" url="#createURL("Jira2786/appcfc/Test.cfc?method=test&suppress=true")#" addtoken="false";
		assertEquals('{}',result.filecontent);
	}
	
	public void function testSuppressOffAppCFC(){
		http method="get" result="local.result" url="#createURL("Jira2786/appcfc/Test.cfc?method=test&suppress=false")#" addtoken="false";
		assertEquals('Body Content{}',result.filecontent);
	}
	
	public void function testSuppressOnTagApp(){
		http method="get" result="local.result" url="#createURL("Jira2786/tag/Test.cfc?method=test&suppress=true")#" addtoken="false";
		assertEquals('{}',result.filecontent);
	}
	
	public void function testSuppressOffTagApp(){
		http method="get" result="local.result" url="#createURL("Jira2786/tag/Test.cfc?method=test&suppress=false")#" addtoken="false";
		assertEquals('Body Content{}',result.filecontent);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>