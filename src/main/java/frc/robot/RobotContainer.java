// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.Kitbotshooter;
import frc.robot.subsystems.fuelintake;

import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

public class RobotContainer {
   
  private CommandXboxController Xbox; 
  public Kitbotshooter shooter;
  private fuelintake Intake;

  


//CTRE SWERVE GENERATED CONTENT DECLARATIONS
    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

  private double MaxSpeed = 1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
  private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity
  /* Setting up bindings for necessary control of the swerve drive platform */
  private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
    .withDeadband(0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
    .withDriveRequestType(DriveRequestType.Velocity); // Use open-loop control for drive motors
  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
  private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();


  public RobotContainer(){
    
    Xbox = new CommandXboxController(Constants.XboxController); 
    shooter = new Kitbotshooter(100, 100);
    Intake = new fuelintake(100, 100);
   configBindings();

  }

  public void configBindings(){
    //shooter test controlls
    Xbox.a().onTrue(shooter.SHOOT());
    Xbox.a().onFalse(shooter.STOP());
    
    //intake controlls 
    Xbox.leftBumper().toggleOnTrue(Intake.ACTIVE());
    Xbox.leftBumper().toggleOnFalse(Intake.IDLE());
    Xbox.leftTrigger().onTrue(Intake.INTAKE());
    Xbox.leftTrigger().onFalse(Intake.STOPINTAKE());


    //THIS MOMENT BEYOND IS CTRE SWERVE GENERATED
    drivetrain.setDefaultCommand(
      // Drivetrain will execute this command periodically
      drivetrain.applyRequest(() ->
      drive.withVelocityX(-Xbox.getLeftY() * MaxSpeed) 
      // Drive forward with negative Y (forward)
      .withVelocityY(-Xbox.getLeftX() * MaxSpeed) 
      // Drive left with negative X (left)
      .withRotationalRate(-Xbox.getRightX() * MaxAngularRate) 
      // Drive counterclockwise with negative X (left)
     )
    );
    // Idle while the robot is disabled. This ensures the configured
    // neutral mode is applied to the drive motors while disabled.
    final var idle = new SwerveRequest.Idle();
    RobotModeTriggers.disabled().whileTrue(
      drivetrain.applyRequest(() -> idle).ignoringDisable(true)
    );

    Xbox.a().whileTrue(drivetrain.applyRequest(() -> brake));
    Xbox.b().whileTrue(drivetrain.applyRequest(() ->
      point.withModuleDirection(new Rotation2d(-Xbox.getLeftY(), -Xbox.getLeftX()))
    ));

    // Run SysId routines when holding back/start and X/Y.
    // Note that each routine should be run exactly once in a single log.
    Xbox.back().and(Xbox.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
    Xbox.back().and(Xbox.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
    Xbox.start().and(Xbox.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
    Xbox.start().and(Xbox.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

    // Reset the field-centric heading on left bumper press.
    Xbox.leftBumper().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

  }
public Command getAutonomousCommand() {
        // Simple drive forward auton
        final var idle = new SwerveRequest.Idle();
        return Commands.sequence(
            // Reset our field centric heading to match the robot
            // facing away from our alliance station wall (0 deg).
            drivetrain.runOnce(() -> drivetrain.seedFieldCentric(Rotation2d.kZero)),
            // Then slowly drive forward (away from us) for 5 seconds.
            drivetrain.applyRequest(() ->
                drive.withVelocityX(0.5)
                    .withVelocityY(0)
                    .withRotationalRate(0)
            )
            .withTimeout(5.0),
            // Finally idle for the rest of auton
            drivetrain.applyRequest(() -> idle)
        );
    }
  
}
  
  
 
