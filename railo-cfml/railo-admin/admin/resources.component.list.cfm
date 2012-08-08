
<!--- 
Create Datasource --->
<cfoutput><table class="tbl" width="100%">
<tr>
	<td colspan="2">
<cfif request.adminType EQ "server">
	#stText.Components.Server#
<cfelse>
	#stText.Components.Web#
</cfif>
	</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">

<!--- Base Component ---->
<tr>
	<td class="tblHead" width="150">#stText.Components.BaseComponent#</td>
	<cfset css=iif(len(component.baseComponentTemplate) EQ 0 and len(component.strBaseComponentTemplate) NEQ 0,de('Red'),de(''))>
	<td class="tblContent#css#" title="#component.strBaseComponentTemplate#
#component.BaseComponentTemplate#">
		<span class="comment">#stText.Components.BaseComponentDescription#</span><br>
		<cfif hasAccess>
		<cfinput type="text" name="baseComponentTemplate" value="#component.strBaseComponentTemplate#" style="width:350px" 
			required="no" 
			message="#stText.Components.BaseComponentMissing#">
		<cfelse>
			<br><b>#component.strbaseComponentTemplate#</b>
		</cfif>
	</td>
</tr>

<!--- Auto Import ---->
<tr>
	<td class="tblHead" width="150">#stText.Components.AutoImport#</td>
	<td class="tblContent">
		<span class="comment">#stText.Components.AutoImportDescription#</span><br>
		<cfif hasAccess>
		<cfinput type="text" name="componentDefaultImport" value="#component.componentDefaultImport#" style="width:350px" 
			required="no" 
			message="#stText.Components.AutoImportMissing#">
		<cfelse>
			<br><b>#component.componentDefaultImport#</b>
		</cfif>
	</td>
</tr>

<!--- Search Local ---->
<tr>
	<td class="tblHead" width="150">#stText.Components.componentLocalSearch#</td>
	<td class="tblContent">
		<cfif hasAccess>
			<input type="checkbox" class="checkbox" name="componentLocalSearch" value="yes" <cfif component.componentLocalSearch>checked</cfif>>
			
		<cfelse>
			<b>#YesNoFormat(component.componentLocalSearch)#</b><br />
		</cfif><span class="comment">#stText.Components.componentLocalSearchDesc#</span>
	</td>
</tr>

<!--- Search Mappings ---->
<tr>
	<td class="tblHead" width="150">#stText.Components.componentMappingSearch#</td>
	<td class="tblContent">
		
		<b>Yes (coming soon)</b><br />
		<span class="comment">#stText.Components.componentMappingSearchDesc#</span>
	</td>
</tr>

<!--- Deep Search ---->
<tr>
	<td class="tblHead" width="150">#stText.Components.componentDeepSearch#</td>
	<td class="tblContent" colspan="4">
	<cfif hasAccess>
    	<input type="checkbox" class="checkbox" name="componentDeepSearchDesc" value="yes" <cfif component.deepsearch>checked</cfif>>
    <cfelse>
    	<b>#yesNoFormat(setting.deepsearch)#</b>
	</cfif>
    
    <span class="comment">#stText.Components.componentDeepSearchDesc#</span></td>
</tr>


<!--- component path cache ---->
<tr>
	<td class="tblHead" width="150">#stText.Components.componentPathCache#</td>
	<td class="tblContent">
		<cfif hasAccess>
			<input type="checkbox" class="checkbox" name="componentPathCache" value="yes" <cfif component.componentPathCache>checked</cfif>>
            <span class="comment">#stText.Components.componentPathCacheDesc#</span>
            <cfif component.componentPathCache><br />
            <input type="submit" class="submit" name="mainAction" value="#flushName#">
            </cfif>
            
		<cfelse>
			<b>#YesNoFormat(component.componentPathCache)#</b><br />
            <span class="comment">#stText.Components.componentPathCacheDesc#</span>
		</cfif>
		
	</td>
</tr>






<!--- Component Dump Template ---->
<tr>
	<td class="tblHead" width="150">#stText.Components.ComponentDumpTemplate#</td>
	<cfset css=iif(len(component.componentDumpTemplate) EQ 0 and len(component.strComponentDumpTemplate) NEQ 0,de('Red'),de(''))>
	<td class="tblContent#css#" title="#component.strcomponentDumpTemplate#
#component.componentDumpTemplate#">
		<span class="comment">#stText.Components.ComponentDumpTemplateDescription#</span><br>
		<cfif hasAccess>
			<cfinput type="text" name="componentDumpTemplate" value="#component.strcomponentDumpTemplate#" style="width:350px" 
				required="no" 
				message="#stText.Components.ComponentDumpTemplateMissing#">
		<cfelse>
			<br><b>#component.strcomponentDumpTemplate#</b>
		</cfif>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Components.DataMemberAccessType#</td>
	<td class="tblContent">
		<cfset access=component.componentDataMemberDefaultAccess>  
		<span class="comment">#stText.Components.DataMemberAccessTypeDescription#</span><br>
      	<cfif hasAccess>
		<select name="componentDataMemberDefaultAccess" style="width:200px">
				<option value="private" <cfif access EQ "private">selected</cfif>>#stText.Components.DMATPrivate#</option>
				<option value="package" <cfif access EQ "package">selected</cfif>>#stText.Components.DMATPackage#</option>
				<option value="public" <cfif access EQ "public">selected</cfif>>#stText.Components.DMATPublic#</option>
				<option value="remote" <cfif access EQ "remote">selected</cfif>>#stText.Components.DMATRemote#</option>
		</select>
		<cfelse>
		<br><b>#access#</b>
		</cfif>
	</td>
</tr>
<!---
Trigger Data Member --->
<tr>
	<td class="tblHead" width="150">#stText.Components.triggerDataMember#</td>
	<td class="tblContent">
		<cfif hasAccess>
		<input class="checkbox" type="checkbox" class="checkbox" name="triggerDataMember" 
			value="yes" <cfif component.triggerDataMember>checked</cfif>>
		<cfelse>
		<br><b>#iif(component.triggerDataMember,de('Yes'),de('No'))#</b>
		</cfif>
		<span class="comment">#stText.Components.triggerDataMemberDescription#</span><br>
      	
	</td>
</tr>
<!---
Use Shadow --->
<tr>
	<td class="tblHead" width="150">#stText.Components.useShadow#</td>
	<td class="tblContent">
		<cfif hasAccess>
		<input class="checkbox" type="checkbox" class="checkbox" name="useShadow" 
			value="yes" <cfif component.useShadow>checked</cfif>>
		<cfelse>
		<br><b>#iif(component.useShadow,de('Yes'),de('No'))#</b>
		</cfif>
		<span class="comment">#stText.Components.useShadowDescription#</span><br>
      	
	</td>
</tr>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">

<tr>
	<td colspan="2">
		<input class="submit" type="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfif>
</cfform></cfoutput>
</table>
<br><br>





<cfoutput>
<table class="tbl" width="740">
<tr>
	<td colspan="5"><h2>#stText.Components.componentMappings#</h2>
#stText.Components.componentMappingsDesc#</td>
</tr>

<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
	<tr>
		<td><cfif hasAccess><input type="checkbox" class="checkbox" 
			name="rro" onclick="selectAll(this)"></cfif></td>
		<td><cfmodule template="tp.cfm"  width="17" height="1"></td>
		<td class="tblHead" nowrap>#stText.Components.Physical#</td>
		<td class="tblHead" nowrap>#stText.Components.Archive#</td>
		<td class="tblHead" nowrap>#stText.Components.Primary#</td>
		<td class="tblHead" nowrap>#stText.Mappings.TrustedHead#</td>
	</tr>
	<cfset count=0>

<cfloop query="mappings">
		<!--- and now display --->
	<tr>
    
    	<!--- checkbox --->
		<td height="28">
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><cfif not mappings.ReadOnly><cfset count=count+1>
			<input type="hidden" name="virtual_#mappings.currentrow#" value="#mappings.virtual#"><input type="checkbox" class="checkbox" 
			name="row_#mappings.currentrow#" value="#mappings.currentrow#">
			</cfif></td>
		</tr>
		</table>
		</td>
        
		<!--- edit --->
		<td><cfif not mappings.readOnly><a href="#request.self#?action=#url.action#&action2=create&virtual=#mappings.virtual#">
		<cfmodule template="img.cfm" src="edit.png" border="0"></a></cfif></td>
        
        
        
        <!--- physical --->
		<cfset css=iif(len(mappings.physical) EQ 0 and len(mappings.strPhysical) NEQ 0,de('Red'),de(''))>
		<td class="tblContent#css#" title="#mappings.strphysical#
#mappings.physical#" nowrap><cfif mappings.ReadOnly>#cut(mappings.strphysical,40)#<cfelse><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="physical_#mappings.currentrow#" value="#mappings.strphysical#" required="no"  
			style="width:270px" 
			message="#stText.Components.PhysicalMissing##mappings.currentrow#)"></cfif></td>
		
        <!--- achive --->
		<cfset css=iif(len(mappings.archive) EQ 0 and len(mappings.strArchive) NEQ 0,de('Red'),de(''))>
		<td class="tblContent#css#" title="#mappings.strarchive#
#mappings.archive#" nowrap><cfif mappings.ReadOnly>#cut(mappings.strarchive,40)#<cfelse><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="archive_#mappings.currentrow#" value="#mappings.strarchive#" required="no"  
			style="width:270px" 
			message="#stText.Components.ArchiveMissing##mappings.currentrow#)"></cfif></td>
		
        <!--- primary --->
		<td class="tblContent" nowrap><cfif mappings.ReadOnly><cfif mappings.physicalFirst>physical<cfelse>archive</cfif><cfelse><select name="primary_#mappings.currentrow#" onChange="checkTheBox(this)">
			<option value="physical" <cfif mappings.physicalFirst>selected</cfif>>#stText.Components.physical#</option>
			<option value="archive" <cfif not mappings.physicalFirst>selected</cfif>>#stText.Components.archive#</option>
		</select></cfif></td>
		
        <!--- inspect --->
		<td class="tblContent" nowrap>
		<cfif mappings.readOnly>
            	#mappings.Trusted?stText.setting.inspecttemplateneverShort:stText.setting.inspecttemplatealwaysShort#
			<cfelse>
            <select name="trusted_#mappings.currentrow#" onChange="checkTheBox(this)">
                <option value="true" <cfif mappings.Trusted>selected</cfif>>#stText.setting.inspecttemplateneverShort#</option>
                <option value="false" <cfif not mappings.Trusted>selected</cfif>>#stText.setting.inspecttemplatealwaysShort#</option>
            </select>
            </cfif>
		
		</td>
	</tr>
</cfloop>
<cfif hasAccess>
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><input type="checkbox" class="checkbox" name="row_#mappings.recordcount+1#" value="#mappings.recordcount+1#">
			<input type="hidden" name="virtual_#mappings.recordcount+1#" value="/#mappings.recordcount+1#"></td>
		</tr>
		</table>
		</td>
		<td><cfmodule template="tp.cfm"  width="17" height="1"></td>
		<td class="tblContent" nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="physical_#mappings.recordcount+1#" value="" required="no"  style="width:270px"></td>
		<td class="tblContent" nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="archive_#mappings.recordcount+1#" value="" required="no"  style="width:270px" ></td>
		<td class="tblContent" nowrap><select name="primary_#mappings.recordcount+1#" onChange="checkTheBox(this)">
			<option value="physical" selected>#stText.Components.physical#</option>
			<option value="archive">#stText.Components.archive#</option>
		</select></td>
		<td class="tblContent" nowrap>
        <select name="trusted_#mappings.recordcount+1#" onChange="checkTheBox(this)">
                <option value="true">#stText.setting.inspecttemplateneverShort#</option>
                <option value="false" selected>#stText.setting.inspecttemplatealwaysShort#</option>
            </select></td>
	</tr>
</cfif>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="8" line>
	<tr>
		<td colspan="8">
		 <table border="0" cellpadding="0" cellspacing="0">
		 <tr>
			<td><cfmodule template="tp.cfm"  width="10" height="1"></td>		
			<td><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="10"></td>
			<td></td>
		 </tr>
		 <tr>
			<td></td>
			<td valign="top"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="14"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="36" height="1"></td>
			<td>&nbsp;
			<input type="hidden" name="mainAction" value="#stText.Buttons.Update#">
			<input type="submit" class="submit" name="subAction" value="#stText.Buttons.Update#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			<input type="submit" class="submit" name="subAction" value="#stText.Buttons.Delete#">
			</td>	
		</tr>
		 </table>
		 </td>
	</tr>
</cfif>
</cfform>
</cfoutput>
</table>

