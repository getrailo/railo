<cfcomponent>
<cfset ColorCaster=createObject('java','railo.commons.color.ColorCaster')>
<cfset NL="
">
<cfset default={}>
<cfset default.browser="html">
<cfset default.console="text">

<cfset supportedFormats=["simple","text","html","classic"]>
<cfset this.indexCount=0>

<!--- Meta data --->
	<cfset this.metadata.attributetype="fixed">
    <cfset this.metadata.attributes={
		var:{required:false,type:"any"},
		eval:{required:false,type:"any"},
		expand:{required:false,type:"boolean",default:true},
		label:{required:false,type:"string",default:""},
		top:{required:false,type:"number",default:9999},
		showUDFs:{required:false,type:"boolean",default:true},
		show:{required:false,type:"string",default:"all"},
		output:{required:false,type:"string",default:"browser"},
		metainfo:{required:false,type:"boolean",default:true},
		keys:{required:false,type:"number",default:9999},
		hide:{required:false,type:"string",default:"all"},
		format:{required:false,type:"string",default:""},
		abort:{required:false,type:"boolean",default:false}
	}>

    <cffunction name="init" output="yes" returntype="void"
      hint="invoked after tag is constructed">
    	<cfargument name="hasEndTag" type="boolean" required="yes">
      	<cfargument name="parent" type="component" required="no" hint="the parent cfc custom tag, if there is one">
        <cfif server.railo.version LT "3.1.1.011">
        	<cfthrow message="you need at least version [3.1.1.011] to execute this tag">
        </cfif>
  	</cffunction> 
    
    <cffunction name="onStartTag" output="yes" returntype="boolean" ><!--- 
   		---><cfargument name="attributes" type="struct"><!---
   		---><cfargument name="caller" type="struct"><!---
		---><cfsilent>
        <!--- eval --->
        <cfset var attributes=arguments.attributes>
        
        <cfif not StructKeyExists(attributes,'var') and StructKeyExists(attributes,'eval')>
        	<cfif not len(attributes.label)><cfset attributes.label=attributes.eval></cfif>
            <cfset attributes.var=evaluate(attributes.eval,arguments.caller)>
        </cfif>
        
        <!--- context --->
        <cfset var context=GetCurrentContext()>
		<cfset context=context[2].template& ":"& context[2].line>
    	
        
        <!--- format --->
        <cfset attributes.format=trim(attributes.format)>
        <cfif len(attributes.format) EQ 0>
        	<cfif attributes.output EQ "console">
            	<cfset attributes.format=default.console>
            <cfelseif attributes.output EQ "browser">
            	<cfset attributes.format=default.browser>
            <cfelse>
            	<cfset attributes.format=default.console>
            </cfif>
        <cfelseif not arrayFind(supportedFormats,attributes.format)>
        	<cfthrow message="format [#attributes.format#] is not supported, supported fomats are [#ArrayToList(supportedFormats)#]">
		</cfif>
        
        <!--- create dump struct out of the object --->
        <cftry>
        	<cfset var meta=dumpStruct(StructKeyExists(attributes,'var')?attributes.var:nullValue(),attributes.top,attributes.show,attributes.hide,attributes.keys,attributes.metaInfo,attributes.showUDFs,attributes.label)>
            <cfcatch>
            	<cfset var meta=dumpStruct(StructKeyExists(attributes,'var')?attributes.var:nullValue(),attributes.top,attributes.show,attributes.hide,attributes.keys,attributes.metaInfo,attributes.showUDFs)>
            </cfcatch>
        </cftry>
         
		<!--- create output --->
        <cfset result=this[attributes.format](meta,context,attributes.expand,attributes.output)>
		
        
        </cfsilent><!--- output chanel
		 ---><cfif attributes.output EQ "browser"><cfoutput>#result#</cfoutput><!---
         ---><cfelseif attributes.output EQ "console"><cfset systemOutput(result)><!---
		 ---><cfelse><cffile action="write" addnewline="yes" file="#attributes.output#" output="#result#"><!---
         ---></cfif><!---
        
        ---><cfif attributes.abort><cfabort></cfif><!---
        ---><cfreturn true><!---
    ---></cffunction>

<!----------------------------------------------------------------------------------------------------------------
 												FORMAT HTML														  
----------------------------------------------------------------------------------------------------------------->
<cffunction name="html" output="no" returntype="string">
	<cfargument name="meta" type="struct" required="yes">
	<cfargument name="context" type="string" required="no" default="">
	<cfargument name="expand" type="string" required="no" default="">
	<cfargument name="output" type="string" required="no" default="">
	<cfargument name="inside" type="boolean" required="no" default="#false#">
    <cfset var columncount=StructKeyExists(arguments.meta,'data')?listLen(arguments.meta.data.columnlist):0>
    <cfset var rtn="">
    <cfset var id="_dump"&hash(CreateUUID())>
   
<cfsavecontent variable="rtn"><cfoutput>
<script>
function dumpOC(name){
	var tds=document.all?document.getElementsByTagName('tr'):document.getElementsByName('_'+name);
	var s=null;
	name='_'+name;
	for(var i=0;i<tds.length;i++) {
		if(document.all && tds[i].name!=name)continue;
		s=tds[i].style;
		if(s.display=='none') s.display='';
		else s.display='none';
	}
}
</script>

<table cellpadding="3" cellspacing="1"
    style="font-family : Verdana, Geneva, Arial, Helvetica, sans-serif;font-size : 11px;color :#arguments.meta.fontColor# ;empty-cells:show;"
	#structKeyExists(arguments.meta,'width')?' width="'&arguments.meta.width&'"':''##structKeyExists(arguments.meta,'height')?' height="'&arguments.meta.height&'"':''#>
<!--- title --->
<cfif structKeyExists(arguments.meta,'title')><tr>
	<td title="#arguments.context#" onclick="dumpOC('#id#');" colspan="#columncount#" bgcolor="#arguments.meta.highLightColor#" style="border : 1px solid #arguments.meta.borderColor#; empty-cells:show;">
		<span style="font-weight:bold;">#arguments.meta.title#</span>
        <cfif structKeyExists(arguments.meta,'comment')><br /> #replace(HTMLEditFormat(arguments.meta.comment),chr(10),' <br>','all')#</cfif>
    </td>
</tr>
<cfelse>
	<cfset id="">
</cfif>
<!--- data --->
<cfset var value="">
<cfset var c=1>
<cfif columncount><cfloop query="arguments.meta.data">
<tr #len(id)?'name="_#id#"':''#<cfif len(id)> name="_#id#"<cfif not arguments.expand> style="display:none"</cfif></cfif>>
<cfset c=1><cfloop index="col" from="1" to="#columncount-1#"><cfset value=arguments.meta.data["data"&col]>
	<td valign="top" #!arguments.inside?'title="#arguments.context#"':''# bgcolor="#bgColor(arguments.meta,c)#" style="border : 1px solid #arguments.meta.borderColor#;empty-cells:show;"><cfif isStruct(value)>#this.html(value,"",arguments.expand,arguments.output,false)#<cfelse>#HTMLEditFormat(value)#</cfif></td><cfset c*=2>
</cfloop>
</tr>
</cfloop></cfif>
</table>    

</cfoutput></cfsavecontent>
<cfreturn rtn>
</cffunction>



<!----------------------------------------------------------------------------------------------------------------
 												FORMAT CLASSIC														  
----------------------------------------------------------------------------------------------------------------->
<cffunction name="classic" output="no" returntype="string">
	<cfargument name="meta" type="struct" required="yes">
	<cfargument name="context" type="string" required="no" default="">
	<cfargument name="expand" type="string" required="no" default="">
	<cfargument name="output" type="string" required="no" default="">
	<cfargument name="inside" type="boolean" required="no" default="#false#">
    <cfset var columncount=StructKeyExists(arguments.meta,'data')?listLen(arguments.meta.data.columnlist):0>
    <cfset var rtn="">
	<cfset var id="_dump"&(this.indexCount++)>
    
    <!--- deine colors --->
    <cfset var h1Color=arguments.meta.highLightColor>
    <cfset var h2Color=arguments.meta.normalColor>
    <cfset arguments.meta.normalColor="white">
    <cfset borderColor=arguments.meta.highLightColor>
	<cftry>
    	<cfset borderColor=ColorCaster.toHexString(ColorCaster.toColor(h1Color).darker().darker())>
        <cfcatch></cfcatch>
    </cftry>
    
    	
<cfsavecontent variable="rtn"><cfoutput>
<script>
function dumpOC(name){
	var tds=document.all?document.getElementsByTagName('tr'):document.getElementsByName('_'+name);
	var s=null;
	name='_'+name;
	for(var i=0;i<tds.length;i++) {
		if(document.all && tds[i].name!=name)continue;
		s=tds[i].style;
		if(s.display=='none') s.display='';
		else s.display='none';
	}
}
</script>

<table cellpadding="3" cellspacing="0"
    style="font-family : Verdana, Geneva, Arial, Helvetica, sans-serif;font-size : 10;color :#arguments.meta.fontColor# ;empty-cells:show; border : 1px solid #borderColor#;"
	#structKeyExists(arguments.meta,'width')?' width="'&arguments.meta.width&'"':''##structKeyExists(arguments.meta,'height')?' height="'&arguments.meta.height&'"':''#>
<!--- title --->
<cfif structKeyExists(arguments.meta,'title')><tr>
	<td title="#arguments.context#" onclick="dumpOC('#id#');" colspan="#columncount#" bgcolor="#h1Color#" style="color:white;border : 1px solid #borderColor#; empty-cells:show;">
		<span style="font-weight:bold;">#arguments.meta.title#</span>
        <cfif structKeyExists(arguments.meta,'comment')><br />#replace(HTMLEditFormat(arguments.meta.comment),chr(10),' <br>','all')#</cfif>
    </td>
</tr>
<cfelse>
	<cfset id="">
</cfif>
<!--- data --->
<cfset var value="">
<cfset var c=1>
<cfif columncount><cfloop query="arguments.meta.data">
<tr #len(id)?'name="_#id#"':''#>
<cfset c=1><cfloop index="col" from="1" to="#columncount-1#"><cfset value=arguments.meta.data["data"&col]>
	<td valign="top" #!arguments.inside?'title="#arguments.context#"':''# bgcolor="#bgColor(arguments.meta,c,h2Color)#" style="border : 1px solid #borderColor#;empty-cells:show;"><cfif isStruct(value)>#this.classic(value,"",arguments.expand,arguments.output,false)#<cfelse>#HTMLEditFormat(value)#</cfif></td><cfset c*=2>
</cfloop>
</tr>
</cfloop></cfif>
</table>    
<cfif not arguments.expand><script>dumpOC('#id#');</script></cfif>
</cfoutput></cfsavecontent>
<cfreturn rtn>
</cffunction>

<!----------------------------------------------------------------------------------------------------------------
 												FORMAT SIMPLE														  
----------------------------------------------------------------------------------------------------------------->
<cffunction name="simple" output="no" returntype="string">
	<cfargument name="meta" type="struct" required="yes">
	<cfargument name="context" type="string" required="no" default="">
	<cfargument name="expand" type="string" required="no" default="">
	<cfargument name="output" type="string" required="no" default="">
    <cfset var columncount=StructKeyExists(arguments.meta,'data')?listLen(arguments.meta.data.columnlist):0>
    <cfset var rtn="">
	
<cfsavecontent variable="rtn"><cfoutput>
<table  cellpadding="1" cellspacing="0"#structKeyExists(arguments.meta,'width')?' width="'&arguments.meta.width&'"':''##structKeyExists(arguments.meta,'height')?' height="'&arguments.meta.height&'"':''# border="1">
<!--- title --->
<cfif structKeyExists(arguments.meta,'title')>
<tr>
	<td title="#arguments.context#" colspan="#columncount#" bgcolor="#arguments.meta.highLightColor#">
		<b>#arguments.meta.title#</b>
        <cfif structKeyExists(arguments.meta,'comment')><br />#replace(HTMLEditFormat(arguments.meta.comment),chr(10),' <br>','all')#</cfif>
    </td>
</tr>
</cfif>
<!--- data --->
<cfset var value="">
<cfset var c=1>
<cfif columncount><cfloop query="arguments.meta.data">
<tr><cfset c=1><cfloop index="col" from="1" to="#columncount-1#">
	<cfset value=arguments.meta.data["data"&col]><!---
	---><td title="#arguments.context#" bgcolor="#bgColor(arguments.meta,c)#"><cfif isStruct(value)>#this.simple(value,"",arguments.expand)#<cfelseif len(value)>#HTMLEditFormat(value)#<cfelse>&nbsp;</cfif></td><!---
    ---><cfset c*=2><!---
---></cfloop>
</tr>
</cfloop></cfif>
</table>    
</cfoutput></cfsavecontent>
<cfreturn rtn>
</cffunction>


<!----------------------------------------------------------------------------------------------------------------
 												FORMAT TEXT														  
----------------------------------------------------------------------------------------------------------------->
<cffunction name="text" output="no" returntype="string">
	<cfargument name="meta" type="struct" required="yes">
	<cfargument name="context" type="string" required="no" default="">
	<cfargument name="expand" type="string" required="no" default="">
	<cfargument name="output" type="string" required="no" default="">
	<cfargument name="level" type="numeric" required="no" default="0">
    
    
    <cfset var columncount=StructKeyExists(arguments.meta,'data')?listLen(arguments.meta.data.columnlist):0>
    <cfset var rtn="">
	<cfset var bq=RepeatString("	",level)>

	<!--- title --->
	<cfif structKeyExists(arguments.meta,'title')>
		<cfset rtn=arguments.meta.title><cfif structKeyExists(arguments.meta,'comment')><cfset rtn&=NL&arguments.meta.comment></cfif>
        <cfset rtn&=NL&bq>
	</cfif>
	<!--- data --->
    
    <cfset var value="">
    <cfset var c=1>
    <cfif columncount>
    	<cfloop query="arguments.meta.data">
			<cfset c=1>
            <cfloop index="col" from="1" to="#columncount-1#">
                <cfset value=arguments.meta.data["data"&col]>
                <cfif isStruct(value)><cfset rtn&=this.text(value,"",arguments.expand,"console",level+1)><cfelse><cfset rtn&=value></cfif>
                <cfset rtn&=" ">
				<cfset c*=2>
            </cfloop>
            <cfset rtn&=NL&bq>
        	
        </cfloop>
	</cfif>
      
    <cfif arguments.output NEQ "console"><cfreturn "<pre>"&rtn&"</pre>"></cfif>
    <cfreturn rtn>
</cffunction>

    <cffunction name="bgColor" output="no" returntype="string" access="private">
        <cfargument name="meta" type="struct" required="yes">
        <cfargument name="c" type="numeric" required="yes">
        <cfargument name="highLightColor" type="string" required="no" default="#arguments.meta.highLightColor#">
        
		<cfif arguments.meta.data.highlight EQ -1>
            <cfreturn highLightColor>
        <cfelseif arguments.meta.data.highlight EQ 0>
            <cfreturn arguments.meta.normalColor>
        <cfelse>
            <cfreturn bitand(arguments.meta.data.highlight,c)?highLightColor:arguments.meta.normalColor>
        </cfif>
    </cffunction>
</cfcomponent>