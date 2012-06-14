<cfparam name="url.tag" default="">

<cfset tagList=getTagList()>


<cfoutput>

<script language="javascript">
function detail(field){
	var value=field.options[field.selectedIndex].value;
	var path="#request.self#?action=#url.action#&tag="+value;
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
<select name="tag" onchange="detail(this)">
	<option value="" > -------------- </option>
<cfloop collection="#tagList#" item="ns">
	<cfset arr=StructKeyArray(tagList[ns])>
	<cfset ArraySort(arr,'textnocase')>
	
	<cfloop array="#arr#" index="key">
		<option value="#ns#,#key#" <cfif url.tag EQ ns&","&key>selected="selected"</cfif>>#ns##key#</option>
	</cfloop>
</cfloop>
</select>
</td>
<td>
<input class="button submit" type="submit" name="mainAction" value="#stText.Buttons.OK#"> 
</td>
</tr>
</table>
</form>





<cfif len(url.tag)>
<div style="width:740px">
	<cfset data=getTagData(listFirst(url.tag),listLast(url.tag))>
	
<!--- Desc --->
<cfif data.status EQ "deprecated"><b class="error">
#stText.doc.depTag#</b><br />
</cfif>
#data.description#

<style>
.error{color:red;}
.syntaxTag{color:##993300;}
.syntaxText {color:##CC0000;}
.syntaxAttr {color:##000099;}
</style>
<cfset tagName=data.namespace&data.namespaceseperator&data.name>
<cfif data.hasNameAppendix><cfset tagName&="CustomName"></cfif>

<cfset arrAttrNames=StructKeyArray(data.attributes)>
<cfset ArraySort(arrAttrNames,'textnocase')>
<pre><span class="syntaxTag">&lt;#tagName#</span><cfif data.attributeType EQ "noname"> <span class="syntaxAttr">##<cfloop array="#arrAttrNames#" index="key">#data.attributes[key].type# <cfbreak></cfloop>expression##</span> <cfelse><!--- 
---><cfloop array="#arrAttrNames#" index="key"><cfset attr=data.attributes[key]><cfif attr.status EQ "hidden"><cfcontinue></cfif>
	<cfif not attr.required><span class="syntaxAttr">[</span></cfif><!---
	---><span class="syntaxAttr">#key#</span>=<span class="syntaxText">"<cfif not attr.required><i></cfif>#attr.type#<cfif not attr.required></i></cfif>"</span><!---
	---><cfif not attr.required><span class="syntaxAttr">]</span></cfif></cfloop></cfif><!---

---><cfif data.attributeType EQ "dynamic" or data.attributeType EQ "mixed"> <span class="syntaxAttr">...</span> </cfif><cfif data.bodyType EQ "prohibited"><span class="syntaxTag">&gt;</span>
<cfelseif data.bodyType EQ "free"><span class="syntaxTag">&gt;

[&lt;/#tagName#&gt;]</span>
<cfelseif data.bodyType EQ "required"><span class="syntaxTag">&gt;

&lt;/#tagName#&gt;</span></cfif>
</pre>



<cfif structKeyExists(data,"script") and data.script.type NEQ "none">
<cfset arrAttrNames=StructKeyArray(data.attributes)>
<cfset ArraySort(arrAttrNames,'textnocase')>
<br />#stText.doc.alsoScript#
<pre><span class="syntaxTag">&lt;cfscript></span>
<span class="syntaxAttr">#data.name#</span><!---



No Name
 ---><cfif data.attributeType EQ "noname"> <span class="syntaxAttr">##<cfloop array="#arrAttrNames#" index="key">#data.attributes[key].type# <cfbreak></cfloop>expression##</span><!---
 
Single type 
 ---><cfelseif data.script.type EQ "single"><span class="syntaxAttr"><cfloop array="#arrAttrNames#" index="key"><cfset ss=data.attributes[key].scriptSupport><cfif ss NEQ "none"> <!--- 
 ---><cfif ss EQ "optional">[</cfif>#data.attributes[key].type#<cfif data.script.rtexpr> expression</cfif><cfif ss EQ "optional">]</cfif><cfbreak></cfif></cfloop></span><!--- 
 
 
multiple
---><cfelse><cfloop array="#arrAttrNames#" index="key"><cfset attr=data.attributes[key]><cfif attr.status EQ "hidden"><cfcontinue></cfif>
	<cfif not attr.required><span class="syntaxAttr">[</span></cfif><!---
	---><span class="syntaxAttr">#key#</span>=<span class="syntaxText">"<cfif not attr.required><i></cfif>#attr.type#<cfif not attr.required></i></cfif>"</span><!---
	---><cfif not attr.required><span class="syntaxAttr">]</span></cfif></cfloop></cfif><!---

---><cfif data.attributeType EQ "dynamic" or data.attributeType EQ "mixed"> <span class="syntaxAttr">...</span> </cfif><cfif data.bodyType EQ "prohibited"><span class="syntaxAttr">;</span><cfelseif data.bodyType EQ "required" or data.bodyType EQ "free"><span class="syntaxAttr"> {

}</span></cfif>
<span class="syntaxTag">&lt;/cfscript></span></pre>
</cfif>


<!--- Body --->
<h2>#stText.doc.bodyTitle#</h2>
#stText.doc.body[data.bodyType]#
	




<!--- Attributes --->


<h2>#stText.doc.attrTitle#</h2>

<cfif data.attributeType EQ "fixed" and not arrayLen(arrAttrNames)>
	#stText.doc.attr.zero#
<cfelse>
#stText.doc.attr.type[data.attributeType]#
<cfif data.attributeType EQ "dynamic">
	<cfif data.attrMin GT 0 and data.attrMax GT 0>
	#replace(replace(stText.doc.attr.minMax,"{min}",data.attrMin),"{max}",data.attrMax)#
	<cfelseif data.attrMin GT 0>
	#replace(stText.doc.attr.min,"{min}",data.attrMin)#
	<cfelseif data.attrMax GT 0>
	#replace(stText.doc.attr.max,"{max}",data.attrMax)#
	</cfif>

</cfif>

</cfif>
<cfif (data.attributeType EQ "fixed" or data.attributeType EQ "mixed") and arrayLen(arrAttrNames)>
<br />
<table class="tbl">
<colgroup>
	<col width="150" />
	<col width="50" />
	<col width="50" />
	<col width="450" />
</colgroup>
<tr>
	<td class="tblHead">#stText.doc.attr.name#</td>
	<td class="tblHead">#stText.doc.attr._type#</td>
	<td class="tblHead">#stText.doc.attr.required#</td>
	<td class="tblHead">#stText.doc.attr.description#</td>
</tr>

<cfloop array="#arrAttrNames#" index="key">
<cfset attr=data.attributes[key]>
<cfif attr.status EQ "hidden"><cfcontinue></cfif>
<tr>
	<td>#key#</td>
	<td><cfif attr.type EQ "object">any<cfelse>#attr.type#</cfif></td>
	<td>#YesNoFormat(attr.required)#</td>
	<td><cfif attr.status EQ "deprecated"><b class="error">#stText.doc.depAttr#</b><cfelse>#formatDesc(attr.description)#</cfif>&nbsp;</td>
</tr>
</cfloop>

</table>

</cfif>
</div>
</cfif>


</cfoutput>

