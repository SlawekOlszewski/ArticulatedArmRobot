package bird;

/* Mykeynavbeh.java
 * Originally based on code from BackgroundApp.java
 *      @(#)BackgroundApp.java 1.1 00/09/22 14:03
 *
 * portions Copyright (c) 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

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

 private TransformGroup tg = null;
 private TransformGroup kula1 = null;
 private TransformGroup walec1 = null;
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
  tg = new TransformGroup();
  kula1 = new TransformGroup();
  walec1 = new TransformGroup();
  t3d = new Transform3D();

  tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  kula1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  walec1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

  t3d.setTranslation(new Vector3d(0.0, -5.0, -30.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f));
  t3d.setScale(1.00);

  tg.setTransform(t3d);
  t3d.setTranslation(new Vector3d(0.0, -10.0, -40.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f));
  t3d.setScale(2.0);
  kula1.setTransform(t3d);
  
  t3d.setTranslation(new Vector3d(0.0, 2.0, -40.0));
  t3d.setRotation(new AxisAngle4f(0.0f, 0.0f, 90.0f, -1.2f));
  t3d.setScale(1.0);
  walec1.setTransform(t3d);

  ObjectFile loader = new ObjectFile();
  Scene s = null;
  Scene s1 = null;
  Scene s2 = null;

  File file = new java.io.File("model/bird_bl.obj");
  File file1 = new java.io.File("model/kula.obj");
  File file2 = new java.io.File("model/walec.obj");

  try {
   s = loader.load(file.toURI().toURL());
   s1 = loader.load(file1.toURI().toURL());
   s2 = loader.load(file2.toURI().toURL());
  } catch (Exception e) {
   System.err.println(e);
   System.exit(1);
  }


  tg.addChild(s.getSceneGroup());
  kula1.addChild(s1.getSceneGroup());
   t3dstep.set(new Vector3d(0.0, 6.0, 0.0));
   kula1.getTransform(t3d);
   t3d.mul(t3dstep);
   kula1.setTransform(t3d);
  walec1.addChild(s2.getSceneGroup());
   t3dstep.set(new Vector3d(0.0, 0.0, 0.0));
   walec1.getTransform(t3d);
   t3d.mul(t3dstep);
   walec1.setTransform(t3d);
  objRoot.addChild(tg);
  objRoot.addChild(kula1);
  objRoot.addChild(walec1);
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

  }
 }

 public void keyReleased(KeyEvent e) {
 }

 public void keyPressed(KeyEvent e) {
 }
}