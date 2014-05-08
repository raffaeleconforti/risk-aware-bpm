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
        <ui:page binding="#{SensorInfo.page1}" id="page1">
            <ui:html binding="#{SensorInfo.html1}" id="html1">
                <ui:head binding="#{SensorInfo.head1}" id="head1"
                         title="YAWL Monitor Service - Selected Sensor Detail">

                    <ui:link binding="#{SensorInfo.link1}" id="link1"
                             url="/resources/stylesheet.css"/>

                    <ui:link binding="#{ApplicationBean.favIcon}" id="lnkFavIcon"
                             rel="shortcut icon"
                            type="image/x-icon" url="/resources/favicon.ico"/>

                    <ui:script binding="#{SessionBean.script}" id="script1"
                               url="/resources/script.js"/>

                </ui:head>
                <ui:body binding="#{SensorInfo.body1}" id="body1"
                         style="-rave-layout: grid">
                    <ui:form binding="#{SensorInfo.form1}" id="form1">

                        <!-- include banner -->
                        <div><jsp:directive.include file="pfHeader.jspf"/></div>

                        <center>

                        <ui:panelLayout binding="#{SensorInfo.pnlContainer}"
                                        id="pnlContainer"
                                        styleClass="itemsContainerPanel">

                        <ui:panelLayout binding="#{SensorInfo.layoutPanel}"
                                        id="layoutPanel1"
                                        styleClass="casesPanel"
                                        style="position: absolute; left:160px">

                            <ui:staticText binding="#{SensorInfo.staticText}"
                                           id="staticText1"
                                           styleClass="pageHeading"
                                           style="left: 12px; top: 12px"
                                           text="Info of Selected Sensor Notification"/>

                            <ui:button binding="#{SessionBean.btnRefresh}"
                                        action="#{SensorInfo.btnDelete_action}"
                                        id="btnRefresh"
                                        imageURL="/resources/delete.png"
                                        styleClass="refreshButton"
                                        toolTip="Delete Sensor Notification"
                                        text=""/>

                             <ui:button binding="#{SessionBean.btnLogout}"
                                        action="#{SensorInfo.btnLogout_action}"
                                        id="btnLogout"
                                        imageURL="/resources/logout.png"
                                        styleClass="logoutButton"
                                        toolTip="Logout"
                                        text=""/>

                             <ui:button binding="#{SessionBean.btnBack}"
                                        action="#{SensorInfo.btnBack_action}"
                                        id="btnBack"
                                        imageURL="/resources/back.png"
                                        styleClass="backButton"
                                        toolTip="Return to Sensor Notification"
                                        text=""/>
                                        
                             <ui:button binding="#{SessionBean.btnMitigate}"
                                        action="#{SensorInfo.btnMitigate_action}"
                                        id="btnMitigate"
                                        imageURL="/resources/success.png"
                                        styleClass="mitigateButton"
                                        toolTip="Mitigate Process Instance"
                                        text=""/>

                         </ui:panelLayout>

                           <ui:panelGroup binding="#{SensorInfo.pnlGroupCaseData}"
                                        id="pnlGroupCaseData"
                                        styleClass="caseDataPnlGroup">

                            <ui:staticText binding="#{SensorInfo.sensorCaseID}"
                                           id="sensorCaseID"
                                           styleClass="pageSubheading"
                                           style="left: 12px; top: 12px"
                                           text="Sensor CaseID"/>

                             <ui:staticText binding="#{SensorInfo.sensorName}"
                                            id="sensorName"
                                            styleClass="pageSubheading"
                                            style="left: 12px; top: 52px"
                                            text="Sensor Name"/>

                             <ui:staticText binding="#{SensorInfo.sensorTimeStamp}"
                                            id="sensorTimeStamp"
                                            styleClass="pageSubheading"
                                            style="left: 12px; top: 92px"
                                            text="Sensor TimeStamp"/>

                             <ui:staticText binding="#{SensorInfo.sensorCaseIDText}"
                                            id="sensorCaseIDText"
                                            style="left: 12px; top: 30px; position: absolute"
                                            text="#{SessionBean.sensorCaseID}"/>

                             <ui:staticText binding="#{SensorInfo.sensorNameText}"
                                            id="sensorNameText"
                                            style="left: 12px; top: 70px; position: absolute"
                                            text="#{SessionBean.sensorName}" />

                             <ui:staticText binding="#{SensorInfo.sensorTimeStampText}"
                                            id="sensorTimeStampText"
                                            style="left: 12px; top: 110px; position: absolute"
                                            text="#{SessionBean.sensorTimeStamp}"/>

                             <ui:staticText binding="#{SensorInfo.sensorTrigger}"
                                            id="sensorTrigger"
                                            styleClass="pageSubheading"
                                            style="left: 212px; top: 12px; position: absolute"
                                            text="Sensor Trigger"/>

                            <ui:textArea binding="#{SensorInfo.sensorTriggerText}"
                                         id="sensorTriggerText"
                                         styleClass="caseDataTextArea"
                                         style="left: 212px; top: 30px; position: absolute"
                                         rows="4"
                                         disabled="true"
                                         text="#{SessionBean.sensorTrigger}"/>
                         </ui:panelGroup>   


                          <!--  <ui:panelGroup binding="#{SensorInfo.pnlGroup}"
                                            id="pnlGroup"
                                            styleClass="itemsTablePnlGroup">


                            <h:dataTable binding="#{SensorInfo.dataTable}"
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

                                <h:column binding="#{SensorInfo.colItemID}"
                                          id="colItemID">
                                           <f:facet name="header" >
                                               <h:commandLink value="#{SensorInfo.caseIDHeaderText}"
                                                       action="#{SensorInfo.caseIDHeaderClick}"/>
                                           </f:facet>
                                    <h:outputText binding="#{SensorInfo.colItemIDRows}"
                                                  id="colItemIDRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.caseID}"/>
                                </h:column>

                                <h:column binding="#{SensorInfo.colTaskID}"
                                          id="colTaskID">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorInfo.taskIDHeaderText}"
                                                action="#{SensorInfo.taskIDHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorInfo.colTaskIDRows}"
                                                  id="colTaskIDRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.taskID}"/>
                                </h:column>

                                <h:column binding="#{SensorInfo.colStatus}"
                                          id="colStatus">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorInfo.statusHeaderText}"
                                                action="#{SensorInfo.statusHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorInfo.colStatusRows}"
                                                  id="colStatusRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.plainStatus}"/>
                                </h:column>

                                <h:column binding="#{SensorInfo.colService}"
                                          id="colService">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorInfo.serviceHeaderText}"
                                                action="#{SensorInfo.serviceHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorInfo.colServiceRows}"
                                                  id="colServiceRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.resourceName}"/>
                                </h:column>

                                <h:column binding="#{SensorInfo.colEnabledTime}"
                                          id="colEnabledTime">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorInfo.enabledTimeHeaderText}"
                                                action="#{SensorInfo.enabledTimeHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorInfo.colEnabledTimeRows}"
                                                  id="colEnabledTimeRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.enabledTimeAsDateString}"/>
                                </h:column>

                                <h:column binding="#{SensorInfo.colStartTime}"
                                          id="colStartTime">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorInfo.startTimeHeaderText}"
                                                action="#{SensorInfo.startTimeHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorInfo.colStartTimeRows}"
                                                  id="colStartTimeRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.startTimeAsDateString}"/>
                                </h:column>

                                <h:column binding="#{SensorInfo.colCompletionTime}"
                                          id="colCompletionTime">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorInfo.completionTimeHeaderText}"
                                                action="#{SensorInfo.completionTimeHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorInfo.colCompletionTimeRows}"
                                                  id="colCompletionTimeRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.completionTimeAsDateString}"/>
                                </h:column>

                                <h:column binding="#{SensorInfo.colTimerStatus}"
                                          id="colTimerStatus">
                                    <f:facet name="header" >
                                        <h:commandLink value="#{SensorInfo.timerStatusHeaderText}"
                                                action="#{SensorInfo.timerStatusHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorInfo.colTimerStatusRows}"
                                                  id="colTimerStatusRows"
                                                  styleClass="dataTableItemText"
                                                  value="#{currentRow.timerStatus}"/>
                                </h:column>

                                <h:column binding="#{SensorInfo.colTimerExpiry}"
                                          id="colTimerExpiry">
                                    <f:facet name="header" >
                                       <h:commandLink value="#{SensorInfo.timerExpiryHeaderText}"
                                               action="#{SensorInfo.timerExpiryHeaderClick}"/>
                                    </f:facet>
                                    <h:outputText binding="#{SensorInfo.colTimerExpiryRows}"
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

                        <ui:hiddenField binding="#{SensorInfo.hdnRowIndex}" id="hdnRowIndex"/>

                        <ui:button binding="#{SensorInfo.btnDetails}"
                                   action="#{SensorInfo.btnDetails_action}"
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
