<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:ui="http://www.sun.com/web/ui">

<!--
  ~ Copyright (c) 2004-2010 The YAWL Foundation. All rights reserved.
  ~ The YAWL Foundation is a collaboration of individuals and
  ~ organisations who are committed to improving workflow technology.
  ~
  ~ This file is part of YAWL. YAWL is free software: you can
  ~ redistribute it and/or modify it under the terms of the GNU Lesser
  ~ General Public License as published by the Free Software Foundation.
  ~
  ~ YAWL is distributed in the hope that it will be useful, but WITHOUT
  ~ ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  ~ or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
  ~ Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
  -->

    <jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
    <f:view>
        <ui:page binding="#{SensorNotification.page1}" id="page1">
            <ui:html binding="#{SensorNotification.html1}" id="html1">
                <ui:head binding="#{SensorNotification.head1}" id="head1"
                         title="YAWL Monitor Service - Sensor Notification">

                    <ui:link binding="#{SensorNotification.link1}" id="link1"
                             url="/resources/stylesheet.css"/>

                    <ui:link binding="#{ApplicationBean.favIcon}" id="lnkFavIcon"
                             rel="shortcut icon"
                            type="image/x-icon" url="/resources/favicon.ico"/>

                    <ui:script binding="#{SessionBean.script}" id="script1"
                               url="/resources/script.js"/>

                </ui:head>
                <ui:body binding="#{SensorNotification.body1}" id="body1"
                         style="-rave-layout: grid">
                    <ui:form binding="#{SensorNotification.form1}" id="form1">

                        <!-- include banner -->
                        <div><jsp:directive.include file="pfHeader.jspf"/></div>

                        <center>

                        <ui:panelLayout binding="#{SensorNotification.pnlContainer}"
                                        id="pnlContainer"
                                        styleClass="sensorsContainerPanel">
			
                        <ui:panelLayout binding="#{SensorNotification.layoutPanel}"
                                        id="layoutPanel1"
                                        styleClass="sensorsPanel"> 

                            <ui:staticText binding="#{SensorNotification.staticText}"
                                           id="staticText1"
                                           styleClass="pageHeading"
                                           style="left: 12px; top: 12px"
                                           text="Sensor Notification"/>

                            <ui:button binding="#{SessionBean.btnRefresh}"
                                        action="#{SensorNotification.btnRefresh_action}"
                                        id="btnRefresh"
                                        imageURL="/resources/refresh.png"
                                        styleClass="refreshButton"
                                        toolTip="Refresh Sensor Notification"
                                        text=""/>

                             <ui:button binding="#{SessionBean.btnLogout}"
                                        action="#{SensorNotification.btnLogout_action}"
                                        id="btnLogout"
                                        imageURL="/resources/logout.png"
                                        styleClass="logoutButton"
                                        toolTip="Logout"
                                        text=""/>

                         </ui:panelLayout>
                         
                            <ui:staticText binding="#{ActiveCases.stUptime}"
                            		   id="stuptime"
                                           style="position:absolute; left: 15px; top: 52px;"
                                           text=" "/>
                         
                            <ui:panelGroup binding="#{SensorNotification.pnlGroup}"
                                            id="pnlGroup"
                                            styleClass="sensorsTablePnlGroup">
                                                        
                             <h:dataTable binding="#{SensorNotification.dataTable}"
                                         headerClass="dataTableHeader"
                                         id="dataTable1"
                                         cellpadding="4"
                                         styleClass="dataTable"
                                         columnClasses="caseIDCol,
                                                        sensorNameCol,
							sensorStatusCol,
                                                        sensorMessageCol,
                                                        sensorConditionCol,
                                                        sensorProbabilityCol,
                                                        sensorConsequenceCol,
                                                        sensorProbabilityXConsequenceCol,
                                                        timeCol"
                                         value="#{SessionBean.sensorNotificationList}"
                                         var="currentRow"
                                         width="960">

                                <h:column binding="#{SensorNotification.colSensorCaseID}"
                                          id="colCaseID">
                                           <f:facet name="header" >
                                               <h:commandLink value="#{SensorNotification.sensorCaseIDHeaderText}"
                                                       action="#{SensorNotification.sensorCaseIDHeaderClick}"/>
                                           </f:facet>
                                    <h:outputText binding="#{SensorNotification.colSensorCaseIDRows}"
                                                  id="colSensorCaseIDRows"
                                                  styleClass="dataTableText"
                                                  value="#{currentRow.caseID}"/>
                                </h:column>
  
                                <h:column binding="#{SensorNotification.colSensorName}"
                                          id="colSensorName">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorNotification.sensorNameHeaderText}"
                                                action="#{SensorNotification.sensorNameHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorNotification.colSensorNameRows}"
                                                  id="colSensorNameRows"
                                                  styleClass="dataTableText"
                                                  value="#{currentRow.sensorName}"/>
                                </h:column> 
                                
                                <h:column binding="#{SensorNotification.colSensorMessage}"
                                          id="colSensorMessage">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorNotification.sensorMessageHeaderText}"
                                                action="#{SensorNotification.sensorMessageHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorNotification.colSensorMessageRows}"
                                                  id="colSensorMessageRows"
                                                  styleClass="dataTableText"
                                                  value="#{currentRow.sensorMessage}"/>
                                </h:column>  
                                
                                <h:column binding="#{SensorNotification.colSensorStatus}"
                                          id="colSensorStatus">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorNotification.sensorStatusHeaderText}"
                                                action="#{SensorNotification.sensorStatusHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorNotification.colSensorStatusRows}"
                                                  id="colSensorStatusRows"
                                                  styleClass="dataTableText"
                                                  value="#{currentRow.sensorStatus}"/>
                                </h:column>   
                                
                                <h:column binding="#{SensorNotification.colSensorProbability}"
                                          id="colSensorProbability">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorNotification.sensorProbabilityHeaderText}"
                                                action="#{SensorNotification.sensorProbabilityHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorNotification.colSensorProbabilityRows}"
                                                  id="colSensorProbabilityRows"
                                                  styleClass="dataTableText"
                                                  value="#{currentRow.sensorProbability}"/>
                                </h:column>                             

                                <h:column binding="#{SensorNotification.colSensorConsequence}"
                                          id="colSensorConsequence">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorNotification.sensorConsequenceHeaderText}"
                                                action="#{SensorNotification.sensorConsequenceHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorNotification.colSensorConsequenceRows}"
                                                  id="colSensorConsequenceRows"
                                                  styleClass="dataTableText"
                                                  value="#{currentRow.sensorConsequence}"/>
                                </h:column>        
                                
                                <h:column binding="#{SensorNotification.colSensorProbabilityXConsequence}"
                                          id="colSensorProbabilityXConsequence">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorNotification.sensorProbabilityXConsequenceHeaderText}"
                                                action="#{SensorNotification.sensorProbabilityXConsequenceHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorNotification.colSensorProbabilityXConsequenceRows}"
                                                  id="colSensorProbabilityXConsequenceRows"
                                                  styleClass="dataTableText"
                                                  value="#{currentRow.sensorProbabilityXConsequence}"/>
                                </h:column>  

                                <h:column binding="#{SensorNotification.colSensorTimeStamp}"
                                          id="colTime">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorNotification.sensorTimeStampHeaderText}"
                                                action="#{SensorNotification.sensorTimeStampHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorNotification.colSensorTimeStampRows}"
                                                  id="colSensorTimeStampRows"
                                                  styleClass="dataTableText"
                                                  value="#{currentRow.sensorTimeStamp}"/>
                                </h:column>

                            </h:dataTable>
                           </ui:panelGroup>

                            <div><jsp:include page="pfMsgPanel.jspf"/></div>

                         </ui:panelLayout>

                        <ui:hiddenField binding="#{SensorNotification.hdnRowIndex}" id="hdnRowIndex"/>

                        <ui:button binding="#{SensorNotification.btnDetails}"
                                   action="#{SensorNotification.btnDetails_action}"
                                   id="btnDetails"
                                   style="display: none"
                                   text=""/>

                        </center>
                    </ui:form>

                    <ui:script>
                        addOnclickToDatatableRows();
                    </ui:script>

                </ui:body>
            </ui:html>
        </ui:page>
    </f:view>
</jsp:root>