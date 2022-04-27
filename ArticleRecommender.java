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
        Scraper sc = new Scraper(source, searchDistance);
        Map<String, Set<String>> adjList = sc.getAdjList();
    }
}
