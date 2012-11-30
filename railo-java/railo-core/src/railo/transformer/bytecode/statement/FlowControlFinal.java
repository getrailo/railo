package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;

public interface FlowControlFinal {
	public Label getFinalEntryLabel();
	public void setAfterFinalGOTOLabel(Label label);
	public Label getAfterFinalGOTOLabel(); 
}
