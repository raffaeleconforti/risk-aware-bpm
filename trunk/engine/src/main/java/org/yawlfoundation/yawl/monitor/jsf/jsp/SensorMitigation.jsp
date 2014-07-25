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
        <ui:page binding="#{SensorMitigation.page1}" id="page1">
            <ui:html binding="#{SensorMitigation.html1}" id="html1">
                <ui:head binding="#{SensorMitigation.head1}" id="head1"
                         title="YAWL Monitor Service - Selected Sensor Detail">

                    <ui:link binding="#{SensorMitigation.link1}" id="link1"
                             url="/resources/stylesheet.css"/>

                    <ui:link binding="#{ApplicationBean.favIcon}" id="lnkFavIcon"
                             rel="shortcut icon"
                            type="image/x-icon" url="/resources/favicon.ico"/>

                    <ui:script binding="#{SessionBean.script}" id="script1"
                               url="/resources/script.js"/>

                </ui:head>
                <ui:body binding="#{SensorMitigation.body1}" id="body1"
                         style="-rave-layout: grid">
                    <ui:form binding="#{SensorMitigation.form1}" id="form1">

                        <!-- include banner -->
                        <div><jsp:directive.include file="pfHeader.jspf"/></div>

                        <center>

                        <ui:panelLayout binding="#{SensorMitigation.pnlContainer}"
                                        id="pnlContainer"
                                        styleClass="itemsContainerPanel">

                        <ui:panelLayout binding="#{SensorMitigation.layoutPanel}"
                                        id="layoutPanel1"
                                        styleClass="casesPanel"
                                        style="position: absolute; left:160px">

                            <ui:staticText binding="#{SensorMitigation.staticText}"
                                           id="staticText1"
                                           styleClass="pageHeading"
                                           style="left: 12px; top: 12px"
                                           text="Suggested Mitigation"/>

                             <ui:button binding="#{SessionBean.btnLogout}"
                                        action="#{SensorMitigation.btnLogout_action}"
                                        id="btnLogout"
                                        imageURL="/resources/logout.png"
                                        styleClass="logoutButton"
                                        toolTip="Logout"
                                        text=""/>

                             <ui:button binding="#{SessionBean.btnBack}"
                                        action="#{SensorMitigation.btnBack_action}"
                                        id="btnBack"
                                        imageURL="/resources/back.png"
                                        styleClass="backButton"
                                        toolTip="Return to Sensor Information"
                                        text=""/>

                         </ui:panelLayout>

                           <ui:panelGroup binding="#{SensorMitigation.pnlGroupCaseData}"
                                        id="pnlGroupCaseData"
                                        styleClass="caseDataPnlGroup">

                            <ui:staticText binding="#{SensorMitigation.sensorCaseID}"
                                           id="sensorCaseID"
                                           styleClass="pageSubheading"
                                           style="left: 12px; top: 12px"
                                           text="Sensor CaseID"/>

                             <ui:staticText binding="#{SensorMitigation.sensorName}"
                                            id="sensorName"
                                            styleClass="pageSubheading"
                                            style="left: 12px; top: 52px"
                                            text="Sensor Name"/>

                             <ui:staticText binding="#{SensorMitigation.sensorCaseIDText}"
                                            id="sensorCaseIDText"
                                            style="left: 12px; top: 30px; position: absolute"
                                            text="#{SessionBean.sensorCaseID}"/>

                             <ui:staticText binding="#{SensorMitigation.sensorNameText}"
                                            id="sensorNameText"
                                            style="left: 12px; top: 70px; position: absolute"
                                            text="#{SessionBean.sensorName}" />
                                            
                             <ui:staticText binding="#{SensorMitigation.sensorMitigation}"
                                            id="sensorMitigation"
                                            styleClass="pageSubheading"
                                            style="left: 212px; top: 12px; position: absolute"
                                            text="Mitigation"/>

                            <ui:textArea binding="#{SensorMitigation.sensorMitigationText}"
                                         id="sensorMitigationText"
                                         styleClass="caseDataTextArea"
                                         style="left: 212px; top: 30px; position: absolute"
                                         rows="4"
                                         disabled="true"
                                         text="#{SessionBean.sensorMitigation}"/>

                         </ui:panelGroup>   


                          <!--  <ui:panelGroup binding="#{SensorMitigation.pnlGroup}"
                                            id="pnlGroup"
                                            styleClass="itemsTablePnlGroup">


                            <h:dataTable binding="#{SensorMitigation.dataTable}"
                                         headerClass="dataTableHeader"
                                         id="dataTable1"
                                         cellpadding="3"
                                         styleClass="dataTable"
                                         columnClasses="itemIDCol,
                                                        taskIDCol,
                                                        statusCol,
                                                        serviceCol,
                                                        timeCol,
                                                        timeCol,
                                                        timeCol,
                                                        timerStatusCol,
                                                        timerExpiryCol"
                                         value="#{SessionBean.caseItems}"
                                         var="currentRow"
                                         width="940">

                                <h:column binding="#{SensorMitigation.colItemID}"
                                          id="colItemID">
                                           <f:facet name="header" >
                                               <h:commandLink value="#{SensorMitigation.caseIDHeaderText}"
                                                       action="#{SensorMitigation.caseIDHeaderClick}"/>
                                           </f:facet>
                                    <h:outputText binding="#{SensorMitigation.colItemIDRows}"
                                                  id="colItemIDRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.caseID}"/>
                                </h:column>

                                <h:column binding="#{SensorMitigation.colTaskID}"
                                          id="colTaskID">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorMitigation.taskIDHeaderText}"
                                                action="#{SensorMitigation.taskIDHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorMitigation.colTaskIDRows}"
                                                  id="colTaskIDRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.taskID}"/>
                                </h:column>

                                <h:column binding="#{SensorMitigation.colStatus}"
                                          id="colStatus">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorMitigation.statusHeaderText}"
                                                action="#{SensorMitigation.statusHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorMitigation.colStatusRows}"
                                                  id="colStatusRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.plainStatus}"/>
                                </h:column>

                                <h:column binding="#{SensorMitigation.colService}"
                                          id="colService">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorMitigation.serviceHeaderText}"
                                                action="#{SensorMitigation.serviceHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorMitigation.colServiceRows}"
                                                  id="colServiceRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.resourceName}"/>
                                </h:column>

                                <h:column binding="#{SensorMitigation.colEnabledTime}"
                                          id="colEnabledTime">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorMitigation.enabledTimeHeaderText}"
                                                action="#{SensorMitigation.enabledTimeHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorMitigation.colEnabledTimeRows}"
                                                  id="colEnabledTimeRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.enabledTimeAsDateString}"/>
                                </h:column>

                                <h:column binding="#{SensorMitigation.colStartTime}"
                                          id="colStartTime">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorMitigation.startTimeHeaderText}"
                                                action="#{SensorMitigation.startTimeHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorMitigation.colStartTimeRows}"
                                                  id="colStartTimeRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.startTimeAsDateString}"/>
                                </h:column>

                                <h:column binding="#{SensorMitigation.colCompletionTime}"
                                          id="colCompletionTime">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorMitigation.completionTimeHeaderText}"
                                                action="#{SensorMitigation.completionTimeHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorMitigation.colCompletionTimeRows}"
                                                  id="colCompletionTimeRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.completionTimeAsDateString}"/>
                                </h:column>

                                <h:column binding="#{SensorMitigation.colTimerStatus}"
                                          id="colTimerStatus">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorMitigation.timerStatusHeaderText}"
                                                action="#{SensorMitigation.timerStatusHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorMitigation.colTimerStatusRows}"
                                                  id="colTimerStatusRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.timerStatus}"/>
                                </h:column>

                                <h:column binding="#{SensorMitigation.colTimerExpiry}"
                                          id="colTimerExpiry">
                                    <f:facet name="header" >
                                       <h:commandLink value="#{SensorMitigation.timerExpiryHeaderText}"
                                               action="#{SensorMitigation.timerExpiryHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorMitigation.colTimerExpiryRows}"
                                                  id="colTimerExpiryRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.timerExpiryAsCountdown}"/>
                                </h:column>

                            </h:dataTable>
                            </ui:panelGroup>
                            -->

                        <div><jsp:include page="pfMsgPanel.jspf"/></div>

                         </ui:panelLayout>

                        </center>

                        <ui:hiddenField binding="#{SensorMitigation.hdnRowIndex}" id="hdnRowIndex"/>

                        <ui:button binding="#{SensorMitigation.btnDetails}"
                                   action="#{SensorMitigation.btnDetails_action}"
                                   id="btnDetails"
                                   style="display: none"
                                   text=""/>

                    </ui:form>

                    <ui:script>
                        addOnclickToDatatableRows();
                    </ui:script>

                </ui:body>
            </ui:html>
        </ui:page>
    </f:view>
</jsp:root>
