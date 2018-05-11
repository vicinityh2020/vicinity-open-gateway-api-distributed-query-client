# VICINITY Gateway API distributed query client

### About

Gateway API distributed query client implements the interoperability among Gateway APIs in VICINITY. For a given SPARQL query it allows to discover Gateway APIs that expose relevant data and generate a set of solutions. The communication to remote endpoints is handled by the Gateway API itself, and therefore, the security is ensured.


## Installation 

Download the source code from the repository:

```
#!shell
git clone https://github.com/vicinityh2020/vicinity-open-gateway-api-distributed-query-client.git
```
Then compile the source and build a jar file
```
#!shell
sudo mvn clean package
```
Finally import the jar file that will be generated in the target folder to your project

## Configuration 

No further configuration is required

## Usage 

In order to use the Gateway API distributed query client several variables must be provided. Notice that the TED and the prefixes are retrieved from the Gateway API Services as described in their [documentation](http://vicinity.bavenir.eu).
```
// -- Data required for discovery relevant data and solve a SPARQL query
String query  = ...            // A SPARQL query
Set<String> neighbours = ...   // A set of neighbour oids
StringBuilder log = new StringBuilder(); // An empty in-memory log
    
// Retrieve from the Gateway API Services using a secured channel (datails)
String jsonTED = ...  	  // A JSON-LD with a relevant TED for the query
String jsonPrefixes = ... // A JSON document containing VICINITY ontology prefixes    
```

Then a client object must be initialized using previous variables

```
// -- Init the client
VicinityClient client = new VicinityClient(jsonTED, neighbours, jsonPrefixes);
```

Following we discover relevant Things in the TED taking the neighbours of the requester Gateway API into account. We suggest to parallelized the loop 'for' in the code below   to increase efficiency. In addition, to access the Gateway API Services a secured channel must be used; details to exchange data with the Gateway API Services is described in its [documentation](http://vicinity.bavenir.eu).

```
// -- Discovery
while(client.existIterativelyDiscoverableThings()){
   // Discover relevant resources in the TED
   List<String> neighboursThingsIRIs = client.discoverRelevantThingIRI();
   // Retrieve remote JSON data for each Thing IRI
   for(String neighboursThingIRI:neighboursThingsIRIs){
       String thingsJsonRDF = ... // Retrieve the JSON-LD exposed by the GATEWAY API SERVICES for this IRI Thing 
       client.updateDiscovery(thingsJsonRDF);
   }
}
List<Triple<String,String,String>> relevantGatewayAPIAddresses = client.getRelevantGatewayAPIAddresses();
```

Then relaying on a secure channel we access each Gateway API address to retrieve the data that they expose. To increase execution efficiency we recomend to parallelize the loop 'for' in the code below. 
```
// -- Distributed access thorugh secured channel
for(Triple<String,String,String> neighbourGatewayAPIAddress:relevantGatewayAPIAddresses){ 
   String gatewayApiAddress =  neighbourGatewayAPIAddress.getThirdElement();
   String jsonData = ... // Retrieve the JSON document exposed by URL in gatewayApiAddress
   neighbourGatewayAPIAddress.setThirdElement(jsonData);
}    
```

Finally they query is solved using the JSON documents exposed by the different Gateway APIs. The query solution is provided as a list of maps each of which represents a solution for the given query. Notice that afterwards we close the client, this frees memory used increasing the efficiency.

```
// -- Solve query
List<Map<String,String>> queryResults = client.solveQuery(query, relevantGatewayAPIAddresses);
client.close();
```

Pulling all together
```
// -- Data required for discovery relevant data and solve a SPARQL query
String query  = ... 		  			 // A SPARQL query
Set<String> neighbours = ...  			 // A set of neighbour oids
StringBuilder log = new StringBuilder(); // An empty in-memory log
    
// Retrieve from the Gateway API Services using a secured channel (datails)
String jsonTED = ...  	  // A JSON-LD with a relevant TED for the query
String jsonPrefixes = ... // A JSON document containing VICINITY ontology prefixes

// -- Init the client
VicinityClient client = new VicinityClient(jsonTED, neighbours, jsonPrefixes);

// -- Discovery
while(client.existIterativelyDiscoverableThings()){
  // Discover relevant resources in the TED
  List<String> neighboursThingsIRIs = client.discoverRelevantThingIRI();
  // Retrieve remote JSON data for each Thing IRI
  for(String neighboursThingIRI:neighboursThingsIRIs){
    String thingsJsonRDF = ... // Retrieve the JSON-LD exposed by the GATEWAY API SERVICES for this IRI Thing 
    client.updateDiscovery(thingsJsonRDF);
  }
}
List<Triple<String,String,String>> relevantGatewayAPIAddresses = client.getRelevantGatewayAPIAddresses();

// -- Distributed access thorugh secured channel
for(Triple<String,String,String> neighbourGatewayAPIAddress:relevantGatewayAPIAddresses){ 
   String gatewayApiAddress =  neighbourGatewayAPIAddress.getThirdElement();
   String jsonData = ... // Retrieve the JSON document exposed by URL in gatewayApiAddress
   neighbourGatewayAPIAddress.setThirdElement(jsonData);
}

// -- Solve query
List<Map<String,String>> queryResults = client.solveQuery(query, relevantGatewayAPIAddresses);
client.close();

```