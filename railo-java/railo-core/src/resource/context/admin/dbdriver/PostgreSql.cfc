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
 ---><cfcomponent extends="types.Driver" implements="types.IDatasource">
	<cfset this.dsn="jdbc:postgresql://{host}:{port}/{database}">
	<cfset this.className="org.postgresql.Driver">
	
	<cfset this.type.port=this.TYPE_FREE>
	<cfset this.value.host="localhost">
	<cfset this.value.port=5432>
	<cfset fields=array()>
	
	<cffunction name="getName" returntype="string" output="no"
		hint="returns display name of the driver">
		<cfreturn "PostgreSQL">
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns description for the driver">
		<cfreturn "PostgreSQL JDBC Driver">
	</cffunction>
	
	<cffunction name="getFields" returntype="array" output="no"
		hint="returns array of fields">
		<cfreturn fields>
	</cffunction>
	
	<cffunction name="getUsername" returntype="string" output="no"
		hint="return Username">
		<cfreturn data.username>
	</cffunction>
	
	<cffunction name="getPassword" returntype="string" output="no"
		hint="return Password">
		<cfreturn data.password>
	</cffunction>
	
	
</cfcomponent>