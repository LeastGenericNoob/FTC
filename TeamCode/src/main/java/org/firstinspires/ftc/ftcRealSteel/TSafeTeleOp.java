package org.firstinspires.ftc.ftcRealSteel;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="TSafeTeleOp")
public class TSafeTeleOp extends LinearOpMode {
    //motor names, front left, front right, back left, back right motors
    DcMotor fl;
    DcMotor fr;
    DcMotor bl;
    DcMotor br;
    DcMotor intake;
    DcMotor midintake;
    DcMotor flywheel;



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

        boolean override = false;
        double lx=0;
        double ly=0;
        double rx=0;

        while (opModeIsActive()){

            if (gamepad2.x){
                override = !override;
            }

            if (!override) {
                lx = gamepad1.left_stick_x;
                ly = gamepad1.left_stick_y;
                rx = gamepad1.right_stick_x;
            }
            if(gamepad2.left_stick_x != 0 || gamepad2.left_stick_y != 0 || gamepad2.right_stick_x != 0){  // Controller 2 override
                lx = -gamepad2.left_stick_x;
                ly = -gamepad2.left_stick_y;
                rx = -gamepad2.right_stick_x;
            }

            lx = lx * 1.1;
            // movement stuff
            double limit = Math.max((lx+ly+rx),1);
            double flpower = (ly+lx+rx)/limit;
            double frpower = (ly-lx-rx)/limit;
            double blpower = (ly-lx+rx)/limit;
            double brpower = (ly+lx-rx)/limit;

            // intake #1
            double intakepower = 0;
            if (!override) {
                if (gamepad1.right_bumper) {
                    intakepower = 1;
                }
                if (gamepad1.right_trigger > 0) {
                    intakepower = -1;
                }
            }
            if (gamepad2.right_bumper){
                intakepower = 1;
            }
            if (gamepad2.right_trigger > 0){
                intakepower = -1;
            }


            // middle intake
            double midintakepower = 0;
            if (!override) {
                if (gamepad1.left_bumper) {
                    midintakepower = -1;
                }
                if (gamepad1.left_trigger > 0) {
                    midintakepower = 1;
                }
            }
            if (gamepad2.left_bumper){
                midintakepower = -1;
            }
            if (gamepad2.left_trigger > 0){
                midintakepower = 1;
            }

            // flywheel
            double flywheelpower = 0;
            if (gamepad1.y && !override){
                flywheelpower = -1;
            }
            if (gamepad2.y){
                flywheelpower = -1;
            }

            telemetry.addData("Alex's Skill in Clash:", "Error: Variable does not exist.");
            telemetry.addData("Override Status", override);
            telemetry.update();


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