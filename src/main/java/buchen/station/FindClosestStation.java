package buchen.station;

import java.util.HashMap;
import java.util.Map;

public class FindClosestStation {
    public Map<String, Station> merge(CitiBikeService service) {
        Map<String, Station> stationsMap = new HashMap<>();

        try {
            Stations stationInfo = service.stationInfo().blockingGet();
            Stations stationStatus = service.stationStatus().blockingGet();

            if (stationInfo != null && stationInfo.data != null) {
                for (Station station : stationInfo.data.stations) {
                    stationsMap.put(station.station_id, new Station(
                            station.lat, station.lon, station.station_id, station.name,
                            0, 0, 0
                    ));
                }
            }

            if (stationStatus != null && stationStatus.data != null) {
                for (Station station : stationStatus.data.stations) {
                    Station existingStation = stationsMap.get(station.station_id);
                    if (existingStation != null) {
                        existingStation.num_docks_available = station.num_docks_available;
                        existingStation.num_bikes_available = station.num_bikes_available;
                        existingStation.num_ebikes_available = station.num_ebikes_available;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch or merge station data: " + e.getMessage());
        }

        return stationsMap;
    }

    public Station closestStationAvailableBikes(Map<String, Station> stationsMap, double latitude, double longitude) {
        Station closestStation = null;
        double closestDistance = Double.MAX_VALUE;

        for (Station station : stationsMap.values()) {

            if (station.num_bikes_available > 0 || station.num_ebikes_available > 0) {
                double distance = haversine(latitude, longitude, station.lat, station.lon);

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestStation = station;
                }
            }
        }

        return closestStation;
    }
    public Station closestStationAvailableSlots(Map<String, Station> stationsMap, double latitude, double longitude) {
        Station closestStation = null;
        double closestDistance = Double.MAX_VALUE;

        for (Station station : stationsMap.values()) {
            if (station.num_docks_available > 0) {
                double distance = haversine(latitude, longitude, station.lat, station.lon);

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestStation = station;
                }
            }
        }

        return closestStation;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double earthRadius = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double kilometerDistance = earthRadius * c;
        return kilometerDistance;
    }
}

