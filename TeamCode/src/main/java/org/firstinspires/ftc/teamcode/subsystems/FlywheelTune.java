package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp

public class FlywheelTune extends OpMode {

    public DcMotorEx flyWheelMotorL;
    public DcMotorEx flyWheelMotorR;


    public double highVelocity =1500;
    public double lowVelocity =900;

    double curTargetVelocity = highVelocity;

    double F =0;
    double P =0;

    double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};

    int stepIndex = 1;


    @Override
    public void init() {

        flyWheelMotorL = hardwareMap.get(DcMotorEx.class, "SL");
        flyWheelMotorR = hardwareMap.get(DcMotorEx.class, "SR");

        flyWheelMotorL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flyWheelMotorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        flyWheelMotorR.setDirection(DcMotorSimple.Direction.REVERSE);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        flyWheelMotorL.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        flyWheelMotorR.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        telemetry.addLine("init complete");
    }


    @Override
    public void loop() {
        //velocity switch
        if (gamepad1.yWasPressed()) {
           if(curTargetVelocity == highVelocity) {
               curTargetVelocity = lowVelocity;
           } else { curTargetVelocity = highVelocity; }
        }
        //index switch
        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }
        //F switch
        if (gamepad1.dpadLeftWasPressed()) {
            F -= stepSizes[stepIndex];
        }
        if (gamepad1.dpadRightWasPressed()) {
            F += stepSizes[stepIndex];
        }
        //p switch
        if (gamepad1.dpadUpWasPressed()) {
            P += stepSizes[stepIndex];
        }
        if (gamepad1.dpadDownWasPressed()) {
            P -= stepSizes[stepIndex];
        }

        //set new PIDF coefficients
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        flyWheelMotorL.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        flyWheelMotorR.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        //set velocity
        flyWheelMotorL.setVelocity(curTargetVelocity);
        flyWheelMotorR.setVelocity(curTargetVelocity);

        double curVelocityR = flyWheelMotorR.getVelocity();
        double curVelocityL = flyWheelMotorL.getVelocity();

        double error = curTargetVelocity - curVelocityR;

        telemetry.addData("target Velocity", curTargetVelocity);
        telemetry.addData("current Velocity", "%.2f", curVelocityR);
        telemetry.addData("error", "%.2f", error);
        telemetry.addLine("--------------------------");
        telemetry.addData("Tuning P", "%.4f (D-pad U/D", P);
        telemetry.addData("Tuning F", "%.4f (D-pad L/R", F);
        telemetry.addData("Step Sizes", "%.4f (B-Button", stepSizes[stepIndex]);



    }


}
