<cfsilent><cfparam name="request.recount" default="0">
<cfparam name="attributes.line" default="0">
<cfparam name="attributes.attention" default="">
<cfset request.recount=request.recount+1>

<cffunction name="hasClients" output="no" returntype="boolean">
	<cfargument name="clients" type="query">

	<cfloop query="clients">
		<cfif ListFindNoCase(clients.usage,"synchronisation")>
        	<cfreturn true>
		</cfif>
    </cfloop>
    <cfreturn false>
</cffunction>
<cfadmin 
	action="getRemoteClients"
	type="#request.adminType#"
	password="#session["password"&request.adminType]#"
	returnVariable="clients">
</cfsilent><cfif hasClients(clients)>
<cfoutput>
<cfif attributes.line>
<tr>
	<td colspan="#attributes.colspan#">
	 <table border="0" cellpadding="0" cellspacing="0">
	 <tr>
		<td><cfmodule template="tp.cfm"  width="10" height="1"></td>		
		<td><img src="resources/img/#caller.ad#-bgcolor.gif.cfm" width="1" height="10"></td>
		<td></td>
	 </tr>
	 </table>
	 </td>
</tr>
</cfif>
<tr>
<td colspan="#attributes.colspan#" class="tblHead">
<table class="tbl" width="100%">
<tr>
	<td class="tblContent" style="background-color:DFE9F6" onclick="dumpOC('_remoteclient#request.recount#')">
	
		<script>
		function dumpOC(name){
			var tds=document.all?document.getElementsByTagName('tr'):document.getElementsByName('_'+name);
			var button=document.images['__btn'+name];
			var s=null;
			if(button.src.indexOf('plus')==-1) button.src=button.src.replace('minus','plus');
			else button.src=button.src.replace('plus','minus');
			name='_'+name;
			for(var i=0;i<tds.length;i++) {
				if(document.all && tds[i].name!=name)continue;
				s=tds[i].style;
				if(s.display=='none') s.display='';
				else s.display='none';
			}
		}
		</script>
		<table>
		<tr>
			<td valign="top"><img src="/railo-context/admin/resources/img/#request.adminType#-plus.gif.cfm" style="margin:2px 2px 0px 0px;" name="__btn_remoteclient#request.recount#"/></td>
			<td><span class="comment"><b>#caller.stText.remote.sync.title#</b><br />
		#caller.stText.remote.sync.desc# <cfif len(attributes.attention)><br /><br /><span style="color:red">#attributes.attention#</span></cfif></span></td>
		</tr>
		</table>
	
	
		
	</td>
</tr>
<tr name="__remoteclient#request.recount#" style="display:none;">
	<td class="tblContent" style="background-color:DFE9F6;">
		<table class="tbl">
		<cfloop query="clients"><cfif ListFindNoCase(clients.usage,"synchronisation")>
		<tr>
			<td><input type="checkbox" name="_securtyKeys[]" value="#clients.securityKey#"  checked="checked"/></td>
			<td>#clients.label#</td>
		</tr>
		</cfif></cfloop>
		</table>
	</td>
</tr>

</table>
</td>
</tr>
</cfoutput>
</cfif>