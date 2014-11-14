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
 ---><cfif request.admintype EQ "server"><cflocation url="#request.self#" addtoken="no"></cfif>

<cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">

<cfadmin 
	action="getGatewayEntries"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="entries">
    
<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="access"
	secType="gateway">
    
	



<!--- load available drivers --->
<cfset variables.drivers={}>
<cfset driverNames=structnew("linked")>
<cfset driverNames=ComponentListPackageAsStruct("railo-server-context.admin.gdriver",driverNames)>
<cfset driverNames=ComponentListPackageAsStruct("railo-context.admin.gdriver",driverNames)>
<cfset driverNames=ComponentListPackageAsStruct("gdriver",driverNames)>

<cfloop collection="#driverNames#" index="n" item="fn">
	
	<cfif n NEQ "Gateway" and n NEQ "Field" and n NEQ "Group">
		<cfset tmp = createObject("component",fn)>
		<cfset drivers[n]=tmp>
	</cfif>
</cfloop>
	
<!--- add driver to query --->
<cfset QueryAddColumn(entries,"driver",array())>
<cfloop query="entries">
    <cfloop collection="#drivers#" index="key" item="d">
    	<cfif 
			(StructKeyExists(d,'getCFCPath')?d.getCFCPath() EQ entries.cfcPath:"" EQ entries.cfcPath)
			and 
			(StructKeyExists(d,'getClass')?d.getClass() EQ entries.class:"" EQ entries.class)
			>
			<cfset QuerySetCell(entries,"driver",d,entries.currentrow)>
            
		</cfif>
    </cfloop>
    
</cfloop>

<!---<span class="CheckError">
The Gateway Implementation is currently in Beta State. Its functionality can change before it's final release.
If you have any problems while using the Gateway Implementation, please post the bugs and errors in our <a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank" class="CheckError">bugtracking system</a>. 
</span><br /><br />--->

<cfswitch expression="#url.action2#">
	<cfcase value="list"><cfinclude template="services.gateway.list.cfm"/></cfcase>
	<cfcase value="create"><cfinclude template="services.gateway.create.cfm"/></cfcase>

</cfswitch>