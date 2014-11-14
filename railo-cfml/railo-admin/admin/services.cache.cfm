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
	action="getCacheConnections"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="connections">
 
<!--- load available drivers --->
<cfset driverNames=structnew("linked")>
<cfset driverNames=ComponentListPackageAsStruct("railo-server-context.admin.cdriver",driverNames)>
<cfset driverNames=ComponentListPackageAsStruct("railo-context.admin.cdriver",driverNames)>
<cfset driverNames=ComponentListPackageAsStruct("cdriver",driverNames)>

<cfset drivers={}>
<cfloop collection="#driverNames#" index="n" item="fn">
	
	<cfif n NEQ "Cache" and n NEQ "Field" and n NEQ "Group">
		<cfset tmp = createObject("component",fn)>
		<!--- Workaround for EHCache Extension --->
		<cfset clazz=tmp.getClass()>
		<cfif "railo.extension.io.cache.eh.EHCache" EQ clazz>
			<cfset clazz="railo.runtime.cache.eh.EHCache">
		</cfif>
		<cfset drivers[clazz]=tmp>
	</cfif>
</cfloop>
<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="access"
	secType="cache">

<cfswitch expression="#url.action2#">
	<cfcase value="list"><cfinclude template="services.cache.list.cfm"/></cfcase>
	<cfcase value="create"><cfinclude template="services.cache.create.cfm"/></cfcase>

</cfswitch>