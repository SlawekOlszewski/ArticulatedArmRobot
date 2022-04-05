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
/**
 * The ArticualtedArmRobot is a interactive 3D model of a 6-axis robot arm 
 * developed with use of Java3D library. It allows to control the movement of 
 * the robotic arm, grab the cube and move it around.
 * It is possible to  connect to the robot from another computer within
 * local network and steer the arm remotely. 
 * There's also a possibility to steer the arm sequentialy, sequence
 * of moves can be recorded and then repeated by the arm automatically. 
 * @author Sławek Olszewski
 */
public class ArticulatedArmRobot extends Applet implements KeyListener {

    private final int port = 64003;
    
    private SimpleUniverse universe = null;
    private ColorCube colorCube = new ColorCube();
    private BoundingSphere bounds = null;
    
    private TransformGroup tg = null;
    private TransformGroup mainRoller = null;
    private TransformGroup midRoller = null;
    private TransformGroup topRoller = null;
    private TransformGroup joint1 = null;
    private TransformGroup joint2 = null;
    private TransformGroup joint3 = null;
    private TransformGroup gripper1 = null;
    private TransformGroup gripper2 = null;
    private TransformGroup floor = null;
    private TransformGroup cube = null;
    private TransformGroup background = null;
    private TransformGroup tgRotation1 = null;
    private TransformGroup tgRotation2 = null;
    private TransformGroup tgRotation3 = null;
    private TransformGroup tgRotation4 = null;
    private TransformGroup tgRotation5 = null;
    private TransformGroup tgRotation6 = null;
    private BranchGroup bgRotation1 = null;
    private BranchGroup bgRotation2 = null;
    private BranchGroup bgRotation3 = null;
    private BranchGroup bgRotation4 = null;
    private BranchGroup bgRotation5 = null;
    private BranchGroup bgRotation6 = null;
    private Transform3D t3d = null;
    private Transform3D t3dFloor = new Transform3D();
    private Transform3D t3dBackground = new Transform3D();
    private Transform3D t3dCube = new Transform3D();
    private Transform3D t3dRotation3 = new Transform3D();
    private Transform3D t3dRotation4 = new Transform3D();
    private Transform3D t3dRotation5 = new Transform3D();
    private Transform3D t3dRotation6 = new Transform3D();
    private final Transform3D t3dStep = new Transform3D();

    private boolean remote = false;
    private ServerSocket welcomeSocket = null;
    private Socket connectionSocket = null;

    private double angle1 = 0;
    private double angle2 = 0;
    private double angle3 = 0;
    private double angle4 = 0;
    private double angle5 = 0;
    private double angle6 = 0;
    private double angle7 = 0;
    private double angle8 = 0;
    private double angle9 = 0;
    private double angle10 = 0;
    private StringBuilder moves;

    private boolean stop = false;
    private boolean holdingCube = false;
    private boolean floorColision = false;
    private boolean recording = false;
    private boolean playing = false;
    
    private final double divider = 256;
    private final Vector3f vector1 = new Vector3f();
    private final Vector3f vector2 = new Vector3f();
    private final Map<String, String> movesMap = Map.ofEntries(
            entry("w", "s"),
            entry("a", "d"),
            entry("2", "8"),
            entry("4", "6"),
            entry("1", "9"),
            entry("3", "7"),
            entry("M", "m")
    );
    /**
     * ArticulatedArmRobot Class Constuctor
     * @see createSceneGraph
    */
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
    
    /**
     * Function creating base scene BranchGroup.
     * It consists of Background, AmbientLight and two DirectionalLight. As a 
     * last step it calls  createArm() fucntions and adds the return to this
     * function returned object
     * 
     * @return the BranchGroup with all of elements as childs attached
     * @see createArm()
     */
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
    /**
     * Function initializing all of needed objects to make the arm functional
     * 
     * @return the BranchGroup with all the elements of the arm
     * @see setCapabilities()
     * @see assignTransform3D()
     * @see addChild()
     * @see addChildren()
     * @see CollisionDetectorGroup
     */
    private BranchGroup createArm() {

        t3dFloor = new Transform3D();
        t3dFloor.setTranslation(new Vector3f(0.0f, -7.0f, 0.0f));
        t3dFloor.setScale(100);

        t3dBackground = new Transform3D();
        t3dBackground.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
        t3dBackground.setRotation(new AxisAngle4f(2.0f, 0.0f, 0.0f, 0.0f));
        t3dBackground.setScale(100);

        t3dCube = new Transform3D();
        t3dCube.setTranslation(new Vector3f(-7.0f, -6.0f, 0.0f));

        t3dRotation3 = new Transform3D();
        t3dRotation3.setTranslation(new Vector3f(-6.0f, 0.0f, 0.0f));

        t3dRotation4 = new Transform3D();
        t3dRotation4.setTranslation(new Vector3f(0.0f, -4.5f, 0.0f));

        t3dRotation5 = new Transform3D();
        t3dRotation5.setTranslation(new Vector3f(-0.1f, 0.1f, 0.0f));

        t3dRotation6 = new Transform3D();
        t3dRotation6.setTranslation(new Vector3f(0.1f, 0.1f, 0.0f));

        colorCube = new ColorCube(0.3);

        BranchGroup objRoot = new BranchGroup();

        bgRotation1 = new BranchGroup();
        bgRotation2 = new BranchGroup();
        bgRotation3 = new BranchGroup();
        bgRotation4 = new BranchGroup();
        bgRotation5 = new BranchGroup();
        bgRotation6 = new BranchGroup();
        tg = new TransformGroup();
        mainRoller = new TransformGroup();
        midRoller = new TransformGroup();
        topRoller = new TransformGroup();
        joint1 = new TransformGroup();
        joint2 = new TransformGroup();
        joint3 = new TransformGroup();
        gripper1 = new TransformGroup();
        gripper2 = new TransformGroup();
        floor = new TransformGroup(t3dFloor);
        background = new TransformGroup(t3dBackground);
        cube = new TransformGroup(t3dCube);
        tgRotation1 = new TransformGroup();
        tgRotation2 = new TransformGroup();
        tgRotation3 = new TransformGroup(t3dRotation3);
        tgRotation4 = new TransformGroup(t3dRotation4);
        tgRotation5 = new TransformGroup(t3dRotation5);
        tgRotation6 = new TransformGroup(t3dRotation6);
        t3d = new Transform3D();

        ArrayList<BranchGroup> branchGroups = new ArrayList<>(Arrays.asList(bgRotation1, bgRotation2, bgRotation3, bgRotation4, bgRotation5, bgRotation6));

        ArrayList<TransformGroup> transformGroups = new ArrayList<>(Arrays.asList(mainRoller, midRoller, topRoller, joint1, joint2, joint3, gripper1, gripper2, tgRotation1,
                tgRotation2, tgRotation3, tgRotation4, tgRotation5, tgRotation6, floor, background, cube));

        ArrayList<Group> groups = new ArrayList<>();
        groups.addAll(branchGroups);
        groups.addAll(transformGroups);

        for (Group group : groups) {
            setCapabilities(group);
        }
        
        assignTransform3D(new Vector3d(0.0, -7.0, 0.0), new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f), 1.00, mainRoller);
        assignTransform3D(new Vector3d(0.0f, 0.0, 0.0), new AxisAngle4f(0.0f, 0.0f, 1.57f, (float) Math.PI / 2), 1.00, midRoller);
        assignTransform3D(new Vector3d(0.0f, 0.0, 0.0), new AxisAngle4f(0.0f, 0.0f, 1.57f, (float) Math.PI), 0.70, topRoller);
        assignTransform3D(new Vector3d(0.0, 0.0, 0.0), new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f), 2.00, joint1);
        assignTransform3D(new Vector3d(0.0, 0.0, 0.0), new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f), 1.50, joint2);
        assignTransform3D(new Vector3d(0.0, 0.0, 0.0), new AxisAngle4f(0.0f, 1.0f, 0.0f, -1.2f), 0.90, joint3);
        assignTransform3D(new Vector3d(-0.5, -1.2, 0.0), new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f), 0.60, gripper1);
        assignTransform3D(new Vector3d(0.5, -1.2, 0.0), new AxisAngle4f(0.0f, 1.0f, 0.0f, (float) Math.PI), 0.60, gripper2);

        ObjectFile loader = new ObjectFile();

        addChild(mainRoller, loader, "model/podstawka.obj");
        addChild(midRoller, loader, "model/walec.obj");
        addChild(topRoller, loader, "model/walec.obj");
        addChild(joint1, loader, "model/kula.obj");
        addChild(joint2, loader, "model/kula.obj");
        addChild(joint3, loader, "model/kula.obj");
        addChild(gripper1, loader, "model/lapa.obj");
        addChild(gripper2, loader, "model/lapa.obj");
        addChild(floor, loader, "model/podloga.obj");
        addChild(background, loader, "model/tlo.obj");

        Map<Group, Group[]> groupMap = Map.ofEntries(
                entry(bgRotation1, new Group[]{mainRoller}),
                entry(bgRotation2, new Group[]{joint1, midRoller}),
                entry(bgRotation3, new Group[]{topRoller, joint2}),
                entry(bgRotation4, new Group[]{joint3}),
                entry(bgRotation5, new Group[]{gripper1}),
                entry(bgRotation6, new Group[]{gripper2}),
                entry(tgRotation1, new Group[]{bgRotation1, tgRotation2}),
                entry(tgRotation2, new Group[]{bgRotation2, tgRotation3}),
                entry(tgRotation3, new Group[]{bgRotation3, tgRotation4}),
                entry(tgRotation4, new Group[]{bgRotation4, tgRotation5, tgRotation6}),
                entry(tgRotation5, new Group[]{bgRotation5}),
                entry(tgRotation6, new Group[]{bgRotation6})
        );

        for (Map.Entry<Group, Group[]> entry : groupMap.entrySet()) {
            addChildren(entry.getKey(), entry.getValue());
        }

        cube.addChild(colorCube);
        CollisionDetectorGroup cdGroup = new CollisionDetectorGroup(cube);
        cdGroup.setSchedulingBounds(bounds);

        tg.addChild(cube);
        tg.addChild(cdGroup);

        tg.addChild(tgRotation1);
        objRoot.addChild(tg);
        objRoot.addChild(floor);

        objRoot.compile();
        return objRoot;
    }
    /**
     * Function that is assigning Vector3D, AxisAnlge4f and set's the scale to the target TransformGroup
     * @param vector
     * @param rotation
     * @param scale
     * @param target 
     */
    private void assignTransform3D(Vector3d vector, AxisAngle4f rotation, double scale, TransformGroup target) {
        t3d.set(vector);
        t3d.setRotation(rotation);
        t3d.setScale(scale);
        target.setTransform(t3d);
    }
    /**
     * Function assiging all of needed capabilities to the bassed TransformGroup or BranchGroup (they inherit from Group)
     * @param group 
     */
    private void setCapabilities(Group group) {
        group.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        group.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        group.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        group.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        group.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    }
    /*
    * Function that adds all of the TransformGroups or BranchGroups to the target Group as child
    */
    private void addChildren(Group parent, Group children[]) {
        for (Group child : children) {
            parent.addChild(child);
        }
    }
    /**
     * Function assigning .obj model to the TrasformGroup
     * @param tg - target TransformGroup
     * @param loader - ObjectFile loader
     * @param filePath - path to the .obj file
     */
    private void addChild(TransformGroup tg, ObjectFile loader, String filePath) {
        try {
            File file = new java.io.File(filePath);
            tg.addChild(loader.load(file.toURI().toURL()).getSceneGroup());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
    /**
     * Main function, creates an instance of ArticulatedArmRobot, set's size of the frame and title
     * @param args notused
     */
    public static void main(String[] args) {
        ArticulatedArmRobot applet = new ArticulatedArmRobot();
        Frame frame = new MainFrame(applet, 800, 600);
        frame.setTitle("ArticulatedArmRobot");
    }
    /**
     * Function checking if there is a colissing between the robot and the floor
     * If yes, then it set's the floorColision to true, otherwise it is false
     */
    private void checkFloor() {
        floor.getLocalToVworld(t3d);
        t3d.get(vector1);
        gripper1.getLocalToVworld(t3dCube);
        t3dCube.get(vector2);
        if (vector2.y + 4.4 <= vector1.y) {
            floorColision = true;
        } else {
            floorColision = false;
        }
    }
    /**
     * Function allowing to steer the cube along with the rest of arm the conditions
     * of holding the cube are met.
     */
    private void cubeSteering() {
        if (stop && angle10 == 13 * (Math.PI / divider)) {
            tgRotation5.getLocalToVworld(t3dCube);

            t3d.set(new Vector3f(0.0f, -1.7f, 0.0f));
            t3dCube.mul(t3d);
            cube.setTransform(t3dCube);
            holdingCube = true;
        }
    }
    /**
     * Steering by the A-D keys of the robot
     * @param direction specifies the direction in which the robot should turn
     * @param key if recording is turned on, then this key is saved to be able to replay the movement later
     * @see cubeSteering()
     * @see addToReplay()
     */
    private void steeringAD(double direction, char key) {
        t3dStep.rotY(direction * Math.PI / divider);
        tgRotation1.getTransform(t3d);
        t3d.mul(t3dStep);
        tgRotation1.setTransform(t3d);

        holdingCube = false;
        cubeSteering();
        addToReplay(key);
    }
    /**
     * Steering by the W-S keys of the robotr
     * @param direction specifies the direction in which the robot should turn
     * @param key if recording is turned on, then this key is saved to be able to replay the movement later
     * @see cubeSteering()
     * @see addToReplay()
     */
    private void steeringWS(double direction, char key) {
        t3dStep.rotZ(direction * Math.PI / divider);
        tgRotation2.getTransform(t3d);
        t3d.mul(t3dStep);
        tgRotation2.setTransform(t3d);
        angle1 = angle1 - direction * Math.PI / divider;
        angle2 = angle2 + direction * Math.PI / divider;
        holdingCube = false;
        cubeSteering();
        addToReplay(key);
    }
    /**
     * Steering by 2-8 keys of the robot
     * @param direction specifies the direction in which the robot should turn
     * @param key if recording is turned on, then this key is saved to be able to replay the movement later
     * @see cubeSteering()
     * @see addToReplay()
     */
    private void steering28(double direction, char key) {
        t3dStep.rotZ(direction * Math.PI / divider);
        t3dRotation3.mul(t3dStep);
        tgRotation3.setTransform(t3dRotation3);
        angle3 = angle3 - direction * Math.PI / divider;
        angle4 = angle4 + direction * Math.PI / divider;
        holdingCube = false;
        cubeSteering();
        addToReplay(key);
    }
    /**
     * Steering by 4-6 keys of the robot
     * @param direction specifies the direction in which the robot should turn
     * @param key if recording is turned on, then this key is saved to be able to replay the movement later
     * @see cubeSteering()
     * @see addToReplay()
     */
    private void steering46(double direction, char key) {
        t3dStep.rotY(direction * Math.PI / divider);
        t3dRotation4.mul(t3dStep);
        tgRotation4.setTransform(t3dRotation4);
        holdingCube = false;
        cubeSteering();
        addToReplay(key);
    }
    /**
     * Steering of 1-9 keys of the robot
     * @param direction specifies the direction in which the robot should turn
     * @param key if recording is turned on, then this key is saved to be able to replay the movement later
     * @see cubeSteering()
     * @see addToReplay()
     */
    private void steering19(double direction, char key) {
        t3dStep.rotZ(direction * Math.PI / divider);
        t3dRotation4.mul(t3dStep);
        tgRotation4.setTransform(t3dRotation4);
        angle5 = angle5 - direction * Math.PI / divider;
        angle6 = angle6 + direction * Math.PI / divider;
        holdingCube = false;
        cubeSteering();
        addToReplay(key);
    }
    /**
     * Steering of 3-7 keys of the robot
     * @param direction specifies the direction in which the robot should turn
     * @param key if recording is turned on, then this key is saved to be able to replay the movement later
     * @see cubeSteering()
     * @see addToReplay()
     */
    private void steering37(double direction, char key) {
        t3dStep.rotX(direction * Math.PI / divider);
        t3dRotation4.mul(t3dStep);
        tgRotation4.setTransform(t3dRotation4);
        angle7 = angle7 - direction * Math.PI / divider;
        angle8 = angle8 + direction * Math.PI / divider;
        holdingCube = false;
        cubeSteering();
        addToReplay(key);
    }
    /**
     * Steering of M-m keys of the robot
     * @param direction specifies the direction in which the robot should turn
     * @param key if recording is turned on, then this key is saved to be able to replay the movement later
     * @see addToReplay()
     */
    private void steeringMm(double direction, char key) {
        int ile = 13;
        t3dStep.rotZ(ile * direction * Math.PI / divider);
        t3dRotation5.mul(t3dStep);
        tgRotation5.setTransform(t3dRotation5);

        t3dStep.rotZ(ile * -direction * Math.PI / divider);
        t3dRotation6.mul(t3dStep);
        tgRotation6.setTransform(t3dRotation6);

        angle9 = angle9 + direction * ile * Math.PI / divider;
        angle10 = angle10 - direction * ile * Math.PI / divider;
        addToReplay(key);
    }
    /**
     * Function adding pressed key to the moves to be replayed later
     * @param key 
     */
    private void addToReplay(char key) {
        if (recording) {
            moves.append(key);
        }
    }
    /**
     * Function responsible for steering of the robot
     * It also checks if the move that is going to be done is possible, or if the limits are already met
     * It aslo check if there are no colisions between the elements of the robot, and doesn't allow to 
     * hit each other.
     * @param key keyPressed by user
     * @param flag flag specifying type of steering, possible options: 'automatic', 'key', 'remote'
     */
    private void steering(char key, String flag) {
        if (key == 'a') {
            steeringAD(1, key);
        }
        if (key == 'd') {
            steeringAD(-1, key);
        }
        if (key == 'w') {
            if (angle1 < Math.PI / 4) {
                steeringWS(-1, key);
            }
        }
        if (key == 's') {
            checkFloor();
            if (angle2 < Math.PI / 8 && !floorColision) {
                steeringWS(1, key);
            }
        }
        if (key == '8') {
            if (angle3 < Math.PI / 4) {
                steering28(-1, key);
            }
        }
        if (key == '2') {
            checkFloor();
            if (angle4 < Math.PI / 8 && !floorColision) {
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
            if (angle5 < Math.PI / 3) {
                steering19(-1, key);
            }
        }
        if (key == '9') {
            if (angle6 < Math.PI / 3) {
                steering19(1, key);
            }
        }
        if (key == '3') {
            if (angle7 < Math.PI / 3) {
                steering37(-1, key);
            }
        }
        if (key == '7') {
            if (angle8 < Math.PI / 3) {
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
            if (angle10 < 2 * 13 * Math.PI / divider) {
                steeringMm(-1, key);
            }
        }
        if (key == 'M') {
            if (!holdingCube & angle10 - 13 * Math.PI / divider >= 0) {
                steeringMm(1, key);
            }
        }
        if (key == 'n') {
            if (!recording) {
                moves = new StringBuilder();
            }
            recording = true;
        }
        if (key == 'p') {
            recording = false;
            if (moves != null && moves.length() != 0) {
                playing = !playing;
                try {
                    automaticControl();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ArticulatedArmRobot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    /**
     * Override keyTyped funtion, calls steering() function on each keyTyped event
     * @param e 
     */
    @Override
    public void keyTyped(KeyEvent e) {
        char key;
        if (!remote) {
            key = e.getKeyChar();
            if (!playing) {
                steering(key, "key");
            }
        }
    }
    /**
     * Static function to get key of the Map based on the value passed
     * @param map
     * @param value
     * @return key of the map
     */
    static private String getKey(Map<String, String> map, char value) {
        String key = null;
        for (Entry entry : map.entrySet()) {
            if (entry.getValue().equals("" + value)) {
                key = (String) entry.getKey();
                break;
            }
        }
        return key;
    }
    /**
     * Function that has a loop inside, allows to control the robot automaticly,
     * by the previously recorded sequence of moves.
     * @throws InterruptedException 
     * @see steering()
     */
    private void automaticControl() throws InterruptedException {
        String key;
        int length = moves.length() - 1;
        moves.reverse();
        for (int i = 0; i < length; i++) {
            Thread.sleep(32);
            char mapKey = moves.charAt(i);
            key = movesMap.get("" + mapKey);
            if (key == null) {
                key = getKey(movesMap, mapKey);
            }
            steering(key.charAt(0), "automatic");
        }
        Thread.sleep(500);
        moves.reverse();
        for (int i = 0; i < length; i++) {
            Thread.sleep(32);
            key = "" + moves.charAt(i);
            steering(key.charAt(0), "automatic");
        }
        playing = false;
    }
    /**
     * Function allowing for external control based on the socket connection that is made.
     * It has a while loop, so until the connection is not stopped from the other device
     * this function is reading the passed keys from other computer.
     */
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
    /**
     * Utility class allowing to detect collision of robot arm and cube.
     */
    class CollisionDetectorGroup extends Behavior {

        private boolean inCollision = false;
        private final Group group;
        private WakeupOnCollisionEntry wEnter;
        private WakeupOnCollisionExit wExit;

        /**
         * Constructor of the class
         * @param gp 
         */
        public CollisionDetectorGroup(Group gp) {
            group = gp;
            inCollision = false;
        }
        
        /**
         * Override function to initialize the CollisionDetector
         */
        @Override
        public void initialize() {
            wEnter = new WakeupOnCollisionEntry(group);
            wExit = new WakeupOnCollisionExit(group);
            wakeupOn(wEnter);
        }

        /**
         * Override function that is executed when the collision happens.
         * It set's the stop to true if there is a collision between arm and cube,
         * otherwise it's set to false.
         * @param criteria not used
         */
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
