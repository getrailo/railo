component {
	remote function undefined(){
		return [];
	}
	remote function returnformat() returnformat="json" {
		return queryNew('a');
	}
	remote function returnformat2() returnformat="wddx" {
		return queryNew('a');
	}
}