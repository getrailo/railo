component extends="Gateway" {


    fields = array(
    
          field( "Script To Execute", "script", "", true, "Either a relative script name that will be executed via cfinclude, or a full URL address that will be called via http.", "text" ) 

        , group( "Sleep Time", "The time to sleep between each execution of the Gateway's task",3)
        
        // , field( "Function Name","functionName", "invoke", false, "function to call when there is a new mail", "text" )
        
        , field( "Sleep Days", "sleepDays", 1, false, "The sleep time in days", "text" )
        , field( "Sleep Hours", "sleepHours", 0, false, "The sleep time in hours", "text" )
        , field( "Sleep Minutes", "sleepMinutes", 0, false, "The sleep time in minutes", "text" )
        , field( "Sleep Seconds", "sleepSeconds", 0, false, "The sleep time in seconds", "text" )
    );


    public function getLabel() {            return "Generic Gateway" }

    public function getDescription() {      return "A Generic Gateway which will perform a task (include a script or make an http request) and then sleep for a certain interval until the next execution." }

    public function getCfcPath() {          return "railo.extension.gateway.GenericGateway"; }


    public function getClass() {            return ""; }

    public function getListenerPath() {     return ""; }


    // public function getListenerCfcMode() {  return "required"; }


    /*/ validate args and throw on failure
    public function onBeforeUpdate( required cfcPath, required startupMode, required custom ) {

        
    }   //*/

}