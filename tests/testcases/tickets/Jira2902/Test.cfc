component extends="Abs"	{
	
	public void function testDirect(){
		echo("{before:Test.cfc}");
		_test();
		echo("{after:Test.cfc}");
	}
	public void function testIndirect(){
		execute(function() {
			echo("{before:Test.cfc}");
			_test();
			echo("{after:Test.cfc}");
		});
	}
}