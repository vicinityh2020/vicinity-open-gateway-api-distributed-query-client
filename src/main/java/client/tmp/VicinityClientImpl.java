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
		String jsonTED = Unirest.post("http://vicinity-gateway-services.vicinity.linkeddata.es/advanced-discovery?neighbors=2331930e-b78c-499e-8236-cbb38c19d9a2,da494aa6-dc83-416c-86c9-8fb2f289274f,0253cebb-70b3-4f0b-9dff-bcbb4b1e2d4d,99a08321-2856-4b68-ad27-7289d97fffb7,5c390a78-e5df-415c-8de6-d76f9d3a67cb,a0e7e46f-0f56-4c93-abdf-8a00f96c717f,53a1fe3a-e03c-4c75-bf03-1406a7d19ef3,37e70df2-ec8e-4331-8745-3312e0d879d7,31606ec3-ddbd-40d9-ba13-9627495a419b,d2e5db62-2c12-4103-b6f0-3b92edbfe35d,016226ab-b24e-423f-971e-4d12dcf39aa3,be613d35-b98a-4c2c-b429-f97838be92e2,c8269833-578d-42c0-814c-1b24d644b350,4699bb54-e5bd-4845-ab9c-50eb112e2f3a,5d5040d5-45dc-4000-ac00-fd0928238d85,f4d8b6b9-7ef0-410f-9cde-e999f2aba5d3,46f472bf-5e56-495c-b909-f56efbe58413,6b374e02-429a-4634-a6b0-43b0bc05633d,5055aed3-a367-4c70-877d-096ffab75c6b").body(query).asString().getBody();
		System.out.println(">"+jsonTED);
		Set<String> neighbours = new HashSet<String>();
		neighbours.add("2331930e-b78c-499e-8236-cbb38c19d9a2");
		neighbours.add("da494aa6-dc83-416c-86c9-8fb2f289274f");
		neighbours.add("0253cebb-70b3-4f0b-9dff-bcbb4b1e2d4d");
		neighbours.add("99a08321-2856-4b68-ad27-7289d97fffb7");
		neighbours.add("5c390a78-e5df-415c-8de6-d76f9d3a67cb");
		neighbours.add("a0e7e46f-0f56-4c93-abdf-8a00f96c717f");
		neighbours.add("53a1fe3a-e03c-4c75-bf03-1406a7d19ef3");
		neighbours.add("37e70df2-ec8e-4331-8745-3312e0d879d7");
		neighbours.add("31606ec3-ddbd-40d9-ba13-9627495a419b");
		neighbours.add("d2e5db62-2c12-4103-b6f0-3b92edbfe35d");
		neighbours.add("016226ab-b24e-423f-971e-4d12dcf39aa3");
		neighbours.add("be613d35-b98a-4c2c-b429-f97838be92e2");
		neighbours.add("c8269833-578d-42c0-814c-1b24d644b350");
		neighbours.add("4699bb54-e5bd-4845-ab9c-50eb112e2f3a");
		neighbours.add("5d5040d5-45dc-4000-ac00-fd0928238d85");
		neighbours.add("f4d8b6b9-7ef0-410f-9cde-e999f2aba5d3");
		neighbours.add("46f472bf-5e56-495c-b909-f56efbe58413");
		neighbours.add("6b374e02-429a-4634-a6b0-43b0bc05633d");
		neighbours.add("5055aed3-a367-4c70-877d-096ffab75c6b");
		// -- Init the client
		neighbours.clear();
		VicinityClient client = new VicinityAgoraClient(jsonTED, neighbours, query);

		List<Entry<String,String>> remoteEndpoints = client.getRelevantGatewayAPIAddresses();
		int size = remoteEndpoints.size();
		for(int index=0; index < size; index++) {
			//String jsonDocument = "{\"data\":{\"echo\":\"get property\",\"pid\":\"prop-1\",\"oid\":\"test-agora-1\"},\"status\":\""+Math.random()+"\"}";
			System.out.println("\t>"+remoteEndpoints.get(index).getValue());
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
