package frc.robot.util.annotations;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Processes @Tunable annotations and manages NetworkTables entries
 * Automatically tracks all registered objects for updates
 */
public class TunableProcessor {
    
    private static final Map<Field, NetworkTableEntry> entries = new HashMap<>();
    private static final Set<Object> registeredObjects = ConcurrentHashMap.newKeySet();
    private static final NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();
    private static final NetworkTable table = ntInstance.getTable("Tunable");
    
    /**
     * Registers all @Tunable fields in the given object
     * Automatically tracks the object for periodic updates
     * @param obj Object to scan for @Tunable annotations
     */
    public static void register(Object obj) {
        if (obj == null || registeredObjects.contains(obj)) {
            return;
        }
        
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Tunable.class)) {
                processTunableField(obj, field);
            }
        }
        
        // Track this object for automatic updates
        registeredObjects.add(obj);
    }
    
    private static void processTunableField(Object obj, Field field) {
        Tunable annotation = field.getAnnotation(Tunable.class);
        field.setAccessible(true);
        
        // Determine display name
        String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
                
        try {
            // Get current value or use default
            Object currentValue = field.get(obj);
            double value = (currentValue != null) 
                ? ((Number) currentValue).doubleValue() 
                : annotation.defaultValue();
            
            // Create entry
            NetworkTableEntry entry = table.getEntry(name);
            entry.setDouble(value);
            
            // Store for periodic updates
            entries.put(field, entry);
            
            // Set initial value
            field.set(obj, value);
            
        } catch (IllegalAccessException e) {
            System.err.println("Failed to register tunable field: " + field.getName());
            e.printStackTrace();
        }
    }
    
    /**
     * Updates ALL registered tunable fields with current NetworkTables values
     * Call this once in robotPeriodic() to update everything automatically
     */
    public static void updateAll() {
        for (Object obj : registeredObjects) {
            update(obj);
        }
    }
    
    /**
     * Updates all registered tunable fields for a specific object
     * @param obj Object whose tunables should be updated
     */
    public static void update(Object obj) {
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Tunable.class)) {
                updateTunableField(obj, field);
            }
        }
    }
    

    /**
     * updates the network table entry with the new value
     * @param obj
     * @param field
     */
    private static void updateTunableField(Object obj, Field field) {
        NetworkTableEntry entry = entries.get(field);
        if (entry == null) return;
        
        try {
            field.setAccessible(true);
            double value = entry.getDouble(0.0);
            
            // Set the appropriate type
            Class<?> type = field.getType();
            if (type == double.class || type == Double.class) {
                field.set(obj, value);
            } else if (type == float.class || type == Float.class) {
                field.set(obj, (float) value);
            } else if (type == int.class || type == Integer.class) {
                field.set(obj, (int) value);
            } else if (type == long.class || type == Long.class) {
                field.set(obj, (long) value);
            }
            
        } catch (IllegalAccessException e) {
            System.err.println("Failed to update tunable field: " + field.getName());
        }
    }
    
    /**
     * Unregisters an object from automatic updates
     * @param obj Object to unregister
     */
    public static void unregister(Object obj) {
        registeredObjects.remove(obj);
    }
    
    /**
     * flush all entries
     */
    public static void clearAll() {
        registeredObjects.clear();
        entries.clear();
    }
}