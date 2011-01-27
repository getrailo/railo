package railo.runtime.type.scope;

import railo.runtime.type.RequestScope;
import railo.runtime.type.scope.storage.StorageScope;

public interface ClientPlus extends Client,RequestScope,StorageScope {

}
