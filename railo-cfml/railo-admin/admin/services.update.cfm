<cfif request.admintype EQ "web"><cflocation url="#request.self#" addtoken="no"></cfif>

<cfparam name="url.action2" default="none">
<cfset error.message="">
<cfset error.detail="">

<cftry>
<cfswitch expression="#url.action2#">
	<cfcase value="settings">
    	<cfif not len(form.location)>
        	<cfset form.location=form.locationCustom>
        </cfif>
        
		<cfadmin 
			action="UpdateUpdate"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			
			updateType="#form.type#"
			updateLocation="#form.location#"
			remoteClients="#request.getRemoteClients()#">
	</cfcase>
	<cfcase value="run">
		<cfsetting requesttimeout="10000">
		<cfadmin 
			action="runUpdate"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			remoteClients="#request.getRemoteClients()#">
	</cfcase>
	<cfcase value="updateJars">
		<cfsetting requesttimeout="10000">
		<cfadmin 
			action="updateJars"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			remoteClients="#request.getRemoteClients()#">
	</cfcase>
	<cfcase value="remove">
		<cfadmin 
			action="removeUpdate"
            onlyLatest="#StructKeyExists(form,'latest')#"
			type="#request.adminType#"
			password="#session["password"&request.adminType]#"
			remoteClients="#request.getRemoteClients()#">
	</cfcase>
</cfswitch>
	<cfcatch>
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
			action="listPatches"
			returnvariable="patches"
            type="#request.adminType#"
            password="#session["password"&request.adminType]#">
            
<cfadmin 
			action="needNewJars"
			returnvariable="needNewJars"
            type="#request.adminType#"
            password="#session["password"&request.adminType]#">


<cffunction name="getAviableVersion" output="false">
	
	<cfset var http="">
	<cftry>
	<cfhttp 
			url="#update.location#/railo/remote/version/Info.cfc?method=getpatchversionfor&level=#server.ColdFusion.ProductLevel#&version=#server.railo.version#" 
		method="get" resolveurl="no" result="http">
	<cfwddx action="wddx2cfml" input="#http.fileContent#" output="wddx">
	<cfset session.avaiableVersion=wddx>
	<cfreturn session.avaiableVersion>
		<cfcatch>
			<cfreturn "">
		</cfcatch>
	</cftry>
</cffunction>

<cffunction name="getAviableVersionDoc" output="false">
	
	<cfset var http="">
	<cftry>
	<cfhttp 
		url="#update.location#/railo/remote/version/Info.cfc?method=getPatchVersionDocFor&level=#server.ColdFusion.ProductLevel#&version=#server.railo.version#" 
		method="get" resolveurl="no" result="http"><!--- #server.railo.version# --->
	<cfwddx action="wddx2cfml" input="#http.fileContent#" output="wddx">
	<cfreturn wddx>
		<cfcatch>
			<cfreturn "-">
		</cfcatch>
	</cftry>
</cffunction>
<cfoutput>

<cfadmin 
		action="getUpdate"
		type="#request.adminType#"
		password="#session["password"&request.adminType]#"
		returnvariable="update">


<cfset curr=server.railo.version>
<cfset avi=getAviableVersion()>
<cfset hasAccess=1>
<cfset hasUpdate=curr LT avi>



<script>
function checkTheBox(field) {
	
	var box=field.form.location;
	box[box.length-1].checked=true;
}

</script>
#stText.services.update.desc#<br><br>

<!--- 
Settings --->

<table class="tbl" width="740">
<tr>
	<td colspan="2"><h2>#stText.services.update.setTitle#</h2>#stText.services.update.setDesc#</td>
</tr>


<cfform action="#go(url.action,"settings")#" method="post">
<tr>
	<td class="tblHead" width="150">#stText.services.update.provider#</td>
	<td class="tblContent">
	<cfif hasAccess>
    <cfset isCustom=true>
    <table class="tbl">
    <tr>
    	<td valign="top"><input type="radio" name="location" value="http://www.getrailo.org"<cfif update.location EQ 'http://www.getrailo.org'> <cfset isCustom=false>checked="checked"</cfif> /></td>
        <td>#stText.services.update.location_www#<br /><span class="comment">#stText.services.update.location_wwwdesc#</span></td>
    </tr>
    <tr>
    	<td valign="top"><input type="radio" name="location" value="http://preview.getrailo.org"<cfif update.location EQ 'http://preview.getrailo.org'> <cfset isCustom=false>checked="checked"</cfif> /></td>
        <td>#stText.services.update.location_preview#<br /><span class="comment">#stText.services.update.location_previewdesc#</span></td>
    </tr>
    <tr>
    	<td valign="top"><input type="radio" name="location" value="http://dev.getrailo.org"<cfif update.location EQ 'http://dev.getrailo.org'> <cfset isCustom=false>checked="checked"</cfif> /></td>
        <td>#stText.services.update.location_dev#<br /><span class="comment">#stText.services.update.location_devdesc#</span></td>
    </tr>
    <tr>
    	<td valign="top"><input type="radio" name="location"<cfif isCustom> checked="checked"</cfif> value="" /></td>
        <td>#stText.services.update.location_custom# <input onkeydown="checkTheBox(this)"  onclick="checkTheBox(this)" type="text" class="text" name="locationCustom" size="40" value="<cfif isCustom>#update.location#</cfif>"><br />
        <span class="comment">#stText.services.update.location_customDesc#</span></td>
    </tr>
    </table>
     
    
    
    
	<cfelse>
	<b>#update.location#</b>
	</cfif></td>
	
</tr>
<tr>
	<td class="tblHead" width="150">#stText.services.update.type#</td>
	<td class="tblContent">
	<cfif hasAccess>
	<select name="type">
		<option value="manual" <cfif update.type EQ "manual">selected</cfif>>#stText.services.update.type_manually#</option>
		<option value="auto" <cfif update.type EQ "auto">selected</cfif>>#stText.services.update.type_auto#</option>
	</select>
	<cfelse>
	<b>#update.type#</b>
	</cfif><br>
	<span class="comment">#stText.services.update.typeDesc#</span></td>
	
</tr>
<cfif hasAccess>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr></cfif>
</cfform>
</table>
<br><br>

<!--- 
Info --->

<cfif hasUpdate>
<h2>#stText.services.update.infoTitle#</h2>
#replace(replace(replace(stText.services.update.update,'{available}','<b>#avi#</b>'),'{current}','<b>#curr#</b>'),'{avaiable}','<b>#avi#</b>')#
<cfscript>
// Jira
jira=stText.services.update.jira;
jira=replace(jira,'{a}','<a href="http://jira.jboss.org/jira/browse/RAILO" target="_blank">');
jira=replace(jira,'{/a}','</a>');
try	{
	// Changelog
	content=getAviableVersionDoc();
	start=1;
	arr=array();
	
	while(true){
		res=REFindNoCase("\[\ *(RAILO-([0-9]*)) *\]",content,start,true);
		if(arraylen(res.pos) LT 3)break;
		ArrayAppend(arr,res);
		start=res.pos[1]+res.len[1];
	}
	
	for(i=arrayLen(arr);i>=1;i--){
		res=arr[i];
		label=mid(content,res.pos[2],res.len[2]);
		nbr=mid(content,res.pos[3],res.len[3]);
		content=replace(content,label,'<a target="_blank" href="http://jira.jboss.org/jira/browse/RAILO-'&nbr&'">'& label& '</a>');
	}
}
catch(e){}

</cfscript>


<div class="tblContent" style="overflow:auto;width:740px;height:200px;border-style:solid;border-width:1px;padding:10px"><pre>#trim(content)#</pre></div>
#jira#

<cfelseif not needNewJars>
<h2>#stText.services.update.infoTitle#</h2>
#replace(stText.services.update.noUpdate,'{current}',curr)#
</cfif>


<cfif hasUpdate>
<br><br>
<!--- 
run update --->

<table class="tbl" width="740">
<tr>
	<td colspan="2"><h2>#stText.services.update.exe#</h2>#stText.services.update.exeDesc#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>

<cfform action="#go(url.action,"Run")#" method="post">

<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.services.update.exeRun#">
	</td>
</tr>
</cfform>
</table>

<cfelseif needNewJars>
<br><br>
    <table class="tbl" width="740">
    <tr>
        <td colspan="2"><h2>#stText.services.update.lib#</h2>#stText.services.update.libDesc#</td>
    </tr>
    <tr>
        <td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
    </tr>
    
    
    <cfform action="#go(url.action,"updateJars")#" method="post">
    
    <cfmodule template="remoteclients.cfm" colspan="2">
    <tr>
        <td colspan="2">
            <input type="submit" class="submit" name="mainAction" value="#stText.services.update.lib#">
        </td>
    </tr>
    </cfform>
    </table>

</cfif>





<!--- 
remove update --->
<cfset size=arrayLen(patches)>
<cfif size>

<br><br>

<table class="tbl" width="740">

<tr>
	<td colspan="2"><h2>#stText.services.update.remove#</h2>
#stText.services.update.removeDesc#</td>
</tr>
<tr>
	<td class="tblHead" colspan="2">#stText.services.update.patch#</td>
</tr>

<cfloop index="i" from="1" to="#size#">
<tr>
	<td class="tblContent" colspan="2">#patches[i]#</td>
	
</tr><cfset version=patches[i]>
</cfloop>

<cfform action="#go(url.action,"Remove")#" method="post">

<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.services.update.removeRun#">
		<input type="submit" class="submit" name="latest" value="#replace(stText.services.update.removeLatest,'{version}',version)#">
	</td>
</tr>
</cfform>
</table>
</cfif>
</cfoutput>