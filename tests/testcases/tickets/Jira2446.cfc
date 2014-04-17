component extends="org.railo.cfml.test.RailoTestCase"  {


	public function testString() {

		var classpath = directoryList( "#getTicketName()#/lib", false, "path", "*.jar" );

  		var obj = createObject( "java", "org.xbill.DNS.Client", classpath.toList() );
	}


	public function testArray() {

		var classpath = directoryList( "#getTicketName()#/lib", false, "path", "*.jar" );

  		var obj = createObject( "java", "org.xbill.DNS.Client", classpath );
	}


	public function testNone() {

		try {

			var obj = createObject( "java", "org.xbill.DNS.Client" );

			fail( "Expected class not found exception" );
		} 
		catch( ex ) {}
	}


	private function getTicketName() {

		return listFirst( listLast( getCurrentTemplatePath(), '\/' ), '.' );
	}

}