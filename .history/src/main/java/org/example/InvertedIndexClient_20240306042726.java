package org.example;
import java.rmi.Naming;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



public class InvertedIndexClient {
    // Logger for logging errors and information
    private static final Logger logger = Logger.getLogger(InvertedIndexClient.class.getName());
    public static void main(String[] args) {
        try {
            // Remote service endpoint URL
            String registryEndpoint = "rmi://168.138.68.157:8099/InvertedIndexService";
            // Look up the remote service
            InvertedIndexService service = (InvertedIndexService) Naming.lookup(registryEndpoint);
            // File name to retrieve the inverted index
            String fileName = "sample_data.txt";
            // Invoke the remote service to get the inverted index
            Map<String, List<Integer>> invertedIndex = service.getInvertedIndex(fileName);
            // Display the top tokens with the most frequent appearance
            printTopTokens(invertedIndex);
        } catch (java.rmi.NotBoundException e) {
            // Log error if the remote service is not bound
            logger.log(Level.SEVERE, "Remote service not bound", e);
        } catch (java.rmi.RemoteException e) {
            // Log error if there's a remote communication error
            logger.log(Level.SEVERE, "Remote communication error", e);
        } catch (Exception e) {
            // Log any other unexpected exceptions
            handleClientException(e);
        }
    }
    // Print the top tokens with the most frequent appearance
    private static void printTopTokens(Map<String, List<Integer>> invertedIndex) {
        System.out.println("Top-5 tokens with the most frequent appearance:");
        invertedIndex.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().size() - entry1.getValue().size())
                .limit(5)
                .forEach(entry -> {
                    String token = entry.getKey();
                    List<Integer> positions = entry.getValue();
                    System.out.println("Token: " + token + ", Frequency: " + positions.size() + ", Locations: " + positions);
                });
    }
    // Handle client-side exceptions
    private static void handleClientException(Exception e) {
        // Log the exception with the logger
        logger.log(Level.SEVERE, "Client error", e);
    }
}