Answers

1.

Algeria
Benin
Burkina Faso
Burundi
Cameroon
Central African Republic
Congo, Republic of the
Djibouti
Egypt
Equatorial Guinea
Eritrea
Ethiopia
Gambia, The
Ghana
Guinea
Guinea-Bissau
Kenya
Libya
Madagascar
Malawi
Mauritania
Mauritius
Morocco
Mozambique
Namibia
Sao Tome and Principe
Senegal
Seychelles
South Africa
South Sudan
Sudan
Togo
Zambia
Zimbabwe
Afghanistan
Bangladesh
Maldives
Belize
Dominica
Grenada
Guatemala
Saint Kitts and Nevis
Azerbaijan
Iran
Iraq
Kuwait
Lebanon
Oman
United Arab Emirates
Yemen
Bermuda
Mexico
Saint Pierre and Miquelon
Bolivia
Guyana
Paraguay
Suriname
Vanuatu
Tajikistan
Turkmenistan
Uzbekistan
Belarus
Hungary
Ireland
Lithuania
Moldova
Montenegro
Portugal

My algorithm first collects a list containing all the countries and their URLs using the countriesByRegion() method. 
Then, for each country, I find the flag description section in the page and extract the entire paragraph that describes
the flag. If the paragraph contains both colors as words, then I add the country to the output list. This method
can produce false positives such as Ireland because it is possible for the paragraph to talk about things unrelated
to the flag.

2. Puerto Rico Trench

My algorithm first finds the URL to the ocean's page from home in the Oceans section. Then, it reads the contents of
the page and finds where it mentions the lowest point. From there, we just read the string until it ends and return
that.

3. South Africa

My algorithm gets a list of countries in each region using the countriesByRegion() method. Then, it isolates the
countries in the region of interest, and finds where in each country description page electricity production is
mentioned. Then, it parses the words after "Electricity - production," such as converting strings to doubles and
million/billion to a numerical value. Finally, we update the maximum and return it after we are done.

4. Monaco (ratio of 2.05).

My algorithm gets a list of countries in each region using the countriesByRegion() method. Then, it isolates the
countries in the region of interest, and in each country's description page, it gets the value for coastline and
land area. From there, it calculates the ratio and updates the maximum so far. Return the country with this maximum
at the end.

5. 18,307,925 (Chile with a mean elevation of 1871).

My algorithm gets a list of countries in each region using the countriesByRegion() method. Then, it isolates the
countries in the region of interest, and in country's description page, it gets the value for mean elevation and
population. As we go through each country, it stores the maximum mean elevation and the corresponding population.
At the end, we return this population.

6. The United States, China, and Turkey are the Import Partners for Haiti.

To find countries in the Caribbean, we go through countries in Central America and read their Geography Location
section. If the first word is Caribbean, we can determine that the country is in the Caribbean. After we get this
list of countries, we go through each country's description page, and fetch the value for the total area. We store
the total area as well as the country in a list and sort by the total area in descending order. Finally, we grab all
the import partners of the third largest country and return them.

7.

Dhekelia
Dominica
Djibouti
Denmark
Dominican Republic

My algorithm goes through the list of all the countries from countriesByRegion() and isolates the ones that start
with the specified letter. Then, for each country, we grab the total area and add the total area as well as the
country to a list. We sort the list according to the total area in ascending order and we return just the countries.

8. My question is which country in *Africa* has the largest external debt to population ratio? The answer is Mauritius
with a debt to population ratio of $163,620.41 per person.

My algorithm isolates the countries according to the specified region using the countriesByRegion() method and goes
through each country's description page. It extracts the external debt and population values and calculates the
ratios. We keep track of the largest ratio so far and return the associated country.

EC 1.

20 capitals
[Plymouth, Montserrat]
[Charlotte Amalie, Virgin Islands]
[Gustavia, Saint Barthelemy]
[Basseterre, Saint Kitts and Nevis]
[San Juan, Puerto Rico]
[Oranjestad, Aruba]
[Marigot, Saint Martin]
[Castries, Saint Lucia]
[Saint John's, Antigua and Barbuda]
[Philipsburg, Sint Maarten]
[Road Town, British Virgin Islands]
[Roseau, Dominica]
[Saint George's, Grenada]
[The Valley, Anguilla]
[Port of Spain, Trinidad and Tobago]
[Caracas, Venezuela]
[Willemstad, Curacao]
[Kingstown, Saint Vincent and the Grenadines]
[Santo Domingo, Dominican Republic]

Box centered around: [13.499999999999151 N, 65.9000000000062 W]

My algorithm first extracts all the capitals and their coordinates by going through all the countries and looking
in their description pages. We convert each coordinate of a capital to a decimal, so we can compare coordinates
easily. Then, starting from -90 degrees longitude and -180 degrees latitude, we create a box and go through all the
capitals and find the ones that are inside. If the number of capitals in the box are larger than what we have so far,
we update the capitals and the coordinates of the box. We move the box 0.1 degrees each time until we end up at the
upper right of the world. Finally, we return the capitals and coordinates of the box.

EC 2. An example collage and cheatsheet are in flagcollage.png and cheatsheet.csv respectively. My algorithm works by
isolating the countries in the specified region through the countriesByRegion() method. Then, it goes to each country's
flag description page and finds the flag URL using regular expressions. It populates a map that associates the URLs
with the country names. Then, it writes this data to flagURL.csv. In task 2, it goes through flagURL.csv, and for each
country, it downloads the flag and puts it in the /files/ directory. The name of the file is *name of the country*.jpg.
In task 3, we first make each flag image 100 x 80 by shrinking them and adding whitespace if necessary. We add these
images to a list as well as their country names. Then, we create a large image for the collage with dimensions
calculated from the number of images. Finally, we write each flag in the list to the large image row-by-row until we
fill it up. To create cheatsheet.csv, as we are writing to the collage, we add to the csv, adding in new lines when
we switch rows.


Directions

To run the program for a certain problem, run Parser.java and enter the problem number when the terminal prompts you.
To run EC1, enter 9 and to run EC2, enter 10. Then, enter your choice of the italicized/underlined string. For EC2,
the program prompts you to enter a specific region to display the flags from.

To tell if the program is running properly, I had the program print out helpful things like what it is
currently processing.


Assumptions

1. I only consider countries accessible from the links in "The World & Its Regions" section of the home page.
Of course, I ignore the countries/territories without flags. Also, I assume that the input will be valid colors.
Finally, my metric for determining a flag's color is by looking in the flag description section and checking if 
the color is a word in there.

2. I assume that the input is one of the five oceans with correct spacing. If it is not, I return a null string.
The input is not case-sensitive.

3. I assume that the input is one of the regions from this list: Africa, Australia and Oceania, Central America,
Central Asia, East and Southeast Asia, Europe, Middle East, North America, South America, or South Asia. If it is not,
I return null. Also, I assume that countries produce a maximum of 999 trillion kWh of electricity. Finally, I assume
that the EU is a country. The input is not case-sensitive.

4. I assume that the input is one of the regions from this list: Africa, Australia and Oceania, Central America,
Central Asia, East and Southeast Asia, Europe, Middle East, North America, South America, or South Asia. If it is not,
I return null. Also, if a value is presented as "NA," I set the value to 0. For islands with multiple coastlines, I
take the sum of each island's coastline length. Finally, I assume Sint Maarten, the Coral Sea Islands, the Spratly
Islands, the Falkland Islands, the British Indian Ocean Territory and Bermuda are individual countries. The input
is not case-sensitive.

5. I assume that the input is one of the regions from this list: Africa, Australia and Oceania, Central America,
Central Asia, East and Southeast Asia, Europe, Middle East, North America, South America, or South Asia. If it is not,
I return null. Also, if the mean elevation is not listed or population is invalid, I skip the country. Finally, I assume
Greenland is a country. The input is not case-sensitive.

6. I assume that the input is one of the regions from this list: The Caribbean, Africa, Australia and Oceania, Central
America, Central Asia, East and Southeast Asia, Europe, Middle East, North America, South America, or South Asia. If it
is not, I return null. Also, I identify that a country/territory is in the Caribbean by checking the first word in the
location description. I determined that this is the best metric by looking through islands in the Caribbean Sea, and most 
of them had Caribbean as the first word. The input is not case-sensitive.

7. I assume that the input is a valid English letter (not case-sensitive). If it is not, I return null. Also, I assume all 
territories are individual countries, so they will be included in the final list. 

8. I assume that the input is one of the regions from this list: Africa, Australia and Oceania, Central America, Central
Asia, East and Southeast Asia, Europe, Middle East, North America, South America, or South Asia. Also, I skip countries
without debt or population data (shows up as NA). The input is not case-sensitive.

EC 1. I assume that the input width is non-negative and I return null if otherwise. When moving my box, I assume
that it is sufficient to move 0.1 degrees in each direction at a time. This delta is small enough such that I
capture every box without making the program too slow. Also, I do not consider territories without capitals
such as Macau or the Paracel Islands. Finally, I return the coordinates where the box is centered around as the
last element of the list.

EC 2. I skip the countries without valid flag pages, such as Baker Island. I treat territories as countries, so
there may be duplicate flags in a single collage. Also, I assume that the input for part 1 is one of the regions from
the home page list: Africa, Australia and Oceania, Central America, Central Asia, East and Southeast Asia, Europe,
Middle East, North America, South America, or South Asia. When loading the countries to flagURL.csv, I remove all
commas from country names, so countries in cheatsheet.csv will also not have commas in them. Finally, I fixed the
size of each flag image to be 100 x 80 pixels. The input is not case-sensitive.


Difficulties

The main issues I experienced with this homework was getting good data that was consistent based on some rule.
Many problems contained a few edge case countries where my general rule for locating the data would break. Thus,
I had to write more code to check for these conditions and handle them accordingly. Testing for these cases and
handling them took a lot of time.
