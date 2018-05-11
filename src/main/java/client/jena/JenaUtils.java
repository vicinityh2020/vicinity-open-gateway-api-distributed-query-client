package client.jena;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <P>Static class that aims at providing support for using Jena functions</P>
 * @author Andrea Cimmino
 * @version 1.0
 */
public class JenaUtils {

    /**
     * This method transforms a list of jena RFDNodes into a list of Strings containing the IRI of each RDFNode
     * @param nodes A list of RDFNodes
     * @return A list containing the string value of each input RDFNode
     */
    public static List<String> fromRDFNodeToString(List<RDFNode> nodes){
        List<String> iris = new ArrayList<String>();
        for(RDFNode node:nodes){
            if(node.isLiteral())
                iris.add(node.asLiteral().getString());
            if(node.isResource())
                iris.add(node.asResource().getURI());
        }
        return iris;
    }

    /**
     * This method returns the list of resource IRIs contained in the input RDF jena Model that are leafs in the RDF graph
     * @param rdfModel A jena Model, i.e., a RDF graph
     * @return A list of resource IRIs contained in the input Model that are leafs in the RDF graph
     */
    public static List<String> retrieveLeafsFromModel(Model rdfModel){
        List<String> leafs= new ArrayList<String>();
        List<Statement> statements = rdfModel.listStatements(null,null, (RDFNode) null).toList();
        for(Statement statement:statements){
            RDFNode objectIri = statement.getObject();
            if(objectIri.isResource()) {
                Boolean objectIriHasData = rdfModel.listStatements(objectIri.asResource(), null, (RDFNode) null).toList().size() != 0;
                if(!objectIriHasData)
                    leafs.add(objectIri.toString());
            }
        }
        return leafs;
    }

    /**
     * This method translates a JSON-LD document into a jena RDF Model
     * @param json A JSON-LD document
     * @param log A cached log that will be filled with the execution information
     * @return A jena RDF Model containing the JSON-LD data
     */
    public static Model fromJSONtoRDFModel(String json, StringBuilder log){
        Model rdfModel = ModelFactory.createDefaultModel();
        try {
            log.append("\n[INFO] Generating RDF ");
            InputStream tedStream = new ByteArrayInputStream(json.getBytes());
            rdfModel.read(tedStream, null, Lang.JSONLD.getName());
        }catch(Exception e){
            log.append("\n[ERROR] Unable to generate RDF from JSON: \n").append(json).append("\n");
        }

        return rdfModel;
    }

    /**
     * This method transforms a JSON document into a Map that contains the same key/values of the JSON. It is assumed that the json does not contain nested objects, only plain values
     * @param json A Json document that only contains plain values
     * @param log A cached log that will be filled with the execution information
     * @return A Map containing the key-values of the input JSON document
     */
    public static Map<String,String> fromJsonToMap(String json, StringBuilder log){
        Map<String,String> map = new HashMap<String, String>();
        JSONObject object = new JSONObject(json);
        for(Object keyObject:object.keySet()){
            String key = (String) keyObject;
            String value = object.getString(key);
            log.append("\n[INFO] Prefix loaded ").append(key).append(":").append(value);
            map.put(key,value);
        }
        return map;
    }

}
