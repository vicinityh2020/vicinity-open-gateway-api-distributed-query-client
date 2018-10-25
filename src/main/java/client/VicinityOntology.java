package client;

import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * <P>This class contains static variables that reference the terms from the VICINITY ontologies</P>
 *
 * @author Andrea Cimmino
 * @version 0.6.3
 */
public class VicinityOntology {
 
	// -- Attributes
	
	// -- -- RDF formats
	public static final String TURTULE = "TURTLE";
	public static final String JSONLD = "JSONLD";
	public static final String RDF_XML = "RDF/XML";
	public static final String NT = "N-TRIPLES";
    // -- -- RDF content types
    public static final String CONTENT_TYPE_RDF_XML = "application/rdf+xml";
    public static final String CONTENT_TYPE_RDF_TURLEX = "application/x-turtle";
    public static final String CONTENT_TYPE_RDF_TURLE = "text/turtle";
    public static final String CONTENT_TYPE_RDF_NT = "text/nt";
    public static final String CONTENT_TYPE_RDF_JSONLD = "application/ld+json";
    
    // -- -- rdf properties
    public static final Property RDF_TYPE = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    
    // -- --	 core properties
    public static final Property CORE_TD_DESCRIBES = ResourceFactory.createProperty("http://iot.linkeddata.es/def/core#describes");
    public static final String CORE_TED_HAS_COMPONENT = "http://iot.linkeddata.es/def/core#hasComponent";
    
    // -- --	 wot properties
    public static final Property WOT_HAS_MAPPING = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#hasMapping");
    public static final String WOT_OBJECT_PROPERTY_MAPPING = "http://iot.linkeddata.es/def/wot-mappings#ObjectPropertyMapping";
    public static final Property WOT_ACCESS_MAPPING_HAS_MEDIA_TYPE = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot#hasMediaType");
    public static final Property WOT_ACCESS_MAPPING_MEDIA_TYPE = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot#mediaType");
    public static final Resource WOT_TYPE_ACCESS_MAPPING = ResourceFactory.createResource("http://iot.linkeddata.es/def/wot-mappings#AccessMapping");
    public static final String WOT_MAPPING_MAPS_RESOURCES_FROM = "http://iot.linkeddata.es/def/wot-mappings#mapsResourcesFrom";
    public static final String WOT_MAPPING_LINK_HREF = "http://iot.linkeddata.es/def/wot#href";

    // -- --	 wot-mapping properties
    public static final Property MAP_KEY = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#key");
    public static final Property MAP_PREDICATE = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#predicate");
    public static final Property MAP_JSONPATH = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#jsonPath");
    public static final Property MAP_VALUES_TRANSFORMED_BY = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#valuesTransformedBy");
	public static final Property MAP_TD_HAS_ACCESS_MAPPING = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#hasAccessMapping");

	// -- -- SPARQL query statements entailing query needs to retrieve remote data
	private static final List<String> virtualizationPatterns = new ArrayList<String>(14);
	static {
		virtualizationPatterns.add("http://iot.linkeddata.es/def/core#Value");
		virtualizationPatterns.add("core:Value");
		virtualizationPatterns.add("http://iot.linkeddata.es/def/core#hasValue");
		virtualizationPatterns.add("core:hasValue");
		virtualizationPatterns.add("http://iot.linkeddata.es/def/core#literalValue");
		virtualizationPatterns.add("core:literalValue");
		virtualizationPatterns.add("http://iot.linkeddata.es/def/core#hasMaxValue");
		virtualizationPatterns.add("core:hasMaxValue");
		virtualizationPatterns.add("http://iot.linkeddata.es/def/core#hasMinValue");
		virtualizationPatterns.add("core:hasMinValue");
		virtualizationPatterns.add("http://iot.linkeddata.es/def/core#timeStamp");
		virtualizationPatterns.add("core:timeStamp");
		virtualizationPatterns.add("http://iot.linkeddata.es/def/core#expressedInFormat");
		virtualizationPatterns.add("core:expressedInFormat");
	}

    // -- Constructor
    
	/**
	 * Private constructor prevents Java to add an implicit public one for this utility class which is not meant to be instantiated
	 */
	private VicinityOntology() {
		// empty
	}
  
	// -- methods
	
	/**
     * This method checks that the query provided contains a pattern referencing the part of the ontology that models real-time data<p>.
     * Therefore this method specifies whether a query requires to retrieve data from remote endpoints or not.
     * @return A boolean value specifying if remote endpoints should be accessed
     */
	public static Boolean areRemoteEnpointsRequired(String query) {
		Boolean gatherRemoteEndpoints = false;
		int size = VicinityOntology.virtualizationPatterns.size();
		for (int index = 0; index < size; index++) {
			String pattern = VicinityOntology.virtualizationPatterns.get(index);
			if (query.contains(pattern)) {
				gatherRemoteEndpoints = true;
				break;
			}
		}
		return gatherRemoteEndpoints;
	}
}
