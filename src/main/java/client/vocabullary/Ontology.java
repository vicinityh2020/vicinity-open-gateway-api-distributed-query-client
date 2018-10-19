package client.vocabullary;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * <P>This class contains static variables that reference the terms from the VICINITY ontologies</P>
 *
 * * @author Andrea Cimmino
 * @version 1.0
 */
public class Ontology {

	public static final String TURTULE = "TURTLE";
	public static final String JSONLD = "JSONLD";
	
	public static final List<String> virtualizationPatterns = new ArrayList<String>(14);
	
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
	
    public static final String WOT_MAPPING_MAPS_RESOURCES_FROM = "http://iot.linkeddata.es/def/wot-mappings#mapsResourcesFrom";
    public static final String WOT_MAPPING_LINK_HREF = "http://iot.linkeddata.es/def/wot#href";

	public static final String CORE_TED_HAS_COMPONENT = "http://iot.linkeddata.es/def/core#hasComponent";
	public static final Property MAP_TD_HAS_ACCESS_MAPPING = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#hasAccessMapping");
    public static final Property RDF_TYPE = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

    public static final Resource WOT_TYPE_ACCESS_MAPPING = ResourceFactory.createResource("http://iot.linkeddata.es/def/wot-mappings#AccessMapping");
    
    public static final Property CORE_TD_DESCRIBES = ResourceFactory.createProperty("http://iot.linkeddata.es/def/core#describes");

    public static final String mapsResourcesFromProperty = "http://iot.linkeddata.es/def/wot-mappings#mapsResourcesFrom";
    public static final String hrefProperty = "http://iot.linkeddata.es/def/wot#href";

    public static final String hasMappingProperty = "http://iot.linkeddata.es/def/wot-mappings#hasMapping";
    
    public static final String keyProperty = "http://iot.linkeddata.es/def/wot-mappings#key";
    public static final String predicateProperty = "http://iot.linkeddata.es/def/wot-mappings#predicate";
    public static final String jsonPathProperty = "http://iot.linkeddata.es/def/wot-mappings#jsonPath";
    public static final String valuesTransformedBy = "http://iot.linkeddata.es/def/wot-mappings#valuesTransformedBy";


    public static final String objectPropertyMappingType = "http://iot.linkeddata.es/def/wot-mappings#ObjectPropertyMapping";

    public static final RDFNode ThingDescription = ResourceFactory.createResource("http://iot.linkeddata.es/def/core#ThingDescription");
    public static final String hasComponentProperty = "http://iot.linkeddata.es/def/core#hasComponent";
}
