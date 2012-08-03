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
	<div class="pageintro">
		#stText.debug.settingDesc#
	</div>
	<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post" name="debug_settings">
		<table class="maintbl">
			<tbody>
				<tr>
					<th scope="row">#stText.debug.maxLogs#</th>
					<td>
						<select name="maxLogs">
							<cfset selected=false>
							<cfloop list="10,20,50,100,200,500,1000" index="idx">
								<option <cfif idx EQ setting.maxLogs><cfset selected=true>selected="selected"</cfif> value="#idx#">#idx#</option>
							</cfloop>
							<cfif !selected>
								<option selected="selected" value="#setting.maxLogs#">#setting.maxLogs#</option>
							</cfif>
						</select>
					</td>
				</tr>
				<!---
				<tr>
					<th scope="row">#stText.debug.minExeTime#</th>
					<td><input name="minExeTime" value="0" style="width:60px"/> ms<br /><span class="comment">#stText.debug.minExeTimeDesc#</span></td>
				</tr>
				<tr>
					<th scope="row">#stText.debug.pathRestriction#</th>
					<td><input name="minExeTime" value="0" style="width:60px"/> ms<br /><span class="comment">#stText.debug.pathRestrictionDesc#</span></td>
				</tr>
				--->
				<cfmodule template="remoteclients.cfm" colspan="2">
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.Update#">
						<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
						<cfif request.adminType EQ "web"><input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
					</td>
				</tr>
			</tfoot>
		</table>
	</cfform>
	
	<cfif isWeb>
		<!---<h2>#stText.debug.filterTitle#</h2>
		<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post" name="debug_settings">
		<table class="tbl" width="740">
		<tr>
			<th scope="row">#stText.debug.minExeTime#</th>
			<td>
				<table class="tbl">
				<tr>
					<td class="tblHead" >Total</td
				</tr>
				<tr>
					<td><input name="minExeTimeTotal" value="0" style="width:60px"/></td>
				</tr>
				</table>
			</td>
		</tr>
		<tr>
			<th scope="row">#stText.debug.pathRestriction#</th>
			<td><textarea name="pathRestriction" cols="60" rows="10" style="width:100%"></textarea><br /><span class="comment">#stText.debug.pathRestrictionDesc#</span></td>
		</tr>
		<cfmodule template="remoteclients.cfm" colspan="2">
		<tr>
			<td colspan="2">
				<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.Update#">
				<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			</td>
		</tr>
		</cfform>
		</table>
		<br /><br />--->
	
		<h2>#stText.debug.outputTitle#</h2>
		<div class="itemintro">#stText.debug.outputDesc#</div>
		<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post" name="debug_settings">
			<table class="maintbl">
				<thead>
					<tr>
						<th width="50%" rowspan="2">#stText.Debug.path#</th>
						<th width="35%" rowspan="2">#stText.Debug.reqTime#</th>
						<th width="15%" colspan="3">#stText.Debug.exeTime#</th>
					</tr>
					<tr>
						<th width="5%">#stText.Debug.exeTimeQuery#</th>
						<th width="5%">#stText.Debug.exeTimeApp#</th>
						<th width="5%">#stText.Debug.exeTimeTotal#</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>
							<input type="text" name="path" class="xlarge" value="#session.debugFilter.path#" />
							<div class="comment">#stText.Debug.filterPath#</div>
						</td>
						<td><input type="text" name="IntervalFilter" class="xlarge" value="#session.debugFilter.path#" /></td>
						<td><input type="text" name="urlFilter" class="number" value="#session.debugFilter.path#" /></td>
						<td><input type="text" name="urlFilter" class="number" value="#session.debugFilter.path#" /></td>
						<td><input type="text" name="urlFilter" class="number" value="#session.debugFilter.path#" /></td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="5"><input type="submit" name="filter" class="button submit" value="#stText.Debug.filter#" /></th>
					</tr>
				</tfoot>
				<cfif not arrayIsEmpty(logs)>
					<tbody>
						<cfloop from="1" to="#arrayLen(logs)#" index="i">
							<cfset el=logs[i]>
							<cfset _total=0><cfloop query="el.pages"><cfset _total+=el.pages.total></cfloop>
							<cfset _query=0><cfloop query="el.pages"><cfset _query+=el.pages.query></cfloop>
							<cfset _app=0><cfloop query="el.pages"><cfset _app+=el.pages.app></cfloop>	
							<tr>
								<td><a href="#request.self#?action=#url.action#">#el.cgi.SCRIPT_NAME##len(el.cgi.QUERY_STRING)?"?"& el.cgi.QUERY_STRING:""#</a></td>
								<td>#LSDateFormat(el.starttime)# #LSTimeFormat(el.starttime)#</td>
								<td>#_query#</td>
								<td>#_app#</td>
								<td>#_total#</td>
							</tr>
						</cfloop>
					</tbody>
				</cfif>
			</table>
		</cfform>
	<!--- 
	<tr>
		<td colspan="2">
			<input type="submit" class="button submit" name="mainAction" value="#stText.Buttons.Update#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			<cfif request.adminType EQ "web"><input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
		</td>
	</tr>
	--->
	
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