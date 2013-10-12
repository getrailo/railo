component {

    this.logfile    = "TaskGateway";

    variables.state = "stopped";


    public function init( string id, struct config, component listener ) {

        variables.id       = arguments.id;
        variables.config   = arguments.config;
        variables.listener = arguments.listener;

        variables.interval = arguments.config.sleep * 1000;

        if ( variables.interval < 1000 )
            variables.interval = 1000;

        log text="Event Gateway #variables.id# initialized with interval of #variables.interval# ms" file=this.logfile;
    }


    public function start() {

        while ( variables.state == "stopping" )
            sleep( 10 );

        variables.state = "running";

        log text="Event Gateway #variables.id# started" file=this.logfile;

        while ( variables.state == "running" ) {

            try {

                this.PerformTask();
            }
            catch ( ex ) {

                log text="Event Gateway #variables.id# error: #ex.message#" file=this.logfile type="error";
            }

            // sleep( variables.interval );
            
            var ts = getTickCount();
            
            while( getTickCount() - ts < variables.interval ) {

                sleep( 1000 );

                if ( variables.state != "running" )
                    break;
            }
        }

        variables.state = "stopped";
        log text="Event Gateway #variables.id# stopped" file=this.logfile;
    }


    public function stop() {

        log text="Event Gateway #variables.id# stopping" file=this.logfile;
        variables.state = "stopping";
    }


    public function restart() {

        if ( variables.state == "running" )
            this.stop();

        this.start();
    }


    public function getState() {

        return variables.state;
    }


    public function sendMessage( struct data={} ) {

        return "ERROR: sendMessage not supported";
    }


    /**
    * override this method in extending components
    */
    public function PerformTask() {

        if ( left( variables.config.script, 4 ) == "http" ) {

            http url=variables.config.script result="local.cfhttp";
        }
        else {

            include template=variables.config.script;
        }
    }

}