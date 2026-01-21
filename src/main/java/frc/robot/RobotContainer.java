// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Kitbotshooter;
import frc.robot.subsystems.fuelintake;
public class RobotContainer {
   
  private CommandXboxController Xbox; 
  private Kitbotshooter shooter;
  private fuelintake Intake;
//i recommend to init all objects here, 
  public RobotContainer(){
    
    Xbox = new CommandXboxController(Constants.XboxController); 


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
  }
  
}
  
  
 
