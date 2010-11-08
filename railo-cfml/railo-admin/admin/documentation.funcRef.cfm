<cfparam name="url.func" default="">

<cfset funcList=getFunctionList()>


<cfoutput>

<script language="javascript">
function detail(field){
	var value=field.options[field.selectedIndex].value;
	var path="#request.self#?action=#url.action#&func="+value;
	window.location=(path);

}
</script>
<form action="#request.self#">
<input type="hidden" name="action" value="#url.action#" />
<table class="tbl">
<tr>
<td class="tblHead" width="300">
<select name="func" onchange="detail(this)">
	<option value="" > -------------- </option>
	<cfset arr=StructKeyArray(funcList)>
	<cfset ArraySort(arr,'textnocase')>
	
	<cfloop array="#arr#" index="key">
		<cfif left(key,1) NEQ "_"><option value="#key#" <cfif url.func EQ key>selected="selected"</cfif>>#key#</option></cfif>
	</cfloop>
</select>
</td>
<td class="tblContent">
<input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.OK#"> 
</td>
</tr>
</table>
</form>



<cfif len(url.func)>
<div style="width:740px">
	<cfset data=getFunctionData(url.func)>



<cfif data.status EQ "deprecated"><b class="error">
#stText.doc.depFunction#</b><br />
</cfif>
<!--- Desc --->
#data.description#

<style>
.error{color:red;}
.syntaxFunc{color:##993300;}
.syntaxText {color:##CC0000;}
.syntaxType {color:##000099;}
</style>







<cfset first=true>
<cfset optCount=0>
<pre><span class="syntaxFunc">#data.name#(</span><cfloop array="#data.arguments#" index="item"><cfif not first><span class="syntaxFunc">,</span></cfif><cfif not item.required><cfset optCount=optCount+1><span class="syntaxFunc">[</span></cfif><span class="syntaxType">#item.type#</span> <span class="syntaxText">#item.name#</span><cfset first=false></cfloop><span class="syntaxFunc">#RepeatString(']',optCount)#):</span><span class="syntaxType">#data.returntype#</span>
</pre>


<!--- Argumente --->
<h2>#stText.doc.argTitle#</h2>
<cfif data.argumentType EQ "fixed" and not arraylen(data.arguments)>
#stText.doc.arg.zero#
<cfelse>
#stText.doc.arg.type[data.argumentType]#
<cfif data.argumentType EQ "dynamic">
	<cfif data.argMin GT 0 and data.argMax GT 0>
	#replace(replace(stText.doc.arg.minMax,"{min}",data.argMin),"{max}",data.argMax)#
	<cfelseif data.argMin GT 0>
	#replace(stText.doc.arg.min,"{min}",data.argMin)#
	<cfelseif data.argMax GT 0>
	#replace(stText.doc.arg.max,"{max}",data.argMax)#
	</cfif>

</cfif>
</cfif>
<cfif data.argumentType EQ "fixed" and arraylen(data.arguments)>
<br />
<table class="tbl">
<colgroup>
	<col width="150" />
	<col width="50" />
	<col width="50" />
	<col width="450" />
</colgroup>
<tr>
	<td class="tblHead">#stText.doc.arg.name#</td>
	<td class="tblHead">#stText.doc.arg._type#</td>
	<td class="tblHead">#stText.doc.arg.required#</td>
	<td class="tblHead">#stText.doc.arg.description#</td>
</tr>

<cfloop array="#data.arguments#" index="attr">
<tr>
	<td class="tblContent">#attr.name	#</td>
	<td class="tblContent">#attr.type#&nbsp;</td>
	<td class="tblContent">#YesNoFormat(attr.required)#</td>
	<td class="tblContent"><cfif attr.status EQ "deprecated"><b class="error">#stText.doc.depArg#</b><cfelse>#attr.description#</cfif>&nbsp;</td>
</tr>
</cfloop>

</table>

</cfif>
</div>
</cfif>


</cfoutput>

