		<cfif attributes.ErrorOutput neq "">
			<cfset "caller.#attributes.ErrorOutput#" = "shit happen!">
		</cfif>
