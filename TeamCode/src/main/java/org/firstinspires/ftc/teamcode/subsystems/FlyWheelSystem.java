package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Configurable
public class FlyWheelSystem {

    public Follower fol;
    public Telemetry telemetry;
    public VoltageSensor battery;

    private DcMotor FL;
    private DcMotor BL;
    private DcMotor FR;
    private DcMotor BR;

    private DcMotor IS;
    private DcMotorEx flywheelLeft;
    private DcMotorEx flywheelRight;

    public Servo flipR, flipL;

    // Gains from the basis codeline (ShootSystem)
    public static double kP = 0.0176, kS = 0.02, kV = 0.00203;


    public String shootMode = "Middle";

    // Manual TPS targets (replacing the 2800 constant calculation)
    public static double middleVelo = 26.7; // mid shoot
    public static double backVelo = 70; //from far away
    public static double topBoxVelo = 45; //big triangle far
    public static double closeBoxVelo = 30; // big triangle close

    public double currentPower = middleVelo;

    public FlyWheelSystem(HardwareMap hardwareMap, Telemetry telemetry){
        this.fol = follower;
        this.telemetry = telemetry;

        // Initialize Voltage Sensor for compensation
        this.battery = hardwareMap.voltageSensor.iterator().next();

        FL = hardwareMap.get(DcMotor.class, "FL");
        BL = hardwareMap.get(DcMotor.class, "BL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BR = hardwareMap.get(DcMotor.class, "BR");


        IS = hardwareMap.get(DcMotor.class, "IS");
        flywheelLeft = hardwareMap.get(DcMotorEx.class, "SL");
        flywheelRight = hardwareMap.get(DcMotorEx.class, "SR");

        flipR = hardwareMap.get(Servo.class, "flipR");
        flipL = hardwareMap.get(Servo.class, "flipL");

        flipL.setDirection(Servo.Direction.REVERSE);
        flywheelRight.setDirection(DcMotorEx.Direction.REVERSE);
        flywheelLeft.setDirection(DcMotorEx.Direction.FORWARD);

        // Reset encoders and run without internal PID to use our custom PIDF
        flywheelLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheelRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheelLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheelRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        IS.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flywheelLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flywheelRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Updated control using Feedforward + Proportional feedback
     * with Voltage Compensation from the ShootSystem basis.
     */
    public void updateFlywheelControl(double targetTPS) {
        double currentTPS = (flywheelLeft.getVelocity() + flywheelRight.getVelocity()) / 2.0;

        // Feedforward: Velocity gain + Static friction offset
        double ff = (kV * targetTPS) + (kS * Math.signum(targetTPS));

        // Feedback: Proportional error
        double error = targetTPS - currentTPS;
        double fb = kP * error;

        // Voltage Compensation: (Output) * (Nominal 12V / Current Battery Voltage)
        // This ensures the motor hits the same RPM regardless of battery level.
        double power = (ff + fb) * (12.0 / battery.getVoltage());

        double finalPower = Math.max(-1, Math.min(1, power));

        flywheelLeft.setPower(finalPower);
        flywheelRight.setPower(finalPower);

        telemetry.addData("Mode", shootMode);
        telemetry.addData("Target TPS", targetTPS);
        telemetry.addData("Current TPS", flywheelLeft.getVelocity());
        telemetry.addData("Voltage", battery.getVoltage());
        telemetry.update();
    }

    public void Shoot() {
        updateFlywheelControl(currentPower);
    }

    public void powerSwap(Gamepad gamepad){
        if (gamepad.left_bumper) {  //mid shoot
            shootMode = "Middle";
            currentPower = middleVelo;
        }
        else if (gamepad.dpad_right) {  //from far awy
            shootMode = "Long";
            currentPower = backVelo;
        }
        else if (gamepad.dpad_down) { // big triangle far
            shootMode = "Far";
            currentPower = topBoxVelo;
        }
        else if (gamepad.dpad_left) { // big triangle close
            shootMode = "Close";
            currentPower = closeBoxVelo;
        }
    }

    public void StopMotors(){
        flywheelLeft.setPower(0);
        flywheelRight.setPower(0);
    }

    public void intake(Gamepad gamepad) {
        if (gamepad.right_trigger > 0) {
            IS.setPower(.9);
        } else if (gamepad.left_trigger > 0) {
            IS.setPower(-.8);
        } else {
            IS.setPower(0);
        }
    }


    public void flips(Gamepad gamepad) {
        if (gamepad.y) {
            flipR.setPosition(0.48);
            flipL.setPosition(0.48);
        }
        if (gamepad.x) {
            flipR.setPosition(0.2);
            flipL.setPosition(0.2);
        }
    }



    public void strafe(Gamepad gamepad) {
        double power = 0;
        double ZoomYes;
        ZoomYes = power;
        power = .6;
        FL.setPower(power * gamepad1.right_stick_x + power * gamepad1.left_stick_x + -power * gamepad1.left_stick_y);
        FR.setPower(power * gamepad1.right_stick_x + power * gamepad1.left_stick_x + power * gamepad1.left_stick_y);
        BL.setPower(power * gamepad1.right_stick_x + -power * gamepad1.left_stick_x + -power * gamepad1.left_stick_y);
        BR.setPower(power * gamepad1.right_stick_x + -power * gamepad1.left_stick_x + power * gamepad1.left_stick_y);
    }



}