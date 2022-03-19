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

class Client extends JFrame implements ChangeListener, ActionListener {

    class LabelSlider {

        public final JLabel label;
        public final JSlider slider;
        public final char pierwsza;
        public char druga;
        public int major = 20;
        public int minor = 1;

        public LabelSlider(LabelSliderConfig lsConfig) throws Exception {
            this.pierwsza = lsConfig.labelText.toLowerCase().charAt(0);
            this.druga = lsConfig.labelText.toLowerCase().charAt(1);
            
            if (lsConfig.max==2) {
                this.major = 1;
                this.minor = 1;
                this.druga = 'M';
            }
            
            this.label = new JLabel(lsConfig.labelText);
            this.slider = new JSlider(lsConfig.min, lsConfig.max, lsConfig.value);
        }
    }
    
    class LabelSliderConfig {
        public double dzielnik = 0.703125;
        public final String labelText;
        public final int min;
        public final int max;
        public final int value;
        
        
        public LabelSliderConfig(int min, int max, int value, String labelText) throws Exception {
            this.labelText = labelText;
            if (max==2){
                dzielnik = 1;
            }
            this.min = (int)(min / dzielnik);
            this.max = (int)(max / dzielnik);
            this.value = value;
        }
    }
    
    
    private int port = 64003;
    private String ip = "localhost";
//192.168.0.143
    
    
    
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
    int poprzednia = 0;
    int wynik = 0;
    Socket clientSocket = new Socket(InetAddress.getByName(ip), port);
    ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());

    public Client() throws Exception {
        super("Zdalne sterowanie ramieniem robota");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(324, 300);
        setResizable(false);
        JPanel panel = new JPanel();

        for (LabelSliderConfig lsConfig : lsConfigs) {
            LabelSlider ls = new LabelSlider(lsConfig);
            LabelSliderList.add(ls);
            TworzenieSlidera(ls, panel);
        }

        r.addActionListener(this);
        panel.add(r);
        setContentPane(panel);
        show();
    }

    public static void main(String argv[]) throws Exception {
        new Client();

    }

    public void TworzenieSlidera(LabelSlider ls, JPanel panel) {
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

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        int sleepSense = (int) source.getValue();
        for (LabelSlider ls : LabelSliderList) {
            if (source == ls.slider) {
                Przesuwanie(ls.pierwsza, ls.druga, ls.label, sleepSense);
            }
        }
    }

    public void Przesuwanie(char pierwsza, char druga, JLabel label, int sleepSense) {
        try {
            label.setText(label.getText().split(": ")[0] + ": " + String.valueOf(sleepSense));
            wynik = sleepSense - poprzednia;
            poprzednia = sleepSense;
            if (wynik > 0) {
                for (int i = 0; i < wynik; i++) {
                    Thread.sleep(1);
                    outToServer.writeObject(pierwsza + "\n");
                }
            } else {
                for (int i = wynik; i < 0; i++) {
                    Thread.sleep(1);
                    outToServer.writeObject(druga + "\n");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object zrodlo = e.getSource();

        if (zrodlo == r) {
            try {
                r();
                System.exit(0);
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
    }

    public void r() throws Exception {
        char sentence = 'r';
        outToServer.writeObject(sentence + "\n");
    }
}
