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
component extends="Gateway" {


    fields = array(
    
          field( "Script To Execute", "script", "", true, "Either a relative script name that will be executed via cfinclude, or a full URL address that will be called via http.", "text" ) 

        , field( "Sleep Time", "sleep", 24 * 60 * 60 * 1, false, "The time to sleep between each execution of the Gateway's task", "time" )
    );


    public function getLabel() {            return "Task Gateway" }

    public function getDescription() {      return "A general purpose event gateway which will perform a task (include a script or make an http request) and then sleep for a certain interval until the next execution." }

    public function getCfcPath() {          return "railo.extension.gateway.TaskGateway"; }


    public function getClass() {            return ""; }

    public function getListenerPath() {     return ""; }


    // public function getListenerCfcMode() {  return "required"; }


    /*/ validate args and throw on failure
    public function onBeforeUpdate( required cfcPath, required startupMode, required custom ) {

        
    }   //*/

}