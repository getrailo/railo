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
 --->
<cfcomponent output="false">
	<cffunction name="echoVersion" output="false" access="remote" returntype="Version">
		<cfargument name="version" type="Version" />
		<cfreturn arguments.version />
	</cffunction>

	<cffunction name="returnVersion" output="false" access="remote" returntype="Version">
		<cfargument name="version" type="Version" />
		<cfset local.result = createObject("component","Version") />


		<cfset local.result.application = arguments.version.application />
		<cfset local.result.build = arguments.version.build />
		<cfset local.result.builddate = arguments.version.builddate />
		<cfset local.result.version = arguments.version.version />
		<cfreturn local.result />
	</cffunction>
</cfcomponent>