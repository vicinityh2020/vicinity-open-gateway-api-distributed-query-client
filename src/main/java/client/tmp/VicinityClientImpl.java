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
				"SELECT DISTINCT ?interactionName ?observed  WHERE {\n" +  //
				"       <http://vicinity.eu/data/things/c0d66ac6-b952-458f-8bb6-0ceea539b082> rdf:type wot:Thing . \n" +
				"		<http://vicinity.eu/data/things/c0d66ac6-b952-458f-8bb6-0ceea539b082> core:represents ?object . \n" +
				//"       ?object rdf:type ?type .\n" + 
				"       	<http://vicinity.eu/data/things/c0d66ac6-b952-458f-8bb6-0ceea539b082> wot:providesInteractionPattern ?patterns . \n"+
				"       	?patterns wot:interactionName ?interactionName . \n"+
				"       	?patterns sosa:observes ?observed . \n"+
				//"       ?object wot:thingName ?name . \n"+
				//"		FILTER regex(str(?type), \"adapters\") .\n"+
				"}";
		
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
		Map<String, String> headers = new HashMap<String, String>();
        //headers.put("Content-Type", "application/ld+json"); .headers(headers) /vicinity- advanced-discovery
		String jsonTED = Unirest.post("http://vicinity-gateway-services.vicinity.linkeddata.es/advanced-discovery?neighbors=2331930e-b78c-499e-8236-cbb38c19d9a2,da494aa6-dc83-416c-86c9-8fb2f289274f,0253cebb-70b3-4f0b-9dff-bcbb4b1e2d4d,99a08321-2856-4b68-ad27-7289d97fffb7,5c390a78-e5df-415c-8de6-d76f9d3a67cb,a0e7e46f-0f56-4c93-abdf-8a00f96c717f,53a1fe3a-e03c-4c75-bf03-1406a7d19ef3,37e70df2-ec8e-4331-8745-3312e0d879d7,31606ec3-ddbd-40d9-ba13-9627495a419b,d2e5db62-2c12-4103-b6f0-3b92edbfe35d,016226ab-b24e-423f-971e-4d12dcf39aa3,be613d35-b98a-4c2c-b429-f97838be92e2,c8269833-578d-42c0-814c-1b24d644b350,4699bb54-e5bd-4845-ab9c-50eb112e2f3a,5d5040d5-45dc-4000-ac00-fd0928238d85,f4d8b6b9-7ef0-410f-9cde-e999f2aba5d3,46f472bf-5e56-495c-b909-f56efbe58413,6b374e02-429a-4634-a6b0-43b0bc05633d,5055aed3-a367-4c70-877d-096ffab75c6b").headers(headers).body(query).asString().getBody();
		System.out.println(">"+jsonTED);
		Set<String> neighbours = new HashSet<String>();
		// production CERTH
		/*neighbours.add("c8269833-578d-42c0-814c-1b24d644b350");
		neighbours.add("6967e210-e1e8-4a1d-9fd8-4a40cd8df41b");
		neighbours.add("3d3bcd90-27cc-4f07-91d6-f76d36c789c1");
		// Dev
		neighbours.add("0d61ad2b-5431-46d8-a523-6aa68f13158f");
		neighbours.add("848451c1-8705-4f20-9af8-bfbbd701e0a8");
		neighbours.add("bbfc27ad-6aef-41c9-8752-5acbc8b8a7d1");
		neighbours.add("13e770d0-e0ec-47d6-96e6-665cb1c71b20");
		neighbours.add("9c7cc993-d601-4e91-b949-d3b40aeb6e2f");
		neighbours.add("13dff4a0-e269-4e68-9e2c-7a77d2732e70");
		neighbours.add("690a5ca1-8cb8-434b-946c-7b64ad66df14");
		neighbours.add("45259e34-df39-4483-b2f6-60bad2ea146b");
		neighbours.add("9a122d08-52a1-4e27-a781-1b0a87dc1e62");
		neighbours.add("4370ecb6-dbf5-48a8-a0f6-bb3f94f1d541");
		neighbours.add("970f5849-0aad-48ad-bb03-1ae22aa7f668");
		neighbours.add("34730b62-097a-4bde-a298-e0bcbcab2bd1");
		neighbours.add("6f7b08b2-fd41-4037-870a-a530cf14b9ae");
		neighbours.add("4f75dd67-9bc1-41db-be2c-7e1829dd4d48");
		neighbours.add("636c2fde-50d4-4fb2-9fa0-d758591d19b0");
		neighbours.add("0d61ad2b-5431-46d8-a523-6aa68f13158f");
		neighbours.add("848451c1-8705-4f20-9af8-bfbbd701e0a8");
		neighbours.add("bbfc27ad-6aef-41c9-8752-5acbc8b8a7d1");
		neighbours.add("13e770d0-e0ec-47d6-96e6-665cb1c71b20");
		neighbours.add("9c7cc993-d601-4e91-b949-d3b40aeb6e2f");
		neighbours.add("13dff4a0-e269-4e68-9e2c-7a77d2732e70");
		neighbours.add("690a5ca1-8cb8-434b-946c-7b64ad66df14");
		neighbours.add("45259e34-df39-4483-b2f6-60bad2ea146b");
		neighbours.add("9a122d08-52a1-4e27-a781-1b0a87dc1e62");
		neighbours.add("4370ecb6-dbf5-48a8-a0f6-bb3f94f1d541");
		neighbours.add("970f5849-0aad-48ad-bb03-1ae22aa7f668");
		neighbours.add("34730b62-097a-4bde-a298-e0bcbcab2bd1");
		neighbours.add("6f7b08b2-fd41-4037-870a-a530cf14b9ae");
		neighbours.add("4f75dd67-9bc1-41db-be2c-7e1829dd4d48");
		neighbours.add("636c2fde-50d4-4fb2-9fa0-d758591d19b0");*/
		// Our vas
		neighbours.add("dXBtLXd-lYXRoZX-Itc2VydmljZQ");
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
