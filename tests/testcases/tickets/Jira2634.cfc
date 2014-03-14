<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="testCfchartScatterHorizontalbarStep">
<!---
		<cfif directoryExists("./WEB-INF/railo/temp/graph/")>
			<cfdirectory action="delete" directory="./WEB-INF/railo/temp/graph/" recurse="true"/>
		</cfif>
--->
		<cfchart
		   format="png"
		   scalefrom="0"
		   scaleto="1200000"
		      show3d="true"
		   url="index.cfm">
		  <cfchartseries
		      type="horizontalbar"
		      serieslabel="Website Traffic 2006"
		      seriescolor="blue">
		    <cfchartdata item="January" value="503100">
		    <cfchartdata item="February" value="720310">
		    <cfchartdata item="March" value="688700">
		    <cfchartdata item="April" value="986500">
		    <cfchartdata item="May" value="1063911">
		    <cfchartdata item="June" value="1125123">
		  </cfchartseries>
		</cfchart>
		
		<cfchart
		   format="png"
		   url="index.cfm?Series=$SERIESLABEL$&Item=$ITEMLABEL$&Value=$VALUE$"
		   scalefrom="0"
		   scaleto="1200000">
		  <cfchartseries
		      type="step"
		      serieslabel="Website Traffic 2006"
		      seriescolor="blue">
		    <cfchartdata item="January" value="503100">
		    <cfchartdata item="February" value="720310">
		    <cfchartdata item="March" value="688700">
		    <cfchartdata item="April" value="986500">
		    <cfchartdata item="May" value="1063911">
		    <cfchartdata item="June" value="1125123">
		  </cfchartseries>
		</cfchart>
		
		<cfchart
		   format="png"
		   url="index.cfm?Series=$SERIESLABEL$&Item=$ITEMLABEL$&Value=$VALUE$"
		   scalefrom="0"
		   categorylabelpositions="vertical"
		   scaleto="1200000">
		  <cfchartseries
		      type="step"
		      serieslabel="Website Traffic 2006"
		      seriescolor="blue">
		    <cfchartdata item="January" value="503100">
		    <cfchartdata item="February" value="720310">
		    <cfchartdata item="March" value="688700">
		    <cfchartdata item="April" value="986500">
		    <cfchartdata item="May" value="1063911">
		    <cfchartdata item="June" value="1125123">
		  </cfchartseries>
		</cfchart>
		
		<cfchart
		   format="png"
		   url="index.cfm?Series=$SERIESLABEL$&Item=$ITEMLABEL$&Value=$VALUE$"
		   scalefrom="0"
		   scaleto="1200000">
		  <cfchartseries
		      type="step"
		      serieslabel="Website Traffic 2006"
		      seriescolor="blue">
		    <cfchartdata item="10" value="503100">
		    <cfchartdata item="22" value="720310">
		    <cfchartdata item="11" value="688700">
		    <cfchartdata item="33" value="986500">
		    <cfchartdata item="35" value="1063911">
		    <cfchartdata item="56" value="1125123">
		  </cfchartseries>
		</cfchart>
		
		<cfchart
		   format="png"
		   url="index.cfm?Series=$SERIESLABEL$&Item=$ITEMLABEL$&Value=$VALUE$"
		   scalefrom="0"
		   scaleto="1200000">
		  <cfchartseries
		      type="scatter"
		      serieslabel="Website Traffic 2006"
		      seriescolor="blue">
		    <cfchartdata item="January" value="503100">
		    <cfchartdata item="February" value="720310">
		    <cfchartdata item="March" value="688700">
		    <cfchartdata item="April" value="986500">
		    <cfchartdata item="May" value="1063911">
		    <cfchartdata item="June" value="1125123">
		  </cfchartseries>
		</cfchart>
		
		<cfchart
		   format="png"
		   url="index.cfm?Series=$SERIESLABEL$&Item=$ITEMLABEL$&Value=$VALUE$"
		   scalefrom="0"
		   scaleto="1200000">
		  <cfchartseries
		      type="scatter"
		      serieslabel="Website Traffic 2006"
		      seriescolor="blue">
		    <cfchartdata item="10" value="503100">
		    <cfchartdata item="22" value="720310">
		    <cfchartdata item="11" value="688700">
		    <cfchartdata item="33" value="986500">
		    <cfchartdata item="35" value="1063911">
		    <cfchartdata item="56" value="1125123">
		  </cfchartseries>
		</cfchart>
		
		<cfchart
		showtooltip="true"
		   format="png"
		   url="index.cfm?Series=$SERIESLABEL$&Item=$ITEMLABEL$&Value=$VALUE$"
		   scalefrom="0"
		   scaleto="1200000">
		  <cfchartseries
		      type="bar"
		      serieslabel="Website Traffic 2006"
		      seriescolor="blue">
		    <cfchartdata item="January" value="503100">
		    <cfchartdata item="February" value="720310">
		    <cfchartdata item="March" value="688700">
		    <cfchartdata item="April" value="986500">
		    <cfchartdata item="May" value="1063911">
		    <cfchartdata item="June" value="1125123">
		  </cfchartseries>
		</cfchart>
		
		<cfchart
		   format="png"
		   url="index.cfm?Series=$SERIESLABEL$&Item=$ITEMLABEL$&Value=$VALUE$"
		   scalefrom="0"
		   scaleto="1200000">
		  <cfchartseries
		      type="pie"
		      serieslabel="Website Traffic 2006"
		      seriescolor="blue">
		    <cfchartdata item="January" value="503100">
		    <cfchartdata item="February" value="720310">
		    <cfchartdata item="March" value="688700">
		    <cfchartdata item="April" value="986500">
		    <cfchartdata item="May" value="1063911">
		    <cfchartdata item="June" value="1125123">
		  </cfchartseries>
		</cfchart>

	</cffunction>
</cfcomponent>