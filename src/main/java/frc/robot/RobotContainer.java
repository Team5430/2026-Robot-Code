// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of

// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.FollowPathCommand;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.fuelintake;
import frc.robot.subsystems.led;
import frc.robot.subsystems.limelight;

public class RobotContainer {

  private CommandXboxController Xbox;

  private fuelintake Intake;

  private Shooter L_Shoot;

  private limelight Vision;

  private led LEDControl;

  Command fullShootSequence;
  // CTRE SWERVE GENERATED CONTENT DECLARATIONS

  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

  private final SendableChooser<Command> autoChooser;

  private double MaxSpeed =
      1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed

  private double MaxAngularRate =
      RotationsPerSecond.of(0.75)
          .in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

  /* Setting up bindings for necessary control of the swerve drive platform */

  private final SwerveRequest.FieldCentric drive =
      new SwerveRequest.FieldCentric()
          .withDeadband(0.1)
          .withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
          .withDriveRequestType(
              DriveRequestType.Velocity); // Use open-loop control for drive motors

  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();

  private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();



  public RobotContainer() {
    
  //turn on leds first in order to make timing as accurate as can be
    LEDControl = new led(1);
    LEDControl.initShiftTimer();

    Xbox = new CommandXboxController(Constants.XboxController);

    Intake = new fuelintake(Constants.IntakeRoller, Constants.IntakePivot, Constants.CANID);

    L_Shoot = new Shooter(0,0, true);

    Vision = new limelight("limelight", () -> drivetrain.getPigeon2().getYaw(true).getValueAsDouble());

    // automnomous commands
namedCommands();
    // dashboard chooer
    autoChooser = AutoBuilder.buildAutoChooser();

    SmartDashboard.putData("Auto Mode", autoChooser);

  

    // button bindings
    configBindings();
       

    FollowPathCommand.warmupCommand().schedule();

  }

  public void configBindings() {

    // intake controls

    Xbox.leftBumper().onTrue(Intake.INTAKE());

    //TODO: configure theta lock
    Xbox.leftTrigger().onTrue(drivetrain.thetaLock(new Rotation2d()));

    Xbox.leftBumper().onFalse(Intake.IDLE());

    //TODO: add theta lock when in range for shooting
    

    //shoots based on distance to goal, probably needs boolean before shooting
    Xbox.a().onTrue(L_Shoot.SHOOT(Vision.getDistanceToGoal()));


    drivetrain.setDefaultCommand(

        // Drivetrain will execute this command periodically

        drivetrain.applyRequest(
            () ->
                drive
                    .withVelocityX(-Xbox.getLeftY() * MaxSpeed)

                    // Drive forward with negative Y (forward)

                    .withVelocityY(-Xbox.getLeftX() * MaxSpeed)

                    // Drive left with negative X (left)

                    .withRotationalRate(-Xbox.getRightX() * MaxAngularRate)

            // Drive counterclockwise with negative X (left)

            ));

    // Idle while the robot is disabled. This ensures the configured

    // neutral mode is applied to the drive motors while disabled.

    final var idle = new SwerveRequest.Idle();

    RobotModeTriggers.disabled()
        .whileTrue(drivetrain.applyRequest(() -> idle).ignoringDisable(true));

    Xbox.a().whileTrue(drivetrain.applyRequest(() -> brake));

    Xbox.b()
        .whileTrue(
            drivetrain.applyRequest(
                () ->
                    point.withModuleDirection(new Rotation2d(-Xbox.getLeftY(), -Xbox.getLeftX()))));
  }

  // all commands to be used in auto should be passed into 🤓 nerd 🤓
  private void namedCommands() {

    NamedCommands.registerCommand("INTAKE", Intake.INTAKE());
    NamedCommands.registerCommand("IDLE", Intake.IDLE());
  }

  public Command getAutonomousCommand() {

    return autoChooser.getSelected();
   
  }
}
