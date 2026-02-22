package frc.robot.subsystems;

import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicDutyCycle;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// ab code, 0.243 is '0'
public class fuelintake extends SubsystemBase {

  // Motor Declarations

  private SparkMax IntakeRoller;

  private TalonFX IntakePivot;

  private MotionMagicDutyCycle controller;
  private CANcoder CanCode;

  // Identifying Designated Motor Id's (Id's found in constants)

  public fuelintake(int RollerID, int PivotID, int CANID) {

    IntakePivot = new TalonFX(PivotID);

    controller = new MotionMagicDutyCycle(0);

    IntakeRoller = new SparkMax(RollerID, MotorType.kBrushless);
    CanCode = new CANcoder(CANID);

    motorconfig();
  }

  public enum IntakeState {

    // States of the intake---When Pivot get to a certain angle, the roller motor will go
    // on/off(Angle,RollerSpeed)

    IDLE(3, 0),

    OUTTAKE(-20, .6), // Not sure about speeds, test them out

    INTAKE(-20, -0.6);

    public final double Position;

    public final double Speed;

    private IntakeState(double position, double speed) {

      Position = position;

      Speed = speed;
    }
  }

  // Void action that when used in commands will control the speed of the motors

  public void setSpeed(double WantedSpeed) {

    IntakeRoller.set(WantedSpeed);
  }

  // declare this in order to be able to control position, there is different control requests

  private IntakeState currentState = IntakeState.IDLE;

  // Void action that when used will set our desired state on the motor using an encoder.

  private void setState(IntakeState WantedState) {

    currentState = WantedState;

    IntakePivot.setControl(controller.withPosition(currentState.Position));

    setSpeed(currentState.Speed);
  }

  // Command that is binded to button which puts the pivot up  (idle mode)

  public Command IDLE() {

    return Commands.runOnce(() -> setState(IntakeState.IDLE), this);
  }

  // Command that is binded to button setting the pivot to go down (active intake)

  public Command INTAKE() {

    return Commands.runOnce(() -> setState(IntakeState.INTAKE), this);
  }

  public Command OUTTAKE() {

    return Commands.runOnce(() -> setState(IntakeState.OUTTAKE), this);
  }

  // Our use of an encoder and PID

  // Long story short, it slows down the pivot motor once the intake aproaches its desired angle.

  private void motorconfig() {

    // Configure TalonFX

    TalonFXConfiguration motorConfig = new TalonFXConfiguration();

    // Configure feedback settings to use remote CANcoder

    FeedbackConfigs feedbackConfigs = motorConfig.Feedback;

    feedbackConfigs.FeedbackRemoteSensorID = CanCode.getDeviceID();
    feedbackConfigs.FeedbackSensorSource = FeedbackSensorSourceValue.RemoteCANcoder;

    Slot0Configs pidConfigs = motorConfig.Slot0;

    pidConfigs.kP = 0.011;

    MotionMagicConfigs motionMagicConfigs = motorConfig.MotionMagic;

    motionMagicConfigs.MotionMagicAcceleration = 275;

    motionMagicConfigs.MotionMagicCruiseVelocity = 210;

    motionMagicConfigs.MotionMagicExpo_kA = 0.10000000149011612;

    motionMagicConfigs.MotionMagicExpo_kV = 0.11999999731779099;
    // Set the feedback source to RemoteCANcoder

    // Set the gear ratio between CANcoder and mechanism

    // This is sensor-to-mechanism ratio

    // Example: if CANcoder is 1:1 with output shaft, use 1.0

    // If there's a 10:1 reduction between CANcoder and mechanism, use 10.0

    // Set the gear ratio between motor rotor and CANcoder

    // Example: if motor drives CANcoder through a 12.8:1 reduction, use 12.8

    feedbackConfigs.FeedbackSensorSource = FeedbackSensorSourceValue.RotorSensor;

    feedbackConfigs.SensorToMechanismRatio = 1;

    feedbackConfigs.RotorToSensorRatio = 80.123;

    // Apply TalonFX configuration

    IntakePivot.getConfigurator().apply(motorConfig);
  }
}
