package aws;

import buchen.station.CitiBikeService;
import buchen.station.CitiBikeServiceFactory;
import buchen.station.FindClosestStation;
import buchen.station.Station;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;

import java.util.Map;

public class CitiBikeRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, CitiBikeResponse> {

    @Override
    public CitiBikeResponse handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String body = event.getBody();
        Gson gson = new Gson();
        CitiBikeRequest request = gson.fromJson(body, CitiBikeRequest.class);

        CitiBikeServiceFactory factory = new CitiBikeServiceFactory();
        CitiBikeService service = factory.getService();
        FindClosestStation stationFinder = new FindClosestStation();
        Map<String, Station> stationsMap = stationFinder.merge(service);

        Station start = stationFinder.closestStationAvailableBikes(stationsMap, request.from.lat, request.from.lon);
        Station end = stationFinder.closestStationAvailableSlots(stationsMap, request.to.lat, request.to.lon);

        return new CitiBikeResponse(request.from, request.to, start, end);
    }
}