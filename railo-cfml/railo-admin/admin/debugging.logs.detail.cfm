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

<cfoutput>


<!--- load available drivers --->
<cfset driverNames=structnew("linked")>
<cfset driverNames=ComponentListPackageAsStruct("railo-server-context.admin.debug",driverNames)>
<cfset driverNames=ComponentListPackageAsStruct("railo-context.admin.debug",driverNames)>
<cfset driverNames=ComponentListPackageAsStruct("debug",driverNames)>

<cfset drivers={}>
<cfloop collection="#driverNames#" index="n" item="fn">
	<cfif n EQ "Debug" or n EQ "Field" or n EQ "Group">
    	<cfcontinue>
    </cfif>
	<cfset tmp=createObject('component',fn)>
    <cfset drivers[trim(tmp.getId())]=tmp>
</cfloop>
	
    <cfset driver=drivers["railo-modern"]>
	<cfset entry={}>
	<cfloop query="entries">
		<cfif entries.type EQ "railo-modern">
        	<cfset entry=querySlice(entries, entries.currentrow ,1)>
        </cfif>    
    </cfloop>
	
	
	<!--- get matching log entry --->
	<cfset log="">
    <cfloop from="1" to="#arrayLen(logs)#" index="i">
    	<cfset el=logs[i]>
    	<cfset id=hash(el.id&":"&el.startTime)>
        <cfif url.id EQ id>
        	<cfset log=el>
        </cfif>
    </cfloop>
    
    <table width="100%">
    <tr>
    	<td><cfif !isSimpleValue(log)>
			<cfset c=structKeyExists(entry,'custom')?entry.custom:{}>
			<cfset c.scopes=false>
			<cfset driver.output(c,log,"admin")><cfelse>Data no longer available</cfif> </td>
    </tr>
    </table>
	
    
<table class="tbl" width="740">
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post" name="debug_settings">
<tr>
    <td ><input type="submit" name="mainAction" class="submit" value="#stText.buttons.back#" /></td>
</tr>
</cfform>
</table>





</cfoutput>
<br><br>
