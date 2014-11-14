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
 ---><cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">	
	
<cfadmin 
        action="getLogSettings" 
        type="#request.adminType#"
        password="#session["password"&request.adminType]#"
        
        returnVariable="logs"
        remoteClients="#request.getRemoteClients()#">

<!--- load available appenders --->
<cfset appenders={}>
<cfset names=structnew("linked")>
<cfset names=ComponentListPackageAsStruct("railo-server-context.admin.logging.appender",names)>
<cfset names=ComponentListPackageAsStruct("railo-context.admin.logging.appender",names)>
<cfset names=ComponentListPackageAsStruct("logging.appender",names)>
<cfloop collection="#names#" index="n" item="fn">
	<cfif n NEQ "Appender" and n NEQ "Field" and n NEQ "Group">
		<cfset tmp = createObject("component",fn)>
		<cfset appenders[tmp.getClass()]=tmp>
	</cfif>
</cfloop>
 
<!--- load available layouts --->
<cfset layouts={}>
<cfset names=structnew("linked")>
<cfset names=ComponentListPackageAsStruct("railo-server-context.admin.logging.layout",names)>
<cfset names=ComponentListPackageAsStruct("railo-context.admin.logging.layout",names)>
<cfset names=ComponentListPackageAsStruct("logging.layout",names)>
<cfloop collection="#names#" index="n" item="fn">
	<cfif n NEQ "Layout" and n NEQ "Field" and n NEQ "Group">
		<cfset tmp = createObject("component",fn)>
		<cfset layouts[tmp.getClass()]=tmp>
	</cfif>
</cfloop>


<cfset access=true>
<!--- TODO
<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="access"
	secType="logging">
	 --->
	

<cfswitch expression="#url.action2#">
	<cfcase value="list"><cfinclude template="server.logging.list.cfm"/></cfcase>
	<cfcase value="create"><cfinclude template="server.logging.create.cfm"/></cfcase>

</cfswitch>