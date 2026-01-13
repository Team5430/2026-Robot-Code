package frc.robot;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class Constants {
    
    public static boolean isRed = DriverStation.getAlliance().get() == Alliance.Blue;
    public static int[] redIds = new int[] { 1,2,3,45,5};
    public static int[] blueIds = new int[] { 9,8,7};

    public static AprilTagFieldLayout aprilTagLayout =
      AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

    public static double maxAmbiguity = .2;
    
}

