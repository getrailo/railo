<cfif not isDefined('session.filter')>
	<cfset session.filter.type="">
	<cfset session.filter.name="">
	<cfset session.filter.next="">
	<cfset session.filter.tries="">
</cfif>

<cfparam name="form.mainAction" default="none">
<cfparam name="session.taskRange" default="10">
<cfparam name="form.subAction" default="none">
<cfparam name="url.startrow" default="1">
<cfparam name="url.maxrow" default="100">

<cfparam name="error" default="#struct(message:"",detail:"")#">
<cfset error.message="">
<cfset stVeritfyMessages = StructNew()>
<cftry>
	<cfswitch expression="#form.mainAction#">
	<!--- Filter --->
		<cfcase value="#stText.Buttons.filter#">
			
			
			
			<cfset session.filter.type=trim(form.typeFilter)>
			<cfset session.filter.name=trim(form.nameFilter)>
			<cfset session.filter.next=trim(form.nextFilter)>
			<cfset session.filter.tries=trim(form.triesFilter)>
		</cfcase>
	<!--- EXECUTE --->
		<cfcase value="#stText.Buttons.Execute#">
			<cfset data.ids=toArrayFromForm("id")>
			<cfset data.rows=toArrayFromForm("row")>
			<cfloop index="idx" from="1" to="#arrayLen(data.ids)#">
				<cfif isDefined("data.rows[#idx#]") and data.ids[idx] NEQ "">
					<cftry>
						<cfadmin 
							action="executeSpoolerTask"
							type="#request.adminType#"
							password="#session["password"&request.adminType]#"
							
							id="#data.ids[idx]#">
							<cfset stVeritfyMessages[data.ids[idx]].Label = "OK">
						<cfcatch>
							<cfset stVeritfyMessages[data.ids[idx]].Label = "Error">
							<cfset stVeritfyMessages[data.ids[idx]].message = cfcatch.message>
						</cfcatch>
					</cftry>
				</cfif>
			</cfloop>
		</cfcase>
	<!--- DELETE --->
		<cfcase value="#stText.Buttons.Delete#">
			<cfset data.ids=toArrayFromForm("id")>
			<cfset data.rows=toArrayFromForm("row")>
			
			<cfloop index="idx" from="1" to="#arrayLen(data.ids)#">
				<cfif isDefined("data.rows[#idx#]") and data.ids[idx] NEQ "">
					<cfadmin 
						action="removeSpoolerTask"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#"
						
						id="#data.ids[idx]#">
				</cfif>
			</cfloop>
			<cfif cgi.request_method EQ "POST" and error.message EQ "">
				<cflocation url="#request.self#?action=#url.action#" addtoken="no">
			</cfif>
		</cfcase>
	<!--- DELETE ALL --->
		<cfcase value="#stText.Buttons.DeleteAll#">
			
					<cfadmin 
						action="removeAllSpoolerTask"
						type="#request.adminType#"
						password="#session["password"&request.adminType]#">

			<cfif cgi.request_method EQ "POST" and error.message EQ "">
				<cflocation url="#request.self#?action=#url.action#" addtoken="no">
			</cfif>
		</cfcase>
	</cfswitch>
	<cfcatch>
		<cfset error.message=cfcatch.message>
		<cfset error.detail=cfcatch.Detail>
	</cfcatch>
</cftry>





<!--- 
Error Output--->
<cfif error.message NEQ "">
<cfoutput><span class="CheckError">
#error.message#<br>
#error.detail#
</span><br><br></cfoutput>
</cfif>


<cfparam name="url.id" default="0">

<cfadmin 
    action="getSpoolerTasks"
    type="#request.adminType#"
    password="#session["password"&request.adminType]#"
    startrow="#url.startrow#"
    maxrow="#url.maxrow#"
    result="result"
    returnVariable="tasks">

<cffunction name="addZeros" returntype="string" output="false">
	<cfargument name="nbr" required="yes" type="numeric">
	
	<cfif arguments.nbr GT 9>
		<cfreturn  arguments.nbr>
	<cfelse>
		<cfreturn  "0"&arguments.nbr>
	</cfif>
</cffunction>

<cffunction name="toTime" returntype="string" output="false">
	<cfargument name="date" required="yes" type="date">
	<cfargument name="dspMinus" required="no" type="boolean" default="false">
	
	<cfset seconds=DateDiff("s",now(),arguments.date)>
	<cfset str="">
	<cfif seconds LT 0>
		<cfset s=seconds>
		<cfset seconds=(s-s)-s>
		<cfif dspMinus><cfset str="- "></cfif>
	</cfif>
	
	<cfset h=int(seconds/3600)>
	<cfset m=int(seconds/60)-h*60>
	<cfset s=(seconds-h*3600)-m*60>
	<cfreturn "#str##addZeros(h)#:#addZeros(m)#:#addZeros(s)#">
</cffunction>


<cffunction name="inMinutes" returntype="string" output="false">
	<cfargument name="date" required="yes" type="date">
	<cfargument name="dspMinus" required="no" type="boolean" default="false">
	
	<cfreturn DateDiff("m",now(),arguments.date)>
	
	
	
</cffunction>

<cffunction name="doFilter" returntype="string" output="false">
	<cfargument name="filter" required="yes" type="string">
	<cfargument name="value" required="yes" type="string">
	<cfargument name="exact" required="no" type="boolean" default="false">
	
	<cfset arguments.filter=replace(arguments.filter,'*','',"all")>
	<cfif not len(filter)>
		<cfreturn true>
	</cfif>
	
	
	<cfif exact>
		<cfreturn filter EQ value>
	<cfelse>
		<cfreturn FindNoCase(filter,value)>
	</cfif>
	
	
</cffunction>
				
						
<cfset querySort(tasks,"lastExecution","desc")>
			


<!--- 0 records ---->
<cfif result.open+result.closed EQ 0>
<cfoutput><b>#stText.remote.ot.noOt#</b></cfoutput>

<!--- DETAIL ---->
<cfelseif url.id NEQ 0>
<cfoutput query="tasks">
<cfif url.id EQ tasks.id>

<cfset css=iif(not tasks.closed,de('Green'),de('Red'))>
#replace(replace(stText.remote.ot.detailDesc[css],'<tries>',tasks.tries),'<triesleft>',tasks.triesMax-tasks.tries)#
<table class="tbl" width="600">

<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
<tr>
	<td colspan="4"></td>
</tr>
<tr>
	<td colspan="4"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>

	<!--- MUST wieso geht hier der direkte aufruf nicht! ---><cfset detail=tasks.detail>
	<cfif isDefined("detail.label")>
	<tr>
		<td width="100" class="tblHead" nowrap>x#stText.remote.ot.name#</td>
		<td class="tblContent#css#" nowrap>#detail.label#</td>
	</tr>
	</cfif>
	<cfif isDefined("detail.url")>
	<tr>
		<td width="100" class="tblHead" nowrap>#stText.remote.ot.url#</td>
		<td class="tblContent#css#" nowrap>#detail.url#</td>
	</tr>
	</cfif>
	<cfif isDefined("detail.action")>
	<tr>
		<td width="100" class="tblHead" nowrap>#stText.remote.ot.action#</td>
		<td class="tblContent#css#" nowrap>#detail.action#</td>
	</tr>
	</cfif>
	<tr>
		<td width="100" class="tblHead" nowrap>#stText.remote.ot.lastExecution#<br /><span class="comment" style="color:##DFE9F6">(mm/dd/yyyy HH:mm:ss)</span></td>
		<td class="tblContent#css#" title="" nowrap>#dateFormat(tasks.lastExecution,'mm/dd/yyyy')# #timeFormat(tasks.lastExecution,'HH:mm:ss')#</td>
	</tr>
	<tr>
		<td width="100" class="tblHead" nowrap>#stText.remote.ot.nextExecution#<br /><span class="comment" style="color:##DFE9F6">(mm/dd/yyyy HH:mm:ss)</span></td>
		<td class="tblContent#css#" title="" nowrap><cfif tasks.closed> <center>-</center> <cfelse>
		#dateFormat(tasks.nextExecution,'mm/dd/yyyy')# #timeFormat(tasks.nextExecution,'HH:mm:ss')#</cfif></td>
	</tr>
	
	<tr>
		<td width="100" class="tblHead" nowrap>#stText.remote.ot.tries#<br /></td>
		<td class="tblContent#css#" title="" nowrap>#tasks.tries#</td>
	</tr>
	<tr>
		<td width="100" class="tblHead" nowrap>#stText.remote.ot.triesLeft#<br /></td>
		<cfset tmp=tasks.triesMax-tasks.tries>
		<cfif tmp LT 0><cfset tmp=0></cfif>
		<td class="tblContent#css#" title="" nowrap>#tmp#</td>
	</tr>
	<tr>
		<td width="100" class="tblHead" nowrap>#stText.remote.ot.state#<br /></td>
		<td class="tblContent#css#" title="" nowrap>#iif(tasks.closed,de("Close"),de("Open"))#</td>
	</tr>
	
	<tr>
		<td colspan="2"></td>
	</tr>
	<cfloop collection="#detail#" item="key">
		<cfif ListFindNoCase("label,url,action",key)>
			<cfcontinue>
		</cfif>
	<tr>
		<td width="100" class="tblHead" nowrap>#(tasks.type)# #key#</td>
		<td class="tblContent#css#">#replace(detail[key],'<','&lt;','all')#</td>
	</tr>
	</cfloop>
	
	<tr>
		<td colspan="2"><br /><b>#stText.remote.ot.error#</b></td>
	</tr>

	<tr>
		<td width="100" class="tblHead" nowrap>#stText.remote.ot.exetime#<br /><span class="comment" style="color:##DFE9F6">(mm/dd/yyyy HH:mm:ss)</span></td>
		<td class="tblHead" title="" nowrap>#stText.remote.ot.error#</td>
	</tr>
	<cfset exp=tasks.exceptions>
	<cfloop collection="#exp#" item="i">
	<tr>
		<td width="100" class="tblContent#css#" nowrap>
			<cfif isDate(exp[i].time) and year(exp[i].time) NEQ 1970>
			#dateFormat(exp[i].time,'mm/dd/yyyy')# #timeFormat(exp[i].time,'HH:mm:ss')#<cfelse>-</cfif><br /></td>
		<td class="tblContent#css#" title="">
        	<cfif structKeyExists(exp[i],"message")><b>#exp[i].message#</b></cfif>
			<cfif structKeyExists(exp[i],"stacktrace")><br /><span class="comment"> #replace(exp[i].stacktrace,chr(13),'<br />','all')#</span></cfif>
        </td>
	</tr>
	</cfloop>
	

<tr>
	<td colspan="2">
		<input type="hidden" class="checkbox" name="row_#tasks.currentrow#" value="#tasks.currentrow#">
		<input type="hidden" name="id_#tasks.currentrow#" value="#tasks.id#">
		
		<input onClick="window.location='#request.self#?action=#url.action#';" type="button" class="button" name="canel" value="#stText.Buttons.Cancel#">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Execute#">
		<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Delete#">
		
		</td>
</tr>
</cfform>
</table>
</cfif>
</cfoutput>

<!--- List ---->
<cfelse>
<cfoutput>
#stText.remote.ot.overviewDesc#
</cfoutput>

<cfset types=struct()>
<cfsilent>
<cfloop query="tasks">
	<cfset types[tasks.type]="">
</cfloop>
<cfset types=StructKeyArray(types)>
</cfsilent>

<script language="javascript">
function selectAll(field) {
	var form=field.form;
	var str="";
	for(var key in form.elements){
		if((form.elements[key] && ""+form.elements[key].name).indexOf("row_")==0){
			form.elements[key].checked=field.checked;
		}
	}
}
</script>

<table class="tbl" width="600">
<tr>
	<td colspan="4"></td>
</tr>
<tr>
	<td colspan="4"><cfmodule template="tp.cfm"  width="1" height="1"></td>
</tr>
<cfform onerror="customError" action="#request.self#?action=#url.action#" method="post">
	<cfoutput>

	<!---
	
	FILTER take out temporary
	
	
	<tr>
		<td width="200"></td>
		<td class="tblHead" nowrap>
		<select name="typeFilter" style="width:120px">
		<option value="" <cfif not len(session.filter.type)> selected</cfif>>- all -</option>
		<cfloop array="#types#" index="i">
			<option <cfif i EQ session.filter.type> selected</cfif>>#i#</option>
		</cfloop>
		</select>
		
		</td>
		<td width="250" class="tblHead" nowrap><input type="text" name="nameFilter" style="width:250px" value="#session.filter.name#" /></td>
		<td width="100" class="tblHead" nowrap><input type="text" name="nextFilter" style="width:90px" value="#session.filter.next#" /></td>
		<td width="100" class="tblHead" nowrap><input type="text" name="triesFilter" style="width:90px" value="#session.filter.tries#" /></td>
		<td class="tblHead" nowrap><input type="submit" class="submit" name="mainAction" value="#stText.Buttons.filter#"></td>
	</tr>
	--->
	<tr>
		<td width="380" colspan="7" align="right"></td>
	</tr>
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><input type="checkbox" class="checkbox" name="row" onclick="selectAll(this)"></td>
			<td></td>
		</tr>
		</table>
		</td>
		<td class="tblHead" nowrap>#stText.remote.ot.type#</td>
		<td width="250" class="tblHead" nowrap>#stText.remote.ot.name#</td>
		<td width="100" class="tblHead" nowrap>#stText.remote.ot.nextExecution#<!---<br /><span class="comment" style="color:##DFE9F6">(mm/dd/yyyy HH:mm:ss)</span>---></td>
		<td width="100" class="tblHead" nowrap>#stText.remote.ot.tries#</td>
		<td class="tblHead" nowrap>#stText.Settings.DBCheck#</td>
	</tr>

<cfsavecontent variable="browse">
<cfset to=url.startrow+url.maxrow-1>

<cfif to GT result.open+result.closed>
	<cfset to=result.open+result.closed>
<cfelse>
	
</cfif>
	<tr>
		<td>&nbsp;</td>
		<td class="tblHead" colspan="5" align="center" nowrap>
        	<table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
            	<td width="100">
                <cfif url.startrow GT 1><a href="#request.self#?action=#url.action#&startrow=#url.startrow-url.maxrow#" class="comment"><cfmodule template="img.cfm" src="arrow-left.gif" border="0" hspace="4">#stText.remote.previous#</a><cfelse>&nbsp;</cfif>
                
                </td>
            	<td align="center"><span class="comment">#url.startrow# #stText.remote.to# #to# #stText.remote.from# #result.open+result.closed#</span></td>
            	<td width="100" align="right">
                <cfif to LT result.open+result.closed><a href="#request.self#?action=#url.action#&startrow=#url.startrow+url.maxrow#" class="comment">#stText.remote.next#<cfmodule template="img.cfm" src="arrow-right.gif" border="0" hspace="4"></a><cfelse>&nbsp;</cfif>
                </td>
            </tr>
            </table>
        </td>
	</tr>
</cfsavecontent> 
    
    #browse#



<cfloop query="tasks">
		<cfset css="">
		<cfset next=inMinutes(tasks.nextExecution,true)>
		<cfset closed=tasks.closed NEQ "" and tasks.closed>
		<cfif closed><cfset next='-'></cfif>
		<!--- filter 
			doFilter(session.filter.type,tasks.type,false)
			and
			doFilter(session.filter.name,tasks.name,false)
			and
			doFilter(session.filter.next,next,true)
			and
			doFilter(session.filter.tries,tasks.tries,true)--->
		<cfif true>
			
		
		<cfif tasks.closed NEQ ""><cfset css=iif(not tasks.closed,de('Green'),de('Red'))></cfif>
		<!--- and now display --->
	<tr>
		<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><input type="checkbox" class="checkbox" name="row_#tasks.currentrow#" value="#tasks.currentrow#"></td>
			<td><a href="#request.self#?action=#url.action#&action2=edit&id=#tasks.id#">
			<cfmodule template="img.cfm" src="edit.png" hspace="2" border="0"></a></td>
		</tr>
		</table>
		</td>
		<td class="tblContent#css#" nowrap><input type="hidden" name="id_#tasks.currentrow#" value="#tasks.id#"><b>#tasks.type#</b></td>
		<td class="tblContent#css#" nowrap><b>#tasks.name#</b></td>
		<!---
		<td class="tblContent#css#" nowrap align="center">
			<cfif isDate(tasks.lastExecution) and year(tasks.lastExecution) NEQ 1970>
				<!--- #dateFormat(tasks.lastExecution,'mm/dd/yyyy')# #timeFormat(tasks.lastExecution,'HH:mm:ss')#--->
				#toTime(tasks.lastExecution)#
			<cfelse>
				-
			</cfif>
		</td>
		--->
		<td class="tblContent#css#" title="" nowrap align="center">
			<cfif closed> 
				<center>-</center>
			<cfelse>
				#lsDateFormat(tasks.nextExecution)# #timeFormat(tasks.nextExecution,'HH:mm:ss')#
			</cfif>
		</td>
		<td class="tblContent#css#" title="" nowrap align="center">
			#tasks.tries#
		</td>
		
			<td class="tblContent#css#" nowrap valign="middle" align="center">
				<cfif StructKeyExists(stVeritfyMessages, tasks.id)>
					<cfif stVeritfyMessages[tasks.id].label eq "OK">
						<span class="CheckOk">#stVeritfyMessages[tasks.id].label#</span>
					<cfelse>
						<span class="CheckError" title="#stVeritfyMessages[tasks.id].message##Chr(13)#">#stVeritfyMessages[tasks.id].label#</span>
						&nbsp;<cfmodule template="img.cfm" src="red-info.gif" 
							width="9" 
							height="9" 
							border="0" 
							title="#stVeritfyMessages[tasks.id].message##Chr(13)#">
					</cfif>
				<cfelse>
					&nbsp;				
				</cfif>
			</td>
	</tr></cfif>
    
</cfloop>

 
    
    #browse#
	<tr>
		<td colspan="6">
		 <table border="0" cellpadding="0" cellspacing="0">
		 <tr>
			<td><cfmodule template="tp.cfm"  width="10" height="1"></td>		
			<td><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="20"></td>
			<td></td>
		 </tr>
		 <tr>
			<td></td>
			<td valign="top"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="1" height="14"><cfmodule template="img.cfm" src="#ad#-bgcolor.gif" width="36" height="1"></td>
			<td>&nbsp;
			<cfoutput>
			<input type="reset" class="reset" name="cancel" value="#stText.Buttons.Cancel#">
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Execute#">
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.Delete#">
			<input type="submit" class="submit" name="mainAction" value="#stText.Buttons.DeleteAll#">
			</cfoutput>
			</td>	
		</tr>
		 </table>
		 </td>
	</tr>
</cfoutput>
</cfform>
</table>




</cfif>


