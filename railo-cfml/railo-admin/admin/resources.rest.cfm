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
                
                list="#structKeyExists(form,'list') and form.list#">				
		</cfcase>
        <!--- reset/settings --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
            <cfadmin 
                action="updateRestSettings"
                type="#request.adminType#"
                password="#session["password"&request.adminType]#"
                remoteClients="#request.getRemoteClients()#"
                
                list="">				
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
<cfset stText.rest.list="List">
<cfset stText.rest.listDesc="List Services when ""/rest/"" is called ">
<cfset stText.rest.mapping="Mappings">
<cfset stText.rest.mappingDesc="Mappings ...">


<cfset stText.rest.desc="Rest is ...">
<cfset stText.rest.VirtualHead="Virtual">
<cfset stText.rest.PhysicalHead="Physical">
<cfset stText.rest.DefaultHead="Default">
<cfset stText.rest.PhysicalMissing="Please enter a value for the physical resource.">


<!--- 
list all mappings and display necessary edit fields --->
<script>
function checkTheBox(field) {
	var apendix=field.name.split('_')[1];
	var box=field.form['row_'+apendix];
	box.checked=true;
}

function changeDefault(field) {
	var form=field.form;
	for(var i=0;i<form.length;i++){
		if(form[i].name=='default') {
			if(form[i].checked)
				form["row_"+form[i].value].checked=true;
			
			//alert(form[i].value+":"+form[i].checked);
			
			//row_#rest.currentrow#
		}
	}
	
}

</script>
<cfoutput>

<cfif not hasAccess><cfset noAccess(stText.setting.noAccess)></cfif>


#stText.rest.desc#<br /><br />

<!--- 
Settings --->
<h2>#stText.rest.setting#</h2>
<table class="tbl" width="540">
<colgroup>
    <col width="150">
    <col width="390">
</colgroup>
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
<tr>
	<th scope="row">#stText.rest.list#</th>
	<td>
	<cfif hasAccess NEQ 0><input type="checkbox" class="checkbox" name="list" value="yes" <cfif settings.list>checked</cfif>><cfelse><b>#yesNoFormat(settings.list)#</b></cfif>
	<div class="comment">#stText.rest.listDesc#</div></td>
	
</tr>
<cfif hasAccess NEQ 0>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr></cfif>
</cfform>
</table>

<br><br>

<!--- 
Mappings --->
<h2>#stText.rest.mapping#</h2>
#stText.rest.mappingDesc#
<table class="tbl" width="100%" border="0">
 	<colgroup>
        <col width="10">
        <col width="10">
        <col width="30%">
        <col width="70%">
        <col>
    </colgroup>
<tr>
	<td colspan="7"><cfmodule template="tp.cfm" width="1" height="1"></td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
	<tr>
		<td><cfif hasAccess><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"></cfif><cfmodule template="tp.cfm"  width="10" height="1"></td>
		<td><cfmodule template="tp.cfm"  width="17" height="1"></td>
		<td class="tblHead" nowrap>#stText.rest.VirtualHead#</td>
		<td class="tblHead" nowrap>#stText.rest.PhysicalHead#</td>
		<td class="tblHead" nowrap>#stText.rest.DefaultHead#</td>
	</tr>
	<cfloop query="rest">
		<cfif not rest.hidden>
		<!--- and now display --->
		<input type="hidden" name="stopOnError_#rest.currentrow#" value="yes">
	<tr>
		<!--- checkbox ---->
		<td><table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><cfif not rest.readOnly><input type="checkbox" class="checkbox" name="row_#rest.currentrow#" value="#rest.currentrow#"></cfif></td>
		</tr>
		</table></td>
		
		<!--- edit --->
		<td><cfif not rest.readOnly><a href="#request.self#?action=#url.action#&action2=create&virtual=#rest.virtual#">
		<img src="resources/img/edit.png.cfm" border="0"></a></cfif></td>
		
		<!--- virtual --->
		<td height="30" title="#rest.virtual#" nowrap><input type="hidden" 
			name="virtual_#rest.currentrow#" value="#rest.virtual#">#cut(rest.virtual,14)#</td>
		
		<!--- physical --->
		<cfset css=iif(len(rest.physical) EQ 0 and len(rest.strPhysical) NEQ 0,de('Red'),de(''))>
		<td class="tblContent#css#" nowrap <cfif len(rest.strPhysical)>title="#rest.Physical#"</cfif>><cfif rest.readOnly>#cut(rest.strPhysical,36)#<cfelse><cfinput  onKeyDown="checkTheBox(this)" type="text" 
			name="physical_#rest.currentrow#" value="#rest.strPhysical#" required="no"  
			style="width:100%" message="#stText.rest.PhysicalMissing##rest.currentrow#)"></cfif></td>
		
		
		
		
		<!--- default --->
		<td nowrap>
			<cfif rest.readOnly>
            	#yesNoFormat(rest.default)#
			<cfelse>
            	<input type="radio" name="default" value="#rest.currentrow#" onchange="changeDefault(this)" <cfif rest.default>checked="checked"</cfif>/>
            </cfif>
		
		</td>
	</tr>
	</cfif>
</cfloop>
<cfif hasAccess>
	<tr>
		<td><table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><input type="checkbox" class="checkbox" name="row_#rest.recordcount+1#" value="#rest.recordcount+1#"></td>
		</tr>
		</table></td>
		
		<td><cfmodule template="tp.cfm"  width="17" height="1"></td>
		<td nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="virtual_#rest.recordcount+1#" value="" required="no" style="width:100%"></td>
		<td nowrap><cfinput onKeyDown="checkTheBox(this)" type="text" 
			name="physical_#rest.recordcount+1#" value="" required="no"  style="width:100%"></td>
		
		<td nowrap>
        	<input type="radio" name="default" value="#rest.recordcount+1#" onchange="changeDefault(this)"/>
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
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.save#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Delete#">
			</td>	
		</tr>
		 </table>
		 </td>
	</tr>
</cfif>
</cfform>
</cfoutput>
</table>