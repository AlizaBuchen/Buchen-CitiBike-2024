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



    public Station(double lat, double lon, String station_id, String name,
                   int num_docks_available, int num_ebikes_available, int num_bikes_available) {
        this.lat = lat;
        this.lon = lon;
        this.station_id = station_id;
        this.name = name;
        this.num_docks_available = num_docks_available;
        this.num_ebikes_available = num_ebikes_available;
        this.num_bikes_available = num_bikes_available;
    }

}