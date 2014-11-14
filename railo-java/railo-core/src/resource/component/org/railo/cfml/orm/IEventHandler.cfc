/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
/**
 * Event handler for ORM Events. This should be used as a global application wide handler that can be set in the application
 * using ormsettings.eventHandler=MyEventHandler. These events can be handled by the application to perform any pre or post 
 * actions for all ORM operations.
 */
Interface
{
	/**
     * Called before injecting property values into a newly loaded entity instance.
	 */
	public void function preLoad(any entity);
	
    /**
     * Called after an entity is fully loaded.
     */
	public void function postLoad(any entity);

   /**
    * Called before inserting the enetity into the database.
    */
	public void function preInsert(any entity);
	
    /**
     * Called after the entity is inserted into the database. 
     */
	public void function postInsert(any entity);
    
    /**
     * Called before the entity is updated in the database.
     */
    public void function preUpdate(any entity, Struct oldData);
	
    /**
     * Called after the entity is updated in the database. 
     */
    public void function postUpdate(any entity);
	

    /**
     * Called before the entity is deleted from the database. 
     */
    public void function preDelete(any entity);
	
    /**
     * Called after deleting an item from the datastore
     */
    public void function postDelete(any entity);
}
