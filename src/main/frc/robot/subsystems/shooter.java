package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs.ShooterConfig;
import frc.robot.hardware.MotorFactory;

public class shooter extends SubsystemBase {
    
    private TalonFX motor;
    private InterpolatingDoubleTreeMap LUT = new InterpolatingDoubleTreeMap();
    private final ShooterConfig config;


    public shooter(ShooterConfig config){
        this.config = (config == null) ? new ShooterConfig() : config;
        motor = MotorFactory.createTalonFX(this.config.motor);
        setLUT();
        motorConfig();
    }
    
    public shooter(int id){
        this(configFromId(id));
    }
    
    private void setLUT(){
        LUT.clear();
        for (ShooterConfig.ShotPoint point : config.shots) {
            LUT.put(point.distanceMeters, point.rps);
        }
    }

    private void motorConfig(){

    }

    private VelocityVoltage VelocityController = new VelocityVoltage(0);

    private void setRPM(double distance){
        double rps = LUT.get(distance);
        motor.setControl(VelocityController.withVelocity(rps));
    }

    /*
     * 
     * get distance -> rps
     * wait for motor to get to wanted rps 
     * 
     * 
     */

     //return if motor is close to wanted target velocity
    private boolean atTargetRPS(){
         return Math.abs(motor.getVelocity().getValueAsDouble() - VelocityController.Velocity) < config.atTargetToleranceRps;
    }

    public Command Shoot(DoubleSupplier distanceSupplier){
        return Commands.sequence(
        Commands.runOnce(() -> setRPM(distanceSupplier.getAsDouble())),
        Commands.waitUntil(this::atTargetRPS).withTimeout(config.spinupTimeoutSeconds)
        // trigger your feed mechanism here
    ).withTimeout(config.shotTimeoutSeconds); // safety timeout
        
    }

    private static ShooterConfig configFromId(int id) {
        ShooterConfig cfg = new ShooterConfig();
        cfg.motor.canId = id;
        return cfg;
    }
}
