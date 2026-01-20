package frc.generic.pivot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.math.geometry.Rotation2d;

public class PivotSRX implements PivotIO {

    private TalonSRX pivot;

    //TUNE
    private double kP = 0.1;
    private double kI = 0.0;
    private double kD = 0.0;


    public PivotSRX(int id){
        pivot = new TalonSRX(id);
        motorConfig();
        
    }


    private void motorConfig(){

         //tell motor on right to follow same output
    //select SRX magencoder for feedback
        pivot.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
    //invert sensor readings
        pivot.setSensorPhase(true);
    //invert motor output ^ does not affect sensor phase
        pivot.setInverted(false);

    //use slot0 on TalonSRX with set PID values
        pivot.config_kP(0, kP);
        pivot.config_kI(0, kI);
        pivot.config_kD(0, kD);

    }

    //convert degrees to ticks as the magencoder reads in ticks; 4096 ticks per rotation per documentation
    private double degreestoTicks(double degrees){
        return degrees * 4096 / 360;
    }

    @Override
    public void setPosition(Rotation2d position) {
        pivot.set(ControlMode.Position, degreestoTicks(position.getDegrees()));
    }

    @Override
    public Rotation2d getPosition() {
        return Rotation2d.fromDegrees(degreestoTicks(pivot.getSelectedSensorPosition()));     
    }
    
}
