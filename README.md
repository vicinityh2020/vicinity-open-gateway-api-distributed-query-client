# VICINITY Gateway API distributed query client

### About

Gateway API distributed query client implements the interoperability among Gateway APIs in VICINITY. For a given SPARQL query it allows to discover Gateway APIs that expose relevant data and generate a set of solutions. The communication to remote endpoints is handled by the Gateway API itself, and therefore, the security is ensured.


## Installation 

### Compiling code
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

### As Maven Dependency

Include the following statements in your pom.xml

```
<repositories>
       <repository>
              <id>jitpack.io</id>
              <url>https://jitpack.io</url>
       </repository>
</repositories>
<dependencies>
        <dependencies>
        <!-- Gateway API Services -->
        <dependency>
            <groupId>com.github.vicinityh2020</groupId>
            <artifactId>vicinity-open-gateway-api-distributed-query-client</artifactId>
             <version>1.0</version>
        </dependency>
</dependencies>

```

Refresh your project maven dependencies

## Configuration 

No further configuration is required

## Usage 

In order to use the Gateway API distributed query client several variables must be provided. Notice that the TED and the prefixes are retrieved from the Gateway API Services as described in their [documentation](https://github.com/vicinityh2020/vicinity-gateway-api-services).
```
// -- Data required for discovery relevant data and solve a SPARQL query
String query  = ...            // A SPARQL query
Set<String> neighbours = ...   // A set of neighbour oids
String jsonTED = ...  	  // A JSON-LD with a relevant TED for the query
```

Then a client object must be initialized using previous variables

```
// -- Init the client
VicinityClient client = new VicinityClient(jsonTED, neighbours, query);
```

Following we discover relevant Things in the TED taking the neighbours of the requester Gateway API into account. We suggest to parallelized the loop 'for' in the code below   to increase efficiency. In addition, to access the Gateway API Services a secured channel must be used; details to exchange data with the Gateway API Services is described in its [documentation](https://github.com/vicinityh2020/vicinity-gateway-api-services).

```
// -- Discovery
List<Entry<String,String>> remoteGatewayEndpoints = client.getRelevantGatewayAPIAddresses();
for(Entry<String,String> remoteGateway:remoteGatewayEndpoints) {
	String remoteGatewayLink = remoteGateway.getKey();
	String remoteGatewayExposedJSon = ... // retrieve the json exposed by the gateway by means of 'remoteGatewayLink'
	remoteGateway.setValue(jsonDocument);
}
```

Finally they query is solved using the JSON documents exposed by the different Gateway APIs. The query solution is provided as a list of maps each of which represents a solution for the given query. Notice that afterwards we close the client, this frees memory used increasing the efficiency.

```
// -- Solve query
List<Map<String,String>> queryResults = client.solveQuery(remoteEndpoints);
```

Pulling all together
```
// -- Data required for discovery relevant data and solve a SPARQL query
String query  = ...          // A SPARQL query
Set<String> neighbours = ... // A set of neighbour oids
    
// Retrieve from the Gateway API Services using a secured channel
String jsonTED = ...  	  // A JSON-LD with a relevant TED for the query

// -- Init the client
VicinityClient client = new VicinityClient(jsonTED, neighbours, query);

// -- Discovery & Distributed access thorugh secured channel
List<Entry<String,String>> remoteGatewayEndpoints = client.getRelevantGatewayAPIAddresses();
for(Entry<String,String> remoteGateway:remoteGatewayEndpoints) {
	String remoteGatewayLink = remoteGateway.getKey();
	String remoteGatewayExposedJSon = ... // retrieve the json exposed by the gateway by means of 'remoteGatewayLink'
	remoteGateway.setValue(jsonDocument);
}

// -- Solve query
List<Map<String,String>> queryResults = client.solveQuery(remoteEndpoints);

```