package org.yawlfoundation.yawl.editor.actions.net;

import java.awt.event.ActionEvent;

public class CheckProcessCorrectness extends YAWLSelectedNetAction{

	 {
		    putValue(SHORT_DESCRIPTION, "Check Configuration Correctness");
		    putValue(NAME, "Check Configuration Correctness");
		    putValue(LONG_DESCRIPTION, "Check Configuration Correctness");
        putValue(SMALL_ICON, getPNGIcon("tick"));

		  }
	 
	 private boolean selected = false;

	 public void actionPerformed(ActionEvent event) {
		 selected = !selected;
		 if (selected) {
			 getGraph().createServiceAutonomous();
		 }
     else {
			 getGraph().setServiceAutonomous(null);
		 }
	 }

	 
}
