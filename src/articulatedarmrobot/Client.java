package articulatedarmrobot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
/**
 * This file make gui with sliders to remote control robot from ArticulatedArmRobot.java file.
 * We have sliders to all 6-axis of robot arm. Sliders have parameters set to be compatible with axis of robot arm.
 * In gui we can find on button to disconnect remote control.
 * 
 * @author hubert
 */
class Client extends JFrame implements ChangeListener, ActionListener {
/**
 * Class to set sliders parameters as major and minor tick value, characters to description of slider and set before value of slider.
 */
    class LabelSlider {
        public final JLabel label;
        public final JSlider slider;
        public final char first;
        public char second;
        public int major = 20;
        public int minor = 1;
        public int before = 0;

        public LabelSlider(LabelSliderConfig lsConfig) throws Exception {
            this.first = lsConfig.labelText.toLowerCase().charAt(0); 
            this.second = lsConfig.labelText.toLowerCase().charAt(1);

            if (lsConfig.max == 2) {
                this.major = 1;
                this.minor = 1;
                this.second = 'M';
            }

            this.label = new JLabel(lsConfig.labelText);
            this.slider = new JSlider(lsConfig.min, lsConfig.max, lsConfig.value);
        }
    }
/**
 * Class to set all of sliders. To slider of gripper we use one "if" to set this special option with only three positions.
 */
    class LabelSliderConfig {

        public double divider = 0.703125;
        public final String labelText;
        public final int min;
        public final int max;
        public final int value;

        public LabelSliderConfig(int min, int max, int value, String labelText) throws Exception {
            this.labelText = labelText;
            if (max == 2) {
                divider = 1;
            }
            
            this.min = (int) (min / divider);
            this.max = (int) (max / divider);
            this.value = value;
        }
    }
    
    private final int port = 64003;
    private final String ip = "localhost";
    
    LabelSliderConfig lsConfigs[] = {new LabelSliderConfig(-180, 180, 0, "AD: 0"),
        new LabelSliderConfig(-23, 45, 0, "WS: 0"),
        new LabelSliderConfig(-60, 60, 0, "19: 0"),
        new LabelSliderConfig(-23, 45, 0, "28: 0"),
        new LabelSliderConfig(-60, 60, 0, "37: 0"),
        new LabelSliderConfig(-180, 180, 0, "46: 0"),
        new LabelSliderConfig(0, 2, 0, "mM: 0")
    };
    ArrayList<LabelSlider> LabelSliderList = new ArrayList();

    JButton r = new JButton("Przelacz na lokalne sterowanie");
    Socket clientSocket = new Socket(InetAddress.getByName(ip), port);
    ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
/**
 * Class constructor
 * @throws Exception 
 */
    public Client() throws Exception {
        super("Zdalne sterowanie ramieniem robota");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(324, 300);
        setResizable(false);
        JPanel panel = new JPanel();

        for (LabelSliderConfig lsConfig : lsConfigs) {
            LabelSlider ls = new LabelSlider(lsConfig);
            LabelSliderList.add(ls);
            CreateSlider(ls, panel);
        }

        r.addActionListener(this);
        panel.add(r);
        setContentPane(panel);
        show();
    }
/**
 * 
 * Main with only calls our constructor.
 * @param argv
 * @throws Exception 
 */
    public static void main(String argv[]) throws Exception {
        new Client();

    }
/**
 * This function create sliders using LabelSlider class and add them position in JPanel.
 * @param ls
 * @param panel 
 */
    private void CreateSlider(LabelSlider ls, JPanel panel) {
        JSlider slider = ls.slider;
        JLabel label = ls.label;
        slider.addChangeListener(this);
        slider.setMajorTickSpacing(ls.major);
        slider.setMinorTickSpacing(ls.minor);
        slider.setSnapToTicks(true);

        panel.add(slider);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
    }
/**
 * Function to check actually value of sliders.
 * @param e 
 */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        int sleepSense = (int) source.getValue();
        for (LabelSlider ls : LabelSliderList) {
            if (source == ls.slider) {
                Moving(ls, sleepSense);
            }
        }
    }
/**
 * Function to calculate value of sliders and send it to ArticulatedArmRobot.java open in our remmote connection.
 * It send parameters as char to host.
 * @param ls
 * @param sleepSense 
 */
    private void Moving(LabelSlider ls, int sleepSense) {
        try {
            ls.label.setText(ls.label.getText().split(": ")[0] + ": " + String.valueOf(sleepSense));
            int result = sleepSense - ls.before;
            ls.before = sleepSense;
            if (result > 0) {
                for (int i = 0; i < result; i++) {
                    Thread.sleep(1);
                    outToServer.writeObject(ls.first + "\n");
                }
            } else {
                for (int i = result; i < 0; i++) {
                    Thread.sleep(1);
                    outToServer.writeObject(ls.second + "\n");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
/**
 * Function to send to host information about exit from remote connection.
 * @param e 
 */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == r) {
            try {
                outToServer.writeObject('r' + "\n");
                System.exit(0);
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
