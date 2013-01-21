

<cfoutput>



<!--- load available drivers --->
	<cfset drivers=struct()>
    <cfdirectory directory="./debug" action="list" name="dir" recurse="no" filter="*.cfc">
    <cfloop query="dir">
    	<cfif dir.name EQ "Debug.cfc" or dir.name EQ "Field.cfc" or dir.name EQ "Group.cfc">
        	<cfcontinue>
        </cfif>
    	<cfset tmp=createObject('component','debug/#ReplaceNoCase(dir.name,'.cfc','')#')>
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
