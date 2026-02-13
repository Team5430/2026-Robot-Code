package frc.robot.subsystems;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs.FeederConfig;
import frc.robot.hardware.MotorFactory;

public class feeder extends SubsystemBase {
    
    private TalonFX motor;
    private ColorSensorV3 sensor;
    private FeederState currentState = FeederState.IDLE;
    private final FeederConfig config;
    
    private DutyCycleOut dutyCycleControl = new DutyCycleOut(0);

    public feeder(FeederConfig config) {
        this.config = (config == null) ? new FeederConfig() : config;
        motor = MotorFactory.createTalonFX(this.config.motor);
        sensor = new ColorSensorV3(Port.kOnboard);
    }
    
    public feeder(int id) {
        this(configFromId(id));
    }

    private enum FeederState {
        IDLE(0.0),
        FEEDING(0.0),
        INTAKING(0.0),
        STAGING(0.0),
        REJECT(0.0);

        public final double speed;
        
        private FeederState(double speed) {
            this.speed = speed;
        }
    }

    /**
     * Set the feeder state
     */
    private void setState(FeederState state) {
        currentState = state;
        motor.setControl(dutyCycleControl.withOutput(getStateSpeed(state)));
    }

    /**
     * Check if FUEL is detected with proximity AND yellow color verification
     */
    public boolean hasFuel() {
        return sensor.getProximity() > config.proximityThreshold && isYellowFuel();
    }

    /**
     * Basic proximity check (for initial detection)
     */
    public boolean hasGamePiece() {
        return sensor.getProximity() > config.proximityThreshold;
    }

    /**
     * Verify the detected object is yellow FUEL (not opponent game piece)
     */
    private boolean isYellowFuel() {
        Color detected = sensor.getColor();
        
        // Yellow = high red + high green + low blue
        boolean isYellow = detected.red > config.minRed && 
                          detected.green > config.minGreen && 
                          detected.blue < config.maxBlue;
        
        return isYellow;
    }

    /**
     * Get current proximity value for tuning
     */
    public int getProximity() {
        return sensor.getProximity();
    }

    /**
     * Get detected color (for dashboard/debugging)
     */
    public Color getColor() {
        return sensor.getColor();
    }

    /**
     * Command to intake FUEL until sensor detects it
     * Includes staging phase to pull foam ball fully into position
     */
    public Command IntakeFuel() {
        return Commands.sequence(
            // Initial intake
            Commands.runOnce(() -> setState(FeederState.INTAKING)),
            Commands.waitUntil(this::hasGamePiece),
            
            // Stage the FUEL - pull it in fully (foam compresses, needs extra time)
            Commands.runOnce(() -> setState(FeederState.STAGING)),
            Commands.waitSeconds(config.stagingSeconds),
            
            // Stop and verify we have yellow FUEL
            Commands.runOnce(() -> setState(FeederState.IDLE)),
            Commands.runOnce(() -> {
                if (!isYellowFuel()) {
                    System.out.println("Warning: Non-FUEL game piece detected!");
                }
            })
        ).withTimeout(config.intakeTimeoutSeconds)
         .finallyDo(() -> setState(FeederState.IDLE))
         .withName("IntakeFuel");
    }

    /**
     * Command to feed FUEL to shooter
     * Longer timing for foam ball exit
     */
    public Command FeedToShooter() {
        return Commands.sequence(
            Commands.runOnce(() -> setState(FeederState.FEEDING)),
            Commands.waitSeconds(config.feedSeconds),
            Commands.runOnce(() -> setState(FeederState.IDLE))
        ).withName("FeedToShooter");
    }

    /**
     * Command to reject wrong game piece or clear jam
     */
    public Command RejectGamePiece() {
        return Commands.sequence(
            Commands.runOnce(() -> setState(FeederState.REJECT)),
            Commands.waitSeconds(config.rejectSeconds),
            Commands.runOnce(() -> setState(FeederState.IDLE))
        ).withName("RejectGamePiece");
    }

    /**
     * Auto-reject non-FUEL game pieces
     */
    public Command IntakeWithAutoReject() {
        return Commands.either(
            IntakeFuel(),
            Commands.sequence(
                Commands.print("Wrong game piece detected - rejecting!"),
                RejectGamePiece()
            ),
            this::isYellowFuel
        );
    }

    /**
     * Manual control command (for testing/tuning)
     */
    public Command ManualFeed(double speed) {
        return Commands.run(
            () -> motor.setControl(dutyCycleControl.withOutput(speed)),
            this
        );
    }

    /**
     * Stop the feeder
     */
    public Command Stop() {
        return Commands.runOnce(() -> setState(FeederState.IDLE));
    }

    @Override
    public void periodic() {
        // Useful for tuning color detection
        // SmartDashboard.putBoolean("Has FUEL", hasFuel());
        // SmartDashboard.putBoolean("Is Yellow", isYellowFuel());
        // SmartDashboard.putNumber("Proximity", getProximity());
        // SmartDashboard.putNumber("Red", sensor.getColor().red);
        // SmartDashboard.putNumber("Green", sensor.getColor().green);
        // SmartDashboard.putNumber("Blue", sensor.getColor().blue);
        // SmartDashboard.putString("Feeder State", currentState.name());
    }

    private static FeederConfig configFromId(int id) {
        FeederConfig cfg = new FeederConfig();
        cfg.motor.canId = id;
        return cfg;
    }

    private double getStateSpeed(FeederState state) {
        switch (state) {
            case FEEDING:
                return config.feedSpeed;
            case INTAKING:
                return config.intakeSpeed;
            case STAGING:
                return config.stagingSpeed;
            case REJECT:
                return config.reverseSpeed;
            case IDLE:
            default:
                return 0.0;
        }
    }
}
