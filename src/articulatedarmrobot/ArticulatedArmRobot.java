package articulatedarmrobot;

import java.applet.*;
import java.awt.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;

import com.sun.j3d.utils.geometry.ColorCube;

import java.io.*;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import java.util.*;
import java.util.Map.Entry;
import static java.util.Map.entry;

public class ArticulatedArmRobot extends Applet implements KeyListener {

    private final int port = 64003;

    private SimpleUniverse universe = null;

    private ColorCube cubek = new ColorCube();

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
    private final Transform3D t3dstep = new Transform3D();

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

    private final double dzielnik = 256;
    private final Vector3f wektor_1 = new Vector3f();
    private final Vector3f wektor_2 = new Vector3f();

    private boolean stop = false;
    private boolean trzyma = false;
    private boolean czy_podloga = false;
    private boolean nagrywanie = false;
    private boolean odtwarzanie = false;

    private final Map<String, String> movesMap = Map.ofEntries(
            entry("w", "s"),
            entry("a", "d"),
            entry("2", "8"),
            entry("4", "6"),
            entry("1", "9"),
            entry("3", "7"),
            entry("M", "m")
    );

    private StringBuilder ruchy;

    public ArticulatedArmRobot() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas = new Canvas3D(config);
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

        TransformGroup viewtrans = universe.getViewingPlatform().getViewPlatformTransform();

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

        ArrayList<BranchGroup> branchGroups = new ArrayList<BranchGroup>(Arrays.asList(b_obrot_1, b_obrot_2, b_obrot_3, b_obrot_4, b_obrot_5, b_obrot_6));

        ArrayList<TransformGroup> transformGroups = new ArrayList<TransformGroup>(Arrays.asList(walec_glowny, walec_srodek, walec_gora, kula_1, kula_2, ryst, lapa_1, lapa_2, t_obrot_1,
                t_obrot_2, t_obrot_3, t_obrot_4, t_obrot_5, t_obrot_6, podloga, tlo, szescian));

        ArrayList<Group> groups = new ArrayList<Group>();
        groups.addAll(branchGroups);
        groups.addAll(transformGroups);

        for (Group group : groups) {
            setCapabilities(group);
        }

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

        t3d.set(new Vector3d(0.5, -1.2, 0.0));
        t3d.setRotation(new AxisAngle4f(0.0f, 1.0f, 0.0f, (float) Math.PI));
        t3d.setScale(0.6);
        lapa_2.setTransform(t3d);

        ObjectFile loader = new ObjectFile();

        addChild(walec_glowny, loader, "model/podstawka.obj");
        addChild(walec_srodek, loader, "model/walec.obj");
        addChild(walec_gora, loader, "model/walec.obj");
        addChild(kula_1, loader, "model/kula.obj");
        addChild(kula_2, loader, "model/kula.obj");
        addChild(ryst, loader, "model/kula.obj");
        addChild(lapa_1, loader, "model/lapa.obj");
        addChild(lapa_2, loader, "model/lapa.obj");
        addChild(podloga, loader, "model/podloga.obj");
        addChild(tlo, loader, "model/tlo.obj");

        Map<Group, Group[]> test = Map.ofEntries(
                entry(b_obrot_1, new Group[]{walec_glowny}),
                entry(b_obrot_2, new Group[]{kula_1, walec_srodek}),
                entry(b_obrot_3, new Group[]{walec_gora, kula_2}),
                entry(b_obrot_4, new Group[]{ryst}),
                entry(b_obrot_5, new Group[]{lapa_1}),
                entry(b_obrot_6, new Group[]{lapa_2}),
                entry(t_obrot_1, new Group[]{b_obrot_1, t_obrot_2}),
                entry(t_obrot_2, new Group[]{b_obrot_2, t_obrot_3}),
                entry(t_obrot_3, new Group[]{b_obrot_3, t_obrot_4}),
                entry(t_obrot_4, new Group[]{b_obrot_4, t_obrot_5, t_obrot_6}),
                entry(t_obrot_5, new Group[]{b_obrot_5}),
                entry(t_obrot_6, new Group[]{b_obrot_6})
        );

        for (Map.Entry<Group, Group[]> entry : test.entrySet()) {
            addChildren(entry.getKey(), entry.getValue());
        }

        szescian.addChild(cubek);
        CollisionDetectorGroup cdGroup = new CollisionDetectorGroup(szescian);
        cdGroup.setSchedulingBounds(bounds);

        tg.addChild(szescian);
        tg.addChild(cdGroup);

        tg.addChild(t_obrot_1);
        objRoot.addChild(tg);
        objRoot.addChild(podloga);

        objRoot.compile();
        return objRoot;
    }

    private void setCapabilities(Group group) {
        group.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        group.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        group.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        group.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        group.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    }

    private void addChildren(Group parent, Group children[]) {
        for (Group child : children) {
            parent.addChild(child);
        }
    }

    private void addChild(TransformGroup tg, ObjectFile loader, String filePath) {
        try {
            File file = new java.io.File(filePath);
            tg.addChild(loader.load(file.toURI().toURL()).getSceneGroup());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        ArticulatedArmRobot applet = new ArticulatedArmRobot();
        Frame frame = new MainFrame(applet, 800, 600);
    }

    private void checkPodloga() {
        podloga.getLocalToVworld(t3d);
        t3d.get(wektor_1);
        lapa_1.getLocalToVworld(t3d_szescian);
        t3d_szescian.get(wektor_2);
        if (wektor_2.y + 4.4 <= wektor_1.y) {
            czy_podloga = true;
        } else {
            czy_podloga = false;
        }
    }

    private void cubeSteering() {
        if (stop && kat_10 == 13 * (Math.PI / dzielnik)) {
            t_obrot_5.getLocalToVworld(t3d_szescian);

            t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
            t3d_szescian.mul(t3d);
            szescian.setTransform(t3d_szescian);
        }
    }

    private void steeringAD(double direction, char key) {
        t3dstep.rotY(direction * Math.PI / dzielnik);
        t_obrot_1.getTransform(t3d);
        t3d.mul(t3dstep);
        t_obrot_1.setTransform(t3d);

        trzyma = stop;
        cubeSteering();
        addToReplay(key);
    }

    private void steeringWS(double direction, char key) {
        t3dstep.rotZ(direction * Math.PI / dzielnik);
        t_obrot_2.getTransform(t3d);
        t3d.mul(t3dstep);
        t_obrot_2.setTransform(t3d);
        kat_1 = kat_1 - direction * Math.PI / dzielnik;
        kat_2 = kat_2 + direction * Math.PI / dzielnik;
        trzyma = stop;
        cubeSteering();
        addToReplay(key);
    }

    private void steering28(double direction, char key) {
        t3dstep.rotZ(direction * Math.PI / dzielnik);
        t3d_obrot_3.mul(t3dstep);
        t_obrot_3.setTransform(t3d_obrot_3);
        kat_3 = kat_3 - direction * Math.PI / dzielnik;
        kat_4 = kat_4 + direction * Math.PI / dzielnik;
        trzyma = stop;
        cubeSteering();
        addToReplay(key);
    }

    private void steering46(double direction, char key) {
        t3dstep.rotY(direction * Math.PI / dzielnik);
        t3d_obrot_4.mul(t3dstep);
        t_obrot_4.setTransform(t3d_obrot_4);
        trzyma = stop;
        cubeSteering();
        addToReplay(key);
    }

    private void steering19(double direction, char key) {
        t3dstep.rotZ(direction * Math.PI / dzielnik);
        t3d_obrot_4.mul(t3dstep);
        t_obrot_4.setTransform(t3d_obrot_4);
        kat_5 = kat_5 - direction * Math.PI / dzielnik;
        kat_6 = kat_6 + direction * Math.PI / dzielnik;
        trzyma = stop;
        cubeSteering();
        addToReplay(key);
    }

    private void steering37(double direction, char key) {
        t3dstep.rotX(direction * Math.PI / dzielnik);
        t3d_obrot_4.mul(t3dstep);
        t_obrot_4.setTransform(t3d_obrot_4);
        kat_7 = kat_7 - direction * Math.PI / dzielnik;
        kat_8 = kat_8 + direction * Math.PI / dzielnik;
        trzyma = stop;
        cubeSteering();
        addToReplay(key);
    }

    private void steeringMm(double direction, char key) {
        int ile = 13;
        t3dstep.rotZ(ile * direction * Math.PI / dzielnik);
        t3d_obrot_5.mul(t3dstep);
        t_obrot_5.setTransform(t3d_obrot_5);

        t3dstep.rotZ(ile * -direction * Math.PI / dzielnik);
        t3d_obrot_6.mul(t3dstep);
        t_obrot_6.setTransform(t3d_obrot_6);

        kat_9 = kat_9 + direction * ile * Math.PI / dzielnik;
        kat_10 = kat_10 - direction * ile * Math.PI / dzielnik;
        addToReplay(key);
    }

    private void addToReplay(char ruch) {
        if (nagrywanie) {
            ruchy.append(ruch);
        }
    }

    private void steering(char key, String flag) {
        if (key == 'a') {
            steeringAD(1, key);
        }
        if (key == 'd') {
            steeringAD(-1, key);
        }
        if (key == 'w') {
            if (kat_1 < Math.PI / 4) {
                steeringWS(-1, key);
            }
        }
        if (key == 's') {
            checkPodloga();
            if (kat_2 < Math.PI / 8 && !czy_podloga) {
                steeringWS(1, key);
            }
        }
        if (key == '8') {
            if (kat_3 < Math.PI / 4) {
                steering28(-1, key);
            }
        }
        if (key == '2') {
            checkPodloga();
            if (kat_4 < Math.PI / 8 && !czy_podloga) {
                steering28(1, key);
            }
        }
        if (key == '4') {
            steering46(1, key);
        }
        if (key == '6') {
            steering46(-1, key);
        }
        if (key == '1') {
            if (kat_5 < Math.PI / 3) {
                steering19(-1, key);
            }
        }
        if (key == '9') {
            if (kat_6 < Math.PI / 3) {
                steering19(1, key);
            }
        }
        if (key == '3') {
            if (kat_7 < Math.PI / 3) {
                steering37(-1, key);
            }
        }
        if (key == '7') {
            if (kat_8 < Math.PI / 3) {
                steering37(1, key);
            }
        }
        if (key == 'r') {
            remote = !remote;
            if (!"external".equals(flag)) {
                try {
                    welcomeSocket = new ServerSocket(port);
                    connectionSocket = welcomeSocket.accept();
                    externalControl();
                } catch (IOException ex) {
                    Logger.getLogger(ArticulatedArmRobot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (key == 'm') {
            if (kat_10 < 2 * 13 * Math.PI / dzielnik) {
                steeringMm(-1, key);
            }
        }
        if (key == 'M') {
            if (!trzyma & kat_10 - 13 * Math.PI / dzielnik >= 0) {
                steeringMm(1, key);
            }
        }
        if (key == 'n') {
            if (!nagrywanie) {
                ruchy = new StringBuilder();
            }
            nagrywanie = true;
        }
        if (key == 'p') {
            nagrywanie = false;
            if (ruchy != null && ruchy.length() != 0) {
                odtwarzanie = !odtwarzanie;
                try {
                    automaticControl();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ArticulatedArmRobot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char key;
        if (!remote) {
            key = e.getKeyChar();
            if (!odtwarzanie) {
                steering(key, "key");
            }
        }
    }

    private String getKey(Map<String, String> map, char value) {
        String key = null;
        for (Entry entry : map.entrySet()) {
            if (entry.getValue().equals("" + value)) {
                key = (String) entry.getKey();
                break;
            }
        }
        return key;
    }

    private void automaticControl() throws InterruptedException {
        String key;
        int length = ruchy.length() - 1;
        ruchy.reverse();
        for (int i = 0; i < length; i++) {
            Thread.sleep(32);
            char mapKey = ruchy.charAt(i);
            key = movesMap.get("" + mapKey);
            if (key == null) {
                key = getKey(movesMap, mapKey);
            }
            steering(key.charAt(0), "automatic");
        }
        Thread.sleep(500);
        ruchy.reverse();
        for (int i = 0; i < length; i++) {
            Thread.sleep(32);
            key = "" + ruchy.charAt(i);
            steering(key.charAt(0), "automatic");
        }
        odtwarzanie = false;
    }

    private void externalControl() {
        char key;
        String clientSentence = null;
        BufferedReader inFromClient;
        while (remote) {
            try {
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientSentence = inFromClient.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ArticulatedArmRobot.class.getName()).log(Level.SEVERE, null, ex);
            }
            key = clientSentence.charAt(clientSentence.length() - 1);
            steering(key, "external");
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
        private final Group group;
        private WakeupOnCollisionEntry wEnter;
        private WakeupOnCollisionExit wExit;

        public CollisionDetectorGroup(Group gp) {
            group = gp;
            inCollision = false;
        }

        @Override
        public void initialize() {
            wEnter = new WakeupOnCollisionEntry(group);
            wExit = new WakeupOnCollisionExit(group);
            wakeupOn(wEnter);
        }

        @Override
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
