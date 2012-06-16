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


<cfscript>
NL="
";

function formatDesc(string desc){
	desc=replace(trim(desc),NL&"-","<br><li>","all");
	desc=replace(desc,NL,"<br>","all");

	return desc;
}
</cfscript>
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
<td>
<input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.OK#"> 
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
#replace(replace(data.description,'	','&nbsp;&nbsp;&nbsp;','all'),'
','<br />','all')#

<style>
.error{color:red;}
.syntaxFunc{color:##993300;}
.syntaxText {color:##CC0000;}
.syntaxType {color:##000099;}
</style>







<cfset first=true>
<cfset optCount=0>
<pre><span class="syntaxFunc">#data.name#(</span><cfloop array="#data.arguments#" index="item"><cfif item.status EQ "hidden"><cfcontinue></cfif><cfif not first><span class="syntaxFunc">,</span></cfif><cfif not item.required><cfset optCount=optCount+1><span class="syntaxFunc">[</span></cfif><span class="syntaxType">#item.type#</span> <span class="syntaxText">#item.name#</span><cfset first=false></cfloop><span class="syntaxFunc">#RepeatString(']',optCount)#):</span><span class="syntaxType">#data.returntype#</span>
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
	<th scope="row">#stText.doc.arg.name#</th>
	<th scope="row">#stText.doc.arg._type#</th>
	<th scope="row">#stText.doc.arg.required#</th>
	<th scope="row">#stText.doc.arg.description#</th>
</tr>

<cfloop array="#data.arguments#" index="attr"><cfif attr.status EQ "hidden"><cfcontinue></cfif>
<tr>
	<td>#attr.name	#</td>
	<td>#attr.type#&nbsp;</td>
	<td>#YesNoFormat(attr.required)#</td>
	<td><cfif attr.status EQ "deprecated"><b class="error">#stText.doc.depArg#</b><cfelse>#formatDesc(attr.description)#</cfif>&nbsp;</td>
</tr>
</cfloop>

</table>

</cfif>
</div>
</cfif>


</cfoutput>

