package railo.runtime.exp;

/**
 * This Exception is only thrown after cfcontent with file, to indicate a silent Abort when used in onMissingTemplate
 */
public class PostContentAbort extends Abort {

    public PostContentAbort() {

        super( SCOPE_REQUEST );
    }
}
