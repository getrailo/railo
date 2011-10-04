<cfparam name="error" default="#struct(message:"",detail:"")#">
<cfparam name="form.run" default="">
<cfset stars="****************">


<!--- 
ACTIONS --->
<cftry>
	<cfif form.run EQ "create2">
		<cfset attrColl=struct()>
		
		<cfif isDefined('form.url')>
			<cfset attrColl.url=form.url>
		<cfelse>
			<cfset form.url_server=replace(form.url_server,'\','/','all')>
			<cfset form.url_path=replace(form.url_path,'\','/','all')>
		
			<cfif left(form.url_path,1) NEQ "/">
				<cfset form.url_path="/"&form.url_path>
			</cfif>
			<cfif right(form.url_server,1) EQ "/">
				<cfset form.url_server=mid(form.url_server,1,len(form.url_server)-1)>
			</cfif>
		
			<cfset attrColl.url=form.url_server&form.url_path>
		</cfif>
		<cfset attrColl.proxypassword=form.proxypassword>
		<cfset attrColl.securitykey=trim(form.securitykey)>
		<cfset attrColl.serverpassword=form.serverpassword>
		<cfset attrColl.adminpassword=form.adminpassword>
		<cfset attrColl.label=form.label>
		
		<cfif form.proxypassword EQ stars><cfset attrColl.proxypassword=form.proxypasswordh></cfif>
		<cfif form.securitykey EQ stars><cfset attrColl.securitykey=trim(form.securitykeyh)></cfif>
		<cfif form.serverpassword EQ stars><cfset attrColl.serverpassword=form.serverpasswordh></cfif>
		<cfif form.adminpassword EQ stars><cfset attrColl.adminpassword=form.adminpasswordh></cfif>
		
		
		<cfset attrColl.serverusername=form.serverusername>
		<cfset attrColl.usage="">
		<cfif isDefined('form.usage')><cfset attrColl.usage=form.usage></cfif>
		<cfset attrColl.proxyport=form.proxyport>
		<cfset attrColl.proxyusername=form.proxyusername>
		<cfset attrColl.proxyserver=form.proxyserver>
        <!--- 
		<cfadmin 
			action="verifyRemoteClient"
			type="#request.adminType#"
			remotetype="#request.adminType#"
			password="#session["password"&request.adminType]#"
			attributeCollection="#attrColl#">
		--->
		<cfadmin 
			action="updateRemoteClient"
			type="#request.adminType#"
			remotetype="#request.adminType#"
			password="#session["password"&request.adminType]#"
			attributeCollection="#attrColl#"
			>
        <cfadmin 
            action="getRemoteClients"
            type="#request.adminType#"
            password="#session["password"&request.adminType]#"
            returnVariable="clients">
        <cfset row=0>
        <cfloop query="clients">
        	<cfif clients.securityKey EQ attrColl.securityKey and clients.url EQ attrColl.url>
            	<cfset row=clients.currentrow>
            </cfif>
        </cfloop>
		<cflocation url="#request.self#?action=#url.action#&row=#row#" addtoken="no">
	</cfif>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>
<!---
<cfadmin 
    action="updateRemoteClientUsage"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    
    code="susi" displayname="susanne">

<cfadmin 
    action="removeRemoteClientUsage"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    
    code="susi" >
--->
<cfadmin 
    action="getRemoteClientUsage"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    
    returnVariable="usage">
    
<!--- 
Error Output--->
<cfset printError(error)>
<cfsilent>
	<cfif structKeyExists(url,'url')>
		<cfset actionType="update">
		<cfadmin 
			action="getRemoteClients"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			
			returnVariable="clients">
		<cfloop query="clients">
			<cfif hash(clients.url) EQ url.url>
				<cfadmin 
					action="getRemoteClient"
					type="#request.adminType#"
					password="#session["password"&request.adminType]#"
					url="#clients.url#"
					returnVariable="rc">
			</cfif>
		</cfloop>
		<cfset rc.serverpasswordh=rc.serverpassword>
		<cfset rc.adminpasswordh=rc.adminpassword>
		<cfset rc.proxypasswordh=rc.proxypassword>
		<cfset rc.securityKeyh=rc.securityKey>
		<cfif len(rc.serverpassword)><cfset rc.serverpassword=stars></cfif>
		<cfif len(rc.adminpassword)><cfset rc.adminpassword=stars></cfif>
		<cfif len(rc.proxypassword)><cfset rc.proxypassword=stars></cfif>
		<cfif len(rc.securityKey)><cfset rc.securityKey=stars></cfif>
		
	<cfelse>
		<cfset actionType="create">
		<cfset rc.url_server="">
		<cfset rc.url_path="/railo-context/admin.cfc?wsdl">
		<cfset rc.serverpassword="">
		<cfset rc.serverpasswordh="">
		<cfset rc.adminpassword="">
		<cfset rc.adminpasswordh="">
		<cfset rc.securitykey="">
		<cfset rc.securitykeyh="">
		<cfset rc.proxyusername="">
		<cfset rc.proxyport="">
		<cfset rc.type="">
		<cfset rc.usage="">
		<cfset rc.serverusername="">
		<cfset rc.proxypassword="">
		<cfset rc.proxypasswordh="">
		<cfset rc.proxyserver="">
		<cfset rc.label="">
	</cfif>

</cfsilent>

<cfoutput>
<script language="javascript">
function removeStars(field) {
	if(field.value=="****************")field.value="";
}

</script>

<h2># stText.remote.detail[actionType]#</h2>

<table class="tbl" width="650">
<tr>
	<td colspan="2">#stText.remote.detail[actionType& "Desc"]#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#&action2=create#iif(isDefined('url.url'),de('&url=##url.url##'),de(''))#" method="post">

	<tr>
		<td class="tblHead" width="200">#stText.remote.label#</td>
		<td class="tblContent" width="450">
			<cfinput type="text" name="label" id="label" value="#rc.label#" style="width:300px" required="yes" message="#stText.remote.LabelMissing#">
			
		</td>
	</tr>
	
	
	
	
	<tr>
		<td width="200" colspan="2"><br /></td>
	</tr>
	<tr>
		<td class="tblHead" width="200">#stText.remote.usage.title#</td>
		<td class="tblContent" width="450">
			<span class="comment">#stText.remote.usage.desc#</span><br />
            
            <cfloop query="usage">
            <input type="checkbox" name="usage" id="usage" value="#usage.code#" <cfif FindNoCase(usage.code,rc.usage)>  checked="checked"</cfif>> #usage.displayname#<br />
            </cfloop>
            <!---
			<input type="checkbox" name="usage" id="usage" value="synchronisation"<cfif FindNoCase('synchronisation',rc.usage)>  checked="checked"</cfif>> #stText.remote.usage.sync#
			<cfif request.admintype EQ "server"><br /><input type="checkbox" name="usage" id="usage" value="cluster"<cfif FindNoCase('cluster',rc.usage)>  checked="checked"</cfif>> #stText.remote.usage.cluster#</cfif>--->
		</td>
	</tr>
	
	<tr>
		<td width="200" colspan="2"><br /><b>#stText.remote.connection#</b><br /><span class="comment">#stText.remote.connectionDesc#</span></td>
	</tr>
	
<cfif actionType EQ "create">
	<tr>
		<td class="tblHead" width="200">#stText.remote.urlServer#</td>
		<td class="tblContent" width="450">
			<span class="comment">#stText.remote.urlServerDesc#</span><br />
			<cfinput type="text" name="url_server" id="url_server" value="#rc.url_server#" style="width:450px" required="yes" message="#stText.remote.urlServerMissing#">
		</td>
	</tr>

	<tr>
		<td class="tblHead" width="200">#stText.remote.urlPath#</td>
		<td class="tblContent" width="450">
			<span class="comment">#stText.remote.urlPathDesc#</span><br />
			<cfinput type="text" name="url_path" id="url_path" value="#rc.url_path#" style="width:450px" required="yes" message="#stText.remote.urlPathMissing#">
		</td>
	</tr>
<cfelse>

	<tr>
		<td class="tblHead" width="200">#stText.remote.url#</td>
		<td class="tblContent" width="450">
			<input type="hidden" name="url" id="url" value="#rc.url#">
			<b>#rc.url#</b>
		</td>
	</tr>
</cfif>
	<tr>
		<td class="tblHead" width="200">#stText.remote.serverusername#</td>
		<td class="tblContent" width="450">
			<span class="comment">#stText.remote.serverusernameDesc#</span><br />
			<cfinput type="text" name="serverusername" id="serverusername" value="#rc.serverusername#" style="width:200px">
		</td>
	</tr>
	<tr>
		<td class="tblHead" width="200">#stText.remote.serverpassword#</td>
		<td class="tblContent" width="450">
			<span class="comment">#stText.remote.serverpasswordDesc#</span><br />
			<input type="hidden" name="serverpasswordh" id="serverpasswordh" value="#rc.serverpasswordh#">
			<cfinput type="password" passthrough='autocomplete="off"' onClick="this.value='';" name="serverpassword" id="serverpassword" value="#rc.serverpassword#" style="width:200px">
		</td>
	</tr>
	<tr>
		<td width="150" colspan="2" ><br /><b>#stText.remote.adminAccess#</b><br /><span class="comment">#stText.remote.adminAccessDesc#</span></td>
	</tr>
	<tr>
		<td class="tblHead" width="200">#stText.remote.adminPassword[request.adminType]#</td>
		<td class="tblContent" width="450">
			<span class="comment">#stText.remote.adminPasswordDesc[request.adminType]#</span><br />
			<input type="hidden" name="adminPasswordh" id="adminPasswordh" value="#rc.adminPasswordh#">
			<cfinput type="password" passthrough='autocomplete="off"' onClick="this.value='';" name="adminPassword" id="adminPassword" value="#rc.adminPassword#" style="width:200px" required="yes" message="#stText.remote.passwordMissing#">
		</td>
	</tr>
	<tr>
		<td class="tblHead" width="200">#stText.remote.securityKey#</td>
		<td class="tblContent" width="450">
			<span class="comment">#stText.remote.securityKeyDesc#</span><br />
			<input type="hidden" name="securityKeyh" id="securityKeyh" value="#rc.securityKeyh#">
			<cfinput type="text" name="securityKey" id="securityKey" value="#rc.securityKey#" onClick="removeStars(this)" style="width:300px" required="yes" message="#stText.remote.securityKeyMissing#"></td>
	</tr>

	<tr>
		<td width="150" colspan="2"><br /><b>#stText.remote.proxy#</b><br /><span class="comment">#stText.remote.proxyDesc#</span></td>
	</tr>
	<tr>
		<td class="tblHead" width="200">#stText.remote.proxyServer#</td>
		<td class="tblContent" width="450">
			<span class="comment">#stText.remote.proxyServerDesc#</span><br />
			<cfinput type="text" name="proxyServer" id="proxyServer" value="#rc.proxyServer#" style="width:250px">
		</td>
	</tr>
	<tr>
		<td class="tblHead" width="200">#stText.remote.proxyPort#</td>
		<td class="tblContent" width="450">
			<span class="comment">#stText.remote.proxyPortDesc#</span><br />
			<cfinput type="text" name="proxyPort" id="proxyPort" value="#rc.proxyPort#" style="width:50px">
		</td>
	</tr>
	<tr>
		<td class="tblHead" width="200">#stText.remote.proxyUsername#</td>
		<td class="tblContent" width="450">
			<cfinput type="text" name="proxyUsername" id="proxyUsername" value="#rc.proxyUsername#" style="width:200px">
		</td>
	</tr>
	<tr>
		<td class="tblHead" width="200">#stText.remote.proxyPassword#</td>
		<td class="tblContent" width="450">
			<input type="hidden" name="proxyPasswordh" id="proxyPasswordh" value="#rc.proxyPasswordh#">
			<cfinput type="password" passthrough='autocomplete="off"' onClick="this.value='';" name="proxyPassword" id="proxyPassword" value="#rc.proxyPassword#" style="width:200px">
		</td>
	</tr>


<tr>
	<td width="150" colspan="2">&nbsp;</td>
</tr>

<tr>
	<td colspan="2">
	<input type="hidden" name="run" id="run" value="create2">
	<input type="submit" class="submit" name="_run" id="_run" value="#stText.Buttons[actionType]#">
	<input onClick="window.location='#request.self#?action=#url.action#';" type="button" class="button" name="cancel" id="cancel" value="#stText.Buttons.Cancel#"></td>
</tr>






</cfform>
</table>
</cfoutput>