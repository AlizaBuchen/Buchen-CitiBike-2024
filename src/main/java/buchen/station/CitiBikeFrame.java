package buchen.station;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class CitiBikeFrame extends JFrame {

    public CitiBikeFrame() {
        setTitle("CitiBike");
        setSize(1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        CitiBikeComponent view = new CitiBikeComponent();
        CitiBikeController controller = new CitiBikeController(view);

        view.getMapViewer().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                controller.insertPoint(x, y);
                controller.updateWaypoints();
            }
        });

        add(view, BorderLayout.CENTER);

        JLabel start = new JLabel("Start: ");
        JLabel end = new JLabel("End: ");

        JTextField startLocation = new JTextField(30);
        JTextField endLocation = new JTextField(30);

        JPanel location = new JPanel();
        location.add(start, BorderLayout.NORTH);
        location.add(startLocation, BorderLayout.NORTH);
        location.add(end, BorderLayout.SOUTH);
        location.add(endLocation, BorderLayout.SOUTH);

        JButton directions = new JButton("Directions");
        JButton clear = new JButton("Clear");

        JPanel buttons = new JPanel();
        buttons.add(directions);
        buttons.add(clear);

        JPanel south = new JPanel();
        south.add(location, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

        controller.setStartPoint((lat, lon) -> startLocation.setText(String.format("%.6f, %.6f", lat, lon)));

        controller.setEndPoint((lat, lon) -> endLocation.setText(String.format("%.6f, %.6f", lat, lon)));

        directions.addActionListener(e -> {
            controller.closestStations();
            repaint();
        });


        clear.addActionListener(e -> {
            controller.clear();
            startLocation.setText("");
            endLocation.setText("");
            controller.setStart(false);
            repaint();
        });
    }
}