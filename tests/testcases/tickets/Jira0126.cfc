<cfcomponent extends="org.railo.cfml.test.RailoTestCase">
	
	<cffunction name="setUp"></cffunction>
	<cffunction name="test">
		<cfimport prefix="t" taglib="./Jira0126"> 
		
<cfsavecontent variable="content">
<t:_asso_tree>
	<t:_asso_node level="1">
		<t:_asso_node level="2">
			<t:_asso_node level="3">
				<t:_asso_node level="4" />
			</t:_asso_node>
		</t:_asso_node>
	</t:_asso_node>
</t:_asso_tree>
</cfsavecontent>


<cfset assertEquals("tree: {node: {level: 1,node: {level: 2,node: {level: 3,node: {level: 4}}}}}",trim(content))>
	
	</cffunction>
</cfcomponent>