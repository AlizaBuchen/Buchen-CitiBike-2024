package buchen.station;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.Painter;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.function.BiConsumer;

public class CitiBikeController {
    private JXMapViewer mapViewer;
    boolean start = false;
    private GeoPosition startLocation;
    private GeoPosition endLocation;
    private Station startStation;
    private Station endStation;
    private BiConsumer<Double, Double> startPointDouble;
    private BiConsumer<Double, Double> endPointDouble;
    private final List<GeoPosition> track = new ArrayList<>();

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
                    } else
                    {
                        endLocation = mapViewer.convertPointToGeoPosition(point);
                        endPointDouble.accept(endLocation.getLatitude(), endLocation.getLongitude());
                    }
                updateWaypoints();
            }
        });
        return mapViewer;
    }

    public void showRoute() {
        CitiBikeServiceFactory factory = new CitiBikeServiceFactory();
        CitiBikeService service = factory.getService();
        FindClosestStation find = new FindClosestStation();
        Map<String, Station> stationsMap = find.merge(service);

        startStation = find.closestStationAvailableBikes(stationsMap,
                startLocation.getLatitude(), startLocation.getLongitude());
        endStation = find.closestStationAvailableSlots(stationsMap,
                endLocation.getLatitude(), endLocation.getLongitude());

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        Set<Waypoint> waypoints = Set.of(
                new DefaultWaypoint(startLocation),
                new DefaultWaypoint(endLocation),
                new DefaultWaypoint(new GeoPosition(startStation.lat, startStation.lon)),
                new DefaultWaypoint(new GeoPosition(endStation.lat, endStation.lon))
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

        CompoundPainter<JXMapViewer> compoundPainter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(compoundPainter);

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
        startStation = null;
        endStation = null;
        mapViewer.setOverlayPainter(null);
        mapViewer.repaint();
    }
}