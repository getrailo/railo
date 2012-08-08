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

NL="
";

function formatDesc(string desc){
	desc=replace(trim(desc),NL&"-","<br><li>","all");
	desc=replace(desc,NL,"<br>","all");

	return desc;
}
</cfscript>

<cfoutput>
	<script type="text/javascript">
		function detail(field){
			var value=field.options[field.selectedIndex].value;
			var path="#request.self#?action=#url.action#&func="+value;
			window.location=(path);
		}
	</script>
	<form action="#request.self#">
		<input type="hidden" name="action" value="#url.action#" />
		<table class="maintbl">
			<tbody>
				<tr>
					<th scope="row">#stText.doc.choosetag#</th>
					<td>
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
				</tr>
			</tbody>
			<tfoot>
				<td colspan="2">
					<input class="submit" type="submit" class="submit" name="mainAction" value="#stText.Buttons.OK#"> 
				</td>
			</tfoot>
		</table>
	</form>

	<cfif len(url.func)>
		<cfset data=getFunctionData(url.func)>
		<h2>Documentation for object method <em>#ucFirst(data.member.type)#.#data.member.name#</em></h2>
		<cfif data.status EQ "deprecated">
			<div class="warning nofocus">#stText.doc.depFunction#</div>
		</cfif>
<!--- Desc --->
		<div class="text">
			<cfif not StructKeyExists(data, "description") or data.description eq "">
				<em>No decription found</em>
			<cfelse>
				#replace(replace(data.description,'	','&nbsp;&nbsp;&nbsp;','all'), server.separator.line,'<br />','all')#
			</cfif>
		</div>

		<cfset first=true>
		<cfset optCount=0>
		<pre><span class="syntaxFunc">#ucFirst(data.member.type)#.#data.member.name#(</span><cfloop array="#data.arguments#" index="index" item="item"><cfif index EQ 1 or item.status EQ "hidden"><cfcontinue></cfif><cfif not first><span class="syntaxFunc">,</span></cfif><cfif not item.required><cfset optCount=optCount+1><span class="syntaxFunc">[</span></cfif><span class="syntaxType">#item.type#</span> <span class="syntaxText">#item.name#</span><cfset first=false></cfloop><span class="syntaxFunc">#RepeatString(']',optCount)#):</span><span class="syntaxType">#data.returntype#</span></pre>

		<!--- Argumente --->
		<h2>#stText.doc.argTitle#</h2>
		<div class="itemintro">
			<cfif data.argumentType EQ "fixed" and arraylen(data.arguments) LTE 1>
				#stText.doc.arg.zero#
			<cfelse>
				#stText.doc.arg.type[data.argumentType]#
			</cfif>
		</div>
		<cfif data.argumentType EQ "fixed" and arraylen(data.arguments) GT 1>
			<table class="maintbl">
				<thead>
					<tr>
						<th width="20%">#stText.doc.arg.name#</th>
						<th width="7%">#stText.doc.arg._type#</th>
						<th width="7%">#stText.doc.arg.required#</th>
						<th width="66%">#stText.doc.arg.description#</th>
					</tr>
				</thead>
				<tbody>
					<cfloop array="#data.arguments#" index="index" item="attr">
						<cfif index EQ 1 or attr.status EQ "hidden"><cfcontinue></cfif>
						<tr>
							<td>#attr.name	#</td>
							<td>#attr.type#&nbsp;</td>
							<td>#YesNoFormat(attr.required)#</td>
							<td>
								<cfif attr.status EQ "deprecated">
									<b class="error">#stText.doc.depArg#</b>
								<cfelse>
									#formatDesc(attr.description)#
								</cfif>
							</td>
						</tr>
					</cfloop>
				</tbody>
			</table>
		</cfif>
	</cfif>
</cfoutput>