package test.client;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import client.VicinityAgoraClient;

public class VicinityAgoraClientTest {

	
	@Test
	public void test1() {
		String jsonTed = "";
		Set<String> neighbours = new HashSet<String>();
		String query = "";
		VicinityAgoraClient client = new VicinityAgoraClient(jsonTed, neighbours, query);
	
		Assert.assertTrue(true);
	}
}
