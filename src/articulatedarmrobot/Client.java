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

class Client extends JFrame implements ChangeListener {

    private int port = 64003;
    private String ip = "localhost";
//192.168.0.143
    
        // create a slider
    JSlider SliderWS = new JSlider(0,100);
    JSlider SliderAD = new JSlider(0,100);
    /*JButton w = new JButton("W");
    JButton s = new JButton("S");
    JButton a = new JButton("A");
    JButton d = new JButton("D");
    JButton i = new JButton("I");
    JButton j = new JButton("J");
    JButton k = new JButton("K");
    JButton l = new JButton("L");
    JButton b1 = new JButton("1");
    JButton b2 = new JButton("2");
    JButton b3 = new JButton("3");
    JButton b4 = new JButton("4");
    JButton b5 = new JButton("5");
    JButton b6 = new JButton("6");
    JButton b7 = new JButton("7");
    JButton b8 = new JButton("8");
    JButton b9 = new JButton("9");
    JButton b0 = new JButton("0");*/
    JButton r = new JButton("Przelacz na lokalne sterowanie");
    int poprzednia=0;
    int wynik=0;
    Socket clientSocket = new Socket(InetAddress.getByName(ip), port);
    ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());

    public Client() throws Exception{
        super("Zdalne sterowanie ramieniem robota");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(324, 248);
        setResizable(false);
        JPanel panel = new JPanel();
       TworzenieSlidera(SliderWS,panel,"WS");
       TworzenieSlidera(SliderAD,panel,"AD");
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
          if (!source.getValueIsAdjusting()) {
              int sleepSense = (int)source.getValue();
          if (source == SliderWS) {
            try {
                System.out.print(sleepSense+"\n");
                wynik=sleepSense-poprzednia;
                poprzednia=sleepSense;
                if(wynik>0){
                for(int i=0;i<wynik;i++){
                Thread.sleep(10);
                PrzekazanieZdalne('w');
                }
                }
                else{
                    for(int i=wynik;i<0;i++){
                Thread.sleep(10);
                PrzekazanieZdalne('s');
                }
                }
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            


    }
 }
  }
  public void PrzekazanieZdalne(char przekazanie) throws Exception {
        char sentence = przekazanie;
        outToServer.writeObject(sentence + "\n");
    }
  
}

        
 /*   public void actionPerformed(ActionEvent e) {
        Object zrodlo = e.getSource();
        
        if (zrodlo == w) {
            try {
                w();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == s) {
            try {
                s();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == a) {
            try {
                a();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == d) {
            try {
                d();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == i) {
            try {
                i();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == j) {
            try {
                j();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == k) {
            try {
                k();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == l) {
            try {
                l();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == b1) {
            try {
                b1();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == b2) {
            try {
                b2();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == b3) {
            try {
                b3();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == b4) {
            try {
                b4();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == b5) {
            try {
                b5();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == b6) {
            try {
                b6();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == b7) {
            try {
                b7();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == b8) {
            try {
                b8();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == b9) {
            try {
                b9();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == b0) {
            try {
                b0();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (zrodlo == r) {
            try {
                r();
                System.exit(0);
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
    }

    public void w() throws Exception {
        char sentence = 'w';
        outToServer.writeObject(sentence + "\n");
    }

    public void s() throws Exception {
        char sentence = 's';
        outToServer.writeObject(sentence + "\n");
    }

    public void a() throws Exception {
        char sentence = 'a';
        outToServer.writeObject(sentence + "\n");
    }

    public void d() throws Exception {
        char sentence = 'd';
        outToServer.writeObject(sentence + "\n");
    }

    public void i() throws Exception {
        char sentence = 'i';
        outToServer.writeObject(sentence + "\n");
    }

    public void j() throws Exception {
        char sentence = 'j';
        outToServer.writeObject(sentence + "\n");
    }

    public void k() throws Exception {
        char sentence = 'k';
        outToServer.writeObject(sentence + "\n");
    }
    
    public void m() throws Exception {
        char sentence = 'm';
        outToServer.writeObject(sentence + "\n");
    }

    public void l() throws Exception {
        char sentence = 'l';
        outToServer.writeObject(sentence + "\n");
    }

    public void b1() throws Exception {
        char sentence = '1';
        outToServer.writeObject(sentence + "\n");
    }

    public void b2() throws Exception {
        char sentence = '2';
        outToServer.writeObject(sentence + "\n");
    }

    public void b3() throws Exception {
        char sentence = '3';
        outToServer.writeObject(sentence + "\n");
    }

    public void b4() throws Exception {
        char sentence = '4';
        outToServer.writeObject(sentence + "\n");
    }

    public void b5() throws Exception {
        char sentence = '5';
        outToServer.writeObject(sentence + "\n");
    }

    public void b6() throws Exception {
        char sentence = '6';
        outToServer.writeObject(sentence + "\n");
    }

    public void b7() throws Exception {
        char sentence = '7';
        outToServer.writeObject(sentence + "\n");
    }

    public void b8() throws Exception {
        char sentence = '8';
        outToServer.writeObject(sentence + "\n");
    }

    public void b9() throws Exception {
        char sentence = '9';
        outToServer.writeObject(sentence + "\n");
    }

    public void b0() throws Exception {
        char sentence = '0';
        outToServer.writeObject(sentence + "\n");
    }

    public void r() throws Exception {
        char sentence = 'r';
        outToServer.writeObject(sentence + "\n");
    }

    public static void main(String argv[]) throws Exception {
        new Client();

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            try {
                w();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == 's') {
            try {
                s();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == 'a') {
            try {
                a();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == 'd') {
            try {
                d();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == '9') {
            try {
                b9();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == '8') {
            try {
                b8();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == '7') {
            try {
                b7();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == '6') {
            try {
                b6();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == '5') {
            try {
                b5();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == '4') {
            try {
                b4();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == '3') {
            try {
                b3();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == '2') {
            try {
                b2();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == '1') {
            try {
                b1();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == '0') {
            try {
                b0();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == 'i') {
            try {
                i();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == 'k') {
            try {
                k();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == 'l') {
            try {
                l();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == 'j') {
            try {
                j();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == 'r') {
            try {
                r();
                System.exit(0);
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (e.getKeyChar() == 'm') {
            try {
                m();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'r') {
            try {
                r();
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
*/