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
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

@Autonomous(name = "AutoClose")
public class AutoClose extends LinearOpMode {

    int team = 0; // 0 for blue 1 for red

    DcMotor fl;
    DcMotor fr;
    DcMotor bl;
    DcMotor br;
    DcMotor intake;
    DcMotor midintake;
    DcMotor flywheel;

    int prevFwPos = 0;
    double fwSpeed = 0;
    double tick = 537.7;
    double newtarget;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    int step = 0;
    boolean done = false;
    int seed = -1;

    double shotpower = -0.80;

    private ElapsedTime runtime = new ElapsedTime();

    private ElapsedTime measure = new ElapsedTime();

    double fwpower = 0;
    double intakepower = 0;
    double midintakepower = 0;

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

            if (gamepad1.dpad_up) {
                shotpower += -0.01;
                sleep(100);
            }
            if (gamepad1.dpad_down) {
                shotpower += 0.01;
                sleep(100);
            }

            telemetry.addData("power:", shotpower);
            telemetry.addData("team:", team);
            telemetry.update();

        }

        runtime.reset();
        measure.reset();

        boolean once = false;
        while (opModeIsActive()) {

            seed = identifyObelisk();

            // -----------------COMMANDS YOU GOTTA KNOW ------------------
            // driveForward(Value)    when value = 2.06 it drives a full mat forward
            // WaitUntilDone()  not a very good function, only detects if the frontleft motor has finished its job.
            // proceeds to the next step. if used when not running drive train (e.g intake/outake commands) will just move to next step
            // Use runtime.milliseconds to check how long its been since the last waitUntilDone, use it for intake/outake time control
            // rotate(Value)  when value = 1.6 roughly 90 degrees clockwise
            // strafe(Value)  dont use, too inconsistent

            fwpower = 0;
            intakepower = 0;
            midintakepower = 0;

            if (step == 0) {
                if (!once) {
                    driveForward(-4.3, 0.8);
                    once = true;
                }
                if (runtime.milliseconds() > 2000){
                    step +=1;
                    once = false;
                    runtime.reset();
                }
            }


            if (step == 1) {
                fwpower = pid(fwSpeed, 1300,-0.0002,-0.65);
                for (int i = 0; i < 5; i++) {
                    if (runtime.milliseconds() > 3000 + 1200 * i && runtime.milliseconds() < 3500 + 1200 * i) {
                        intakepower = 1;
                        midintakepower = -1;
                    }
                    if (runtime.milliseconds() > 3500 + 1200 * i && runtime.milliseconds() < 4700+1200*i) {
                        intakepower = 0.1;
                        midintakepower = 0;
                    }
                }

                if (runtime.milliseconds() > 10000){
                    step +=1;
                    runtime.reset();
                }
            }

            if (step == 2) {
                intakepower = -1;
                midintakepower = -1;
                if (!once) {
                    once = true;
                    if (team == 0) {
                        rotate(-2.8);
                    } else {
                        rotate(2.8);
                    }
                }
                if (runtime.milliseconds() > 3000){
                    step +=1;
                    once = false;
                    runtime.reset();
                }
            }

            if (step == 3) {
                intakepower = 1;
                if (!once) {
                    once = true;
                    driveForward(-3.5,0.5);
                }
                if (runtime.milliseconds() > 3000){
                    step +=1;
                    once = false;
                    runtime.reset();
                }
            }

            if (step == 4) {
                if (runtime.milliseconds() > 100) {
                    intakepower = 1;
                }
                if (!once) {
                    once = true;
                    driveForward(3.5, 0.8);
                }
                if (runtime.milliseconds() > 2000){
                    step +=1;
                    once = false;
                    runtime.reset();
                }
            }

            if (step == 5) {
                if (!once) {
                    once = true;
                    if (team == 0) {
                        rotate(2.9);
                    } else {
                        rotate(-2.9);
                    }
                }
                if (runtime.milliseconds() > 1500){
                    step +=1;
                    once = false;
                    runtime.reset();
                }
            }



            if (step == 6) {
                fwpower = pid(fwSpeed, 1300,-0.0002,-0.65);
                for (int i = 0; i < 5; i++) {
                    if (runtime.milliseconds() > 3000 + 1200 * i && runtime.milliseconds() < 3500 + 1200 * i) {
                        intakepower = 1;
                        midintakepower = -1;
                    }
                    if (runtime.milliseconds() > 3500 + 1200 * i && runtime.milliseconds() < 4700+1200*i) {
                        intakepower = 0.1;
                        midintakepower = 0;
                    }
                }

                if (runtime.milliseconds() > 10000){
                    step +=1;
                    runtime.reset();
                }
            }

            if (step == 12) {
                if (!once) {
                    once = true;
                }
                telemetry.addData("end it", "now");
            }

            flywheel.setPower(fwpower);
            intake.setPower(intakepower);
            midintake.setPower(midintakepower);

            //telemetryAprilTag();
            // Push telemetry to the Driver Station.

            if (measure.milliseconds() > 1000) {
                fwSpeed = prevFwPos-flywheel.getCurrentPosition();
                prevFwPos = flywheel.getCurrentPosition();
                measure.reset();
            }

            telemetry.addData("Step Number:", step);
            telemetry.addData("Seed:", seed);
            telemetry.addData("team", team);
            telemetry.addData("fwpower", fwpower);
            telemetry.update();

        }
        visionPortal.close();
    }



    private void waitUntilDone() {
        while (!(fr.getCurrentPosition() == fr.getTargetPosition())){
            sleep(10);
            telemetry.addData("waiting", "kalsdf");
        }
        runtime.reset();
        step+=1;
        done=false;
    }

    private void strafe(double rotation) {
        RunEncoder(fr,-rotation,0.3);
        RunEncoder(fl,rotation,0.3);
        RunEncoder(br,rotation,0.3);
        RunEncoder(bl,-rotation,0.3);
    }

    private void rotate(double rotation) { //Figure out how much rotation rotates bobot
        RunEncoder(fr,-rotation,0.5);
        RunEncoder(fl,rotation,0.5);
        RunEncoder(br,-rotation,0.5);
        RunEncoder(bl,rotation,0.5);
    }

    private void driveForward(double rotation, double power) { //Define a good constant for distance a;lksdj;lkjdsaf
        RunEncoder(fr,rotation,power);
        RunEncoder(fl,rotation,power);
        RunEncoder(br,rotation,power);
        RunEncoder(bl,rotation,power);
        if (fr.getCurrentPosition() == fr.getTargetPosition()){
            done = true;
        }
    }

    private void encoderInit(DcMotor motor) {
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void RunEncoder(DcMotor motor, double rotation, double power) {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        newtarget = tick*rotation;
        motor.setTargetPosition((int)newtarget);
        motor.setPower(power);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }




    private double pid(double speed, double target, double kP, double f) {
        double power = 0;
        double error = target - speed;

        power = kP*error+f;

        return power;
    }




    // APRIL TAG STUFF

    private void autoAim() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                if (detection.id == 20) {
                    if (detection.ftcPose.x < 0) {
                        rotate(0.02);
                    } else {
                        rotate(-0.02);
                    }
                }
                if (detection.id == 24) {
                    if (detection.ftcPose.x < 0) {
                        rotate(0.1);
                    } else {
                        rotate(-0.1);
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

