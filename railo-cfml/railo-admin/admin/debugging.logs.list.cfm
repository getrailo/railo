

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
	<td class="tblContent"><input name="minExeTime" value="0" style="width:60px"/> ms<br /><span class="comment">#stText.debug.minExeTimeDesc#</span></td>
</tr>
<tr>
	<td class="tblHead">#stText.debug.pathRestriction#</td>
	<td class="tblContent"><input name="minExeTime" value="0" style="width:60px"/> ms<br /><span class="comment">#stText.debug.pathRestrictionDesc#</span></td>
</tr>
--->
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<cfif request.adminType EQ "web"><input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.resetServerAdmin#"></cfif>
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
            <td class="tblContent"><input name="minExeTimeTotal" value="0" style="width:60px"/></td>
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
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
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
    <td class="tblHead" nowrap><input type="text" name="path" style="width:100%" value="#session.debugFilter.path#" /><br />
    	<span class="comment">#stText.Debug.filterPath#</span></td>
    <td class="tblHead" nowrap><input type="text" name="starttime" style="width:100%" value="#LSDateFormat(session.debugFilter.starttime)# #LSTimeFormat(session.debugFilter.starttime)#" /></td>
    <td class="tblHead" nowrap><input type="text" name="query" style="width:40px" value="#session.debugFilter.query#" /></td>
    <td class="tblHead" nowrap><input type="text" name="app" style="width:40px" value="#session.debugFilter.app#" /></td>
    <td class="tblHead" nowrap><input type="text" name="total" style="width:40px" value="#session.debugFilter.total#" /></td>
</tr>
<tr>
    <td class="tblHead" colspan="5"><input type="submit" name="mainAction" class="submit" value="#stText.Debug.filter#" style="width:100%"/></td>
</tr>
<tr>
    <td colspan="5"></td>
</tr>

<cfloop from="1" to="#arrayLen(logs)#" index="i">
<cfset el=logs[i]>
<cfset _total=0><cfloop query="el.pages"><cfset _total+=el.pages.total></cfloop>
<cfset _query=0><cfloop query="el.pages"><cfset _query+=el.pages.query></cfloop>
<cfset _app=0><cfloop query="el.pages"><cfset _app+=el.pages.app></cfloop>	
<cfset _path=el.cgi.SCRIPT_NAME& (len(el.cgi.QUERY_STRING)?"?"& el.cgi.QUERY_STRING:"")>
<cfif 
	doFilter(session.debugFilter.path,_path,false) and 
	doFilterMin(session.debugFilter.query,_query) and 
	doFilterMin(session.debugFilter.app,_app) and 
	doFilterMin(session.debugFilter.total,_total)> 
<tr>
	<td class="tblContent"><a href="#request.self#?action=#url.action#&action2=detail&id=#hash(el.id&":"&el.startTime)#">#_path#</a></td>
	<td class="tblContent">#LSDateFormat(el.starttime)# #LSTimeFormat(el.starttime)#</td>
	<td class="tblContent" nowrap="nowrap">#formatUnit(_query)#</td>
	<td class="tblContent" nowrap="nowrap">#formatUnit(_app)#</td>
	<td class="tblContent" nowrap="nowrap">#formatUnit(_total)#</td>
</tr>
</cfif>
</cfloop>


</cfform>
</table>

</cfif>
</cfoutput>
<br><br>
