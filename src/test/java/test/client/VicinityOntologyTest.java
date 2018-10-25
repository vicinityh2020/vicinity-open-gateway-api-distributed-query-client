package test.client;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Assert;
import org.junit.Test;

import client.VicinityOntology;


/**
 * This class checks all the attributes of {@link VicinityOntology} class
 * @author Andrea Cimmino
 * @version 0.6.3
 *
 */
public class VicinityOntologyTest {
	
	
	
	@Test
	public void test1() {
		Property RDF_TYPE = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		Assert.assertEquals(VicinityOntology.RDF_TYPE, RDF_TYPE);
	}
	
	@Test
	public void test2() {
		Property CORE_TD_DESCRIBES = ResourceFactory.createProperty("http://iot.linkeddata.es/def/core#describes");
		Assert.assertEquals(VicinityOntology.CORE_TD_DESCRIBES, CORE_TD_DESCRIBES);
	}
	
	@Test
	public void test3() {
		Property WOT_HAS_MAPPING = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#hasMapping");
		Assert.assertEquals(VicinityOntology.WOT_HAS_MAPPING, WOT_HAS_MAPPING);
	}
	
	@Test
	public void test4() {
		Property WOT_ACCESS_MAPPING_HAS_MEDIA_TYPE = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot#hasMediaType");
		Assert.assertEquals(VicinityOntology.WOT_ACCESS_MAPPING_HAS_MEDIA_TYPE, WOT_ACCESS_MAPPING_HAS_MEDIA_TYPE);
	}
	
	@Test
	public void test5() {
		Property WOT_ACCESS_MAPPING_MEDIA_TYPE = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot#mediaType");
		Assert.assertEquals(VicinityOntology.WOT_ACCESS_MAPPING_MEDIA_TYPE, WOT_ACCESS_MAPPING_MEDIA_TYPE);
	}

	@Test
	public void test6() {
		Resource WOT_TYPE_ACCESS_MAPPING = ResourceFactory.createResource("http://iot.linkeddata.es/def/wot-mappings#AccessMapping");
		Assert.assertEquals(VicinityOntology.WOT_TYPE_ACCESS_MAPPING, WOT_TYPE_ACCESS_MAPPING);
	}
	
	@Test
	public void test7() {
		Property MAP_KEY = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#key");
		Assert.assertEquals(VicinityOntology.MAP_KEY, MAP_KEY);
	}
	
	@Test
	public void test8() {
		Property MAP_PREDICATE = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#predicate");
		Assert.assertEquals(VicinityOntology.MAP_PREDICATE, MAP_PREDICATE);
	}

	@Test
	public void test9() {
		Property MAP_JSONPATH = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#jsonPath");
		Assert.assertEquals(VicinityOntology.MAP_JSONPATH, MAP_JSONPATH);
	}
	
	@Test
	public void test10() {
		Property MAP_VALUES_TRANSFORMED_BY = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#valuesTransformedBy");
		Assert.assertEquals(VicinityOntology.MAP_VALUES_TRANSFORMED_BY, MAP_VALUES_TRANSFORMED_BY);
	}
	
	@Test
	public void test11() {
		Property MAP_TD_HAS_ACCESS_MAPPING = ResourceFactory.createProperty("http://iot.linkeddata.es/def/wot-mappings#hasAccessMapping");
		Assert.assertEquals(VicinityOntology.MAP_TD_HAS_ACCESS_MAPPING, MAP_TD_HAS_ACCESS_MAPPING);
	}
	
	@Test
	public void test12() {
		String TURTULE = "TURTLE";
		Assert.assertEquals(VicinityOntology.TURTULE, TURTULE);
	}
	
	@Test
	public void test13() {
		String JSONLD = "JSONLD";
		Assert.assertEquals(VicinityOntology.JSONLD, JSONLD);
	}
	
	@Test
	public void test14() {
		String CONTENT_TYPE_RDF_XML = "application/rdf+xml";
		Assert.assertEquals(VicinityOntology.CONTENT_TYPE_RDF_XML, CONTENT_TYPE_RDF_XML);
	}	
	
	@Test
	public void test15() {
		String CONTENT_TYPE_RDF_TURLEX = "application/x-turtle";
		Assert.assertEquals(VicinityOntology.CONTENT_TYPE_RDF_TURLEX, CONTENT_TYPE_RDF_TURLEX);
	}	
	
	@Test
	public void test16() {
		String CONTENT_TYPE_RDF_TURLE = "text/turtle";
		Assert.assertEquals(VicinityOntology.CONTENT_TYPE_RDF_TURLE, CONTENT_TYPE_RDF_TURLE);
	}	
	
	@Test
	public void test17() {
		String CONTENT_TYPE_RDF_NT = "text/nt";
		Assert.assertEquals(VicinityOntology.CONTENT_TYPE_RDF_NT, CONTENT_TYPE_RDF_NT);
	}	
	
	@Test
	public void test18() {
		String CONTENT_TYPE_RDF_JSONLD = "application/ld+json";
		Assert.assertEquals(VicinityOntology.CONTENT_TYPE_RDF_JSONLD, CONTENT_TYPE_RDF_JSONLD);
	}
	
	@Test
	public void test19() {
		String CORE_TED_HAS_COMPONENT = "http://iot.linkeddata.es/def/core#hasComponent";
		Assert.assertEquals(VicinityOntology.CORE_TED_HAS_COMPONENT, CORE_TED_HAS_COMPONENT);
	}	
	
	@Test
	public void test20() {
		String WOT_OBJECT_PROPERTY_MAPPING = "http://iot.linkeddata.es/def/wot-mappings#ObjectPropertyMapping";
		Assert.assertEquals(VicinityOntology.WOT_OBJECT_PROPERTY_MAPPING, WOT_OBJECT_PROPERTY_MAPPING);
	}	
	
	@Test
	public void test21() {
		String WOT_MAPPING_MAPS_RESOURCES_FROM = "http://iot.linkeddata.es/def/wot-mappings#mapsResourcesFrom";
		Assert.assertEquals(VicinityOntology.WOT_MAPPING_MAPS_RESOURCES_FROM, WOT_MAPPING_MAPS_RESOURCES_FROM);
	}
	
	@Test
	public void test22() {
		String WOT_MAPPING_LINK_HREF = "http://iot.linkeddata.es/def/wot#href";
		Assert.assertEquals(VicinityOntology.WOT_MAPPING_LINK_HREF, WOT_MAPPING_LINK_HREF);
	}
	
	@Test
	public void test23() {
		String WOT_MAPPING_LINK_HREF = "http://iot.linkeddata.es/def/wot#href";
		Assert.assertEquals(VicinityOntology.WOT_MAPPING_LINK_HREF, WOT_MAPPING_LINK_HREF);
	}
	
	@Test
	public void test24() {
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
				"select distinct ?things ?type where {\n" + 
				"       ?things rdf:type wot:Thing . \n" + 
				"       	?things core:represents ?object . \n"+
				"       ?object rdf:type ?type . \n"+
				"		FILTER regex(?type, \"adapters\") .\n"+
				"}";
		
		Assert.assertTrue(!VicinityOntology.areRemoteEnpointsRequired(query));
	}
	
	@Test
	public void test25() {
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
				"}";
		
		Assert.assertTrue(VicinityOntology.areRemoteEnpointsRequired(query));
	}
	
	@Test
	public void test26() {
		String RDF_XML = "RDF/XML";
		Assert.assertEquals(VicinityOntology.RDF_XML, RDF_XML);
	}
	
	@Test
	public void test27() {
		String NT = "N-TRIPLES";
		Assert.assertEquals(VicinityOntology.NT, NT);
	}
	

}
