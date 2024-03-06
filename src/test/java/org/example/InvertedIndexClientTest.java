package org.example;

//JUnit test for the client code
//import org.junit.Test;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.rmi.Naming;
//import static org.junit.Assert.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvertedIndexClientTest {
 
 @Test
 public void testInvertedIndex() throws Exception {
     // Locate the registry and get the stub of the service
	 String endpoint = "rmi://127.0.0.1:8099/InvertedIndexService";

     InvertedIndexService service = (InvertedIndexService) 
     						Naming.lookup(endpoint);
     
     // Prepare a sample text
     String text ="sample_data.txt";
     // Invoke the service and get the inverted index
     Map<String, List<Integer>> index = service.getInvertedIndex(text);
     //System.out.println(index.get("RMI"));  
     // Check the results
     //assertEquals(8, index.size());
     assertEquals(1, (int) index.get("usually").size());
     /*assertEquals(1, (int) index.get("world").size());
     assertEquals(1, (int) index.get("Java").size());
     assertEquals(1, (int) index.get("RMI").size());
     
     assertEquals(new ArrayList<>(List.of(1, 2, 3)),  index.get("Hello"));
     assertEquals(new ArrayList<>(List.of(1)),  index.get("world"));
     assertEquals(new ArrayList<>(List.of(2)),  index.get("Java"));
     assertEquals(new ArrayList<>(List.of(3)),  index.get("RMI"));*/
 }
}
