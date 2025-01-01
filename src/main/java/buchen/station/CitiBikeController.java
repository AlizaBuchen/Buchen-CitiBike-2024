package buchen.station;



import aws.CitiBikeRequest;
import aws.CitiBikeResponse;
import aws.Point;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lambda.LambdaService;
import lambda.LambdaServiceFactory;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;



import javax.swing.event.MouseInputListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class CitiBikeController {
    private JXMapViewer mapViewer;
    private BiConsumer<Double, Double> startPointDouble;
    private BiConsumer<Double, Double> endPointDouble;
    private boolean start = false;
    private GeoPosition startLocation;
    private GeoPosition endLocation;
    private final List<GeoPosition> track = new ArrayList<>();
    private Set<Waypoint> waypoints;

    public JXMapViewer getMap() {
        mapViewer = new JXMapViewer();

        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        tileFactory.setThreadPoolSize(8);

        mapViewer.setZoom(7);
        GeoPosition nyc = new GeoPosition(40.77228687788679, -73.9842939376831);
        mapViewer.setAddressLocation(nyc);


        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Point2D.Double point = new Point2D.Double(x, y);
                if (!start) {
                    startLocation = mapViewer.convertPointToGeoPosition(point);
                    startPointDouble.accept(startLocation.getLatitude(), startLocation.getLongitude());
                    start = true;
                } else {
                    endLocation = mapViewer.convertPointToGeoPosition(point);
                    endPointDouble.accept(endLocation.getLatitude(), endLocation.getLongitude());
                }
                updateWaypoints();
            }
        });
        return mapViewer;
    }

    public void showRoute() {
        CitiBikeResponse response = getLambda(writeToJson());
        Station startStation = response.start;
        Station endStation = response.end;

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        waypoints = Set.of(
                new DefaultWaypoint(startLocation),
                new DefaultWaypoint(endLocation),
                new DefaultWaypoint(startStation.lat, startStation.lon),
                new DefaultWaypoint(endStation.lat, endStation.lon)
        );
        waypointPainter.setWaypoints(waypoints);


        track.clear();
        track.add(startLocation);
        track.add(new GeoPosition(startStation.lat, startStation.lon));
        track.add(new GeoPosition(endStation.lat, endStation.lon));
        track.add(endLocation);


        RoutePainter routePainter = new RoutePainter(track);

        List<Painter<JXMapViewer>> painters = List.of(
                waypointPainter,
                routePainter
        );

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);

        mapViewer.zoomToBestFit(
                Set.of(
                        startLocation,
                        new GeoPosition(startStation.lat, startStation.lon),
                        new GeoPosition(endStation.lat, endStation.lon),
                        endLocation
                ),
                1.0
        );
        mapViewer.repaint();
    }

    public void setStartPoint(BiConsumer<Double, Double> listener) {
        this.startPointDouble = listener;
    }

    public void setEndPoint(BiConsumer<Double, Double> listener) {
        this.endPointDouble = listener;
    }

    private void updateWaypoints() {
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        Set<Waypoint> waypoints = new HashSet<>();

        if (startLocation != null) {
            waypoints.add(new DefaultWaypoint(startLocation));
        }
        if (endLocation != null) {
            waypoints.add(new DefaultWaypoint(endLocation));
        }
        waypointPainter.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(waypointPainter);
        mapViewer.repaint();
    }

    public void clear() {
        start = false;
        startLocation = null;
        endLocation = null;
        mapViewer.setOverlayPainter(null);
        mapViewer.repaint();
    }

    public CitiBikeRequest writeToJson() {
        Point from = new Point();
        from.lat = startLocation.getLatitude();
        from.lon = startLocation.getLongitude();

        Point to = new Point();
        to.lat = endLocation.getLatitude();
        to.lon = endLocation.getLongitude();

        CitiBikeRequest request = new CitiBikeRequest();
        request.from = from;
        request.to = to;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("request.json")) {
            gson.toJson(request, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request;
    }

    public CitiBikeResponse getLambda(CitiBikeRequest request) {
        CitiBikeResponse citibikeResponse = null;
        LambdaService api = new LambdaServiceFactory().getService();

        try {
            citibikeResponse = api.getClosestStations(request).blockingGet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return citibikeResponse;
    }
}
