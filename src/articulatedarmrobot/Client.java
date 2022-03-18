package articulatedarmrobot;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.*;
import java.awt.*;
import javax.swing.*;

class Client extends JFrame implements ChangeListener, ActionListener {

    private int port = 64003;
    private String ip = "localhost";
//192.168.0.143
    
        // create a slider
    JSlider SliderWS = new JSlider(0,100);
    JSlider SliderAD = new JSlider(0,100);
    JSlider SliderIK = new JSlider(0,100);
    JSlider SliderJL = new JSlider(0,100);
    JSlider Slider19 = new JSlider(0,100);
    JSlider Slider28 = new JSlider(0,100);
    JSlider Slider37 = new JSlider(0,100);
    JSlider Slider46 = new JSlider(0,100);
    JSlider Slider50 = new JSlider(0,100);

    JButton r = new JButton("Przelacz na lokalne sterowanie");
    int poprzednia=0;
    int wynik=0;
    Socket clientSocket = new Socket(InetAddress.getByName(ip), port);
    ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());

    public Client() throws Exception{
        super("Zdalne sterowanie ramieniem robota");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(324, 400);
        setResizable(false);
        JPanel panel = new JPanel();
       TworzenieSlidera(SliderWS,panel,"WS");
       TworzenieSlidera(SliderAD,panel,"AD");
       TworzenieSlidera(SliderIK,panel,"IJ");
       TworzenieSlidera(SliderJL,panel,"KL");
       TworzenieSlidera(Slider19,panel,"19");
       TworzenieSlidera(Slider28,panel,"28");
       TworzenieSlidera(Slider37,panel,"37");
       TworzenieSlidera(Slider46,panel,"46");
       TworzenieSlidera(Slider50,panel,"50");
       r.addActionListener(this);
       panel.add(r);
        setContentPane(panel);
        show();
    }

 public static void main(String argv[]) throws Exception {
        new Client();

    }
 public void TworzenieSlidera(JSlider nowy, JPanel panel,String nazwa){
 nowy.addChangeListener(this);
       nowy.setMajorTickSpacing(20);
        nowy.setMinorTickSpacing(5);
        nowy.setPaintTicks(true);
        nowy.setSnapToTicks(true);

        panel.add(nowy);
        panel.add(new JLabel(nazwa));
 }
 
  public void stateChanged(ChangeEvent e) {
      JSlider source = (JSlider)e.getSource();
          int sleepSense = (int)source.getValue();
          if (source == SliderWS)       
            Przesuwanie('w','s',sleepSense);
          if (source == SliderAD)         
            Przesuwanie('a','d',sleepSense);
          if (source == SliderIK)         
            Przesuwanie('i','k',sleepSense);
          if (source == SliderJL)         
            Przesuwanie('j','l',sleepSense);
          if (source == Slider19)       
            Przesuwanie('1','9',sleepSense);
          if (source == Slider28)         
            Przesuwanie('2','8',sleepSense);
          if (source == Slider37)         
            Przesuwanie('3','7',sleepSense);
          if (source == Slider46)         
            Przesuwanie('4','6',sleepSense);
          if (source == Slider50)         
            Przesuwanie('5','0',sleepSense);

  }
    public void Przesuwanie(char pierwsza,char druga,int sleepSense) {
        try {
                System.out.print(sleepSense+"\n");
                wynik=sleepSense-poprzednia;
                poprzednia=sleepSense;
                if(wynik>0){
                for(int i=0;i<wynik;i++){
                Thread.sleep(10);
                PrzekazanieZdalne(pierwsza);
                }
                }
                else{
                    for(int i=wynik;i<0;i++){
                Thread.sleep(10);
                PrzekazanieZdalne(druga);
                }
                }
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

  public void PrzekazanieZdalne(char przekazanie) throws Exception {
        char sentence = przekazanie;
        outToServer.writeObject(sentence + "\n");
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
//nie wiem czym jest m w starym kodzie
