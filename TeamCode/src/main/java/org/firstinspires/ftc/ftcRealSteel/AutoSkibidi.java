package org.firstinspires.ftc.ftcRealSteel;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
@Autonomous(name="AutoSkibidi")
public class AutoSkibidi extends LinearOpMode {
    //motor names, front left, front right, back left, back right motors
    DcMotor fl;
    DcMotor fr;
    DcMotor bl;
    DcMotor br;
    DcMotor intake;
    //DcMotor midintake;
    //DcMotor flywheel;

    double tick = 537.7;
    double newtarget;

    @Override
    public void runOpMode() throws InterruptedException{
        fl = hardwareMap.dcMotor.get("frontleft");
        fr = hardwareMap.dcMotor.get("frontright");
        bl = hardwareMap.dcMotor.get("backleft");
        br = hardwareMap.dcMotor.get("backright");
        intake = hardwareMap.dcMotor.get("intake");
        //midintake = hardwareMap.dcMotor.get("midintake");
        //flywheel = hardwareMap.dcMotor.get("flywheel");

        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);

        fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);



        waitForStart();
        if (isStopRequested()){
            return;
        }

        double flpower = 0;
        double frpower = 0;
        double blpower = 0;
        double brpower = 0;

        double intakepower = 0;
        double midintakepower = 0;
        double flywheelpower = 0;
        telemetry.addData("Alex's Skill in Clash Royale:", "Error: Variable does not exist.");
        telemetry.update();

        encoder(1);

        sleep(200);

        encoder(0.5);


    }
    public void encoder(double rotation){
        newtarget = tick*rotation;
        intake.setTargetPosition((int)newtarget);
        intake.setPower(0.3);
        intake.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    public void MoveForward(double rotation){
        newtarget = tick*rotation; // total rotation = 295 mm or 29.5 cm, field tile is 609.5 mm, 2.06 rot per tile
        fl.setTargetPosition((int)newtarget);
        fr.setTargetPosition((int)newtarget);
        bl.setTargetPosition((int)newtarget);
        br.setTargetPosition((int)newtarget);
        fl.setPower(0.3);
        fr.setPower(0.3);
        bl.setPower(0.3);
        br.setPower(0.3);
        fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        br.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

}