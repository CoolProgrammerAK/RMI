//package org.example;
package org.example;
import org.example.InvertedIndexService;
import org.example.in;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

public class InvertedIndexServiceImpl extends UnicastRemoteObject implements InvertedIndexService {
    private final ForkJoinPool pool;
    private final ExecutorService executorService;

    public InvertedIndexServiceImpl() throws RemoteException {
        super();
        pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public Map<String, List<Integer>> getInvertedIndex(String fileName) throws RemoteException {
        String text = new ReadFile().readFileData(fileName);
        String[] lines = text.split("\n");

        // Use fork-join pool to compute inverted index
//       Map<String, List<Integer>> index = computeInvertedIndexWithForkJoin(lines);

        // Use executors with callable to compute inverted index
         Map<String, List<Integer>> index = computeInvertedIndexWithExecutors(lines);

        return index;
    }

    private Map<String, List<Integer>> computeInvertedIndexWithExecutors(String[] lines) {
        Map<String, List<Integer>> index = new HashMap<>();
        List<Future<Void>> futures = new ArrayList<>();

        // For each line, submit a task to the executor to compute the inverted index
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineNumber = i;
            Future<Void> future = (Future<Void>) executorService.submit(() -> processLine(line, lineNumber, index));
            futures.add(future);
        }

        // Wait for all tasks to complete
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Shutdown the executor service
        executorService.shutdown();
        return index;
    }

    private void processLine(String line, int lineNumber, Map<String, List<Integer>> index) {
        String[] words = line.split("\\s+");
        Map<String, List<Integer>> lineIndex = new HashMap<>();

        for (String word : words) {
            List<Integer> positions = lineIndex.getOrDefault(word, new ArrayList<>());
            positions.add(lineNumber + 1);
            lineIndex.put(word, positions);
        }

        synchronized (index) {
            for (Map.Entry<String, List<Integer>> entry : lineIndex.entrySet()) {
                List<Integer> existingPositions = index.getOrDefault(entry.getKey(), new ArrayList<>());
                existingPositions.addAll(entry.getValue());
                Collections.sort(existingPositions);
                index.put(entry.getKey(), existingPositions);
            }
        }
    }

    private Map<String, List<Integer>> computeInvertedIndexWithForkJoin(String[] lines) {
        Map<String, List<Integer>> index = new HashMap<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineNumber = i;
            pool.submit(() -> processLine(line, lineNumber, index));
        }

        pool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        return index;
    }

    public static void main(String args[]) {
        try {
            String classpath = System.getProperty("java.class.path");
            System.out.println("Classpath: " + classpath);
            InvertedIndexServiceImpl server = new InvertedIndexServiceImpl();
            LocateRegistry.createRegistry(8099);
            Naming.rebind("//168.138.68.157:8099/InvertedIndexService", server);
            System.out.println("InvertedIndexService ready...");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}