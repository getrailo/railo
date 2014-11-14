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
 ---><cfinclude template="../resources/resources.cfm">

<cfset dir=GetDirectoryFromPath(getCurrenttemplatePath())>
<cfdump var="#dir#">
<cfset dir=GetDirectoryFromPath(cgi.script_name)>
<cfset dir=left(GetDirectoryFromPath(cgi.script_name),len(dir)-1)>
<cfset dir=replace(GetDirectoryFromPath(dir),"/",".","all")>

<cfdirectory directory="../dbdriver" action="list" name="dbdriver" filter="*.cfc">
<cfset drivers=struct()>
<cfoutput query="dbdriver">
	<cfset n=listFirst(dbdriver.name,".")>
	<cfif n NEQ "Driver">
		<cfset drivers[n]=createObject("component",dir&"content.dbdriver."&n)>
	</cfif>
</cfoutput>


<cfoutput><cfset serialize(drivers)></cfoutput>
<!--- 

<cfdump var="#drivers#">

<cffile action="write" file="../resources/text.cfm" output="#content#" addnewline="yes"> --->