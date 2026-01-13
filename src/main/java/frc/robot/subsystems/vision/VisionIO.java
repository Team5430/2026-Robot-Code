package frc.robot.subsystems.vision;

import edu.wpi.first.math.geometry.Pose2d;

public interface VisionIO {

    //info needed from any type of vision processor
    public poseEstimate getPose();

    //used to identify the cameras
    public String getName();


    public class poseEstimate{

        //position of the robot in a 2d plane
        public Pose2d pose = Pose2d.kZero;
        //timestamp of camera sample
        public double timestamp = 0;
        //make sure estimate has targets
        public boolean hasTargets = false;
        //ids gathered from the samples
        public int[] ids = new int[10];
        //number of tags present
        public int tagCount = 0;
        // how ambigous is the sample
        public double ambiguity;
    }

 
}  
