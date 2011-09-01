
<!---
<cfloop from="1" to="5" index="i">
<cfthread name="t#i#">
	<cfhttp url="http://localhost:8080/jm/test5.cfm" resolveurl="no" />
</cfthread>
</cfloop>
<cfset sleep(10)>
--->

<cfadmin 
	action="getContexts"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="contexts">
    

<cfadmin action="surveillance" type="server" password="server" returnvariable="allThreads">
    
    
 <cfoutput query="contexts">
 <cfset threads=allThreads[contexts.label]>
 <h2>#ucFirst(contexts.label)# (#contexts.path#)</h2>
 running requests:#arraylen(threads)#<br />
 <cfloop from="1" to="#arrayLen(threads)#" index="i">
 	<cfset thread=threads[i]>
    
<table class="tbl" width="740">
<tr>
	<td class="tblHead" colspan="2">#thread.scopes.cgi.SCRIPT_NAME#?#thread.scopes.cgi.QUERY_STRING#</td>
</tr>
<tr>
	<td class="tblHead">Runs for </td>
	<td class="tblContent">#DateDiff("s",thread.startTime,now())# seconds</td>
</tr>
<tr>
	<td class="tblHead">Runs for </td>
	<td class="tblContent">#thread.startTime#</td>
</tr>
</table>
    
    
 </cfloop>
 
 
 </cfoutput>
 
 
 
 
 
 