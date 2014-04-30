component output="false" persistent="false" {

	remote Item function returnThingWithDate () {
		local.item = new Item();
		item.date=now();
		item.number=1;
		return local.item;
	}
}