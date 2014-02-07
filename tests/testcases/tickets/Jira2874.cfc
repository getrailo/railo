<cfcomponent output="false" extends="org.railo.cfml.test.RailoTestCase">
	
	<cffunction name="WhitespaceAfterArgs" output="false">
		<cfset x = () ->5+1 />
		<cfset assertEquals(expected:6, actual: x()) />
	</cffunction>

	<cffunction name="NoWhitespaceAfterArgs" output="false">
		<cfset x = ()->5+1 />
		<cfset assertEquals(expected:6, actual: x()) />
	</cffunction>

	<cffunction name="argsWork" output="false" >
		<cfset x = (x,y)->x + y />
		<cfset assertEquals(expected:6, actual: x(1,5)) />
	</cffunction>
		
	<cffunction name="whitespaceAfterLambdaOperator">
		<cfset x = (x)-> x * x />
		<cfset assertEquals(expected:4, actual: x(2)) />
	</cffunction>
	
	<cffunction name="cfscript">
		<cfscript>
			local.x = (x)->x*x;
			assertEquals(expected:9, actual: local.x(3));
		</cfscript>
	</cffunction>

	<cffunction name="enclosedVars">
		<cfset local.y = 5 />
		<cfset local.lambda = (x) -> x+y />
		<cfset assertEquals(expected:7, actual:local.lambda(2)) />
	</cffunction>	

	<cffunction name="otherFunction" test="false">
		<cfargument name="x">
		<cfreturn 5+x />
	</cffunction>

	<cffunction name="CallAnotherFunction">
		<cfset local.lambda = (x) -> otherFunction(x) />
		<cfset assertEquals(expected:8, actual:local.lambda(3)) />
	</cffunction>	
	
	<cffunction name="LambdaBegetsLambda">
		<cfset local.ll = (x) -> (y,z) -> x+y+z  />
		<cfset local.lambda = local.ll(5) />
		<cfset assertEquals(expected:10, actual:local.lambda(3,2)) />
	</cffunction>	
</cfcomponent>