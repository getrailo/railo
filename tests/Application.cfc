component {
	this.name = hash( getCurrentTemplatePath() );
    request.webadminpassword="server";
	
	// make sure testbox exists 
	// TODO cache this test for a minute
	try{
		getComponentMetaData("testbox.system.testing.TestBox");
	}
	catch(e){
		// only add mapping when necessary
		this.componentpaths = [{archive:getDirectoryFromPath(getCurrentTemplatePath())&"testbox.ra"}]; // "{railo-server}/context/testbox.ra"
	}
	

}