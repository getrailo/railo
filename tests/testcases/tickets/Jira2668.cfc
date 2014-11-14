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


	public void function testCompressionNoAttr(){
		http method="get" result="local.result" url="#createURL("Jira2668/index.cfm")#" addtoken="false";
		var req=evaluate(replace(result.filecontent,'>>','','all'));
		assertEquals("gzip",req.headers['Accept-Encoding']);
		assertEquals("text/html; charset=utf-8",result.responseheader['Content-Type']);
	}
	
	public void function testCompressionNone(){
		http method="get" compression="none" result="local.result" url="#createURL("Jira2668/index.cfm")#" addtoken="false";
		var req=evaluate(replace(result.filecontent,'>>','','all'));
		
		assertEquals("deflate;q=0",req.headers['Accept-Encoding']);
		assertEquals("deflate;q=0",req.headers['TE']);
		assertEquals("text/html; charset=utf-8",result.responseheader['Content-Type']);
	}
	
	
	public void function testCompressionTrue(){
		http method="get" compression="true" result="local.result" url="#createURL("Jira2668/index.cfm")#" addtoken="false";
		var req=evaluate(replace(result.filecontent,'>>','','all'));
		assertEquals("gzip",req.headers['Accept-Encoding']);
		assertEquals("text/html; charset=utf-8",result.responseheader['Content-Type']);
	}
	
	
	public void function testCompressionFalse(){
		http method="get" compression="false" result="local.result" url="#createURL("Jira2668/index.cfm")#" addtoken="false";
		var req=evaluate(replace(result.filecontent,'>>','','all'));
		
		assertEquals("deflate;q=0",req.headers['Accept-Encoding']);
		assertEquals("deflate;q=0",req.headers['TE']);
		assertEquals("text/html; charset=utf-8",result.responseheader['Content-Type']);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>