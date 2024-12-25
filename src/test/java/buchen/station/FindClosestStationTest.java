package buchen.station;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;


public class FindClosestStationTest {

    @Test
    void testClosestStationWithAvailableBikes() {
        //Given
        CitiBikeServiceFactory factory = new CitiBikeServiceFactory();
        CitiBikeService service = factory.getService();
        FindClosestStation stationFinder = new FindClosestStation();

        Map<String, Station> stationsMap = stationFinder.merge(service);
        double lon = -73.971212141;
        double lat = 40.744220;

        // when
        Station closestStation = stationFinder.closestStationAvailableBikes(stationsMap, lat, lon);
        boolean hasAvailable = closestStation.num_bikes_available > 0 || closestStation.num_ebikes_available > 0;

        // then
        assertNotNull(closestStation);
        assertEquals("FDR Drive & E 35 St", closestStation.name, "Expected station name to be 'FDR Drive & E 35 St'");
        assertTrue(hasAvailable);
    }

    @Test
    void testClosestStationWithAvailableSlots() {
        //Given
        CitiBikeServiceFactory factory = new CitiBikeServiceFactory();
        CitiBikeService service = factory.getService();
        FindClosestStation stationFinder = new FindClosestStation();

        Map<String, Station> stationsMap = stationFinder.merge(service);
        double lat = 40.7851;
        double lon = -73.9683;

        // when
        Station closestStation = stationFinder.closestStationAvailableSlots(stationsMap, lat, lon);
        boolean hasAvailable = closestStation.num_docks_available > 0;

        // then
        assertNotNull(closestStation);
        assertEquals("Central Park W & W 85 St", closestStation.name,
                "Expected station name to be 'FDR Drive & E 35 St'");
        assertTrue(hasAvailable);
    }

    @Test
    void findClosestStation() {
        //given
        CitiBikeServiceFactory factory = new CitiBikeServiceFactory();
        CitiBikeService service = factory.getService();
        FindClosestStation stationFinder = new FindClosestStation();
        String id = "50575cba-95ac-4134-9da1-3eec8253afc4";

        //when
        Map<String, Station> stationsMap = stationFinder.merge(service);

        //then
        assertNotNull(stationsMap.get(id));
        assertEquals("Central Park W & W 85 St", stationsMap.get(id).name,
                "Expected station name to be 'Central Park W & W 85 St'");
    }
}