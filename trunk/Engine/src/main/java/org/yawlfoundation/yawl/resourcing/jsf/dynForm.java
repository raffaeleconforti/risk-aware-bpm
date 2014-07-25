/*
 * Copyright (c) 2004-2010 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.resourcing.jsf;

import java.util.List;

import com.sun.rave.web.ui.appbase.AbstractPageBean;
import com.sun.rave.web.ui.component.*;

import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.resourcing.WorkQueue;
import org.yawlfoundation.yawl.resourcing.jsf.dynform.DynFormFactory;
import org.yawlfoundation.yawl.resourcing.jsf.dynform.SubPanel;
import org.yawlfoundation.yawl.risk.prediction.OperationalSupportExecutionHelper;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.event.ActionEvent;

/**
 *  Backing bean for the dynamic forms.
 *
 *  @author Michael Adams
 *  BPM Group, QUT Australia
 *  v0.1, 08/01/2008
 *
 *
 *  Last Date: 16/03/2008
 */

public class dynForm extends AbstractPageBean {

    // Constructor
     public dynForm() { }

    /****** JSF Required Members and Methods ***************************************/

    private int __placeholder;

    private void _init() throws Exception { }

    /** @return a reference to the application scoped data bean. */
    protected ApplicationBean getApplicationBean() {
        return (ApplicationBean) getBean("ApplicationBean");
    }

    /** @return a reference to the session scoped data bean. */
    protected SessionBean getSessionBean() {
        return (SessionBean) getBean("SessionBean");
    }

    /** @return a reference to the session scoped factory bean. */
    private DynFormFactory getDynFormFactory() {
        return (DynFormFactory) getBean("DynFormFactory");
    }

    @Override
	public void init() {
        super.init();

        // *Note* - JSF requirement this block should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("dynForm Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }
    }

    /****** Abstract Method Implementations *************************************/

    @Override
	public void preprocess() { }

    @Override
	public void destroy() { }


    /****** Page Components ****************************************************/

    private Page page1 = new Page();
    
    public Page getPage1() { return page1; }
    
    public void setPage1(Page p) { page1 = p; }


    private Html html1 = new Html();
    
    public Html getHtml1() { return html1; }
    
    public void setHtml1(Html h) { html1 = h; }


    private Head head1 = new Head();
    
    public Head getHead1() { return head1; }
    
    public void setHead1(Head h) { head1 = h; }


    private Link link1 = new Link();
    
    public Link getLink1() { return link1; }
    
    public void setLink1(Link l) { link1 = l; }


    private Body body1 = new Body();
    
    public Body getBody1() { return body1; }
    
    public void setBody1(Body b) { body1 = b; }


    private Form form1 = new Form();
    
    public Form getForm1() { return form1; }
    
    public void setForm1(Form f) { form1 = f; }


    private StaticText txtHeader = new StaticText();

    public StaticText getTxtHeader() { return txtHeader; }

    public void setTxtHeader(StaticText st) { txtHeader = st; }


    private Button btnOK = new Button();

    public Button getBtnOK() { return btnOK; }

    public void setBtnOK(Button b) { btnOK = b; }


    private Button btnCancel = new Button();

    public Button getBtnCancel() { return btnCancel; }

    public void setBtnCancel(Button b) { btnCancel = b; }


    private Button btnComplete = new Button();

    public Button getBtnComplete() { return btnComplete; }

    public void setBtnComplete(Button b) { btnComplete = b; }
    
    
    private Button btnPredict = new Button();

    public Button getBtnPredict() { return btnPredict; }

    public void setBtnPredict(Button b) { btnPredict = b; }
    
    
    private HtmlGraphicImage imgRiskIndicator = new HtmlGraphicImage();

    public HtmlGraphicImage getImgRiskIndicator() { return imgRiskIndicator; }

    public void setImgRiskIndicator(HtmlGraphicImage h) { imgRiskIndicator = h; }


    private PanelLayout pnlContainer ;

    public PanelLayout getPnlContainer() { return pnlContainer; }

    public void setPnlContainer(PanelLayout pnl) { pnlContainer = pnl; }


    private PanelLayout bottomPanel;

    public PanelLayout getBottomPanel() { return bottomPanel; }

    public void setBottomPanel(PanelLayout pnl) { bottomPanel = pnl; }

    /****** Custom Methods ******************************************************/

    private final SessionBean _sb = getSessionBean();


    @Override
	public void prerender() {
        _sb.checkLogon();
        _sb.setActivePage(ApplicationBean.PageRef.dynForm);
        _sb.showMessagePanel();
        setupButtons();
        loadBackground();
    }


    /**
     * Updates workitem parameters with values added by user
     * @return a reference to the referring page
     */
    public String btnOK_action() {
        if (! getDynFormFactory().validateInputs())
            return null;

        return saveForm();
    }


    /**
     * Same as btnOK, but also sets a flag to complete the item directly.
     * Note: this button is only visible for item edits, and not for case starts
     * @return a reference to the referring page
     */
    public String btnComplete_action() {
        if (! getDynFormFactory().validateInputs())
            return null;

        _sb.setCompleteAfterEdit(true);
        return saveForm();
    }
    
    
    /**
     * Same as btnOK, but also sets a flag to predict the item directly.
     * Note: this button is only visible for item edits, and not for case starts
     * @return a reference to the referring page
     */
    public void btnPredict_action() {//FIXME
    	updateRisk();
    }


    /**
     * Returns to referring page without saving changed values
     * @return a reference to the referring page
     */
    public String btnCancel_action() {
        String refPage = getReferringPage();
        if (refPage.equals("showVisualiser")) {
            _sb.setVisualiserReferred(false);
            _sb.setVisualiserEditedWIR(null);
        }
        return refPage;
    }


    /**
     * Sets flags in sessionBean to action the form save
     * @return the name of the page that called this dynform
     */
    private String saveForm() {
        if (_sb.getDynFormType() == ApplicationBean.DynFormType.netlevel) {
            _sb.setCaseLaunch(true);                        // temp flag for post action
        }
        else {
            _sb.setWirEdit(true) ;
        }
        return getReferringPage() ;
    }


    private String getReferringPage() {
        String result;
        if (_sb.getDynFormType() == ApplicationBean.DynFormType.netlevel) {
           result = "showCaseMgt";
        }
        else if (_sb.isVisualiserReferred()) {
            result = "showVisualiser" ;
        }
        else if (_sb.isRssFormDisplay()) {
            result = "showRssForm";
        }
        else {
            result = "showUserQueues";
        }
        return result;
    }


    /********************************************************************************/

    private void setupButtons() {
        if (getSessionBean().getDynFormType() == ApplicationBean.DynFormType.netlevel) {
            btnOK.setText("Start");                        // start new case
            btnComplete.setVisible(false);                 // hide for case starts
            btnPredict.setVisible(false);                 // hide for case starts
        }
        else {
            btnOK.setText("Save");                         // save workitem edits
        }
    }

    /**
     * Loads the background image or colour for the entire page (not the form within)
     */
    private void loadBackground() {
        String imageURL = getDynFormFactory().getPageBackgroundURL();
        if (imageURL != null) {
           body1.setImageURL(imageURL);  
        }
        else {
            String bgColor = getDynFormFactory().getPageBackgroundColour();
            if (bgColor != null) {
                body1.setStyle("background-color: " + bgColor);
            }
        }
    }

    // adds or substracts an instance of fields for a complex type
    public String btnOccursAction(ActionEvent event) {
        Button source = (Button) event.getComponent() ;
        SubPanel parent = (SubPanel) source.getParent();
        String btnType = (String) source.getText();
        getApplicationBean().refresh();
        getDynFormFactory().processOccursAction(parent, btnType);
        return null;
    }
    
    
    /********************************************************************************/

	/**
	 * Loads the background image or colour for the entire page based in risk level (not the form within)
	 */
	private void updateRisk() {
		
        WorkItemRecord wir = _sb.getChosenWIR(WorkQueue.STARTED);
		
		String resource = _sb.getParticipant().getID();
		String data = _sb.getDynFormFactoryInstance().getDataList();
		
		int prediction = 0;
		try {
			YSpecificationID specID = new YSpecificationID(wir.getSpecIdentifier(), wir.getSpecVersion(), wir.getSpecURI());
			List<SpecificationData> specDatas = _sb.getLoadedSpecs();
			SpecificationData specData = null;
			for(SpecificationData specData2 : specDatas) {
				if(specData2.getID().equals(specID)) {
					specData = specData2;
				}
			}			
			prediction = OperationalSupportExecutionHelper.getInstance().getPrediction(wir, resource, data, specData);
		}catch(Exception e) {
//			prediction = 100;
			e.printStackTrace();
		}
		
		imgRiskIndicator.setUrl("resources/risk"+prediction+".png");
		
	}
}

