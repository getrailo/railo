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
 ---><cfcomponent output="false" hint="A generic service.">
	
	<cffunction name="getMembers" access="remote" returntype="Members"
			output="false"
			hint="Returns a Members object containing an array of Member
				objects.">
		
		<cfset local.memberCount = 3 />

		<cfset local.membersObj = CreateObject( 'component', 'Members' ) />
		<cfset local.membersObj.members = [] />

		<cfloop from="1" to="#local.memberCount#" index="local.i">
			<cfset local.memberObj = CreateObject( 'component', 'Member' ) />
			<cfset local.memberObj.name = 'Member #local.i#' />
			<cfset local.memberObj.joinDate =CreateDate( 2012 - local.i, local.i, 1 ) />

			<cfset ArrayAppend( local.membersObj.members, local.memberObj ) />
		</cfloop>
		<xcfset systemOutput(membersObj)>
		<cfreturn local.membersObj />
	</cffunction>

</cfcomponent>