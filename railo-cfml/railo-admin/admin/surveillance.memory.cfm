<cfparam name="session.timerange" default="60" type="numeric">
<cfif StructKeyExists(form,"range")>
	<cfset session.timerange=form.range>
</cfif>
<cfset _range=session.timerange>

<cfparam name="session.memoryShow" default="PAR_EDEN_SPACE,PAR_SURVIVOR_SPACE,CMS_PERM_GEN" type="string">
<cfif StructKeyExists(form,"mainAction")>
	<cfset session.memoryShow=StructKeyExists(form,"show")?form.show:"">
</cfif>

<cfscript>

// define time slot
	timeSlot=1;
	// a day
	if(_range GTE 24*60)
		timeSlot=5*60;// 5 minutes
	
	// a hour
	else if(_range GTE 60)
		timeSlot=60;// 1 minutes
	
	// 10 minutes
	else if(_range GTE 10)
		timeSlot=20;// 20 seconds
	
	// 5 minutes
	else if(_range GTE 5)
		timeSlot=10;// 10 seconds


</cfscript>



<cfSurveillance action="memory" from="#DateAdd("n",-_range,now())#" returnVariable="data" slotSize="#timeSlot#">

<cfloop query="data"><cfset QuerySetCell(data,"TIME",TimeFormat(data.time,"HH:mm:ss"),data.currentrow)></cfloop>

<cfset sctRanges={"1":"1 minute","5":"5 minutes","15":"15 minutes","30":"30 minutes","60":"1 hour","120":"2 hours","240":"4 hours","720":"12 hours","1440":"1 day"}>

<cfset ranges=query(
	label:["1 minute","5 minutes","15 minutes","30 minutes","1 hour","2 hours","4 hours","12 hours","1 day"],
	value:[1,5,15,30,60,120,240,720,1440]
)>



<cfset types.heap="Heap">
<cfset types.non_heap="None-Heap">


<cfsavecontent variable="desc.heap">

The JVM (Java Virtual Machine) has a heap that is the runtime data area from which memory for all objects are allocated.
<!--- The heap size may be configured with the following VM options:
<li>Xmx{size} - to set the maximum Java heap size
<li>Xms{size} - to set the initial Java heap size--->
</cfsavecontent>
<cfsavecontent variable="desc.non_heap">
Also, the JVM has memory other than the heap, referred to as non-heap memory. It stores al cfc/cfm templates, java classes, interned Strings and meta-data.<!---<br />

The abnormal growth of non-heap memory mostly indicate that Railo has to load many cfc/cfm templates.

If the application indeed needs that much of non-heap memory and the default maximum size of 64 Mb is not enough, you may enlarge the maximum size with the help of -XX:MaxPermSize VM option. For example, -XX:MaxPermSize=128m sets the size of 128 Mb.
--->
</cfsavecontent>



<cfset pool["Par Eden Space"]="The pool from which memory is initially allocated for most objects.">
<cfset pool["Par Survivor Space"]="The pool containing objects that have survived the garbage collection of the Eden space.">
<cfset pool["CMS Old Gen"]="The pool containing objects that have existed for some time in the survivor space.">
<cfset pool["CMS Perm Gen"]="The pool containing all the reflective data of the virtual machine itself, such as class and method objects. With Java VMs that use class data sharing, this generation is divided into read-only and read-write areas.">
<cfset pool["Code Cache"]="The HotSpot Java VM also includes a code cache, containing memory that is used for compilation and storage of native code.">

<cfset keys["Par Eden Space"]="PAR_EDEN_SPACE">
<cfset keys["Par Survivor Space"]="PAR_SURVIVOR_SPACE">
<cfset keys["CMS Old Gen"]="CMS_OLD_GEN">
<cfset keys["CMS Perm Gen"]="CMS_PERM_GEN">
<cfset keys["Code Cache"]="CODE_CACHE">




<cffunction name="showChart">
	<cfargument name="qry" required="yes" type="query">
	<cfargument name="column" required="yes" type="string">
    <cfchart 
    	showlegend="yes"  markersize="1" format="png"  show3d="no" scalefrom="-0.001" scaleto="1" showXGridlines=1 showxlabel="0" showmarkers="false"
        chartWidth="550" chartHeight="90" labelFormat="percent">  
    	<cfchartseries type="line"  query="qry"  itemcolumn="Time" colorlist="##9c0000" valuecolumn="#column#"></cfchartseries> 
	</cfchart> 
</cffunction>


<cffunction name="printMemory" output="yes">
	<cfargument name="name" type="string" required="yes">
	<cfset var usage=getmemoryUsage(name)>
    <cfset height=6>
    <cfset width=70>
    
<cfsavecontent variable="local.body" trim="true">
<cfloop query="usage">
	<cfset pused=int(100/usage.max*usage.used)>
	<cfset pfree=100-pused> 
     <cfif session.memoryShow NEQ "all" and !ListContainsNoCase(session.memoryShow,keys[usage.name])>
     	<cfcontinue>
     </cfif>
<tr>
	<td colspan="2" class="tblHead" width="100">
    <b>#usage.name# (last #sctRanges[_range]#)</b>
	
	
	<cfif StructKeyExists(pool,usage.name)><br /><span class="comment">#pool[usage.name]#</span></cfif>
    </td>
</tr>       
<tr>
	<td class="tblContent" width="100">
    <cfset showChart(data,keys[usage.name])><br />
                <cfmodule template="tp.cfm" height="4" width="1" />
    </td>
	<td class="tblContent" width="130">
                Max: #int(usage.max/1024)#kb<br />
                Free: #int((usage.max-usage.used)/1024)#kb (#pfree#%)<br />
                Used: #int(usage.used/1024)#kb (#pused#%)
                </td>
</tr>
</cfloop>
</cfsavecontent>


<cfif len(body)>
<h2>#types[name]#</h2>
#desc[name]#
<table class="tbl" width="740">
#body#
</table>
</cfif>
</cffunction>

<cfset total=query(
	name:["Total"],
	type:[""],
	used:[server.java.totalMemory-server.java.freeMemory],
	max:[server.java.totalMemory],
	init:[0]
)>
<cfoutput>
<cfif request.admintype EQ "server">

<cfsavecontent variable="memoryInfo">

<cfset printMemory(("heap"))>
<cfset printMemory(("non_heap"))>
<!---
<tr>
	<td class="tblHead" width="150">Total</td>
	<td class="tblContent">
        <cfset printMemory(total)>
    </td>
</tr>
--->
</cfsavecontent>
#memoryInfo#
<cftry><cfcatch></cfcatch>
</cftry>
</cfif>











<cfset stText.surveillance.historyRange="Time Range">
<cfset stText.surveillance.historyRangeDesc="Defines the time Range of the data displayed.">
<cfset stText.surveillance.show="Show">


<br /><br />

<cfform action="#request.self#?action=#url.action#" method="post">
<table class="tbl" width="740">
<colgroup>
    <col width="150">
    <col width="590">
</colgroup>

<tr>
	<td class="tblHead" width="150">#stText.surveillance.historyRange#</td>
	<td class="tblContent">
		<span class="comment">#stText.surveillance.historyRangeDesc#</span><br>
		<select name="range">
			<cfloop query="ranges"><option <cfif _range EQ ranges.value>selected</cfif> value="#ranges.value#">#ranges.label#</option></cfloop>
		</select>
	</td>
</tr>
<tr>
	<td class="tblHead" width="150">#stText.surveillance.show#</td>
	<td class="tblContent">
		<cfloop collection="#pool#" item="name">
        	<input <cfif session.memoryShow EQ "all" or ListContainsNoCase(session.memoryShow,keys[name])>checked</cfif> type="checkbox" name="show" value="#keys[name]#" /> #name#<br />
        </cfloop>
	</td>
</tr>
<tr>
	<td colspan="2">
		<input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.Update#">
	</td>
</tr>

</table>
</cfform>

</cfoutput>