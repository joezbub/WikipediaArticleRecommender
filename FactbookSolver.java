import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.*;

public class FactbookSolver {
    private static final String rootUrl = "https://www.cia.gov/the-world-factbook/";

    /**
     * Helper function to print the fetched content of a page.
     * @param contents ArrayList of page content
     */
    private static void printContents(ArrayList<String> contents) {
        for (String s : contents) {
            System.out.println(s);
        }
    }

    /**
     * Helper function to fetch contents of a page given a URL.
     * @param desiredUrl String form of the desired URL
     * @return the contents of the page
     */
    private static ArrayList<String> fetchContents(String desiredUrl) {
        ArrayList<String> contents = new ArrayList<>();
        try {
            URL url = new URL(desiredUrl);
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
     * Helper function to return a map of regions to their countries.
     * @return A hash map with regions mapped to a list of countries associated with them
     */
    private static Map<String, ArrayList<ArrayList<String> > > countriesByRegion() {
        Map<String, ArrayList<ArrayList<String> > > countries = new HashMap<>();
        Map<String, String> linkLabels = new HashMap<>();
        ArrayList<String> rootPageContents = fetchContents(rootUrl);

        Pattern sectionTitle = Pattern.compile("The World & Its Regions", Pattern.CASE_INSENSITIVE);
        boolean found = false;
        for (String line : rootPageContents) {
            if (found) {
                Pattern linkName = Pattern.compile("a href=\"/the-world-factbook/([^\"]+)\"");
                Matcher matcher = linkName.matcher(line);
                if (matcher.find()) {
                    int ind = matcher.end();
                    String link = matcher.group(1);
                    StringBuilder region = new StringBuilder();
                    for (int i = ind + 1; i < line.length(); ++i) {
                        if (line.charAt(i) == '<') {
                            break;
                        } else {
                            region.append(line.charAt(i));
                        }
                    }
                    linkLabels.put(region.toString().toLowerCase(), link);
                } else {
                    break;
                }
            } else {
                Matcher matcher = sectionTitle.matcher(line);
                if (matcher.find()) {
                    found = true;
                }
            }
        }

        for (Map.Entry<String, String> e : linkLabels.entrySet()) {
            countries.put(e.getKey(), new ArrayList<>());
            ArrayList<String> countryPage = fetchContents(rootUrl + e.getValue());
            for (String line : countryPage) {
                Pattern linkName = Pattern.compile("<li><h5><a class=\"link-button bold\" " +
                        "href=\"/the-world-factbook/([^\"]+)\"");
                Matcher matcher = linkName.matcher(line);
                while (matcher.find()) {
                    int ind = matcher.end();
                    StringBuilder country = new StringBuilder();
                    for (int i = ind + 1; i < line.length(); ++i) {
                        if (line.charAt(i) == '<') {
                            break;
                        } else {
                            country.append(line.charAt(i));
                        }
                    }
                    countries.get(e.getKey()).add(new ArrayList<>(
                            Arrays.asList(country.toString(), rootUrl + matcher.group(1)))
                    );
                }
            }
        }
        return countries;
    }

    /**
     * Method used to answer #1.
     * @param color1 the first required color
     * @param color2 the second required color
     * @return the list of countries with the two colors in their flags
     */
    public static ArrayList<String> findFlagsWithColors(String color1, String color2) {
        ArrayList<String> ans = new ArrayList<>();
        Map<String, ArrayList<ArrayList<String> > > countries = countriesByRegion();
        for (ArrayList<ArrayList<String> > region : countries.values()) {
            for (ArrayList<String> country : region) {
                String name = country.get(0);
                String url = country.get(1);
                ArrayList<String> contents = fetchContents(url);
                for (String line : contents) {
                    Pattern linkName = Pattern.compile("Flag description[0-9a-z<>/]+<p>");
                    Matcher matcher = linkName.matcher(line);
                    if (matcher.find()) {
                        int ind = matcher.end();
                        StringBuilder desc = new StringBuilder();
                        for (int j = ind; j < line.length(); ++j) {
                            if (line.substring(j, j + 4).equals("</p>")) {
                                break;
                            } else {
                                desc.append(line.charAt(j));
                            }
                        }
                        String converted = desc.toString().toLowerCase();
                        if (Arrays.asList(converted.split(" ")).contains(color1) &&
                                Arrays.asList(converted.split(" ")).contains(color2)) {
                            System.out.println(name);
                            System.out.println(desc);
                            ans.add(name);
                        }
                        break;
                    }
                }
            }
        }
        return ans;
    }

    /**
     * Helper method to return the URL associated with an ocean.
     * @param oceanName the name of the ocean we want to query
     * @return the URL of the ocean page, returns null if the ocean does not exist.
     */
    private static String getOceanUrl(String oceanName) {
        oceanName = oceanName.toLowerCase();
        ArrayList<String> rootPageContents = fetchContents(rootUrl);

        String oceanUrl = null;
        Pattern sectionTitle = Pattern.compile("<h2>Oceans</h2>", Pattern.CASE_INSENSITIVE);
        boolean found = false;
        for (String line : rootPageContents) {
            if (found) {
                Pattern linkName = Pattern.compile("a href=\"/the-world-factbook/([^\"]+)\"");
                Matcher matcher = linkName.matcher(line);
                if (matcher.find()) {
                    int ind = matcher.end();
                    String link = matcher.group(1);
                    StringBuilder currOcean = new StringBuilder();
                    for (int i = ind + 1; i < line.length(); ++i) {
                        if (line.charAt(i) == '<') {
                            break;
                        } else {
                            currOcean.append(line.charAt(i));
                        }
                    }
                    if (currOcean.toString().toLowerCase().equals(oceanName)) {
                        oceanUrl = link;
                        break;
                    }
                } else {
                    break;
                }
            } else {
                Matcher matcher = sectionTitle.matcher(line);
                if (matcher.find()) {
                    found = true;
                }
            }
        }

        if (oceanUrl == null) {
            return null;
        } else {
            return rootUrl + oceanUrl;
        }
    }

    /**
     * Method used to answer #2.
     * @param oceanName the name of the ocean we want to query
     * @return the lowest point of the ocean, returns null if the ocean does not exist.
     */
    public static String getLowestPoint(String oceanName) {
        String lowest = null;
        String oceanUrl = getOceanUrl(oceanName);
        if (oceanUrl == null) {
            return null;
        }
        ArrayList<String> rootPageContents = fetchContents(oceanUrl);
        for (String line : rootPageContents) {
            Pattern linkName = Pattern.compile("<strong>lowest point: </strong>");
            Matcher matcher = linkName.matcher(line);
            if (matcher.find()) {
                StringBuilder loc = new StringBuilder();
                for (int i = matcher.end(); i < line.length(); ++i) {
                    if (line.charAt(i) == '-') {
                        break;
                    } else {
                        loc.append(line.charAt(i));
                    }
                }
                lowest = loc.toString();
            }
        }
        return lowest;
    }

    /**
     * Method used to answer #3.
     * @param region The region in question, must be one of the regions listed on the home page.
     * @return the largest country in the specified region by electricity production
     */
    public static String getLargestCountryByElectricityProduction(String region) {
        Map<String, Long> convert = new HashMap<String, Long>() {
            {
                put("million", 1000000L);
                put("billion", 1000000000L);
                put("trillion", 1000000000000L);
            }
        };
        String largest = null;
        double amount = 0;
        region = region.toLowerCase();
        Map<String, ArrayList<ArrayList<String> > > countries = countriesByRegion();
        if (!countries.containsKey(region)) {
            return null;
        }

        for (ArrayList<String> country : countries.get(region)) {
            String name = country.get(0);
            String url = country.get(1);
            ArrayList<String> countryPage = fetchContents(url);
            for (String line : countryPage) {
                // System.out.println(line);
                Pattern pattern = Pattern.compile("Electricity - production</a></h3><p>");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    StringBuilder current = new StringBuilder();
                    for (int i = matcher.end(); i < line.length(); ++i) {
                        if (line.charAt(i) == '(') {
                            break;
                        } else {
                            current.append(line.charAt(i));
                        }
                    }
                    String[] arr = current.toString().replaceAll(",", "").split(" ");
                    Double currentAmount = null;
                    if (arr.length == 3 && !arr[0].equals("0")) {
                        currentAmount = Double.parseDouble(arr[0]) * convert.get(arr[1]);
                    } else if (arr.length == 2) {
                        currentAmount = Double.parseDouble(arr[0]);
                    }
                    System.out.println(name + " " + currentAmount);
                    if (currentAmount != null && currentAmount > amount) {
                        amount = currentAmount;
                        largest = name;
                    }
                    break;
                }
            }
        }
        return largest;
    }

    /**
     * Method used to answer #4.
     * @param region The region in question, must be one of the regions listed on the home page.
     * @return the country with the largest coastline to land area ratio.
     */
    public static String getCountryWithLargestRatio(String region) {
        double largestRatio = 0;
        String largest = null;
        region = region.toLowerCase();
        Map<String, ArrayList<ArrayList<String>>> countries = countriesByRegion();
        if (!countries.containsKey(region)) {
            return null;
        }

        for (ArrayList<String> country : countries.get(region)) {
            String name = country.get(0);
            String url = country.get(1);
            System.out.println(name);
            ArrayList<String> countryPage = fetchContents(url);
            Double coastline = null, landArea = null;
            for (String line : countryPage) {
                Pattern patternLand = Pattern.compile("<strong>land: </strong>");
                Matcher matcherLand = patternLand.matcher(line);
                if (matcherLand.find()) {
                    StringBuilder current = new StringBuilder();
                    for (int i = matcherLand.end(); i < line.length(); ++i) {
                        if (line.charAt(i) == ' ') {
                            break;
                        } else {
                            current.append(line.charAt(i));
                        }
                    }
                    String tmp = current.toString().replaceAll(",", "");
                    if (tmp.matches("[\\d.]+")) {
                        landArea = Double.valueOf(tmp);
                    }
                    System.out.println(landArea);
                }
            }
            for (String line : countryPage) {
                Pattern patternCoast = Pattern.compile("Coastline</a></h3><p>");
                Matcher matcherCoast = patternCoast.matcher(line);
                if (matcherCoast.find()) {
                    StringBuilder current = new StringBuilder();
                    for (int i = matcherCoast.end(); i < line.length(); ++i) {
                        if (line.substring(i, i + 4).equals("</p>")) {
                            break;
                        } else {
                            current.append(line.charAt(i));
                            if (line.charAt(i) == '>') {
                                current.append(' ');
                            }
                        }
                    }
                    String[] arr = current.toString().split(" ");
                    for (int i = 0; i < arr.length; ++i) {
                        arr[i] = arr[i].replaceAll(",", "");
                        if (arr[i].matches("[\\d.]+")) {
                            if (coastline == null) {
                                coastline = 0.0;
                            }
                            coastline += Double.valueOf(arr[i]);
                        }
                    }
                    System.out.println(coastline);
                }
            }
            if (landArea != null && coastline != null) {
                Double currentRatio = coastline / landArea;
                if (currentRatio > largestRatio) {
                    largestRatio = currentRatio;
                    largest = name;
                }
            }
        }
        System.out.println("\nRatio: " + largestRatio);
        return largest;
    }

    /**
     * Method used to answer #5.
     * @param region The region in question, must be one of the regions listed on the home page.
     * @return the population of the country with the highest mean elevation.
     */
    public static String getPopulationOfHighestCountry(String region) {
        String pop = null;
        Integer highestElevation = -1;
        String countryName = null;
        region = region.toLowerCase();
        Map<String, ArrayList<ArrayList<String>>> countries = countriesByRegion();
        if (!countries.containsKey(region)) {
            return null;
        }

        for (ArrayList<String> country : countries.get(region)) {
            String name = country.get(0);
            String url = country.get(1);
            System.out.println(name);
            ArrayList<String> countryPage = fetchContents(url);
            Integer elevation = null;
            for (String line : countryPage) {
                Pattern patternElevation = Pattern.compile("<strong>mean elevation: </strong>");
                Matcher matcherElevation = patternElevation.matcher(line);
                if (matcherElevation.find()) {
                    StringBuilder current = new StringBuilder();
                    for (int i = matcherElevation.end(); i < line.length(); ++i) {
                        if (line.charAt(i) == ' ') {
                            break;
                        } else {
                            current.append(line.charAt(i));
                        }
                    }
                    String tmp = current.toString().replaceAll(",", "");
                    if (tmp.matches("[\\d.]+")) {
                        elevation = Integer.valueOf(tmp);
                    }
                    System.out.println(elevation);
                }
            }
            String population = null;
            for (String line : countryPage) {
                Pattern patternPop = Pattern.compile("Population</a></h3><p>");
                Matcher matcherPop = patternPop.matcher(line);
                if (matcherPop.find()) {
                    if (!Character.isDigit(line.charAt(matcherPop.end()))) {
                        break;
                    }
                    StringBuilder current = new StringBuilder();
                    for (int i = matcherPop.end(); i < line.length(); ++i) {
                        if (line.charAt(i) == '(') {
                            break;
                        } else {
                            current.append(line.charAt(i));
                        }
                    }
                    population = current.toString();
                    System.out.println(population);
                }
            }
            if (population != null && elevation != null && elevation > highestElevation) {
                highestElevation = elevation;
                countryName = name;
                pop = population;
            }
        }
        System.out.println("\n" + countryName + "\n" + highestElevation);
        return pop;
    }

    /**
     * Helper method to filter out non-Caribbean countries
     * @param allCountries The original list of countries
     * @return A list of countries that are in the Caribbean
     */
    private static ArrayList<ArrayList<String> > filerCaribbean(
            ArrayList<ArrayList<String> > allCountries) {
        ArrayList<ArrayList<String> > result = new ArrayList<>();
        for (ArrayList<String> country : allCountries) {
            String name = country.get(0);
            String url = country.get(1);
            ArrayList<String> countryPage = fetchContents(url);
            for (String line : countryPage) {
                Pattern patternLocation = Pattern.compile("Location</a></h3><p>");
                Matcher matcherLocation = patternLocation.matcher(line);
                if (matcherLocation.find()) {
                    if (line.substring(matcherLocation.end(),
                            matcherLocation.end() + 9).equals("Caribbean")) {
                        result.add(country);
                        break;
                    }
                }
            }
        }
        System.out.println(result.size());
        for (ArrayList<String> country : result) {
            String name = country.get(0);
            String url = country.get(1);
            System.out.println(name + "\n" + url);
        }
        return result;
    }

    /**
     * Method used to answer question #6.
     * @param region The region in question, must be one of the regions listed on the home page or
     *               the Caribbean.
     * @return A list of countries that the third largest country by total area is import
     * partners with
     */
    public static ArrayList<String> getImportPartnersOfThirdLargestCountry(String region) {
        ArrayList<String> partners = new ArrayList<>();
        String countryName = null;
        region = region.toLowerCase();
        Map<String, ArrayList<ArrayList<String>>> countries = countriesByRegion();
        ArrayList<ArrayList<String> > filtered = new ArrayList<>();
        if (region.equals("caribbean")) {
            filtered = filerCaribbean(countries.get("central america"));
        } else {
            if (!countries.containsKey(region)) {
                return null;
            }
            filtered = countries.get(region);
        }

        Map<Double, ArrayList<String> > totalArea = new TreeMap<>(Collections.reverseOrder());
        Map<String, Long> convert = new HashMap<String, Long>() {
            {
                put("million", 1000000L);
                put("billion", 1000000000L);
            }
        };
        for (ArrayList<String> country : filtered) {
            String name = country.get(0);
            String url = country.get(1);
            ArrayList<String> countryPage = fetchContents(url);
            Double total = 0.0;
            for (String line : countryPage) {
                Pattern patternArea = Pattern.compile("Area</a></h3><p>");
                Matcher matcherArea = patternArea.matcher(line);
                if (matcherArea.find()) {
                    StringBuilder current = new StringBuilder();
                    for (int i = matcherArea.end(); i < line.length(); ++i) {
                        if (line.substring(i, i + 4).equals("</p>")) {
                            break;
                        } else {
                            current.append(line.charAt(i));
                            if (line.charAt(i) == '>') {
                                current.append(' ');
                            }
                        }
                    }
                    String[] arr = current.toString().split(" ");
                    for (int i = 0; i < arr.length; ++i) {
                        if (arr[i].contains("total")) {
                            arr[i + 2] = arr[i + 2].replaceAll(",", "");
                            Long mult = Long.valueOf(1);
                            if (convert.containsKey(arr[i + 3])) {
                                mult = convert.get(arr[i + 3]);
                            }
                            total += Double.valueOf(arr[i + 2]) * mult;
                            i += 2;
                        }
                    }
                    break;
                }
            }
            totalArea.put(total, country);
        }

        System.out.println("\n");
        for (Double d : totalArea.keySet()) {
            System.out.println(totalArea.get(d).get(0));
            System.out.println(d);
        }
        ArrayList<String> val = totalArea.get(totalArea.keySet().toArray()[2]);
        String third = val.get(0);
        String url = val.get(1);
        System.out.println("\n" + third + "\n");
        ArrayList<String> countryPage = fetchContents(url);
        for (String line : countryPage) {
            Pattern patternImports = Pattern.compile("Imports - partners</a></h3><p>");
            Matcher matcherImports = patternImports.matcher(line);
            if (matcherImports.find()) {
                StringBuilder current = new StringBuilder();
                for (int i = matcherImports.end(); i < line.length(); ++i) {
                    if (line.substring(i, i + 4).equals("</p>")) {
                        break;
                    } else {
                        current.append(line.charAt(i));
                    }
                }
                String[] arr = current.toString().split(", ");
                for (int i = 0; i < arr.length; ++i) {
                    StringBuilder partner = new StringBuilder();
                    for (int j = 0; j < arr[i].length(); ++j) {
                        if (Character.isDigit(arr[i].charAt(j))) {
                            break;
                        } else {
                            partner.append(arr[i].charAt(j));
                        }
                    }
                    partners.add(partner.toString());
                }
            }
        }
        return partners;
    }

    /**
     * Method used to answer question #7.
     * @param letter A vaild English letter (not case-sensitive)
     * @return A list of countries that start with the specified letter in ascending order
     * based on total area
     */
    public static ArrayList<String> sortByTotalArea(String letter) {
        ArrayList<String> ans = new ArrayList<>();
        if (letter.length() != 1 || !Character.isLetter(letter.charAt(0))) {
            return null;
        }
        letter = letter.toUpperCase();
        Map<String, ArrayList<ArrayList<String>>> countries = countriesByRegion();

        Map<Double, String> totalArea = new TreeMap<>();
        Map<String, Long> convert = new HashMap<String, Long>() {
            {
                put("million", 1000000L);
                put("billion", 1000000000L);
            }
        };
        for (ArrayList<ArrayList<String> > region : countries.values()) {
            for (ArrayList<String> country : region) {
                String name = country.get(0);
                String url = country.get(1);
                if (name.charAt(0) != letter.charAt(0)) {
                    continue;
                }
                ArrayList<String> countryPage = fetchContents(url);
                Double total = 0.0;
                for (String line : countryPage) {
                    Pattern patternArea = Pattern.compile("Area</a></h3><p>");
                    Matcher matcherArea = patternArea.matcher(line);
                    if (matcherArea.find()) {
                        StringBuilder current = new StringBuilder();
                        for (int i = matcherArea.end(); i < line.length(); ++i) {
                            if (line.substring(i, i + 4).equals("</p>")) {
                                break;
                            } else {
                                current.append(line.charAt(i));
                                if (line.charAt(i) == '>') {
                                    current.append(' ');
                                }
                            }
                        }
                        String[] arr = current.toString().split(" ");
                        for (int i = 0; i < arr.length; ++i) {
                            if (arr[i].contains("total")) {
                                arr[i + 2] = arr[i + 2].replaceAll(",", "");
                                Long mult = Long.valueOf(1);
                                if (convert.containsKey(arr[i + 3])) {
                                    mult = convert.get(arr[i + 3]);
                                }
                                total += Double.valueOf(arr[i + 2]) * mult;
                                i += 2;
                            }
                        }
                        break;
                    }
                }
                System.out.println(name);
                System.out.println(total);
                totalArea.put(total, name);
            }
        }
        for (String country : totalArea.values()) {
            ans.add(country);
        }
        return ans;
    }

    /**
     * Method used to answer question #8.
     * @param region The region in question, must be one of the regions listed on the home
     *               page or the Caribbean.
     * @return The name of the country with the largest external debt to population ratio
     * in the region.
     */
    public static String getLargestDebtToPopulationRatio(String region) {
        String ans = null;
        Double maxRatio = 0.0;
        region = region.toLowerCase();
        Map<String, ArrayList<ArrayList<String>>> countries = countriesByRegion();
        if (!countries.containsKey(region)) {
            return null;
        }

        Map<String, Long> convert = new HashMap<String, Long>() {
            {
                put("million", 1000000L);
                put("billion", 1000000000L);
                put("trillion", 1000000000000L);
            }
        };
        for (ArrayList<String> country : countries.get(region)) {
            String name = country.get(0);
            String url = country.get(1);
            System.out.println(name);
            ArrayList<String> countryPage = fetchContents(url);
            Double debt = null;
            for (String line : countryPage) {
                Pattern patternDebt = Pattern.compile("Debt - external</a></h3><p>\\$");
                Matcher matcherDebt = patternDebt.matcher(line);
                if (matcherDebt.find()) {
                    if (line.substring(matcherDebt.end(), matcherDebt.end() + 2).equals("NA")) {
                        break;
                    }
                    StringBuilder current = new StringBuilder();
                    for (int i = matcherDebt.end(); i < line.length(); ++i) {
                        if (line.charAt(i) == '(') {
                            break;
                        } else {
                            current.append(line.charAt(i));
                        }
                    }
                    String[] arr = current.toString().split(" ");
                    arr[0] = arr[0].replaceAll(",", "");
                    if (arr.length == 1) {
                        debt = Double.valueOf(arr[0]);
                    } else {
                        debt = Double.valueOf(arr[0]) * convert.get(arr[1]);
                    }
                    System.out.println(debt);
                }
            }

            Double population = null;
            for (String line : countryPage) {
                Pattern patternPop = Pattern.compile("Population</a></h3><p>");
                Matcher matcherPop = patternPop.matcher(line);
                if (matcherPop.find()) {
                    if (!Character.isDigit(line.charAt(matcherPop.end()))) {
                        break;
                    }
                    StringBuilder current = new StringBuilder();
                    for (int i = matcherPop.end(); i < line.length(); ++i) {
                        if (line.charAt(i) == '(') {
                            break;
                        } else {
                            current.append(line.charAt(i));
                        }
                    }
                    String[] arr = current.toString().split(" ");
                    population = Double.valueOf(arr[0].replaceAll(",", ""));
                    if (arr.length >= 2 && convert.containsKey(arr[1])) {
                        population *= convert.get(arr[1]);
                    }
                    System.out.println(population);
                }
            }

            if (debt != null && population != null) {
                Double currRatio = debt / population;
                if (currRatio > maxRatio) {
                    maxRatio = currRatio;
                    ans = name;
                }
            }
        }
        System.out.println("\n" + maxRatio);
        return ans;
    }

    /**
     * Helper method for extra credit #1.
     * @return a hash map of countries with their associated capital names and coordinates
     */
    private static Map<String, ArrayList<String> > getCoordinatesAndCapitals() {
        Map<String, ArrayList<String> > ans = new HashMap<>();
        Map<String, ArrayList<ArrayList<String>>> countries = countriesByRegion();
        for (ArrayList<ArrayList<String> > region : countries.values()) {
            for (ArrayList<String> country : region) {
                String name = country.get(0);
                String url = country.get(1);
                ArrayList<String> countryPage = fetchContents(url);
                String finalCoords = null, capitalName = null;
                for (String line : countryPage) {
                    Pattern patternCoords =
                            Pattern.compile("<strong>geographic coordinates: </strong>");
                    Matcher matcherCoords = patternCoords.matcher(line);
                    if (matcherCoords.find()) {
                        StringBuilder coords = new StringBuilder();
                        int ind = matcherCoords.end();
                        while (!Character.isDigit(line.charAt(ind))) {
                            ind++;
                        }
                        for (int i = ind; i < line.length(); ++i) {
                            if (line.charAt(i) == '<') {
                                break;
                            } else {
                                coords.append(line.charAt(i));
                            }
                        }
                        finalCoords = coords.toString();
                        break;
                    }
                }
                for (String line : countryPage) {
                    Pattern patternName =
                            Pattern.compile("Capital</a></h3><p><strong>name: </strong>");
                    Matcher matcherName = patternName.matcher(line);
                    if (matcherName.find()) {
                        StringBuilder currentName = new StringBuilder();
                        for (int i = matcherName.end(); i < line.length(); ++i) {
                            if (line.substring(i, i + 5).equals("<br/>") ||
                                    line.substring(i, i + 2).equals("; ")) {
                                break;
                            } else {
                                currentName.append(line.charAt(i));
                            }
                        }
                        capitalName = currentName.toString();
                        break;
                    }
                }
                if (finalCoords != null && capitalName != null) {
                    System.out.println(capitalName + ", " + name);
                    System.out.println(finalCoords + "\n");
                    ans.put(name, new ArrayList<>(Arrays.asList(finalCoords, capitalName)));
                }
            }
        }
        return ans;
    }

    /**
     * Method used to answer Extra Credit #1.
     * @param width the number of degrees of latitude and longitude we want the capitals to be
     *              within each other
     * @return A list of capitals and countries that fit within a square with a dimension of
     * width degrees.
     */
    public static ArrayList<ArrayList<String> > maximizeCaptials(int width) {
        if (width < 0) {
            return null;
        }
        ArrayList<ArrayList<String> > ans = new ArrayList<>();
        Double ansLatitude = 0.0, ansLongitude = 0.0;
        Map<String, ArrayList<String> > countries = getCoordinatesAndCapitals();
        Map<String, ArrayList<Double> > decimalCoords = new HashMap<>();

        for (Map.Entry<String, ArrayList<String> > e : countries.entrySet()) {
            String[] coords = e.getValue().get(0).split(", ");
            String[] latitudeString = coords[0].split(" ");
            String[] longitudeString = coords[1].split(" ");
            Double latitude, longitude;
            if (latitudeString[2].equals("N")) {
                latitude = Double.valueOf(latitudeString[0]) +
                        Double.valueOf(latitudeString[1]) / 60.0;
            } else {
                latitude = -Double.valueOf(latitudeString[0]) -
                        Double.valueOf(latitudeString[1]) / 60.0;
            }
            if (longitudeString[2].equals("E")) {
                longitude = Double.valueOf(longitudeString[0]) +
                        Double.valueOf(longitudeString[1]) / 60.0;
            } else {
                longitude = -Double.valueOf(longitudeString[0]) -
                        Double.valueOf(longitudeString[1]) / 60.0;
            }
            decimalCoords.put(e.getKey(), new ArrayList<>(Arrays.asList(latitude, longitude)));
        }

        Double precision = 10.0;
        for (Double r = -90.0; r <= 90.0; r += 1.0 / precision) {
            for (Double c = -180.0; c <= 180.0; c += 1.0 / precision) {
                Double oppr = r + width, oppc = c + width;
                ArrayList<ArrayList<String> > currentAns = new ArrayList<>();
                for (Map.Entry<String, ArrayList<Double> > e : decimalCoords.entrySet()) {
                    Double latitude = e.getValue().get(0), longitude = e.getValue().get(1);
                    if (latitude >= r && latitude <= oppr && longitude >= c && longitude <= oppc) {
                        currentAns.add(new ArrayList<>(
                                Arrays.asList(countries.get(e.getKey()).get(1), e.getKey())));
                    }
                }
                if (currentAns.size() > ans.size()) {
                    ansLatitude = r + (width / 2.0);
                    ansLongitude = c + (width / 2.0);
                    ans = currentAns;
                }
            }
        }
        String boxCoords = "";
        if (ansLatitude < 0) {
            boxCoords += (-ansLatitude + " S");
        } else {
            boxCoords += (ansLatitude + " N");
        }
        boxCoords += ", ";
        if (ansLongitude < 0) {
            boxCoords += (-ansLongitude + " W");
        } else {
            boxCoords += (ansLongitude + " E");
        }
        ans.add(new ArrayList<>(Arrays.asList(boxCoords)));
        return ans;
    }

    /**
     * Method used to answer EC2-1
     * @param region the region we are interested in
     * @return A map of URLs and the countries of the flags.
     */
    public static Map<String, String> getFlagUrls(String region) {
        Map<String, String> ans = new HashMap<>();
        region = region.toLowerCase();
        Map<String, ArrayList<ArrayList<String> > > countries = countriesByRegion();
        if (!countries.containsKey(region)) {
            return null;
        }

        for (ArrayList<String> country : countries.get(region)) {
            String name = country.get(0);
            String url = country.get(1);
            ArrayList<String> contents = fetchContents(url + "flag/");
            for (String line : contents) {
                Pattern patternFlag = Pattern.compile(
                        "image-detail-block-header-download p0\"><a href=\"/the-world-factbook/"
                );
                Matcher matcherFlag = patternFlag.matcher(line);
                if (matcherFlag.find()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = matcherFlag.end(); i < line.length(); ++i) {
                        if (line.charAt(i) == '\"') {
                            break;
                        } else {
                            sb.append(line.charAt(i));
                        }
                    }
                    System.out.println(rootUrl + sb + " " + name);
                    ans.put(rootUrl + sb.toString(), name);
                    break;
                }
            }
        }
        return ans;
    }
}