<!--- 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ---><cfcomponent extends="railo.core.ajax.AjaxBase">
	
	<cfset variables.tags = 'CFAJAXPROXY,CFDIV,CFWINDOW,CFMAP,CFMENU' />
	
	<!--- Meta data --->
    <cfset this.metadata.hint="Controls the JavaScript files that are imported for use on pages that use Railox AJAX tags and features.">
	<cfset this.metadata.attributetype="fixed">
    <cfset this.metadata.attributes={
		scriptSrc:	{required:false,type:"string",default:"",hint="Specifies the URL, relative to the web root, of the directory that contains the JavaScript files used used by Railo."},
		tags:       {required:false,type:"string",default:"",hint="A comma-delimited list of tags or tag-attribute combinations for which to import the supporting JavaScript files on this page."},
		cssSrc:     {required:false,type:"string",default:"",hint="Specifies the URL, relative to the web root, of the directory that contains the CSS files used by AJAX features"},
		adapter:    {required:false,type:"string",default:"",hint=""},
		params :    {required:false,type:"struct",default:{},hint=""}
	}>
         
    <cffunction name="init" output="no" returntype="void" hint="invoked after tag is constructed">
    	<cfargument name="hasEndTag" type="boolean" required="yes">
      	<cfargument name="parent" type="component" required="no" hint="the parent cfc custom tag, if there is one">
  	</cffunction> 
    
    <cffunction name="onStartTag" output="no" returntype="boolean">
   		<cfargument name="attributes" type="struct">
   		<cfargument name="caller" type="struct">
		
      	<cfset var opts = {} />
		
		<!--- init the base ajax class --->
		<cfif len(attributes.scriptSrc)>
			<cfset opts['scriptSrc'] = attributes.scriptSrc />
		</cfif>
		<cfif len(attributes.cssSrc)>
			<cfset opts['cssSrc'] = attributes.cssSrc />
		</cfif>
		<cfif len(attributes.adapter)>
			<cfset opts['adapter'] = attributes.adapter/>
		</cfif>
		
		<!--- TODO: remove this when railo bug is solved --->
		<cfif not structKeyExists(attributes,'params')>
			<cfset attributes.params = struct() />
		</cfif> 
		
		<cfset opts.params = attributes.params />
				
      	<cfset super.init(argumentCollection:opts)/>
  
		<!--- check --->
		<cfloop list="#attributes.tags#" index="el">
			<cfif listFind(variables.tags,el) eq 0>
				<cfthrow message="tag [#el#] is not a valid value. Valid tag names are [#variables.tags#]" />
			</cfif>	
		</cfloop>
		
        <cfset doImport(argumentCollection=arguments) />
        
        <cfreturn false>
    </cffunction>
	
    <cffunction name="doImport" output="no" returntype="void">
   		<cfargument name="attributes" type="struct">
   		<cfargument name="caller" type="struct">
   		
		<cfset var js = "" />		
	
   		<cfif len(attributes.tags)>
			<cfsavecontent variable="js"><cfoutput>
			<script type="text/javascript">
			<cfloop list="#attributes.tags#" index="el">Railo.Ajax.importTag('#el#');
			</cfloop>
			</script>		
			</cfoutput>
			</cfsavecontent>
			<cfset writeHeader(js,'_import_#el#') />
		</cfif>
	</cffunction>
		
</cfcomponent>