package org.firstinspires.ftc.ftcRealSteel;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class RunMotorTest extends LinearOpMode {
    DcMotor testmotor;
    @Override
    public void runOpMode() throws InterruptedException{
        testmotor = hardwareMap.dcMotor.get("motor");

        waitForStart();

        if (isStopRequested()){
            return;
        }

        while (opModeIsActive()){
            testmotor.setPower(0.5);
            }

        }
    }

