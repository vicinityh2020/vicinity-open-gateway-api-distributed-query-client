package client.jena;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphExtract;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.TripleBoundary;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.json.JSONObject;

import client.VicinityOntology;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * <P>Static class that provides useful functions for using Jena Models</P>
 * @author Andrea Cimmino
 * @version 0.6.3
 */
public class JenaUtils {

	// -- Attributes
	
	private static Logger log = Logger.getLogger(JenaUtils.class.getName());
	
	// -- Constructor
	
	/**
	 * Private constructor prevents Java to add an implicit public one for this utility class which is not meant to be instantiated
	 */
	private JenaUtils() {
		// empty
	}
	
	// -- Methods
	
    /**
     * This method transforms a list of Jena {@link RFDNodes} into a list with their Strings values, i.e., URIs or literals
     * @param nodes A list of {@link RFDNodes}
     * @return A list containing the String values of each input RDFNode, i.e., URIs or literals
     */
    public static List<String> fromRDFNodeToString(List<RDFNode> nodes){
    		int nodeNumbers = nodes.size();
        List<String> iris = new ArrayList<String>(nodeNumbers);
        for(int index=0; index < nodeNumbers; index++){
        		RDFNode node = nodes.get(index);
            if(node.isLiteral())
                iris.add(node.asLiteral().getString());
            if(node.isResource())
                iris.add(node.asResource().getURI());
        }
        return iris;
    }

   
    /**
     * This method translates a JSON-LD document into a jena RDF Model
     * @param json A JSON-LD document
     * @return A jena RDF Model containing the JSON-LD data
     */
    public static Model fromJsonLDtoRDFModel(String json){
        Model rdfModel = ModelFactory.createDefaultModel();
        try {
            log.info("\n[INFO] Generating RDF ");
            InputStream tedStream = new ByteArrayInputStream(json.getBytes());
            rdfModel.read(tedStream, null, Lang.JSONLD.getName());
        }catch(Exception e){
            log.info("\n[ERROR] Unable to generate RDF from JSON: \n"+json+"\n");
        }

        return rdfModel;
    }

    /**
     * This method transforms a JSON document into a Map that contains the same key/values of the JSON. It is assumed that the json does not contain nested objects, only plain values
     * @param json A Json document that only contains plain values
     * @return A Map containing the key-values of the input JSON document
     */
    public static Map<String,String> fromJsonToMap(String json){
        Map<String,String> map = new HashMap<String, String>();
        JSONObject object = new JSONObject(json);
        for(Object keyObject:object.keySet()){
            String key = (String) keyObject;
            String value = object.getString(key);
            String logMessage = buildFromJsonToMapLog(key, value);
            log.info(logMessage);
            map.put(key,value);
        }
        return map;
    }
    
    private static String buildFromJsonToMapLog(String key, String value) {
    		StringBuilder logMessage = new StringBuilder("\n[INFO] Prefix loaded ");
    		logMessage.append(key).append(":").append(value);
    		return logMessage.toString();
    }
    
    // -----
    
    
    /**
     * This method transforms a String variable with RDF content into a jena {@link Model}
     * @param strRDF A String variable containing RDF in "JSON-LD" format
     * @return a jena {@link Model}
     */
    public static Model parseRDF(String strRDF, String format) {
    		Model parsedModel = ModelFactory.createDefaultModel();
    		try {
			 InputStream is = new ByteArrayInputStream( strRDF.getBytes() );
			 parsedModel.read(is, null, format);
    		}catch(Exception e) {
    			String message = ("Something went wrong parsing RDF\n").concat(e.toString());
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
    public static Model extractFullSubTree(Node resourceNode, Model ted) {
    		GraphExtract extractor = new GraphExtract(TripleBoundary.stopNowhere);
        Graph extracted = extractor.extract(resourceNode, ted.getGraph());
        return ModelFactory.createModelForGraph(extracted);
    }
    
    /**
     * This method transforms the RDF within a {@link Model} into a String variable
     * @param model a jena {@link Model} 
     * @return a String variable with the same RDF of the input {@link Model} in "TURTLE" format
     */
    public static String toString(Model model) {
		Writer output = new StringWriter();
		model.write(output, VicinityOntology.TURTULE);
		return output.toString();
	}

}
