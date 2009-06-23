<cfset stText.MenuStruct = 
	array(
		struct(
			action:"server",label:"Einstellungen",
			children:array(
				struct(action:"regional",label:"Regionales"),
				struct(action:"component",label:"Komponenten"),
				struct(action:"charset",label:"Charset"),
				struct(action:"scope",label:"Scope"),
				struct(action:"application",label:"Applikation"),
				struct(action:"output",label:"Ausgabe"),
				struct(action:"error",label:"Error")
			)
		),
		struct(
			action:"services",label:"Dienste",
			children:array(
				struct(action:"datasource",label:"Datenquellen"),
				struct(action:"search",label:"Suche",hidden: request.adminType NEQ "web"),
				struct(action:"mail",label:"Mail"),
				struct(action:"tasks",label:"Tasks",xhidden:  	 server.ColdFusion.ProductLevel eq "community" or server.ColdFusion.ProductLevel eq "professional"),
				//struct(action:"video",label:"Video",hidden:server.ColdFusion.ProductLevel eq "community"),
				struct(action:"schedule",label:"Scheduled Tasks",hidden:request.adminType NEQ "web"),
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
				struct(action:"clients",label:"Clients",hidden:  server.ColdFusion.ProductLevel eq "community" or server.ColdFusion.ProductLevel eq "professional")
			)
		),
		
		struct(
			action:"resources",label:"Archive & Ressourcen",
			children:array(
				struct(action:"mappings",label:"Mappings"),
				struct(action:"customtags",label:"Custom Tags"),
				struct(action:"cfx_tags",label:"CFX Tags")
			)
		),
		struct(action:"development",label:"Entwicklung",
			children:array(
				struct(action:"debugging",label:"Debugging")
			)
		),
		struct(action:"security",label:"Sicherheit",
			children:array(
				struct(action:"access",label:"Zugriff",hidden:request.adminType NEQ "server"),
				struct(action:"password",label:"Passwort",display:true)
				//struct(action:"serial",label:"Seriennummer",hidden:request.adminType NEQ "server",display:true)
			)
		),
		struct(action:"documentation",label:"Dokumentation",
			children:array(
				struct(action:"tagRef",label:"Tag Reference"),
				struct(action:"funcRef",label:"Function Reference")
			)
		)
		//,struct(action:"logout",label:"Ausloggen",display:true)
	)>
