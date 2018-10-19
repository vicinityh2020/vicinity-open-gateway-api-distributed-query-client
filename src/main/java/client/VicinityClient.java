package client;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface VicinityClient {

	/**
	 * This method finds the gatewayAPIAddresses that expose relevant real-time data to answer the given query
	 * @return A Collection of tuples containing as first element the Mapping to translate from to RDF the JSON exposed by a Gateway, which address is the second element of the tuple
	 */
	List<Entry<String, String>> getRelevantGatewayAPIAddresses();

	List<Map<String, String>> solveQuery(List<Entry<String, String>> gatewaysData);

}