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

	public void function testForm(){
		// FALSE
		http method="post" result="local.result" url="#createURL("Jira1923/index.cfm?sameFormFieldsAsArray=false")#" addtoken="false" {
			httpparam type="formfield" name="test" value="1";
			httpparam type="formfield" name="test" value="2";
		}
		assertEquals("form:'1,2'->false;",trim(result.filecontent));
		
		// TRUE
		http method="post" result="local.result" url="#createURL("Jira1923/index.cfm?sameFormFieldsAsArray=true")#" addtoken="false" {
			httpparam type="formfield" name="test" value="1";
			httpparam type="formfield" name="test" value="2";
		}
		assertEquals("form:['1','2']->true;",trim(result.filecontent));
	}


	public void function testURL(){
		// FALSE
		http method="get" result="local.result" url="#createURL("Jira1923/index.cfm?sameURLFieldsAsArray=false")#" addtoken="false" {
			httpparam type="url" name="test" value="1";
			httpparam type="url" name="test" value="2";
		}
		assertEquals("url:'1,2'->false;",trim(result.filecontent));
		
		// TRUE
		http method="get" result="local.result" url="#createURL("Jira1923/index.cfm?sameURLFieldsAsArray=true")#" addtoken="false" {
			httpparam type="url" name="test" value="1";
			httpparam type="url" name="test" value="2";
		}
		assertEquals("url:['1','2']->true;",trim(result.filecontent));
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>