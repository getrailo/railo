component persistent="true" table="bestsellers2644"  
{
 property name="gender" fieldtype="id" generator="assigned";
 property name="sortorder" fieldtype="id" generator="assigned";
 property name="magid";

 function init() {
  return this;
 }
}