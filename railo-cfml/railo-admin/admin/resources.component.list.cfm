<cfoutput>
	<div class="pageintro">
		<cfif request.adminType EQ "server">
			#stText.Components.Server#
		<cfelse>
			#stText.Components.Web#
		</cfif>
	</div>
	
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl">
			<tbody>
				<!--- Base Component ---->
				<tr>
					<th scope="row">#stText.Components.BaseComponent#</th>
					<cfset css=iif(len(component.baseComponentTemplate) EQ 0 and len(component.strBaseComponentTemplate) NEQ 0,de('Red'),de(''))>
					<td class="tblContent#css#" title="#component.strBaseComponentTemplate#
#component.BaseComponentTemplate#">
						<cfif hasAccess>
							<cfinput type="text" name="baseComponentTemplate" value="#component.strBaseComponentTemplate#" style="width:350px" 
								required="no" 
								message="#stText.Components.BaseComponentMissing#">
						<cfelse>
							<b>#component.strbaseComponentTemplate#</b>
						</cfif>
						<div class="comment">#stText.Components.BaseComponentDescription#</div>
					</td>
				</tr>
				<!--- Auto Import ---->
				<tr>
					<th scope="row">#stText.Components.AutoImport#</th>
					<td>
						<cfif hasAccess>
							<cfinput type="text" name="componentDefaultImport" value="#component.componentDefaultImport#" style="width:350px" 
								required="no" 
								message="#stText.Components.AutoImportMissing#">
						<cfelse>
							<b>#component.componentDefaultImport#</b>
						</cfif>
						<div class="comment">#stText.Components.AutoImportDescription#</div>
					</td>
				</tr>
				<!--- Search Local ---->
				<tr>
					<th scope="row">#stText.Components.componentLocalSearch#</th>
					<td>
						<cfif hasAccess>
							<input type="checkbox" class="checkbox" name="componentLocalSearch" value="yes" <cfif component.componentLocalSearch>checked</cfif>>
						<cfelse>
							<b>#YesNoFormat(component.componentLocalSearch)#</b>
						</cfif>
						<div class="comment">#stText.Components.componentLocalSearchDesc#</div>
					</td>
				</tr>
				<!--- Search Mappings ---->
				<tr>
					<th scope="row">#stText.Components.componentMappingSearch#</th>
					<td>
						<b>Yes (coming soon)</b>
						<div class="comment">#stText.Components.componentMappingSearchDesc#</div>
					</td>
				</tr>
				<!--- Deep Search ---->
				<tr>
					<th scope="row">#stText.Components.componentDeepSearch#</th>
					<td>
						<cfif hasAccess>
							<input type="checkbox" class="checkbox" name="componentDeepSearchDesc" value="yes" <cfif component.deepsearch>checked</cfif>>
						<cfelse>
							<b>#yesNoFormat(setting.deepsearch)#</b>
						</cfif>
						<div class="comment">#stText.Components.componentDeepSearchDesc#</div>
					</td>
				</tr>
				<!--- component path cache ---->
				<tr>
					<th scope="row">#stText.Components.componentPathCache#</th>
					<td>
						<cfif hasAccess>
							<input type="checkbox" class="checkbox" name="componentPathCache" value="yes" <cfif component.componentPathCache>checked</cfif>>
							<div class="comment">#stText.Components.componentPathCacheDesc#</div>
							<cfif component.componentPathCache>
								<input type="submit" class="button submit" name="mainAction" value="#flushName#">
							</cfif>
						<cfelse>
							<b>#YesNoFormat(component.componentPathCache)#</b>
							<div class="comment">#stText.Components.componentPathCacheDesc#</div>
						</cfif>
					</td>
				</tr>
				<!--- Component Dump Template ---->
				<tr>
					<th scope="row">#stText.Components.ComponentDumpTemplate#</th>
					<cfset css=iif(len(component.componentDumpTemplate) EQ 0 and len(component.strComponentDumpTemplate) NEQ 0,de('Red'),de(''))>
					<td class="tblContent#css#" title="#component.strcomponentDumpTemplate#
#component.componentDumpTemplate#">
						<cfif hasAccess>
							<cfinput type="text" name="componentDumpTemplate" value="#component.strcomponentDumpTemplate#" class="large"
								required="no" 
								message="#stText.Components.ComponentDumpTemplateMissing#">
						<cfelse>
							<b>#component.strcomponentDumpTemplate#</b>
						</cfif>
						<div class="comment">#stText.Components.ComponentDumpTemplateDescription#</div>
					</td>
				</tr>
				<tr>
					<th scope="row">#stText.Components.DataMemberAccessType#</th>
					<td>
						<cfset access=component.componentDataMemberDefaultAccess>
						<cfif hasAccess>
							<select name="componentDataMemberDefaultAccess" class="medium">
								<option value="private" <cfif access EQ "private">selected</cfif>>#stText.Components.DMATPrivate#</option>
								<option value="package" <cfif access EQ "package">selected</cfif>>#stText.Components.DMATPackage#</option>
								<option value="public" <cfif access EQ "public">selected</cfif>>#stText.Components.DMATPublic#</option>
								<option value="remote" <cfif access EQ "remote">selected</cfif>>#stText.Components.DMATRemote#</option>
							</select>
						<cfelse>
							<b>#access#</b>
						</cfif>
						<div class="comment">#stText.Components.DataMemberAccessTypeDescription#</div>
					</td>
				</tr>
				<!---
				Trigger Data Member --->
				<tr>
					<th scope="row">#stText.Components.triggerDataMember#</th>
					<td>
						<cfif hasAccess>
							<input class="checkbox" type="checkbox" class="checkbox" name="triggerDataMember" 
							value="yes" <cfif component.triggerDataMember>checked</cfif>>
						<cfelse>
							<b>#iif(component.triggerDataMember,de('Yes'),de('No'))#</b>
						</cfif>
						<div class="comment">#stText.Components.triggerDataMemberDescription#</div>
					</td>
				</tr>
				<!---
				Use Shadow --->
				<tr>
					<th scope="row">#stText.Components.useShadow#</th>
					<td>
						<cfif hasAccess>
							<input class="checkbox" type="checkbox" class="checkbox" name="useShadow" 
							value="yes" <cfif component.useShadow>checked</cfif>>
						<cfelse>
							<b>#iif(component.useShadow,de('Yes'),de('No'))#</b>
						</cfif>
						<div class="comment">#stText.Components.useShadowDescription#</div>
					</td>
				</tr>
				<cfif hasAccess>
					<cfmodule template="remoteclients.cfm" colspan="2">
				</cfif>
			</tbody>
			<cfif hasAccess>
				<tfoot>
					<tr>
						<td colspan="2">
							<input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.Update#">
							<input type="reset" class="reset" name="cancel" value="#stText.Buttons.cancel#">
							<cfif request.adminType EQ "web"><input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
						</td>
					</tr>
				</tfoot>
			</cfif>
		</table>
	</cfform>
	
	
	<h2>#stText.Components.componentMappings#</h2>
	<div class="itemintro">#stText.Components.componentMappingsDesc#</div>

	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl checkboxtbl">
			<thead>
				<tr>
					<th>
						<cfif hasAccess>
							<input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)">
						</cfif>
					</th>
					<th>#stText.Components.Physical#</th>
					<th>#stText.Components.Archive#</th>
					<th>#stText.Components.Primary#</th>
					<th>#stText.Mappings.TrustedHead#</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<cfset count=0>
				<cfloop query="mappings">
					<tr>
						<td>
							<cfif not mappings.ReadOnly>
								<cfset count=count+1>
								<input type="hidden" name="virtual_#mappings.currentrow#" value="#mappings.virtual#"><input type="checkbox" class="checkbox" 
								name="row_#mappings.currentrow#" value="#mappings.currentrow#">
							</cfif>
						</td>

						<cfset css=iif(len(mappings.physical) EQ 0 and len(mappings.strPhysical) NEQ 0,de('Red'),de(''))>
						<td class="tblContent#css# longwords">
							<cfif mappings.ReadOnly>
								#mappings.strphysical#
							<cfelse>
								<cfinput onKeyDown="checkTheBox(this)" type="text"
								name="physical_#mappings.currentrow#" value="#mappings.strphysical#" required="no"
								style="width:270px"
								message="#stText.Components.PhysicalMissing##mappings.currentrow#)">
							</cfif>
						</td>
						
						<cfset css=iif(len(mappings.archive) EQ 0 and len(mappings.strArchive) NEQ 0,de('Red'),de(''))>
						<td class="tblContent#css# longwords">
							<cfif mappings.ReadOnly>
								#mappings.strarchive#
							<cfelse>
								<cfinput onKeyDown="checkTheBox(this)" type="text"
								name="archive_#mappings.currentrow#" value="#mappings.strarchive#" required="no"
								style="width:270px"
								message="#stText.Components.ArchiveMissing##mappings.currentrow#)">
							</cfif>
						</td>
						
						<td nowrap><cfif mappings.ReadOnly>
							<cfif mappings.PhysicalFirst>
									#stText.Mappings.Physical#
								<cfelse>
									#stText.Mappings.Archive#
								</cfif>
							<cfelse><select name="primary_#mappings.currentrow#" onchange="checkTheBox(this)">
							<option value="physical" <cfif mappings.physicalFirst>selected</cfif>>#stText.Components.physical#</option>
							<option value="archive" <cfif not mappings.physicalFirst>selected</cfif>>#stText.Components.archive#</option>
						</select></cfif></td>
						
						<td nowrap>
						<cfif mappings.readOnly>
								#mappings.Trusted?stText.setting.inspecttemplateneverShort:stText.setting.inspecttemplatealwaysShort#
							<cfelse>
							<select name="trusted_#mappings.currentrow#" onchange="checkTheBox(this)">
								<option value="true" <cfif mappings.Trusted>selected</cfif>>#stText.setting.inspecttemplateneverShort#</option>
								<option value="false" <cfif not mappings.Trusted>selected</cfif>>#stText.setting.inspecttemplatealwaysShort#</option>
							</select>
							</cfif>
						
						</td>
						<!--- edit --->
						<td>
							<cfif not mappings.readOnly>
								<a href="#request.self#?action=#url.action#&action2=create&virtual=#mappings.virtual#" class="btn-mini edit"><span>edit</span></a>
							</cfif>
						</td>
					</tr>
				</cfloop>
				<cfif hasAccess>
					<cfmodule template="remoteclients.cfm" colspan="8" line>
				</cfif>
			</tbody>
			<tfoot>
				<cfif hasAccess>
					<tr>
						<td colspan="6">
							<input type="hidden" name="mainAction" value="#stText.Buttons.Update#">
							<input type="submit" class="button submit" name="subAction" value="#stText.Buttons.Update#">
							<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
							<input type="submit" class="button submit" name="subAction" value="#stText.Buttons.Delete#">
						</td>	
					</tr>
				</cfif>
			</tfoot>
		</table>
	</cfform>

	<cfif hasAccess>
		<h2>#stText.components.createnewcompmapping#</h2>
		<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
			<table class="maintbl">
				<tbody>
					<tr>
						<th scope="row">#stText.Components.Physical#</th>
						<td>
							<input type="hidden" name="row_1" value="1">
							<input type="hidden" name="virtual_1" value="/#mappings.recordcount+1#">
							<cfinput type="text" name="physical_1" value="" required="no" class="large">
						</td>
					</tr>
					<tr>
						<th scope="row">#stText.Components.Archive#</th>
						<td>
							<cfinput type="text" name="archive_1" value="" required="no" class="large">
						</td>
					</tr>
					<tr>
						<th scope="row">#stText.Components.Primary#</th>
						<td>
							<select name="primary_1" class="medium">
								<option value="physical" selected>#stText.Components.physical#</option>
								<option value="archive">#stText.Components.archive#</option>
							</select>
						</td>
					</tr>
					<tr>
						<th scope="row">#stText.Mappings.TrustedHead#</th>
						<td>
							<select name="trusted_1" class="medium">
								<option value="true">#stText.setting.inspecttemplateneverShort#</option>
								<option value="false" selected>#stText.setting.inspecttemplatealwaysShort#</option>
							</select>
						</td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2">
							<input type="hidden" name="mainAction" value="#stText.Buttons.update#">
							<input type="hidden" name="subAction" value="#stText.Buttons.update#">
							<input type="submit" class="button submit" name="sdasd" value="#stText.Buttons.save#" />
						</td>
					</tr>
				</tfoot>
			</table>
		</cfform>
	</cfif>
</cfoutput>