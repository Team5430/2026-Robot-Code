package frc.generic.Rollers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class RollersSRX implements RollersIO {

    private TalonSRX roller;

    public RollersSRX(int id){
        roller = new TalonSRX(id);
    }

    @Override
    public void setSpeed(double speed) {
        roller.set(ControlMode.PercentOutput, speed);
    }
    

}
