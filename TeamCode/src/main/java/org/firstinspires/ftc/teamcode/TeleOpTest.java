package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="TeleOpTest")
public class TeleOpTest extends LinearOpMode {
    //motor names, front left, front right, back left, back right motors
    DcMotor fl;
    DcMotor fr;
    DcMotor bl;
    DcMotor br;

    @Override
    public void runOpMode() throws InterruptedException{
        fl = hardwareMap.dcMotor.get("insert_name1");
        fr = hardwareMap.dcMotor.get("insert_name2");
        bl = hardwareMap.dcMotor.get("insert_name3");
        br = hardwareMap.dcMotor.get("insert_name4");

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

            double limit = Math.max((lx+ly+rx),1);
            double flpower = (ly+lx+rx)/limit;
            double frpower = (ly-lx-rx)/limit;
            double blpower = (ly-lx+rx)/limit;
            double brpower = (ly+lx-rx)/limit;


            fl.setPower(flpower);
            fr.setPower(frpower);
            bl.setPower(blpower);
            br.setPower(brpower);
        }
    }


}
