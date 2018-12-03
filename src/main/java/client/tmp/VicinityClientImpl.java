package client.tmp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import client.VicinityAgoraClient;
import client.VicinityClient;
import client.model.Triple;

public class VicinityClientImpl {

	public static void main(String[] args) throws UnirestException {
		Unirest.setTimeouts(0, 0);
		// -- Data required for discovery relevant data and solve a SPARQL query
		String query  = "prefix wot: <http://iot.linkeddata.es/def/wot#>\n" + 
				"prefix core: <http://iot.linkeddata.es/def/core#>\n" + 
				"prefix foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"prefix sosa: <http://www.w3.org/ns/sosa/>\n" + 
				"prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" + 
				"prefix ssn: <http://www.w3.org/ns/ssn/>\n" + 
				"prefix adp: <http://iot.linkeddata.es/def/adapters#> \n" + 
				"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + 
				"prefix map: <http://iot.linkeddata.es/def/wot-mappings#> \n" + 
				"\n" + 
				"select distinct ?thing ?clazz ?value where {\n" + 
				"       ?thing rdf:type wot:Thing .\n" + 
				"       ?thing core:represents ?object .\n" + 
				"       ?object rdf:type ?clazz .\n" + 
				"       ?thing wot:providesInteractionPattern ?pattern .\n" + 
				"       ?pattern core:hasValue ?valueResource .\n" + 
				//"       optional { ?valueResource core:literalValue ?value .} \n" + 
				"} LIMIT 5";
	
		
		long startTime = System.currentTimeMillis();
		// Retrieve from the Gateway API Services using a secured channel (datails)
		String jsonTED = Unirest.post("http://localhost:8081/advanced-discovery?neighbors=46f472bf-5e56-495c-b909-f56efbe58413").body(query).asString().getBody();
		System.out.println(">"+jsonTED);
		Set<String> neighbours = new HashSet<String>();
		neighbours.add("46f472bf-5e56-495c-b909-f56efbe58413");
		
		VicinityClient client = new VicinityAgoraClient(jsonTED, neighbours, query);

		List<Entry<String,String>> remoteEndpoints = client.getRelevantGatewayAPIAddresses();
		System.out.println(remoteEndpoints);
		int size = remoteEndpoints.size();
		for(int index=0; index < size; index++) {
			//String jsonDocument = "{\"data\":{\"echo\":\"get property\",\"pid\":\"prop-1\",\"oid\":\"test-agora-1\"},\"status\":\""+Math.random()+"\"}";
			System.out.println("\t>"+remoteEndpoints.get(index).getValue());
			remoteEndpoints.get(index).setValue("{\"detected\":true}");
			//remoteEndpoints.get(index).setValue(jsonDocument);
		}
		
		// -- Solve query
		List<Map<String,String>> queryResults = client.solveQuery(remoteEndpoints);
		for(Map<String,String> result:queryResults)
			System.out.println(result);
	
		 long stopTime = System.currentTimeMillis();
	     long elapsedTime = stopTime - startTime;
	     System.out.println("->"+elapsedTime/60000);
	}
	

}
