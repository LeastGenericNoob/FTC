package org.firstinspires.ftc.ftcRealSteel;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "AutonomousTest")
public class AutoTest extends LinearOpMode {

    DcMotor fl;
    DcMotor fr;
    DcMotor bl;
    DcMotor br;
    ElapsedTime timer = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {

        fl = hardwareMap.dcMotor.get("insert_name1");
        fr = hardwareMap.dcMotor.get("insert_name2");
        bl = hardwareMap.dcMotor.get("insert_name3");
        br = hardwareMap.dcMotor.get("insert_name4");

        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        for (int i = 0; i<360;i+=90){
            move(i,0.2,1);
            wait(200);
        }
        for (int i = 45; i<360;i+=90){
            move(i,0.2,1);
            wait(200);
        }

    }

    public void move(double dir, double time, double power){
        timer.reset();
        while (timer.time() <= time){

            double lx = Math.cos(Math.toRadians(dir));
            double ly = Math.sin(Math.toRadians(dir));

            double limit = Math.max((lx+ly),1);
            double flpower = (ly+lx)/limit;
            double frpower = (ly-lx)/limit;
            double blpower = (ly-lx)/limit;
            double brpower = (ly+lx)/limit;


            fl.setPower(flpower*power);
            fr.setPower(frpower*power);
            bl.setPower(blpower*power);
            br.setPower(brpower*power);
        }

    }

}
