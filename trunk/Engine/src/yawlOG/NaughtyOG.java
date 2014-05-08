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

package yawlOG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.yawlfoundation.yawl.authentication.YExternalClient;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.state.YIdentifier;
import org.yawlfoundation.yawl.engine.ObserverGateway;
import org.yawlfoundation.yawl.engine.YEngine;
import org.yawlfoundation.yawl.engine.YWorkItem;
import org.yawlfoundation.yawl.engine.YWorkItemStatus;
import org.yawlfoundation.yawl.engine.announcement.Announcements;
import org.yawlfoundation.yawl.engine.announcement.CancelWorkItemAnnouncement;
import org.yawlfoundation.yawl.engine.announcement.NewWorkItemAnnouncement;

import java.util.Set;

public class NaughtyOG implements ObserverGateway {

	/* Commons logging logger */
	private static final Log log = LogFactory.getLog(
											NaughtyOG.class);

	private YEngine yawlEngine;
	private YExternalClient user;

	/*-----------------------------------------------------------------------*/
	/*              Constructor (singleton pattern)                          */
	/*-----------------------------------------------------------------------*/

	private static NaughtyOG THE_GATEWAY = null;

	/**
	 * Singleton pattern static factory method. Must be called getInstance
	 * (with no parms) to allow for reflective instantiation by YEngine.
	 *
	 * @return The singleton instance
	 */
	public static NaughtyOG getInstance() {

		if (THE_GATEWAY == null) {
			THE_GATEWAY = new NaughtyOG();
		}
		return THE_GATEWAY;

	}

	private NaughtyOG() {

		log.fatal("NaughtyOG started");

	}

	/*-----------------------------------------------------------------------*/
	/*                     ObserverGateway Methods                           */
	/*-----------------------------------------------------------------------*/

	public String getScheme() {

		return "http";

	}

	public void announceEngineInitialised(Set<YAWLServiceReference> services) {

		this.yawlEngine = YEngine.getInstance();	// Should be there now

    user = new YExternalClient("testUser", "pwd", null);
		try {
        yawlEngine.addExternalClient(user);
		}
		catch (Exception e) {}

	}

	public void announceWorkItems(
			Announcements<NewWorkItemAnnouncement> announcements) {

		for (NewWorkItemAnnouncement a : announcements.getAllAnnouncements()) {
			YWorkItem wi = a.getItem();

			log.fatal("Got WI with task ID " + wi.getTaskID());

			if (wi.getTaskID().startsWith("Task2")) {
				try {
					log.fatal("Checking Task2 out");
					YWorkItem childWI = yawlEngine.startWorkItem(wi, user);
				}
				catch (Exception e) {}

				try {
					String caseID = extractMainCaseID(wi.getCaseID().get_idString());
					log.fatal("Cancelling case ID " + caseID);
					yawlEngine.cancelCase(new YIdentifier(caseID));
				}
				catch (Exception e) {}
//				log.fatal("Creating exception");
//				int j = 1 / 0;				// Create exception
			}
		}

	}

	private String extractMainCaseID(String caseID) {

		String[] idParts = caseID.split("\\.");		// Regexp so escape .
		if (idParts.length == 0) {
			return caseID;
		}
		else {
			return idParts[0];
		}

	}


	public void announceCaseCancellation(Set<YAWLServiceReference> services,
										 YIdentifier id) {
		//

	}


	public void announceCaseCompletion(YAWLServiceReference yawlService,
									   YIdentifier caseIdentifier,
									   Document caseEndData) {

		//

	}


	public void announceCaseCompletion(YIdentifier caseIdentifier,
									   Document caseEndData) {

		//

	}



	public void announceWorkItemStatusChange(YWorkItem workItem,
			YWorkItemStatus oldStatus, YWorkItemStatus newStatus) {

		//

	}


	public void announceCaseResumption(YIdentifier caseID) {

		// Do nothing

	}


	public void announceCaseSuspended(YIdentifier caseID) {

		// Do nothing

	}


	public void announceCaseSuspending(YIdentifier caseID) {

		// Do nothing

	}


	public void announceTimerExpiry(YAWLServiceReference yawlService,
									YWorkItem item) {

		// Do nothing

	}


	public void cancelAllWorkItemsInGroupOf(
			Announcements<CancelWorkItemAnnouncement> announcements) {

		// Do nothing

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
