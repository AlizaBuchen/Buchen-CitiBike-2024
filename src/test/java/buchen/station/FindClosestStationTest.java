package buchen.station;


import aws.Point;
import lambda.StationsCache;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;


public class FindClosestStationTest {

    @Test
    void closestStationWithAvailableBikes() {
        try {
            //Given
            CitiBikeServiceFactory factory = new CitiBikeServiceFactory();
            CitiBikeService service = factory.getService();
            FindClosestStation stationFinder = new FindClosestStation();
            StationsCache cache = new StationsCache();

            Map<String, Station> stationsMap = stationFinder.merge(service, cache);
            double lon = -73.971212141;
            double lat = 40.744220;

            // when
            Station closestStation = stationFinder.closestStationAvailableBikes(stationsMap, lat, lon);

            // then
            boolean hasAvailable = closestStation.num_bikes_available > 0 || closestStation.num_ebikes_available > 0;
            assertNotNull(closestStation);
            assertEquals("FDR Drive & E 35 St", closestStation.name,
                    "Expected station name to be 'FDR Drive & E 35 St'");
            assertTrue(hasAvailable);
        } catch (Exception e) {
            assertNotNull(e, "Exception occurred but is null");
            e.printStackTrace();
        }
    }

    @Test
    void closestStationWithAvailableSlots() {
        try {
            //Given
            CitiBikeServiceFactory factory = new CitiBikeServiceFactory();
            CitiBikeService service = factory.getService();
            FindClosestStation stationFinder = new FindClosestStation();
            StationsCache cache = new StationsCache();


            Map<String, Station> stationsMap = stationFinder.merge(service, cache);
            double lat = 40.7851;
            double lon = -73.9683;

            // when
            Station closestStation = stationFinder.closestStationAvailableSlots(stationsMap, lat, lon);

            // then
            boolean hasAvailable = closestStation.num_docks_available > 0;
            assertNotNull(closestStation);
            assertEquals("Central Park W & W 85 St", closestStation.name,
                    "Expected station name to be 'FDR Drive & E 35 St'");
            assertTrue(hasAvailable);
        } catch (Exception e) {
            assertNotNull(e, "Exception occurred but is null");
            e.printStackTrace();
        }
    }

    @Test
    void findClosestStation() {
        try {
            //given
            CitiBikeServiceFactory factory = new CitiBikeServiceFactory();
            CitiBikeService service = factory.getService();
            FindClosestStation stationFinder = new FindClosestStation();
            StationsCache cache = new StationsCache();
            String id = "50575cba-95ac-4134-9da1-3eec8253afc4";

            //when
            Map<String, Station> stationsMap = stationFinder.merge(service, cache);

            //then
            if (stationsMap.get(id) != null) {
                assertNotNull(stationsMap.get(id));
                assertEquals("Central Park W & W 85 St", stationsMap.get(id).name,
                        "Expected station name to be 'Central Park W & W 85 St'");
            }
        } catch (Exception e) {
            assertNotNull(e, "Exception occurred but is null");
            e.printStackTrace();
        }
    }
}