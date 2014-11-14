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
	<cffunction name="test">
		<cfstoredproc procedure="spweb_ZipCodes_LookupLatLon3074" datasource="mySQL" cachedwithin="#CreateTimeSpan(0,0,10,0)#">
		    <cfprocparam cfsqltype="cf_sql_varchar" value="57103">
		    <cfprocresult name="rsLatLon" resultset="1">
		</cfstoredproc>

		<cfset assertEquals("","")>
	</cffunction>
</cfcomponent>