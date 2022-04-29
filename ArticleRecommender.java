import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ArticleRecommender {
    private static String source;
    private static int searchDistance;

    /**
     * Take in user input: the source URL and the distance to search
     */
    private static void input() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Source URL: ");
        source = reader.next();
        System.out.print("Search Distance: ");
        searchDistance = reader.nextInt();
        reader.close();
    }

    public static void main(String[] args) {
        System.out.println("Wikipedia Article Recommender:");
        input();
        long start = System.nanoTime();
        Scraper sc = new Scraper(source, searchDistance);
        Map<String, Set<String>> adjList = sc.getAdjList();
        String[] ranks = PageRanks.getPageRank(adjList, 0.1, 0.01, 100);
//        for (String url : ranks) {
//            System.out.println(url);
//        }
        long end = System.nanoTime();
        System.out.println(adjList.size() + " nodes");
        System.out.println((end - start) / 1e9 + " seconds");
    }
}
