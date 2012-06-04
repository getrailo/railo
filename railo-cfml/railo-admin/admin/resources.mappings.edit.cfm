
<cfsilent>
	<cfset mapping=struct()>
	<cfloop query="mappings">
		<cfif mappings.virtual EQ url.virtual>
			<cfloop index="key" list="#mappings.columnlist#">
				<cfset mapping[key]=mappings[key]>
			</cfloop>
			<cfset mapping.id=mappings.currentrow>
		</cfif>
	</cfloop>
</cfsilent>

<cfoutput>
<table class="tbl" width="100%">
<tr>
	<td colspan="2">#stText.Mappings.editDesc#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform onerror="customError" action="#request.self#?virtual=#mapping.virtual#&action=#url.action#&action2=#url.action2#" method="post">
<input type="hidden" name="mainAction" value="#stText.Buttons.save#">
<input type="hidden"  name="row_#mapping.id#" value="#mapping.id#">

<tr>
	<th scope="row">#stText.Mappings.VirtualHead#</th>
	<td title="#mapping.virtual#" nowrap><input type="hidden" 
			name="virtual_#mapping.id#" value="#mapping.virtual#">#mapping.virtual#</td>
</tr>
<tr>
	<th scope="row">#stText.Mappings.PhysicalHead#</th>
	<cfset css=iif(len(mapping.physical) EQ 0 and len(mapping.strPhysical) NEQ 0,de('Red'),de(''))>
	<td class="tblContent#css#" nowrap <cfif len(mapping.strPhysical)>title="#mapping.strPhysical##newLine()##mapping.Physical#"</cfif>><cfif mapping.readOnly>#cut(mapping.strPhysical,72)#<cfelse><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="physical_#mapping.id#" value="#mapping.strPhysical#" required="no"  
			style="width:100%" message="#stText.Mappings.PhysicalMissing##mapping.id#)"></cfif></td>
</tr>
<tr>
	<th scope="row">#stText.Mappings.ArchiveHead#</th>
	<cfset css=iif(len(mapping.archive) EQ 0 and len(mapping.strArchive) NEQ 0,de('Red'),de(''))>
	<td class="tblContent#css#" nowrap <cfif len(mapping.strArchive)>title="#mapping.strArchive##newLine()##mapping.Archive#"</cfif>><cfif mapping.readOnly>#cut(mappings.strArchive,72)#<cfelse><cfinput onKeyDown="checkTheBox(this)" type="text" 
		name="archive_#mapping.id#" value="#mapping.strArchive#" required="no"  
		style="width:100%" message="#stText.Mappings.ArchiveMissing##mapping.id#)"></cfif></td>
</tr>
<tr>
	<th scope="row">#stText.Mappings.PrimaryHead#</th>
	<td nowrap><cfif mapping.readOnly><cfif mapping.PhysicalFirst>physical<cfelse>archive</cfif><cfelse><select name="primary_#mapping.id#" onchange="checkTheBox(this)">
		<option value="physical" <cfif mapping.PhysicalFirst>selected</cfif>>#stText.Mappings.Physical#</option>
		<option value="archive" <cfif not mapping.PhysicalFirst>selected</cfif>>#stText.Mappings.Archive#</option>
	</select></cfif></td>
</tr>
<tr>
	<th scope="row">#stText.setting.inspecttemplate#</th>
	<td><cfif mapping.readOnly>
    	<cfif mapping.Trusted>
        	#stText.setting.inspecttemplatenever#
            <br /><div class="comment">#stText.setting.inspecttemplateneverdesc#</div>
            
        <cfelse>
        	#stText.setting.inspecttemplatealways#
            <br /><div class="comment">#stText.setting.inspecttemplatealwaysdesc#</div>
        </cfif>
    	
	
	<cfelse>
    	<!--- never --->
    	<input class="radio" type="radio" name="trusted_#mapping.id#" value="true"<cfif mapping.Trusted> checked="checked"</cfif>>
    	<b>#stText.setting.inspectTemplateNever#</b><br />
		<div class="comment">#stText.setting.inspectTemplateNeverDesc#</div><br>
    	<!--- always --->
    	<input class="radio" type="radio" name="trusted_#mapping.id#" value="false"<cfif not mapping.Trusted> checked="checked"</cfif>>
    	<b>#stText.setting.inspectTemplateAlways#</b><br />
		<div class="comment">#stText.setting.inspectTemplateAlwaysDesc#</div>
    </cfif></td>
</tr>

<tr>
	<th scope="row">#stText.Mappings.ToplevelHead#</th>
	<td nowrap><cfif mapping.readOnly>#iif(mapping.toplevel,de("Yes"),de("No"))#<cfelse><input 
	type="checkbox" class="checkbox" name="toplevel_#mapping.id#" 
	value="yes" <cfif mapping.toplevel>checked</cfif>></cfif>
    <br /><div class="comment">#stText.Mappings.ToplevelDesc#</div></td>
</tr>

<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="button submit" name="subAction" value="#stText.Buttons.save#">
		<input onclick="window.location='#request.self#?action=#url.action#';" type="button" class="button" name="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>
</cfif>


</table>
<br />
<h2>#stText.Mappings.compileTitle#</h2>
#stText.Mappings.compileDesc#

<table class="tbl" width="600">
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>


<tr>
	<th scope="row">#stText.Mappings.compileStopOnError#</th>
	<td nowrap><input 
	type="checkbox" class="checkbox" name="stopOnError_#mapping.id#" value="yes" checked="checked"> <div class="comment">#stText.Mappings.compileStopOnErrorDesc#</div></td>
</tr>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="button submit" name="subAction" value="#stText.Buttons.compileAll#">
		<input onclick="window.location='#request.self#?action=#url.action#';" type="button" class="button" name="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>
</cfif>
<!---



Create Archive --->
</table>
<br />
<h2>#stText.Mappings.archiveTitle#</h2>
#stText.Mappings.archiveDesc#

<table class="tbl" width="600">
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<tr>
	<th scope="row">#stText.Mappings.archiveSecure#</th>
	<td nowrap><input 
	type="checkbox" class="checkbox" name="secure_#mapping.id#" value="yes" checked> <div class="comment">#stText.Mappings.archiveSecureDesc#</div></td>
</tr>

<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2" attention="#stText.remote.downloadArchive#">
<tr>
	<td colspan="2">
		<input type="submit" class="button submit" name="subAction" value="#stText.Buttons.downloadArchive#">
		<input type="submit" class="button submit" name="subAction" value="#stText.Buttons.addArchive#">
		<input onclick="window.location='#request.self#?action=#url.action#';" type="button" class="button" name="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>
</cfif>



</cfform>





</table>




</cfoutput>