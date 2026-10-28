
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CampusMapFinder {

    // Maximum number of places and connections we can store
    static final int MAX_PLACES = 100;
    static final int MAX_ROADS = 100;

    // Stores the names of all places
    static String[] placeNames = new String[MAX_PLACES];
    static int placeCount = 0;

    // Stores the neighboring places for each place
    static String[][] neighborPlaces =
            new String[MAX_PLACES][MAX_ROADS];
    static int[] neighborCount = new int[MAX_PLACES];

    public static void main(String[] args) {

        String fileName = "campus_map.txt";

        System.out.println("---------------------------------");
        System.out.println("      CAMPUS MAP FINDER");
        System.out.println("--------------------------------");
        System.out.println("Loading map file...\n");

        int roadCount = loadMap(fileName);

        if (roadCount >= 0) {
            System.out.println("\nMap loaded successfully!");
            System.out.println("Places found: " + placeCount);
            System.out.println("Roads loaded: " + roadCount);

            showGraph();
        }
    }

    // Reads the map file and builds the graph
    public static int loadMap(String fileName) {

        int roadCount = 0;

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(fileName))) {

            String line;

            while ((line = reader.readLine()) != null) {

                // Ignore blank lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Separate the two place names
                String[] places = line.trim().split("\\s+");

                // Every line must contain two place names
                if (places.length != 2) {
                    System.out.println(
                            "Skipping invalid line: " + line);
                    continue;
                }

                String firstPlace = places[0];
                String secondPlace = places[1];

                // Add the places if they do not exist yet
                int firstPlaceIndex = addPlace(firstPlace);
                int secondPlaceIndex = addPlace(secondPlace);

                // Stop if the place array is full
                if (firstPlaceIndex == -1 ||
                        secondPlaceIndex == -1) {

                    System.out.println(
                            "Error: Place storage is full.");
                    return -1;
                }

                // Add the road in both directions
                boolean roadAdded =
                        addRoad(firstPlaceIndex, secondPlaceIndex);

                if (roadAdded) {
                    roadCount++;
                }
            }

        } catch (IOException e) {
            System.out.println("Error: Could not load the map file.");
            System.out.println("Details: " + e.getMessage());
            return -1;
        }

        return roadCount;
    }

    // Adds a place if it has not been stored before
    public static int addPlace(String placeName) {

        int placeIndex = findPlace(placeName);

        // Return the existing index if the place already exists
        if (placeIndex != -1) {
            return placeIndex;
        }

        // Check whether the place array is full
        if (placeCount >= MAX_PLACES) {
            return -1;
        }

        // Store the new place
        placeNames[placeCount] = placeName;

        placeCount++;

        return placeCount - 1;
    }

    // Finds a place and returns its index
    public static int findPlace(String placeName) {

        for (int i = 0; i < placeCount; i++) {

            if (placeNames[i].equalsIgnoreCase(placeName)) {
                return i;
            }
        }

        return -1;
    }

    // Checks whether two places are already connected
    public static boolean hasRoad(int placeIndex,
                                  String neighborName) {

        for (int i = 0; i < neighborCount[placeIndex]; i++) {

            if (neighborPlaces[placeIndex][i]
                    .equalsIgnoreCase(neighborName)) {
                return true;
            }
        }

        return false;
    }

    // Connects two places in both directions
    public static boolean addRoad(int firstPlaceIndex,
                                  int secondPlaceIndex) {

        String firstPlace = placeNames[firstPlaceIndex];
        String secondPlace = placeNames[secondPlaceIndex];

        // Avoid duplicate roads
        if (hasRoad(firstPlaceIndex, secondPlace)) {
            return false;
        }

        // Check whether either neighbor list is full
        if (neighborCount[firstPlaceIndex] >= MAX_ROADS ||
                neighborCount[secondPlaceIndex] >= MAX_ROADS) {

            System.out.println(
                    "Error: Too many connections for a place.");
            return false;
        }

        // Connect the first place to the second place
        neighborPlaces[firstPlaceIndex]
                [neighborCount[firstPlaceIndex]] = secondPlace;

        neighborCount[firstPlaceIndex]++;

        // Connect the second place back to the first place
        neighborPlaces[secondPlaceIndex]
                [neighborCount[secondPlaceIndex]] = firstPlace;

        neighborCount[secondPlaceIndex]++;

        return true;
    }

    // Displays every place and its neighbors
    public static void showGraph() {

        System.out.println("\n================================");
        System.out.println("       GRAPH CONNECTIONS");
        System.out.println("================================");

        for (int i = 0; i < placeCount; i++) {

            System.out.print(placeNames[i] + ": ");

            for (int j = 0; j < neighborCount[i]; j++) {

                System.out.print(neighborPlaces[i][j]);

                if (j < neighborCount[i] - 1) {
                    System.out.print(", ");
                }
            }

            System.out.println();
        }
    }
}
