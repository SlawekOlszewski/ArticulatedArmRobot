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
 private Transform3D t3d = null;
 private Transform3D t3dstep = new Transform3D();
 private Matrix4d matrix = new Matrix4d();

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

  objRoot.addChild(createBird());

  return objRoot;
 }

 private BranchGroup createBird() {

  BranchGroup objRoot = new BranchGroup();
  walec_glowny = new TransformGroup();
  walec_srodek = new TransformGroup();
  walec_gora = new TransformGroup();
  kula_1 = new TransformGroup();
  kula_2 = new TransformGroup();
  ryst = new TransformGroup();
  lapa_1 = new TransformGroup();
  lapa_2 =  new TransformGroup();
  t3d = new Transform3D();

  walec_glowny.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  walec_srodek.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  walec_gora.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  kula_1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  kula_2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  ryst.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  lapa_2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  walec_gora.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

  t3d.setTranslation(new Vector3d(0.0, -5.0, -30.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f));
  t3d.setScale(1.00);
  walec_glowny.setTransform(t3d);
  
  t3d.setTranslation(new Vector3d(0.0, -10.0, -40.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f));
  t3d.setScale(2.0);
  kula_1.setTransform(t3d);
  
  t3d.setTranslation(new Vector3d(0.0, 2.0, -40.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 0.0f, 90.0f, -1.2f));
  t3d.setScale(1.0);
  walec_srodek.setTransform(t3d);

  ObjectFile loader = new ObjectFile();
  Scene s_walec_glowny = null;
  Scene s_kula_1 = null;
  Scene s_walec_srodek = null;

  File file = new java.io.File("model/bird_bl.obj");
  File file1 = new java.io.File("model/kula.obj");
  File file2 = new java.io.File("model/walec.obj");

  try {
   s_walec_glowny = loader.load(file.toURI().toURL());
   s_kula_1 = loader.load(file1.toURI().toURL());
   s_walec_srodek = loader.load(file2.toURI().toURL());
  } catch (Exception e) {
   System.err.println(e);
   System.exit(1);
  }


  walec_glowny.addChild(s_walec_glowny.getSceneGroup());
  kula_1.addChild(s_kula_1.getSceneGroup());
  t3dstep.set(new Vector3d(1.0, 6.0, 0.0));
  kula_1.getTransform(t3d);
  t3d.mul(t3dstep);
  kula_1.setTransform(t3d);
  
  walec_srodek.addChild(s_walec_srodek.getSceneGroup());
  t3dstep.set(new Vector3d(0.0, 0.0, 0.0));
  walec_srodek.getTransform(t3d);
  t3d.mul(t3dstep);
  walec_srodek.setTransform(t3d);
  
  objRoot.addChild(walec_glowny);
  objRoot.addChild(kula_1);
  objRoot.addChild(walec_srodek);
  objRoot.addChild(createLight());

  objRoot.compile();

  return objRoot;

 }

 private Light createLight() {
  DirectionalLight light = new DirectionalLight(true, new Color3f(1.0f,
    1.0f, 1.0f), new Vector3f(-0.3f, 0.2f, -1.0f));

  light.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));

  return light;
 }

 public static void main(String[] args) {
  Mykeynavbeh applet = new Mykeynavbeh();
  Frame frame = new MainFrame(applet, 800, 600);
 }

 public void keyTyped(KeyEvent e) {
  char key = e.getKeyChar();
/*
  if (key == 'e') {
   t3dstep.set(new Vector3d(0.0, 0.0, 0.1));
   tg.getTransform(t3d);
   t3d.mul(t3dstep);
   tg.setTransform(t3d);
  }

  if (key == 'y') {

   t3dstep.rotY(Math.PI / 32);
   tg.getTransform(t3d);
   t3d.get(matrix);
   t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   t3d.mul(t3dstep);
   t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
   tg.setTransform(t3d);

  }

  if (key == 'u') {

   t3dstep.rotY(-Math.PI / 32);
   tg.getTransform(t3d);
   t3d.get(matrix);
   t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   t3d.mul(t3dstep);
   t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
   tg.setTransform(t3d);

  }

  if (key == 's') {
   t3dstep.set(new Vector3d(0.0, 0.1, 0.0));
   tg.getTransform(t3d);
   t3d.mul(t3dstep);
   tg.setTransform(t3d);
  }

  if (key == 'd') {
   t3dstep.set(new Vector3d(0.0, -0.1, 0.0));
   tg.getTransform(t3d);
   t3d.mul(t3dstep);
   tg.setTransform(t3d);
  }
  if (key == 'w') {
   t3dstep.setScale(new Vector3d(0.5, 0.5, 0.5));
   tg.getTransform(t3d);
   t3d.mul(t3dstep);
   tg.setTransform(t3d);
  }
    if (key == 'q') {
   t3dstep.setScale(new Vector3d(1.5, 1.5, 1.5));
   tg.getTransform(t3d);
   t3d.mul(t3dstep);
   tg.setTransform(t3d);
  }
  if (key == 'x') {

   t3dstep.rotX(Math.PI / 32);
   tg.getTransform(t3d);
   t3d.get(matrix);
   t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   t3d.mul(t3dstep);
   t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
   tg.setTransform(t3d);

  }

  if (key == 'c') {

   t3dstep.rotX(-Math.PI / 32);
   tg.getTransform(t3d);
   t3d.get(matrix);
   t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   t3d.mul(t3dstep);
   t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
   tg.setTransform(t3d);

  }
    if (key == 'z') {

   t3dstep.rotZ(Math.PI / 32);
   tg.getTransform(t3d);
   t3d.get(matrix);
   t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   t3d.mul(t3dstep);
   t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
   tg.setTransform(t3d);

  }

  if (key == 'a') {

   t3dstep.rotZ(-Math.PI / 32);
   tg.getTransform(t3d);
   t3d.get(matrix);
   t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
   t3d.mul(t3dstep);
   t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
   tg.setTransform(t3d);

  }*/
 }

 public void keyReleased(KeyEvent e) {
 }

 public void keyPressed(KeyEvent e) {
 }
}