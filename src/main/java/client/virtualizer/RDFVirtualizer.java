package client.virtualizer;

import client.VicinityOntology;
import client.VicinityAgoraClient;
import client.jena.JenaUtils;

import com.jayway.jsonpath.JsonPath;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * <P>This class generates RDF from a given JSON following Agora mappings</P>
 *
 * @author Andrea Cimmino
 * @version 0.6.3
 */
public class RDFVirtualizer {

	// -- Attributes
	
	private Logger log = Logger.getLogger(VicinityAgoraClient.class.getName());
	
	// -- Constructors

	// -- Methods
	
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
        List<RDFNode> mappingNodes = nodeRDF.listObjectsOfProperty(ResourceFactory.createResource(accessMappingIri), VicinityOntology.WOT_HAS_MAPPING).toList();
        if(mappingNodes.isEmpty()) {
        		String message = logMessage1(accessMappingIri);
            log.warning(message);
        }
        for(RDFNode mappingNode:mappingNodes){
            mappingList.add(mappingNode.toString());
        }
        return mappingList;
    }

    private String logMessage1(String accessMappingIri) {
    		StringBuilder message = new StringBuilder("[WARNING] Access mapping '");
    		message.append(accessMappingIri).append("' has no mappings");
    		return message.toString();
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
        String key = retrieveValuesOfMappingProperties(mappingIri, VicinityOntology.MAP_KEY, thingDescriptionModel);
        String property = retrieveValuesOfMappingProperties(mappingIri, VicinityOntology.MAP_PREDICATE, thingDescriptionModel);
        String jsonPath = retrieveValuesOfMappingProperties(mappingIri, VicinityOntology.MAP_JSONPATH, thingDescriptionModel);
        String transformedByThingDescriptionIri = retrieveValuesOfMappingProperties(mappingIri, VicinityOntology.MAP_VALUES_TRANSFORMED_BY, thingDescriptionModel); // point the thingDescription from the transformedBy mapping
    
        // Filter the received json if there is a jsonPath
        	JSONArray filteredJsons = retrieveFilteredJsonArray(jsonData, jsonPath);
        for (int index = 0; index < filteredJsons.length(); index++) {
        		if(filteredJsons.get(index) instanceof JSONArray) { // current json is an array, never been here
        			JSONArray arrayOfJsonsfilteredJsons = (JSONArray) filteredJsons.get(index);
        			for(int nestedIndex = 0; nestedIndex< arrayOfJsonsfilteredJsons.length(); nestedIndex++) {
        				JSONObject jsonDocument = (JSONObject) arrayOfJsonsfilteredJsons.get(nestedIndex);
        				Model virtualization = processJSONObject(jsonDocument, key, transformedByThingDescriptionIri, property, mappingIri, thingResource, thingDescriptionModel);
        				result.add(virtualization);
        			}
        			// maybe here I shoukld call the same funciton recursively for each contained document
        		}else {
        			if(filteredJsons.get(0) instanceof String) { // check if current json is actually a string value from an array
        				String literalValue = (String) filteredJsons.get(index);
        				RDFNode literal = ResourceFactory.createTypedLiteral(literalValue);
        				result.add(thingResource, ResourceFactory.createProperty(property), literal);
            		}else { // current json is basic json
            			JSONObject filteredJson = (JSONObject) filteredJsons.get(index);
            			Model virtualization = processJSONObject(filteredJson, key, transformedByThingDescriptionIri, property, mappingIri, thingResource, thingDescriptionModel);
            			result.add(virtualization);
            		}
        			
        		}
        		
        }

        return result;
    }

    
    private Model processJSONObject(JSONObject filteredJson, String key, String transformedByThingDescriptionIri, String property, String mappingIri, Resource thingResource, Model thingDescriptionModel) {
    		Model result = ModelFactory.createDefaultModel();
    		if(!key.startsWith("$."))
    			key = ("$.").concat(key);
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
            		String filteredJsonString = "";
            		if(filteredJson!=null)
            			filteredJsonString = filteredJson.toString();
            		String waning0 = logMessage("\n[WARNING] mapping key \"",key,"\" does not match the json document ",filteredJsonString);
                log.warning(waning0);
            }

        }else if(transformedByThingDescriptionIri.length()>0){
            // Recursive case: Object property
            String jsonPartition = filteredJson.toString(); // Retrieve the json that contains what should be transformed by
            if(key.length()>0) // if there is a key a partition of the json is retrieved, otherwhise the whole json will be transformed
                jsonPartition = String.valueOf(filteredJson.get(key));

            String blankIri =  NodeFactory.createBlankNode().toString();
            
            List<String> accessMappingIris = JenaUtils.fromRDFNodeToString(thingDescriptionModel.listObjectsOfProperty(ResourceFactory.createResource(transformedByThingDescriptionIri), VicinityOntology.MAP_TD_HAS_ACCESS_MAPPING).toList());
            if(accessMappingIris.isEmpty()){
            		String warning1 = logMessage("[WARNING] No AccessMapping were found when virtualizing RDF using description '",transformedByThingDescriptionIri,"'");
                log.warning(warning1);
            }else if(accessMappingIris.size()>1){
            		log.warning("[WARNING] More than one AccessMapping were found when virtualizing rdf");
            }else{
                String accessMappingIri = accessMappingIris.get(0);
                Model model = virtualizeRDF(thingDescriptionModel, blankIri, accessMappingIri, jsonPartition);
                result.add(thingResource, ResourceFactory.createProperty(property), ResourceFactory.createResource(blankIri));
                result.add(model);
            }

        }else{
        		String message1 = logMessage("[INFO] key received ",key," is empty");
        		String message2 = logMessage("[INFO] TransformedBy received ",transformedByThingDescriptionIri," is empty");
            log.info(message1);
            log.info(message2);
        }
        return result;
    }
    
    private String logMessage(String... args) {
    		StringBuilder builder = new StringBuilder();
    		int size = args.length;
    		for(int index=0; index < size; index++)
    			builder.append(args[index]);
    		return builder.toString();
    }
    
    
    // --------

    /**
     * This method returns the array of filtered JSONs as result of applying a json path to a JSON document
     * @param json A JSON Document
     * @param jsonPath A json path
     * @return An array of JSON Documents that fulfil the json path
     */
    private JSONArray retrieveFilteredJsonArray(String json, String jsonPath){
    
        JSONArray filteredJsons = new JSONArray();
        String stringJsons;
        try {
	        if(jsonPath.length()>0) {
	            stringJsons = applyJsonPath(json, jsonPath);
	        }else{
	            StringBuilder filteredStringJsons = new StringBuilder();
	            
	            if(!json.startsWith("[") && !json.endsWith("]")) {
	            		filteredStringJsons.append("[").append(json).append("]");
	            }else {
	            	filteredStringJsons.append(json);
	            }
	            stringJsons = filteredStringJsons.toString();
	            
	        }
	    		// Obtain the array
	        if(stringJsons!=null) {
	        	
	            filteredJsons = new JSONArray(stringJsons);
	        }else{
	        		String logMessage = logMessage("[ERROR] An error occurred with JSON Document: \n",json);
	            log.severe(logMessage);
	        }
        }catch(Exception e) {
        		log.severe(e.toString());
        		e.printStackTrace();
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
                for(Entry<String, String> entry : jsonMap.entrySet()) {
                    newJson.put(entry.getKey(), entry.getValue());
                }
                StringBuilder buffer = new StringBuilder();
                buffer.append("[").append(newJson.toString()).append("]");
                stringJson = buffer.toString();
            } else if (value instanceof List) {
                stringJson = value.toString();
            } else {
            		String logWarning = logMessage("[WARNING] Unknown type found when applying the JSON Path '",jsonPath,"' to the JSON Document: \n",json);
                log.warning(logWarning);
            }
        }catch (Exception e){
    		   String logWarning = logMessage("\n[WARNING] JSON Path \"",jsonPath+"\" not applicable to JSON Document: \n",json);
           log.warning(logWarning);
           log.severe(e.toString());
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
    private String retrieveValuesOfMappingProperties(String mappingIri, Property property, Model rdfData) {
        String value = null;

        Resource subject = ResourceFactory.createResource(mappingIri);
        List<RDFNode> valuesNodes = rdfData.listObjectsOfProperty(subject, property).toList();
        List<String> values = JenaUtils.fromRDFNodeToString(valuesNodes);

        if (values.size() > 1) {
        		String logMessage = logMessage("[ERROR] More than one property value retrieved in mapping ",mappingIri);
            log.severe(logMessage);
        } else if (values.isEmpty()){
            value ="";
            if(property.getURI().contains("rootMode"))
                value ="false";
        }else{
            value = values.get(0).trim();
            if(value.equals("None") && property.getURI().contains("key")) {
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
        Property typeProperty = VicinityOntology.RDF_TYPE;

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
        return types.contains(VicinityOntology.WOT_OBJECT_PROPERTY_MAPPING);
    }


}
