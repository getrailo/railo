<!--- <cfif isDefined("form")>
	<cfinclude template="act/resources.act_mapping.cfm">
</cfif> --->
<cfset error.message="">
<cfset error.detail="">
<cfparam name="stveritfymessages" default="#struct()#">

<!--- 
Defaults --->
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cfparam name="error" default="#struct(message:"",detail:"")#">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="has.cfx_setting"
	secType="cfx_setting"
	secValue="yes">
	
<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="has.cfx_usage"
	secType="cfx_usage"
	secValue="yes">


<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="updateJava">
		<!--- update --->
			<cfif form.subAction EQ "#stText.Buttons.save#">
				<cfset data.classes=toArrayFromForm("class")>
				<cfset data.names=toArrayFromForm("name")>
				<cfset data.rows=toArrayFromForm("row")>
				<cfloop index="idx" from="1" to="#arrayLen(data.classes)#">
					<cfif isDefined("data.rows[#idx#]") and data.classes[idx] NEQ "" and data.names[idx] NEQ "">
						
					<cfadmin 
						action="updateJavaCFX"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						name="#data.names[idx]#"
						class="#data.classes[idx]#"
			remoteClients="#request.getRemoteClients()#">
					
					</cfif>
				</cfloop>
		<!--- verify --->
			<cfelseif form.subAction EQ "#stText.Buttons.verify#">
				<cfset data.classes=toArrayFromForm("class")>
				<cfset data.names=toArrayFromForm("name")>
				<cfset data.rows=toArrayFromForm("row")>
                <cfset noRedirect=true>
				<cfloop index="idx" from="1" to="#arrayLen(data.classes)#">
					<cfif isDefined("data.rows[#idx#]") and data.classes[idx] NEQ "" and data.names[idx] NEQ "">
						<cftry>
                            <cfadmin 
                                action="verifyJavaCFX"
                                type="#request.adminType#"
                                password="#session["password"&request.adminType]#"
                                
                                name="#data.names[idx]#"
                                class="#data.classes[idx]#">
								<cfset stVeritfyMessages[data.names[idx]&data.classes[idx]].Label = "OK">
							<cfcatch>
								<cfset stVeritfyMessages[data.names[idx]&data.classes[idx]].Label = "Error">
								<cfset stVeritfyMessages[data.names[idx]&data.classes[idx]].message = cfcatch.message>
                            </cfcatch>
						</cftry>
					</cfif>
				</cfloop>
                
                
		<!--- delete --->
			<cfelseif form.subAction EQ "#stText.Buttons.Delete#">
				<cfset data.names=toArrayFromForm("name")>
				<cfset data.rows=toArrayFromForm("row")>
				
				<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
					<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
					<cfadmin 
						action="removeCFX"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						name="#data.names[idx]#"
			remoteClients="#request.getRemoteClients()#">
					</cfif>
				</cfloop>
			</cfif>
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>


<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "" and not isDefined('noRedirect')>
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<!--- 
Error Output--->
<cfif error.message NEQ "">
<cfoutput><span class="CheckError">
#error.message#<br>
#error.detail#
</span><br><br></cfoutput>
</cfif>



<cfif not has.cfx_usage>
<cfoutput>#stText.CFX.NoAccess#</cfoutput>
<cfelse>

<cfadmin 
	action="getJavaCFXTags"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="jtags">

<!--- 
<cfset javaCFXTagClasses=array()>
<cfloop collection="#cfxTagClasses#" item="key">
	<cfset tmp=cfxTagClasses[key]>
	<cfif tmp.class.name EQ "railo.runtime.cfx.customtag.JavaCFXTagClass">
		<cfset ArrayAppend(javaCFXTagClasses,tmp)>
	</cfif>
</cfloop>
 --->
<script>
function checkTheBox(field) {
	var apendix=field.name.split('_')[1];
	var box=field.form['row_'+apendix];
	box.checked=true;
}

function selectAll(field) {
	var form=field.form;
	for(var key in form.elements){
		if((""+form.elements[key].name).indexOf("row_")==0){
			form.elements[key].checked=field.checked;
		}
	}
}
</script>

<cfoutput><h2>#stText.CFX.CFXTags#</h2></cfoutput>
<table class="tbl" width="450">
<tr>
	<td colspan="4"></td>
</tr>
<tr>
	<td colspan="4"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>

<cfform action="#request.self#?action=#url.action#" method="post">
<cfoutput>
	<tr>
		<td><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"></td>
		<td class="tblHead" nowrap>#stText.CFX.Name#</td>
		<td class="tblHead" nowrap>#stText.CFX.Class#</td>
		<td width="50" class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
	</tr>
	<cfloop query="jtags">
		<!--- and now display --->
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><cfif not jtags.readOnly><input type="checkbox" class="checkbox" name="row_#jtags.currentrow#" 
			value="#jtags.currentrow#"></cfif></td>
		</tr>
		</table>
		</td>
		<td class="tblContent" nowrap height="28"><input type="hidden" 
			name="name_#jtags.currentrow#" value="#jtags.name#">&lt;cfx_<b>#jtags.name#</b>&gt;</td>
		<cfset css=iif(not jtags.isvalid,de(' style="background-color:####E3D1D6"'),de(''))>
		
		<td class="tblContent<cfoutput>#css#</cfoutput>" nowrap><cfif not has.cfx_setting or jtags.readOnly>#jtags.class#<cfelse><cfinput 
		onKeyDown="checkTheBox(this)" type="text" name="class_#jtags.currentrow#" value="#jtags.class#" 
		required="yes"  style="width:350px" message="#stText.CFX.MissingClassValue##jtags.currentrow#)"></cfif></td>
        
        
		<!--- check --->
        <td class="tblContent" nowrap valign="middle" align="center">
            <cfif StructKeyExists(stVeritfyMessages, jtags.name&jtags.class)>
                <cfif stVeritfyMessages[jtags.name&jtags.class].label eq "OK">
                    <span class="CheckOk">#stVeritfyMessages[jtags.name& jtags.class].label#</span>
                <cfelse>
                    <span class="CheckError" title="#stVeritfyMessages[jtags.name&jtags.class].message##Chr(13)#">#stVeritfyMessages[jtags.name& jtags.class].label#</span>
                    &nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
                        width="9" 
                        height="9" 
                        border="0" 
                        title="#stVeritfyMessages[jtags.name&jtags.class].message##Chr(13)#">
                </cfif>
            <cfelse>
                &nbsp;				
            </cfif>
        </td>
        
	</tr>
</cfloop>
<cfset idx=jtags.recordcount+1>
<cfif has.cfx_setting>
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><input type="checkbox" class="checkbox" name="row_#idx#" value="#idx#"></td>
		</tr>
		</table>
		</td>
		<td class="tblContent" nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="name_#idx#" value="" required="no" style="width:150px"></td>
		<td class="tblContent" nowrap colspan="2"><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="class_#idx#" value="" required="no" style="width:350px"></td>
	</tr>
</cfif>
</cfoutput>
<cfif has.cfx_setting>
<cfmodule template="remoteclients.cfm" colspan="8" line>
<cfoutput>	
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
			<input type="hidden" name="mainAction" value="updateJava">
			<input type="submit" class="submit" name="subAction" value="#stText.Buttons.Verify#">
			<input type="submit" class="submit" name="subAction" value="#stText.Buttons.save#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			<input type="submit" class="submit" name="subAction" value="#stText.Buttons.Delete#">
			</td>	
		</tr>
		 </table>
		 </td>
	</tr>
</cfoutput>
</cfif>
</cfform>
</table></cfif>