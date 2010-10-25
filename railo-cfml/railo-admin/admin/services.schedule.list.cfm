<cfset error.message="">
<cfset error.detail="">

<!--- 
Defaults --->
<cfparam name="form.mainAction" default="none">
<cfparam name="form.subAction" default="none">
<cfparam name="error" default="#struct(message:"",detail:"")#">
<cfset error.message="">

<cftry>
	<cfswitch expression="#form.mainAction#">
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
		if((""+form.elements[key].name).indexOf("row_")==0){
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

<!--- 
List --->
<cfif tasks.recordcount>
<cfoutput><h2>#stText.Schedule.Detail#</h2>
#stText.Schedule.DetailDescription#</cfoutput>


<table class="tbl" width="600">
<tr>
	<td colspan="4"></td>
</tr>
<tr>
	<td colspan="4"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform action="#request.self#?action=#url.action#" method="post">
	<cfoutput>
	<tr>
		<td width="20"><input type="checkbox" class="checkbox" name="rro" onclick="selectAll(this)"></td>
		<td width="140" class="tblHead" nowrap>#stText.Schedule.Name#</td>
		<td width="160" class="tblHead" nowrap>#stText.Schedule.Interval#</td>
		<td width="170" class="tblHead" nowrap>#stText.Schedule.URL#</td>
		<td width="60" class="tblHead" nowrap>#stText.Schedule.paused#</td>
	</tr>
	
	<cfloop query="tasks">
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
		<td class="tblContent#css#" nowrap>
			<cfif isNumeric(tasks.interval)>
				<cfset _int=toStructInterval(tasks.interval)>
				#stText.Schedule.Every# (hh:mm:ss) #two(_int.hour)#:#two(_int.minute)#:#two(_int.second)#
			<cfelse>#tasks.interval#</cfif></td>
		<td class="tblContent#css#" title="#tasks.url#" nowrap>#cut(tasks.url,50)#</td>
		<td class="tblContent#css#"  nowrap>#YesNoFormat(tasks.paused)#</td>
	</tr>
</cfloop>

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
<h2>#stText.Schedule.CreateTask#</h2>
<table class="tbl" width="600">
<tr>
	<td colspan="2"></td>
</tr>
<tr>
	<td colspan="2"><cfmodule template="tp.cfm"  width="1" height="1"></td>
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