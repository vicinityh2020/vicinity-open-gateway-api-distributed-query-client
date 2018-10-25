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
				"select distinct ?interactionName ?property ?literal ?timestamp ?minValue ?maxValue where {\n" + 
				"       ?things a wot:Thing .\n" + 
				"       	?things wot:providesInteractionPattern ?pattern . \n"+
				"       	?pattern wot:interactionName ?interactionName . \n"+
				"       	?pattern core:hasValue ?value . \n"+
				"     	?pattern sosa:observes ?property \n"+				
				"       OPTIONAL { ?value core:literalValue ?literal . }\n" +
				"		OPTIONAL { ?value core:timeStamp ?timestamp  . } \n"+
				"		OPTIONAL { ?value core:hasMinValue ?minValue . } \n"+
				"		OPTIONAL { ?value core:hasMaxValue ?maxValue . } \n" +
				"		VALUES ?property { adp:AverageTemperature adp:AmbientTemperature } . \n"+
				"}" ;
			
		query  = "prefix wot: <http://iot.linkeddata.es/def/wot#>\n" + 
				"prefix core: <http://iot.linkeddata.es/def/core#>\n" + 
				"prefix foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"prefix sosa: <http://www.w3.org/ns/sosa/>\n" + 
				"prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" + 
				"prefix ssn: <http://www.w3.org/ns/ssn/>\n" + 
				"prefix adp: <http://iot.linkeddata.es/def/adapters#> \n" + 
				"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + 
				"prefix map: <http://iot.linkeddata.es/def/wot-mappings#> \n" + 
				"\n" + 
				"select distinct * where {\n" + 
				"       ?things rdf:type wot:Thing . \n" + 
			//	"       	?things core:represents ?object . \n"+
			//	"       ?object rdf:type ?type . \n"+
			//	"		FILTER regex(?type, \"adapters\") .\n"+
				"}";
		// Retrieve from the Gateway API Services using a secured channel (datails)
		Map<String, String> headers = new HashMap<String, String>();
        //headers.put("Content-Type", "application/ld+json"); .headers(headers)
		String jsonTED = Unirest.post("http://gateway-services.vicinity.linkeddata.es/discovery").headers(headers).body(query).asJson().getBody().toString();
		System.out.println(jsonTED);
		Set<String> neighbours = new HashSet<String>();
		neighbours.add("6f7b08b2-fd41-4037-870a-a530cf14b9ae");
		neighbours.add("54304c27-2cda-45c4-a905-0debaa2fc45a");
		neighbours.add("9704844e-0fc0-4a04-a480-201dc2a29386");
		neighbours.add("20648eb1-3711-4641-8db8-bc683d187320");
		neighbours.add("241748ae-3997-471c-8648-bd4f29013c30");
		neighbours.add("00467afa-60a7-4a46-81cc-711b7c273303");
		neighbours.add("e82ab8a4-39c9-4f68-9b73-89faed98d8c1");
		neighbours.add("824cdee0-c82a-4c87-a69a-358cb1bd0cbe");
		neighbours.add("f7dacf77-4149-4d11-9baf-23b2930d182b");
		neighbours.add("33f957f2-3978-4ce6-b03f-096e3ddccecf");
		neighbours.add("7ad7208e-1e33-472b-bf93-168f698de8bd");
		neighbours.add("ff7055bb-164a-47e9-8b86-d063d2c518f0");
		neighbours.add("802977c4-6439-4e63-81ba-e570251f4a49");
		neighbours.add("31df9622-423d-4dce-b2bb-223a619bcd1a");
		neighbours.add("9003cb7d-8787-4676-b20d-697280118334");
		neighbours.add("235f2259-eba1-477c-9aeb-48ff31ab58b9");
		neighbours.add("0899803c-03ce-4a9c-9e93-77b3dd9dad69");
		neighbours.add("67944241-1143-40ad-9e2d-b7be7fa29bd0");
		neighbours.add("d1a586d4-12d4-4460-ad56-a3e3a32bcfba");
		neighbours.add("87aa1347-5f39-4b1f-830f-0e51cd38bce8");
		neighbours.add("fff27ba7-f8b7-4a6c-adfb-0a2a82210c4a");
		neighbours.add("970f5849-0aad-48ad-bb03-1ae22aa7f668");
		neighbours.add("d34f961f-bacd-42aa-a3a7-a969cffc0dab");
		neighbours.add("815f7ff4-8ae8-46be-bb23-165c39adecf2");
		neighbours.add("bb6bd408-6c01-419d-a8ea-97b5e9d39863");
		neighbours.add("9c7cc993-d601-4e91-b949-d3b40aeb6e2f");
		neighbours.add("09e6c750-8293-4650-aeb5-ef306fbbc8ae");
		neighbours.add("e80c2b21-5f91-44ce-bfce-c07f9f48e04a");
		neighbours.add("73cfecd9-d94e-4163-8e6f-f817d7016788");
		neighbours.add("45259e34-df39-4483-b2f6-60bad2ea146b");
		neighbours.add("1781b81f-330d-427c-9374-548f60a4bbec");
		neighbours.add("dXBtLXd-lYXRoZX-Itc2VydmljZQ");
		// -- Init the client
		VicinityClient client = new VicinityAgoraClient(jsonTED, neighbours, query);

		List<Entry<String,String>> remoteEndpoints = client.getRelevantGatewayAPIAddresses();
		int size = remoteEndpoints.size();
		for(int index=0; index < size; index++) {
			String jsonDocument = "{\"data\":{\"echo\":\"get property\",\"pid\":\"prop-1\",\"oid\":\"test-agora-1\"},\"status\":\""+Math.random()+"\"}";
			System.out.println("\t>"+remoteEndpoints.get(index).getValue());
			remoteEndpoints.get(index).setValue(jsonDocument);
		}
		
		// -- Solve query
		List<Map<String,String>> queryResults = client.solveQuery(remoteEndpoints);
		for(Map<String,String> result:queryResults)
			System.out.println(result);
	
	}
	
	public static String get(String iri) {
		System.out.println(iri);
		return "{}";
	}
}
