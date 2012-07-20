

<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.Update#">
			<cfadmin 
				action="updateDebug"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				debug="#form.debug#"
				debugTemplate=""
				remoteClients="#request.getRemoteClients()#">
			
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			<cfadmin 
				action="updateDebug"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
                debug=""
				debugTemplate=""
                
				remoteClients="#request.getRemoteClients()#">
			
		</cfcase>
    <!--- delete --->
		<cfcase value="#stText.Buttons.Delete#">
				<cfset data.rows=toArrayFromForm("row")>
				<cfset data.ids=toArrayFromForm("id")>
				<cfloop index="idx" from="1" to="#arrayLen(data.ids)#">
					<cfif isDefined("data.rows[#idx#]") and data.ids[idx] NEQ "">
						<cfadmin 
							action="removeDebugEntry"
							type="#request.adminType#"
							password="#session["password"&request.adminType]#"
							id="#data.ids[idx]#"
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
<cfif cgi.request_method EQ "POST" and error.message EQ "" and form.mainAction neq stText.Buttons.verify>
	<cflocation url="#request.self#?action=#url.action#" addtoken="no">
</cfif>

<cfset querySort(debug,"id")>
<cfset qryWeb=queryNew("id,label,iprange,type,custom,readonly,driver")>
<cfset qryServer=queryNew("id,label,iprange,type,custom,readonly,driver")>


<cfloop query="debug">	
	<cfif not debug.readOnly>
    	<cfset tmp=qryWeb>
	<cfelse>
    	<cfset tmp=qryServer>
	</cfif>
	<cfset QueryAddRow(tmp)>
    <cfset QuerySetCell(tmp,"id",debug.id)>
    <cfset QuerySetCell(tmp,"label",debug.label)>
    <cfset QuerySetCell(tmp,"iprange",debug.iprange)>
    <cfset QuerySetCell(tmp,"type",debug.type)>
    <cfset QuerySetCell(tmp,"custom",debug.custom)>
    <cfset QuerySetCell(tmp,"readonly",debug.readonly)>
    <cfif structKeyExists(drivers,debug.type)><cfset QuerySetCell(tmp,"driver",drivers[debug.type])></cfif>
</cfloop>
<cfoutput>

<!--- 
Error Output--->
<cfset printError(error)>
<script language="javascript">
var drivers={};
<cfloop collection="#drivers#" item="key">drivers['#JSStringFormat(key)#']='#JSStringFormat(drivers[key].getDescription())#';
</cfloop>
function setDesc(id,key){
	var div = document.getElementById(id);
	if(div.hasChildNodes())
		div.removeChild(div.firstChild);
	div.appendChild(document.createTextNode(drivers[key]));
	

}
</script>

<table class="tbl" width="740">
<cfoutput><cfform onerror="customError" action="#request.self#?action=#url.action#" method="post" name="debug_settings">
<tr>
	<td class="tblHead" width="150">#stText.Debug.EnableDebugging#</td>
	<td class="tblContent" height="28">
		<cfset lbl=iif(_debug.debug,de(stText.general.yes),de(stText.general.no))>
	
		<span class="comment">#stText.Debug.EnableDescription#</span><br />
		<cfif hasAccess>
			<select name="debug">
				<cfif request.admintype EQ "web">
					<option #iif(_debug.debugsrc EQ "server",de('selected'),de(''))# value="">#stText.Regional.ServerProp[request.adminType]# <cfif _debug.debugsrc EQ "server">(#lbl#) </cfif></option>
					<option #iif(_debug.debugsrc EQ "web" and _debug.debug,de('selected'),de(''))# value="true">#stText.general.yes#</option>
					<option #iif(_debug.debugsrc EQ "web" and not _debug.debug,de('selected'),de(''))# value="false">#stText.general.no#</option>
				<cfelse>
					<option #iif(_debug.debug,de('selected'),de(''))# value="true">#stText.general.yes#</option>
					<option #iif(_debug.debug,de(''),de('selected'))# value="false">#stText.general.no#</option>
				</cfif>
			</select>
		
			<!--- <input type="checkbox" class="checkbox" name="debug" value="yes" <cfif debug.debug>checked</cfif>>--->
		<cfelse>
			<b>#lbl#</b> <input type="hidden" name="debug" value="#_debug.debug#">
		</cfif>
		
		
	</td>
</tr>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfif>

</cfform></cfoutput>
</table>
<br><br>



<!--- LIST --->
<cfloop list="server,web" index="k">
<cfset isWeb=k EQ "web">
<cfset qry=variables["qry"&k]>
<cfif qry.recordcount>
	<h2>#stText.debug.list[k & "title"]#</h2>
	#stText.debug.list[k & "titleDesc"]#
    
<table class="tbl" width="740">

<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
	<tr>
		<cfif isWeb><td width="40"><input type="checkbox" class="checkbox" name="rowreadonly" onclick="selectAll(this)"></td></cfif>
		<td width="160" class="tblHead" nowrap>#stText.debug.label#</td>
		<td width="#isWeb?440:480#" class="tblHead" nowrap>#stText.debug.ipRange#</td>
		<td width="100" class="tblHead" nowrap># stText.debug.type#</td>
	</tr>
	<cfloop query="qry">
    <cfif IsSimpleValue(qry.driver)><cfcontinue></cfif>
   	<input type="hidden" name="id_#qry.currentrow#" value="#qry.id#">
    <input type="hidden" name="type_#qry.currentrow#" value="#qry.type#">
    
    <tr>
    <cfif isWeb>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
			<input type="checkbox" class="checkbox" name="row_#qry.currentrow#" value="#qry.currentrow#">
			</td>
            <td>
            <a href="#request.self#?action=#url.action#&action2=create&id=#qry.id#">
			<img src="resources/img/edit.png.cfm" hspace="2" border="0"></a>
            </td>
		</tr>
		</table>
		</td>
     </cfif>
		<td class="tblContent" nowrap>#qry.label#</td>
		<td class="tblContent" nowrap>#replace(qry.ipRange,",","<br />","all")#</td>
		<td class="tblContent" nowrap>#qry.driver.getLabel()#</td>
		
	</tr>
	</cfloop>
<cfif isWeb>
	<tr>
		<td colspan="#isWeb?4:3#">
		 <table border="0" cellpadding="0" cellspacing="0">
		 <tr>
			<td><img src="resources/img/tp.gif.cfm" width="8" height="1"></td>		
			<td><img src="resources/img/#request.admintype#-bgcolor.gif.cfm" width="1" height="20"></td>
			<td></td>
		 </tr>
		 <tr>
			<td></td>
			<td valign="top"><img src="resources/img/#request.admintype#-bgcolor.gif.cfm" width="1" height="14"><img src="resources/img/#request.admintype#-bgcolor.gif.cfm" width="36" height="1"></td>
			<td>&nbsp;
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.delete#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			</td>	
		</tr>
		 </table>
		 </td>
	</tr>
</cfif>
</cfform>
</table>
<br><br>
</cfif>
</cfloop>



</cfoutput>
<!--- 
	Create debug entry --->
<cfif access EQ "yes">
<cfoutput>
	<cfset _drivers=ListSort(StructKeyList(drivers),'textnocase')>
	
    <cfif listLen(_drivers)>
    <h2>#stText.debug.titleCreate#</h2>
	<table class="tbl" width="420">
	<cfform onerror="customError" action="#request.self#?action=#url.action#&action2=create" method="post">
	<tr>
		<td class="tblHead" width="50">#stText.debug.label#</td>
		<td class="tblContent" width="370"><cfinput type="text" name="label" value="" style="width:370px" required="yes" 
			message="#stText.debug.labelMissing#"></td>
	</tr>
	
	<tr>
		<td class="tblHead" width="50">#stText.Settings.gateway.type#</td>
		<td class="tblContent" width="300"><select name="type" onchange="setDesc('typeDesc',this.value);" on>
					<cfloop list="#_drivers#" index="key">
                    <cfset driver=drivers[key]>
                    <option value="#trim(driver.getId())#">#trim(driver.getLabel())#</option>
					</cfloop>
				</select>
                <div id="typeDesc" style="position:relative"></div>
                <script>setDesc('typeDesc','#JSStringFormat(listFirst(_drivers))#');</script>
                
                
                </td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" class="submit" name="run" value="#stText.Buttons.create#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		</td>
	</tr>
	</cfform>
	</table>   
	<br><br>
    <cfelse>
    #stText.debug.noDriver#
    </cfif>
    
	</cfoutput>
<cfelse>
 	<cfset noAccess(stText.debug.noAccess)>


</cfif>

    
    
