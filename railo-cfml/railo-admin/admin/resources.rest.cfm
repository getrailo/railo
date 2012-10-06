<cfset hasAccess=true>
<cfset newRecord="sd812jvjv23uif2u32d">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cfset error={message:"",detail:""}>


<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE/settings --->
		<cfcase value="#stText.Buttons.Update#">
            <cfadmin 
                action="updateRestSettings"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                remoteClients="#request.getRemoteClients()#"
                
                list="#structKeyExists(form,'list') and form.list#"
                >				
		</cfcase>
        <!--- reset/settings --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
            <cfadmin 
                action="updateRestSettings"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                remoteClients="#request.getRemoteClients()#"
                
                list=""
                >				
		</cfcase>
        <!--- save/mapping --->
		<cfcase value="#stText.Buttons.save#">
        	
			<cfset data.physicals=toArrayFromForm("physical")>
            <cfset data.virtuals=toArrayFromForm("virtual")>
            <cfset data.rows=toArrayFromForm("row")>
            
            
            <cfloop index="idx" from="1" to="#arrayLen(data.virtuals)#">
            	<cfset _default=StructKeyExists(form,'default') and form.default EQ idx>
				<cfif isDefined("data.rows[#idx#]") and data.virtuals[idx] NEQ "">aaa
                <cfadmin 
                    action="updateRestMapping"
                    type="#request.adminType#"
                    password="#session["password"&request.adminType]#"
                    
                    virtual="#data.virtuals[idx]#"
                    physical="#data.physicals[idx]#"
                    default="#_default#"
                    
        remoteClients="#request.getRemoteClients()#">
                </cfif>
            </cfloop>
		</cfcase>
        
        <!--- delete/mapping --->
		<cfcase value="#stText.Buttons.delete#">
        	
			<cfset data.virtuals=toArrayFromForm("virtual")>
            <cfset data.rows=toArrayFromForm("row")>
            
            <cfloop index="idx" from="1" to="#arrayLen(data.virtuals)#">
            	<cfset _default=StructKeyExists(form,'default') and form.default EQ idx>
				<cfif isDefined("data.rows[#idx#]") and data.virtuals[idx] NEQ "">aaa
                <cfadmin 
                    action="removeRestMapping"
                    type="#request.adminType#"
                    password="#session["password"&request.adminType]#"
                    
                    virtual="#data.virtuals[idx]#"
                    
        remoteClients="#request.getRemoteClients()#">
                </cfif>
            </cfloop>
		</cfcase>
        
        
	</cfswitch>
	<cfcatch><cfrethrow>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>

<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output --->
<cfset printError(error)>


<cfadmin 
	action="getRestMappings"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="rest">
<cfadmin 
	action="getRestSettings"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="settings">


<cfset stText.rest.setting="Settings">
<cfset stText.rest.list="List services">
<cfset stText.rest.listDesc="List Services when ""/rest/"" is called ">
<cfset stText.rest.changes="Allow Change Mappings">
<cfset stText.rest.changesDesc="Allow to add or remove Mappings in the Application with help of the function restInitApplication/restDeleteApplication.">
<cfset stText.rest.mapping="Mappings">
<cfset stText.rest.mappingDesc="Mappings ...">


<cfset stText.rest.desc="Rest is ...">
<cfset stText.rest.VirtualHead="Virtual">
<cfset stText.rest.PhysicalHead="Physical">
<cfset stText.rest.DefaultHead="Default">
<cfset stText.rest.PhysicalMissing="Please enter a value for the physical resource.">


<!--- 
list all mappings and display necessary edit fields --->
<script type="text/javascript">
	function changeDefault(field) {
		var form=field.form;
		for(var i=0;i<form.length;i++){
			if(form[i].name=='default') {
				$(form["row_"+form[i].value]).prop('checked', form[i].checked).triggerHandler('change');
				
				//alert(form[i].value+":"+form[i].checked);
				
				//row_#rest.currentrow#
			}
		}
	}
</script>
<cfoutput>
	<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>
	<div class="pageintro">#stText.rest.desc#</div>
	<!--- Settings --->
	<h2>#stText.rest.setting#</h2>
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl">
			<tbody>
				<tr>
					<th scope="row">#stText.rest.list#</th>
					<td>
						<cfif hasAccess NEQ 0><input type="checkbox" class="checkbox" name="list" value="yes" <cfif settings.list>checked</cfif>>
						<cfelse><b>#yesNoFormat(settings.list)#</b></cfif>
						<div class="comment">#stText.rest.listDesc#</div>
					</td>
				</tr>
				<cfif hasAccess NEQ 0>
					<cfmodule template="remoteclients.cfm" colspan="2">
				</cfif>
			</tbody>
			<cfif hasAccess NEQ 0>
				<tfoot>
					<tr>
						<td colspan="2">
							<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.Update#">
							<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
							<cfif request.adminType EQ "web"><input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
						</td>
					</tr>
				</tfoot>
			</cfif>
		</table>
	</cfform>

	<!--- Mappings --->
	<h2>#stText.rest.mapping#</h2>
	<div class="itemintro">#stText.rest.mappingDesc#</div>
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
		<table class="maintbl checkboxtbl">
			<thead>
				<tr>
					<th width="3%"><cfif hasAccess><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"></cfif></th>
					<th width="24%">#stText.rest.VirtualHead#</th>
					<th width="65%">#stText.rest.PhysicalHead#</th>
					<th width="5%">#stText.rest.DefaultHead#</th>
					<!---<th width="3%"></th>--->
				</tr>
			</thead>
			<tbody>
				<cfloop query="rest">
					<cfif not rest.hidden>
						<tr>
							<!--- checkbox ---->
							<td>
								<input type="hidden" name="stopOnError_#rest.currentrow#" value="yes">
								<cfif not rest.readOnly>
									<input type="checkbox" class="checkbox" name="row_#rest.currentrow#" value="#rest.currentrow#">
								</cfif>
							</td>
							<!--- virtual --->
							<td>
								<input type="hidden" name="virtual_#rest.currentrow#" value="#rest.virtual#">
								#rest.virtual#
							</td>
							<!--- physical --->
							<cfset css=iif(len(rest.physical) EQ 0 and len(rest.strPhysical) NEQ 0,de('Red'),de(''))>
							<td class="tblContent#css#">
								<cfif rest.readOnly>
									#rest.strPhysical#
								<cfelse>
									<cfinput  onKeyDown="checkTheBox(this)" type="text" 
									name="physical_#rest.currentrow#" value="#rest.strPhysical#" required="no"  
									class="xlarge" message="#stText.rest.PhysicalMissing##rest.currentrow#)">
								</cfif>
							</td>
							<!--- default --->
							<td>
								<cfif rest.readOnly>
									#yesNoFormat(rest.default)#
								<cfelse>
									<input type="radio" class="radio" name="default" value="#rest.currentrow#" onchange="changeDefault(this)" <cfif rest.default>checked="checked"</cfif>/>
								</cfif>
							</td>
							<!--- edit
							<td>
								<cfif not rest.readOnly>
									<a href="#request.self#?action=#url.action#&action2=create&virtual=#rest.virtual#" class="btn-mini edit"><span>edit</span></a>
								</cfif>
							</td> --->
						</tr>
					</cfif>
				</cfloop>
				<cfif hasAccess>
					<cfmodule template="remoteclients.cfm" colspan="4" line=true>
				</cfif>
			</tbody>
			<cfif hasAccess>
				<tfoot>
					<tr>
						<td colspan="5">
							<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.save#">
							<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
							<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.Delete#">
						</td>	
					</tr>
				</tfoot>
			</cfif>
		</table>

		<cfif hasAccess>
			<h2>Create new mapping</h2>
			<table class="maintbl">
				<tbody>
					<tr>
						<th scope="row">#stText.rest.VirtualHead#</th>
						<td>
							<input type="hidden" name="row_#rest.recordcount+1#" value="#rest.recordcount+1#">
							<cfinput type="text" name="virtual_#rest.recordcount+1#" value="" required="no" class="medium" />
						</td>
					</tr>
					<tr>
						<th scope="row">#stText.rest.PhysicalHead#</th>
						<td>
							<cfinput type="text" name="physical_#rest.recordcount+1#" value="" required="no" class="large">
						</td>
					</tr>
					<tr>
						<th scope="row">#stText.rest.DefaultHead#</th>
						<td>
							<input type="radio" class="radio" name="default" value="#rest.recordcount+1#" onchange="changeDefault(this)"/>
						</td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2">
							<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.save#">
						</td>
					</tr>
				</tfoot>
			</table>
		</cfif>
	</cfform>
</cfoutput>
