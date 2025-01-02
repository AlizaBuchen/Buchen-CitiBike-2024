package buchen.station;

import aws.CitiBikeRequest;
import aws.Point;
import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lambda.LambdaService;
import lambda.LambdaServiceFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;


public class CitiBikeController {
    private BiConsumer<Double, Double> startPointDouble;
    private BiConsumer<Double, Double> endPointDouble;
    private boolean start = false;
    private GeoPosition startLocation;
    private GeoPosition endLocation;
    private GeoPosition startStationGeo;
    private GeoPosition endStationGeo;
    private final List<GeoPosition> track = new ArrayList<>();
    private Set<Waypoint> waypoints;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final CitiBikeComponent view;

    public CitiBikeController(CitiBikeComponent view) {
        this.view = view;
    }

    public void closestStations() {

        Point from = new Point();
        from.lat = startLocation.getLatitude();
        from.lon = startLocation.getLongitude();

        Point to = new Point();
        to.lat = endLocation.getLatitude();
        to.lon = endLocation.getLongitude();

        CitiBikeRequest request = new CitiBikeRequest();
        request.from = from;
        request.to = to;

        LambdaService service = new LambdaServiceFactory().getService();
        disposables.add(service.getClosestStations(request)
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .subscribe(
                        response -> {
                            if (response.start != null && response.end != null) {
                                startStationGeo = new GeoPosition(response.start.lat, response.start.lon);
                                endStationGeo = new GeoPosition(response.end.lat, response.end.lon);
                                showRoute();
                            }
                        },
                        Throwable::printStackTrace
                ));
    }

    public void showRoute() {

        waypoints = Set.of(
                new DefaultWaypoint(startLocation),
                new DefaultWaypoint(endLocation),
                new DefaultWaypoint(startStationGeo),
                new DefaultWaypoint(endStationGeo)
        );

        track.clear();
        track.add(startLocation);
        track.add(startStationGeo);
        track.add(endStationGeo);
        track.add(endLocation);

        RoutePainter routePainter = new RoutePainter(track);

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        List<Painter<JXMapViewer>> painters = List.of(
                waypointPainter,
                routePainter
        );

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        view.getMapViewer().setOverlayPainter(painter);

        view.getMapViewer().zoomToBestFit(
                Set.of(
                        startLocation,
                        startStationGeo,
                        endStationGeo,
                        endLocation
                ),
                1.0
        );
        view.getMapViewer().repaint();
    }

    public void setStartPoint(BiConsumer<Double, Double> listener) {
        this.startPointDouble = listener;
    }

    public void setEndPoint(BiConsumer<Double, Double> listener) {
        this.endPointDouble = listener;
    }

    void updateWaypoints() {
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        Set<Waypoint> waypoints = new HashSet<>();

        if (startLocation != null) {
            waypoints.add(new DefaultWaypoint(startLocation));
        }
        if (endLocation != null) {
            waypoints.add(new DefaultWaypoint(endLocation));
        }
        waypointPainter.setWaypoints(waypoints);
        view.getMapViewer().setOverlayPainter(waypointPainter);
        view.getMapViewer().repaint();
    }

    public void clear() {
        start = false;
        startLocation = null;
        endLocation = null;
        view.getMapViewer().setOverlayPainter(null);
        view.getMapViewer().repaint();
    }

    public void setStartLocation(int x, int y) {
        Point2D.Double point = new Point2D.Double(x, y);
        startLocation = view.getMapViewer().convertPointToGeoPosition(point);
        startPointDouble.accept(startLocation.getLatitude(), startLocation.getLongitude());
    }

    public void setEndLocation(int x, int y) {
        Point2D.Double point = new Point2D.Double(x, y);
        endLocation = view.getMapViewer().convertPointToGeoPosition(point);
        endPointDouble.accept(endLocation.getLatitude(), endLocation.getLongitude());
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean canStart() {
        return start;
    }
}
