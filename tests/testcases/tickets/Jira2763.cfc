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

	

	public void function testTagAttrApplication_cfc1(){
		http method="get" result="local.result" url="#createURL("Jira2763/index.cfm?trim=true")#" addtoken="false";
		assertEquals("-a-",trim(result.filecontent));
	}
	public void function testTagAttrApplication_cfc2(){
		http method="get" result="local.result" url="#createURL("Jira2763/index.cfm?trim=false")#" addtoken="false";
		assertEquals("- a -",trim(result.filecontent));
	}
	
	public void function testTagAttrCFApplication1(){
		application action="update" tag="#{savecontent:{trim:false}}#";
		savecontent variable="local.c" {
			echo(" a ");
		}
		assertEquals(" a ",c);
	}
	
	public void function testTagAttrCFApplication2(){
		application action="update" tag="#{savecontent:{trim:true}}#";
		savecontent variable="local.c" {
			echo(" a ");
		}
		assertEquals("a",c);
	}
	
	public void function testTagAttrCFApplication3(){
		application action="update" tag="#{cfsavecontent:{trim:true}}#";
		savecontent variable="local.c" {
			echo(" a ");
		}
		assertEquals("a",c);
	}
	
	public void function testTagAttrCFApplication4(){
		application action="update" tag="#{cfsavecontent:{trim:false}}#";
		savecontent variable="local.c" {
			echo(" a ");
		}
		assertEquals(" a ",c);
	}
	
	
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>