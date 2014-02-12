component extends="AbsAbs"	{
	
	public void function _test(){
		echo("{before:Abs.cfc}");
		super._test();
		echo("{after:Abs.cfc}");
	}
}