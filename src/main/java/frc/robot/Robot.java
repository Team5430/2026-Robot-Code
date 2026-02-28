// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDPattern.GradientType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private final RobotContainer m_robotContainer;

  private final AddressableLED m_led = new AddressableLED(1);

  private AddressableLEDBuffer buffer = new AddressableLEDBuffer(60);

  public Robot() {
    m_led.setLength(buffer.getLength());

    // color scheme of sunset shimmer from my litte pony ❤️😌
    LEDPattern sunsetShimmer =
        LEDPattern.gradient(GradientType.kDiscontinuous, Color.kDarkRed, Color.kRed, Color.kOrange);
    // value is blend, satuation is color powerfullness
    sunsetShimmer = LEDPattern.rainbow(600, 20);
    // apply sunset
    sunsetShimmer.applyTo(buffer);
    //
    m_led.setData(buffer);
    // start sunset
    m_led.start();

    m_robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    CommandScheduler.getInstance().schedule(m_autonomousCommand);
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}
}
