<cffunction name="ajaxOnLoad" output="true">
	<cfargument name="fn" required="no"/>
	<cfif len(arguments.fn)>
		<!--- load js lib if required --->
		<cfajaximport />
		<!--- subscribe to the onload event --->
		<cfoutput>
			<script type="text/javascript">
			Railo.Events.subscribe(#arguments.fn#,'onLoad');
			</script>
		</cfoutput>
	</cfif>	
</cffunction>