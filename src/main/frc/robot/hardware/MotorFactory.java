package frc.robot.hardware;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import frc.robot.Configs.MotorConfig;

public final class MotorFactory {
    private MotorFactory() {}

    public static TalonFX createTalonFX(MotorConfig config) {
        MotorConfig cfg = (config == null) ? new MotorConfig() : config;
        warnIfControllerMismatch(cfg, "TalonFX");
        return new TalonFX(cfg.canId);
    }

    public static SparkMax createSparkMax(MotorConfig config) {
        MotorConfig cfg = (config == null) ? new MotorConfig() : config;
        warnIfControllerMismatch(cfg, "SparkMax");
        return new SparkMax(cfg.canId, motorTypeFromString(cfg.sparkMotorType));
    }

    private static MotorType motorTypeFromString(String value) {
        if (value == null) {
            return MotorType.kBrushless;
        }
        if ("kBrushed".equalsIgnoreCase(value) || "brushed".equalsIgnoreCase(value)) {
            return MotorType.kBrushed;
        }
        return MotorType.kBrushless;
    }

    private static void warnIfControllerMismatch(MotorConfig cfg, String expected) {
        if (cfg.controller == null || cfg.controller.isBlank()) {
            return;
        }
        if (!expected.equalsIgnoreCase(cfg.controller)) {
            System.out.println("MotorConfig controller mismatch: expected " + expected + " but got " + cfg.controller);
        }
    }
}
