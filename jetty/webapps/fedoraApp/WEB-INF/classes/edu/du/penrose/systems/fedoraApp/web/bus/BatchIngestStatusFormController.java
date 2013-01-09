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

package edu.du.penrose.systems.fedoraApp.web.bus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestThreadManager;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.util.MyServletContextListener;

/**
 * THIS HAS BEEN REPLACED WITH A GOOGLE TOOTKIT AJAX PAGE.
 * <br>
 * <br>
 * Controller for the batch ingest status page. The page is refresed every few
 * seconds and the status of the batch ingest is displayed. In addition there
 * is a button to stop the ingest.
 * 
 * @author chet.rebman
 * @see edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.client.BatchIngestStatus
 *
 */
public class BatchIngestStatusFormController extends SimpleFormController {


    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
	protected Object formBackingObject(HttpServletRequest request)
    throws Exception {

		BatchIngestOptions commandObj = new BatchIngestOptions();
		
		return commandObj;
	}
	
	
	public ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
    	throws Exception {
		
		String batch_set = (String) request.getSession().getAttribute( "BATCH_SET_NAME" );
		
		if ( request.getParameter( FedoraAppConstants.BATCH_INGEST_STOP_INGEST_BTN_NAME )  != null ){
		       
		    BatchIngestThreadManager.stopBatchIngest( batch_set );
                request.getSession().setAttribute( "DISABLE_STOP_INGEST_SUBMIT_BTN", "anything");
		        return this.showNewForm(request, response);
		}
        else {
            request.getSession().removeAttribute( "DISABLE_STOP_INGEST_SUBMIT_BTN" );  // enable re-start
        }

        if ( request.getParameter( FedoraAppConstants.BATCH_INGEST_VIEW_REPORT_BTN_NAME )  != null ) {
            
            return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"batchIngestReport.jsp?" );
        }
        
        if ( request.getParameter( FedoraAppConstants.BATCH_INGEST_VIEW_PID_REPORT_BTN_NAME )  != null ) {
            
            return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"batchIngestPidReport.jsp?" );
        }

        if ( request.getParameter( FedoraAppConstants.BATCH_INGEST_ENABLE_NEW_BATCH_BTN_NAME )  != null ) {
            BatchIngestThreadManager.removeBatchset( batch_set );
        }
        
        return new ModelAndView( "batchIngest.htm" );
        
	}

	
} // BatchIngestStatusFormController
