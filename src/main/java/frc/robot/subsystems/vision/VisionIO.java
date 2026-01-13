package frc.robot.subsystems.vision;

import edu.wpi.first.math.geometry.Pose2d;

public interface VisionIO {

    //info needed from any type of vision processor
    public poseEstimate getPose();

    //used to identify the cameras
    public String getName();
    
 
}  class poseEstimate{

    //position of the robot in a 2d plane
    public Pose2d pose = Pose2d.kZero;
    //timestamp of camera sample
    public double timestamp = 0;
    //make sure estimate has targets
    public boolean hasTargets = false;




}
