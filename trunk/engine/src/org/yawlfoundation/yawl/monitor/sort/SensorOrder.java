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

/**
 * Author: Raffaele Conforti
* Creation Date: 13/09/2010
*/
public class SensorOrder {
    private TableSorter.SensorColumn _column;
    private boolean _ascending;

    public SensorOrder(TableSorter.SensorColumn column) {
        _column = column;
        _ascending = true;
    }

    public void setOrder(TableSorter.SensorColumn column) {
        _ascending = (column != _column) || ! _ascending;  // toggle order if same column
        _column = column;
    }

    public TableSorter.SensorColumn getColumn() { return _column; }

    public boolean isAscending() { return _ascending; }
}

