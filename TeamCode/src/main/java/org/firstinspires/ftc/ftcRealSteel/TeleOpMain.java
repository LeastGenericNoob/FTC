package org.firstinspires.ftc.ftcRealSteel;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

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

    double fwpower = -1;

    int counter = 0;
    int heldcounter = 0;
    int prevFwPos = 0;

    double sfix = 1;

    private ElapsedTime runtime = new ElapsedTime();



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

        waitForStart();
        if (isStopRequested()){
            return;
        }



        while (opModeIsActive()){
            double lx = gamepad1.left_stick_x;
            double ly = gamepad1.left_stick_y;
            double rx = gamepad1.right_stick_x;
            if(gamepad2.left_stick_x != 0 || gamepad2.left_stick_y != 0 || gamepad2.right_stick_x != 0){  // Controller 2 override
                lx = -gamepad2.left_stick_x;
                ly = -gamepad2.left_stick_y;
                rx = gamepad2.right_stick_x;
            }




            lx = lx * 1.1;
            // movement stuff
            double limit = Math.max((lx+ly+rx),1);
            double flpower = (ly+lx+rx)/(limit*sfix);
            double frpower = (ly-lx-rx)/(limit*sfix);
            double blpower = (ly-lx+rx)/limit;
            double brpower = (ly+lx-rx)/limit;

            // intake #1
            double intakepower = 0;
            if (gamepad1.right_bumper){
                intakepower = 1;
                intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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



            // flywheel
            boolean yPressed = gamepad1.y || gamepad2.y;
            boolean aPressed = gamepad1.a || gamepad2.a;
            boolean xPressed = gamepad1.x || gamepad2.x;

            boolean anyPressed = yPressed || aPressed || xPressed;
            double flywheelpower = 0;

            if (anyPressed) {
                if (heldcounter == 0) {
                    runtime.reset();
                    heldcounter = 1;
                }

                if (yPressed) {
                    if (runtime.milliseconds() < 1000) {
                        flywheelpower = -1;
                    } else {
                        flywheelpower = -0.8;
                    }
                }
                else if (aPressed) {
                    if (runtime.milliseconds() < 2000) flywheelpower = -1;
                    else flywheelpower = -0.69;
                }
                else if (xPressed) {
                    if (runtime.milliseconds() < 2000) flywheelpower = -1;
                    else flywheelpower = fwpower;
                }

            } else {
                // Only reset when NO flywheel button is pressed
                heldcounter = 0;
                flywheelpower = 0;
            }



            if (gamepad1.dpad_up || gamepad2.dpad_up){
                if (counter <= 0){
                    fwpower -= 0.01;
                    counter = 30;
                }
            }
            if (gamepad1.dpad_down || gamepad2.dpad_down){
                if (counter <= 0){
                    fwpower += 0.01;
                    counter = 30;
                }
            }





            telemetry.addData("Alex's Skill in Clash:", "Error: Variable does not exist.");
            telemetry.addData("Flywheel set powa:", fwpower);
            telemetry.addData("Flywheel actual powa:", flywheelpower);
            telemetry.addData("Flywheel speed:", prevFwPos-flywheel.getCurrentPosition());
            telemetry.addData("runtime:", runtime.milliseconds());
            telemetry.addData("count:", counter);
            telemetry.addData("sfix:", sfix);
            telemetry.update();

            prevFwPos = flywheel.getCurrentPosition();

            fl.setPower(flpower);
            fr.setPower(frpower);
            bl.setPower(blpower);
            br.setPower(brpower);
            intake.setPower(intakepower);
            midintake.setPower(midintakepower);
            flywheel.setPower(flywheelpower);
        }
    }
}
