<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	<cffunction name="testCfchartCategoryPositions">
	<!--- not specified, should default to horizontal --->
		<cfchart
		   format="png"
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
		     CategoryLabelPositions="horizontal"
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
		     CategoryLabelPositions="vertical"
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
		     CategoryLabelPositions="up_45"
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
		     CategoryLabelPositions="up_90"
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
		     CategoryLabelPositions="down_45"
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
		     CategoryLabelPositions="down_90"
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