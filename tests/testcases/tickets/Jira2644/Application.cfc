component {

	this.name = hash( getCurrentTemplatePath() );
	
	this.ormEnabled = true;
	this.ormSettings.flushatrequestend = false;
	this.ormSettings.autoManageSession = false;
	this.ormSettings.datasource = "mysql";
	this.ormSettings.dbcreate = "update";
	this.ormSettings.savemapping = true;
	this.ormSettings.eventHandling = true;
	//this.ormSettings.dbcreate = 'dropcreate' ;
	
	public any function onRequestStart() {
		ormReload();
	}
}