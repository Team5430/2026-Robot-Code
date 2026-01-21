package frc.robot.subsystems;

//ctre imports

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class Kitbotshooter extends SubsystemBase {

    private TalonSRX KitbotShooterL;
    private TalonSRX KitbotShooterR;

 public Kitbotshooter(int id4,int id5){

        KitbotShooterR = new TalonSRX(id4);
        KitbotShooterL = new TalonSRX(id5);
 }

 public void setSpeed(double WantedSpeed) {
        KitbotShooterL.set(TalonSRXControlMode.PercentOutput, WantedSpeed);
        KitbotShooterR.set(TalonSRXControlMode.PercentOutput, WantedSpeed);


}




public Command SHOOT(){

    return Commands.runOnce(()-> setSpeed(1), this);

}


public Command STOP(){

    return Commands.runOnce(()-> setSpeed(0), this);

}








}
