package railo.runtime.net.ipsettings;


import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * this class represents the * match so it is used as a root of the node tree and matches all addresses
 *
 * @param <T>
 */
public final class IPRangeNodeRoot<T> extends IPRangeNode<T> {


	public IPRangeNodeRoot() throws UnknownHostException {

		super( "0" );
	}


	@Override
	public boolean isInRange(InetAddress addr) {

		return true;
	}


	@Override
	public boolean containsRange(IPRangeNode other) {

		return true;
	}

}
