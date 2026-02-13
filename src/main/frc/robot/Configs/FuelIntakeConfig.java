package frc.robot.Configs;

import frc.robot.Constants;

public class FuelIntakeConfig {
    public MotorConfig roller = new MotorConfig();
    public MotorConfig pivot = new MotorConfig();
    public int canCoderId = Constants.CANCODER_ID;

    public double idleAngleDeg = 90.0;
    public double intakeAngleDeg = 180.0;
    public double outtakeAngleDeg = 180.0;

    public double idleSpeed = 0.0;
    public double intakeSpeed = 0.6;
    public double outtakeSpeed = -0.6;

    public boolean canCoderCounterClockwisePositive = true;
    public double absoluteSensorDiscontinuityPoint = 1.0;
    public double magnetOffset = 0.0;
    public double sensorToMechanismRatio = 1.0;
    public double rotorToSensorRatio = 80.2;

    public FuelIntakeConfig() {
        roller.controller = "SparkMax";
        roller.canId = Constants.IntakeRoller;
        roller.sparkMotorType = "kBrushless";

        pivot.controller = "TalonFX";
        pivot.canId = Constants.IntakePivot;
    }
}
