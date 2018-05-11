package client.vocabullary;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * <P>This class contains static variables that reference the terms from the VICINITY ontologies</P>
 *
 * * @author Andrea Cimmino
 * @version 1.0
 */
public class Ontology {

    public static final String describesProperty = "http://iot.linkeddata.es/def/core#describes";
    public static final String hasAccessMappingProperty = "http://iot.linkeddata.es/def/wot-mappings#hasAccessMapping";

    public static final String mapsResourcesFromProperty = "http://iot.linkeddata.es/def/wot-mappings#mapsResourcesFrom";
    public static final String hrefProperty = "http://iot.linkeddata.es/def/wot#href";

    public static final String hasMappingProperty = "http://iot.linkeddata.es/def/wot-mappings#hasMapping";

    public static final String keyProperty = "http://iot.linkeddata.es/def/wot-mappings#key";
    public static final String predicateProperty = "http://iot.linkeddata.es/def/wot-mappings#predicate";
    public static final String jsonPathProperty = "http://iot.linkeddata.es/def/wot-mappings#jsonPath";
    public static final String valuesTransformedBy = "http://iot.linkeddata.es/def/wot-mappings#valuesTransformedBy";

    public static final String type = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    public static final String objectPropertyMappingType = "http://iot.linkeddata.es/def/wot-mappings#ObjectPropertyMapping";

    public static final RDFNode ThingDescription = ResourceFactory.createResource("http://iot.linkeddata.es/def/core#ThingDescription");
    public static final String hasComponentProperty = "http://iot.linkeddata.es/def/core#hasComponent";
}
