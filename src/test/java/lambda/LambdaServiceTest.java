package lambda;

import aws.CitiBikeRequest;
import aws.CitiBikeResponse;
import aws.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LambdaServiceTest {

    @Test
    void getClosestStations() {

        Point fromLocation = new Point();
        fromLocation.lat = 40.8211;
        fromLocation.lon = -73.9359;

        Point toLocation = new Point();
        toLocation.lat = 40.719;
        toLocation.lon = -73.9585;

        CitiBikeRequest request = new CitiBikeRequest();
        request.from = fromLocation;
        request.to = toLocation;

        LambdaService service = new LambdaServiceFactory().getService();
        CitiBikeResponse response = service.getClosestStations(request).blockingGet();

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.from, "From location should not be null");
        assertNotNull(response.to, "To location should not be null");
        assertNotNull(response.start, "Start station should not be null");
        assertNotNull(response.end, "End station should not be null");
        assertNotNull(response.start.name, "Start station name should not be null");
        assertNotNull(response.end.name, "End station name should not be null");
        assertTrue(response.start.lat != 0, "Start station latitude should be valid");
        assertTrue(response.start.lon != 0, "Start station longitude should be valid");
        assertTrue(response.end.lat != 0, "End station latitude should be valid");
        assertTrue(response.end.lon != 0, "End station longitude should be valid");
    }
}
