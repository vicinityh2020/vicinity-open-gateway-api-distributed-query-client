package client.discovery;

import client.model.Triple;
import client.jena.JenaUtils;
import client.vocabullary.Ontology;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphExtract;
import org.apache.jena.graph.TripleBoundary;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;


/**
 * <P>Iterator that discovers IRI Things withing a TED in each step. It allows another class do the requests to remote endpoints in order to retrieve the Thing RDF data.</P>
 *
 * <P> Notice that each step of the Iterator returns a list of IRIs so their requests can be parallelized</P>
 * @author Andrea Cimmino
 * @version 1.0
 */
public class IteratorDiscovery implements Iterator<List<String>> {

    // Variables required Iterator
    private Boolean hasNestedThings; // Iterator condition
    private List<String> thingsFound; // Used to check that a Thing is not discovered twice

    // RDF variables used for discovery
    private Model rdfData; // RDF model that is built incrementally with new discovered things
    private Map<String,String> modelPrefixes; // Prefixes used in the GATEWAY API SERVICES

    private StringBuilder log; // log cached in-memory

    /**
     * Contructor for the IteratorDiscovery class
     * @param jsonTED A JSON-LD document containing a TED
     * @param neighbours A set of neighbour oid
     * @param jsonPrefixes A JSON document containing the prefixes used in the VICINITY Ontologies
     */
    public IteratorDiscovery(String jsonTED, Set<String> neighbours, String jsonPrefixes){
        this.hasNestedThings = true;
        this.log = new StringBuilder();
        this.thingsFound = new ArrayList<String>();
        this.modelPrefixes = JenaUtils.fromJsonToMap(jsonPrefixes, log);
        this.rdfData = discoveryRelevantThingsInTED(jsonTED, neighbours);
    }

    /**
     * This method specifies if there are still Things for discover within a provided TED
     * @return A boolean value that specifies if discovery should continue
     */
    public boolean hasNext() {
        return hasNestedThings;
    }

    /**
     * This method returns a list of Thing IRIs that where discovered in the TED and which RDF should be retrieved from the Semantic Repository
     * @return A list of Thing IRIs
     */
    public List<String> next() {
        List<String> nestedThingsIris = new ArrayList<String>();
       if(hasNestedThings) {
           // Discover things in the model
           nestedThingsIris = retrieveNestedThings(rdfData);
           // Check that things retrieved not were already discovered before
           List<String> nestedThingsAlreadyFound = new ArrayList<String>();
           for(String nestedThingIri:nestedThingsIris){
                if(thingsFound.contains(nestedThingIri)) {
                    nestedThingsAlreadyFound.add(nestedThingIri);
                }else{
                    thingsFound.add(nestedThingIri);
                }
           }
           nestedThingsIris.removeAll(nestedThingsAlreadyFound);
           // Update iteration condiction
           hasNestedThings = !nestedThingsIris.isEmpty();
           log.append("\n[INFO] New Things discovered: ").append(nestedThingsIris.size());
        }
       return nestedThingsIris;
    }

    /**
     * This method updates the Things discovered allowing further discoveries
     * @param jsonThing A JSON-LD document containing a Thing information
     */
    public void updateDiscovery(String jsonThing){
        Model thing = ModelFactory.createDefaultModel();
        try {
            // Transforms jsonThing into RDF
            log.append("\n[INFO] Updating RDF model from JSON Thing document");
            InputStream thingRDFStream = new ByteArrayInputStream(jsonThing.getBytes());
            thing.read(thingRDFStream, null, Lang.JSONLD.getName());
        }catch(Exception e){
            log.append("\n[ERROR] ").append(e.toString());
        }
        // Update global model
        rdfData.add(thing);
    }

    @Deprecated
    public void remove() {
        // empty method
    }

    /**
     * This method returns the cached log for this method
     * @return A log containing all the execution information of this class
     */
    public StringBuilder getLog(){
        return log;
    }

    /**
     * This method finds the Gateway API Addresses that should be queried in order to complete the Things information
     * @return  A list of Triples in which the first element is a Thing IRI, the second an AccessMapping IRI related to the Thing, and the third is a Gateway API address containing relevant JSON data that should be virtualized into RDF
     */
    public List<Triple<String,String,String>> getRelevantGatewayAPIAddresses(){
        List<Triple<String,String,String>> output = null;
        if(!hasNestedThings) {
            output = retrieveNetworkLinks(rdfData);
        }else{
            log.append("\n[ERROR] There are still relevant Thing IRIs to discover from the GATEWAY API SERVICES");
        }
        return output;
    }

    /**
     * This method outputs all the data discovered
     * @return A jena RDF Model containing all the Things discovered
     */
    public Model getRDFDataDiscovered(){
        Model output = null;
        if(!hasNestedThings) {
            output = this.rdfData;
        }else{
            log.append("\n[ERROR] There are still relevant Thing IRIs to discover from the GATEWAY API SERVICES");
        }
        return output;
    }

    /**
     * This method cleans all the memory used by this class
     */
    public void end(){
        rdfData.close();
        this.rdfData = null;
        this.hasNestedThings = false;
        this.thingsFound = null;
        this.modelPrefixes = null;
        this.log = null;
    }

    /* ******************************* */
    /*  Private methods for discovery  */
    /* ******************************* */

    /*
     *  Methods to work with the TED
     */

    /**
     * This method returns a RDF model with the Things from a TED that are in a neighbourhood
     * @param jsonTed A JSON containing the data of a TED
     * @param neighbours A set of ids that are neighbours of current requester
     * @return A jena Model with the RDF of the Things in the neighbourhood
     */
    private Model discoveryRelevantThingsInTED(String jsonTed, Set<String> neighbours) {
        Model relevantThings = ModelFactory.createDefaultModel();
        Model ted = JenaUtils.fromJSONtoRDFModel(jsonTed, log);
        List<RDFNode> tedComponents = ted.listObjectsOfProperty(ResourceFactory.createProperty(Ontology.hasComponentProperty)).toList();
        log.append("\n[INFO] Retrieved Things from TED: ").append(tedComponents.size());
        log.append("\n[INFO] Filtering Things outside the neighbourhood");
        int things = 0;
        for (RDFNode node : tedComponents) {
            if (isANeighbor(neighbours, node.asResource().toString())) { // keeps only things in the neighbourhood
                GraphExtract extractor = new GraphExtract(TripleBoundary.stopNowhere);
                Graph extracted = extractor.extract(node.asNode(), ted.getGraph());
                Model thingModel = ModelFactory.createModelForGraph(extracted);
                relevantThings.add(thingModel);
                things++;
            }
        }
        log.append("\n[INFO] Things in the neighbourhood: ").append(things);
        return relevantThings;
    }

    /**
     * This method returns true when an IRI is contained in a set of neighbors OID, and thus, the IRI is a neighbour
     * @param neighbors A Set of OID node neighbours
     * @param neighborIRI An IRI referring to a resource
     * @return A boolean value that establishes whether the IRI is a neighbour
     */
    private Boolean isANeighbor(Set<String> neighbors, String neighborIRI){
        Boolean areTheSame = false;
        for(String neighborID:neighbors) {
            String[] tokens = neighborIRI.split("/");
            for (String token : tokens) {
                if(token.equals(neighborID)) {
                    areTheSame = true;
                    break;
                }
            }
            if(areTheSame)
                break;
        }

        return areTheSame;
    }

    /*
     *  Methods to discovery from TED
     */


    /**
     * This method returns a jena Model with Things that are related to Things not contained in the input model
     * @param nodeRDF A jena Model
     * @return A list Thing IRIs connected to the input Thing IRI
     */
    private List<String> retrieveNestedThings(Model nodeRDF){

        List<String> leafs = JenaUtils.retrieveLeafsFromModel(nodeRDF);
        List<String> things = new ArrayList<String>();
        for(String leaf:leafs){
            Boolean hasDataInModel = nodeRDF.contains(ResourceFactory.createResource(leaf), null);
            if(!isVocabularyIri(leaf) && !hasDataInModel){
                things.add(leaf.trim());

            }
        }
        log.append("\n[INFO] Nested things found: ").append(things.size());
        return things;
    }

    /**
     * This method establishes when a given IRI its a vocabulary IRI or not
     * @param iri A IRI
     * @return true when the input IRI was a vocabulary IRI
     */
    private Boolean isVocabularyIri(String iri){
        Boolean isOntologyResource= false;
        for(String prefix:modelPrefixes.keySet()){
            String iriPrefix = modelPrefixes.get(prefix);
            if(iri.contains(iriPrefix)){
                isOntologyResource = true;
                break;
            }
        }
        return isOntologyResource;
    }

    /**
     * This method finds Gateway Api addresses and relates them to the AccessMapping required to transform their Json into RDF, in addition also relates them to the Thing where such RDF should be attached
     * @param neighborThings A model containing Things and their Thing Descriptions
     * @return A triple containing a Thing IRI, an associated AccessMapping, and a Gateway Api address
     */
    private  List<Triple<String,String,String>> retrieveNetworkLinks(Model neighborThings){
        List<Triple<String,String,String>> networkLinks = new ArrayList<Triple<String, String, String>>();
        // Retrieve thing descriptions
        StmtIterator thingDescriptionIterator = neighborThings.listStatements(null, ResourceFactory.createProperty(Ontology.type), Ontology.ThingDescription);
        while(thingDescriptionIterator.hasNext()){
            Statement thingDescriptionStatement = thingDescriptionIterator.next();
            Resource thingDescriptionResource = thingDescriptionStatement.getSubject();
            Model thingDescriptionModel = thingDescriptionStatement.getModel();
            // For each retrieve their access mappings
            List<RDFNode> accessMappings = thingDescriptionModel.listObjectsOfProperty(thingDescriptionResource, ResourceFactory.createProperty(Ontology.hasAccessMappingProperty)).toList();
            for(RDFNode accessMapping : accessMappings) {
                // Retrieve described thing
                List<RDFNode> describedThingIris = thingDescriptionModel.listObjectsOfProperty(thingDescriptionResource, ResourceFactory.createProperty(Ontology.describesProperty)).toList();
                if (describedThingIris.size() == 0){
                    log.append("\n[WARNING] No things described by description '").append(thingDescriptionStatement.getSubject()).append("'");
                }else if(describedThingIris.size()>1){
                    log.append("\n[ERROR] Thing Description '").append(thingDescriptionResource).append("' describes more than one Thing : ").append(JenaUtils.fromRDFNodeToString(describedThingIris));
                }else{
                    // Retrieve links
                    List<RDFNode> links = thingDescriptionModel.listObjectsOfProperty(accessMapping.asResource(), ResourceFactory.createProperty(Ontology.mapsResourcesFromProperty)).toList();
                    if(links.size()==0)
                        log.append("\n[WARNING] No Links were found for access mapping '").append(accessMapping).append("'");
                    for(RDFNode link: links) {
                        // Retrieve href
                        List<RDFNode> hrefs = thingDescriptionModel.listObjectsOfProperty(link.asResource(), ResourceFactory.createProperty(Ontology.hrefProperty)).toList();
                        if(hrefs.size()==0){
                            log.append("\n[WARNING] No hrefs were found for Link '").append(link).append("'");
                        }else if(hrefs.size()>1){
                            log.append("\n[ERROR] More than one href was found for Link '").append(link).append("'");
                        }else{
                            String thingIri = describedThingIris.get(0).toString();
                            String accessMappingIri = accessMapping.toString();
                            String href = hrefs.get(0).asLiteral().getString();
                            Triple<String, String, String> networkLink = new Triple<String, String, String>(thingIri, accessMappingIri, href);
                            networkLinks.add(networkLink);
                        }
                    }
                }
            }
        }
        log.append("\n[INFO] Relevant network links for query found: ").append(networkLinks.size());
        return networkLinks;
    }
}
