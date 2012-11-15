<!-- NOTE: THIS IS CURRENTLY ONLY USED BY StringTemplate TEST , We are using API-M instead -->
<foxml:datastream ID="RELS-EXT" STATE="A" CONTROL_GROUP="X" VERSIONABLE="true">
 	<foxml:datastreamVersion ID="REL-EXT.0" MIMETYPE="application/rdf+xml" LABEL="RDF Statements about this Object">
		<foxml:xmlContent>
		
			<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:fedora="info:fedora/fedora-system:def/relations-external#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:fedora-model="info:fedora/fedora-system:def/model#">
				<rdf:description rdf:about="info:fedora/$PID$">
					<fedora:isMemberOfCollection rdf:resource="info:fedora/$PID_NAMESPACE$:$COLLECTION_NAME$"></fedora:isMemberOfCollection>
					<fedora-model:hasModel rdf:resource="info:fedora/$PID_NAMESPACE$:$CONTENT_MODEL$"></fedora-model:hasModel>
				</rdf:description>
			</rdf:RDF>

		</foxml:xmlContent>
	</foxml:datastreamVersion>
</foxml:datastream>			
		