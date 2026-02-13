package frc.robot.subsystems;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs.FuelIntakeConfig;
import frc.robot.hardware.MotorFactory;

public class fuelintake extends SubsystemBase {

    //Motor Declarations
    private SparkMax IntakeRoller;
    private TalonFX IntakePivot;
    private final FuelIntakeConfig config;



    //Identifying Designated Motor Id's (Id's found in constants)
    public fuelintake(FuelIntakeConfig config){
        this.config = (config == null) ? new FuelIntakeConfig() : config;
        IntakeRoller = MotorFactory.createSparkMax(this.config.roller);
        IntakePivot = MotorFactory.createTalonFX(this.config.pivot);
        motorconfig();
    }

    public fuelintake(int roller, int pivot, int encoder){
        this(configFromIds(roller, pivot, encoder));
    }

    public enum IntakeState {
    
    //States of the intake---When Pivot get to a certain angle, the roller motor will go on/off(Angle,RollerSpeed)
    IDLE,
    OUTTAKE,
    INTAKE;
    }



    //Void action that when used in commands will control the speed of the motors
    public void setSpeed(double WantedSpeed) {
        IntakeRoller.set(WantedSpeed);
    }


    //declare this in order to be able to control position, there is different control requests
    private PositionDutyCycle posController = new PositionDutyCycle(0);

    private IntakeState currentState = IntakeState.IDLE;

    //Void action that when used will set our desired state on the motor using an encoder.
    private void setState(IntakeState WantedState) {
        currentState = WantedState;
        Rotation2d targetAngle = Rotation2d.fromDegrees(getStateAngleDeg(WantedState));
        IntakePivot.setControl(posController.withPosition(targetAngle.getRotations()));
        setSpeed(getStateSpeed(WantedState));
    }

    //Command that is binded to button which puts the pivot up  (idle mode)
    public Command IDLE (){
        return Commands.runOnce( ()-> setState(IntakeState.IDLE), this);
    }

    //Command that is binded to button setting the pivot to go down (active intake)
    public Command INTAKE (){
        return Commands.runOnce(()-> setState(IntakeState.INTAKE), this);
    }

    public Command OUTTAKE (){
        return Commands.runOnce(() -> setState(IntakeState.OUTTAKE), this);
    }


    //Our use of an encoder and PID
    //Long story short, it slows down the pivot motor once the intake aproaches its desired angle.
     private void motorconfig(){

        // Initialize hardware
        // Configure CANcoder
        CANcoderConfiguration canCoderConfig = new CANcoderConfiguration();
        
        // Set the direction of the CANcoder (match your mechanism)
        canCoderConfig.MagnetSensor.SensorDirection = config.canCoderCounterClockwisePositive
                ? SensorDirectionValue.CounterClockwise_Positive
                : SensorDirectionValue.Clockwise_Positive;
        
        // Set absolute sensor range (0 to 1 rotation or -0.5 to 0.5 rotation)
        canCoderConfig.MagnetSensor.AbsoluteSensorDiscontinuityPoint = config.absoluteSensorDiscontinuityPoint;
        
        // Set magnet offset if needed (in rotations, 0 to 1)
        canCoderConfig.MagnetSensor.MagnetOffset = config.magnetOffset;
        
        
        // Configure TalonFX
        TalonFXConfiguration motorConfig = new TalonFXConfiguration();
        
        // Configure feedback settings to use remote CANcoder
        FeedbackConfigs feedbackConfigs = motorConfig.Feedback;
        
        // Set the feedback source to RemoteCANcoder
        feedbackConfigs.FeedbackRemoteSensorID = config.canCoderId;
        feedbackConfigs.FeedbackSensorSource = FeedbackSensorSourceValue.RemoteCANcoder;
        
        // Set the gear ratio between CANcoder and mechanism
        // This is sensor-to-mechanism ratio
        // Example: if CANcoder is 1:1 with output shaft, use 1.0
        // If there's a 10:1 reduction between CANcoder and mechanism, use 10.0
        feedbackConfigs.SensorToMechanismRatio = config.sensorToMechanismRatio;
        
        // Set the gear ratio between motor rotor and CANcoder
        // Example: if motor drives CANcoder through a 12.8:1 reduction, use 12.8
        feedbackConfigs.RotorToSensorRatio = config.rotorToSensorRatio;
        
        // Apply TalonFX configuration
        IntakePivot.getConfigurator().apply(motorConfig);
        
    



    }

    private static FuelIntakeConfig configFromIds(int roller, int pivot, int encoder) {
        FuelIntakeConfig cfg = new FuelIntakeConfig();
        cfg.roller.canId = roller;
        cfg.pivot.canId = pivot;
        cfg.canCoderId = encoder;
        return cfg;
    }

    private double getStateAngleDeg(IntakeState state) {
        switch (state) {
            case OUTTAKE:
                return config.outtakeAngleDeg;
            case INTAKE:
                return config.intakeAngleDeg;
            case IDLE:
            default:
                return config.idleAngleDeg;
        }
    }

    private double getStateSpeed(IntakeState state) {
        switch (state) {
            case OUTTAKE:
                return config.outtakeSpeed;
            case INTAKE:
                return config.intakeSpeed;
            case IDLE:
            default:
                return config.idleSpeed;
        }
    }





    






    }

























































