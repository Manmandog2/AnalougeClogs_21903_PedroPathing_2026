package org.firstinspires.ftc.teamcode.teleop;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;

@Configurable
@TeleOp(name = "FlyWheel_Individual_Tuner")
public class FlyWheelShooter extends OpMode {

    // Left Motor Constants
    public static double kP_L = 0.015, kS_L = 0.07, kV_L = 0.0005;
    // Right Motor Constants
    public static double kP_R = 0.01, kS_R = 0.07, kV_R = 0.0004;

    public static double targetTPS = 0.0;

    private DcMotor IS;
    private DcMotorEx flywheelLeft, flywheelRight;
    private VoltageSensor battery;

    private enum TuningMode { KP, KS, KV }
    private TuningMode currentParam = TuningMode.KP;

    private boolean tuningLeft = true; // Toggle between Left and Right motor

    private boolean lastLB = false, lastRB = false;
    private boolean lastUp = false, lastDown = false;
    private boolean lastX = false, lastY = false;

    @Override
    public void init() {
        this.battery = hardwareMap.voltageSensor.iterator().next();
        IS = hardwareMap.get(DcMotor.class, "IS");

        flywheelLeft = hardwareMap.get(DcMotorEx.class, "SL");
        flywheelRight = hardwareMap.get(DcMotorEx.class, "SR");

        flywheelLeft.setDirection(DcMotorEx.Direction.FORWARD);
        flywheelRight.setDirection(DcMotorEx.Direction.REVERSE);

        flywheelLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheelRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        flywheelLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheelRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        flywheelLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheelRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void loop() {
        handleTuningControls();

        double vL = flywheelLeft.getVelocity();
        double vR = flywheelRight.getVelocity();
        double currentVoltage = battery.getVoltage();
        double voltComp = 12.0 / currentVoltage;

        // --- Left Motor PIDF ---
        double ffL = (kV_L * targetTPS) + (kS_L * Math.signum(targetTPS));
        double powerL = (ffL + (kP_L * (targetTPS - vL))) * voltComp;

        // --- Right Motor PIDF ---
        double ffR = (kV_R * targetTPS) + (kS_R * Math.signum(targetTPS));
        double powerR = (ffR + (kP_R * (targetTPS - vR))) * voltComp;

        flywheelLeft.setPower(Math.max(-1, Math.min(1, powerL)));
        flywheelRight.setPower(Math.max(-1, Math.min(1, powerR)));

        if (gamepad1.right_trigger > 0.1) IS.setPower(0.9);
        else if (gamepad1.left_trigger > 0.1) IS.setPower(-0.8);
        else IS.setPower(0);

        // --- UI ---
        telemetry.addLine("=== [ INDIVIDUAL MOTOR TUNER ] ===");
        telemetry.addData("CURRENTLY TUNING", tuningLeft ? "LEFT MOTOR (SL)" : "RIGHT MOTOR (SR)");
        telemetry.addLine("RB: Toggle L/R Motor | LB: Cycle Parameter");
        telemetry.addLine("--------------------------------");
        telemetry.addData("Target TPS (Y/X)", targetTPS);
        telemetry.addData("Left Vel", vL);
        telemetry.addData("Right Vel", vR);
        telemetry.addLine("--- Active Motor Constants ---");
        telemetry.addData("Adjusting", currentParam.name());
        telemetry.addData("kP", tuningLeft ? kP_L : kP_R);
        telemetry.addData("kS", tuningLeft ? kS_L : kS_R);
        telemetry.addData("kV", tuningLeft ? kV_L : kV_R);
        telemetry.update();
    }

    private void handleTuningControls() {
        // Toggle Left/Right Motor selection
        if (gamepad1.right_bumper && !lastRB) tuningLeft = !tuningLeft;
        lastRB = gamepad1.right_bumper;

        // Cycle Parameter (KP -> KS -> KV)
        if (gamepad1.left_bumper && !lastLB) {
            TuningMode[] modes = TuningMode.values();
            currentParam = modes[(currentParam.ordinal() + 1) % modes.length];
        }
        lastLB = gamepad1.left_bumper;

        // Target TPS (Y/X)
        if (gamepad1.y && !lastY) targetTPS += 50.0;
        if (gamepad1.x && !lastX) targetTPS -= 50.0;
        lastY = gamepad1.y; lastX = gamepad1.x;

        // Parameter Adjustment (D-pad)
        if (gamepad1.dpad_up && !lastUp) adjustParam(true);
        if (gamepad1.dpad_down && !lastDown) adjustParam(false);
        lastUp = gamepad1.dpad_up; lastDown = gamepad1.dpad_down;

        if (targetTPS < 0) targetTPS = 0;
    }

    private void adjustParam(boolean increase) {
        double sign = increase ? 1.0 : -1.0;
        switch (currentParam) {
            case KP:
                if (tuningLeft) kP_L += sign * 0.0001; else kP_R += sign * 0.0001;
                break;
            case KS:
                if (tuningLeft) kS_L += sign * 0.005;  else kS_R += sign * 0.005;
                break;
            case KV:
                if (tuningLeft) kV_L += sign * 0.0001; else kV_R += sign * 0.0001;
                break;
        }
    }
}