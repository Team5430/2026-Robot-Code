package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LED extends SubsystemBase {

  public boolean intakebro = false;

  private AddressableLED stripAddressableLED;

  public LED(int port) {
    stripAddressableLED = new AddressableLED(port);
  }

  private enum LEDState {
    IDLE(Color.kWhite),
    INTAKING(Color.kBrown),
    OUTTAKING(Color.kOrange);

    private Color NEEDEDcolor;

    private LEDState(Color wantedColor) {
      NEEDEDcolor = wantedColor;
    }

    public void setcolors() {}
  }
}
