package client;

import client.virtualizer.RDFVirtualizer;
import client.vocabullary.Ontology;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphExtract;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.TripleBoundary;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * <P>This class is allows discovering and accessing Things in the VICINITY cloud thorugh the Gateway API Services</P>
 *
 * @author Andrea Cimmino
 * @email cimmino@fi.upm.es
 * @version 0.6.3
 */
public class VicinityAgoraClient implements VicinityClient {

    private RDFVirtualizer virtualizer;
    private String queryString;
	private Logger log = Logger.getLogger(VicinityAgoraClient.class.getName());
	private Model filteredStaticRDF;
    
	/**
     * Constructor of VicinityClient class
     * @param jsonTed A JSON-LD document containing a TED
     * @param neighbours A set of neighbour oid
     */
    public VicinityAgoraClient(String jsonTed, Set<String> neighbours, String query){
    		Model ted = parseRDF(jsonTed, Ontology.JSONLD);
    		filteredStaticRDF = filterTedByNeighbours(ted, neighbours);
    		queryString = query;
    		this.virtualizer = new RDFVirtualizer();
    }
    
    // -- Neighbors filtering
    
    /**
     * This method filters the resources within a TED taking into account a set of allowed neighbors
     * @param ted a Thing Ecosystem Descriptor
     * @param neighbours A set of neighbors identifiers
     * @return A RDF {@link Model} containing all the allowed neighbors static RDF within the TED
     */
    private Model filterTedByNeighbours(Model ted, Set<String> neighbours) {
    		Model neighboursStaticRDF = ModelFactory.createDefaultModel();
		// 1. Retrieve from ted all these objects that are in the range of core:component property
		NodeIterator tedResourcesIris = ted.listObjectsOfProperty(ResourceFactory.createProperty(Ontology.CORE_TED_HAS_COMPONENT));
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
				    Model neighbourRDF = extractFullSubTree(resourceNode, ted) ;
				    neighboursStaticRDF.add(neighbourRDF);
				}
			}
		}
		return neighboursStaticRDF;
    }
    
    
    
    
    // -- Iteratively discovery
    
    /* (non-Javadoc)
	 * @see client.VicinityClient#getRelevantGatewayAPIAddresses()
	 */
    public List<Entry<String, String>> getRelevantGatewayAPIAddresses(){
    		List<Entry<String, String>> remoteEndpoints = new ArrayList<Entry<String, String>>();
    		// 1. Check that query requires to access real-time distributed data
    		Boolean gatherRemoteEndpoints = areRemoteEnpointsRequired();
    		if(gatherRemoteEndpoints) {
    			// 1.A if that's the case retrieve first all the map:AccessMapping resources
    			NodeIterator accessMappings = this.filteredStaticRDF.listObjectsOfProperty(Ontology.MAP_TD_HAS_ACCESS_MAPPING);
    			
    			while(accessMappings.hasNext()) {
    				// 1.A.1 For each map:AccessMapping extract its IRI, RDF, and remote endpoint Link
    				Node accessMappingNode = accessMappings.next().asNode();
    				Model accessMappingRDF = extractFullSubTree(accessMappingNode, this.filteredStaticRDF);
    				String accessMappingRDFString = toString(accessMappingRDF);
    				List<String> remoteEndpointAddresses = extractRemoteAddresses(accessMappingRDF);
    				// 1.A.2 Add each remote endpoint Link found to the output variable, consider that each endpoint will be associated to the map:AccessMapping RDF
    				int remoteAddressesSize = remoteEndpointAddresses.size();
    				for(int remoteAddressIndex=0; remoteAddressIndex < remoteAddressesSize; remoteAddressIndex++)
    					remoteEndpoints.add(new AbstractMap.SimpleEntry<String, String>(accessMappingRDFString,remoteEndpointAddresses.get(remoteAddressIndex)));
    			}
    		}
    		if(remoteEndpoints.isEmpty()) 
    			log.info("No remote endpoints are required to solve this query");
    		
    			
    		return remoteEndpoints;
    }
    
    /**
     * This method checks that the query provided in the constructor contains a pattern referencing the part of the ontology that models real-time data<p>.
     * Therefore this method specifies whether a query requires to retrieve data from remote endpoints or not.
     * @return A boolean value specifying if remote endpoints should be accessed
     */
	private Boolean areRemoteEnpointsRequired() {
		Boolean gatherRemoteEndpoints = false;
		int size = Ontology.virtualizationPatterns.size();
		for (int index = 0; index < size; index++) {
			String pattern = Ontology.virtualizationPatterns.get(index);
			if (this.queryString.contains(pattern)) {
				gatherRemoteEndpoints = true;
				break;
			}
		}
		return gatherRemoteEndpoints;
	}
    
    /**
     * This method extracts the wot:Link contained in a jena {@link Model} mapping
     * @param rdf The RDF related to a jena {@link Model} mapping
     * @return A collection of hrefs referenced in the input mapping
     */
    private List<String> extractRemoteAddresses(Model rdf) {
		List<String> remoteAddresses = new ArrayList<String>();
		NodeIterator remoteLinks = rdf.listObjectsOfProperty(ResourceFactory.createProperty(Ontology.WOT_MAPPING_MAPS_RESOURCES_FROM));
		while(remoteLinks.hasNext()) {
			RDFNode rdfNode = remoteLinks.next();
			// TODO: do here, if the node has media type RDF retrieve it
			NodeIterator remoteHRefs = rdfNode.getModel().listObjectsOfProperty(ResourceFactory.createProperty(Ontology.WOT_MAPPING_LINK_HREF));
			while(remoteHRefs.hasNext()) {
				String href = remoteHRefs.next().toString();
				remoteAddresses.add(href);
			}
		}
		
		return remoteAddresses;
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
    			Model accessMappingRDF = parseRDF(dynamicRDFTuple.getKey(), "TURTLE");
    			String jsonData = dynamicRDFTuple.getValue();
    			// translates the dynamic data into RDF and adds it to this.filteredStaticRDF
    			addVirtualizedData(accessMappingRDF, jsonData);
    		}
    		// 2. Notice that if gatewaysData was empty the query will be solved using the already stored static RDF
    		return executeQuery(queryString, this.filteredStaticRDF);
    }
    
    
    private void addVirtualizedData(Model accessMappingRDF, String jsonData) {
    		List<Resource> accessMappings = accessMappingRDF.listSubjectsWithProperty(Ontology.RDF_TYPE, Ontology.WOT_TYPE_ACCESS_MAPPING).toList();
    		int accessMappingsSize = accessMappings.size();
    		for(int index=0; index < accessMappingsSize; index++) {
        		String accessMappingIri = accessMappings.get(index).getURI();
        		List<String> describedThings = getDescribedThingsOfAccessMapping(accessMappingIri);
        		if(!describedThings.isEmpty()) {
        			int describedThingsSize = describedThings.size();
        			for(int describedThingsIndex = 0; describedThingsIndex < describedThingsSize; describedThingsIndex++) {
        				String thingDescribedIRI = describedThings.get(describedThingsIndex);
        				Model dynamicData =  this.virtualizer.virtualizeRDF(accessMappingRDF, thingDescribedIRI, accessMappingIri, jsonData);
        				this.filteredStaticRDF.add(dynamicData);
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
    		ResIterator descriptionsIterator = this.filteredStaticRDF.listSubjectsWithProperty(Ontology.MAP_TD_HAS_ACCESS_MAPPING, ResourceFactory.createResource(accessMappingIri));
    		while(descriptionsIterator.hasNext()) {
    			Resource descriptionResource = descriptionsIterator.next();
        		NodeIterator thingsDescribedIterator = this.filteredStaticRDF.listObjectsOfProperty(descriptionResource, Ontology.CORE_TD_DESCRIBES);
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
        Query query = QueryFactory.create(queryString) ;
        try {
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
        return queryResults;
    }
    
    
    
   
    
    /**
     * This method transforms a String variable with RDF content into a jena {@link Model}
     * @param strRDF A String variable containing RDF in "JSON-LD" format
     * @return a jena {@link Model}
     */
    private Model parseRDF(String strRDF, String format) {
    		Model parsedModel = ModelFactory.createDefaultModel();
    		try {
			 InputStream is = new ByteArrayInputStream( strRDF.getBytes() );
			 parsedModel.read(is, null, format);
    		}catch(Exception e) {
    			String message = new String("Something went wrong parsing RDF\n").concat(e.toString());
    			log.severe(message);
    		}
		return parsedModel;
	}
    
    /**
     * This method explores a graph (given as a {@link Model}) starting from a given {@link Node} until it reaches the leafs of the tree 
     * @param resourceNode A {@link Node} in the graph where the search starts
     * @param ted A {@link Model} containing the graph to explore
     * @return a String variable containing the explored graph as RDF in "TURTLE" format
     */
    private Model extractFullSubTree(Node resourceNode, Model ted) {
    		GraphExtract extractor = new GraphExtract(TripleBoundary.stopNowhere);
        Graph extracted = extractor.extract(resourceNode, ted.getGraph());
        return ModelFactory.createModelForGraph(extracted);
    }
    
    /**
     * This method transforms the RDF within a {@link Model} into a String variable
     * @param model a jena {@link Model} 
     * @return a String variable with the same RDF of the input {@link Model} in "TURTLE" format
     */
    private String toString(Model model) {
		Writer output = new StringWriter();
		model.write(output, Ontology.TURTULE);
		return output.toString();
	}

}
