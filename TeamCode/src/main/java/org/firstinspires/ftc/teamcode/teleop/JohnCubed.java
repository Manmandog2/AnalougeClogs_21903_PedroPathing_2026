package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.FlywheelLogic;

@TeleOp(name = "JohnCubed")
public class JohnCubed extends LinearOpMode {

    private DcMotor FL;
    private DcMotor BL;
    private DcMotor FR;
    private DcMotor BR;
    private DcMotor IS;
    private DcMotorEx SL;
    private DcMotorEx SR;

    FlywheelLogic shooter;



    private Servo flipR;
    private Servo flipL;
    private Servo angle;

    // i like men
    @Override
    public void runOpMode() {




        double my_1;

        FL = hardwareMap.get(DcMotor.class, "FL");
        BL = hardwareMap.get(DcMotor.class, "BL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BR = hardwareMap.get(DcMotor.class, "BR");
        IS = hardwareMap.get(DcMotor.class, "IS");
        SL = hardwareMap.get(DcMotorEx.class, "SL");
        SR = hardwareMap.get(DcMotorEx.class, "SR");

        flipR = hardwareMap.get(Servo.class, "flipR");
        flipL = hardwareMap.get(Servo.class, "flipL");
        angle = hardwareMap.get(Servo.class, "angle");

        flipL.setDirection(Servo.Direction.REVERSE);

        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.FORWARD);
        FR.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.FORWARD);
        SR.setDirection(DcMotorEx.Direction.REVERSE);
        SL.setDirection(DcMotorEx.Direction.FORWARD);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        IS.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SL.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        SR.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        my_1 = 0.6;
        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                // Put loop blocks here.

                shootie();
                intake();
                if (gamepad1.y) {
                    flipR.setPosition(0.48);
                    flipL.setPosition(0.48);
                }
                if (gamepad1.x) {
                    flipR.setPosition(0.2);
                    flipL.setPosition(0.2);
                }
                if (gamepad1.a) {
                    angle.setPosition(0.8);
                }
                if (gamepad1.b) {
                    angle.setPosition(0.2);
                }
                if (gamepad1.dpad_up) {
                    my_1 = 0.9;
                }
                if (gamepad1.dpad_down) {
                    my_1 = 0.65;
                }
            }
        }

//        telemetry.addData("Velocity", SL.getPower());
//        telemetry.update();
    }

    /**
     * Describe this function...
     */
    private void strafe(double power) {
        double ZoomYes;
        ZoomYes = power;
        FL.setPower(power * gamepad1.right_stick_x + power * gamepad1.left_stick_x + -power * gamepad1.left_stick_y);
        FR.setPower(power * gamepad1.right_stick_x + power * gamepad1.left_stick_x + power * gamepad1.left_stick_y);
        BL.setPower(power * gamepad1.right_stick_x + -power * gamepad1.left_stick_x + -power * gamepad1.left_stick_y);
        BR.setPower(power * gamepad1.right_stick_x + -power * gamepad1.left_stick_x + power * gamepad1.left_stick_y);
    }

    /**
     * Describe this function...
     */
    private void shootie() {
        if (gamepad1.right_bumper) {
            SL.setPower(0.5);
            SR.setPower(0.5);
        }
        if (gamepad1.left_bumper) {
            SL.setPower(0);
            SR.setPower(0);
        }
        if (gamepad1.dpad_left) {
            SL.setPower(0.69);
            SR.setPower(0.69);
        }
        if (gamepad1.dpad_right) {
            SL.setPower(0.36);
            SR.setPower(0.36);
        }
    }

    /**
     * Describe this function...
     */
    private void intake() {
        if (0 < gamepad1.right_trigger) {
            IS.setPower(1);
        } else if (0 < gamepad1.left_trigger) {
            IS.setPower(-1);
        } else {
            IS.setPower(0);
        }

    }





}

