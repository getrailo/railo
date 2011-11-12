

<cfadmin 
        action="getLogSettings" 
        type="#request.adminType#"
        password="#session["password"&request.adminType]#"
        
        returnVariable="logs"
        remoteClients="#request.getRemoteClients()#">

<cfdump var="#logs#">
<cfset caller.stText.log.level="Level">
<cfset caller.stText.log.source="Source">
<cfset caller.stText.log.name="Name">
<cfset caller.stText.log.maxFile="Max Files">
<cfset caller.stText.log.maxFileSize="Max File Size in KB">

<cfoutput>
<cfloop query="logs">
<table class="tbl" width="740">
<tr>
	<td colspan="2"><h2>#ucFirst(logs.name)# #attributes.title#</h2>#attributes.description#</td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#&action2=create" method="post">
<tr>
	<td class="tblHead">#caller.stText.log.level#</td>
	<td class="tblContent">
    <select name="#logs.name#_level">
    	<cfloop list="Info,Debug,Warn,Error,Fatal" index="l"><option <cfif l EQ level>selected</cfif>>#l#</option></cfloop>
	</select></td>
</tr>
<tr>
	<td class="tblHead"><label for="#logs.name#_source">#caller.stText.log.source#</label></td>
	<td class="tblContent"><cfinput type="text" name="#logs.name#_source" id="#logs.name#_source" value="#logs.virtualpath#" style="width:300px" required="yes" message=""></td>
</tr>
<tr>
	<td class="tblHead"><label for="#logs.name#_maxFile">#caller.stText.log.maxFile#</label></td>
	<td class="tblContent"><cfinput type="text" name="#logs.name#_maxFile" id="#logs.name#_maxFile" value="#logs.maxFile#" style="width:60px" required="yes" message=""></td>
</tr>
<tr>
	<td class="tblHead"><label for="#logs.name#_maxFileSize">#caller.stText.log.maxFileSize#</label></td>
	<td class="tblContent"><cfinput type="text" name="#logs.name#_maxFileSize" id="#logs.name#_maxFileSize" value="#logs.maxFileSize/1024#" style="width:60px" required="yes" message=""></td>
</tr>
<tr>
</tr>
<tr>
</tr>






<!---<cfmodule template="remoteclients.cfm" colspan="2">--->
<tr>
	<td colspan="2">
		<input type="reset" class="reset" name="cancel" value="#caller.stText.Buttons.Cancel#">
		<input type="submit" class="submit" name="run" value="#caller.stText.Buttons.Create#">
	</td>
</tr>
</cfform>

</table>
</cfloop>
</cfoutput>