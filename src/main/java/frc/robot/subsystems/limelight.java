package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class limelight extends SubsystemBase {

    private final String name;
    //goal position map
    private Translation2d goalPosition;
    //table with all limelight data
    private NetworkTable data;
    private DoubleSupplier yawSupplier;

    // key -> hostname on limelight dashboard
    public limelight(String key, DoubleSupplier yaw) {
        name = key;
        data = NetworkTableInstance.getDefault().getTable(key);
        goalPosition = new Translation2d(4.7, 4.0); // Hub cetner
        yawSupplier = yaw;
    }


    private double getDouble(String key) {
        return data.getEntry(key).getDouble(0.0);
    }

    private double[] getDoubleArray(String key) {
        return data.getEntry(key).getDoubleArray(new double[0]);
    }

    //checks if target is visible
    public boolean hasTarget() {
        return getDouble("tv") == 1.0;
    }

//can be used for pid/servoing
    //get x offset from center of target tag
    public double getTx() {
        return getDouble("tx");
    }

    //get y offset from center of target tag
    public double getTy() {
        return getDouble("ty");
    }

//robobt pose using megatag2 (check docs)
    public Pose2d getBotPose() {
        double[] pose = getDoubleArray("botpose_orb_wpiblue");

        //docs says so in order to be a pose -> valid target exists
        if (pose.length < 6) return null;

        return new Pose2d(
            new Translation2d(pose[0], pose[1]),
            Rotation2d.fromDegrees(pose[5])
        );
    }

    //needs to be run on loop in order for camera to use megatag2
    public void setRobotOrientation(DoubleSupplier yawDegrees) {
    data.getEntry("robot_orientation_set").setDoubleArray(new double[]{
        yawDegrees.getAsDouble(), 0, 0, 0, 0, 0
    });
    }

    //turn pose into 2d cordinates
    public Translation2d getBotTranslation() {
        Pose2d pose = getBotPose();
        return (pose != null) ? pose.getTranslation() : null;
    }

//distance formula 
    public double getDistanceToGoal() {
        Translation2d robot = getBotTranslation();
        //use -1 to handle errors or just do var < 0
        if (robot == null) return -1.0;
        //distance is treated on a 2d plane
        return robot.getDistance(goalPosition);
    }

    //i guess if you want to change it
    public void setGoalPosition(Translation2d goal) {
        goalPosition = goal;
    }

    public String getName(){
        return name;
    }

    @Override
    public void periodic(){ 
        //make sure limelight has update yaw
        setRobotOrientation(yawSupplier);
    }
}