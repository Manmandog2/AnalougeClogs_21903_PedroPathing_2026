package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShootSystem {
    // Hardware
    public DcMotorEx flywheelLeft, flywheelRight;
    private DcMotor intakeSide;
    private Servo flipR, flipL;
    public final Limelight3A cam;
    private final VoltageSensor battery;
    private final Telemetry telemetry;

    // --- Smoothing & Stability Variables ---
    private double smoothedDist = 1.5;
    private final double alpha = 0.08;        // Lower = Smoother (0.05-0.10 is ideal)
    private double lastCalculatedTPS = 0;
    private final double UPDATE_THRESHOLD = 20.0; // Ignore jumps smaller than 20 TPS

    // --- Control Constants (Tuned individually) ---
    public static double kP_L = 0.004, kS_L = 0.02, kV_L = 0.00045;
    public static double kP_R = 0.004, kS_R = 0.02, kV_R = 0.00045;

    // --- Output Variables ---
    public int shootVel = 0; // Final stable integer target
    public double beltSpeed = 1.0;
    public static final double MAX_HEIGHT = 1.4;

    public ShootSystem(HardwareMap hardwareMap, Telemetry telemetry, Follower follower) {
        this.telemetry = telemetry;

        // Motors: SL and SR from your config
        flywheelLeft = hardwareMap.get(DcMotorEx.class, "SR");
        flywheelRight = hardwareMap.get(DcMotorEx.class, "SL");

        flywheelLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheelRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheelLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheelRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Direction logic: ensures they spin correctly to launch
        flywheelLeft.setDirection(DcMotorEx.Direction.REVERSE);
        flywheelRight.setDirection(DcMotorEx.Direction.FORWARD);

        flywheelLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheelRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        intakeSide = hardwareMap.get(DcMotor.class, "IS");
        intakeSide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        flipR = hardwareMap.get(Servo.class, "flipR");
        flipL = hardwareMap.get(Servo.class, "flipL");
        flipL.setDirection(Servo.Direction.REVERSE);

        cam = hardwareMap.get(Limelight3A.class, "cam");
        battery = hardwareMap.voltageSensor.iterator().next();

        cam.pipelineSwitch(0);
        cam.start();
    }

    /**
     * Call this in your TeleOp loop when you want to use the Limelight
     */
    public void AutoShoot() {
        LLResult result = cam.getLatestResult();

        // Only update calculations if we have a valid lock
        if (result != null && result.isValid()) {
            calculateTargetVelocity(result);
        }
        // If target is lost, shootVel remains at the last known valid integer

        updateFlywheelControl(shootVel);
    }

    private void calculateTargetVelocity(LLResult result) {
        for (LLResultTypes.FiducialResult res : result.getFiducialResults()) {
            if (res.getFiducialId() == 20 || res.getFiducialId() == 24) {

                // 1. Get raw distance from camera
                double angle = 25.2 + res.getTargetYDegrees();
                double rawDist = (0.646 / Math.tan(Math.toRadians(angle))) + 0.2;

                // 2. Low-Pass Filter: Blends new data with old data to kill jitter
                smoothedDist = (alpha * rawDist) + ((1.0 - alpha) * smoothedDist);

                // 3. Physics Math
                double effectiveDist = smoothedDist * 0.8;
                double veloMult = 1.6 + (effectiveDist * 0.12); // Scaled down base multiplier
                double targetAngle = Math.toDegrees(Math.atan(54.88 / (9.8 * effectiveDist)));

                double rawVel = Math.sqrt((MAX_HEIGHT * 19.6) /
                        Math.pow(Math.sin(Math.toRadians(targetAngle)), 2)) * veloMult;

                // 4. Convert to double TPS
                double newCalculatedTPS = (rawVel / (9.6 * Math.PI)) * 2800;

                // 5. Threshold & Integer Logic: Stops the "jumping"
                // Only update the motor target if the change is significant
                if (Math.abs(newCalculatedTPS - lastCalculatedTPS) > UPDATE_THRESHOLD) {
                    shootVel = (int) Math.round(newCalculatedTPS);
                    lastCalculatedTPS = newCalculatedTPS;
                }

                // Dynamic Belt Speed
                if (smoothedDist < 2.2) beltSpeed = 0.8;
                else if (smoothedDist < 2.8) beltSpeed = 0.45;
                else beltSpeed = 1.0;
            }
        }
    }

    /**
     * Core PIDF loop for dual motors with individual tuning
     */
    public void updateFlywheelControl(double target) {
        double vL = flywheelLeft.getVelocity();
        double vR = flywheelRight.getVelocity();
        double vComp = 12.0 / battery.getVoltage();

        // Left Motor Logic
        double ffL = (kV_L * target) + (kS_L * Math.signum(target));
        double fbL = kP_L * (target - vL);
        double powL = (ffL + fbL) * vComp;

        // Right Motor Logic
        double ffR = (kV_R * target) + (kS_R * Math.signum(target));
        double fbR = kP_R * (target - vR);
        double powR = (ffR + fbR) * vComp;

        flywheelLeft.setPower(Math.max(-1, Math.min(1, powL)));
        flywheelRight.setPower(Math.max(-1, Math.min(1, powR)));

        telemetry.addData("Target TPS (Int)", shootVel);
        telemetry.addData("Actual L", (int)vL);
        telemetry.addData("Actual R", (int)vR);
    }

    // --- Helper Methods ---
    public void RunBelt(double speed) { intakeSide.setPower(speed); }
    public void StopBelt() { intakeSide.setPower(0); }
    public void StopMotors() {
        flywheelLeft.setPower(0);
        flywheelRight.setPower(0);
        intakeSide.setPower(0);
    }
    public void gateOpen() { flipR.setPosition(0.48); flipL.setPosition(0.48); }
    public void gateClose() { flipR.setPosition(0.2); flipL.setPosition(0.2); }
}