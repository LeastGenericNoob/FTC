package org.firstinspires.ftc.ftcRealSteel;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name="TeleOpMain")
public class TeleOpMain extends LinearOpMode {
    //motor names, front left, front right, back left, back right motors
    DcMotor fl;
    DcMotor fr;
    DcMotor bl;
    DcMotor br;
    DcMotor intake;
    DcMotor midintake;
    DcMotor flywheel;

    double fwpower = -0.64;

    int counter = 0;
    int heldcounter = 0;
    int prevFwPos = 0;
    double fwSpeed = 0;

    double fwEsum = 0;
    boolean updateFwSpeed = false;

    boolean yPressed = false;
    boolean aPressed = false;

    int chosenfire = 0;
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime measure = new ElapsedTime();

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() throws InterruptedException{
        fl = hardwareMap.dcMotor.get("frontleft");
        fr = hardwareMap.dcMotor.get("frontright");
        bl = hardwareMap.dcMotor.get("backleft");
        br = hardwareMap.dcMotor.get("backright");
        intake = hardwareMap.dcMotor.get("intake");
        midintake = hardwareMap.dcMotor.get("midintake");
        flywheel = hardwareMap.dcMotor.get("flywheel");

        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);

        initAprilTag();

        waitForStart();
        if (isStopRequested()){
            return;
        }



        while (opModeIsActive()){
            double lx = -gamepad1.left_stick_x;
            double ly = gamepad1.left_stick_y;
            double rx = gamepad1.right_stick_x;
            if(gamepad2.left_stick_x != 0 || gamepad2.left_stick_y != 0 || gamepad2.right_stick_x != 0){  // Controller 2 override
                lx = gamepad2.left_stick_x;
                ly = -gamepad2.left_stick_y;
                rx = gamepad2.right_stick_x;
            }




            lx = lx * 1.1;
            rx = rx * 0.8;

            // movement stuff
            double limit = Math.max((lx+ly+rx),1);
            double flpower = (ly+lx+rx)/(limit);
            double frpower = (ly-lx-rx)/(limit);
            double blpower = (ly-lx+rx)/limit;
            double brpower = (ly+lx-rx)/limit;

            // intake #1
            double intakepower = 0;
            if (gamepad1.right_bumper){
                intakepower = 1;
            }
            if (gamepad1.right_trigger > 0){
                intakepower = -1;
            }
            if (gamepad2.right_bumper){
                intakepower = 1;
            }
            if (gamepad2.right_trigger > 0){
                intakepower = -1;
            }

            // middle intake
            double midintakepower = 0;
            if (gamepad1.left_bumper){
                midintakepower = -0.7;
            }
            if (gamepad1.left_trigger > 0){
                midintakepower = 0.7;
            }
            if (gamepad2.left_bumper){
                midintakepower = -0.7;
            }
            if (gamepad2.left_trigger > 0){
                midintakepower = 0.7;
            }


            counter -= 1;

            if (gamepad1.dpad_up) {
                fwpower += -0.01;
            }
            if (gamepad1.dpad_down) {
                fwpower += 0.01;
            }

            // flywheel
            double flywheelpower = 0;

            if (gamepad1.x || gamepad2.x) {
                chosenfire = 0;
            }
            if (gamepad1.y || gamepad2.y) {
                chosenfire = 1;
                fwEsum = 0;
            }
            if (gamepad1.a || gamepad2.a) {
                chosenfire = 2;
                fwEsum = 0;
            }

            if (chosenfire == 2) {
                flywheelpower = pid(fwSpeed, 1370,-0.0002,-0.65,fwEsum,0);
            }

            if (chosenfire == 1) {
                flywheelpower = pid(fwSpeed, 1700,-0.0002,-0.8,fwEsum,0);
            }

            if (chosenfire == 0) {
                flywheelpower = pid(fwSpeed, 0,-0.0002,0,0,0);
            }

//            boolean anyPressed = yPressed || aPressed || xPressed;
//
//
//            if (anyPressed) {
//                if (heldcounter == 0) {
//                    runtime.reset();
//                    heldcounter = 1;
//                }
//
//                if (yPressed) {
//                    if (runtime.milliseconds() < 1000) {
//                        flywheelpower = -1;
//                    } else {
//                        flywheelpower = -0.85;
//                    }
//                }
//                else if (aPressed) {
//                    if (runtime.milliseconds() < 2000) flywheelpower = -1;
//                    else flywheelpower = -0.69;
//                }
//                else if (xPressed) {
//                    if (runtime.milliseconds() < 2000) flywheelpower = -1;
//                    else flywheelpower = fwpower;
//                }
//
//            } else {
//                // Only reset when NO flywheel button is pressed
//                heldcounter = 0;
//                flywheelpower = 0;
//            }

            

            if (measure.milliseconds() > 1000) {
                fwSpeed = prevFwPos-flywheel.getCurrentPosition();
                prevFwPos = flywheel.getCurrentPosition();
                measure.reset();
            }

            if (gamepad1.b || gamepad2.b){
                autoAim();
            }

            telemetry.addData("Alex's Skill in Clash:", "Error: Variable does not exist.");
            telemetry.addData("Flywheel set powa:", fwpower);
            telemetry.addData("Flywheel actual powa:", flywheelpower);
            telemetry.addData("pos:", flywheel.getCurrentPosition());
            telemetry.addData("Flywheel speed:", fwSpeed);
            telemetry.addData("measure:", measure.milliseconds());
            telemetry.addData("runtime:", runtime.milliseconds());
            telemetry.update();


            fl.setPower(flpower);
            fr.setPower(frpower);
            bl.setPower(blpower);
            br.setPower(brpower);
            intake.setPower(intakepower);
            midintake.setPower(midintakepower);
            flywheel.setPower(flywheelpower);


        }

        if (!opModeIsActive()) {
            fl.setPower(0);
            fr.setPower(0);
            bl.setPower(0);
            br.setPower(0);
            intake.setPower(0);
            midintake.setPower(0);
            flywheel.setPower(0);
        }

    }

    //1370 short
    //1670 long
    private double pid(double speed, double target, double kP, double f, double esum, double kI) {
        double power = 0;
        double error = target - speed;



        power = kP*error+esum*kI+f;

        return power;
    }



    private void rotate(double power) {
        fl.setPower(power);
        fr.setPower(-power);
        bl.setPower(power);
        br.setPower(-power);
    }

    private void autoAim() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                if (detection.id == 20 || detection.id == 24) {
                    if (detection.ftcPose.x < 0) { //power = kP*error+f;
                        rotate(pid(detection.ftcPose.x, 0, -0.03, 0,0,0));
                        telemetry.addData("rotate ahahahha", detection.ftcPose.x);
                    } else {
                        rotate(pid(detection.ftcPose.x, 0, -0.03, 0,0,0));
                        telemetry.addData("gotate mamamamama:", detection.ftcPose.x);
                    }
                }
            }
        }
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

}
