package frc.robot.subsystems;



import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;



import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;


 

public class Shooter extends SubsystemBase {

    
//declare motors
public   TalonFX Rollormotor;
public   TalonFX Controllermotor;
    
public Shooter(int id10 , int id11){
    Rollormotor = new TalonFX(id10);
    Controllermotor = new TalonFX(id11);

TalonFXConfiguration configs = new TalonFXConfiguration();
    // Set your PID gains (the "tuning" numbers)
    configs.Slot0.kP = 1; // How hard to fight to reach the speed
    configs.Slot0.kV = 0.12; // "Feed-forward" - an initial boost of power
    

    // Apply the configurations to the motor
    Rollormotor.getConfigurator().apply(configs);

}

//this makes a velocity and its being used by the Rollermotor
  public void RunAtVelocity(double targetRPS){
    var velocity = new VelocityVoltage(targetRPS);
    Rollormotor.setControl(velocity); 
  }


//smaller wheel
//action
public void setspeed(double Wspeed) {



}

public void initDefaultCommand() {
    // This tells the robot to run 'MyDefaultCommand' whenever this subsystem is idle
    setDefaultCommand(SHOOT()); 
}

//commands to use 


// Method to check if we are close to the target RPS
public boolean isAtTargetSpeed(double targetRPS) {
    double currentRPS = Rollormotor.getVelocity().getValueAsDouble(); 
    // Check if we are within 0.5 rotations of our goal
    return Math.abs(targetRPS - currentRPS) < 0.5; 
}


public void runFeeder(double speed) {
    Controllermotor.setControl(new DutyCycleOut(speed)); 
}






public Command SHOOT(){
  return Commands.run(()-> isAtTargetSpeed(60), this);
}

public Command TAKE(){
    return Commands.runOnce(()-> runFeeder(1), this);
}

}

