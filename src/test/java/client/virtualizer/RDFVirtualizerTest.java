package client.virtualizer;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Assert;
import org.junit.Test;

import client.VicinityOntology;
import client.jena.JenaUtils;

public class RDFVirtualizerTest {
	
	// Requires that all the rest of the test are passed, except the VicinityAgoraClient
	/*
	 	Tests:
	 		- Test1: Plain json virtualization
	 		- Test2: virtualization of json with jsonpath restrictions on mappings
	 */
	
	
	// Test1: Plain json virtualization
	
	private Model accessMappingPlainJson = JenaUtils.parseRDF("<http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjaXRvbl9wYXR0ZXJuLzEvYWNjZXNzTWFwcGluLzE=> a <http://iot.linkeddata.es/def/wot-mappings#AccessMapping>;\n" + 
			"	<http://iot.linkeddata.es/def/wot-mappings#hasMapping> <http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjaXRvbl9wYXR0ZXJuLzEvbWFwcGluZy8x>, <http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjaXRvbl9wYXR0ZXJuLzEvbWFwcGluZy8y>;\n" + 
			"	<http://iot.linkeddata.es/def/wot-mappings#mapsResourcesFrom> <http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjaXRvbl9wYXR0ZXJuLzEvbGluay8x> .\n" + 
			"  		\n" + 
			"<http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjaXRvbl9wYXR0ZXJuLzEvbWFwcGluZy8x> a <http://iot.linkeddata.es/def/wot-mappings#Mapping>;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#key> \"chill\" ;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#predicate> <http://iot.linkeddata.es/def/core#literalValue> .\n" + 
			"\n" + 
			"<http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjaXRvbl9wYXR0ZXJuLzEvbWFwcGluZy8y> a <http://iot.linkeddata.es/def/wot-mappings#Mapping>;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#key> \"lastBuildDate\" ;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#predicate> <http://iot.linkeddata.es/def/core#timeStamp> .\n" + 
			"\n" + 
			"\n" + 
			"<http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjaXRvbl9wYXR0ZXJuLzEvbGluay8x> a <http://iot.linkeddata.es/def/wot-mappings#Link>;\n" + 
			"  		<http://iot.linkeddata.es/def/wot#hasMediaType> \"application/json\";\n" + 
			"  		<http://iot.linkeddata.es/def/wot#href> \"https://fake-link.com\" .\n", VicinityOntology.TURTULE) ;
	
	@Test
	public void test1() {
		String thingIri = "http://vicinity-test.com";
		String jsonData = "{\"chill\":65, \"lastBuildDate\":\"23:09 pm CEST\"}";
		RDFVirtualizer virtualizer = new RDFVirtualizer();
		Model virtualRDF = virtualizer.virtualizeRDF(accessMappingPlainJson, thingIri, "http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjaXRvbl9wYXR0ZXJuLzEvYWNjZXNzTWFwcGluLzE=", jsonData);
		Boolean testCondition = virtualRDF.contains(ResourceFactory.createResource("http://vicinity-test.com"), ResourceFactory.createProperty("http://iot.linkeddata.es/def/core#literalValue"), "65");
		testCondition &= virtualRDF.contains(ResourceFactory.createResource("http://vicinity-test.com"), ResourceFactory.createProperty("http://iot.linkeddata.es/def/core#timeStamp"), "23:09 pm CEST");
		Assert.assertTrue(testCondition);
	}

	// Test2: virtualization of json with jsonpath restrictions on mappings
	private Model accessMappingJsonPaths = JenaUtils.parseRDF("<http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9hY2Nlc3NNYXBwaW5n> a <http://iot.linkeddata.es/def/wot-mappings#AccessMapping>;\n" + 
			"	<http://iot.linkeddata.es/def/wot-mappings#hasMapping> <http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9tYXBwaW5nLzE=>, <http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9tYXBwaW5nLzI=>, <http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9tYXBwaW5nLzM=>, <http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9tYXBwaW5nLzQ=> ;\n" + 
			"	<http://iot.linkeddata.es/def/wot-mappings#mapsResourcesFrom> <http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9tYXBwaW5nLzMvTGluaw==> .\n" + 
			"  		\n" + 
			"<http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9tYXBwaW5nLzE=> a <http://iot.linkeddata.es/def/wot-mappings#Mapping>;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#jsonPath> \"$.query.results.channel.item.forecast.[1]\" ;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#key> \"text\" ;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#predicate> <http://iot.linkeddata.es/def/core#literalValue> .\n" + 
			"\n" + 
			"<http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9tYXBwaW5nLzI=> a <http://iot.linkeddata.es/def/wot-mappings#Mapping>;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#jsonPath> \"$.query.results.channel.item.forecast.[1]\" ;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#key> \"date\" ;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#predicate> <http://iot.linkeddata.es/def/core#timeStamp> .\n" + 
			"\n" + 
			"<http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9tYXBwaW5nLzM=> a <http://iot.linkeddata.es/def/wot-mappings#Mapping>;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#jsonPath> \"$.query.results.channel.item.forecast.[1]\" ;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#key> \"high\" ;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#predicate> <http://iot.linkeddata.es/def/core#hasMaxValue> .\n" + 
			"\n" + 
			"<http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9tYXBwaW5nLzQ=> a <http://iot.linkeddata.es/def/wot-mappings#Mapping>;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#jsonPath> \"$.query.results.channel.item.forecast.[1]\" ;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#key> \"low\" ;\n" + 
			"  		<http://iot.linkeddata.es/def/wot-mappings#predicate> <http://iot.linkeddata.es/def/core#hasMinValue> .\n" + 
			"\n" + 
			"<http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9tYXBwaW5nLzMvTGluaw==> a <http://iot.linkeddata.es/def/wot-mappings#Link>;\n" + 
			"  		<http://iot.linkeddata.es/def/wot#hasMediaType> \"application/json\";\n" + 
			"  		<http://iot.linkeddata.es/def/wot#href> \"https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22thessaloniki%2C%20gk%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys\" .", VicinityOntology.TURTULE) ;
	@Test
	public void test2() {
		String thingIri = "http://vicinity-test.com";
		String jsonData = "{\"query\":{\"count\":1,\"created\":\"2018-10-25T14:37:58Z\",\"lang\":\"es-ES\",\"results\":{\"channel\":{\"units\":{\"distance\":\"mi\",\"pressure\":\"in\",\"speed\":\"mph\",\"temperature\":\"F\"},\"title\":\"Yahoo! Weather - Thessaloniki, Macedonia and Thrace, GR\",\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-963291/\",\"description\":\"Yahoo! Weather for Thessaloniki, Macedonia and Thrace, GR\",\"language\":\"en-us\",\"lastBuildDate\":\"Thu, 25 Oct 2018 05:37 PM EEST\",\"ttl\":\"60\",\"location\":{\"city\":\"Thessaloniki\",\"country\":\"Greece\",\"region\":\" Macedonia and Thrace\"},\"wind\":{\"chill\":\"57\",\"direction\":\"335\",\"speed\":\"23\"},\"atmosphere\":{\"humidity\":\"24\",\"pressure\":\"998.0\",\"rising\":\"0\",\"visibility\":\"16.1\"},\"astronomy\":{\"sunrise\":\"7:51 am\",\"sunset\":\"6:33 pm\"},\"image\":{\"title\":\"Yahoo! Weather\",\"width\":\"142\",\"height\":\"18\",\"link\":\"http://weather.yahoo.com\",\"url\":\"http://l.yimg.com/a/i/brand/purplelogo//uh/us/news-wea.gif\"},\"item\":{\"title\":\"Conditions for Thessaloniki, Macedonia and Thrace, GR at 05:00 PM EEST\",\"lat\":\"40.623268\",\"long\":\"22.94973\",\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-963291/\",\"pubDate\":\"Thu, 25 Oct 2018 05:00 PM EEST\",\"condition\":{\"code\":\"32\",\"date\":\"Thu, 25 Oct 2018 05:00 PM EEST\",\"temp\":\"60\",\"text\":\"Sunny\"},\"forecast\":[{\"code\":\"32\",\"date\":\"25 Oct 2018\",\"day\":\"Thu\",\"high\":\"62\",\"low\":\"50\",\"text\":\"Sunny\"},{\"code\":\"32\",\"date\":\"26 Oct 2018\",\"day\":\"Fri\",\"high\":\"68\",\"low\":\"49\",\"text\":\"Sunny\"},{\"code\":\"34\",\"date\":\"27 Oct 2018\",\"day\":\"Sat\",\"high\":\"71\",\"low\":\"54\",\"text\":\"Mostly Sunny\"},{\"code\":\"30\",\"date\":\"28 Oct 2018\",\"day\":\"Sun\",\"high\":\"71\",\"low\":\"54\",\"text\":\"Partly Cloudy\"},{\"code\":\"28\",\"date\":\"29 Oct 2018\",\"day\":\"Mon\",\"high\":\"72\",\"low\":\"58\",\"text\":\"Mostly Cloudy\"},{\"code\":\"28\",\"date\":\"30 Oct 2018\",\"day\":\"Tue\",\"high\":\"73\",\"low\":\"58\",\"text\":\"Mostly Cloudy\"},{\"code\":\"30\",\"date\":\"31 Oct 2018\",\"day\":\"Wed\",\"high\":\"75\",\"low\":\"56\",\"text\":\"Partly Cloudy\"},{\"code\":\"30\",\"date\":\"01 Nov 2018\",\"day\":\"Thu\",\"high\":\"75\",\"low\":\"55\",\"text\":\"Partly Cloudy\"},{\"code\":\"28\",\"date\":\"02 Nov 2018\",\"day\":\"Fri\",\"high\":\"72\",\"low\":\"58\",\"text\":\"Mostly Cloudy\"},{\"code\":\"28\",\"date\":\"03 Nov 2018\",\"day\":\"Sat\",\"high\":\"69\",\"low\":\"55\",\"text\":\"Mostly Cloudy\"}],\"description\":\"<![CDATA[<img src=\\\"http://l.yimg.com/a/i/us/we/52/32.gif\\\"/>\\n<BR />\\n<b>Current Conditions:</b>\\n<BR />Sunny\\n<BR />\\n<BR />\\n<b>Forecast:</b>\\n<BR /> Thu - Sunny. High: 62Low: 50\\n<BR /> Fri - Sunny. High: 68Low: 49\\n<BR /> Sat - Mostly Sunny. High: 71Low: 54\\n<BR /> Sun - Partly Cloudy. High: 71Low: 54\\n<BR /> Mon - Mostly Cloudy. High: 72Low: 58\\n<BR />\\n<BR />\\n<a href=\\\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-963291/\\\">Full Forecast at Yahoo! Weather</a>\\n<BR />\\n<BR />\\n<BR />\\n]]>\",\"guid\":{\"isPermaLink\":\"false\"}}}}}}";
		RDFVirtualizer virtualizer = new RDFVirtualizer();
		Model virtualRDF = virtualizer.virtualizeRDF(accessMappingJsonPaths, thingIri, "http://bnodes/dXBtLXdlYXRoZXItc2VydmljZS9pbnRlcmFjdGlvblBhdHRlcm4vMi9hY2Nlc3NNYXBwaW5n", jsonData);
		Boolean testCondition = virtualRDF.contains(ResourceFactory.createResource("http://vicinity-test.com"), ResourceFactory.createProperty("http://iot.linkeddata.es/def/core#literalValue"), "Sunny");
		testCondition &= virtualRDF.contains(ResourceFactory.createResource("http://vicinity-test.com"), ResourceFactory.createProperty("http://iot.linkeddata.es/def/core#timeStamp"), "26 Oct 2018");
		testCondition &= virtualRDF.contains(ResourceFactory.createResource("http://vicinity-test.com"), ResourceFactory.createProperty("http://iot.linkeddata.es/def/core#hasMinValue"), "49");
		testCondition &= virtualRDF.contains(ResourceFactory.createResource("http://vicinity-test.com"), ResourceFactory.createProperty("http://iot.linkeddata.es/def/core#hasMaxValue"), "68");
		Assert.assertTrue(testCondition);
	}

}
