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
 ---><cfcomponent output="false">

	
	<!---get--->
	<cffunction name="get" access="remote" output="false" returntype="string" returnformat="plain">
		<cfargument name="lib" type="string">
		
		<!--- restrict to files from JS directory !--->
		<cfif arguments.lib CT "..">
			<cfheader statuscode="400">
			<cfreturn "// 400 - Bad Request">
		</cfif>
		
		<cfset var relPath = "js/#arguments.lib#.js">

		<cfif fileExists( expandPath( relPath ) )>

			<cfcontent type="text/javascript">
			<cfsavecontent variable="local.result"><cfinclude template="#relPath#"></cfsavecontent>
			<cfreturn result>
		<cfelse>
			
			<cfheader statuscode="404">
			<cfreturn "// 404 - Not Found">
		</cfif>
	</cffunction>

	
</cfcomponent>