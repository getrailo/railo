<cffunction name="ajaxOnLoad" output="true" hint="Causes the specified JavaScript function to run when the page loads.">
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