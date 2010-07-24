<cfcomponent output="false">

	<cfset variables.instance = {} />	
	<cfset variables.instance.DEFAULT_BASEDIR = '/_cfjavascript/' />
	<cfset variables.instance.TEMP = getTempDirectory() />

	<!--- Meta data --->
	<cfset this.metadata.attributetype="fixed">
    <cfset this.metadata.attributes={
		src:		{required:true,type:"any"},
		path :   	{required:false,type:"String",default:""},	
		filename:   {required:false,type:"String",default:""},
		charset :   {required:false,type:"String",default:"utf-8"},		
		cache:      {required:false,type:"boolean",default:"true"},
		debug:      {required:false,type:"boolean",default:"false"},
		lineBreak: 	{required:false,type:"numeric",default:"800"},
		munge:      {required:false,type:"boolean",default:"false"},
		preserveAllSemiColons: {required:false,type:"boolean",default:"false"},
		disableOptimizations : {required:false,type:"boolean",default:"true"}			   		
	}>


	<!--- 
	Constructor
	 --->
    <cffunction name="init" output="no" returntype="void"
      hint="invoked after tag is constructed">
    	<cfargument name="hasEndTag" type="boolean" required="yes">
      	<cfargument name="parent" type="component" required="no" hint="the parent cfc custom tag, if there is one">
      	
		<cfset variables.instance.hasEndTag = hasEndTag />		
  	</cffunction> 



    <cffunction name="onStartTag" output="true" returntype="boolean">
   		<cfargument name="attributes" type="struct">
   		<cfargument name="caller" type="struct">
				
		<!--- 
		If debug is true files are outputted as originally are.
		url.cfjavascript_debug=true or attribute debug=true
		--->
		<cfif structKeyExists(url,'_cfjavascript_debug')>
			<cfset attributes.debug = true />
		</cfif>
		<cfif attributes.debug >	
			<cfset doIncludeSource( attributes ) />
			<cfreturn variables.instance.hasEndTag />
		</cfif>
			
		<!--- 
		If the path has not been passed set to default.
		If not exists create that. 
		If is custom make sure path ends with /
		--->		
		<cfif not len(attributes.path) >
			<cfset attributes.path = variables.instance.DEFAULT_BASEDIR />
		<cfelse>
			<cfset attributes.path = this.normalizePath(attributes.path) />	
		</cfif>
		
		<cfif not directoryExists(expandPath(attributes.path))>
			<cfdirectory action="create" directory="#expandPath(attributes.path)#">
		</cfif>				
	
		<!--- 
		If src is simple value wrap into an array to normalize data.
		--->
		<cfif isSimpleValue( attributes.src )>
			<cfset attributes.src = [attributes.src] >
		</cfif>
		
		
		<!--- 
		Let's see if we have a cache available.
		Also check for force reload attributes in url scope. If exists file will be recompressed also if exists. 
		The key is the filename or the generated hash.
		--->
		<cfif structKeyExists(url,'_cfjavascript_nocache')>
			<cfset attributes.cache = false />
		</cfif>

		<cfset key = getKey( attributes ) />
		
		<cfif attributes.cache and this.cacheExists(attributes,key ) >
			<cfset doInclude( attributes, key ) />
			<cfreturn variables.instance.hasEndTag />
		<cfelse>
			<cfset this.compress(attributes,key) />
			<cfset doInclude( attributes, key ) />	
		</cfif>
		
		<cfreturn variables.instance.hasEndTag />
	</cffunction>
		

	<!--- 
	compress
	 --->
	<cffunction name="compress" returntype="void" output="false" access="private">
		<cfargument name="attributes" required="true" type="Struct" />
		<cfargument name="key" required="true" type="String" />
	
		<cfset var compressor = getCompressor(attributes) />
		<cfset var files = attributes.src />
		<cfset var i = "" />
		<cfset var temp = "" />
		<cfset var jsContent = "/*Generated : #now()#*/" />
		<cfset var jsTemp = "" />
		<cfset var finalPath = expandPath('#attributes.path#/#key#.js') />		
		
		<cfloop array="#files#" index="f">			
			<cfset temp = variables.instance.TEMP & '/' & hash(f) />
			<cfset compressor.compress(f,temp) />			
		</cfloop>				
		
		<cfloop array="#files#" index="f">
			<cfset temp = variables.instance.TEMP & hash(f) />
			<cffile action="read" file="#temp#" variable="jsTemp" />
			<cfset jsContent = jsContent & jsTemp />			
		</cfloop>
		
		<cffile action="write" file="#finalPath#" output="#jsContent#" />
		
	</cffunction>


	<!--- 
	getCompressor
	Return the js compressor instance.
	 --->
	<cffunction name="getCompressor" returntype="any" output="true" access="private">
		<cfargument name="attributes" required="true" type="Struct" />
		<cfreturn createObject("java", "railo.extension.js.RailoJsCompressor").init(attributes.lineBreak, attributes.munge, false, attributes.preserveAllSemiColons, attributes.disableOptimizations) />
	</cffunction>

	<!--- 
	doInclude
	 --->
	<cffunction name="doInclude" returntype="void" output="true" access="private">
		<cfargument name="attributes" required="true" type="Struct" />
		<cfargument name="key" required="true" type="String" />
		
		<cfset var path = attributes.path & key />
			
		<cfoutput>
			<script type="text/javascript" src="#path#.js" charset="#attributes.charset#"></script>	
		</cfoutput>
		
	</cffunction>


	<!--- 
	doIncludeSource
	 --->
	<cffunction name="doIncludeSource" returntype="void" output="true" access="private">
		<cfargument name="attributes" required="true" type="Struct" />
		
		<cfset var files = attributes.src />
		
		<cfoutput>
		<cfloop array="#files#" index="f">
			<script type="text/javascript" src="#f#"></script>	
		</cfloop>
		</cfoutput>
		
	</cffunction>
	
	
	<!--- 
	getKey
	 --->
	<cffunction name="getKey" returntype="String" output="false" access="private">
		<cfargument name="attributes" required="true" type="Struct" />
		
		<cfif structkeyExists(attributes,'filename') and len(attributes.filename)>
			<cfreturn attributes.filename />
		</cfif>
		
		<cfreturn hash(attributes.src.toString()) />			
		
	</cffunction>


	<!--- 
	cacheExists
	 --->
	<cffunction name="cacheExists" returntype="Boolean" output="false" access="private">
		<cfargument name="attributes" required="true" type="Struct" />
		<cfargument name="key" required="true" type="String" />
		
		<cfset var path = attributes.path & key & '.js' />
		<cfif fileExists(expandPath(path)) >
			<cfreturn true />
		</cfif>
		
		<cfreturn false />
		
	</cffunction>

	<!--- 
	normalizePath
	 --->
	<cffunction name="normalizePath" returntype="String" output="false" access="private">
		<cfargument name="str" type="String" required="true" />
		
		<cfif right(arguments.str,1) neq '/'>
			<cfset arguments.str = arguments.str & '/' />
		</cfif>
		
		<cfreturn arguments.str>
	</cffunction>

	
</cfcomponent>