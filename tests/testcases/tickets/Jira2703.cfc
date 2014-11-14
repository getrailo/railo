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

	public function setUp(){
		
	}
	
	public void function testScopeCascadingModernStandard(){
		assertEquals(
		"standard->true;true;true;true;true;",
		call("modern","standard"));
	}
	public void function testScopeCascadingModernSmall(){
		assertEquals(
		"small->true;true;true;false;false;",
		call("modern","small"));
	}
	public void function testScopeCascadingModernStrict(){
		assertEquals(
		"strict->true;false;false;false;false;",
		call("modern","strict"));
	}
	
	public void function testScopeCascadingClassicStandard(){
		assertEquals(
		"standard->true;true;true;true;true;",
		call("classic","standard"));
	}
	public void function testScopeCascadingClassicSmall(){
		assertEquals(
		"small->true;true;true;false;false;",
		call("classic","small"));
	}
	public void function testScopeCascadingClassicStrict(){
		assertEquals(
		"strict->true;false;false;false;false;",
		call("classic","strict"));
	}
	
	
	private function call(type,casc){
		http method="get" result="local.result" url="#createURL("Jira2703/"&type&"/index.cfm?scopecascading="&casc)#" addtoken="false" {
		}
		return trim(result.filecontent);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>