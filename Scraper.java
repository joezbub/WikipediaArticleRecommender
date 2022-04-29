import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scraper {
    private String source;
    private int searchDistance;
    private Map<String, Set<String>> adjList;
    private List<String> bannedSections;

    private final String baseURL = "https://en.wikipedia.org/";
    private final int numberOfSamples = 20;

    /**
     * Constructor that takes in source URL and search distance
     * @param s Source URL
     * @param d Search distance
     */
    public Scraper(String s, int d) {
        source = s;
        searchDistance = d;
        adjList = new HashMap<>();
        bannedSections = new ArrayList<>(
                Arrays.asList(
                        "id=\"External_links\">External links</span>",
                        "id=\"References\">References</span>",
                        "id=\"Notes\">Notes</span>",
                        "id=\"Further_reading\">Further reading</span>",
                        "id=\"Explanatory_notes\">Explanatory notes</span>",
                        "id=\"Citations\">Citations</span>"
                )
        );
        scrape();
    }

    /**
     * Fetch contents of a page given URL
     * @param target URL of page
     * @return list of all content strings
     */
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
     * Randomly samples URLs from current page
     * @param url the URL to the current page
     * @return a set of URLs to other articles
     */
    private Set<String> getNeighbors(String url) {
        List<String> neighbors = new ArrayList<>();
        Set<String> sample = new HashSet<>();
        List<String> contents = fetchPageContents(url);
        Pattern childPattern = Pattern.compile(
                "<a href=\"/(wiki[^:\\r\\n\\t\\f\\v]*?)\""
        );
//        for (String line : contents) {
//            System.out.println(line);
//            System.out.println("line");
//        }
        for (String line : contents) {
            boolean done = false;
            for (String html : bannedSections) {
                if (line.contains(html)) {
                    done = true;
                    break;
                }
            }
            if (done) {
                break;
            }
            Matcher matcher = childPattern.matcher(line);
            while (matcher.find()) {
                String value = matcher.group(1);
                if (value.length() >= 14 && value.substring(0, 14).equals("wiki/Wikipedia")
                   || value.equals("wiki/Main_Page") || value.contains("Glossary_of_")
                    || value.contains("List_of_")) {
                    continue;
                }
                neighbors.add(baseURL + value);
            }
        }
        Collections.shuffle(neighbors);
        int i = 0;
        while (i < neighbors.size() && sample.size() < numberOfSamples) {
            sample.add(neighbors.get(i));
            i++;
        }
        return sample;
    }

    /**
     * Creates an adjacency list by scraping from source through a BFS
     */
    public void scrape() {
        List<String> queue = new LinkedList<>();
        queue.add(source);
        Set<String> visited = new HashSet<>();
        visited.add(source);
        adjList.put(source, new HashSet<>());
        for (int i = 0; i < searchDistance; ++i) {
            List<String> tempQueue = new LinkedList<>();
            while (!queue.isEmpty()) {
                String curr = queue.get(0);
                queue.remove(0);
                Set<String> neighbors = getNeighbors(curr);
                System.out.println("par: " + curr);
                for (String nxt : neighbors) {
                    System.out.println(nxt);
                    if (!visited.contains(nxt)) {
                        if (!adjList.containsKey(nxt)) {
                            adjList.put(nxt, new HashSet<>());
                        }
                        adjList.get(curr).add(nxt);
                        visited.add(nxt);
                        tempQueue.add(nxt);
                    }
                }
            }
            queue = tempQueue;
        }
    }

    /**
     * Getter method for the adjacency list
     * @return the adjacency list
     */
    public Map<String, Set<String>> getAdjList() {
        return adjList;
    }
}