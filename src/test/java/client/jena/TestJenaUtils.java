package client.jena;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Assert;
import org.junit.Test;

import client.VicinityOntology;

/**
 * This class tests all the methods of {@link JenaUtils} class
 * @author Andrea Cimmino
 * @version 0.6.3
 *
 */
public class TestJenaUtils {
	
	/*	
	     JenaUtils methods:
			- test1: JenaUtils.fromJsonLDtoRDFModel(json)		     	  -> covered
			- test2: JenaUtils.fromJsonToMap(json) 					  -> covered
			- test3: JenaUtils.fromRDFNodeToString(nodes)				  -> covered
			- test4: JenaUtils.parseRDF(strRDF, format)				  -> covered
			- test5: JenaUtils.extractFullSubTree(resourceNode, ted)    -> covered
			- test6: JenaUtils.toString(model)						  -> covered
	 */

	@Test
	public void test1() {
		String json = "{\"name\":\"John\", \"surname\": \"Doe\"}";
		Map<String,String> map = JenaUtils.fromJsonToMap(json);
		Boolean containsKeys = map.containsKey("name") && map.containsKey("surname");
		Boolean containsValues = map.get("name").equals("John") &&  map.get("surname").equals("Doe");
		Assert.assertTrue(containsKeys && containsValues);
	}
	
	@Test
	public void test2() {
		String jsonLD = "[{\"@id\":\"http://test2.com/city\",\"http://schema.org/name\":[{\"@value\":\"Seville\"}]},{\"@id\":\"http://test2.com/person\",\"http://schema.org/name\":[{\"@value\":\"John\"}],\"http://schema.org/surname\":[{\"@value\":\"Doe\"}],\"http://schema.org/livesIn\":[{\"@id\":\"http://test2.com/city\"}]}]";
		Model model = JenaUtils.fromJsonLDtoRDFModel(jsonLD);
		Boolean testCondition = model.contains(ResourceFactory.createResource("http://test2.com/person"), ResourceFactory.createProperty("http://schema.org/name"), "John");
		testCondition &= model.contains(ResourceFactory.createResource("http://test2.com/person"), ResourceFactory.createProperty("http://schema.org/surname"), "Doe");
		testCondition &= model.contains(ResourceFactory.createResource("http://test2.com/person"), ResourceFactory.createProperty("http://schema.org/livesIn"), ResourceFactory.createResource("http://test2.com/city"));
		testCondition &= model.contains(ResourceFactory.createResource("http://test2.com/city"), ResourceFactory.createProperty("http://schema.org/name"), "Seville");
		Assert.assertTrue(testCondition);
	}
	
	@Test
	public void test3() {
		List<RDFNode> nodes = new ArrayList<RDFNode>(3);
		nodes.add(ResourceFactory.createResource("http://test3.com/persons/1"));
		nodes.add(ResourceFactory.createResource("http://test3.com/persons/2"));
		nodes.add(ResourceFactory.createResource("http://test3.com/persons/3"));
		List<String> nodeURIs = JenaUtils.fromRDFNodeToString(nodes);
		Boolean testCondition = nodeURIs.size() == 3;
		testCondition &= nodeURIs.contains("http://test3.com/persons/1");
		testCondition &= nodeURIs.contains("http://test3.com/persons/2");
		testCondition &= nodeURIs.contains("http://test3.com/persons/3");
		Assert.assertTrue(testCondition);
	}

	@Test
	public void test4() {
		String rdf = "<http://test4.com/person> <http://schema.org/name> \"John\" .\n" + 
				"<http://test4.com/person> <http://schema.org/surname> \"Doe\" .\n" 	 + 
				"<http://test4.com/person> <http://schema.org/livesIn> <http://test4.com/city> .\n" + 
				"<http://test4.com/city> <http://schema.org/name> \"Seville\" ."; 
		Model model = JenaUtils.parseRDF(rdf, VicinityOntology.TURTULE);
		Boolean testCondition = model.contains(ResourceFactory.createResource("http://test4.com/person"), ResourceFactory.createProperty("http://schema.org/name"), "John");
		testCondition &= model.contains(ResourceFactory.createResource("http://test4.com/person"), ResourceFactory.createProperty("http://schema.org/surname"), "Doe");
		testCondition &= model.contains(ResourceFactory.createResource("http://test4.com/person"), ResourceFactory.createProperty("http://schema.org/livesIn"), ResourceFactory.createResource("http://test4.com/city"));
		testCondition &= model.contains(ResourceFactory.createResource("http://test4.com/city"), ResourceFactory.createProperty("http://schema.org/name"), "Seville");
		Assert.assertTrue(testCondition);
	}
	
	@Test
	public void test5() {
		Model model = ModelFactory.createDefaultModel();
		model.add(ResourceFactory.createResource("http://test5.com/person"), ResourceFactory.createProperty("http://schema.org/name"), "John");
		model.add(ResourceFactory.createResource("http://test5.com/person"), ResourceFactory.createProperty("http://schema.org/surname"), "Doe");
		model.add(ResourceFactory.createResource("http://test5.com/person"), ResourceFactory.createProperty("http://schema.org/livesIn"), ResourceFactory.createResource("http://test5.com/city"));
		model.add(ResourceFactory.createResource("http://test5.com/city"), ResourceFactory.createProperty("http://schema.org/name"), "Seville");
	
		Model subGraph = JenaUtils.extractFullSubTree(ResourceFactory.createResource("http://test5.com/person").asNode(), model);	
		Boolean testCondition = subGraph.contains(ResourceFactory.createResource("http://test5.com/person"), ResourceFactory.createProperty("http://schema.org/name"), "John");
		testCondition &= subGraph.contains(ResourceFactory.createResource("http://test5.com/person"), ResourceFactory.createProperty("http://schema.org/surname"), "Doe");
		testCondition &= subGraph.contains(ResourceFactory.createResource("http://test5.com/person"), ResourceFactory.createProperty("http://schema.org/livesIn"), ResourceFactory.createResource("http://test5.com/city"));
		testCondition &= subGraph.contains(ResourceFactory.createResource("http://test5.com/city"), ResourceFactory.createProperty("http://schema.org/name"), "Seville");
		Assert.assertTrue(testCondition);
	}
	
	@Test
	public void test6() {
		Model model = ModelFactory.createDefaultModel();
		model.add(ResourceFactory.createResource("http://test5.com/person"), ResourceFactory.createProperty("http://schema.org/name"), "John");
		model.add(ResourceFactory.createResource("http://test5.com/person"), ResourceFactory.createProperty("http://schema.org/surname"), "Doe");
		model.add(ResourceFactory.createResource("http://test5.com/person"), ResourceFactory.createProperty("http://schema.org/livesIn"), ResourceFactory.createResource("http://test5.com/city"));
		model.add(ResourceFactory.createResource("http://test5.com/city"), ResourceFactory.createProperty("http://schema.org/name"), "Seville");
		String string = JenaUtils.toString(model);
		Boolean testCondition = string.contains("<http://test5.com/city>\n" + 
				"        <http://schema.org/name>  \"Seville\" .");
		testCondition &= string.contains("<http://test5.com/person>\n" + 
				"        <http://schema.org/livesIn>  <http://test5.com/city> ;\n" + 
				"        <http://schema.org/name>     \"John\" ;\n" + 
				"        <http://schema.org/surname>  \"Doe\" .");
		Assert.assertTrue(testCondition);
	}
}
