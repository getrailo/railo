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


	public void function test(){
		http method="post" result="local.result" url="#createURL("Jira2680/test1.cfm")#" addtoken="false" {
			// Body httpparam type="body" name="test" value="";
			httpparam type="formfield" name="bas[num]" value="test";
			httpparam type="formfield" name="bas2[]" value="test";
		}
		sct=evaluate(trim(result.filecontent));
		assertEquals(true,structKeyExists(sct,'bas[num]'));
		assertEquals(true,structKeyExists(sct,'bas2'));
		assertEquals(true,isArray(sct.bas2));
		
	}
	public void function testSquareBracket(){
		http method="post" result="local.result" url="#createURL("Jira2680/test2.cfm")#" addtoken="false" {
			// Body httpparam type="body" name="test" value="";
			httpparam type="formfield" name="susi" value="test";
			httpparam type="formfield" name="bas[num]" value="test";
		}
		assertEquals(200,result.status_code);
		
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>