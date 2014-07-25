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

package org.yawlfoundation.yawl.monitor.jsf;

import com.sun.rave.web.ui.appbase.AbstractPageBean;
import com.sun.rave.web.ui.component.*;

import org.yawlfoundation.yawl.monitor.sort.ItemOrder;
import org.yawlfoundation.yawl.monitor.sort.TableSorter;
import org.yawlfoundation.yawl.resourcing.jsf.MessagePanel;

import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlOutputText;

/**
 *  Backing bean for the active cases page.
 *
 *  @author Raffaele Conforti
 *  13/09/2010
 */

public class SensorInfo extends AbstractPageBean {

    private int __placeholder;

    private void _init() throws Exception { }

    /** Constructor */
    public SensorInfo() { }


     // Return references to scoped data beans //

    protected SessionBean getSessionBean() {
        return (SessionBean)getBean("SessionBean");
    }

    protected ApplicationBean getApplicationBean() {
        return (ApplicationBean)getBean("ApplicationBean");
    }


    @Override
	public void init() {
        super.init();

        // *Note* - this logic should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("caseItems Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
        }
    }

    @Override
	public void preprocess() { }

    @Override
	public void destroy() { }


    /*********************************************************************************/

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


    private StaticText staticText = new StaticText();

    public StaticText getStaticText() { return staticText; }

    public void setStaticText(StaticText st) { staticText = st; }


    private StaticText sensorTrigger = new StaticText();

    public StaticText getSensorTrigger() { return sensorTrigger; }

    public void setSensorTrigger(StaticText st) { sensorTrigger = st; }


    private StaticText sensorCaseID = new StaticText();

    public StaticText getSensorCaseID() { return sensorCaseID; }

    public void setSensorCaseID(StaticText st) { sensorCaseID = st; }


    private StaticText sensorCaseIDText = new StaticText();

    public StaticText getSensorCaseIDText() { return sensorCaseIDText; }

    public void setSensorCaseIDText(StaticText st) { sensorCaseIDText = st; }


    private StaticText sensorName = new StaticText();

    public StaticText getSensorName() { return sensorName; }

    public void setSensorName(StaticText st) { sensorName = st; }


    private StaticText sensorNameText = new StaticText();

    public StaticText getSensorNameText() { return sensorNameText; }

    public void setSensorNameText(StaticText st) { sensorNameText = st; }


    private StaticText sensorTimeStamp = new StaticText();

    public StaticText getSensorTimeStamp() { return sensorTimeStamp; }

    public void setSensorTimeStamp(StaticText st) { sensorTimeStamp = st; }


    private StaticText sensorTimeStampText = new StaticText();

    public StaticText getSensorTimeStampText() { return sensorTimeStampText; }

    public void setSensorTimeStampText(StaticText st) { sensorTimeStampText = st; }


    private TextArea sensorTriggerText = new TextArea();

    public TextArea getSensorTriggerText() { return sensorTriggerText; }

    public void setSensorTriggerText(TextArea ta) { sensorTriggerText = ta; }
    
    
    private StaticText sensorConsText = new StaticText();

    public StaticText getSensorConsText() { return sensorConsText; }

    public void setSensorConsText(StaticText st) { sensorConsText = st; }


    private PanelLayout layoutPanel = new PanelLayout();

    public PanelLayout getLayoutPanel() { return layoutPanel; }

    public void setLayoutPanel(PanelLayout pl) { layoutPanel = pl; }


    private HtmlDataTable dataTable = new HtmlDataTable();

    public HtmlDataTable getDataTable() { return dataTable; }

    public void setDataTable(HtmlDataTable hdt) { dataTable = hdt; }


    private UIColumn colItemID = new UIColumn();

    public UIColumn getColItemID() { return colItemID; }

    public void setColItemID(UIColumn uic) { colItemID = uic; }


    private HtmlOutputText colItemIDRows = new HtmlOutputText();

    public HtmlOutputText getColItemIDRows() { return colItemIDRows; }

    public void setColItemIDRows(HtmlOutputText hot) { colItemIDRows = hot; }


    private UIColumn colTaskID = new UIColumn();

    public UIColumn getColTaskID() { return colTaskID; }

    public void setColTaskID(UIColumn uic) { colTaskID = uic; }


    private HtmlOutputText colTaskIDRows = new HtmlOutputText();

    public HtmlOutputText getColTaskIDRows() { return colTaskIDRows; }

    public void setColTaskIDRows(HtmlOutputText hot) { colTaskIDRows = hot; }


    private UIColumn colStatus = new UIColumn();

    public UIColumn getColStatus() { return colStatus; }

    public void setColStatus(UIColumn uic) { colStatus = uic; }


    private HtmlOutputText colStatusRows = new HtmlOutputText();

    public HtmlOutputText getColStatusRows() { return colStatusRows; }

    public void setColStatusRows(HtmlOutputText hot) { colStatusRows = hot; }


    private UIColumn colService = new UIColumn();

    public UIColumn getColService() { return colService; }

    public void setColService(UIColumn uic) { colService = uic; }


    private HtmlOutputText colServiceRows = new HtmlOutputText();

    public HtmlOutputText getColServiceRows() { return colServiceRows; }

    public void setColServiceRows(HtmlOutputText hot) { colServiceRows = hot; }


    private UIColumn colEnabledTime = new UIColumn();

    public UIColumn getColEnabledTime() { return colEnabledTime; }

    public void setColEnabledTime(UIColumn uic) { colEnabledTime = uic; }


    private HtmlOutputText colEnabledTimeRows = new HtmlOutputText();

    public HtmlOutputText getColEnabledTimeRows() { return colEnabledTimeRows; }

    public void setColEnabledTimeRows(HtmlOutputText hot) { colEnabledTimeRows = hot; }


    private UIColumn colStartTime = new UIColumn();

    public UIColumn getColStartTime() { return colStartTime; }

    public void setColStartTime(UIColumn uic) { colStartTime = uic; }


    private HtmlOutputText colStartTimeRows = new HtmlOutputText();

    public HtmlOutputText getColStartTimeRows() { return colStartTimeRows; }

    public void setColStartTimeRows(HtmlOutputText hot) { colStartTimeRows = hot; }


    private UIColumn colCompletionTime = new UIColumn();

    public UIColumn getColCompletionTime() { return colCompletionTime; }

    public void setColCompletionTime(UIColumn uic) { colCompletionTime = uic; }


    private HtmlOutputText colCompletionTimeRows = new HtmlOutputText();

    public HtmlOutputText getColCompletionTimeRows() { return colCompletionTimeRows; }

    public void setColCompletionTimeRows(HtmlOutputText hot) { colCompletionTimeRows = hot; }


    private UIColumn colTimerStatus = new UIColumn();

    public UIColumn getColTimerStatus() { return colTimerStatus; }

    public void setColTimerStatus(UIColumn uic) { colTimerStatus = uic; }


    private HtmlOutputText colTimerStatusRows = new HtmlOutputText();

    public HtmlOutputText getColTimerStatusRows() { return colTimerStatusRows; }

    public void setColTimerStatusRows(HtmlOutputText hot) { colTimerStatusRows = hot; }


    private UIColumn colTimerExpiry = new UIColumn();

    public UIColumn getColTimerExpiry() { return colTimerExpiry; }

    public void setColTimerExpiry(UIColumn uic) { colTimerExpiry = uic; }


    private HtmlOutputText colTimerExpiryRows = new HtmlOutputText();

    public HtmlOutputText getColTimerExpiryRows() { return colTimerExpiryRows; }

    public void setColTimerExpiryRows(HtmlOutputText hot) { colTimerExpiryRows = hot; }


    private HiddenField hdnRowIndex = new HiddenField();

    public HiddenField getHdnRowIndex() { return hdnRowIndex; }

    public void setHdnRowIndex(HiddenField hf) { hdnRowIndex = hf; }


    private PanelLayout pnlContainer ;

    public PanelLayout getPnlContainer() { return pnlContainer; }

    public void setPnlContainer(PanelLayout pnl) { pnlContainer = pnl; }


    private PanelGroup pnlGroup ;

    public PanelGroup getPnlGroup() { return pnlGroup; }

    public void setPnlGroup(PanelGroup group) { pnlGroup = group; }


    private PanelGroup pnlGroupCaseData ;

    public PanelGroup getPnlGroupCaseData() { return pnlGroupCaseData; }

    public void setPnlGroupCaseData(PanelGroup group) { pnlGroupCaseData = group; }

    
    private Button btnDetails = new Button();

    public Button getBtnDetails() { return btnDetails; }

    public void setBtnDetails(Button b) { btnDetails = b; }
    

    /*******************************************************************************/

    private final SessionBean _sb = getSessionBean();
    private final MessagePanel msgPanel = _sb.getMessagePanel();


    /**
     * Overridden method that is called immediately before the page is rendered
     */
    @Override
	public void prerender() {
        _sb.showMessagePanel();
    }


    public String btnDelete_action() {
        _sb.deleteSensorNotification(_sb.sensorNotification);
        _sb.refreshSensorNotification(false);
        return "showSensorNotification";
    }

    public String btnLogout_action() {
        _sb.doLogout();
        return "loginPage";
    }


    public String btnDetails_action() {
        Integer selectedRowIndex = new Integer((String) hdnRowIndex.getValue()) - 1;
        _sb.setItemSelection(selectedRowIndex);
        return "showParameters";
    }

    public String btnBack_action() {
        _sb.refreshActiveCases(false);
        return "showSensorNotification";
    }
    
    public String btnMitigate_action() {
        _sb.mitigate(_sb.sensorNotification);
        return "showSensorMitigation";
    }

}