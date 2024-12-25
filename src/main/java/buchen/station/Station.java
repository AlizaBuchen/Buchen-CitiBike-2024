package buchen.station;

public class Station {

    //station information
    double lat;
    public double lon;
    //CHECKSTYLE:OFF
    public String station_id;
    //CHECKSTYLE:ON

    public String name;

    //station status
    //CHECKSTYLE:OFF
    public int num_docks_available;
    public int num_ebikes_available;
    public int num_bikes_available;
    //CHECKSTYLE:ON




    public Station(double lat, double lon, String stationId, String name,
                   int numDocksAvailable, int numEbikesAvailable, int numBikesAvailable) {
        this.lat = lat;
        this.lon = lon;
        this.station_id = stationId;
        this.name = name;
        this.num_docks_available = numDocksAvailable;
        this.num_ebikes_available = numEbikesAvailable;
        this.num_bikes_available = numBikesAvailable;
    }
}