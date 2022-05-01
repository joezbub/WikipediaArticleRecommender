import java.util.*;

public class PageRanks {
    /**
     * Method to rank Wikipedia articles based on page ranks
     * @param adjList adjacency list of Wikipedia articles
     * @param teleport
     * @param MOE margin of error
     * @param iters max iterations
     * @return an array of ranked URLs
     */
    public static String[] getPageRank(Map<String, Set<String>> adjList, double teleport, double MOE, int iters) {
        HashMap<String, Integer> indices = new HashMap<>();
        HashMap<Integer, String> urls = new HashMap<>();

        int count = 0;
        for (String s : adjList.keySet()) {
            indices.put(s, count);
            urls.put(count, s);
            count++;
        }

        if (teleport > 1 || teleport < 0) {
            throw new IllegalArgumentException();
        }
        double[][] matrix = new double[adjList.size()][adjList.size()];
        for (int i = 0; i < adjList.size(); i++) {
            double sum = 1;
            for (int j = 0; j < adjList.size(); j++) {
                if (adjList.get(urls.get(i)).size() == 0) {
                    matrix[j][i] = 1.0 / adjList.size();
                } else {
                    matrix[j][i] = teleport / adjList.size();
                    sum -= teleport / adjList.size();
                }
            }
            for (String s : adjList.get(urls.get(i))) {
                matrix[indices.get(s)][i] += sum / adjList.get(urls.get(i)).size();
            }

        }

        double[] initial = new double[adjList.size()];
        Arrays.fill(initial, 1.0 / adjList.size());

        double[] ranks = run(0, MOE, iters, initial, matrix);

        Entry<String, Double>[] rankEntries = new Entry[ranks.length];

        for (int i = 0; i < ranks.length; i++) {
            rankEntries[i] = new Entry<>(urls.get(i), ranks[i]);
        }

        Arrays.sort(rankEntries, Comparator.comparing(o -> -1 * o.value));

//        for (Entry e : rankEntries) {
//            System.out.println(e.key + " " + e.value);
//        }

        String[] rankedUrls = new String[rankEntries.length];

        for (int i = 0; i < rankedUrls.length; i++) {
            rankedUrls[i] = rankEntries[i].key;
        }

        return rankedUrls;

    }

    /**
     * Helper method for getPageRanks()
     * @param currIter
     * @param MOE margin of error
     * @param maxIters max iterations
     * @param input
     * @param matrix
     * @return ranks of the URLs
     */
    static double[] run(
            int currIter, double MOE, int maxIters,
            double[] input, double[][] matrix) {
        if (currIter == maxIters) {
            return input;
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            for (int out = 0; out < input.length; out++) {
                output[out] += input[i] * matrix[out][i];
            }
        }

        double maxError = 0;
        for (int i = 0; i < input.length; i++) {
            if (Math.abs(input[i] - output[i]) > maxError) {
                maxError = Math.abs(input[i] - output[i]);
            }
        }

        if (maxError <= MOE) {
            return output;
        }

        return run(currIter + 1, MOE, maxIters, output, matrix);

    }

    static class Entry<Key, V> {
        public final Key key;
        public final V value;

        public Entry(Key k, V v) {
            key = k;
            value = v;
        }
    }

}
