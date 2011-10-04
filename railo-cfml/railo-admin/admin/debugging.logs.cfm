<cfparam name="session.debugFilter.path" default="">

<cfset error.message="">
<cfset error.detail="">
<cfparam name="url.action2" default="list">
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cfset isWeb=request.admintype EQ "web">

<cfadmin 
	action="securityManager"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="access"
	secType="debugging">
    
<cfif isWeb>
<cfadmin 
	action="getLoggedDebugData"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="logs">
<cfadmin 
	action="getDebugEntry"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="entries">
</cfif>    
<cfadmin 
	action="getDebugSetting"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="setting">
    

<cftry>
	<cfset stVeritfyMessages = StructNew()>
	<cfswitch expression="#form.mainAction#">
	<!--- UPDATE --->
		<cfcase value="#stText.Buttons.Update#">
			<cfadmin 
				action="updateDebugSetting"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
                maxLogs="#form.maxLogs#"
				remoteClients="#request.getRemoteClients()#">
			
		</cfcase>
	<!--- reset to server setting --->
		<cfcase value="#stText.Buttons.resetServerAdmin#">
			<cfadmin 
				action="updateDebugSetting"
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
                maxLogs=""
				remoteClients="#request.getRemoteClients()#">
			
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
    
    

<cfset stText.debug.path="Path">
<cfset stText.debug.reqTime="Request Time">
<cfset stText.debug.exeTime="Execution Timespan (ms)">
<cfset stText.debug.exeTimeQuery="Query">
<cfset stText.debug.exeTimeTotal="Total">
<cfset stText.debug.exeTimeApp="App">
<cfset stText.debug.maxLogs="Maximal Logged Requests">
<cfset stText.debug.minExeTime="Minimal Execution Time (ms)">
<cfset stText.debug.minExeTimeDesc="Minimal Execution Time that Railo outputs the debugger information of a request.">
<cfset stText.debug.pathRestriction="Path Restriction">
<cfset stText.debug.pathRestrictionDesc="Path that should not be outputted, sperated path by line break.">

<cfset stText.debug.settingTitle="Settings">
<cfset stText.debug.settingDesc="Define how Railo Log the debugging information.">

<cfset stText.debug.outputTitle="Output">
<cfset stText.debug.outputDesc="Debugging information logged by Railo.">



<cfset stText.debug.filter="Filter">
<cfset stText.debug.filterTitle="Output Filters">
<cfset stText.debug.filterPath="Only Outputs records where the path match the following pattern.">



<cfoutput>
<h2>#stText.debug.settingTitle#</h2>
#stText.debug.settingDesc#
<table class="tbl" width="480">
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post" name="debug_settings">
<tr>
	<td class="tblHead" width="160">#stText.debug.maxLogs#</td>
	<td class="tblContent" width="90"><select name="maxLogs">
   <cfset selected=false><cfloop list="10,20,50,100,200,500,1000" index="idx"><option <cfif idx EQ setting.maxLogs><cfset selected=true>selected="selected"</cfif> value="#idx#">#idx#</option></cfloop>
   <cfif !selected><option selected="selected" value="#setting.maxLogs#">#setting.maxLogs#</option></cfif>
    </select></td>
</tr>
<!---
<tr>
	<td class="tblHead">#stText.debug.minExeTime#</td>
	<td class="tblContent"><input name="minExeTime" id="minExeTime" value="0" style="width:60px"/> ms<br /><span class="comment">#stText.debug.minExeTimeDesc#</span></td>
</tr>
<tr>
	<td class="tblHead">#stText.debug.pathRestriction#</td>
	<td class="tblContent"><input name="minExeTime" id="minExeTime" value="0" style="width:60px"/> ms<br /><span class="comment">#stText.debug.pathRestrictionDesc#</span></td>
</tr>
--->
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" id="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" id="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" id="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
</cfform>
</table>
<br /><br />



<cfif isWeb>
<!---<h2>#stText.debug.filterTitle#</h2>
<table class="tbl" width="740">
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post" name="debug_settings">

<tr>
	<td class="tblHead">#stText.debug.minExeTime#</td>
	<td class="tblContent">
    	<table class="tbl">
        <tr>
            <td class="tblHead" >Total</td
        </tr>
        <tr>
            <td class="tblContent"><input name="minExeTimeTotal" id="minExeTimeTotal" value="0" style="width:60px"/></td>
        </tr>
    	</table>
    </td>
</tr>
<tr>
	<td class="tblHead">#stText.debug.pathRestriction#</td>
	<td class="tblContent"><textarea name="pathRestriction" cols="60" rows="10" style="width:100%"></textarea><br /><span class="comment">#stText.debug.pathRestrictionDesc#</span></td>
</tr>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" id="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" id="cancel" value="#stText.Buttons.Cancel#">
	</td>
</tr>
</cfform>
</table>
<br /><br />--->


<h2>#stText.debug.outputTitle#</h2>
#stText.debug.outputDesc#
<table class="tbl" width="740">
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post" name="debug_settings">
<colgroup>
	<col width="400"/>
	<col width="220"/>
	<col width="40"/>
	<col width="40"/>
	<col width="40"/>
</colgroup>


<tr>
	<td class="tblHead" rowspan="2">#stText.Debug.path#</td>
	<td class="tblHead" rowspan="2">#stText.Debug.reqTime#</td>
	<td class="tblHead" colspan="3" align="center">#stText.Debug.exeTime#</td>
</tr>
<tr>
	<td class="tblHead">#stText.Debug.exeTimeQuery#</td>
	<td class="tblHead">#stText.Debug.exeTimeApp#</td>
	<td class="tblHead">#stText.Debug.exeTimeTotal#</td>
</tr>


<tr>
    <td colspan="5"></td>
</tr>
<tr>
    <td class="tblHead" nowrap><input type="text" name="path" id="path" style="width:100%" value="#session.debugFilter.path#" /><br />
    	<span class="comment">#stText.Debug.filterPath#</span></td>
    <td class="tblHead" nowrap><input type="text" name="IntervalFilter" id="IntervalFilter" style="width:100%" value="#session.debugFilter.path#" /></td>
    <td class="tblHead" nowrap><input type="text" name="urlFilter" id="urlFilter" style="width:40px" value="#session.debugFilter.path#" /></td>
    <td class="tblHead" nowrap><input type="text" name="urlFilter" id="urlFilter" style="width:40px" value="#session.debugFilter.path#" /></td>
    <td class="tblHead" nowrap><input type="text" name="urlFilter" id="urlFilter" style="width:40px" value="#session.debugFilter.path#" /></td>
</tr>
<tr>
    <td class="tblHead" colspan="5"><input type="submit" name="filter" id="filter" class="submit" value="#stText.Debug.filter#" style="width:100%"/></td>
</tr>
<tr>
    <td colspan="5"></td>
</tr>









<cfloop from="1" to="#arrayLen(logs)#" index="i">
<cfset el=logs[i]>
<cfset _total=0><cfloop query="el.pages"><cfset _total+=el.pages.total></cfloop>
<cfset _query=0><cfloop query="el.pages"><cfset _query+=el.pages.query></cfloop>
<cfset _app=0><cfloop query="el.pages"><cfset _app+=el.pages.app></cfloop>	
<tr>
	<td class="tblContent"><a href="#request.self#?action=#url.action#">#el.cgi.SCRIPT_NAME##len(el.cgi.QUERY_STRING)?"?"& el.cgi.QUERY_STRING:""#</a></td>
	<td class="tblContent">#LSDateFormat(el.starttime)# #LSTimeFormat(el.starttime)#</td>
	<td class="tblContent">#_query#</td>
	<td class="tblContent">#_app#</td>
	<td class="tblContent">#_total#</td>
</tr>
</cfloop>

<!--- 
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" id="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" id="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" id="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
	</td>
</tr>
--->

</cfform>
</table>

<cfif StructKeyExists(url,"id")>
	
<!--- load available drivers --->
	<cfset drivers=struct()>
    <cfdirectory directory="./debug" action="list" name="dir" recurse="no" filter="*.cfc">
    <cfloop query="dir">
    	<cfif dir.name EQ "Debug.cfc" or dir.name EQ "Field.cfc" or dir.name EQ "Group.cfc">
        	<cfcontinue>
        </cfif>
    	<cfset tmp=createObject('component','debug/#ReplaceNoCase(dir.name,'.cfc','')#')>
        <cfset drivers[trim(tmp.getId())]=tmp>
    </cfloop>
    <cfset driver=drivers["railo-classic"]>
	
	
	<cfloop query="entries">
		<cfif entries.type EQ "railo-classic">
        	<cfset entry=querySlice(entries, entries.currentrow ,1)>
        </cfif>    
    </cfloop>
	
	
	
	<cfset log=logs[id]>
    
	<cfset driver.output(entry.custom,log)>    
    
    
<cfdump var="#driver#">
<cfdump var="#entry#">
<cfdump var="#log#">

</cfif>

</cfif>
</cfoutput>
<br><br>



