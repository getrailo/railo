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
	if(IsDate(task[dateName])) {
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
	if(IsDate(task[timeName])) {
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

<!--- 

<style type="text/css">
 form {padding:0px;}
 input, select, textarea
   { font-size:13px; font-family:Verdana,sans-serif; font-weight:bold; }
 input, select, textarea
   { background-color:#999999;color:#ffffff;border:1px solid #DDDDDD; }
 .Bereich, .Feld
   { background-color:#FFFFAA; width:300px; border:6px solid #DDDDDD; }
 .Auswahl
   { background-color:#DDFFFF; width:300px; border:6px solid #DDDDDD; }
 .Check, .Radio
   { background-color:#DDFFFF; border:1px solid #DDDDDD; }
 .Button
   { background-color:#AAAAAA; color:#FFFFFF; width:200px; border:6px solid #DDDDDD; }
</style> --->



<cfoutput>
<table class="tbl">
<cfform action="#request.self#?action=#url.action#&action2=#url.action2#&task=#url.task#" method="post">
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Name#</td>
	<td class="tblContent" width="400"><input type="hidden" name="name" value="#trim(task.task)#">#task.task#</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.URL#</td>
	<td class="tblContent" width="400">
		
		<cfinput type="text" name="url" value="#task.url#" style="width:400px" required="yes" 
		message="#stText.Schedule.URLMissing#"><br><span class="comment">#stText.Schedule.NameDescEdit#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Port#</td>
	<td class="tblContent" width="400">
		<cfinput type="text" name="port" value="#task.port#" style="width:40px" required="no" validate="integer"><br><span class="comment">#stText.Schedule.PortDescription#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Timeout#</td>
	<td class="tblContent" width="400"><br>
		<cfinput type="text" name="timeout" value="#task.timeout#" style="width:40px" required="no" validate="integer">&nbsp;<br />
        <span class="comment">#stText.Schedule.TimeoutDescription#</span></td>
</tr>

<tr>
	<td class="tblHead" width="150">#stText.Schedule.Username#</td>
	<td class="tblContent" width="400">
		
		<cfinput type="text" name="username" value="#task.username#" style="width:150px" 
		required="no"><br><span class="comment">#stText.Schedule.UserNameDescription#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Password#</td>
	<td class="tblContent" width="400">
		
		<cfinput type="text" name="password" value="#task.password#" style="width:150px" 
		required="no"><br /><span class="comment">#stText.Schedule.PasswordDescription#</span></td>
</tr>
</table>
<br><h2>#stText.Schedule.Proxy#</h2>
<table class="tbl">
<tr>
	<td colspan="2">#stText.Schedule.ProxyDesc#</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Server#</td>
	<td class="tblContent" width="400">
    	<cfinput type="text" name="proxyserver" value="#task.proxyserver#" style="width:300px" 
		required="no">
        <br /><span class="comment">#stText.Schedule.ProxyServerDesc#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Port#</td>
	<td class="tblContent" width="400"><cfinput type="text" name="proxyport" value="#task.proxyport#" style="width:40px" validate="integer"
		required="no"><br>
        <span class="comment">#stText.Schedule.ProxyPort#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Username#</td>
	<td class="tblContent" width="400"><cfinput type="text" name="proxyuser" value="#task.proxyuser#" style="width:150px" 
		required="no"><br><span class="comment">#stText.Schedule.ProxyUserName#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Password#</td>
	<td class="tblContent" width="400"><cfinput type="text" name="proxypassword" value="#task.proxypassword#" style="width:150px" 
		required="no"><br><span class="comment">#stText.Schedule.ProxyPassword#</span></td>
</tr>
</table>
<br><h2>#stText.Schedule.Output#</h2>

<table class="tbl">
<tr>
	<td colspan="2">#stText.Schedule.OutputDesc#</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Publish#</td>
	<td class="tblContent" width="400"><input type="checkbox" class="checkbox" name="publish" value="yes" <cfif task.publish>checked</cfif>>
		<span class="comment">#stText.Schedule.StoreResponse#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.File#</td>
	<td class="tblContent" width="400"><span class="comment">#stText.Schedule.FileDescription#</span><br><cfinput type="text" name="file" value="#toFile(task.path,task.file)#" style="width:400px" 
		required="no"></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.Resolve_URL#</td>
	<td class="tblContent" width="400"><input type="checkbox" class="checkbox" name="resolveurl" value="yes" <cfif task.resolveurl>checked</cfif>>
		<span class="comment">#stText.Schedule.ResolveDescription#</span></td>
</tr>
</table>
<br><h2>#stText.Schedule.ExecutionDate# <cfif isNumeric(task.interval)>(Every...)<cfelse>(#ucFirst(task.interval)#)</cfif></h2>
<table class="tbl">
<tr>
	<td colspan="2"><cfif isNumeric(task.interval)>#stText.Schedule['ExecutionDescEvery']#<cfelse>#stText.Schedule['ExecutionDesc'& task.interval]#</cfif></td>
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
			<td class="comment">#stText.General.Day#</td>
			<td class="comment">#stText.General.Month#</td>
			<td class="comment">#stText.General.Year#</td>
			<td class="comment"></td>
			<td class="comment">#stText.General.Hour#</td>
			<td class="comment">#stText.General.Minute#</td>
			<td class="comment">#stText.General.second#</td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_day" value="#task.start.day#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_month" value="#task.start.month#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_year" value="#task.start.year#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td>&nbsp;&nbsp;-&nbsp;&nbsp;</td>
			<td><cfinput type="text" name="start_hour" value="#task.start.hour#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_minute" value="#task.start.minute#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_second" value="#task.start.second#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
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
			<td class="comment">#stText.General.Day#</td>
			<td class="comment">#stText.General.Month#</td>
			<td class="comment">#stText.General.Year#</td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_day" value="#task.start.day#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_month" value="#task.start.month#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_year" value="#task.start.year#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment">#stText.Schedule.StartsAtDesc#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.ExecutionTime#</td>	
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment">#stText.General.Hour#</td>
			<td class="comment">#stText.General.Minute#</td>
			<td class="comment">#stText.General.second#</td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_hour" value="#task.start.hour#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_minute" value="#task.start.minute#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_second" value="#task.start.second#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment">#stText.Schedule['ExecutionTimeDesc'& task.interval ]#</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.EndsAt#</td>
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment">#stText.General.Day#</td>
			<td class="comment">#stText.General.Month#</td>
			<td class="comment">#stText.General.Year#</td>
		</tr>
		<tr>
			<td><cfinput type="text" name="end_day" value="#task.end.day#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_month" value="#task.end.month#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_year" value="#task.end.year#" style="width:40px" required="no" validate="integer">&nbsp;</td>
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
			<td class="comment">#stText.General.Day#</td>
			<td class="comment">#stText.General.Month#</td>
			<td class="comment">#stText.General.Year#</td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_day" value="#task.start.day#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_month" value="#task.start.month#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_year" value="#task.start.year#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
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
			<td class="comment">#stText.General.Hour#</td>
			<td class="comment">#stText.General.Minute#</td>
			<td class="comment">#stText.General.second#</td>
		</tr>
		<tr>
			<td><cfinput type="text" name="start_hour" value="#task.start.hour#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_minute" value="#task.start.minute#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="start_second" value="#task.start.second#" style="width:40px" required="yes" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment" style="color:red">#stText.Schedule.StartTimeDesc#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.EndDate#</td>
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment">#stText.General.Day#</td>
			<td class="comment">#stText.General.Month#</td>
			<td class="comment">#stText.General.Year#</td>
		</tr>
		<tr>
			<td><cfinput type="text" name="end_day" value="#task.end.day#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_month" value="#task.end.month#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_year" value="#task.end.year#" style="width:40px" required="no" validate="integer">&nbsp;</td>
		</tr>
		</table>
        <span class="comment">#stText.Schedule.endDateDesc#</span></td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.Schedule.EndTime#</td>
	<td class="tblContent" width="400">
		<table class="tbl" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="comment">#stText.General.Hour#</td>
			<td class="comment">#stText.General.Minute#</td>
			<td class="comment">#stText.General.second#</td>
		</tr>
		<tr>
			<td><cfinput type="text" name="end_hour" value="#task.end.hour#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_minute" value="#task.end.minute#" style="width:40px" required="no" validate="integer">&nbsp;</td>
			<td><cfinput type="text" name="end_second" value="#task.end.second#" style="width:40px" required="no" validate="integer">&nbsp;</td>
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
			<td class="comment">#stText.General.Hour#s</td>
			<td class="comment">#stText.General.Minute#s</td>
			<td class="comment">#stText.General.Second#s</td>
		</tr>
		<tr>
			<td><cfinput type="text" name="interval_hour" value="#interval.hour#" style="width:40px" 
		required="no" validate="integer" 
		message="#stText.General.HourError#">&nbsp;</td>
			<td><cfinput type="text" name="interval_minute" value="#interval.minute#" style="width:40px" 
		required="no" validate="integer"
		message="#stText.General.MinuteError#">&nbsp;</td>
			<td><cfinput type="text" name="interval_second" value="#interval.second#" style="width:40px" 
		required="no" validate="integer"
		message="#stText.General.SecondError#"></td>
		</tr>
		</table> <span class="comment">#stText.Schedule.IntervalDesc#</span></td>
</tr>


	</cfdefaultcase>
</cfswitch>


<tr>
	<td class="tblHead" width="150">#stText.Schedule.paused#</td>	
	<td class="tblContent" width="400"><input type="checkbox" name="paused" value="true"<cfif task.paused> checked="checked"</cfif> />
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
		<input onClick="window.location='#request.self#?action=#url.action#';" type="button" class="button" name="canel" value="#stText.Buttons.Cancel#">
		<input type="submit" class="submit" name="run" value="#stText.Buttons.Update#"></td>
</tr>
</cfform>
</table>
<br><br></cfoutput>