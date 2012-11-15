/*
 * Copyright 2011 University of Denver
 * Author Chet Rebman
 * 
 * This file is part of FedoraApp.
 * 
 * FedoraApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FedoraApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FedoraApp.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.server;

import com.google.gwt.user.client.rpc.RemoteService;



/**
 * Specify the services made available through GWT RPC. GWT is the Google
 * web Toolkit.
 * 
 * @author chet.rebman
 *
 */
public interface BatchIngestThreadManagerService extends RemoteService {

    public StatusData getAllBatchSetStatus( String bbatchSetnameatch_set );
    
    public boolean isBatchSetRunning ( String batchSetname );
    
    public boolean isBatchSetThreadExists( String batchSetname );
    
    public void stopBatchIngest( String batchSetname );
    
    public void removeBatchset( String batchSetname );
    
    public void forceHardStop( String batchSetname );
    
} // BatchIngestThreadManagerService
