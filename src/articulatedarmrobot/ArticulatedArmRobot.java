package articulatedarmrobot;

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
import com.sun.tools.doclint.Entity;

import com.sun.j3d.utils.geometry.ColorCube;

import java.io.*;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.objects.Global;

import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import java.util.*;

public class ArticulatedArmRobot extends Applet implements KeyListener {

    private int port = 64003;

    private SimpleUniverse universe = null;
    private Canvas3D canvas = null;
    private TransformGroup viewtrans = null;

    private ColorCube cubek = new ColorCube();

    //private TransformGroup tg = null;
    private BoundingSphere bounds = null;
    private TransformGroup tg = null;
    private TransformGroup walec_glowny = null;
    private TransformGroup walec_srodek = null;
    private TransformGroup walec_gora = null;
    private TransformGroup kula_1 = null;
    private TransformGroup kula_2 = null;
    private TransformGroup ryst = null;
    private TransformGroup lapa_1 = null;
    private TransformGroup lapa_2 = null;
    private TransformGroup podloga = null;
    private TransformGroup szescian = null;
    private TransformGroup tlo = null;
    private TransformGroup t_obrot_1 = null;
    private TransformGroup t_obrot_2 = null;
    private TransformGroup t_obrot_3 = null;
    private TransformGroup t_obrot_4 = null;
    private TransformGroup t_obrot_5 = null;
    private TransformGroup t_obrot_6 = null;
    private BranchGroup b_obrot_1 = null;
    private BranchGroup b_obrot_2 = null;
    private BranchGroup b_obrot_3 = null;
    private BranchGroup b_obrot_4 = null;
    private BranchGroup b_obrot_5 = null;
    private BranchGroup b_obrot_6 = null;
    private Transform3D t3d = null;
    private Transform3D t3d_podloga = new Transform3D();
    private Transform3D t3d_tlo = new Transform3D();
    private Transform3D t3d_szescian = new Transform3D();
    private Transform3D t3d_obrot_3 = new Transform3D();
    private Transform3D t3d_obrot_4 = new Transform3D();
    private Transform3D t3d_obrot_5 = new Transform3D();
    private Transform3D t3d_obrot_6 = new Transform3D();
    private Transform3D t3dstep = new Transform3D();
    private Matrix4d matrix = new Matrix4d();
    private boolean remote = false;
    private ServerSocket welcomeSocket = null;
    private Socket connectionSocket = null;

    private double kat_1 = 0;
    private double kat_2 = 0;
    private double kat_3 = 0;
    private double kat_4 = 0;
    private double kat_5 = 0;
    private double kat_6 = 0;
    private double kat_7 = 0;
    private double kat_8 = 0;
    private double kat_9 = 0;
    private double kat_10 = 0;

    private double dzielnik = 256;
    private Vector3f wektor_1 = new Vector3f();
    private Vector3f wektor_2 = new Vector3f();

    private boolean stop = false;
    private boolean czy_byl_klik = false;
    private boolean czy_podloga = false;
    private boolean nagrywanie = false;
    private boolean odtwarzanie = false;
    private StringBuilder ruchy;
    private StringBuilder powrotne;

    public ArticulatedArmRobot() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse
                .getPreferredConfiguration();

        canvas = new Canvas3D(config);
        add("Center", canvas);
        universe = new SimpleUniverse(canvas);

        BranchGroup scene = createSceneGraph();
        universe.getViewingPlatform().setNominalViewingTransform();

        universe.getViewer().getView().setBackClipDistance(100);

        canvas.addKeyListener(this);

        universe.addBranchGraph(scene);
    }

    private BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();

        bounds = new BoundingSphere(new Point3d(), 10000.0);

        viewtrans = universe.getViewingPlatform().getViewPlatformTransform();

        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(viewtrans);
        keyNavBeh.setSchedulingBounds(bounds);
        PlatformGeometry platformGeom = new PlatformGeometry();
        platformGeom.addChild(keyNavBeh);
        universe.getViewingPlatform().setPlatformGeometry(platformGeom);

        Background background = new Background();
        BoundingSphere sphere = new BoundingSphere(new Point3d(0, 0, 0), 100000);
        background.setApplicationBounds(sphere);
        objRoot.addChild(background);

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

        objRoot.addChild(createArm());

        return objRoot;
    }

    private BranchGroup createArm() {

        t3d_podloga = new Transform3D();
        t3d_podloga.setTranslation(new Vector3f(0.0f, -7.0f, 0.0f));
        t3d_podloga.setScale(100);

        t3d_tlo = new Transform3D();
        t3d_tlo.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
        t3d_tlo.setRotation(new AxisAngle4f(2.0f, 0.0f, 0.0f, 0.0f));
        t3d_tlo.setScale(100);

        t3d_szescian = new Transform3D();
        t3d_szescian.setTranslation(new Vector3f(-7.0f, -6.0f, 0.0f));

        t3d_obrot_3 = new Transform3D();
        t3d_obrot_3.setTranslation(new Vector3f(-6.0f, 0.0f, 0.0f));

        t3d_obrot_4 = new Transform3D();
        t3d_obrot_4.setTranslation(new Vector3f(0.0f, -4.5f, 0.0f));

        t3d_obrot_5 = new Transform3D();
        t3d_obrot_5.setTranslation(new Vector3f(-0.1f, 0.1f, 0.0f));

        t3d_obrot_6 = new Transform3D();
        t3d_obrot_6.setTranslation(new Vector3f(0.1f, 0.1f, 0.0f));

        cubek = new ColorCube(0.3);

        BranchGroup objRoot = new BranchGroup();
        b_obrot_1 = new BranchGroup();
        b_obrot_2 = new BranchGroup();
        b_obrot_3 = new BranchGroup();
        b_obrot_4 = new BranchGroup();
        b_obrot_5 = new BranchGroup();
        b_obrot_6 = new BranchGroup();
        tg = new TransformGroup();
        walec_glowny = new TransformGroup();
        walec_srodek = new TransformGroup();
        walec_gora = new TransformGroup();
        kula_1 = new TransformGroup();
        kula_2 = new TransformGroup();
        ryst = new TransformGroup();
        lapa_1 = new TransformGroup();
        lapa_2 = new TransformGroup();
        podloga = new TransformGroup(t3d_podloga);
        tlo = new TransformGroup(t3d_tlo);
        szescian = new TransformGroup(t3d_szescian);
        t_obrot_1 = new TransformGroup();
        t_obrot_2 = new TransformGroup();
        t_obrot_3 = new TransformGroup(t3d_obrot_3);
        t_obrot_4 = new TransformGroup(t3d_obrot_4);
        t_obrot_5 = new TransformGroup(t3d_obrot_5);
        t_obrot_6 = new TransformGroup(t3d_obrot_6);
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

        b_obrot_5.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        b_obrot_5.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        b_obrot_5.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        b_obrot_5.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        b_obrot_5.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        t_obrot_5.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        t_obrot_5.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        t_obrot_5.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        t_obrot_5.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        t_obrot_5.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        b_obrot_6.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        b_obrot_6.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        b_obrot_6.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        b_obrot_6.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        b_obrot_6.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        t_obrot_6.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        t_obrot_6.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        t_obrot_6.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        t_obrot_6.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        t_obrot_6.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        podloga.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        podloga.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        podloga.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        podloga.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        podloga.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        tlo.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tlo.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        tlo.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        tlo.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        tlo.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        szescian.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        szescian.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        szescian.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        szescian.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        szescian.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        t3d.set(new Vector3d(0.0, -7.0, 0.0));
        t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f));
        t3d.setScale(1.00);
        walec_glowny.setTransform(t3d);

        t3d.set(new Vector3f(0.0f, 0.0f, 0.0f));
        t3d.setRotation(new AxisAngle4f(0.0f, 0.0f, 1.57f, (float) Math.PI / 2));
        t3d.setScale(1.0);
        walec_srodek.setTransform(t3d);

        t3d.set(new Vector3f(0.0f, 0.0f, 0.0f));
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
        Scene s_podloga = null;
        Scene s_tlo = null;
        Scene s_szescian = null;

        File file = new java.io.File("model/podstawka.obj");
        File file1 = new java.io.File("model/kula.obj");
        File file2 = new java.io.File("model/walec.obj");
        File file3 = new java.io.File("model/lapa.obj");
        File file4 = new java.io.File("model/podloga.obj");
        File file5 = new java.io.File("model/szescian.obj");
        File file6 = new java.io.File("model/tlo.obj");

        try {
            s_walec_glowny = loader.load(file.toURI().toURL());
            s_walec_srodek = loader.load(file2.toURI().toURL());
            s_walec_gora = loader.load(file2.toURI().toURL());
            s_kula_1 = loader.load(file1.toURI().toURL());
            s_kula_2 = loader.load(file1.toURI().toURL());
            s_ryst = loader.load(file1.toURI().toURL());
            s_lapa_1 = loader.load(file3.toURI().toURL());
            s_lapa_2 = loader.load(file3.toURI().toURL());
            s_podloga = loader.load(file4.toURI().toURL());
            s_szescian = loader.load(file5.toURI().toURL());
            s_tlo = loader.load(file6.toURI().toURL());

        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

        //podstawa
        walec_glowny.addChild(s_walec_glowny.getSceneGroup());

        //walec_srodek
        walec_srodek.addChild(s_walec_srodek.getSceneGroup());

        //walec_gora
        walec_gora.addChild(s_walec_gora.getSceneGroup());

        // kula_1
        kula_1.addChild(s_kula_1.getSceneGroup());

        //kula_2
        kula_2.addChild(s_kula_2.getSceneGroup());

        //ryst
        ryst.addChild(s_ryst.getSceneGroup());

        //lapa_1
        lapa_1.addChild(s_lapa_1.getSceneGroup());

        //lapa_2
        lapa_2.addChild(s_lapa_2.getSceneGroup());

        //podloga
        podloga.addChild(s_podloga.getSceneGroup());

        //tlo
        tlo.addChild(s_tlo.getSceneGroup());

        szescian.addChild(cubek);
        //szescian.addChild(s_szescian.getSceneGroup());

        b_obrot_1.addChild(walec_glowny);

        b_obrot_2.addChild(walec_srodek);
        b_obrot_2.addChild(kula_1);

        b_obrot_3.addChild(walec_gora);
        b_obrot_3.addChild(kula_2);

        b_obrot_4.addChild(ryst);

        b_obrot_5.addChild(lapa_1);
        b_obrot_6.addChild(lapa_2);

        t_obrot_1.addChild(b_obrot_1);
        t_obrot_2.addChild(b_obrot_2);
        t_obrot_3.addChild(b_obrot_3);
        t_obrot_4.addChild(b_obrot_4);
        t_obrot_5.addChild(b_obrot_5);
        t_obrot_6.addChild(b_obrot_6);

        t_obrot_4.addChild(t_obrot_6);
        t_obrot_4.addChild(t_obrot_5);
        t_obrot_3.addChild(t_obrot_4);
        t_obrot_2.addChild(t_obrot_3);
        t_obrot_1.addChild(t_obrot_2);

        CollisionDetectorGroup cdGroup = new CollisionDetectorGroup(szescian);
        cdGroup.setSchedulingBounds(bounds);

        tg.addChild(szescian);
        // objRoot.addChild(tg_tink);
        tg.addChild(cdGroup);

        //objRoot.addChild(tg);
        //objRoot.addChild(t_obrot_1);
        tg.addChild(t_obrot_1);
        objRoot.addChild(tg);
        objRoot.addChild(podloga);
        //objRoot.addChild(tlo);
        //objRoot.addChild(szescian);

        objRoot.compile();

        return objRoot;

    }

    public static void main(String[] args) {
        ArticulatedArmRobot applet = new ArticulatedArmRobot();
        Frame frame = new MainFrame(applet, 800, 600);
    }

    public void keyTyped(KeyEvent e) {
        char key;
        if (!remote) {
            key = e.getKeyChar();
            if (!odtwarzanie) {
                if (key == 'a') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik)) {
                        t3dstep.rotY(Math.PI / dzielnik);
                        t_obrot_1.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_1.setTransform(t3d);

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else {
                        t3dstep.rotY(Math.PI / dzielnik);
                        t_obrot_1.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_1.setTransform(t3d);
                    }
                    if (nagrywanie) {
                        ruchy.append("a");
                        powrotne.append("d");
                    }

                }

                if (key == 'd') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik)) {

                        t3dstep.rotY(-Math.PI / dzielnik);
                        t_obrot_1.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_1.setTransform(t3d);

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else {
                        t3dstep.rotY(-Math.PI / dzielnik);
                        t_obrot_1.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_1.setTransform(t3d);
                    }
                    if (nagrywanie) {
                        ruchy.append("d");
                        powrotne.append("a");
                    }

                }

                if (key == 'w') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_1 < Math.PI / 4) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t_obrot_2.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_2.setTransform(t3d);
                        kat_1 += Math.PI / dzielnik;
                        kat_2 -= Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);
                    } else if (kat_1 < Math.PI / 4) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t_obrot_2.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_2.setTransform(t3d);
                        kat_1 += Math.PI / dzielnik;
                        kat_2 -= Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("w");
                        powrotne.append("s");
                    }
                }

                if (key == 's') {

                    podloga.getLocalToVworld(t3d);
                    t3d.get(wektor_1);
                    lapa_1.getLocalToVworld(t3d_szescian);
                    t3d_szescian.get(wektor_2);

                    if (wektor_2.y + 4.4 <= wektor_1.y) {
                        czy_podloga = true;
                    } else {
                        czy_podloga = false;
                    }

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_2 < Math.PI / 8 && !czy_podloga) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t_obrot_2.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_2.setTransform(t3d);
                        kat_1 -= Math.PI / dzielnik;
                        kat_2 += Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);
                    } else if (kat_2 < Math.PI / 8 && !czy_podloga) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t_obrot_2.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_2.setTransform(t3d);
                        kat_1 -= Math.PI / dzielnik;
                        kat_2 += Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("s");
                        powrotne.append("w");
                    }
                }
                if (key == '8') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_3 < Math.PI / 4) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_3.mul(t3dstep);
                        t_obrot_3.setTransform(t3d_obrot_3);
                        kat_3 += Math.PI / dzielnik;
                        kat_4 -= Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);
                    } else if (kat_3 < Math.PI / 4) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_3.mul(t3dstep);
                        t_obrot_3.setTransform(t3d_obrot_3);
                        kat_3 += Math.PI / dzielnik;
                        kat_4 -= Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("8");
                        powrotne.append("2");
                    }
                }
                if (key == '2') {

                    podloga.getLocalToVworld(t3d);
                    t3d.get(wektor_1);
                    lapa_1.getLocalToVworld(t3d_szescian);
                    t3d_szescian.get(wektor_2);

                    if (wektor_2.y + 4.4 <= wektor_1.y) {
                        czy_podloga = true;
                    } else {
                        czy_podloga = false;
                    }

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && !czy_podloga) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_3.mul(t3dstep);
                        t_obrot_3.setTransform(t3d_obrot_3);
                        kat_3 -= Math.PI / dzielnik;
                        kat_4 += Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);
                    } else if (kat_4 < Math.PI / 8 && !czy_podloga) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_3.mul(t3dstep);
                        t_obrot_3.setTransform(t3d_obrot_3);
                        kat_3 -= Math.PI / dzielnik;
                        kat_4 += Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("2");
                        powrotne.append("8");
                    }
                }
                if (key == '4') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik)) {
                        t3dstep.rotY(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);
                    } else {

                        t3dstep.rotY(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                    }
                    if (nagrywanie) {
                        ruchy.append("4");
                        powrotne.append("6");
                    }
                }

                if (key == '6') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik)) {
                        t3dstep.rotY(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else {
                        t3dstep.rotY(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                    }
                    if (nagrywanie) {
                        ruchy.append("6");
                        powrotne.append("4");
                    }

                }
                if (key == '1') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_5 < Math.PI / 3) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_5 += Math.PI / dzielnik;
                        kat_6 -= Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else if (kat_5 < Math.PI / 3) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_5 += Math.PI / dzielnik;
                        kat_6 -= Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("1");
                        powrotne.append("9");
                    }
                }

                if (key == '9') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_6 < Math.PI / 3) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_5 -= Math.PI / dzielnik;
                        kat_6 += Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else if (kat_6 < Math.PI / 3) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_5 -= Math.PI / dzielnik;
                        kat_6 += Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("9");
                        powrotne.append("1");
                    }
                }

                if (key == '3') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_7 < Math.PI / 3) {
                        t3dstep.rotX(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_7 += Math.PI / dzielnik;
                        kat_8 -= Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else if (kat_7 < Math.PI / 3) {
                        t3dstep.rotX(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_7 += Math.PI / dzielnik;
                        kat_8 -= Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("3");
                        powrotne.append("7");
                    }
                }

                if (key == '7') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_8 < Math.PI / 3) {
                        t3dstep.rotX(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_7 -= Math.PI / dzielnik;
                        kat_8 += Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else if (kat_8 < Math.PI / 3) {
                        t3dstep.rotX(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_7 -= Math.PI / dzielnik;
                        kat_8 += Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("7");
                        powrotne.append("3");
                    }
                }

                if (key == '5') {
                    if (kat_9 < 0) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_5.mul(t3dstep);
                        t_obrot_5.setTransform(t3d_obrot_5);

                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_6.mul(t3dstep);
                        t_obrot_6.setTransform(t3d_obrot_6);

                        kat_9 += Math.PI / dzielnik;
                        kat_10 -= Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("5");
                        powrotne.append("0");
                    }
                }

                if (key == '0') {
                    if (kat_10 < Math.PI / 8) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_5.mul(t3dstep);
                        t_obrot_5.setTransform(t3d_obrot_5);

                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_6.mul(t3dstep);
                        t_obrot_6.setTransform(t3d_obrot_6);

                        kat_9 -= Math.PI / dzielnik;
                        kat_10 += Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("0");
                        powrotne.append("5");
                    }
                }
                if (key == 'r') {
                    remote = !remote;
                    try {
                        welcomeSocket = new ServerSocket(port);
                        connectionSocket = welcomeSocket.accept();
                        externalControl();
                    } catch (IOException ex) {
                        Logger.getLogger(ArticulatedArmRobot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (key == 'm') {
                    czy_byl_klik = !czy_byl_klik;
                    if (nagrywanie) {
                        ruchy.append("m");
                    }
                }
                if (key == 'n') {
                    if (!nagrywanie) {
                        ruchy = new StringBuilder();
                        powrotne = new StringBuilder();
                    }
                    nagrywanie = !nagrywanie;
                }
            }
            if (key == 'p') {
                nagrywanie = false;
                odtwarzanie = !odtwarzanie;
                try {
                    automaticControl();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ArticulatedArmRobot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void automaticControl() throws InterruptedException {
        char key;
        int length = 0;
        int i = 0;
        int wracamy = 0;
        int length_powrot = 0;
        while (odtwarzanie) {
            Thread.sleep(32);
            length = ruchy.length() - 1;
            length_powrot = powrotne.length() - 1;
            if (wracamy != length_powrot) {
                key = powrotne.charAt(length_powrot - wracamy);
                wracamy++;
            } else {
                key = ruchy.charAt(i);
                i++;
            }
            if (i == length) {
                odtwarzanie = false;
            }
            if (wracamy == length_powrot && i == 0) {
                Thread.sleep(500);
            }

            if (key == 'a') {
                t3dstep.rotY(Math.PI / dzielnik);
                t_obrot_1.getTransform(t3d);
                t3d.get(matrix);
                t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
                t3d.mul(t3dstep);
                t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
                t_obrot_1.setTransform(t3d);

            }

            if (key == 'd') {

                t3dstep.rotY(-Math.PI / dzielnik);
                t_obrot_1.getTransform(t3d);
                t3d.get(matrix);
                t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
                t3d.mul(t3dstep);
                t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
                t_obrot_1.setTransform(t3d);

            }

            if (key == 'w') {
                if (kat_1 < Math.PI / 4) {
                    t3dstep.rotZ(-Math.PI / dzielnik);
                    t_obrot_2.getTransform(t3d);
                    t3d.mul(t3dstep);
                    t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
                    t_obrot_2.setTransform(t3d);
                    kat_1 += Math.PI / dzielnik;
                    kat_2 -= Math.PI / dzielnik;
                }
            }

            if (key == 's') {
                if (kat_2 < Math.PI / 8) {
                    t3dstep.rotZ(Math.PI / dzielnik);
                    t_obrot_2.getTransform(t3d);
                    t3d.mul(t3dstep);
                    t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
                    t_obrot_2.setTransform(t3d);
                    kat_1 -= Math.PI / dzielnik;
                    kat_2 += Math.PI / dzielnik;
                }
            }
            if (key == '8') {
                if (kat_3 < Math.PI / 4) {
                    t3dstep.rotZ(-Math.PI / dzielnik);
                    t3d_obrot_3.mul(t3dstep);
                    t_obrot_3.setTransform(t3d_obrot_3);
                    kat_3 += Math.PI / dzielnik;
                    kat_4 -= Math.PI / dzielnik;
                }
            }
            if (key == '2') {
                if (kat_4 < Math.PI / 8) {
                    t3dstep.rotZ(Math.PI / dzielnik);
                    t3d_obrot_3.mul(t3dstep);
                    t_obrot_3.setTransform(t3d_obrot_3);
                    kat_3 -= Math.PI / dzielnik;
                    kat_4 += Math.PI / dzielnik;
                }
            }
            if (key == '4') {
                t3dstep.rotY(Math.PI / dzielnik);
                t3d_obrot_4.mul(t3dstep);
                t_obrot_4.setTransform(t3d_obrot_4);
            }

            if (key == '6') {
                t3dstep.rotY(-Math.PI / dzielnik);
                t3d_obrot_4.mul(t3dstep);
                t_obrot_4.setTransform(t3d_obrot_4);
            }
            if (key == '1') {
                if (kat_5 < Math.PI / 3) {
                    t3dstep.rotZ(-Math.PI / dzielnik);
                    t3d_obrot_4.mul(t3dstep);
                    t_obrot_4.setTransform(t3d_obrot_4);
                    kat_5 += Math.PI / dzielnik;
                    kat_6 -= Math.PI / dzielnik;
                }
            }

            if (key == '9') {
                if (kat_6 < Math.PI / 3) {
                    t3dstep.rotZ(Math.PI / dzielnik);
                    t3d_obrot_4.mul(t3dstep);
                    t_obrot_4.setTransform(t3d_obrot_4);
                    kat_5 -= Math.PI / dzielnik;
                    kat_6 += Math.PI / dzielnik;
                }
            }

            if (key == '3') {
                if (kat_7 < Math.PI / 3) {
                    t3dstep.rotX(-Math.PI / dzielnik);
                    t3d_obrot_4.mul(t3dstep);
                    t_obrot_4.setTransform(t3d_obrot_4);
                    kat_7 += Math.PI / dzielnik;
                    kat_8 -= Math.PI / dzielnik;
                }
            }

            if (key == '7') {
                if (kat_8 < Math.PI / 3) {
                    t3dstep.rotX(Math.PI / dzielnik);
                    t3d_obrot_4.mul(t3dstep);
                    t_obrot_4.setTransform(t3d_obrot_4);
                    kat_7 -= Math.PI / dzielnik;
                    kat_8 += Math.PI / dzielnik;
                }
            }

            if (key == '5') {
                if (kat_9 < 0) {
                    t3dstep.rotZ(Math.PI / dzielnik);
                    t3d_obrot_5.mul(t3dstep);
                    t_obrot_5.setTransform(t3d_obrot_5);

                    t3dstep.rotZ(-Math.PI / dzielnik);
                    t3d_obrot_6.mul(t3dstep);
                    t_obrot_6.setTransform(t3d_obrot_6);

                    kat_9 += Math.PI / dzielnik;
                    kat_10 -= Math.PI / dzielnik;
                }
            }

            if (key == '0') {
                if (kat_10 < Math.PI / 8) {
                    t3dstep.rotZ(-Math.PI / dzielnik);
                    t3d_obrot_5.mul(t3dstep);
                    t_obrot_5.setTransform(t3d_obrot_5);

                    t3dstep.rotZ(Math.PI / dzielnik);
                    t3d_obrot_6.mul(t3dstep);
                    t_obrot_6.setTransform(t3d_obrot_6);

                    kat_9 -= Math.PI / dzielnik;
                    kat_10 += Math.PI / dzielnik;
                }
            }
            if (key == 'm') {
                czy_byl_klik = !czy_byl_klik;
            }
        }
    }

    private void externalControl() {
        char key;
        String clientSentence = null;
        Transform3D przesuniecie_obserwatora = new Transform3D();
        Transform3D rotacja = new Transform3D();
        float x = 0, z = 5;
        double alfa = 0;
        BufferedReader inFromClient;
        while (remote) {
            try {
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientSentence = inFromClient.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ArticulatedArmRobot.class.getName()).log(Level.SEVERE, null, ex);
            }

            key = clientSentence.charAt(clientSentence.length() - 1);

            if (key == 'r') {
                remote = !remote;
            }
            if (key == 'a') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik)) {
                        t3dstep.rotY(Math.PI / dzielnik);
                        t_obrot_1.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_1.setTransform(t3d);

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else {
                        t3dstep.rotY(Math.PI / dzielnik);
                        t_obrot_1.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_1.setTransform(t3d);
                    }
                    if (nagrywanie) {
                        ruchy.append("a");
                        powrotne.append("d");
                    }

                }

                if (key == 'd') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik)) {

                        t3dstep.rotY(-Math.PI / dzielnik);
                        t_obrot_1.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_1.setTransform(t3d);

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else {
                        t3dstep.rotY(-Math.PI / dzielnik);
                        t_obrot_1.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_1.setTransform(t3d);
                    }
                    if (nagrywanie) {
                        ruchy.append("d");
                        powrotne.append("a");
                    }

                }

                if (key == 'w') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_1 < Math.PI / 4) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t_obrot_2.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_2.setTransform(t3d);
                        kat_1 += Math.PI / dzielnik;
                        kat_2 -= Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);
                    } else if (kat_1 < Math.PI / 4) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t_obrot_2.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_2.setTransform(t3d);
                        kat_1 += Math.PI / dzielnik;
                        kat_2 -= Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("w");
                        powrotne.append("s");
                    }
                }

                if (key == 's') {

                    podloga.getLocalToVworld(t3d);
                    t3d.get(wektor_1);
                    lapa_1.getLocalToVworld(t3d_szescian);
                    t3d_szescian.get(wektor_2);

                    if (wektor_2.y + 4.4 <= wektor_1.y) {
                        czy_podloga = true;
                    } else {
                        czy_podloga = false;
                    }

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_2 < Math.PI / 8 && !czy_podloga) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t_obrot_2.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_2.setTransform(t3d);
                        kat_1 -= Math.PI / dzielnik;
                        kat_2 += Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);
                    } else if (kat_2 < Math.PI / 8 && !czy_podloga) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t_obrot_2.getTransform(t3d);
                        t3d.mul(t3dstep);
                        t_obrot_2.setTransform(t3d);
                        kat_1 -= Math.PI / dzielnik;
                        kat_2 += Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("s");
                        powrotne.append("w");
                    }
                }
                if (key == '8') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_3 < Math.PI / 4) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_3.mul(t3dstep);
                        t_obrot_3.setTransform(t3d_obrot_3);
                        kat_3 += Math.PI / dzielnik;
                        kat_4 -= Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);
                    } else if (kat_3 < Math.PI / 4) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_3.mul(t3dstep);
                        t_obrot_3.setTransform(t3d_obrot_3);
                        kat_3 += Math.PI / dzielnik;
                        kat_4 -= Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("8");
                        powrotne.append("2");
                    }
                }
                if (key == '2') {

                    podloga.getLocalToVworld(t3d);
                    t3d.get(wektor_1);
                    lapa_1.getLocalToVworld(t3d_szescian);
                    t3d_szescian.get(wektor_2);

                    if (wektor_2.y + 4.4 <= wektor_1.y) {
                        czy_podloga = true;
                    } else {
                        czy_podloga = false;
                    }

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && !czy_podloga) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_3.mul(t3dstep);
                        t_obrot_3.setTransform(t3d_obrot_3);
                        kat_3 -= Math.PI / dzielnik;
                        kat_4 += Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);
                    } else if (kat_4 < Math.PI / 8 && !czy_podloga) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_3.mul(t3dstep);
                        t_obrot_3.setTransform(t3d_obrot_3);
                        kat_3 -= Math.PI / dzielnik;
                        kat_4 += Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("2");
                        powrotne.append("8");
                    }
                }
                if (key == '4') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik)) {
                        t3dstep.rotY(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);
                    } else {

                        t3dstep.rotY(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                    }
                    if (nagrywanie) {
                        ruchy.append("4");
                        powrotne.append("6");
                    }
                }

                if (key == '6') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik)) {
                        t3dstep.rotY(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else {
                        t3dstep.rotY(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                    }
                    if (nagrywanie) {
                        ruchy.append("6");
                        powrotne.append("4");
                    }

                }
                if (key == '1') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_5 < Math.PI / 3) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_5 += Math.PI / dzielnik;
                        kat_6 -= Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else if (kat_5 < Math.PI / 3) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_5 += Math.PI / dzielnik;
                        kat_6 -= Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("1");
                        powrotne.append("9");
                    }
                }

                if (key == '9') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_6 < Math.PI / 3) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_5 -= Math.PI / dzielnik;
                        kat_6 += Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else if (kat_6 < Math.PI / 3) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_5 -= Math.PI / dzielnik;
                        kat_6 += Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("9");
                        powrotne.append("1");
                    }
                }

                if (key == '3') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_7 < Math.PI / 3) {
                        t3dstep.rotX(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_7 += Math.PI / dzielnik;
                        kat_8 -= Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else if (kat_7 < Math.PI / 3) {
                        t3dstep.rotX(-Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_7 += Math.PI / dzielnik;
                        kat_8 -= Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("3");
                        powrotne.append("7");
                    }
                }

                if (key == '7') {

                    if (stop && czy_byl_klik && kat_10 == 13 * (Math.PI / dzielnik) && kat_8 < Math.PI / 3) {
                        t3dstep.rotX(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_7 -= Math.PI / dzielnik;
                        kat_8 += Math.PI / dzielnik;

                        t_obrot_5.getLocalToVworld(t3d_szescian);

                        t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
                        t3d_szescian.mul(t3d);
                        szescian.setTransform(t3d_szescian);

                    } else if (kat_8 < Math.PI / 3) {
                        t3dstep.rotX(Math.PI / dzielnik);
                        t3d_obrot_4.mul(t3dstep);
                        t_obrot_4.setTransform(t3d_obrot_4);
                        kat_7 -= Math.PI / dzielnik;
                        kat_8 += Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("7");
                        powrotne.append("3");
                    }
                }

                if (key == '5') {
                    if (kat_9 < 0) {
                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_5.mul(t3dstep);
                        t_obrot_5.setTransform(t3d_obrot_5);

                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_6.mul(t3dstep);
                        t_obrot_6.setTransform(t3d_obrot_6);

                        kat_9 += Math.PI / dzielnik;
                        kat_10 -= Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("5");
                        powrotne.append("0");
                    }
                }

                if (key == '0') {
                    if (kat_10 < Math.PI / 8) {
                        t3dstep.rotZ(-Math.PI / dzielnik);
                        t3d_obrot_5.mul(t3dstep);
                        t_obrot_5.setTransform(t3d_obrot_5);

                        t3dstep.rotZ(Math.PI / dzielnik);
                        t3d_obrot_6.mul(t3dstep);
                        t_obrot_6.setTransform(t3d_obrot_6);

                        kat_9 -= Math.PI / dzielnik;
                        kat_10 += Math.PI / dzielnik;
                    }
                    if (nagrywanie) {
                        ruchy.append("0");
                        powrotne.append("5");
                    }
                }
                if (key == 'r') {
                    remote = !remote;
                    try {
                        welcomeSocket = new ServerSocket(port);
                        connectionSocket = welcomeSocket.accept();
                        externalControl();
                    } catch (IOException ex) {
                        Logger.getLogger(ArticulatedArmRobot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            if (key == 'i') {
                z -= 3 * cos(alfa);
                x -= 3 * sin(alfa);
                przesuniecie_obserwatora.setTranslation(new Vector3f(x, 0.0f, z));
                universe.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
            }
            if (key == 'k') {
                z += 3 * cos(alfa);
                x += 3 * sin(alfa);
                przesuniecie_obserwatora.setTranslation(new Vector3f(x, 0.0f, z));
                universe.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
            }
            if (key == 'j') {
                rotacja.rotY(0.07);
                alfa += 0.07;
                przesuniecie_obserwatora.mul(rotacja);
                universe.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
            }
            if (key == 'l') {
                rotacja.rotY(-0.07);
                alfa -= 0.07;
                przesuniecie_obserwatora.mul(rotacja);
                universe.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
            }
            if (key == 'm') {
                czy_byl_klik = !czy_byl_klik;
            }
        }
        try {
            connectionSocket.close();
            welcomeSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ArticulatedArmRobot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    class CollisionDetectorGroup extends Behavior {

        private boolean inCollision = false;
        private Group group;

        private WakeupOnCollisionEntry wEnter;
        private WakeupOnCollisionExit wExit;

        public CollisionDetectorGroup(Group gp) { // Corrected: gp
            group = gp; // Corrected: gp
            inCollision = false;

        }

        public void initialize() {
            wEnter = new WakeupOnCollisionEntry(group);
            wExit = new WakeupOnCollisionExit(group);
            wakeupOn(wEnter);
        }

        public void processStimulus(Enumeration criteria) {

            inCollision = !inCollision;
            if (inCollision) {

                stop = true;

                wakeupOn(wExit);
            } else {
                stop = false;
                wakeupOn(wEnter);
            }
        }
    }
}
