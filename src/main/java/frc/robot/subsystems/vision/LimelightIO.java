package frc.robot.subsystems.vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LimelightIO implements VisionIO {
    
    //table with limelight data
    private final NetworkTable table;
    private final String name;
    
    /**
     * Creates a new LimelightIO instance
     * @param limelightName The name of the Limelight (e.g., "limelight" or "limelight-front")
     */
    public LimelightIO(String limelightName) {
        this.name = limelightName;
        this.table = NetworkTableInstance.getDefault().getTable(limelightName);
    }
    
    @Override
    public poseEstimate getPose() {
        poseEstimate estimate = new poseEstimate();
        
        // Check if Limelight has valid targets
        double tv = table.getEntry("tv").getDouble(0);
        estimate.hasTargets = tv == 1.0;
        
        if (!estimate.hasTargets) {
            return estimate;
        }
        
        // Get botpose data (MegaTag2 or MegaTag)
        // botpose format: [x, y, z, roll, pitch, yaw, latency]
        double[] botpose = table.getEntry("botpose_wpiblue").getDoubleArray(new double[7]);
        
        if (botpose.length >= 6) {
            // Extract X, Y, and rotation from botpose
            double x = botpose[0];
            double y = botpose[1];
            double yaw = botpose[5];
            
            estimate.pose = new Pose2d(x, y, Rotation2d.fromDegrees(yaw));
            
            // Calculate timestamp (current time - latency)
            double latencyMs = botpose[6];
            estimate.timestamp = (System.currentTimeMillis() / 1000.0) - (latencyMs / 1000.0);
        }
        
        // Get AprilTag IDs
        double[] tidArray = table.getEntry("tid").getDoubleArray(new double[0]);
        estimate.tagCount = tidArray.length;
        
        // Convert double array to int array for IDs
        for (int i = 0; i < Math.min(tidArray.length, estimate.ids.length); i++) {
            estimate.ids[i] = (int) tidArray[i];
        }
        
        //placeholder
        estimate.ambiguity = .1; 

        return estimate;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Sets the Limelight pipeline
     * @param pipeline Pipeline index to switch to
     */
    public void setPipeline(int pipeline) {
        table.getEntry("pipeline").setNumber(pipeline);
    }
    
    /**
     * Sets the LED mode
     * @param mode LED mode (0 = pipeline default, 1 = force off, 2 = force blink, 3 = force on)
     */
    public void setLEDMode(int mode) {
        table.getEntry("ledMode").setNumber(mode);
    }
    
    /**
     * Gets whether the Limelight currently has any valid targets
     * @return true if targets are detected
     */
    public boolean hasTargets() {
        return table.getEntry("tv").getDouble(0) == 1.0;
    }
    
    /**
     * Gets the number of AprilTags currently detected
     * @return Number of tags detected
     */
    public int getTagCount() {
        double[] tidArray = table.getEntry("tid").getDoubleArray(new double[0]);
        return tidArray.length;
    }
}