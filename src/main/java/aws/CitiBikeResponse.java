package aws;

import buchen.station.Station;

public class CitiBikeResponse {
    public Point from;
    public Point to;
    public Station start;
    public Station end;

    public CitiBikeResponse(Point from, Point to, Station start, Station end) {
        this.from = from;
        this.to = to;
        this.start = start;
        this.end = end;
    }
}
