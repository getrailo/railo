component {

	remote function echoAny(data) {
		return arguments.data;
	}

	remote MyItem function echoMyItem(MyItem sct) {
		return arguments.sct;
	}

	remote array function echoArray(array arr) {
		return arguments.arr;
	}
 
	remote MyItem[] function echoMyItemArray(MyItem[] data) {
		//systemOutput(data,true,true);
		return arguments.data;
	} 

	remote MyItem[][] function echoMyItemMyItemArray(MyItem[][] data) {
		return arguments.data;
	}

	remote MyItem[][][] function echoMyItemMyItemMyItemArray(MyItem[][][] data) { 
		return arguments.data;
	}

}