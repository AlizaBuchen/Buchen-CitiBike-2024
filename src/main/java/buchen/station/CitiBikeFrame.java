package buchen.station;

import java.awt.*;
import javax.swing.*;

public class CitiBikeFrame extends JFrame {
    public CitiBikeFrame() {
        setTitle("CitiBike");
        setSize(1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CitiBikeController controller = new CitiBikeController();
        add(controller.getMap(), BorderLayout.CENTER);


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
            controller.showRoute();
            repaint();
        });

        clear.addActionListener(e -> {
            controller.clear();
            startLocation.setText("");
            endLocation.setText("");
            repaint();
        });
    }
}