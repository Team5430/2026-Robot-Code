package frc.robot.Configs;

public class FeederConfig {
    public MotorConfig motor = new MotorConfig();

    public double feedSpeed = 0.6;
    public double intakeSpeed = 0.4;
    public double stagingSpeed = 0.2;
    public double reverseSpeed = -0.35;

    public int proximityThreshold = 250;
    public double minRed = 0.35;
    public double minGreen = 0.35;
    public double maxBlue = 0.35;

    public double stagingSeconds = 0.3;
    public double feedSeconds = 0.7;
    public double rejectSeconds = 1.2;
    public double intakeTimeoutSeconds = 4.0;

    public FeederConfig() {
        motor.controller = "TalonFX";
        motor.canId = 1;
    }
}
