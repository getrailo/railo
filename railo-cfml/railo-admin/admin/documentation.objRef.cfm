<cfparam name="url.func" default="">
<cfscript>
funcList=getFunctionList();
objList={};
loop collection="#funcList#" index="key" {
	data=getFunctionData(key);
	if(structKeyExists(data,'member')) {
		objList[data.member.type][key]=data.member.name;
	}
}
</cfscript>


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
	<cfset arr=StructKeyArray(objList)>
	<cfset ArraySort(arr,'textnocase')>
	
	<cfloop array="#arr#" index="type">
    	<cfset sct=objList[type]>
        <cfset arrr=StructKeyArray(sct)>
		<cfset ArraySort(arrr,'textnocase')>
        <optgroup label="#type#">
    	<cfloop array="#arrr#" index="key">
		<option value="#key#" <cfif url.func EQ key>selected="selected"</cfif>>#ucFirst(type)#.#sct[key]#</option>
        </cfloop>
        </optgroup>
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
<pre><span class="syntaxFunc">#ucFirst(data.member.type)#.#data.member.name#(</span><cfloop array="#data.arguments#" index="index" item="item"><cfif index EQ 1 or item.status EQ "hidden"><cfcontinue></cfif><cfif not first><span class="syntaxFunc">,</span></cfif><cfif not item.required><cfset optCount=optCount+1><span class="syntaxFunc">[</span></cfif><span class="syntaxType">#item.type#</span> <span class="syntaxText">#item.name#</span><cfset first=false></cfloop><span class="syntaxFunc">#RepeatString(']',optCount)#):</span><span class="syntaxType">#data.returntype#</span>
</pre>


<!--- Argumente --->
<h2>#stText.doc.argTitle#</h2>
<cfif data.argumentType EQ "fixed" and arraylen(data.arguments) LTE 1>
#stText.doc.arg.zero#
<cfelse>
#stText.doc.arg.type[data.argumentType]#
</cfif>
<cfif data.argumentType EQ "fixed" and arraylen(data.arguments) GT 1>
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

<cfloop array="#data.arguments#" index="index" item="attr"><cfif index EQ 1 or attr.status EQ "hidden"><cfcontinue></cfif>
<tr>
	<td class="tblContent">#attr.name	#</td>
	<td class="tblContent">#attr.type#&nbsp;</td>
	<td class="tblContent">#YesNoFormat(attr.required)#</td>
	<td class="tblContent"><cfif attr.status EQ "deprecated"><b class="error">#stText.doc.depArg#</b><cfelse>#formatDesc(attr.description)#</cfif>&nbsp;</td>
</tr>
</cfloop>

</table>

</cfif>
</div>
</cfif>


</cfoutput>

