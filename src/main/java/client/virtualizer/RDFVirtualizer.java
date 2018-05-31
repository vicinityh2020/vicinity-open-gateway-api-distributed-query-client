package client.virtualizer;

import client.jena.JenaUtils;
import client.vocabullary.Ontology;
import com.jayway.jsonpath.JsonPath;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <P>This class generates RDF from a given JSON </P>
 *
 * @author Andrea Cimmino
 * @version 1.0
 */
public class RDFVirtualizer {

    private StringBuilder log;

    /**
     * Constructor of RDFVirtualizer class
     */
    public RDFVirtualizer(){
        log = new StringBuilder();
    }

    /**
     * This method returns the cached log for this method
     * @return A log containing all the execution information of this class
     */
    public StringBuilder getLog() {
        return log;
    }

    /**
     * This method cleans all the memory used by this class
     */
    public void clearLog() {
        log = new StringBuilder();
    }

    /**
     * This method virtualizes RDF from a given JSON document following the specifications of a certain AccessMapping
     * @param rdfData A jena Model containing the RDF associated to the AccessMapping
     * @param thingIri A Thing IRI where the virtualized RDF will be attached
     * @param accessMappingIri An AccessMapping IRI specifying how JSON will be transformed into RDF
     * @param jsonData A JSON Document
     * @return A jena Model containing the JSON data virtualized as RDF
     */
    public Model virtualizeRDF(Model rdfData, String thingIri, String accessMappingIri, String jsonData){
        Model outputModel = ModelFactory.createDefaultModel();
        // For each Mapping translate to RDF the value of a json key
        List<String> mappingIris = retrieveMappingResourcesFromAccessMapping(rdfData, accessMappingIri);
        for(String mappingIri: mappingIris) {
            Model virtualizedRDF = translateFromJsonToRDF(rdfData, thingIri, mappingIri, jsonData);
            outputModel.add(virtualizedRDF);
        }

        return outputModel;
    }


    /**
     * This method retrieves the Mappings of an AccessMapping
     * @param nodeRDF A model RDF containing data to be retrieved
     * @param accessMappingIri AccessMapping IRI from which Mapping IRIs will be retrieved
     * @return A list of Mapping IRI related to the input AccessMapping IRI
     */
    private List<String> retrieveMappingResourcesFromAccessMapping(Model nodeRDF, String accessMappingIri){
        List<String> mappingList = new ArrayList<String>();
        List<RDFNode> mappingNodes = nodeRDF.listObjectsOfProperty(ResourceFactory.createResource(accessMappingIri), ResourceFactory.createProperty(Ontology.hasMappingProperty)).toList();
        if(mappingNodes.size()==0)
            log.append("\n[WARNING] Access mapping '").append(accessMappingIri).append("' has no mappings");
        for(RDFNode mappingNode:mappingNodes){
            mappingList.add(mappingNode.toString());
        }
        return mappingList;
    }


    /**
     * This method translates a JSON document into RDF following a Mapping specification
     * @param thingDescriptionModel A RDF jena Model containing the data, e.g., the Mappings and the Thing Descriptions
     * @param describedIri An IRI where the virtualized RDF will be attached as its range
     * @param mappingIri The Mapping IRI that this method will follow to virtualize the RDF
     * @param jsonData A JSON document to be transformed into RDF
     * @return A RDF jena Model containing the virtualized RDF
     */
    private Model translateFromJsonToRDF(Model thingDescriptionModel, String describedIri, String mappingIri, String jsonData){
        Model result = ModelFactory.createDefaultModel();
        Resource thingResource = ResourceFactory.createResource(describedIri);
        // Retrieve Mapping properties
        String key = retrieveValuesOfMappingProperties(mappingIri, Ontology.keyProperty, thingDescriptionModel);
        String property = retrieveValuesOfMappingProperties(mappingIri, Ontology.predicateProperty, thingDescriptionModel);
        String jsonPath = retrieveValuesOfMappingProperties(mappingIri, Ontology.jsonPathProperty, thingDescriptionModel);
        String transformedByThingDescriptionIri = retrieveValuesOfMappingProperties(mappingIri, Ontology.valuesTransformedBy, thingDescriptionModel); // point the thingDescription from the transformedBy mapping

        // Filter the received json if there is a jsonPath
        JSONArray filteredJsons = retrieveFilteredJsonArray(jsonData, jsonPath);
        for (int index = 0; index < filteredJsons.length(); index++) {
            JSONObject filteredJson = (JSONObject) filteredJsons.get(index);
            if(transformedByThingDescriptionIri.length()==0 && key.length()>0) {
                // Plain Case: Data Property
                if(filteredJson!=null && filteredJson.keySet().contains(key)){
                    String jsonValue = String.valueOf(filteredJson.get(key.trim()));
                    // If the mapping is an object property with no transformedBy then the value contained in the json is virtualized as a resource
                    RDFNode node = ResourceFactory.createStringLiteral(jsonValue);
                    if(isObjectPropertyMapping(mappingIri, thingDescriptionModel))
                        node = ResourceFactory.createResource(jsonValue);
                    result.add(thingResource, ResourceFactory.createProperty(property), node);
                }else{
                    log.append("\n[WARNING] mapping key \"").append(key).append("\" does not match the json document ").append(filteredJson);
                }

            }else if(transformedByThingDescriptionIri.length()>0){
                // Recursive case: Object property

                String jsonPartition = filteredJson.toString(); // Retrieve the json that contains what should be transformed by
                if(key.length()>0) // if there is a key a partition of the json is retrieved, otherwhise the whole json will be transformed
                    jsonPartition = String.valueOf(filteredJson.get(key));

                String blankIri =  NodeFactory.createBlankNode().toString();
                List<String> accessMappingIris = JenaUtils.fromRDFNodeToString(thingDescriptionModel.listObjectsOfProperty(ResourceFactory.createResource(transformedByThingDescriptionIri), ResourceFactory.createProperty(Ontology.hasAccessMappingProperty)).toList());
                if( accessMappingIris.size()<1){
                    log.append("\n[WARNING] No AccessMapping were found when virtualizing RDF using description '").append(transformedByThingDescriptionIri).append("'");
                }else if(accessMappingIris.size()>1){
                    log.append("\n[WARNING] More than one AccessMapping were found when virtualizing rdf'");
                }else{
                    String accessMappingIri = accessMappingIris.get(0);
                    Model model = virtualizeRDF(thingDescriptionModel, blankIri, accessMappingIri, jsonPartition);
                    result.add(thingResource, ResourceFactory.createProperty(property), ResourceFactory.createResource(blankIri));
                    result.add(model);
                }

            }else{
                log.append("\n[INFO] key received ").append(key).append(" is empty");
                log.append("\n[INFO] TransformedBy received ").append(transformedByThingDescriptionIri).append(" is empty");
            }
        }

        return result;
    }


    /**
     * This method returns the array of filtered JSONs as result of applying a json path to a JSON document
     * @param json A JSON Document
     * @param jsonPath A json path
     * @return An array of JSON Documents that fulfil the json path
     */
    private JSONArray retrieveFilteredJsonArray(String json, String jsonPath){
        String stringJsons;
        if(jsonPath.length()>0) {
            stringJsons = applyJsonPath(json, jsonPath);
        }else{
            StringBuilder filteredStringJsons = new StringBuilder();
            filteredStringJsons.append("[").append(json).append("]");
            stringJsons = filteredStringJsons.toString();
        }
        // Obtain the array
        JSONArray filteredJsons = new JSONArray();
        if(stringJsons!=null) {
            filteredJsons = new JSONArray(stringJsons);
        }else{
            log.append("\n[ERROR] An error occurred with JSON Document: \n").append(json);
        }
        return filteredJsons;
    }

    /**
     * This method filters a JSON Document using a json path
     * @param json A JSON document
     * @param jsonPath A json path
     * @return A JSON document containing the data filtered by the json path
     */
    @SuppressWarnings("unchecked")
	private String applyJsonPath(String json, String jsonPath){
        String stringJson = null;
        try {
            Object value = JsonPath.parse(json).read(jsonPath);
            if (value instanceof LinkedHashMap) {
                JSONObject newJson = new JSONObject();
                LinkedHashMap<String, String> jsonMap = (LinkedHashMap<String,String>) value;
                for (String key : jsonMap.keySet()) {
                    newJson.put(key, jsonMap.get(key));
                }
                StringBuilder buffer = new StringBuilder();
                buffer.append("[").append(newJson.toString()).append("]");
                stringJson = buffer.toString();
            } else if (value instanceof List) {
                stringJson = value.toString();
            } else {
                log.append("\n[WARNING] Unknown type found when applying the JSON Path '").append(jsonPath).append("' to the JSON Document: \n").append(json);
            }
        }catch (Exception e){
           log.append("\n[WARNING] JSON Path \"").append(jsonPath).append("\" not applicable to JSON Document: \n").append(json);
        }

        return stringJson;
    }



    /**
     * This method retrieves the value that a Property has, as long as it belongs to a Mapping
     * @param mappingIri A Mapping IRI
     * @param propertyIri A Property IRI
     * @param rdfData A jena Model containing the RDF data of the Mapping
     * @return A string containing the value of the input Property
     */
    private String retrieveValuesOfMappingProperties(String mappingIri, String propertyIri, Model rdfData) {
        String value = null;

        Resource subject = ResourceFactory.createResource(mappingIri);
        Property property = ResourceFactory.createProperty(propertyIri);
        List<RDFNode> valuesNodes = rdfData.listObjectsOfProperty(subject, property).toList();
        List<String> values = JenaUtils.fromRDFNodeToString(valuesNodes);

        if (values.size() > 1) {
            log.append("\n[ERROR] More than one property value retrieved in mapping ").append(mappingIri);
        } else if (values.isEmpty()){
            value ="";
            if(propertyIri.contains("rootMode"))
                value ="false";
        }else{
            value = values.get(0).trim();
            if(value.equals("None") && propertyIri.contains("key")) {
                value = "";
            }
        }
        return value;
    }

    /**
     * This method returns the types that a Mapping has, i.e., Mapping, DataProperty or ObjectProperty
     * @param mappingIri A Mapping IRI
     * @param rdfData A jena Model containing the Mapping RDF
     * @return A list of IRIs that are the Mapping types
     */
    private List<String> retrieveMappingTypesFromThingDescription(String mappingIri, Model rdfData){
        Resource mappingIriResource = ResourceFactory.createResource(mappingIri);
        Property typeProperty = ResourceFactory.createProperty(Ontology.type);

        List<RDFNode> typesNodes = rdfData.listObjectsOfProperty(mappingIriResource,typeProperty).toList();

        return JenaUtils.fromRDFNodeToString(typesNodes);
    }

    /**
     * This method returns species whether a Mapping is an Object Property
     * @param mappingIri A Mapping IRI
     * @param rdfData A jena Model containing the Mapping RDF
     * @return A boolean value specifying whether a Mapping is an Object Property (true)
     */
    private Boolean isObjectPropertyMapping(String mappingIri, Model rdfData){
        List<String> types = retrieveMappingTypesFromThingDescription(mappingIri,rdfData);
        return types.contains(Ontology.objectPropertyMappingType);
    }


}
