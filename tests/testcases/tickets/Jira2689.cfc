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

	public void function testApplication_cfc(){
		http method="get" result="local.result" url="#createURL("Jira2689/modern/index.cfm")#" addtoken="false" {
			
		}
		settings=evaluate(trim(result.filecontent));
		assertEquals("UTF-16",settings.charset.web);
		assertEquals("UTF-16",settings.charset.resource);
		
	}
	public void function testApplication_cfm(){
		http method="get" result="local.result" url="#createURL("Jira2689/classic/index.cfm")#" addtoken="false" {
			
		}
		settings=evaluate(trim(result.filecontent));
		assertEquals("UTF-16",settings.charset.web);
		assertEquals("UTF-16",settings.charset.resource);
		
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>