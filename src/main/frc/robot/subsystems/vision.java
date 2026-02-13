package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs.VisionConfig;

public class vision extends SubsystemBase {
    
    private NetworkTable limelight;
    private NetworkTableEntry tv, tx, ty, ta;
    private final VisionConfig config;
    
    public vision(VisionConfig config) {
        this.config = (config == null) ? new VisionConfig() : config;
        limelight = NetworkTableInstance.getDefault().getTable("limelight");
        tv = limelight.getEntry("tv");
        tx = limelight.getEntry("tx");
        ty = limelight.getEntry("ty");
        ta = limelight.getEntry("ta");
        
        setLEDMode(ledModeFromString(this.config.defaultLedMode));
    }
    
    public vision() {
        this(new VisionConfig());
    }
    
    /**
     * Returns distance to target in meters using trigonometry
     */
    public double getDistanceToTarget() {
        if (!hasTarget()) {
            return 0.0;
        }
        
        double targetOffsetAngle_Vertical = ty.getDouble(0.0);
        
        double angleToGoalDegrees = config.limelightAngleDegrees + targetOffsetAngle_Vertical;
        double angleToGoalRadians = Math.toRadians(angleToGoalDegrees);
        
        double distance = (config.targetHeightMeters - config.limelightHeightMeters) / 
                         Math.tan(angleToGoalRadians);
        
        return distance;
    }
    
    /**
     * Returns horizontal offset to target in degrees
     * Positive = target is to the right
     */
    public double getHorizontalOffset() {
        return tx.getDouble(0.0);
    }
    
    /**
     * Returns if limelight sees a valid target
     */
    public boolean hasTarget() {
        return tv.getDouble(0) == 1.0;
    }
    
    /**
     * Returns target area (0-100% of image)
     */
    public double getTargetArea() {
        return ta.getDouble(0.0);
    }
    
    /**
     * Control LED mode
     */
    public void setLEDMode(LEDMode mode) {
        limelight.getEntry("ledMode").setNumber(mode.value);
    }
    
    /**
     * Control pipeline (0-9)
     */
    public void setPipeline(int pipeline) {
        limelight.getEntry("pipeline").setNumber(pipeline);
    }
    
    public enum LEDMode {
        PIPELINE(0),
        OFF(1),
        BLINK(2),
        ON(3);
        
        public final int value;
        LEDMode(int value) {
            this.value = value;
        }
    }
    
    private LEDMode ledModeFromString(String value) {
        if (value == null) {
            return LEDMode.OFF;
        }
        if ("PIPELINE".equalsIgnoreCase(value)) {
            return LEDMode.PIPELINE;
        }
        if ("BLINK".equalsIgnoreCase(value)) {
            return LEDMode.BLINK;
        }
        if ("ON".equalsIgnoreCase(value)) {
            return LEDMode.ON;
        }
        return LEDMode.OFF;
    }
    
    @Override
    public void periodic() {
        // Optional: SmartDashboard logging
        // SmartDashboard.putBoolean("Has Target", hasTarget());
        // SmartDashboard.putNumber("Distance", getDistanceToTarget());
        // SmartDashboard.putNumber("TX", getHorizontalOffset());
    }
}
