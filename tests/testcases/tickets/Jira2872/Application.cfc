component {
	// THIS LOADS THE DSN CREATOR WHEN INSTALLING CONTENTBOX FOR THE FIRST TIME
	// THIS CAN BE REMOVED AFTER INSTALLATION
	// location("modules/contentbox-dsncreator");
	// Application properties, modify as you see fit
	this.name 				= "ContentBox-Shell-" & hash( getCurrentTemplatePath() );
	this.sessionManagement 	= true;
	this.sessionTimeout 	= createTimeSpan(0,0,45,0);
	this.setClientCookies 	= true;
	this.scriptProtect		= false;

this.datasources.contentbox = {
    class: 'org.hsqldb.jdbcDriver'
	, connectionString: 'jdbc:hsqldb:file:#expandPath("{temp-directory}")#'
};

	// THE DATASOURCE FOR CONTENTBOX MANDATORY
	this.datasource = "contentbox";
	// CONTENTBOX ORM SETTINGS
	this.ormEnabled = true;
	this.ormSettings = {
		// ENTITY LOCATIONS, ADD MORE LOCATIONS AS YOU SEE FIT
		cfclocation=["model","modules"],
		// THE DIALECT OF YOUR DATABASE OR LET HIBERNATE FIGURE IT OUT, UP TO YOU TO CONFIGURE
		//dialect 			= "MySQLwithInnoDB",
		// DO NOT REMOVE THE FOLLOWING LINE OR AUTO-UPDATES MIGHT FAIL.
		dbcreate = "update",
		// FILL OUT: IF YOU WANT CHANGE SECONDARY CACHE, PLEASE UPDATE HERE
		secondarycacheenabled = false,
		cacheprovider		= "ehCache",
		// ORM SESSION MANAGEMENT SETTINGS, DO NOT CHANGE
		logSQL 				= false,
		flushAtRequestEnd 	= false,
		autoManageSession	= false,
		// ORM EVENTS MUST BE TURNED ON FOR CONTENTBOX TO WORK
		skipCFCWithError	= true
	};

}