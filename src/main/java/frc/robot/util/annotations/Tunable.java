package frc.robot.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as tunable, automatically publishing it to NetworkTables
 * for real-time adjustment during testing.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Tunable {
    /**
     * Display name on dashboard (defaults to field name if empty)
     */
    String name() default "";
    
    /**
     * Default value for the tunable parameter
     */
    double defaultValue() default 0.0;
    
    /**
     * Whether this tunable is persistent across robot restarts
     */
    boolean persistent() default false;
}

