<cfset stText.MenuStruct =
	array(
		struct(
			action:"server",label:"Settings",
			children:array(
				struct(action:"cache",label:"Performance/Caching"),
				struct(action:"regional",label:"Regional"),
				struct(action:"component",label:"Component"),
				struct(action:"charset",label:"Charset"),
				struct(action:"scope",label:"Scope"),
				struct(action:"application",label:"Application"),
				struct(action:"output",label:"Output"),
				struct(action:"error",label:"Error")
			)
		),
		struct(
			action:"services",label:"Services",
			children:array(
				struct(action:"cache",label:"Cache (Beta)"),
				struct(action:"datasource",label:"Datasource"),
				//struct(action:"gateway",label:"Gateway (Beta)"),
				struct(action:"search",label:"Search",hidden: request.adminType NEQ "web"),
				struct(action:"mail",label:"Mail"),
				struct(action:"tasks",label:"Tasks",xhidden: server.ColdFusion.ProductLevel eq "community" or server.ColdFusion.ProductLevel eq "professional"),
				//struct(action:"video",label:"Video",hidden:  	 server.ColdFusion.ProductLevel eq "community"),
				struct(action:"schedule",label:"Scheduled tasks",hidden:request.adminType NEQ "web"),
				struct(action:"update",label:"Update",hidden:request.adminType EQ "web",display:true),
				struct(action:"restart",label:"Restart",hidden:request.adminType EQ "web",display:true)
			)
		),
		struct(
			action:"extension",label:"Extension",
			children:array(
				struct(action:"applications",label:"Applications"),
				struct(action:"providers",label:"Providers")
			)
		),
		struct(
			action:"remote",label:"Remote",
			children:array(
				struct(action:"securityKey",label:"Security Key",display:true),
				struct(action:"clients",label:"Clients",hidden: server.ColdFusion.ProductLevel eq "community" or server.ColdFusion.ProductLevel eq "professional")
			)
		),

		struct(
			action:"resources",label:"Archives &amp; Resources",
			children:array(
				struct(action:"mappings",label:"Mappings"),
				struct(action:"customtags",label:"Custom tags"),
				struct(action:"cfx_tags",label:"CFX tags")
			)
		),
		struct(action:"development",label:"Development",
			children:array(
				struct(action:"debugging",label:"Debugging")
			)
		),
		struct(action:"security",label:"Security",
			children:array(
				struct(action:"access",label:"Access",hidden:request.adminType NEQ "server"),
				struct(action:"password",label:"Password",display:true)
				,struct(action:"serial",label:"Serial number",hidden:request.adminType NEQ "server" or server.ColdFusion.ProductLevel NEQ "enterprise",display:true)
			)
		),
		struct(action:"documentation",label:"Documentation",
			children:array(
				struct(action:"tagRef",label:"Tag Reference"),
				struct(action:"funcRef",label:"Function Reference")
			)
		)
		//,struct(action:"logout",label:"Logout",display:true)
	)>
