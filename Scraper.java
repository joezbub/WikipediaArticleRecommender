import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Scraper {
    private String source;
    private int searchDistance;
    private Map<String, Set<String>> adjList;

    /**
     * Constructor that takes in source URL and search distance
     * @param s Source URL
     * @param d Search distance
     */
    public Scraper(String s, int d) {
        source = s;
        searchDistance = d;
        scrape();
    }

    private static List<String> fetchPageContents(String target) {
        List<String> contents = new ArrayList<>();
        try {
            URL url = new URL(target);
            URLConnection con = url.openConnection();
            HttpURLConnection httpCon = (HttpURLConnection) con;
            Scanner in = new Scanner(httpCon.getInputStream());
            while (in.hasNextLine()) {
                String line = in.nextLine();
                contents.add(line);
            }
            return contents;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     *
     */
    private List<String> getNeighbors(String url) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     */
    public void scrape() {
        List<String> queue = new LinkedList<>();
        queue.add(source);
        Set<String> visited = new HashSet<>();
        visited.add(source);
        for (int i = 0; i < searchDistance; ++i) {
            while (!queue.isEmpty()) {
                String curr = queue.get(0);
                queue.remove(0);
                List<String> neighbors = getNeighbors(curr);
                for (String nxt : neighbors) {
                    if (!visited.contains(nxt)) {
                        if (!adjList.containsKey(curr)) {
                            adjList.put(curr, new HashSet<>());
                        }
                        adjList.get(curr).add(nxt);
                        visited.add(nxt);
                        queue.add(nxt);
                    }
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public Map<String, Set<String>> getAdjList() {
        throw new UnsupportedOperationException();
    }
}