import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ArticleRecommender {
    private static String source;
    private static int searchDistance;
    private static int numSamples;
    private static int numRecommendations;

    /**
     * Take in user input: the source URL and the distance to search
     */
    private static void input() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Source URL: ");
        source = reader.next();
        System.out.print("Search Distance: ");
        searchDistance = reader.nextInt();
        System.out.print("Maximum number of links sampled per page: ");
        numSamples = reader.nextInt();
        System.out.print("How many recommendations do you want? ");
        numRecommendations = reader.nextInt();
        reader.close();
    }

    public static void main(String[] args) {
        System.out.println("Wikipedia Article Recommender:");
        input();
        long start = System.nanoTime();
        Scraper sc = new Scraper(source, searchDistance, numSamples);
        Map<String, Set<String>> adjList = sc.getAdjList();
        String[] ranks = PageRanks.getPageRank(adjList, 0.1, 0.01, 100);
        int i = 0;
        while (i < ranks.length && i < numRecommendations) {
            System.out.println(ranks[i]);
            i++;
        }
        long end = System.nanoTime();
        System.out.println(adjList.size() + " nodes");
        System.out.println((end - start) / 1e9 + " seconds");
    }
}
