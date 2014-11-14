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

	public void function testWithContentTypeDeclaration(){
		http result="local.result" url="#createURL("Jira2715/index.cfm")#" method="post" {
			httpparam type="header" name="Content-Type" value="application/x-www-form-urlencoded";
			httpparam type="body" value="bas=1&railo=2";
		}
		
		sct=evaluate(trim(result.filecontent));
		assertEquals("bas,fieldnames,railo",listSort(structKeyList(sct),'textnocase'));
		assertEquals("1",sct.bas);
		assertEquals("2",sct.railo);
	}
	
	private void function testNoContentTypeDeclaration(){
		http result="local.result" url="#createURL("Jira2715/index.cfm")#" method="post" {
			httpparam type="body" value="bas=1&railo=2";
		}
		
		sct=evaluate(trim(result.filecontent));
		assertEquals("bas,fieldnames,railo",listSort(structKeyList(sct),'textnocase'));
		assertEquals("1",sct.bas);
		assertEquals("2",sct.railo);
	}
	
	private void function test(){
		http result="local.result" url="#createURL("Jira2715/reqhead.cfm")#" method="post" {
			httpparam type="body" value="bas=1&railo=2";
		}
		
		sct=evaluate(trim(result.filecontent));
		//dump(createURL("Jira2715/reqhead.cfm")); abort;
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>