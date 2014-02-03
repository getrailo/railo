component extends="org.railo.cfml.test.RailoTestCase"	{
	

	public function testNoFilter() {

		directory name="local.qDir" directory=getTestDir() recurse=true filter="";
		assert(qDir.recordCount == 10);
	}
	

	public function testSuffix() {

		directory name="local.qDir" directory=getTestDir() recurse=true filter="*.js";
		assert(qDir.recordCount == 1);
	}


	public function testContains() {

		directory name="local.qDir" directory=getTestDir() recurse=true filter="*json*";
		assert(qDir.recordCount == 2);
	}


	public function testMultiA() {

		directory name="local.qDir" directory=getTestDir() recurse=true filter="testa*2013*.txt";
		assert(qDir.recordCount == 3);
	}


	public function testMultiB() {

		directory name="local.qDir" directory=getTestDir() recurse=true filter="test?*2014*.txt";
		assert(qDir.recordCount == 5);
	}


	public function testOneA() {

		directory name="local.qDir" directory=getTestDir() recurse=true filter="test?-20140201.txt";
		assert(qDir.recordCount == 2);
	}


	public function testOneB() {

		directory name="local.qDir" directory=getTestDir() recurse=true filter="testa-2013123?.txt";
		assert(qDir.recordCount == 2);
	}


	public function testPrefix() {

		directory name="local.qDir" directory=getTestDir() recurse=true filter="testb*";
		assert(qDir.recordCount == 1);
	}


	function getTestDir() {

		var result = GetCurrentTemplatePath();

		result = left(result, len(result) - 4);

		return result;
	}

}