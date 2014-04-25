package railo.intergral.fusiondebug.server.type;

import com.intergral.fusiondebug.server.FDLanguageException;
import com.intergral.fusiondebug.server.FDMutabilityException;
import com.intergral.fusiondebug.server.IFDValue;

public abstract class FDValueNotMutability implements IFDValue {

	
	@Override
	public final boolean isMutable() {
		return false;
	}

	@Override
	public final void set(String arg0) throws FDMutabilityException,FDLanguageException {
		throw new FDMutabilityException();
	}

}
