<cfsetting showdebugoutput="no">

<cffunction name="testTagTF" output="false">
	<cftry>
		<cfreturn "tag:in try;">
		<cffinally>
			<cfreturn "tag:in finally;">
		</cffinally>
	</cftry>
</cffunction>

<cffunction name="testTagTCF" output="false">
	<cftry>
		<cfreturn "tag:in try">
		<cfcatch>
			<cfreturn "tag:in catch;">
		</cfcatch>
		<cffinally>
			<cfreturn "tag:in finally;">
		</cffinally>
	</cftry>
</cffunction>

<cfscript>
	function testScriptTF(){
		try {
			return "script:in try;";
		}
		finally {
			return "script:in finally;";
		}
	}

	function testScriptTCF(){
		try {
			return "script:in try;";
		}
		catch (any e){
			return "script:in catch;";
		}
		finally {
			return "script:in finally;";
		}
	}

	if(isdefined("url.tagtf"))echo(testTagTF());
	if(isdefined("url.tagtcf"))echo(testTagTCF());
	if(isdefined("url.scripttf"))echo(testScriptTF());
	if(isdefined("url.scripttcf"))echo(testScriptTCF());
</cfscript>
