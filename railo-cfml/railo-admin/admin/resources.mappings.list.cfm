

<!--- 
list all mappings and display necessary edit fields --->
<script>
function checkTheBox(field) {
	var apendix=field.name.split('_')[1];
	var box=field.form['row_'+apendix];
	box.checked=true;
}
</script>
<cfoutput>

<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>

#stText.Mappings.IntroText#
<table class="tbl" width="100%" border="0">
 	<colgroup>
        <col width="10">
        <col width="10">
    </colgroup>
<tr>
	<td colspan="7"><cfmodule template="tp.cfm" width="1" height="1"></td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
	<tr>
		<td><cfif hasAccess><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"></cfif><cfmodule template="tp.cfm"  width="10" height="1"></td>
		<td><cfmodule template="tp.cfm"  width="17" height="1"></td>
		<td class="tblHead" nowrap>#stText.Mappings.VirtualHead#</td>
		<td class="tblHead" nowrap>#stText.Mappings.PhysicalHead#</td>
		<td class="tblHead" nowrap>#stText.Mappings.ArchiveHead#</td>
		<td class="tblHead" nowrap>#stText.Mappings.PrimaryHead#</td>
		<td class="tblHead" nowrap>#stText.Mappings.TrustedHead#</td>
	</tr>
	<cfloop query="mappings">
		<cfif not mappings.hidden>
		<!--- and now display --->
		<input type="hidden" name="stopOnError_#mappings.currentrow#" value="yes">
	<tr>
		<!--- checkbox ---->
		<td><table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><cfif not mappings.readOnly><input type="checkbox" class="checkbox" name="row_#mappings.currentrow#" value="#mappings.currentrow#"></cfif></td>
		</tr>
		</table></td>
		
		<!--- edit --->
		<td><cfif not mappings.readOnly><a href="#request.self#?action=#url.action#&action2=create&virtual=#mappings.virtual#">
		<img src="resources/img/edit.png.cfm" border="0"></a></cfif></td>
		
		
		
		
		
		<!--- virtual --->
		<td height="30" title="#mappings.virtual#" nowrap><input type="hidden" 
			name="virtual_#mappings.currentrow#" value="#mappings.virtual#">#cut(mappings.virtual,14)#</td>
		
		<!--- physical --->
		<cfset css=iif(len(mappings.physical) EQ 0 and len(mappings.strPhysical) NEQ 0,de('Red'),de(''))>
		<td class="tblContent#css#" nowrap <cfif len(mappings.strPhysical)>title="#mappings.Physical#"</cfif>><cfif mappings.readOnly>#cut(mappings.strPhysical,36)#<cfelse><cfinput  onKeyDown="checkTheBox(this)" type="text" 
			name="physical_#mappings.currentrow#" value="#mappings.strPhysical#" required="no"  
			style="width:100%" message="#stText.Mappings.PhysicalMissing##mappings.currentrow#)"></cfif></td>
		
		
		<!--- archive --->
		<cfset css=iif(len(mappings.archive) EQ 0 and len(mappings.strArchive) NEQ 0,de('Red'),de(''))>
		<td class="tblContent#css#" nowrap <cfif len(mappings.strArchive)>title="#mappings.Archive#"</cfif>><cfif mappings.readOnly>#cut(mappings.strArchive,36)#<cfelse><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="archive_#mappings.currentrow#" value="#mappings.strArchive#" required="no"  
			style="width:100%" message="#stText.Mappings.ArchiveMissing##mappings.currentrow#)"></cfif></td>
		
		<!--- primary --->
		<td nowrap><cfif mappings.readOnly><cfif mappings.PhysicalFirst>physical<cfelse>archive</cfif><cfelse><select name="primary_#mappings.currentrow#" onchange="checkTheBox(this)">
			<option value="physical" <cfif mappings.PhysicalFirst>selected</cfif>>#stText.Mappings.Physical#</option>
			<option value="archive" <cfif not mappings.PhysicalFirst>selected</cfif>>#stText.Mappings.Archive#</option>
		</select></cfif></td>
		
		<!--- trusted --->
		<td nowrap>
			<cfif mappings.readOnly>
            	#mappings.Trusted?stText.setting.inspecttemplateneverShort:stText.setting.inspecttemplatealwaysShort#
			<cfelse>
            <select name="trusted_#mappings.currentrow#" onchange="checkTheBox(this)">
                <option value="true" <cfif mappings.Trusted>selected</cfif>>#stText.setting.inspecttemplateneverShort#</option>
                <option value="false" <cfif not mappings.Trusted>selected</cfif>>#stText.setting.inspecttemplatealwaysShort#</option>
            </select>
            
            <!---<input 
		type="checkbox" class="checkbox" name="trusted_#mappings.currentrow#" onClick="checkTheBox(this)" 
		value="yes" <cfif mappings.Trusted>checked</cfif>>---></cfif>
		<input type="hidden" name="toplevel_#mappings.currentrow#" value="#mappings.toplevel#">
		</td>
	</tr>
	</cfif>
</cfloop>
<cfif hasAccess>
	<tr>
		<td><table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><input type="checkbox" class="checkbox" name="row_#mappings.recordcount+1#" value="#mappings.recordcount+1#"></td>
		</tr>
		</table></td>
		
		<td><cfmodule template="tp.cfm"  width="17" height="1"></td>
		<td nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="virtual_#mappings.recordcount+1#" value="" required="no" style="width:100%"></td>
		<td nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="physical_#mappings.recordcount+1#" value="" required="no"  style="width:100%"></td>
		<td nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="archive_#mappings.recordcount+1#" value="" required="no"  style="width:100%" ></td>
		<td nowrap><select name="primary_#mappings.recordcount+1#" onchange="checkTheBox(this)">
			<option value="physical" selected>#stText.Mappings.Physical#</option>
			<option value="archive">#stText.Mappings.Archive#</option>
		</select></td>
		<td nowrap>
        
         <select name="trusted_#mappings.recordcount+1#" onchange="checkTheBox(this)">
                <option value="true">#stText.setting.inspecttemplateneverShort#</option>
                <option value="false" selected>#stText.setting.inspecttemplatealwaysShort#</option>
            </select>
        
        
		<input type="hidden" name="toplevel_#mappings.recordcount+1#" value="yes">
		</td>
	</tr>
</cfif>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="8" line=true>
	<tr>
		<td colspan="8">
		 <table border="0" cellpadding="0" cellspacing="0">
		 <tr>
			<td><cfmodule template="tp.cfm"  width="8" height="1"></td>		
			<td><img src="resources/img/#ad#-bgcolor.gif.cfm" width="1" height="10"></td>
			<td></td>
		 </tr>
		 <tr>
			<td></td>
			<td valign="top"><img src="resources/img/#ad#-bgcolor.gif.cfm" width="1" height="14"><img src="resources/img/#ad#-bgcolor.gif.cfm" width="54" height="1"></td>
			<td>&nbsp;
			<input type="hidden" name="mainAction" value="#stText.Buttons.save#">
			<input type="submit" class="submit" name="subAction" value="#stText.Buttons.save#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			<input type="submit" class="submit" name="subAction" value="#stText.Buttons.Delete#">
			<input type="submit" class="submit" name="subAction" value="#stText.Buttons.compileAll#">
			</td>	
		</tr>
		 </table>
		 </td>
	</tr>
</cfif>
</cfform>
</cfoutput>
</table>