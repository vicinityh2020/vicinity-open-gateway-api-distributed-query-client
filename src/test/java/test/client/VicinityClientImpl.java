package test.client;

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
				"select distinct ?sensor ?sensorName ?literal where {\n" + 
				"       ?things a wot:Thing .\n" + 
				"       ?things core:represents ?sensors ."+
				"		?sensors a adp:ActivityTracker ." +
				"		?sensors wot:thingName ?sensorName ." + 
				"       ?things wot:providesInteractionPattern ?pattern ."+
				"       ?pattern core:hasValue ?value ."+
				"       ?value core:literalValue ?literal ."+
		
				"}";          
		
		    
		// Retrieve from the Gateway API Services using a secured channel (datails)
		Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/ld+json");
		String jsonTED = Unirest.post("http://gateway-services.vicinity.linkeddata.es/discovery").headers(headers).body(query).asJson().getBody().toString();
		
		System.out.println(jsonTED);
		Set<String> neighbours = new HashSet<String>();
		neighbours.add("http://vicinity.eu/data/things/03bda50b-7d69-4279-837e-ef344481d391");	
		neighbours.add("http://vicinity.eu/data/things/2343f701-63ee-45a1-92b9-fcb1e1f1df48");	
		neighbours.add("http://vicinity.eu/data/things/7314be25-04af-4a31-ab1e-12f570ac3489");	
		neighbours.add("http://vicinity.eu/data/things/9da48b25-78a1-4ada-a283-5b54680a91b2");	
		neighbours.add("http://vicinity.eu/data/things/9e452da2-0371-44e0-aa60-8325eedc2533");	
		neighbours.add("http://vicinity.eu/data/things/a5643934-16c8-4a91-a015-380ef4f52a38");	
		neighbours.add("http://vicinity.eu/data/things/b4538b89-2cd1-4614-bcd0-d16d93c513e6");	
		neighbours.add("http://vicinity.eu/data/things/b45594fa-863e-4ab4-a987-b6a4455c48b3");	
		neighbours.add("http://vicinity.eu/data/things/df8e3ffa-e2f6-4ecc-81f2-c0449f09d9d4");	
		neighbours.add("http://vicinity.eu/data/things/f02ba0f1-7fa1-4747-8e75-5dac1da595f8");	
		neighbours.add("http://vicinity.eu/data/things/f277a99c-b8cd-4eff-a7fc-9a19c0a663a6");
		
		// -- Init the client
		VicinityClient client = new VicinityAgoraClient(jsonTED, neighbours, query);

		List<Entry<String,String>> remoteEndpoints = client.getRelevantGatewayAPIAddresses();
		int size = remoteEndpoints.size();
		for(int index=0; index < size; index++) {
			String jsonDocument = "{ \"test-agora-1-data\" : \"data agora 1\", \"test-agora-2-data\" : \"data agora 2\", \"test-agora-3-data\" : \"data agora 3\", \"test-agora-4-data\" : \"data agora 4\", \"test-agora-5-data\" : \"data agora 5\" }";
			remoteEndpoints.get(index).setValue(jsonDocument);
		}
		
		// -- Solve query
		List<Map<String,String>> queryResults = client.solveQuery(remoteEndpoints);
		System.out.println(queryResults);
	
	}
	
	public static String get(String iri) {
		System.out.println(iri);
		return "{}";
	}
}
