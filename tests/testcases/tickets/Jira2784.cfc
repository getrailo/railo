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


	public void function testCompressionOnAppCFC(){
		// compress is disabled when this is enabled
		if(getPageContext().getConfig().debugLogOutput()) return;

		http method="get" result="local.result" url="#createURL("Jira2784/appcfc/index.cfm?compression=true")#" addtoken="false";
		assertEquals(false,isNull(result.responseheader['Content-Encoding']));
		assertEquals('gzip',result.responseheader['Content-Encoding']);
	}
	
	public void function testCompressionOffAppCFC(){
		http method="get" result="local.result" url="#createURL("Jira2784/appcfc/index.cfm?compression=false")#" addtoken="false";
		assertEquals(true,isNull(result.responseheader['Content-Encoding']));
	}
	
	public void function testCompressionOnTagApp(){
		// compress is disabled when this is enabled
		if(getPageContext().getConfig().debugLogOutput()) return;

		http method="get" result="local.result" url="#createURL("Jira2784/tag/index.cfm?compression=true")#" addtoken="false";
		assertEquals(false,isNull(result.responseheader['Content-Encoding']));
		assertEquals('gzip',result.responseheader['Content-Encoding']);
	}
	
	public void function testCompressionOffTagApp(){
		http method="get" result="local.result" url="#createURL("Jira2784/tag/index.cfm?compression=false")#" addtoken="false";
		assertEquals(true,isNull(result.responseheader['Content-Encoding']));
	}
	
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>