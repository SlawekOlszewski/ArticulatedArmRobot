package bird;



import java.applet.*;
import java.awt.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.behaviors.keyboard.*;

import com.sun.j3d.loaders.Scene;

import com.sun.j3d.loaders.objectfile.ObjectFile;

import java.io.*;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Mykeynavbeh extends Applet implements KeyListener {

 private SimpleUniverse universe = null;
 private Canvas3D canvas = null;
 private TransformGroup viewtrans = null;

 //private TransformGroup tg = null;
 private TransformGroup walec_glowny = null;
 private TransformGroup walec_srodek = null;
 private TransformGroup walec_gora = null;
 private TransformGroup kula_1 = null;
 private TransformGroup kula_2 = null;
 private TransformGroup ryst = null;
 private TransformGroup lapa_1 = null;
 private TransformGroup lapa_2 = null;
 private TransformGroup t_obrot_1 = null;
 private TransformGroup t_obrot_2 = null;
 private TransformGroup t_obrot_3 = null;
 private TransformGroup t_obrot_4 = null;
 private BranchGroup b_obrot_1 = null;
 private BranchGroup b_obrot_2 = null;
 private BranchGroup b_obrot_3 = null;
 private BranchGroup b_obrot_4 = null;
 private Transform3D t3d = null;
 private Transform3D  t3d_obrot_3   = new Transform3D();
 private Transform3D  t3d_obrot_4   = new Transform3D();
 private Transform3D t3dstep = new Transform3D();
 private Matrix4d matrix = new Matrix4d();
 
     private double kat=0, kat_licz=0;
    private final int ROTATE = 1;
    private int r;
    
    private volatile double t, k, speed;

 public Mykeynavbeh() {
  setLayout(new BorderLayout());
  GraphicsConfiguration config = SimpleUniverse
    .getPreferredConfiguration();

  canvas = new Canvas3D(config);
  add("Center", canvas);
  universe = new SimpleUniverse(canvas);

  BranchGroup scene = createSceneGraph();
  universe.getViewingPlatform().setNominalViewingTransform();

  universe.getViewer().getView().setBackClipDistance(100.0);

  canvas.addKeyListener(this);

  universe.addBranchGraph(scene);
 }

 private BranchGroup createSceneGraph() {
  BranchGroup objRoot = new BranchGroup();

  BoundingSphere bounds = new BoundingSphere(new Point3d(), 10000.0);

  viewtrans = universe.getViewingPlatform().getViewPlatformTransform();

  KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(viewtrans);
  keyNavBeh.setSchedulingBounds(bounds);
  PlatformGeometry platformGeom = new PlatformGeometry();
  platformGeom.addChild(keyNavBeh);
  universe.getViewingPlatform().setPlatformGeometry(platformGeom);

  AmbientLight lightA = new AmbientLight();
  lightA.setInfluencingBounds(bounds);
  objRoot.addChild(lightA);

  DirectionalLight lightD = new DirectionalLight();
  lightD.setInfluencingBounds(bounds);
  lightD.setDirection(new Vector3f(0.0f, 0.0f, -1.0f));
  lightD.setColor(new Color3f(1.0f, 1.0f, 1.0f));
  objRoot.addChild(lightD);
  
  DirectionalLight lightC = new DirectionalLight();
  lightC.setInfluencingBounds(bounds);
  lightC.setDirection(new Vector3f(0.0f, 0.0f, 1.0f));
  lightC.setColor(new Color3f(1.0f, 1.0f, 1.0f));
  objRoot.addChild(lightC);
  
  objRoot.addChild(createBird());

  return objRoot;
 }

 private BranchGroup createBird() {

  t3d_obrot_3 = new Transform3D();
  t3d_obrot_3.setTranslation(new Vector3f(-6.0f, 0.0f, 0.0f));
  
  t3d_obrot_4 = new Transform3D();
  t3d_obrot_4.setTranslation(new Vector3f(0.0f, -4.5f, 0.0f));
     
  BranchGroup objRoot = new BranchGroup();
  b_obrot_1 = new BranchGroup();
  b_obrot_2 = new BranchGroup();
  b_obrot_3 = new BranchGroup();
  b_obrot_4 = new BranchGroup();
  walec_glowny = new TransformGroup();
  walec_srodek = new TransformGroup();
  walec_gora = new TransformGroup();
  kula_1 = new TransformGroup();
  kula_2 = new TransformGroup();
  ryst = new TransformGroup();
  lapa_1 = new TransformGroup();
  lapa_2 =  new TransformGroup();
  t_obrot_1 =  new TransformGroup();
  t_obrot_2 =  new TransformGroup();
  t_obrot_3 =  new TransformGroup(t3d_obrot_3);
  t_obrot_4 =  new TransformGroup(t3d_obrot_4);
  t3d = new Transform3D();


  walec_glowny.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  walec_glowny.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  walec_glowny.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  walec_glowny.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  walec_glowny.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
  
  walec_srodek.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  walec_srodek.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  walec_srodek.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  walec_srodek.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  walec_srodek.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
  
  walec_gora.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  walec_gora.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  walec_gora.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  walec_gora.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  walec_gora.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
  
  kula_1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  kula_1.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  kula_1.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  kula_1.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  kula_1.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
  
  kula_2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  kula_2.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  kula_2.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  kula_2.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  kula_2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
  
  ryst.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  ryst.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  ryst.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  ryst.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  ryst.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
  
  lapa_1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  lapa_1.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  lapa_1.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  lapa_1.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  lapa_1.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
  
  lapa_2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  lapa_2.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  lapa_2.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  lapa_2.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  lapa_2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
  
  walec_gora.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  walec_gora.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  walec_gora.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  walec_gora.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  walec_gora.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
 
  b_obrot_1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  b_obrot_1.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  b_obrot_1.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  b_obrot_1.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  b_obrot_1.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
 
  t_obrot_1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  t_obrot_1.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  t_obrot_1.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  t_obrot_1.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  t_obrot_1.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
 
  b_obrot_2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  b_obrot_2.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  b_obrot_2.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  b_obrot_2.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  b_obrot_2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
 
  t_obrot_2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  t_obrot_2.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  t_obrot_2.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  t_obrot_2.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  t_obrot_2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
  
  b_obrot_3.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  b_obrot_3.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  b_obrot_3.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  b_obrot_3.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  b_obrot_3.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
 
  t_obrot_3.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  t_obrot_3.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  t_obrot_3.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  t_obrot_3.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  t_obrot_3.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
  
  b_obrot_4.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  b_obrot_4.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  b_obrot_4.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  b_obrot_4.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  b_obrot_4.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
 
  t_obrot_4.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  t_obrot_4.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
  t_obrot_4.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
  t_obrot_4.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
  t_obrot_4.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
 

  t3d.set(new Vector3d(0.0, -7.0, 0.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f));
  t3d.setScale(1.00);
  walec_glowny.setTransform(t3d);
  
  t3d.set(new Vector3f(0.0f,0.0f, 0.0f));
  t3d.setRotation(new AxisAngle4f(0.0f, 0.0f, 1.57f,(float) Math.PI/2));
  t3d.setScale(1.0);
  walec_srodek.setTransform(t3d);
  
  t3d.set(new Vector3f(0.0f,0.0f, 0.0f));
  t3d.setRotation(new AxisAngle4f(0.0f, 0.0f, 1.57f, (float) Math.PI));
  t3d.setScale(0.7);
  walec_gora.setTransform(t3d);
  
  t3d.set(new Vector3d(0.0, 0.0, 0.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f));
  t3d.setScale(2.0);
  kula_1.setTransform(t3d);
  
  t3d.set(new Vector3d(0.0, 0.0, 0.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f));
  t3d.setScale(1.5);
  kula_2.setTransform(t3d);
  
  t3d.set(new Vector3d(0.0, 0.0, 0.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f));
  t3d.setScale(0.9);
  ryst.setTransform(t3d);

  t3d.set(new Vector3d(-0.5, -1.2, 0.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f));
  t3d.setScale(0.6);
  lapa_1.setTransform(t3d);
  
  //lapa_2
  t3d.set(new Vector3d(0.5, -1.2, 0.0));
  //t3d.setRotation(new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f));
  t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, (float) Math.PI));
  t3d.setScale(0.6);
  lapa_2.setTransform(t3d);

  ObjectFile loader = new ObjectFile();
  Scene s_walec_glowny = null;
  Scene s_walec_srodek = null;
  Scene s_walec_gora = null;
  Scene s_kula_1 = null;
  Scene s_kula_2 = null;
  Scene s_ryst = null;
  Scene s_lapa_1 = null;
  Scene s_lapa_2 = null;


  File file = new java.io.File("model/bird_bl.obj");
  File file1 = new java.io.File("model/kula.obj");
  File file2 = new java.io.File("model/walec.obj");
  File file3 = new java.io.File("model/lapa.obj");

  try {
   s_walec_glowny = loader.load(file.toURI().toURL());
   s_walec_srodek = loader.load(file2.toURI().toURL());
   s_walec_gora = loader.load(file2.toURI().toURL());
   s_kula_1 = loader.load(file1.toURI().toURL());
   s_kula_2 = loader.load(file1.toURI().toURL());
   s_ryst = loader.load(file1.toURI().toURL());
   s_lapa_1 = loader.load(file3.toURI().toURL());
   s_lapa_2 = loader.load(file3.toURI().toURL());

  } catch (Exception e) {
   System.err.println(e);
   System.exit(1);
  }

  //podstawa
  walec_glowny.addChild(s_walec_glowny.getSceneGroup());
  
    //walec_srodek
  walec_srodek.addChild(s_walec_srodek.getSceneGroup());
 /* t3dstep.set(new Vector3d(0.0, 0.0, 0.0));
  walec_srodek.getTransform(t3d);
  t3d.mul(t3dstep);
  walec_srodek.setTransform(t3d);*/
  
  //walec_gora
  walec_gora.addChild(s_walec_gora.getSceneGroup());
  /*t3dstep.set(new Vector3d(0.0, 0.0, 0.0));
  walec_gora.getTransform(t3d);
  t3d.mul(t3dstep);
  walec_gora.setTransform(t3d);*/
  
  // kula_1
  kula_1.addChild(s_kula_1.getSceneGroup());
  /*t3dstep.set(new Vector3d(0.0, 6.0, 0.0));
  kula_1.getTransform(t3d);
  t3d.mul(t3dstep);
  kula_1.setTransform(t3d);*/
  
  //kula_2
  kula_2.addChild(s_kula_2.getSceneGroup());
  /*t3dstep.set(new Vector3d(0.0, 6.0, 0.0));
  kula_2.getTransform(t3d);
  t3d.mul(t3dstep);
  kula_2.setTransform(t3d);*/
  
  //ryst
  ryst.addChild(s_ryst.getSceneGroup());
  /*t3dstep.set(new Vector3d(0.0, 6.0, 0.0));
  ryst.getTransform(t3d);
  t3d.mul(t3dstep);
  ryst.setTransform(t3d);*/
  
  //lapa_1
  lapa_1.addChild(s_lapa_1.getSceneGroup());
 /* t3dstep.set(new Vector3d(0.0, 6.0, 0.0));
  lapa_1.getTransform(t3d);
  t3d.mul(t3dstep);
  lapa_1.setTransform(t3d);*/
  
  //lapa_2
  lapa_2.addChild(s_lapa_2.getSceneGroup());
  /*t3dstep.set(new Vector3d(0.0, 6.0, 0.0));
  lapa_2.getTransform(t3d);
  t3d.mul(t3dstep);
  lapa_2.setTransform(t3d);*/
 
  /*b_obrot_1.addChild(ryst);
  b_obrot_1.addChild(walec_srodek);
  b_obrot_1.addChild(walec_gora);*/
  b_obrot_1.addChild(walec_glowny);
  /*dupa.addChild(kula_1);
  dupa.addChild(kula_2);
  dupa.addChild(lapa_1);
  dupa.addChild(lapa_2);*/
  b_obrot_2.addChild(walec_srodek);
  b_obrot_2.addChild(kula_1);
  

  b_obrot_3.addChild(walec_gora);
  b_obrot_3.addChild(kula_2);
  
  b_obrot_4.addChild(lapa_1);
  b_obrot_4.addChild(lapa_2);
  b_obrot_4.addChild(ryst);
 

  
  t_obrot_1.addChild(b_obrot_1);
  t_obrot_2.addChild(b_obrot_2);
  t_obrot_3.addChild(b_obrot_3);
  t_obrot_4.addChild(b_obrot_4);
  t_obrot_3.addChild(t_obrot_4);
  t_obrot_2.addChild(t_obrot_3);
  t_obrot_1.addChild(t_obrot_2);

  /*objRoot.addChild(walec_glowny);
  //objRoot.addChild(walec_srodek);
  objRoot.addChild(walec_gora);
  objRoot.addChild(kula_1);
  objRoot.addChild(kula_2);
  //objRoot.addChild(ryst);
  objRoot.addChild(lapa_1);
  objRoot.addChild(lapa_2);*/
   
  //objRoot.addChild(dupa);
 objRoot.addChild(t_obrot_1);
 //objRoot.addChild(t_obrot_2);
  objRoot.compile();

  return objRoot;

 }

 /*private Light createLight() {
  DirectionalLight light = new DirectionalLight(true, new Color3f(1.0f,
    1.0f, 1.0f), new Vector3f(-0.3f, 0.2f, -1.0f));

  light.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));

  return light;
 }*/

 public static void main(String[] args) {
  Mykeynavbeh applet = new Mykeynavbeh();
  Frame frame = new MainFrame(applet, 800, 600);
 }

 public void keyTyped(KeyEvent e) {
  char key = e.getKeyChar();
/*
  if (key == 'a') {
   t3dstep.set(new Vector3d(0.0, 0.0, 0.1));
   tg.getTransform(t3d);
   t3d.mul(t3dstep);
   tg.setTransform(t3d);
  }*/

  if (key == 'a') {

   t3dstep.rotY(Math.PI / 32);
   t_obrot_1.getTransform(t3d);
   t3d.get(matrix);
   t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   t3d.mul(t3dstep);
   t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
   t_obrot_1.setTransform(t3d);

  }

  if (key == 'd') {

   t3dstep.rotY(-Math.PI / 32);
   t_obrot_1.getTransform(t3d);
   t3d.get(matrix);
   t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   t3d.mul(t3dstep);
   t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
   t_obrot_1.setTransform(t3d);

  }

  if (key == 'w') {
   t3dstep.rotZ(-Math.PI / 32);
   t_obrot_2.getTransform(t3d);
   //t3d.get(matrix);
   //t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   t3d.mul(t3dstep);
   t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
   t_obrot_2.setTransform(t3d);
                /*kat=speed*Math.atan(k*t);
             obrot2.setTranslation(new Vector3f(-rotx, -roty, 0.0f));
             obrot3.rotZ(kat);
             obrot3.mul(obrot2);
             obrot.setTranslation(new Vector3f(rotx, roty, 0.0f));
             obrot.mul(obrot3);
             kk.setTransform(obrot);
             
            kat_licz=kat_licz+kat;*/
  }

  if (key == 's') {
   t3dstep.rotZ(Math.PI / 32);
   t_obrot_2.getTransform(t3d);
   //t3d.get(matrix);
   //t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   t3d.mul(t3dstep);
   t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
   t_obrot_2.setTransform(t3d);
  }
  if (key == '8') {
   t3dstep.rotZ(-Math.PI / 32);
   t3d_obrot_3.mul(t3dstep);
   t_obrot_3.setTransform(t3d_obrot_3);
  }
    if (key == '2') {
   t3dstep.rotZ(Math.PI / 32);
   t3d_obrot_3.mul(t3dstep);
   t_obrot_3.setTransform(t3d_obrot_3);
  }
  if (key == '4') {
   t3dstep.rotY(Math.PI / 32);
   t3d_obrot_4.mul(t3dstep);
   t_obrot_4.setTransform(t3d_obrot_4);
  }

  if (key == '6') {
   t3dstep.rotY(-Math.PI / 32);
   t3d_obrot_4.mul(t3dstep);
   t_obrot_4.setTransform(t3d_obrot_4);
  }
    if (key == '1') {
   t3dstep.rotZ(-Math.PI / 32);
   t3d_obrot_4.mul(t3dstep);
   t_obrot_4.setTransform(t3d_obrot_4);
  }

  if (key == '9') {
   t3dstep.rotZ(Math.PI / 32);
   t3d_obrot_4.mul(t3dstep);
   t_obrot_4.setTransform(t3d_obrot_4);
  }
  
   if (key == '3') {
   t3dstep.rotX(-Math.PI / 32);
   t3d_obrot_4.mul(t3dstep);
   t_obrot_4.setTransform(t3d_obrot_4);
  }

  if (key == '7') {
   t3dstep.rotX(Math.PI / 32);
   t3d_obrot_4.mul(t3dstep);
   t_obrot_4.setTransform(t3d_obrot_4);
  }
 }

 public void keyReleased(KeyEvent e) {
 }

 public void keyPressed(KeyEvent e) {
 }
}