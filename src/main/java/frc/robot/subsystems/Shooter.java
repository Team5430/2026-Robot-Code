package frc.robot.subsystems;



import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;



import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;


 

public class Shooter extends SubsystemBase {

    
//declare motors
private   TalonFX innerMotor;
private   TalonFX outerMotor;

private InterpolatingDoubleTreeMap lut = new InterpolatingDoubleTreeMap();
    
public Shooter(int inner , int outer, boolean inverted){
    innerMotor = new TalonFX(inner);
    outerMotor = new TalonFX(outer);

TalonFXConfiguration configs = new TalonFXConfiguration();
    // Set your PID gains (the "tuning" numbers)
    configs.Slot0.kP = 1; // How hard to fight to reach the speed
    configs.Slot0.kV = 0.12; // "Feed-forward" - an initial boost of power
    
    

    // Apply the configurations to the motor
    innerMotor.getConfigurator().apply(configs);
    configs.MotorOutput.Inverted = inverted ? InvertedValue.CounterClockwise_Positive : InvertedValue.Clockwise_Positive;
    outerMotor.getConfigurator().apply(configs);

  addPoints();
}

private void addPoints(){
  //(distance from target, RPS) tats the format that points need to be added in
  //the more the merrier fr
  lut.put(1.0, 50.0);
  lut.put(2.0, 55.0);

}

VelocityVoltage posController = new VelocityVoltage(0);

//this makes a velocity and its being used by the Rollermotor
  public void RunAtVelocity(double targetRPS){
    posController.Velocity = targetRPS;
    innerMotor.setControl(posController);
    outerMotor.setControl(posController); 
  }

  public Command SHOOT(double distanceToTarget){
    var rps = lut.get(distanceToTarget);
    return Commands.runOnce(() -> RunAtVelocity(rps), this);
  }


// Method to check if we are close to the target RPS
public boolean isAtTargetSpeed(double targetRPS) {
    double currentRPS = innerMotor.getVelocity().getValueAsDouble(); 
    // Check if we are within 0.5 rotations of our goal
    return Math.abs(targetRPS - currentRPS) < 0.5; 
}




}

