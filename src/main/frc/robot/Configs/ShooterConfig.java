package frc.robot.Configs;

import java.util.ArrayList;
import java.util.List;

import frc.robot.Constants;

public class ShooterConfig {
    public MotorConfig motor = new MotorConfig();

    public double atTargetToleranceRps = 0.2;
    public double spinupTimeoutSeconds = 1.5;
    public double shotTimeoutSeconds = 2.0;

    public List<ShotPoint> shots = new ArrayList<>();

    public ShooterConfig() {
        motor.controller = "TalonFX";
        motor.canId = Constants.KitbotShooterL;
        // Defaults mirror existing LUT values
        shots.add(new ShotPoint(3.0, 80.0));
        shots.add(new ShotPoint(4.0, 90.0));
    }

    public static class ShotPoint {
        public double distanceMeters;
        public double rps;

        public ShotPoint() {}

        public ShotPoint(double distanceMeters, double rps) {
            this.distanceMeters = distanceMeters;
            this.rps = rps;
        }
    }
}
