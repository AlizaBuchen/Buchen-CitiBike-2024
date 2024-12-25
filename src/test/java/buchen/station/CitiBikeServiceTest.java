package buchen.station;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class CitiBikeServiceTest {

    @Test
    void stationInfo() {
        //Given
        CitiBikeService service = new CitiBikeServiceFactory().getService();

        //when
        Stations info = service.stationInfo().blockingGet();

        //then
        assertNotNull(info.data.stations[0].station_id);
        assertNotNull(info.data.stations[0].name);
        assertNotEquals(0.0, info.data.stations[0].lat, "Latitude should not be 0.0");
        assertNotEquals(0.0, info.data.stations[0].lon, "Longitude should not be 0.0");

    }

    @Test
    void stationStatus() {
        //Given
        CitiBikeService service = new CitiBikeServiceFactory().getService();

        //when
        Stations info = service.stationStatus().blockingGet();

        //then
        assertNotNull(info.data.stations[0].station_id);
        assertNotNull(info.data.stations[0].num_bikes_available);
        assertNotNull(info.data.stations[0].num_ebikes_available);
        assertNotNull(info.data.stations[0].num_docks_available);
    }
}
