package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;

public class FlowControlFinalImpl implements FlowControlFinal {

	private Label entryLabel;
	private Label gotoLabel;

	public FlowControlFinalImpl(){
		this.entryLabel=new Label();
	}

	@Override
	public Label getFinalEntryLabel() {
		return entryLabel;
	}

	@Override
	public void setAfterFinalGOTOLabel(Label gotoLabel) {
		this.gotoLabel=gotoLabel;
	}

	public Label getAfterFinalGOTOLabel() {
		return gotoLabel;
	}

}
