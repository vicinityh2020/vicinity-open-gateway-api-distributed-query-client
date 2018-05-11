package client;

import client.discovery.IteratorDiscovery;
import client.model.Triple;
import client.virtualizer.RDFVirtualizer;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;

import java.util.*;

/**
 * <P>This class is allows discovering and accessing Things in the VICINITY cloud. However relies on an external module request data to the remote endpoints. </P>
 *
 * @author Andrea Cimmino
 * @version 1.0
 */
public class VicinityClient {

    private RDFVirtualizer virtualizer;
    private IteratorDiscovery iterator;
    private StringBuilder log;

    /**
     * Constructor of VicinityClient class
     * @param jsonTed A JSON-LD document containing a TED
     * @param neighbours A set of neighbour oid
     * @param jsonPrefixes A JSON document containing the prefixes used by the VICINITY ontologies
     */
    public VicinityClient(String jsonTed, Set<String> neighbours, String jsonPrefixes){
        this.virtualizer = new RDFVirtualizer();
        iterator = new IteratorDiscovery(jsonTed, neighbours, jsonPrefixes);
        log = new StringBuilder();
    }


    /*
     * General purpose method
     */

    /**
     * This method cleans all the memory used by this class
     */
    public void close(){
        iterator.end();
        virtualizer.clearLog();
        virtualizer = null;
        iterator = null;
    }

    /**
     * This method returns the cached log for this method
     * @return A log containing all the execution information of this class
     */
    public StringBuilder getLog(){
        return iterator.getLog().append(virtualizer.getLog());
    }

    /*
     * Discovery methods
     */

    /**
     * This method specifies if there are still Things for discover within a provided TED
     * @return A boolean value that specifies if discovery should continue
     */
    public Boolean existIterativelyDiscoverableThings(){
        return iterator.hasNext();
    }

    /**
     * This method returns a list of Thing IRIs that where discovered in the TED and which RDF should be retrieved from the Semantic Repository
     * @return A list of Thing IRIs
     */
    public List<String> discoverRelevantThingIRI(){
        List<String> relevantThingIRI = new ArrayList<String>();
        if(iterator.hasNext()){
            relevantThingIRI = iterator.next();
        }
        return relevantThingIRI;
    }

    /**
     * This method updates the Things discovered allowing further discoveries
     * @param jsonThing A JSON-LD document containing a Thing information
     */
    public void updateDiscovery(String jsonThing){
        iterator.updateDiscovery(jsonThing);
    }

    /**
     * This method finds the Gateway API Addresses that should be queried in order to complete the Things information
     * @return  A list of Triples in which the first element is a Thing IRI, the second an AccessMapping IRI related to the Thing, and the third is a Gateway API address containing relevant JSON data that should be virtualized into RDF
     */
    public List<Triple<String,String,String>> getRelevantGatewayAPIAddresses(){
        return iterator.getRelevantGatewayAPIAddresses();
    }

    /*
     * RDF Virtualization
     */

    /**
     * This method virtualizes the JSON data of the relevant Gateway API into RDF and solves a given query
     * @param query A SPARQL Query
     * @param relevantGatewayApiData A list of Triples in which the first element is a Thing IRI, the second an AccessMapping IRI related to the Thing, and the third is a JSON document exposed by a remote Gateway API that will be virtualized into RDF
     * @return A set results for the given query as a List of Maps in which each key is a query variable and its related value the query solution for such variable
     */
    public List<Map<String,String>> solveQuery(String query, List<Triple<String,String,String>> relevantGatewayApiData){
        Model rdf = iterator.getRDFDataDiscovered();
        List<Map<String,String>> queryResults = null;
        if(!iterator.hasNext()) {
            // Virtualize RDF
            for (Triple<String, String, String> gatewayApiData : relevantGatewayApiData) {
                String thingIri = gatewayApiData.getFirstElement();
                String accessMappingIri = gatewayApiData.getSecondElement();
                String jsonData = gatewayApiData.getThirdElement();
                Model virtualizedRDF = virtualizer.virtualizeRDF(rdf, thingIri, accessMappingIri, jsonData);
                rdf.add(virtualizedRDF);
            }
            queryResults = executeQuery(query, rdf);
        }else{
            log.append("\n[ERROR] There are still relevant Thing IRIs to discover from the GATEWAY API SERVICES");
        }

        return queryResults;
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
        }catch (Exception e){
            log.append("\n[ERROR]").append(e.toString());
        }
        return queryResults;
    }


}
