component extends="org.railo.cfml.test.RailoTestCase"	{

	
	function testZipNoFilter() {

		zip action="zip" source="#getDir()#/src/" file="#getDir()#/dst1.zip" {

		};
	}

	
	function testZipPatternFilter() {

		zip action="zip" source="#getDir()#/src/" file="#getDir()#/dst2.zip" filter="*.html" {

		};
	}

	
	function testZipUDFFilter() {

		zip action="zip" source="#getDir()#/src/" file="#getDir()#/dst3.zip" filter="#function(name) { trace(name); return listLast(name, '.') == "html" }#" {

		};
	}

	
	function testZipParamUDFFilter() {

		zip action="zip" file="#getDir()#/dst4.zip" {

			zipparam source="#getDir()#/src/" filter="#function(name) { trace(name); return listLast(name, '.') == "html" }#";
		};
	}


	function testListUDFFilter() {

		zip action="list" file="#getDir()#/dst1.zip" name="local.qZip" filter="#function(name) { trace("LIST: "&name); return listLast(name, '.') == "html" }#" {

		};

		assert(qZip.recordCount == 1);
	}


	function testDeleteUDFFilter() {

		zip action="list" file="#getDir()#/dst1.zip" name="local.qZip1";

		zip action="delete" file="#getDir()#/dst1.zip" filter="#function(name) { trace("DELETE: "&name); return listLast(name, '.') == "html" }#" {

		};

		zip action="list" file="#getDir()#/dst1.zip" name="local.qZip2";

		assert(qZip1.recordCount == (qZip2.recordCount + 1) );
	}


	private function getDir() {

		return listFirst( getCurrentTemplatePath(), '.' );
	}


	public function afterTests() {

		var qDir = directoryList( getDir(), false, "path", "*.zip" );

		for (local.d in qDir) {

			fileDelete(d);
		}
	}
}