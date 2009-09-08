<cfparam name="url.action2" default="none">
<cfset error.message="">
<cfset error.detail="">

<cftry>
<cfswitch expression="#url.action2#">
	<cfcase value="settings">
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
	<cfcase value="remove">
		<cfadmin 
			action="removeUpdate"
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

#stText.services.update.desc#<br><br>

<!--- 
Settings --->
<h2>#stText.services.update.setTitle#</h2>
<table class="tbl" width="600">
<tr>
	<td colspan="2">#stText.services.update.setDesc#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>

<cfform action="#go(url.action,"settings")#" method="post">
<tr>
	<td class="tblHead" width="150">#stText.services.update.location#</td>
	<td class="tblContent">
	<cfif hasAccess><input type="text" class="text" name="location" size="40" value="#update.location#">
	<cfelse>
	<b>#update.location#</b>
	</cfif><br>
	<span class="comment">#stText.services.update.locdesc#</span></td>
	
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
<h2>#stText.services.update.infoTitle#</h2>
<cfif hasUpdate>
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

<cfelse>
#replace(stText.services.update.noUpdate,'{current}',curr)#
</cfif>
<br><br>

<cfif hasUpdate>
<!--- 
run update --->
<h2>#stText.services.update.exe#</h2>
<table class="tbl" width="600">
<tr>
	<td colspan="2">#stText.services.update.exeDesc#</td>
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
</cfif>



<!--- 
remove update --->
<h2>#stText.services.update.remove#</h2>
<table class="tbl" width="600">
<tr>
	<td colspan="2">#stText.services.update.removeDesc#</td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>

<cfform action="#go(url.action,"Remove")#" method="post">

<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.services.update.removeRun#">
	</td>
</tr>
</cfform>
</table>

</cfoutput>