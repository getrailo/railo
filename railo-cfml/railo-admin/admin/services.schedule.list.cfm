<cfset error.message="">
<cfset error.detail="">

<!--- 
Defaults ---> 
<cfparam name="session.st.nameFilter" default="">
<cfparam name="session.st.IntervalFilter" default="">
<cfparam name="session.st.urlFilter" default="">
<cfparam name="session.st.sortOrder" default="">
<cfparam name="session.st.sortName" default="">

<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cfparam name="error" default="#struct(message:"",detail:"")#">
<cfset error.message="">

<cffunction name="doFilter" returntype="string" output="false">
	<cfargument name="filter" required="yes" type="string">
	<cfargument name="value" required="yes" type="string">
	<cfargument name="exact" required="no" type="boolean" default="false">
	
	<cfset arguments.filter=replace(arguments.filter,'*','',"all")>
    <cfset filter=trim(filter)>
	<cfif not len(filter)>
		<cfreturn true>
	</cfif>
	<cfif exact>
		<cfreturn filter EQ value>
	<cfelse>
		<cfreturn FindNoCase(filter,value)>
	</cfif>
</cffunction>

<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- FILTER --->
		<cfcase value="filter">
			<cfset session.st.nameFilter=form.nameFilter>
			<cfset session.st.IntervalFilter=form.IntervalFilter>
			<cfset session.st.urlFilter=form.urlFilter>
		</cfcase>
	<!--- EXECUTE --->
		<cfcase value="#stText.Buttons.Execute#">
			<cfset data.names=toArrayFromForm("name")>
			<cfset data.rows=toArrayFromForm("row")>
			<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
				<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
				<cfsetting requesttimeout="10000">
					<cfadmin 
						action="schedule" 
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						scheduleAction="run" 
						task="#data.names[idx]#"
						remoteClients="#request.getRemoteClients()#">
				</cfif>
			</cfloop>
		</cfcase>
	<!--- DELETE --->
		<cfcase value="#stText.Buttons.Delete#">
			<cfset data.names=toArrayFromForm("name")>
			<cfset data.rows=toArrayFromForm("row")>
			<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
				<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
				
					<cfadmin 
						action="schedule" 
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						scheduleAction="delete" 
						task="#data.names[idx]#"
						remoteClients="#request.getRemoteClients()#">
				</cfif>
			</cfloop>
		</cfcase>
	<!--- pause --->
		<cfcase value="#stText.Schedule.pause#">
			<cfset data.names=toArrayFromForm("name")>
			<cfset data.rows=toArrayFromForm("row")>
			<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
				<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
				
					<cfadmin 
						action="schedule" 
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						scheduleAction="pause" 
						task="#data.names[idx]#"
						remoteClients="#request.getRemoteClients()#">
				</cfif>
			</cfloop>
		</cfcase>
	<!--- resume --->
		<cfcase value="#stText.Schedule.resume#">
			<cfset data.names=toArrayFromForm("name")>
			<cfset data.rows=toArrayFromForm("row")>
			<cfloop index="idx" from="1" to="#arrayLen(data.names)#">
				<cfif isDefined("data.rows[#idx#]") and data.names[idx] NEQ "">
				
					<cfadmin 
						action="schedule" 
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						scheduleAction="resume" 
						task="#data.names[idx]#"
						remoteClients="#request.getRemoteClients()#">
				</cfif>
			</cfloop>
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>


<!--- set order --->
<cfif isDefined("url.order") and ListFindNoCase("task,interval,url",url.order)>
	<cfif session.st.sortName NEQ url.order>
    	<cfset session.st.sortOrder="">
    </cfif>
	<cfset session.st.sortName=url.order>
   
    <cfif session.st.sortOrder EQ "">
    	<cfset session.st.sortOrder="asc">
    <cfelseif  session.st.sortOrder EQ "asc">
    	<cfset session.st.sortOrder="desc">
    <cfelseif  session.st.sortOrder EQ "desc">
    	<cfset session.st.sortOrder="asc">
    </cfif>
</cfif>


<!--- 
Redirtect to entry --->
<cfif cgi.request_method EQ "POST" and error.message EQ "">
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

<cfoutput>#stText.Schedule.Description#</cfoutput><br><br>


<!--- 
list all mappings and display necessary edit fields --->

<script language="javascript">
function selectAll(field) {
	var form=field.form;
	var str="";
	for(var key in form.elements){
		if(form.elements[key] && (""+form.elements[key].name).indexOf("row_")==0){
			form.elements[key].checked=field.checked;
		}
	}
}
function checkTheBox(field) {
	var apendix=field.name.split('_')[1];
	var box=field.form['row_'+apendix];
	box.checked=true;
}
</script>

<cfschedule action="list" returnvariable="tasks">
<cfif len(session.st.sortName) and len(session.st.sortOrder)>
	<cfset querysort(tasks, session.st.sortName,session.st.sortOrder)>
</cfif>


<!--- 
List --->
<cfif tasks.recordcount>



<table class="tbl" width="740">
<tr>
	<td colspan="4"><cfoutput><h2>#stText.Schedule.Detail#</h2>
#stText.Schedule.DetailDescription#</cfoutput></td>
</tr>
<tr>
	<td colspan="4"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">
	<cfoutput>
	<tr>
		<td width="20"></td>
		<td width="225" class="tblHead" nowrap><input type="text" name="nameFilter" style="width:225px" value="#session.st.nameFilter#" /></td>
		<td width="130" class="tblHead" nowrap><input type="text" name="IntervalFilter" style="width:130px" value="#session.st.IntervalFilter#" /></td>
		<td width="225" class="tblHead" nowrap><input type="text" name="urlFilter" style="width:225px" value="#session.st.urlFilter#" /></td>
		<td class="tblHead" nowrap><input type="submit" class="submit" name="mainAction" value="filter"></td>
	</tr>
	<tr>
		<td width="380" colspan="5" align="right"></td>
	</tr>
    
    
    <tr>
		<td width="20"><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"></td>
		<td width="140" class="tblHead" nowrap><a href="#request.self#?action=#url.action#&order=task">#stText.Schedule.Name#
		<cfif session.st.sortName EQ "task" and len(session.st.sortOrder)><cfmodule template="img.cfm" src="arrow-#session.st.sortOrder EQ "asc"?"up":"down"#.gif" hspace="4" vspace="2" border="0"></cfif></a></td>
		<td width="160" class="tblHead" nowrap><a href="#request.self#?action=#url.action#&order=interval">#stText.Schedule.Interval#
		<cfif session.st.sortName EQ "interval" and len(session.st.sortOrder)><cfmodule template="img.cfm" src="arrow-#session.st.sortOrder EQ "asc"?"up":"down"#.gif" hspace="4" vspace="2" border="0"></cfif></a></td>
		<td width="170" class="tblHead" nowrap><a href="#request.self#?action=#url.action#&order=url">#stText.Schedule.URL#
		<cfif session.st.sortName EQ "url" and len(session.st.sortOrder)><cfmodule template="img.cfm" src="arrow-#session.st.sortOrder EQ "asc"?"up":"down"#.gif" hspace="4" vspace="2" border="0"></cfif></a></td>
		<td width="60" class="tblHead" nowrap>#stText.Schedule.paused#</td>
	</tr>
	
	<cfloop query="tasks">
		
		<cfif isNumeric(tasks.interval)>
				<cfset _int=toStructInterval(tasks.interval)>
				<cfset _intervall="#stText.Schedule.Every# (hh:mm:ss) #two(_int.hour)#:#two(_int.minute)#:#two(_int.second)#">
			<cfelse>
				<cfset _intervall=tasks.interval>
			</cfif>
	
	<cfif
			doFilter(session.st.nameFilter,tasks.task,false)
			and
			doFilter(session.st.IntervalFilter,_intervall,false)
			and
			doFilter(session.st.urlFilter,tasks.url,false)
			>
		<cfset css=iif(tasks.valid and not tasks.paused,de('Green'),de('Red'))>
		<!--- and now display --->
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><input type="checkbox" class="checkbox" name="row_#tasks.currentrow#" value="#tasks.currentrow#"></td>
			<td><a href="#request.self#?action=#url.action#&action2=edit&task=#hash(tasks.task)#">
			<cfmodule template="img.cfm" src="edit.png" hspace="2" border="0"></a></td>
		</tr>
		</table>
		</td>
		<td class="tblContent#css#" nowrap><input type="hidden" 
			name="name_#tasks.currentrow#" value="#HTMLEditFormat( tasks.task)#">#tasks.task#</td>
		<td class="tblContent#css#" nowrap>#_intervall#</td>
		<td class="tblContent#css#" title="#tasks.url#" nowrap>#cut(tasks.url,50)#</td>
		<td class="tblContent#css#"  nowrap>#YesNoFormat(tasks.paused)#</td>
	</tr>
</cfif></cfloop>

<cfmodule template="remoteclients.cfm" colspan="8" line=true>
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
			<cfoutput>
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Execute#">
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Delete#">
			<input type="submit" class="submit" name="mainAction" value="#stText.Schedule.pause#">
			<input type="submit" class="submit" name="mainAction" value="#stText.Schedule.resume#">
			</cfoutput>
			</td>	
		</tr>
		 </table>
		 </td>
	</tr>
</cfoutput>
</cfform>
</table>
<br><br>
</cfif>
<!--- 
Create Task --->
<cfoutput>

<table class="tbl" width="740">
<tr>
	<td colspan="2"><h2>#stText.Schedule.CreateTask#</h2></td>
</tr>
<cfform action="#request.self#?action=#url.action#&action2=create" method="post">
<tr>
	<td class="tblHead" width="100">#stText.Schedule.Name#</td>
	<td class="tblContent" width="500"><cfinput type="text" name="name" value="" style="width:200px" required="yes" 
		message="#stText.Schedule.NameMissing#"></td>
</tr>
<tr>
	<td class="tblHead">#stText.Schedule.URL#</td>
	<td class="tblContent">
		<span class="comment">#stText.Schedule.URLDescription#</span><br>
		<cfinput type="text" name="url" value="" style="width:350px" required="yes" 
		message="#stText.Schedule.URLMissing#"></td>
</tr>
<tr>
	<td class="tblHead">#stText.Schedule.IntervalType#</td>
	<td class="tblContent"><span class="comment">#stText.Schedule.IntervalTypeDesc#</span><br><select name="interval">
		<option value="3600">#stText.Schedule.Every# ...</option>
		<option value="once">#stText.Schedule.Once#</option>
		<option value="daily">#stText.Schedule.Daily#</option>
		<option value="weekly">#stText.Schedule.Weekly#</option>
		<option value="monthly">#stText.Schedule.Monthly#</option>
	</select></td>
</tr>
<tr>
	<td class="tblHead">#stText.Schedule.StartDate#</td>
	<td class="tblContent">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment">#stText.General.Day#</td>
			<td class="comment">#stText.General.Month#</td>
			<td class="comment">#stText.General.Year#</td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_day" value="#two(day(now()))#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_month" value="#two(month(now()))#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_year" value="#two(year(now()))#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
		</tr>
		</table></td>
</tr>
<tr>
	<td class="tblHead">#stText.Schedule.StartTime#</td>	
	<td class="tblContent">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment">#stText.General.Hour#</td>
			<td class="comment">#stText.General.Minute#</td>
			<td class="comment">#stText.General.second#</td>
		</tr>
        <tr>
			<td><cfinput type="text" name="start_hour" value="00" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_minute" value="00" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_second" value="00" style="width:40px" required="yes" validate="integer">&nbsp;</td>
		</tr>
        
		</table></td>
</tr>
<tr>
	<td class="tblHead">#stText.Schedule.paused#</td>	
	<td class="tblContent"><input type="checkbox" name="paused" value="true" /></td>
</tr>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
		<input type="submit" class="submit" name="run" value="#stText.Buttons.Create#">
	</td>
</tr>
</cfform>
</cfoutput>
</table>
<!---
<cfmodule template="log.cfm" name="scheduled-task" title="Log" description="this is ...">
--->
