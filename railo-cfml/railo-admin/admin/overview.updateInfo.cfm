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
 ---><cfparam name="url.action2" default="none">
<cfswitch expression="#url.action2#">
	<cfcase value="update">
		<cfadmin 
			action="runUpdate"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#">
			<cfset StructClear(session)>
	</cfcase>
</cfswitch>

<cffunction name="getAvailableVersion" output="false">
	<cfif structKeyExists(session,"availableVersion")>
		<cfreturn session.availableVersion>
	</cfif>
	<cfset var http="">
	<cftry>
	<cfhttp 
		url="#update.location#/railo/remote/version/Info.cfc?method=getpatchversionfor&version=#server.railo.version#" 
		method="get" resolveurl="no" result="http">
	<cfwddx action="wddx2cfml" input="#http.fileContent#" output="wddx">
	<cfset session.availableVersion=wddx>
	<cfreturn session.availableVersion>
		<cfcatch>
			<cfreturn "">
		</cfcatch>
	</cftry>
</cffunction>

<cffunction name="getAvailableVersionDoc" output="false">
	
	<cfset var http="">
	<cftry>
	<cfhttp 
		url="#update.location#/railo/remote/version/Info.cfc?method=getPatchVersionDocFor&version=1.0.0.015" 
		method="get" resolveurl="no" result="http"><!--- #server.railo.version# --->
	<cfwddx action="wddx2cfml" input="#http.fileContent#" output="wddx">
	<cfreturn wddx>
		<cfcatch>
			<cfreturn "">
		</cfcatch>
	</cftry>
</cffunction>
<cfoutput>

<cfadmin 
		action="getUpdate"
		type="#request.adminType#"
		password="#session["password"&request.adminType]#"
		returnvariable="update">

<h2>Update</h2>
<a href="#go(url.action,"update")#">Update to #getAvailableVersion()#</a><br>
Das Update wird in einem eigenen Prozess ablaufen. 
Wenn das System gepatcht wird, werden Sie Ihre Session verliehren und m�ssen Sie sich frisch einloggen.

<br><br>
<h2>Update Info</h2>
 - Installed Version #server.railo.version#<br>
 - Available Version #getAvailableVersion()#<br>
<form>
<textarea name="doc" rows="30" cols="90">#getAvailableVersionDoc()#</textarea>
</form>
<pre></pre>
</cfoutput>