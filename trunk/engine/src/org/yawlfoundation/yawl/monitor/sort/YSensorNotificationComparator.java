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

package org.yawlfoundation.yawl.monitor.sort;

import org.yawlfoundation.yawl.sensors.YSensorNotification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Author: Raffaele Conforti
 * Creation Date: 13/09/2010
 */

public enum YSensorNotificationComparator implements Comparator<YSensorNotification> {
    CaseID {
        public int compare(YSensorNotification o1, YSensorNotification o2) {
            return compareStrings(o1.getCaseID(), o2.getCaseID());
        }},
    SensorName {
        public int compare(YSensorNotification o1, YSensorNotification o2) {
            return compareStrings(o1.getSensorName(), o2.getSensorName());
        }},
    SensorStatus {
        public int compare(YSensorNotification o1, YSensorNotification o2) {
            return compareStrings(o1.getSensorStatus(), o2.getSensorStatus());
        }},
    SensorMessage {
        public int compare(YSensorNotification o1, YSensorNotification o2) {
            return compareStrings(o1.getSensorMessage(), o2.getSensorMessage());
        }},
    SensorProbability {
        public int compare(YSensorNotification o1, YSensorNotification o2) {
            return compareDoubles(o1.getSensorProbability(), o2.getSensorProbability());
        }},
    SensorConsequence {
        public int compare(YSensorNotification o1, YSensorNotification o2) {
            return compareDoubles(o1.getSensorConsequence(), o2.getSensorConsequence());
        }},
    SensorProbabilityXConsequence {
        public int compare(YSensorNotification o1, YSensorNotification o2) {
            return compareDoubles(o1.getSensorProbabilityXConsequence(), o2.getSensorProbabilityXConsequence());
        }},
    SensorTimeStamp {
        public int compare(YSensorNotification o1, YSensorNotification o2) {
            return compareDates(o1.getSensorTimeStamp(), o2.getSensorTimeStamp());
        }};

   private static int compareDoubles(Double a, Double b) {
	   if (a == null) return -1;
       if (b == null) return 1;
       return a.compareTo(b);
   }
    
   private static int compareStrings(String a, String b) {
       if (a == null) return -1;
       if (b == null) return 1;
       return a.compareTo(b);
   }

   private static int compareDates(String a, String b) {
	   SimpleDateFormat original = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
	   try {
		   long a1 = original.parse(a).getTime();
		   long b1 = original.parse(b).getTime();
		   long difference = a1 - b1;

	       // guard against integer overrun
	       if (difference > Integer.MAX_VALUE)
	    	   difference = 1;
	       else if (difference < Integer.MIN_VALUE)
	           difference = -1;

	       return (int) difference;
	   } catch (ParseException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
	   return 0;
       
    }



    public static Comparator<YSensorNotification> descending(final Comparator<YSensorNotification> other) {
        return new Comparator<YSensorNotification>() {
            public int compare(YSensorNotification o1, YSensorNotification o2) {
                return -1 * other.compare(o1, o2);
            }
        };
    }

    public static Comparator<YSensorNotification> getComparator(final YSensorNotificationComparator... multipleOptions) {
        return new Comparator<YSensorNotification>() {
            public int compare(YSensorNotification o1, YSensorNotification o2) {
                for (YSensorNotificationComparator option : multipleOptions) {
                    int result = option.compare(o1, o2);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }

    public static Comparator<YSensorNotification> getComparator(SensorOrder sensorOrder) {
        Comparator<YSensorNotification> comparator = null;
        switch (sensorOrder.getColumn()) {
            case Case : comparator = getComparator(CaseID); break;
            case SensorName : comparator = getComparator(SensorName); break;
            case SensorStatus : comparator = getComparator(SensorStatus); break;
            case SensorMessage : comparator = getComparator(SensorMessage); break;
            case SensorProbability : comparator = getComparator(SensorProbability); break;
            case SensorConsequence : comparator = getComparator(SensorConsequence); break;
            case SensorProbabilityXConsequence : comparator = getComparator(SensorProbabilityXConsequence); break;
            case SensorTimeStamp : comparator = getComparator(SensorTimeStamp);
        }
        if ((comparator != null) && ! sensorOrder.isAscending()) {
            comparator = descending(comparator);
        }
        return comparator;
    }

}

