
The Batch Ingest process uploads data files and the meta data that describes them into a Fedora Repository.

The config file with BATCH_INGEST locations is is ../WEB-INF/config/fedoraApp.properties In addition there are properties files within each institution directory (see below) that contain the user options selected during the previous batch ingest. 

The METS files to be ingested contain the meta data that describes their content files. These files are called PCOs for Primary Content Objects. A single METS file and ALL of it's PCOs become a single Fedora DO (digital object) which is assigned a single unique identifier called a PID. A world wide digital handle is also assigned by handle.net This handle is requested from the Alliance Digital Repository (ADR) for each PID. This is accomplished by passing the PID to a web service hosted at the ADR http://adr.coalliance.org/ and the ADR obtains the handle from handle.net. 

**Prior** to the start of a batch ingest, user options are preformed. This could include any of the following, deleting all previous completed files (in the completed folder), deleting all failed files (in the failed folder) and deleting following reports (in the logs folder). 

**While** the batch ingest is running any METS file that completes without error is moved into the completed folder. Any mets file the fails is moved into the failed folder. If the user has selected to move PCOs (primary content objects) they will moved into the completed folder when a METS ingest is successful. If the ingest fails PCOs are never moved into the failed folder. 

**After** completion of the batch run, two reports are generated, the main report which shows the number of completed and failed files along with a list of all errors. The second report is a PID report, this report maps the PID and the contents of the OBJID in the supplied METS file. 

The batch Ingest allows multiple METS objects and their PCOs (Primary Context Objects) to be ingested with a single background thread. The Batch Ingest is tied to the concept of a '**batch set**'. A batch set is a project for a particular institution. An example of an institution is the 'University of Denver' and a project would be a group of METs files and their PCOs that are to be ingested. The institution and project (batch set) are mapped to a directory structure. For example if the top folder, that contains all institutions and their batch sets, is named 'batch_space' (set in the main config file) and the institution name/string for Colorado's University of Denver is 'codu' and the project is called 'frid' **then the batch set** would be under the path **/batch_space/codu/frid/**

**A batch set contains the following directory structure** (folder names are defined in the main config file):

* **completed**: METs files are moved to this directory after successful ingest. 
* **completedBatchFiles**: batch files are put here after being split into mets files. 
* **failed**: METs file are moved to this directory if they fail to ingest. 
* **failedBatchFiles**: batch files that cannot be split due to format errors are put here. 
* **files**: contains the PCOs referred to in the METs files. 
* **images**: not currently used july-2012 logs - contains reports about each run of the batch ingest. 
* **mets**: contains the new and update directories containing METs XML records that are to be ingested. 
* **tasksTemp** a temporary folder, use by remote ingest tasks, (tasks not started from the GUI). 
* **work**: used by ingest for temp file storage. Also XML files that are going to be split, into separate METS files, prior to ingest are stored here. 

Batch set directory structure example:

```
batch_space (location and name set in WEB-INF/config/fedorapp.properties)
└── codu (institution - determined at runtime from directory names) 
    └── frid (batch set - determined at runtime from directory names) 
        ├── completed
        ├── completedBatchFiles
        ├── failed
        ├── failedBatchFiles
        ├── files
        ├── imags
        ├── logs
        ├── mets
        │   ├── new
        │   └── updates
        ├── tasksTemp
        └── work
```

FedoraProxy
