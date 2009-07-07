package railo.runtime;

import railo.runtime.type.Objects;
import railo.runtime.type.scope.Variables;

public interface ComponentScope extends Variables,Objects {


    /**
     * Returns the value of component.
     * @return value component
     */
    public ComponentImpl getComponent();
    public void setComponent(ComponentImpl c);

}