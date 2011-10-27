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
				<cfset data.classes=toArrayFromForm("class")>
				<cfset data.names=toArrayFromForm("name")>
				<cfset data.rows=toArrayFromForm("row")>
                
                <cfset data.procedures=toArrayFromForm("procedure")>
				<cfset data.serverlibraries=toArrayFromForm("serverlibrary")>
				<cfset data.keepalives=toArrayFromForm("keepalive")>
				<cfset data.types=toArrayFromForm("type")>
				
                
		<!--- update --->
			<cfif form.subAction EQ "#stText.Buttons.save#">
				<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
					<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
					<cfif data.types[idx] EQ "cpp">
                        <cfadmin 
                            action="updateCPPCFX"
                            type="#request.adminType#"
                            password="#session["password"&request.adminType]#"
                            
                            name="#data.names[idx]#"
                            procedure="#data.procedures[idx]#"
                            serverlibrary="#data.serverlibraries[idx]#"
                            keepalive="#isDefined('data.keepalives[idx]') and data.keepalives[idx]#"
                            remoteClients="#request.getRemoteClients()#">
                    <cfelse>
                        <cfadmin 
                            action="updateJavaCFX"
                            type="#request.adminType#"
                            password="#session["password"&request.adminType]#"
                            
                            name="#data.names[idx]#"
                            class="#data.classes[idx]#"
                            remoteClients="#request.getRemoteClients()#">
                    </cfif>
                    
					
					</cfif>
				</cfloop>
		<!--- verify --->
			<cfelseif form.subAction EQ "#stText.Buttons.verify#">
				<cfset noRedirect=true>
				<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
					<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
						<cftry>
                            <cfadmin 
                                action="verifyCFX"
                                type="#request.adminType#"
                                password="#session["password"&request.adminType]#"
                                
                                name="#data.names[idx]#">
								<cfset stVeritfyMessages[data.names[idx]].Label = "OK">
							<cfcatch>
								<cfset stVeritfyMessages[data.names[idx]].Label = "Error">
								<cfset stVeritfyMessages[data.names[idx]].message = cfcatch.message>
                            </cfcatch>
						</cftry>
					</cfif>
				</cfloop>
                
                
		<!--- delete --->
			<cfelseif form.subAction EQ "#stText.Buttons.Delete#">
				
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
	<cfset noAccess(stText.CFX.NoAccessUsage)>
<cfelse>

<cfif not has.cfx_setting><cfset noAccess(stText.CFX.NoAccessSetting)></cfif>

<cfadmin 
	action="getJavaCFXTags"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="jtags">
<cfadmin 
	action="getCPPCFXTags"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="ctags">

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


<!------------------------------ JAVA ------------------------------->
<table class="tbl" width="740">
<tr>
	<td colspan="4"><cfoutput><h2>#stText.CFX.CFXTags#</h2></cfoutput></td>
</tr>

<cfform name="java" action="#request.self#?action=#url.action#" method="post">
<cfoutput>
	<tr>
		<td><cfif has.cfx_setting ><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"></cfif></td>
		<td class="tblHead" nowrap>#stText.CFX.Name#</td>
		<td class="tblHead" nowrap>#stText.CFX.Class#</td>
		<td width="50" class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
	</tr>
	<cfloop query="jtags">
		<!--- and now display --->
	<tr>
		<td>
        <input type="hidden" name="type_#jtags.currentrow#" value="#jtags.displayname#">
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
            <cfif StructKeyExists(stVeritfyMessages, jtags.name)>
                <cfif stVeritfyMessages[jtags.name].label eq "OK">
                    <span class="CheckOk">#stVeritfyMessages[jtags.name].label#</span>
                <cfelse>
                    <span class="CheckError" title="#stVeritfyMessages[jtags.name].message##Chr(13)#">#stVeritfyMessages[jtags.name].label#</span>
                    &nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
                        width="9" 
                        height="9" 
                        border="0" 
                        title="#stVeritfyMessages[jtags.name].message##Chr(13)#">
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
			<input type="hidden" name="type_#idx#" value="java">
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
</table>

<cfif structKeyExists(session,'enable') and session.enable EQ "cfxcpp">
<!------------------------------ C++ ------------------------------->
<br />
<table class="tbl" width="740">
<tr>
	<td colspan="4"><cfoutput><h2>#stText.CFX.cpp.CFXTags#</h2>
	
	
<span class="CheckError">
The C++ CFX tags Implementation is currently in Beta State. Its functionality can change before it's final release.
If you have any problems while using the C++ CFX tags Implementation, please post the bugs and errors in our <a href="https://jira.jboss.org/jira/browse/RAILO" target="_blank" class="CheckError">bugtracking system</a>. 
</span><br /><br />
	
	</cfoutput>
    </td>
</tr>

<cfform name="cpp" action="#request.self#?action=#url.action#" method="post">
<cfoutput>
	<tr>
		<td><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"></td>
		<td class="tblHead" nowrap>#stText.CFX.Name#</td>
		<td class="tblHead" nowrap>#stText.CFX.serverlibrary#</td>
		<td class="tblHead" nowrap>#stText.CFX.procedure#</td>
		<td class="tblHead" nowrap>#stText.CFX.keepAlive#</td>
		<td width="50" class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
	</tr>
	<cfloop query="ctags">
		<!--- and now display --->
	<tr>
        <!--- read-only --->
		<td>
    	<input type="hidden" name="type_#ctags.currentrow#" value="#ctags.displayname#">
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><cfif not ctags.readOnly><input type="checkbox" class="checkbox" name="row_#ctags.currentrow#" 
			value="#ctags.currentrow#"></cfif></td>
		</tr>
		</table>
		</td>
		
        <!--- name --->
        <td class="tblContent" nowrap height="28"><input type="hidden" 
			name="name_#ctags.currentrow#" value="#ctags.name#">&lt;cfx_<b>#ctags.name#</b>&gt;</td>
		<cfset css=iif(not ctags.isvalid,de(' style="background-color:####E3D1D6"'),de(''))>
		
        <!--- serverlibrary --->
		<td class="tblContent<cfoutput>#css#</cfoutput>" nowrap><cfif not has.cfx_setting or ctags.readOnly>#ctags.serverlibrary#<cfelse><cfinput 
		onKeyDown="checkTheBox(this)" type="text" name="serverlibrary_#ctags.currentrow#" value="#ctags.serverlibrary#" 
		required="yes"  style="width:250px" message="#stText.CFX.MissingClassValue##ctags.currentrow#)"></cfif></td>
        
        <!--- procedure --->
		<td class="tblContent<cfoutput>#css#</cfoutput>" nowrap><cfif not has.cfx_setting or ctags.readOnly>#ctags.procedure#<cfelse><cfinput 
		onKeyDown="checkTheBox(this)" type="text" name="procedure_#ctags.currentrow#" value="#ctags.procedure#" 
		required="yes"  style="width:120px" message="#stText.CFX.MissingClassValue##ctags.currentrow#)"></cfif></td>
        
        <!--- keepAlive --->
		<td class="tblContent<cfoutput>#css#</cfoutput>" nowrap>
			<cfif not has.cfx_setting or ctags.readOnly>
        		#yesNoFormat(ctags.procedure)#
			<cfelse>
            	<input type="checkbox" class="checkbox" onclick="checkTheBox(this)" name="keepalive_#ctags.currentrow#" value="true" <cfif ctags.keepAlive>checked</cfif>>
			</cfif>
        </td>
        
        
		<!--- check --->
        <td class="tblContent" nowrap valign="middle" align="center">
            <cfif StructKeyExists(stVeritfyMessages, ctags.name)>
                <cfif stVeritfyMessages[ctags.name].label eq "OK">
                    <span class="CheckOk">#stVeritfyMessages[ctags.name].label#</span>
                <cfelse>
                    <span class="CheckError" title="#stVeritfyMessages[ctags.name].message##Chr(13)#">#stVeritfyMessages[ctags.name].label#</span>
                    &nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
                        width="9" 
                        height="9" 
                        border="0" 
                        title="#stVeritfyMessages[ctags.name].message##Chr(13)#">
                </cfif>
            <cfelse>
                &nbsp;				
            </cfif>
        </td>
        
	</tr>
</cfloop>
<cfset idx=ctags.recordcount+1>
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
		<td class="tblContent" nowrap ><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="serverlibrary_#idx#" value="" required="no" style="width:250px"></td>
		<td class="tblContent" nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="procedure_#idx#" value="ProcessTagRequest" required="no" style="width:120px"></td>
		<td class="tblContent" nowrap colspan="2">
        	<input type="checkbox" class="checkbox" onclick="checkTheBox(this)" name="keepalive_#idx#" value="true"></td>
	</tr>
	<tr>
		<td></td>
		<td align="center" colspan="5">
        
        
	<cfif server.os.archModel NEQ server.java.archModel>
    	<cfset archText=stText.CFX.cpp.archDiff>	
    <cfelse>
    	<cfset archText=stText.CFX.cpp.arch>	
    </cfif>
    <cfset archText=replace(archText,"{os-arch}",server.os.archModel,"all")>
    <cfset archText=replace(archText,"{jre-arch}",server.java.archModel,"all")>	
	<cfoutput><span class="comment"  style="color:red">#archText#</span></cfoutput>
        </td>
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
			<input type="hidden" name="type_#idx#" value="cpp">
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
</table>
</cfif>

</cfif>


	
	

