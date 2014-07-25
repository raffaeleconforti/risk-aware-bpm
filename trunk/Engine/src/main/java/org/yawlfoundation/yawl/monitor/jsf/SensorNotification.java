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

import org.yawlfoundation.yawl.monitor.sort.SensorOrder;
import org.yawlfoundation.yawl.monitor.sort.TableSorter;

import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlOutputText;

/**
 *  Backing bean for the sensor notification page.
 *
 *  @author Raffaele Conforti
 *  13/09/2010
 */

public class SensorNotification extends AbstractPageBean {

    private int __placeholder;

    private void _init() throws Exception { }

    /** Constructor */
    public SensorNotification() { }


    // Return references to scoped data beans //

   protected SessionBean getSessionBean() {
       return (SessionBean)getBean("SessionBean");
   }

   protected ApplicationBean getApplicationBean() {
       return (ApplicationBean)getBean("ApplicationBean");
   }


   public void init() {
       super.init();

       // *Note* - this logic should NOT be modified
       try {
           _init();
       } catch (Exception e) {
           log("Sensor Notification Initialization Failure", e);
           throw e instanceof FacesException ? (FacesException) e: new FacesException(e);
       }
   }

   public void preprocess() { }

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


   private StaticText stUptime = new StaticText();

   public StaticText getStUptime() { return stUptime; }

   public void setStUptime(StaticText st) { stUptime = st; }


   private PanelLayout layoutPanel = new PanelLayout();

   public PanelLayout getLayoutPanel() { return layoutPanel; }

   public void setLayoutPanel(PanelLayout pl) { layoutPanel = pl; }


   private HtmlDataTable dataTable = new HtmlDataTable();

   public HtmlDataTable getDataTable() { return dataTable; }

   public void setDataTable(HtmlDataTable hdt) { dataTable = hdt; }


   private UIColumn colSensorCaseID = new UIColumn();

   public UIColumn getColSensorCaseID() { return colSensorCaseID; }

   public void setColSensorCaseID(UIColumn uic) { colSensorCaseID = uic; }


   private HtmlOutputText colSensorCaseIDRows = new HtmlOutputText();

   public HtmlOutputText getColSensorCaseIDRows() { return colSensorCaseIDRows; }

   public void setColSensorCaseIDRows(HtmlOutputText hot) { colSensorCaseIDRows = hot; }


   private UIColumn colSensorName = new UIColumn();

   public UIColumn getColSensorName() { return colSensorName; }

   public void setColSensorName(UIColumn uic) { colSensorName = uic; }


   private HtmlOutputText colSensorNameRows = new HtmlOutputText();

   public HtmlOutputText getColSensorNameRows() { return colSensorNameRows; }

   public void setColSensorNameRows(HtmlOutputText hot) { colSensorNameRows = hot; }
   
   
   private UIColumn colSensorMessage = new UIColumn();

   public UIColumn getColSensorMessage() { return colSensorMessage; }

   public void setColSensorMessage(UIColumn uic) { colSensorMessage = uic; }


   private HtmlOutputText colSensorMessageRows = new HtmlOutputText();

   public HtmlOutputText getColSensorMessageRows() { return colSensorMessageRows; }

   public void setColSensorMessageRows(HtmlOutputText hot) { colSensorMessageRows = hot; }
   
   
   private UIColumn colSensorStatus = new UIColumn();

   public UIColumn getColSensorStatus() { return colSensorStatus; }

   public void setColSensorStatus(UIColumn uic) { colSensorStatus = uic; }


   private HtmlOutputText colSensorStatusRows = new HtmlOutputText();

   public HtmlOutputText getColSensorStatusRows() { return colSensorStatusRows; }

   public void setColSensorStatusRows(HtmlOutputText hot) { colSensorStatusRows = hot; }
   
   
   private UIColumn colSensorProbability = new UIColumn();

   public UIColumn getColSensorProbability() { return colSensorProbability; }

   public void setColSensorProbability(UIColumn uic) { colSensorProbability = uic; }


   private HtmlOutputText colSensorProbabilityRows = new HtmlOutputText();

   public HtmlOutputText getColSensorProbabilityRows() { return colSensorProbabilityRows; }

   public void setColSensorProbabilityRows(HtmlOutputText hot) { colSensorProbabilityRows = hot; }
   
   
   private UIColumn colSensorConsequence = new UIColumn();

   public UIColumn getColSensorConsequence() { return colSensorConsequence; }

   public void setColSensorConsequence(UIColumn uic) { colSensorConsequence = uic; }


   private HtmlOutputText colSensorConsequenceRows = new HtmlOutputText();

   public HtmlOutputText getColSensorConsequenceRows() { return colSensorConsequenceRows; }

   public void setColSensorConsequenceRows(HtmlOutputText hot) { colSensorConsequenceRows = hot; }
   
   
   private UIColumn colSensorProbabilityXConsequence = new UIColumn();

   public UIColumn getColSensorProbabilityXConsequence() { return colSensorProbabilityXConsequence; }

   public void setColSensorProbabilityXConsequence(UIColumn uic) { colSensorProbabilityXConsequence = uic; }


   private HtmlOutputText colSensorProbabilityXConsequenceRows = new HtmlOutputText();

   public HtmlOutputText getColSensorProbabilityXConsequenceRows() { return colSensorProbabilityXConsequenceRows; }

   public void setColSensorProbabilityXConsequenceRows(HtmlOutputText hot) { colSensorProbabilityXConsequenceRows = hot; }
   

   private UIColumn colSensorTimeStamp = new UIColumn();

   public UIColumn getColSensorTimeStamp() { return colSensorTimeStamp; }

   public void setColSensorTimeStamp(UIColumn uic) { colSensorTimeStamp = uic; }


   private HtmlOutputText colSensorTimeStampRows = new HtmlOutputText();

   public HtmlOutputText getColSensorTimeStampRows() { return colSensorTimeStampRows; }

   public void setColSensorTimeStampRows(HtmlOutputText hot) { colSensorTimeStampRows = hot; }


   private HiddenField hdnRowIndex = new HiddenField();

   public HiddenField getHdnRowIndex() { return hdnRowIndex; }

   public void setHdnRowIndex(HiddenField hf) { hdnRowIndex = hf; }


   private PanelLayout pnlContainer ;

   public PanelLayout getPnlContainer() { return pnlContainer; }

   public void setPnlContainer(PanelLayout pnl) { pnlContainer = pnl; }


   private PanelGroup pnlGroup ;

   public PanelGroup getPnlGroup() { return pnlGroup; }

   public void setPnlGroup(PanelGroup group) { pnlGroup = group; }


   private Button btnDetails = new Button();

   public Button getBtnDetails() { return btnDetails; }

   public void setBtnDetails(Button b) { btnDetails = b; }


   private String sensorCaseIDHeaderText = "Case  v" ;

   public String getSensorCaseIDHeaderText() { return sensorCaseIDHeaderText; }

   public void setSensorCaseIDHeaderText(String s) { sensorCaseIDHeaderText = s; }


   private String sensorNameHeaderText = "Sensor Name" ;

   public String getSensorNameHeaderText() { return sensorNameHeaderText; }

   public void setSensorNameHeaderText(String s) { sensorNameHeaderText = s; }
   
   
   private String sensorMessageHeaderText = "Sensor Message" ;

   public String getSensorMessageHeaderText() { return sensorMessageHeaderText; }

   public void setSensorMessageHeaderText(String s) { sensorMessageHeaderText = s; }
   

   private String sensorStatusHeaderText = "Sensor Status" ;

   public String getSensorStatusHeaderText() { return sensorStatusHeaderText; }

   public void setSensorStatusHeaderText(String s) { sensorStatusHeaderText = s; }
   
   
   private String sensorProbabilityHeaderText = "Sensor Likelihood" ;

   public String getSensorProbabilityHeaderText() { return sensorProbabilityHeaderText; }

   public void setSensorProbabilityHeaderText(String s) { sensorProbabilityHeaderText = s; }


   private String sensorConsequenceHeaderText = "Sensor Consequence" ;

   public String getSensorConsequenceHeaderText() { return sensorConsequenceHeaderText; }

   public void setSensorConsequenceHeaderText(String s) { sensorConsequenceHeaderText = s; }


   private String sensorProbabilityXConsequenceHeaderText = "Sensor Likelihood X Consequence" ;

   public String getSensorProbabilityXConsequenceHeaderText() { return sensorProbabilityXConsequenceHeaderText; }

   public void setSensorProbabilityXConsequenceHeaderText(String s) { sensorProbabilityXConsequenceHeaderText = s; }


   private String sensorTimeStampHeaderText = "TimeStamp" ;

   public String getSensorTimeStampHeaderText() { return sensorTimeStampHeaderText; }

   public void setSensorTimeStampHeaderText(String s) { sensorTimeStampHeaderText = s; }


   /*******************************************************************************/

   private SessionBean _sb = getSessionBean();


   /**
    * Overridden method that is called immediately before the page is rendered
    */
   public void prerender() {
       _sb.showMessagePanel();
   }


   public String btnRefresh_action() {
       _sb.refreshSensorNotification(false);
       setHeaderButtonText();
       return null ;
   }

   public String btnLogout_action() {
       _sb.doLogout();
       return "loginPage";
   }


   public String btnDetails_action() {
       Integer selectedRowIndex = new Integer((String) hdnRowIndex.getValue()) - 1;
       _sb.setSensorSelected(selectedRowIndex);
       return "showSensorInfo";
   }

   public String sensorCaseIDHeaderClick() {
       sortTable(TableSorter.SensorColumn.Case);
       return null ;
   }
      
   public String sensorNameHeaderClick() {
       sortTable(TableSorter.SensorColumn.SensorName);
       return null ;
   }
   
   public String sensorMessageHeaderClick() {
       sortTable(TableSorter.SensorColumn.SensorMessage);
       return null ;
   }
   
   public String sensorStatusHeaderClick() {
       sortTable(TableSorter.SensorColumn.SensorStatus);
       return null ;
   }
   
   public String sensorProbabilityHeaderClick() {
       sortTable(TableSorter.SensorColumn.SensorProbability);
       return null ;
   }
   
   public String sensorConsequenceHeaderClick() {
       sortTable(TableSorter.SensorColumn.SensorConsequence);
       return null ;
   }
   
   public String sensorProbabilityXConsequenceHeaderClick() {
       sortTable(TableSorter.SensorColumn.SensorProbabilityXConsequence);
       return null ;
   }

   public String sensorTimeStampHeaderClick() {
       sortTable(TableSorter.SensorColumn.SensorTimeStamp);
       return null ;
   }

   private void sortTable(TableSorter.SensorColumn column) {
       _sb.sortSensorNotification(column);
       setHeaderButtonText();
   }

   private void setHeaderButtonText() {
       resetHeaderButtons();
       SensorOrder currentOrder = _sb.getCurrentSensorOrder();
       boolean ascending = currentOrder.isAscending();
       switch (currentOrder.getColumn()) {
           case Case : sensorCaseIDHeaderText += getOrderIndicator(ascending); break;
           case SensorName : sensorNameHeaderText += getOrderIndicator(ascending); break;
           case SensorMessage : sensorMessageHeaderText += getOrderIndicator(ascending); break;
           case SensorStatus : sensorStatusHeaderText += getOrderIndicator(ascending); break;
           case SensorProbability : sensorProbabilityHeaderText += getOrderIndicator(ascending); break;
           case SensorConsequence : sensorConsequenceHeaderText += getOrderIndicator(ascending); break;
           case SensorProbabilityXConsequence : sensorProbabilityXConsequenceHeaderText += getOrderIndicator(ascending); break;
           case SensorTimeStamp : sensorTimeStampHeaderText += getOrderIndicator(ascending);
       }

   }

   private void resetHeaderButtons() {
	   sensorCaseIDHeaderText = "Case";
	   sensorNameHeaderText = "Sensor Name" ;
	   sensorMessageHeaderText = "Sensor Message" ;
	   sensorTimeStampHeaderText = "TimeStamp" ;
   }


   private String getOrderIndicator(boolean ascending) {
       return ascending ? "  v" : "  ^";
   }

}