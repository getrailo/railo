<!--- 
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
 ---><cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<!---
	<cffunction name="beforeTests"></cffunction>
	<cffunction name="afterTests"></cffunction>
	<cffunction name="setUp"></cffunction>
	--->
	<cffunction name="testMixed" localmode="modern" access="public">

		<cfhttp url="#wsdlURL#" result="wsdlResult"></cfhttp>
		<cfhttp url="#cfcURL#" result="wsResult">
			<cfhttpparam type="URL" name="method" value="getItems" />
		</cfhttp>

		<cfset container = service.getItems() />	
		<cfset assertEquals(4,arrayLen(container.items))>
		<cfset assertEquals(1,(container.items[1].getId()))>
	</cffunction>

	<cffunction name="testMixedItem" localmode="modern" access="public">

		<cfhttp url="#wsdlURL#" result="wsdlResult"></cfhttp>
		<cfhttp url="#cfcURL#" result="wsResult">
			<cfhttpparam type="URL" name="method" value="getItemsMixed" />
		</cfhttp>

		<cfset container = service.getItemsMixed() />	
		<cfset assertEquals(4,arrayLen(container.items))>
		


		<cfset assertEquals(false,isNull(container.items[1].url))>
		<cfset assertEquals(true,isNull(container.items[2].url))>
		

	</cffunction>

	<cffunction name="testInterfaceItem" localmode="modern" access="private">

		<cfhttp url="#wsdlURL#" result="wsdlResult"></cfhttp>
		<cfhttp url="#cfcURL#" result="wsResult">
			<cfhttpparam type="URL" name="method" value="getItemsInterface" />
		</cfhttp>

		<cfset container = service.getItemsInterface() />	

		<cfdump var="#container#" />
		<cfdump var="#container.items#" />

		<cfdump var="#wsResult#" />
		<cfdump var="#wsdlResult#" />
		<cfabort>

	</cffunction>



<cfscript>
	
	public function beforeTests(){
		variables.cfcURL =createURL("Jira1987/Service.cfc");
		variables.wsdlURL =createURL("Jira1987/Service.cfc?wsdl");
		variables.service = CreateObject("webservice", wsdlURL);
	}

	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}

</cfscript>

</cfcomponent>