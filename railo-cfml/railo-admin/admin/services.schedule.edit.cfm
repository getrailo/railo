<cfscript>
function toFile(path,file) {
	if(len(path) EQ 0) return file;
	if(right(path,1) NEQ server.separator.file) path=path&server.separator.file;
	return path&file;
	
}

function translateDateTime(task,dateName,timeName,newName) {
	var sct=struct();
	var d=0;
	// Date
	if(structKeyExists(task,dateName) and IsDate(task[dateName])) {
		d=task[dateName];
		sct.year=year(d);
		sct.month=two(month(d));
		sct.day=two(day(d));	
	}
	else {
		sct.year='';
		sct.month='';
		sct.day='';
	}
	// Time
	if(structKeyExists(task,timeName) and IsDate(task[timeName])) {
		d=task[timeName];
		sct.hour=two(hour(d));
		sct.minute=two(minute(d));
		sct.second=two(second(d));	
	}
	else {
		sct.hour='';
		sct.minute='';
		sct.second='';
	}
	task[newName]=sct;
}

function formBool(formName) {
	
	return structKeyExists(form,formName) and form[formName];
}
/**
* returns null if string is empty (no return is equal to return null)
*/
function nullIfEmpty(str) {
	str=trim(str);
	if(len(str) GT 0) return str;
}


function _toInt(str) {
	if(isNumeric(str)) return str;
	return 0;
}
</cfscript>

<cfparam name="error" default="#struct(message:"",detail:"")#">

<!--- 
ACTIONS --->
<cftry>
	<cfif StructKeyExists(form,"port")>

		<!--- Check Values --->
		<cfif not IsNumeric(form.port)><cfset form.port=-1></cfif>
		<cfif not IsNumeric(form.timeout)><cfset form.timeout=-1></cfif>
		<cfif not IsNumeric(form.proxyport)><cfset form.proxyport=80></cfif>
		
		
		<cfif not StructKeyExists(form,"interval")>
			<cfif StructKeyExists(form,"interval_hour")>
				<cfset form.interval=
					(_toInt(form.interval_hour)*3600)+
					(_toInt(form.interval_minute)*60)+
					(_toInt(form.interval_second))>
			<cfelse>
				<cfset form.interval=form._interval>
			</cfif>
			
		<cfelseif form.interval EQ "every ...">
			<cfset form.interval="3600">
		</cfif>
		<cfif structKeyExists(session,"passwordserver")>
			<cfset variables.passwordserver=session.passwordserver>
		<cfelse>
			<cfset variables.passwordserver="">
		</cfif>
		
			<cfadmin 
				action="schedule" 
				type="#request.adminType#"
				password="#session["password"&request.adminType]#"
				
				scheduleAction="update"
				task="#form.name#"
				url="#form.url#"
				port="#form.port#" 
				requesttimeout="#form.timeout#"
				username="#nullIfEmpty(form.username)#"
				schedulePassword="#nullIfEmpty(form.password)#"
				proxyserver="#nullIfEmpty(form.proxyserver)#"
				proxyport="#form.proxyport#"
				proxyuser="#nullIfEmpty(form.proxyuser)#"
				proxypassword="#nullIfEmpty(form.proxypassword)#"
				publish="#formBool('publish')#"
				resolveurl="#formBool('resolveurl')#"
				startdate="#nullIfNoDate('start')#"
				starttime="#nullIfNoTime('start')#"
				enddate="#nullIfNoDate('end')#"
				endtime="#nullIfNoTime('end')#"
				interval="#nullIfEmpty(form.interval)#"
				file="#nullIfEmpty(form.file)#"
				serverpassword="#variables.passwordserver#"
				remoteClients="#request.getRemoteClients()#"
                >
                
   <cfif StructKeyExists(form,"paused") and form.paused>
       	<cfadmin 
                    action="schedule" 
                    type="#request.adminType#"
                    password="#session["password"&request.adminType]#"
                    
                    scheduleAction="pause" 
                    task="#trim(form.name)#"
                    remoteClients="#request.getRemoteClients()#">
   <cfelse>
    	<cfadmin 
						action="schedule" 
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						scheduleAction="resume" 
						task="#trim(form.name)#"
						remoteClients="#request.getRemoteClients()#">
    </cfif>
                
		<!--- <cflocation url="#request.self#?action=#url.action#" addtoken="no"> --->
	</cfif>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>
<!--- 
Error Output--->
<cfset printError(error)>
<cfschedule action="list" returnVariable="tasks" >
<cfset task=struct()>
<cfloop query="tasks">
	<cfif hash((tasks.task)) EQ trim(url.task)>
		<cfset task=queryRow2Struct(tasks,tasks.currentrow)>
	</cfif>
</cfloop>

<cfset translateDateTime(task,"startdate","starttime","start")>
<cfset translateDateTime(task,"enddate","endtime","end")>




<cfoutput>
<table class="tbl" width="740">
<cfform onerror="customError" action="#request.self#?action=#url.action#&action2=#url.action2#&task=#url.task#" method="post">
<tr>
	<td class="tblHead" width="150"><label for="name">#stText.Schedule.Name#</label></td>
	<td class="tblContent" width="400"><input type="hidden" name="name" value="#trim(task.task)#">#task.task#</td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="url">#stText.Schedule.URL#</label></td>
	<td class="tblContent" width="400">
		
		<cfinput type="text" name="url" id="url" value="#task.url#" style="width:400px" required="yes" 
		message="#stText.Schedule.URLMissing#"><br><span class="comment">#stText.Schedule.NameDescEdit#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="port">#stText.Schedule.Port#</label></td>
	<td class="tblContent" width="400">
		<cfinput type="text" name="port" id="port" value="#task.port#" style="width:40px" required="no" validate="integer"><br><span class="comment">#stText.Schedule.PortDescription#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="timeout">#stText.Schedule.Timeout#</label></td>
	<td class="tblContent" width="400"><br>
		<cfinput type="text" name="timeout" id="timeout" value="#task.timeout#" style="width:40px" required="no" validate="integer">&nbsp;<br />
        <span class="comment">#stText.Schedule.TimeoutDescription#</span></td>
</tr>

<tr>
	<td class="tblHead" width="150"><label for="username">#stText.Schedule.Username#</label></td>
	<td class="tblContent" width="400">
		
		<cfinput type="text" name="username" id="username" value="#task.username#" style="width:150px" 
		required="no"><br><span class="comment">#stText.Schedule.UserNameDescription#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="password">#stText.Schedule.Password#</label></td>
	<td class="tblContent" width="400">
		
		<cfinput type="text" name="password" id="password" value="#task.password#" style="width:150px" 
		required="no"><br /><span class="comment">#stText.Schedule.PasswordDescription#</span></td>
</tr>
</table>
<br>
<table class="tbl" width="740">
<tr>
	<td colspan="2"><h2>#stText.Schedule.Proxy#</h2>#stText.Schedule.ProxyDesc#</td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="proxyserver">#stText.Schedule.Server#</label></td>
	<td class="tblContent" width="400">
    	<cfinput type="text" name="proxyserver" id="proxyserver" value="#task.proxyserver#" style="width:300px" 
		required="no">
        <br /><span class="comment">#stText.Schedule.ProxyServerDesc#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="proxyport">#stText.Schedule.Port#</label></td>
	<td class="tblContent" width="400"><cfinput type="text" name="proxyport" id="proxyport" value="#task.proxyport#" style="width:40px" validate="integer"
		required="no"><br>
        <span class="comment">#stText.Schedule.ProxyPort#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="proxyuser">#stText.Schedule.Username#</label></td>
	<td class="tblContent" width="400"><cfinput type="text" name="proxyuser" id="proxyuser" value="#task.proxyuser#" style="width:150px" 
		required="no"><br><span class="comment">#stText.Schedule.ProxyUserName#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="proxypassword">#stText.Schedule.Password#</label></td>
	<td class="tblContent" width="400"><cfinput type="text" name="proxypassword" id="proxypassword" value="#task.proxypassword#" style="width:150px" 
		required="no"><br><span class="comment">#stText.Schedule.ProxyPassword#</span></td>
</tr>
</table>
<br>

<table class="tbl" width="740">
<tr>
	<td colspan="2"><h2>#stText.Schedule.Output#</h2>#stText.Schedule.OutputDesc#</td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="publish">#stText.Schedule.Publish#</label></td>
	<td class="tblContent" width="400"><input type="checkbox" class="checkbox" name="publish" id="publish" value="yes" <cfif task.publish>checked</cfif>>
		<span class="comment">#stText.Schedule.StoreResponse#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="file">#stText.Schedule.File#</label></td>
	<td class="tblContent" width="400"><span class="comment">#stText.Schedule.FileDescription#</span><br><cfinput type="text" name="file" id="file" value="#toFile(task.path,task.file)#" style="width:400px" 
		required="no"></td>
</tr>
<tr>
	<td class="tblHead" width="150"><label for="resolveurl">#stText.Schedule.Resolve_URL#</label></td>
	<td class="tblContent" width="400"><input type="checkbox" class="checkbox" name="resolveurl" id="resolveurl" value="yes" <cfif task.resolveurl>checked</cfif>>
		<span class="comment">#stText.Schedule.ResolveDescription#</span></td>
</tr>
</table>
<br>
<table class="tbl" width="740">
<tr>
	<td colspan="2"><h2>#stText.Schedule.ExecutionDate# <cfif isNumeric(task.interval)>(Every...)<cfelse>(#ucFirst(task.interval)#)</cfif></h2><cfif isNumeric(task.interval)>#stText.Schedule['ExecutionDescEvery']#<cfelse>#stText.Schedule['ExecutionDesc'& task.interval]#</cfif></td>
</tr>
<tr>
	<td colspan="2">
	<table class="tbl" border="0" cellpadding="0" cellspacing="0">
	<tr><cfset css="background-color:white;background: url('');">
		<td><input style="tbl#iif(task.interval EQ 'once','css',de(''))#" 
			type="submit" class="submit" name="interval" value="once">&nbsp;</td>
		<td>&nbsp;<input style="tbl#iif(task.interval EQ 'daily','css',de(''))#" 
			type="submit" class="submit" name="interval" value="daily">&nbsp;</td>
		<td>&nbsp;<input style="tbl#iif(task.interval EQ 'weekly','css',de(''))#"  
			type="submit" class="submit" name="interval" value="weekly">&nbsp;</td>
		<td>&nbsp;<input style="tbl#iif(task.interval EQ 'monthly','css',de(''))#" 
			type="submit" class="submit" name="interval" value="monthly">&nbsp;</td>
		<td>&nbsp;<input style="tbl#iif(isNumeric(task.interval),'css',de(''))#" 
			type="submit" class="submit" name="interval" value="every ..."></td>
	</tr>
	</table>
	
	
	</td>
</tr>
<cfswitch expression="#task.interval#">
	<cfcase value="once">
	<input type="hidden" name="_interval" value="#task.interval#">
	<input type="hidden" name="end_hour" value="#task.end.hour#">
	<input type="hidden" name="end_minute" value="#task.end.minute#">
	<input type="hidden" name="end_second" value="#task.end.second#">
	
	<input type="hidden" name="end_day" value="#task.end.day#">
	<input type="hidden" name="end_month" value="#task.end.month#">
	<input type="hidden" name="end_year" value="#task.end.year#">

<tr>
	<td class="tblHead" width="150">#stText.Schedule.ExecuteAt#</td>
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment"><label for="start_day">#stText.General.Day#</label></td>
			<td class="comment"><label for="start_month">#stText.General.Month#</label></td>
			<td class="comment"><label for="start_year">#stText.General.Year#</label></td>
			<td class="comment"></td>
			<td class="comment"><label for="start_hour">#stText.General.Hour#</label></td>
			<td class="comment"><label for="start_minute">#stText.General.Minute#</label></td>
			<td class="comment"><label for="start_second">#stText.General.second#</label></td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_day" id="start_day" value="#task.start.day#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_month" id="start_month" value="#task.start.month#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_year" id="start_year" value="#task.start.year#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
			<td><cfinput type="text" name="start_hour" id="start_hour" value="#task.start.hour#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_minute" id="start_minute" value="#task.start.minute#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_second" id="start_second" value="#task.start.second#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment">#stText.Schedule.ExecuteAtDesc#</span></td>
</tr>
	</cfcase>
	<cfcase value="daily,weekly,monthly">
	<input type="hidden" name="_interval" value="#task.interval#">
	<input type="hidden" name="end_hour" value="#task.end.hour#">
	<input type="hidden" name="end_minute" value="#task.end.minute#">
	<input type="hidden" name="end_second" value="#task.end.second#">
<tr>
	<td class="tblHead" width="150">#stText.Schedule.StartsAt#</td>
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment"><label for="start_day">#stText.General.Day#</label></td>
			<td class="comment"><label for="start_month">#stText.General.Month#</label></td>
			<td class="comment"><label for="start_year">#stText.General.Year#</label></td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_day" id="start_day" value="#task.start.day#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_month" id="start_month" value="#task.start.month#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_year" id="start_year" value="#task.start.year#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment">#stText.Schedule.StartsAtDesc#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.ExecutionTime#</td>	
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment"><label for="start_hour">#stText.General.Hour#<label></td>
			<td class="comment"><label for="start_minute">#stText.General.Minute#<label></td>
			<td class="comment"><label for="start_second">#stText.General.second#<label></td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_hour" id="start_hour" value="#task.start.hour#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_minute" id="start_minute" value="#task.start.minute#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_second" id="start_second" value="#task.start.second#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment">#stText.Schedule['ExecutionTimeDesc'& task.interval ]#</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.EndsAt#</td>
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment"><label for="end_day">#stText.General.Day#</label></td>
			<td class="comment"><label for="end_month">#stText.General.Month#</label></td>
			<td class="comment"><label for="end_year">#stText.General.Year#</label></td>
		</tr>
		<tr>
			<td><cfinput type="text" name="end_day" id="end_day" value="#task.end.day#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_month" id="end_month" value="#task.end.month#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_year" id="end_year" value="#task.end.year#" style="width:40px" required="no" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment">#stText.Schedule['EndsAtDesc'& task.interval ]#</span>
        </td>
</tr>
	</cfcase>
	<cfdefaultcase>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.StartDate#</td>
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment"><label for="start_day">#stText.General.Day#<label></td>
			<td class="comment"><label for="start_month">#stText.General.Month#<label></td>
			<td class="comment"><label for="start_year">#stText.General.Year#<label></td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_day" id="start_day" value="#task.start.day#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_month" id="start_month" value="#task.start.month#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_year" id="start_year" value="#task.start.year#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment">#stText.Schedule.StartDateDesc#</span>
         </td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.StartTime#</td>	
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment"><label for="start_hour">#stText.General.Hour#<label></td>
			<td class="comment"><label for="start_minute">#stText.General.Minute#<label></td>
			<td class="comment"><label for="start_second">#stText.General.second#<label></td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_hour" id="start_hour" value="#task.start.hour#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_minute" id="start_minute" value="#task.start.minute#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_second" id="start_second" value="#task.start.second#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment" style="color:red">#stText.Schedule.StartTimeDesc#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.EndDate#</td>
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment"><label for="end_day">#stText.General.Day#<label></td>
			<td class="comment"><label for="end_month">#stText.General.Month#<label></td>
			<td class="comment"><label for="end_year">#stText.General.Year#<label></td>
		</tr>
		<tr>
			<td><cfinput type="text" name="end_day" id="end_day" value="#task.end.day#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_month" id="end_month" value="#task.end.month#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_year" id="end_year" value="#task.end.year#" style="width:40px" required="no" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment">#stText.Schedule.endDateDesc#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.EndTime#</td>
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment"><label for="end_hour">#stText.General.Hour#</label></td>
			<td class="comment"><label for="end_minute">#stText.General.Minute#</label></td>
			<td class="comment"><label for="end_second">#stText.General.second#</label></td>
		</tr>
		<tr>
			<td><cfinput type="text" name="end_hour" id="end_hour" value="#task.end.hour#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_minute" id="end_minute" value="#task.end.minute#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_second" id="end_second" value="#task.end.second#" style="width:40px" required="no" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment" style="color:red">#stText.Schedule.endTimeDesc#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Interval#</td>
	<td class="tblContent" width="400">
	<cfset interval=toStructInterval(task.interval)>
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment"><label for="interval_hour">#stText.General.Hour#s</label></td>
			<td class="comment"><label for="interval_minute">#stText.General.Minute#s</label></td>
			<td class="comment"><label for="interval_second">#stText.General.Second#s</label></td>
		</tr>
		<tr>
			<td><cfinput type="text" name="interval_hour" id="interval_hour" value="#interval.hour#" style="width:40px" 
		required="no" validate="integer" 
		message="#stText.General.HourError#">&nbsp;</td>
			<td><cfinput type="text" name="interval_minute" id="interval_minute" value="#interval.minute#" style="width:40px" 
		required="no" validate="integer"
		message="#stText.General.MinuteError#">&nbsp;</td>
			<td><cfinput type="text" name="interval_second" id="interval_second" value="#interval.second#" style="width:40px" 
		required="no" validate="integer"
		message="#stText.General.SecondError#"></td>
		</tr>
		</table> <span class="comment">#stText.Schedule.IntervalDesc#</span></td>
</tr>


	</cfdefaultcase>
</cfswitch>


<tr>
	<td class="tblHead" width="150"><label for="paused">#stText.Schedule.paused#</label></td>	
	<td class="tblContent" width="400"><input type="checkbox" name="paused" id="paused" value="true"<cfif task.paused> checked="checked"</cfif> />
    <br /><span class="comment">#stText.Schedule.pauseDesc#</span></td>
</tr>

<tr>
	<td colspan="2" class="comment">#stText.Schedule.CurrentDateTime#
	#dateFormat(now(),'mm/dd/yyyy')# #timeFormat(now(),'hh:mm tt')#
	</td>
</tr>
<cfmodule template="remoteclients.cfm" colspan="2">
<tr>
	<td colspan="2">
		<input onClick="window.location='#request.self#?action=#url.action#';" type="button" class="button" name="cancel" value="#stText.Buttons.Cancel#">
		<input type="submit" class="submit" name="run" value="#stText.Buttons.Update#"></td>
</tr>
</cfform>
</table>
<br><br></cfoutput>