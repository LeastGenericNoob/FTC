package org.firstinspires.ftc.ftcRealSteel;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "AutoMain")
public class AutoMain extends LinearOpMode {

    int team = 0; // 0 for blue 1 for red

    DcMotor fl;
    DcMotor fr;
    DcMotor bl;
    DcMotor br;
    DcMotor intake;
    DcMotor midintake;
    DcMotor flywheel;


    double tick = 537.7;
    double newtarget;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    int step = 0;
    boolean done = false;
    int seed = -1;

    @Override
    public void runOpMode() {
        fl = hardwareMap.dcMotor.get("frontleft");
        fr = hardwareMap.dcMotor.get("frontright");
        bl = hardwareMap.dcMotor.get("backleft");
        br = hardwareMap.dcMotor.get("backright");
        intake = hardwareMap.dcMotor.get("intake");
        midintake = hardwareMap.dcMotor.get("midintake");
        flywheel = hardwareMap.dcMotor.get("flywheel");

        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);

        encoderInit(fl);
        encoderInit(fr);
        encoderInit(bl);
        encoderInit(br);

        initAprilTag();

        while (!isStarted()) {
            if (gamepad1.a || gamepad2.a) {
                team = 0;
            }
            if (gamepad1.b || gamepad2.b) {
                team = 1;
            }
            telemetry.addData("team:", team);
            telemetry.update();

        }

        waitForStart();
        boolean once = false;
        while (opModeIsActive()) {

            seed = identifyObelisk();


            telemetryAprilTag();

            telemetry.addData("Seed:", seed);
            telemetry.update();

            //Pseudocode for Blue
            //Strafe 2 blocks right
            //move 1 block back
            //analyze obelisk
            //MAYBE rotate balls to pattern
            //aim via apriltags and shoot
            //based on obelisk do movement patterns

            //eg PPG
            //rotate to right
            //move back and intake balls
            //move front
            //aim via apriltags and shoot

            if (step == 0){
                driveForward(2);
                waitUntilDone();
                sleep(1000);
            }
            if (step == 1) {
                rotate(1.6);
                waitUntilDone();
                sleep(1000);
            }
            if (step == 2) {
                strafe(2);
                waitUntilDone();
                sleep(1000);
            }

            if (step == 3) {
                telemetry.addData("We Are", "Done");
            }



            //telemetryAprilTag();
            // Push telemetry to the Driver Station.

            telemetry.addData("Step Number:", step);
            telemetry.addData("Seed:", seed);
            telemetry.addData("team", team);
            telemetry.update();

        }
        visionPortal.close();
    }



    private void waitUntilDone() {
        while (!(fr.getCurrentPosition() == fr.getTargetPosition())){
            sleep(10);
            telemetry.addData("waiting", "kalsdf");
            telemetry.update();
        }
        step+=1;
        done=false;
    }

    private void strafe(double rotation) {
        RunEncoder(fr,-rotation);
        RunEncoder(fl,rotation);
        RunEncoder(br,rotation);
        RunEncoder(bl,-rotation);
    }

    private void rotate(double rotation) { //Figure out how much rotation rotates bobot
        RunEncoder(fr,-rotation);
        RunEncoder(fl,rotation);
        RunEncoder(br,-rotation);
        RunEncoder(bl,rotation);
    }

    private void driveForward(double rotation) { //Define a good constant for distance a;lksdj;lkjdsaf
        RunEncoder(fr,rotation);
        RunEncoder(fl,rotation);
        RunEncoder(br,rotation);
        RunEncoder(bl,rotation);
        if (fr.getCurrentPosition() == fr.getTargetPosition()){
            done = true;
        }
    }

    private void encoderInit(DcMotor motor) {
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void RunEncoder(DcMotor motor, double rotation) {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        newtarget = tick*rotation;
        motor.setTargetPosition((int)newtarget);
        motor.setPower(0.3);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }









    // APRIL TAG STUFF

    private void autoAim() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                if (detection.id == 20) {
                    if (detection.ftcPose.x < 0) {
                        strafe(0.1);
                    } else {
                        strafe(-0.1);
                    }
                }
                if (detection.id == 24) {
                    if (detection.ftcPose.x < 0) {
                        strafe(0.1);
                    } else {
                        strafe(-0.1);
                    }
                }
            }
        }
    }

    private int identifyObelisk() {
        int OSeed = -1;
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {

            if (detection.metadata != null) {
                if (detection.id == 21) {
                    OSeed = 1;
                }
                if (detection.id == 22) {
                    OSeed = 2;
                }
                if (detection.id == 23) {
                    OSeed = 3;
                }
            }
        }
        return OSeed;
    }

    private void getPos() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();

    }
    private void initAprilTag() {
        // Create the AprilTag processor.
        aprilTag = new AprilTagProcessor.Builder()
                // The following default settings are available to un-comment and edit as needed.
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                //.setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                //.setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
                //.setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)

                // == CAMERA CALIBRATION ==
                // If you do not manually specify calibration parameters, the SDK will attempt
                // to load a predefined calibration for your camera.
                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
                // ... these parameters are fx, fy, cx, cy.

                .build();

            VisionPortal.Builder builder = new VisionPortal.Builder();

        builder.setCamera(hardwareMap.get(WebcamName.class, "camera"));

        builder.setCameraResolution(new Size(640, 480));


        // Set and enable the processor.
        builder.addProcessor(aprilTag);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Disable or re-enable the aprilTag processor at any time.
        //visionPortal.setProcessorEnabled(aprilTag, true);

    }   // end method initAprilTag()


    /**
     * Add telemetry about AprilTag detections.
     */
    private void telemetryAprilTag() {

        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }

        // key
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");
    }
}


