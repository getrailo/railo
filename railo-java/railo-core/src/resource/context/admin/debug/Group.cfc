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
 ---><cfcomponent output="no">
	<cfset this.data=struct()>
	<cffunction name="init"
		hint="init method of the group">
		<cfargument name="displayName" required="true" type="string">
		<cfargument name="description" required="false" type="string" default="">
		<cfargument name="level" required="false" type="numeric" default="2">
		
		<cfset this.data.displayName=arguments.displayName>
		<cfset this.data.description=arguments.description>
		<cfset this.data.level=arguments.level>
		
		<cfreturn this>
	</cffunction>
	
	<cffunction name="getDisplayName" returntype="string" output="no"
		hint="returns the Display Name">
		<cfreturn this.data.displayName>
	</cffunction>
	
	<cffunction name="getDescription" returntype="string" output="no"
		hint="returns the description value">
		<cfreturn this.data.description>
	</cffunction>
	<cffunction name="getLevel" returntype="numeric" output="no"
		hint="returns the level">
		<cfreturn this.data.level>
	</cffunction>
	
</cfcomponent>