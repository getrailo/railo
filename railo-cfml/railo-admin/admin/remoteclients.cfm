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
	<td class="tblContent" style="background-color:white" onclick="dumpOC('_remoteclient#request.recount#')">
	
		<script>
		var plus="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAERJREFUeNpisbFz+M9AALCAiMMH9zPiUmBr7/ifCZsguhgTAxGABZsJMDbMGXBFMAGQAnQ3EmUdhiJsPmXB5SNkABBgANI5F5L+njplAAAAAElFTkSuQmCC";
		var minus="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJCAYAAADgkQYQAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAADpJREFUeNpitLFz+M9ACIAUAQEDLgySZ2IgAhCliAXGsLV3xHDb4YP7GVEUwQTIto54N2FzDzIACDAAsb0iLABZQ+gAAAAASUVORK5CYII=";
		
		function dumpOC(name){
			var tds=document.all?document.getElementsByTagName('tr'):document.getElementsByName('_'+name);
			var button=document.images['__btn'+name];
			var s=null;
			
			
			if(button.src==plus) button.src=minus;
			else button.src=plus;
			
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
			<td valign="top"><img src="resources/img/plus.png.cfm" style="margin:2px 2px 0px 0px;" name="__btn_remoteclient#request.recount#"/></td>
			<td>#caller.stText.remote.sync.title#<br />
		<span class="comment">#caller.stText.remote.sync.desc# <cfif len(attributes.attention)><br /><br /><span style="color:red">#attributes.attention#</span></cfif></span></td>
		</tr>
		</table>
	
	
		
	</td>
</tr>
<tr name="__remoteclient#request.recount#" style="display:none;">
	<td class="tblContent" style="background-color:white;">
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