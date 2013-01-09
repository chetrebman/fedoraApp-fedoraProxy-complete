/*
 * Copyright 2012 University of Denver
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
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Asynchronous version of BatchIngestThreadManagerService
 * 
 * @author chet.rebman
 *
 */
public interface BatchIngestThreadManagerServiceAsync {

    public void getAllBatchSetStatus( String batch_set, AsyncCallback callback );
    
    public void isBatchSetRunning ( String batch_set, AsyncCallback callback );
    
    public void isBatchSetThreadExists( String batch_set, AsyncCallback callback );
    
    public void stopBatchIngest( String batch_set, AsyncCallback callback );
    
    public void removeBatchset( String batch_set, AsyncCallback callback );

    public void forceHardStop( String batchSetname, AsyncCallback callback ); 

} // BatchIngestThreadManagerService
