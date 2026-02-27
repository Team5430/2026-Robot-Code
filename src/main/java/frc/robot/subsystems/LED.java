package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class led extends SubsystemBase {

    private static final int LED_STRIP_LENGTH = 60;

    private final AddressableLED strip;
    private final AddressableLEDBuffer buffer = new AddressableLEDBuffer(LED_STRIP_LENGTH);


    private static final Timer shiftTimer = new Timer();

    private static final boolean[] ACTIVE_SCHEDULE   = {true,  true,  false, true,  false, true};
    private static final boolean[] INACTIVE_SCHEDULE = {true,  false, true,  false, true,  true};

    // Fudge values so LEDs change slightly BEFORE the actual shift boundary cuz timings
    private static final double APPROACHING_FUDGE = -1.5;
    private static final double ENDING_FUDGE      = -1.5;

    private enum ShiftEnum {
        TRANSITION, SHIFT1, SHIFT2, SHIFT3, SHIFT4, ENDGAME, AUTO, DISABLED
    }

    private record ShiftInfo(ShiftEnum currentShift, boolean active) {}

    private enum LEDState {
        SHOOT      (Color.kGreen),      // Active shift   - go shoot
        NO_SHOOT   (Color.kPink),       // Inactive shift - hold
        TRANSITION (Color.kYellow),     // Shift boundary approaching
        AUTO       (Color.kBlue),       // Autonomous period
        ENDGAME    (Color.kPurple),     // Endgame shift
        DISABLED   (Color.kOrangeRed),  // Robot disabled
        ERROR      (Color.kRed);        // Something is wrong

        private final Color color;

        LEDState(Color color) {
            this.color = color;
        }
    }

    public led(int port) {
        strip = new AddressableLED(port);
        strip.setLength(LED_STRIP_LENGTH);
        strip.start();
    }

    @Override
    public void periodic() {
        applyState(resolveState());
        strip.setData(buffer);
    }


    /** Call this at the start of teleop */
    public void initShiftTimer() {
        shiftTimer.restart();
    }


    //get from driverstation the winner of auto
    private Alliance getFirstActiveAlliance() {
        Alliance alliance = DriverStation.getAlliance().orElse(Alliance.Blue);

        String message = DriverStation.getGameSpecificMessage();
        if (message.length() > 0) {
            char c = message.charAt(0);
            if (c == 'R') return Alliance.Blue;
            if (c == 'B') return Alliance.Red;
        }

        return alliance == Alliance.Blue ? Alliance.Red : Alliance.Blue;
    }

    private boolean[] getSchedule() {
        Alliance startAlliance = getFirstActiveAlliance();
        return startAlliance == DriverStation.getAlliance().orElse(Alliance.Blue)
            ? ACTIVE_SCHEDULE
            : INACTIVE_SCHEDULE;
    }

    //redundancy just in case the driverstation string for who won auto doesnt work
    private ShiftInfo getShiftInfo(boolean[] schedule, double[] startTimes, double[] endTimes) {
        double currentTime = shiftTimer.get();

        if (DriverStation.isAutonomousEnabled()) {
            return new ShiftInfo(ShiftEnum.AUTO, true);
        }

        if (!DriverStation.isEnabled()) {
            return new ShiftInfo(ShiftEnum.DISABLED, false);
        }

        int index = -1;
        for (int i = 0; i < startTimes.length; i++) {
            if (currentTime >= startTimes[i] && currentTime < endTimes[i]) {
                index = i;
                break;
            }
        }

        // Past last shift - endgame
        if (index < 0) index = startTimes.length - 1;

        return new ShiftInfo(ShiftEnum.values()[index], schedule[index]);
    }

    private ShiftInfo getShiftedShiftInfo() {
        boolean[] schedule = getSchedule();

        // Starting active schedule
        if (schedule[1]) {
            double[] shiftedStart = {
                0.0,
                10.0,
                35.0  + ENDING_FUDGE,
                60.0  + APPROACHING_FUDGE,
                85.0  + ENDING_FUDGE,
                110.0 + APPROACHING_FUDGE
            };
            double[] shiftedEnd = {
                10.0,
                35.0  + ENDING_FUDGE,
                60.0  + APPROACHING_FUDGE,
                85.0  + ENDING_FUDGE,
                110.0 + APPROACHING_FUDGE,
                140.0
            };
            return getShiftInfo(schedule, shiftedStart, shiftedEnd);
        }

        // Starting inactive schedule
        double[] shiftedStart = {
            0.0,
            10.0  + ENDING_FUDGE,
            35.0  + APPROACHING_FUDGE,
            60.0  + ENDING_FUDGE,
            85.0  + APPROACHING_FUDGE,
            110.0
        };
        double[] shiftedEnd = {
            10.0  + ENDING_FUDGE,
            35.0  + APPROACHING_FUDGE,
            60.0  + ENDING_FUDGE,
            85.0  + APPROACHING_FUDGE,
            110.0,
            140.0
        };
        return getShiftInfo(schedule, shiftedStart, shiftedEnd);
    }

    //get state based on shift state
    private LEDState resolveState() {
        if (!DriverStation.isEnabled()) return LEDState.DISABLED;

        ShiftInfo shift = getShiftedShiftInfo();

        return switch (shift.currentShift()) {
            case AUTO       -> LEDState.AUTO;
            case ENDGAME    -> LEDState.ENDGAME;
            case TRANSITION -> LEDState.TRANSITION;
            case DISABLED   -> LEDState.DISABLED;
            default         -> shift.active() ? LEDState.SHOOT : LEDState.NO_SHOOT;
        };
    }

    private void applyState(LEDState state) {
        for (int i = 0; i < buffer.getLength(); i++) {
            buffer.setLED(i, state.color);
        }
    }

    public void setColor(Color color) {
        for (int i = 0; i < buffer.getLength(); i++) {
            buffer.setLED(i, color);
        }
        strip.setData(buffer);
    }
}