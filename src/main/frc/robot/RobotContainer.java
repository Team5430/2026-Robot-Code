// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Configs.FeederConfig;
import frc.robot.Configs.FuelIntakeConfig;
import frc.robot.Configs.ShooterConfig;
import frc.robot.Configs.VisionConfig;
import Parsing.ConfigLoader;
import frc.robot.subsystems.feeder;
import frc.robot.subsystems.fuelintake;
import frc.robot.subsystems.shooter;
import frc.robot.subsystems.vision;
public class RobotContainer {
   
  private CommandXboxController Xbox; 
  private fuelintake Intake;
  private shooter Shooter;
  private vision Vision;
  private feeder Feeder;
//i recommend to init all objects here, 
  public RobotContainer(){
    
    Xbox = new CommandXboxController(Constants.XboxController); 
    
    FuelIntakeConfig intakeConfig = loadConfig("configs/fuelintake.json", FuelIntakeConfig.class, new FuelIntakeConfig());
    ShooterConfig shooterConfig = loadConfig("configs/shooter.json", ShooterConfig.class, new ShooterConfig());
    VisionConfig visionConfig = loadConfig("configs/vision.json", VisionConfig.class, new VisionConfig());
    FeederConfig feederConfig = loadConfig("configs/feeder.json", FeederConfig.class, new FeederConfig());

    Intake = new fuelintake(intakeConfig);
    Shooter = new shooter(shooterConfig);
    Vision = new vision(visionConfig);
    Feeder = new feeder(feederConfig);


    configBindings();

  }

  public void configBindings(){


    Xbox.a().whileTrue(
      Feeder.FeedToShooter()
      .andThen(Shooter.Shoot(() -> Vision.getDistanceToTarget())));

    Xbox.rightBumper().onTrue(Intake.INTAKE());
    Xbox.leftBumper().onTrue(Intake.OUTTAKE());
    Xbox.y().onTrue(Intake.IDLE());

    Xbox.b().onTrue(Feeder.IntakeFuel());
    Xbox.x().onTrue(Feeder.RejectGamePiece());

  }

  private <T> T loadConfig(String path, Class<T> clazz, T fallback) {
    try {
      return ConfigLoader.loadFromDeploy(path, clazz);
    } catch (Exception e) {
      System.out.println("Config load failed for " + path + ": " + e.getMessage());
      return fallback;
    }
  }
  
}
  
  
 
