<cfcomponent name="ajaxBase">
		
	<!--- Instance vars --->
	<cfset variables.instance = {} />
	
	<!--- 
	Resources location ( can be overwritten ) 
	--->
	<cfset variables.instance.SCRIPTSRC = "/mapping-tag/railo/core/ajax/JSLoader.cfc?method=get&lib=" />
	<cfset variables.instance.CSSSRC = "/mapping-tag/railo/core/ajax/css/" />
	<cfset variables.instance.LOADERSRC = "/mapping-tag/railo/core/ajax/loader/loading.gif.cfm" />
	
	<!--- 
	Railo js library location 
	--->
	<cfset variables.instance.RAILOJSSRC = "/mapping-tag/railo/core/ajax/JSLoader.cfc?method=get&lib=" />
	
	<!--- Constructor --->
    <cffunction name="init" output="no" returntype="void">
    	<cfargument name="scriptSrc" type="string" default="#variables.instance.SCRIPTSRC#" />
    	<cfargument name="cssSrc" type="string" default="#variables.instance.CSSSRC#" />
    	<cfargument name="loaderSrc" type="string" default="#variables.instance.LOADERSRC#" />
    	<cfargument name="adapter" default="" type="string" required="false" />
		<cfargument name="params" default="#struct()#" type="struct" required="false" />
		
		<cfset var js = "" />
		
		<!--- Support context root differents from '/' --->
		<cfif getContextRoot() neq '/'> 
			<cfset arguments.scriptSrc = getContextRoot() & ARGUMENTS.scriptSrc />
			<cfset arguments.cssSrc = getContextRoot() & ARGUMENTS.cssSrc />
		</cfif>	
		
		<cfif arguments.cssSrc neq variables.instance.CSSSRC>
			<cfset variables.instance.isCustomCss = true />
		</cfif>

		<cfsavecontent variable="js">
			<cfoutput>									
				<script type="text/javascript">
				var _cf_ajaxscriptsrc = '#arguments.scriptsrc#';
				var _cf_ajaxcsssrc = '#arguments.cssSrc#';
				var _cf_loadingtexthtml = '<div style="text-align: center;"><img src="#arguments.loadersrc#"/></div>';				
				var _cf_params = #serializeJson(arguments.params)#;
				</script>
				<script type="text/javascript" src="#variables.instance.RAILOJSSRC#RailoAjax"></script>
				<cfif len(arguments.adapter)><script type="text/javascript" src="#arguments.adapter#"></script></cfif>
            </cfoutput>		
        </cfsavecontent>
		<cfset writeHeader(js,'Railo-Ajax-Core')>
  	</cffunction> 
	
	<!--- Write Header --->	
	<cffunction name="writeHeader" returntype="void" access="public" description="writes data to html header but only once">
        <cfargument name="text" required="yes" type="string">
		<cfargument name="id" required="yes" type="string">
		
        <cfset var head="">
		<cfset var attrs = "" />
		
        <cftry>
			<cfset attrs={action="read", variable="head"}>
        	<cfhtmlhead attributeCollection="#attrs#">
            <!--- throws exception when already flushed or action read is not supported --->
            <cfcatch>
            	<cfoutput>#trim(text)#</cfoutput>
            	<cfreturn>
            </cfcatch>
        </cftry>
       <cfif not find(id,head)>
			<cfhtmlhead action="append" text="<!-- #id# --> #trim(text)#">
		</cfif>
    </cffunction>

	<!--- StripWhiteSpace --->	
	<cffunction name="stripWhiteSpace" output="no" returntype="string" hint="Strips whitespace outside tags from string"> 
		<cfargument name="str" type="string" default="" required="no"/>
		<cfreturn trim(reReplaceNoCase(arguments.str,"(</?.*?\b[^>]*>)[\s]{1,}|[\r]{1,}(</?.*?\b[^>]*>)","\1#chr(13)##chr(10)#\2","All"))/>
	</cffunction>
		
</cfcomponent>