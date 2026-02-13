package frc.robot.Configs;

public class MotorConfig {
    public String controller = ""; // "TalonFX" or "SparkMax"
    public int canId = 0;
    public boolean inverted = false;
    public String idleMode = "Coast"; // "Brake" or "Coast"

    // SparkMax only
    public String sparkMotorType = "kBrushless"; // "kBrushless" or "kBrushed"

    // Reserved for future standardization
    public double supplyCurrentLimit = 0.0;
    public double statorCurrentLimit = 0.0;
    public double openLoopRampSeconds = 0.0;
    public double closedLoopRampSeconds = 0.0;

    public MotorConfig() {}
}
