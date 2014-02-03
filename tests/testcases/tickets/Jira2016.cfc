<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="testCfhartURL">
		<cfchart
		   format="png"
		   scalefrom="0"
		   scaleto="1200000"
		   url="index.cfm">
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
		<cfchart
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
		   url="index.cfm?event=DrillDown&key=$ITEMLABEL$"
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
	</cffunction>
</cfcomponent>