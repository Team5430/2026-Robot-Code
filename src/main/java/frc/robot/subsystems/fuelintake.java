package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class fuelintake extends SubsystemBase {



    //Motor Declarations
    private TalonSRX IntakeRoller;
    private TalonSRX IntakePivot;

    //Identifying Designated Motor Id's (Id's found in constants)
    public fuelintake(int id1,int id2){

        IntakeRoller = new TalonSRX(id1);
        IntakePivot = new TalonSRX(id2);
        motorconfig();
    }


    public enum IntakeState {
    
    //States of the intake---When Pivot get to a certain angle, the roller motor will go on/off(Angle,RollerSpeed)
    IDLE(90),
    ACTIVE(180);
    
    private final double Angle;
        
        IntakeState(double Angle) {
            this.Angle = Angle;
            
        }
    }
    //Motor ticks to Degrees conversion (2048(motorticks)/360(degrees))
    public static double DegtoTick (double Degrees){
        //This Divides the actual Motor ticks by 360 to show us how many ticks are inside one degree (basic conversions)
        double TicksPerDegree = Constants.motorticks/360;  
        //TicksPerDegree = 5.68 which is our base unit of degrees per ticks.
        //When we multiply it by the degrees we want, we bacically get the total ticks. Ex --> 90° = 511.2 motor ticks.
        double Angle = TicksPerDegree*Degrees;  
        return Angle;
    }

    //Void action that when used in commands will control the speed of the motors
    public void setSpeed(double WantedSpeed) {
        IntakeRoller.set(TalonSRXControlMode.PercentOutput, WantedSpeed);


    }


    //Void action that when used will set our desired state on the motor using an encoder.
    private void setState(IntakeState WantedState) {

        var WantedAngle = DegtoTick(WantedState.Angle);

        IntakePivot.set(TalonSRXControlMode.Position, WantedAngle);
        



    }

    //Command that is binded to button which puts the pivot up  (idle mode)
    public Command IDLE (){
        return Commands.runOnce( ()-> setState(IntakeState.IDLE), this);



    }
    //Command that is binded to button setting the pivot to go down (active intake)
    public Command ACTIVE (){
        return Commands.runOnce(()-> setState(IntakeState.ACTIVE));



    }
    //command that actually puts the set speed action to use in order to be able to use our roller motor
    public Command INTAKE (){
        return Commands.runOnce(()-> setSpeed(.75));


    }
    //command that should stop our intake once we let go (Don't judge this as the easiest way for me to do this).
    public Command STOPINTAKE (){
        return Commands.runOnce (()->setSpeed(0));

    }

    //Our use of an encoder and PID
    //Long story short, it slows down the pivot motor once the intake aproaches its desired angle.
     private void motorconfig(){

       //sets all configs on the TalonSRX back to normal
        IntakePivot.configFactoryDefault();

        //chooses which "sensors" were using (in our case an encoder)
        IntakePivot.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        //tells the sensor its direction (+,-)
        IntakePivot.setSensorPhase(true); 
        //the actual math that is done to slow the pivot once we aproach  our  desired angle
        IntakePivot.config_kP(0, Constants.PivotEncoder);





    }





    






    }

























































