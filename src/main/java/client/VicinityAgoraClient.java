package client;

import client.jena.JenaUtils;
import client.virtualizer.RDFVirtualizer;
import org.apache.jena.graph.Node;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * <P>This class allows discovering and accessing Things in the VICINITY cloud through the Gateway API Services</P>
 *
 * @author Andrea Cimmino
 * @email cimmino@fi.upm.es
 * @version 0.6.3
 */
public class VicinityAgoraClient implements VicinityClient {

	// -- Attributes
	
    private RDFVirtualizer virtualizer;
    private String queryString;
	private Logger log = Logger.getLogger(VicinityAgoraClient.class.getName());
	private Model filteredStaticRDF;
  
	
	
	// -- Contructors
	
	/**
     * Constructor of VicinityClient class
     * @param jsonTed A JSON-LD document containing a TED
     * @param neighbours A set of neighbour oid
     */
    public VicinityAgoraClient(String jsonTed, Set<String> neighbours, String query){
    		this.virtualizer = new RDFVirtualizer();
    		if(isSparqlQuery(query))
        		queryString = query;	
    		Model ted = JenaUtils.parseRDF(jsonTed, VicinityOntology.JSONLD);
    		if(!ted.isEmpty()) 
    			filteredStaticRDF = filterTedByNeighbours(ted, neighbours);
    		

    }
    
    private Boolean isSparqlQuery(String queryString) {
    		Boolean isSparql =false;
    		try {
    			QueryFactory.create(queryString);
    			isSparql = true;
    		}catch(Exception e) {
    			log.severe("Provided sparql query has sintax problems, check it out: "+queryString);
    		}
    		return isSparql;
    }
    
    // -- Methods
    
    // -- -- Neighbors filtering
    
    /**
     * This method filters the resources within a TED taking into account a set of allowed neighbors
     * @param ted a Thing Ecosystem Descriptor
     * @param neighbours A set of neighbors identifiers
     * @return A RDF {@link Model} containing all the allowed neighbors static RDF within the TED
     */
    private Model filterTedByNeighbours(Model ted, Set<String> neighbours) {
    		Model neighboursStaticRDF = ModelFactory.createDefaultModel();
		// 1. Retrieve from ted all these objects that are in the range of core:component property
		NodeIterator tedResourcesIris = ted.listObjectsOfProperty(ResourceFactory.createProperty(VicinityOntology.CORE_TED_HAS_COMPONENT));
		while(tedResourcesIris.hasNext()){
			// 1.1. For each object retrieve its RDF and its IRI
			Node resourceNode = tedResourcesIris.next().asNode();
			String resourceIri = resourceNode.getURI();
			// 1.2 Iterate over the neighbours now
			Iterator<String> neighboursIterator = neighbours.iterator();
			while(neighboursIterator.hasNext()) {
				// 1.2.1 If a neighbour identifier is contained in a ted IRI...
				String neighbour = neighboursIterator.next();
				if(resourceIri.contains(neighbour)) {
					// 1.2.1.A ...then, store the RDF of such ted's object
				    Model neighbourRDF = JenaUtils.extractFullSubTree(resourceNode, ted) ;
				    neighboursStaticRDF.add(neighbourRDF);
				}
			}
		}
		return neighboursStaticRDF;
    }
    
    
    
    
    // -- Iteratively discovery

    public List<Entry<String, String>> getRelevantGatewayAPIAddresses(){
    		List<Entry<String, String>> remoteEndpoints = new ArrayList<>();
    		// 1. Check that query requires to access real-time distributed data
    		if(this.queryString!=null && filteredStaticRDF!=null && !filteredStaticRDF.isEmpty()) {
	    		Boolean gatherRemoteEndpoints = VicinityOntology.areRemoteEnpointsRequired(this.queryString);
	    		if(gatherRemoteEndpoints) {
	    			// 1.A if that's the case retrieve first all the map:AccessMapping resources
	    			NodeIterator accessMappings = this.filteredStaticRDF.listObjectsOfProperty(VicinityOntology.MAP_TD_HAS_ACCESS_MAPPING);
	    			while(accessMappings.hasNext()) {
	    				// 1.A.1 For each map:AccessMapping extract its IRI, RDF, and remote endpoint Link
	    				Node accessMappingNode = accessMappings.next().asNode();
	    				Model accessMappingRDF = JenaUtils.extractFullSubTree(accessMappingNode, this.filteredStaticRDF);
	    				String accessMappingRDFString = JenaUtils.toString(accessMappingRDF);
	    				List<String> remoteEndpointAddresses = extractRemoteAddresses(accessMappingRDF);
	    				// 1.A.2 Add each remote endpoint Link found to the output variable, consider that each endpoint will be associated to the map:AccessMapping RDF
	    				int remoteAddressesSize = remoteEndpointAddresses.size();
	    				for(int remoteAddressIndex=0; remoteAddressIndex < remoteAddressesSize; remoteAddressIndex++)
	    					remoteEndpoints.add(new AbstractMap.SimpleEntry<String, String>(accessMappingRDFString,remoteEndpointAddresses.get(remoteAddressIndex)));
	    			}
	    		}
	    		
	    		if(remoteEndpoints.isEmpty()) 
	    			log.info("No remote endpoints are required to solve this query");
    		}
    			
    		return remoteEndpoints;
    }
    
    
    
    /**
     * This method extracts the wot:Link contained in a jena {@link Model} mapping
     * @param rdf The RDF related to a jena {@link Model} mapping
     * @return A collection of hrefs referenced in the input mapping
     */
    private List<String> extractRemoteAddresses(Model rdf) {
		List<String> remoteAddresses = new ArrayList<>();
		NodeIterator remoteLinks = rdf.listObjectsOfProperty(ResourceFactory.createProperty(VicinityOntology.WOT_MAPPING_MAPS_RESOURCES_FROM));
		while(remoteLinks.hasNext()) {
			RDFNode rdfNode = remoteLinks.next();
			Model nodeModel = rdfNode.getModel();
			List<RDFNode> mediaTypes = nodeModel.listObjectsOfProperty(VicinityOntology.WOT_ACCESS_MAPPING_HAS_MEDIA_TYPE).toList();
			mediaTypes.addAll(nodeModel.listObjectsOfProperty(VicinityOntology.WOT_ACCESS_MAPPING_MEDIA_TYPE).toList());
			if(!mediaTypes.isEmpty()) {
				NodeIterator remoteHRefs = rdfNode.getModel().listObjectsOfProperty(ResourceFactory.createProperty(VicinityOntology.WOT_MAPPING_LINK_HREF));
				while(remoteHRefs.hasNext()) {
					String href = remoteHRefs.next().toString();
					String mediaType = mediaTypes.get(0).asLiteral().getString();
					try {
						if(isRDFMediaType(mediaType)) {
							String remoteRDF = Unirest.get(href).asString().getBody();
							addRemoteRDF(remoteRDF, mediaType);
						}else if(isExternalLinkWithJSONMediaType(mediaType, href)){
							String json = Unirest.get(href).asString().getBody();
							ResIterator accessMappingIRIsIterator = rdf.listSubjectsWithProperty(VicinityOntology.RDF_TYPE, VicinityOntology.WOT_TYPE_ACCESS_MAPPING);
							while(accessMappingIRIsIterator.hasNext()) {
								addVirtualizedData(accessMappingIRIsIterator.next().getModel(), json);
							}
						}else {
							remoteAddresses.add(href);
						}
					} catch (UnirestException e) {
						log.severe(e.toString());
					}
				}
				
			}else {
				log.severe("No remote endpoints where found for accessMapping ");
			}
		}
		
		return remoteAddresses;
	}

	
    private void addRemoteRDF(String remoteRDF, String mediaType) {
    		if(VicinityOntology.CONTENT_TYPE_RDF_JSONLD.equals(mediaType)) {	
    			this.filteredStaticRDF.add(JenaUtils.parseRDF(remoteRDF, VicinityOntology.JSONLD));
    		}else if(VicinityOntology.CONTENT_TYPE_RDF_NT.equals(mediaType))	{
    			this.filteredStaticRDF.add(JenaUtils.parseRDF(remoteRDF, VicinityOntology.NT));
    		}else if(VicinityOntology.CONTENT_TYPE_RDF_TURLE.equals(mediaType))	{
    			this.filteredStaticRDF.add(JenaUtils.parseRDF(remoteRDF, VicinityOntology.TURTULE));
    		}else if(VicinityOntology.CONTENT_TYPE_RDF_XML.equals(mediaType))	{
    			this.filteredStaticRDF.add(JenaUtils.parseRDF(remoteRDF, VicinityOntology.RDF_XML));
    		}
		
	}

	private boolean isExternalLinkWithJSONMediaType(String mediaType, String href) {
		return href.contains("http") && mediaType.replace(" ", "").equals("application/json");
	}

	private boolean isRDFMediaType(String mediaType) {
		return mediaType.equals(VicinityOntology.CONTENT_TYPE_RDF_XML) || mediaType.equals(VicinityOntology.CONTENT_TYPE_RDF_TURLEX) || mediaType.equals(VicinityOntology.CONTENT_TYPE_RDF_TURLE) || mediaType.equals(VicinityOntology.CONTENT_TYPE_RDF_NT) || mediaType.equals(VicinityOntology.CONTENT_TYPE_RDF_JSONLD);
	}

	// -- Query solving
   
    /* (non-Javadoc)
	 * @see client.VicinityClient#solveQuery(java.util.List)
	 */
    public List<Map<String,String>> solveQuery(List<Entry<String, String>> gatewaysData){
    		// 1. Retrieve each map:AccessMapping RDF and its related json document
    		int dynamicRDFSize = gatewaysData.size();
    		for(int dynamicRDFIndex=0; dynamicRDFIndex < dynamicRDFSize; dynamicRDFIndex++) {
    			Entry<String,String> dynamicRDFTuple = gatewaysData.get(dynamicRDFIndex);
    			Model accessMappingRDF = JenaUtils.parseRDF(dynamicRDFTuple.getKey(), "TURTLE");
    			String jsonData = dynamicRDFTuple.getValue();
    			// translates the dynamic data into RDF and adds it to this.filteredStaticRDF
    			addVirtualizedData(accessMappingRDF, jsonData);
    		}
    		// 2. Notice that if gatewaysData was empty the query will be solved using the already stored static RDF
    		return executeQuery(queryString, this.filteredStaticRDF);
    }
    
    
    private void addVirtualizedData(Model accessMappingRDF, String jsonData) {
    		List<Resource> accessMappings = accessMappingRDF.listSubjectsWithProperty(VicinityOntology.RDF_TYPE, VicinityOntology.WOT_TYPE_ACCESS_MAPPING).toList();
    		int accessMappingsSize = accessMappings.size();
    		for(int index=0; index < accessMappingsSize; index++) {
        		String accessMappingIri = accessMappings.get(index).getURI();
        		List<String> describedThings = getDescribedThingsOfAccessMapping(accessMappingIri);
        		if(!describedThings.isEmpty()) {
        			int describedThingsSize = describedThings.size();
        			int describedThingsIndex = 0;
        			while( describedThingsIndex < describedThingsSize) {
        				String thingDescribedIRI = describedThings.get(describedThingsIndex);
        				Model dynamicData =  this.virtualizer.virtualizeRDF(accessMappingRDF, thingDescribedIRI, accessMappingIri, jsonData);
        				this.filteredStaticRDF.add(dynamicData);
        				describedThingsIndex++;
        			}
        		}else {
        			StringBuilder logMessage = new StringBuilder("Client is not able to retrieve the core:Value related to the accessMapping: ").append(accessMappingIri);
        			String message = logMessage.toString();
        			log.severe(message);
        		}
    		}
	}

    private List<String> getDescribedThingsOfAccessMapping(String accessMappingIri) {
    		List<String> coreValueIRIs = new ArrayList<String>();
    		ResIterator descriptionsIterator = this.filteredStaticRDF.listSubjectsWithProperty(VicinityOntology.MAP_TD_HAS_ACCESS_MAPPING, ResourceFactory.createResource(accessMappingIri));
    		while(descriptionsIterator.hasNext()) {
    			Resource descriptionResource = descriptionsIterator.next();
        		NodeIterator thingsDescribedIterator = this.filteredStaticRDF.listObjectsOfProperty(descriptionResource, VicinityOntology.CORE_TD_DESCRIBES);
        		while(thingsDescribedIterator.hasNext()) 
        			coreValueIRIs.add(thingsDescribedIterator.next().asResource().getURI());
    		}
    		return coreValueIRIs;
    }
    
	/**
     * This method executes a query over a jena RDF Model
     * @param queryString A SPARQL query
     * @param model A jena RDF Model
     * @return A list solutions for the given query under a Map format
     */
    private List<Map<String,String>> executeQuery(String queryString, Model model){
        List<Map<String,String>> queryResults = new ArrayList<Map<String, String>>();
        if(model!=null && !model.isEmpty()) {
	        try {
	        		Query query = QueryFactory.create(queryString) ;
	            QueryExecution qexec = QueryExecutionFactory.create(query, model);
	            ResultSet results = qexec.execSelect() ;
	            List<String> variables = results.getResultVars();
	            while (results.hasNext()) {
	                QuerySolution soln = results.nextSolution() ; // Query solution
	                Map<String,String> queryResult = new HashMap<String, String>();
	                // Transform to map the solution
	                for(String variable:variables){
	                    if(soln.contains(variable))
	                        queryResult.put(variable, soln.get(variable).toString());
	                }
	                if(queryResult.size()>0)
	                    queryResults.add(queryResult);
	            }
	            
	            qexec.close();
	            
	        }catch (Exception e){
	            log.severe(e.toString());
	        }
        }else {
        		log.severe("Cannot resolve query over null or empty Ted response");
        }
        return queryResults;
    }
    
    
    
   
   

}
