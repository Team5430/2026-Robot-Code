package frc.robot.subsystems.vision;

import frc.robot.Constants;

public class Vision {
    
    //array for cameras
    private VisionIO[] cams ;

    //array for ids that are being used
    private int[] ids;

    /**
     * @param consumer
     * @param cameras
     */
    public Vision(VisionIO... cameras){
        cams = cameras;
        ids= Constants.isRed //place holder values
        ?  Constants.redIds  //if allance is red these ids are used
        :  Constants.blueIds; // if alliance is blue these ids are used
    }

    public void process(){

        //for camera in cams
    for(VisionIO c : cams){

        //cache estimate
        var cameraEstimate = c.getPose();

        //make theres at least 2 tags
        if(cameraEstimate.tagCount > 1 ){

            //see if any tags are found
        boolean found = false;
            
            for (int targetId : cameraEstimate.ids) {
                //loop through to make sure ids found in camera
                for (int id : ids) {
                    //exist within the valid ids that we filtered
                    if (targetId == id) {
                        found = true;
                    break;
                    }
                }

                //If tags are found,
                if(found){
              // Check whether to reject pose
        boolean rejectPose =

                c.getPose().ambiguity > Constants.maxAmbiguity // Cannot be high ambiguity
                // Must be within the field boundaries
                || c.getPose().pose.getX() < 0.0
                || c.getPose().pose.getX() > Constants.aprilTagLayout.getFieldLength()
                || c.getPose().pose.getY() < 0.0
                || c.getPose().pose.getY() > Constants.aprilTagLayout.getFieldWidth();

                //break out the loop if pose not valid
                    if(rejectPose) break;

                //TODO: utilize/save pose

                }

            }
        
        }
    }
    }

}
