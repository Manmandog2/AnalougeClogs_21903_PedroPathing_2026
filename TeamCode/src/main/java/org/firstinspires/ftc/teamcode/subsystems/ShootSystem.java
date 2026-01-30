package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShootSystem {
    public DcMotorEx flywheelLeft;
    public DcMotorEx flywheelRight;

    private DcMotor intakeFront;
    private DcMotor intakeSide;
    private DcMotor leftServo;
    private DcMotor rightServo;

    private Servo flipR;
    private Servo flipL;

    private final Telemetry telemetry;
    public static double kP = 0.004; // This should be fine
    public static double kS = 0.02; // This should be fine
    public static double kV = 0.00045; // Tune this so targetTPS almost reaches speed without kP
    private final VoltageSensor battery;

    public ShootSystem(HardwareMap hardwareMap, Telemetry telemetry){
        this.telemetry = telemetry;

        flywheelLeft = hardwareMap.get(DcMotorEx.class, "SR");
        flywheelRight = hardwareMap.get(DcMotorEx.class, "SL");
        intakeFront = hardwareMap.get(DcMotor.class, "IF");
        intakeSide = hardwareMap.get(DcMotor.class, "IS");

        flipR = hardwareMap.get(Servo.class, "flipR");
        flipL = hardwareMap.get(Servo.class, "flipL");

        flywheelLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        flywheelRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        flywheelLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheelRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeSide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        flywheelLeft.setDirection(DcMotorEx.Direction.REVERSE);
        flywheelRight.setDirection(DcMotorEx.Direction.FORWARD);


        battery = hardwareMap.voltageSensor.iterator().next();

    }

    public void updateFlywheelControl(double targetTPS) {
        // Average the velocity of both encoders for more stability
        double currentTPS = (flywheelLeft.getVelocity() + flywheelRight.getVelocity()) / 2.0;
        double currentVoltage = battery.getVoltage();

        // Power = (kV * velocity) + kS
        double ff = (kV * targetTPS) + (kS * Math.signum(targetTPS));

        // Feedback: Corrects for real-world resistance
        double error = targetTPS - currentTPS;
        double fb = kP * error;


        double power = (ff + fb) * (12.0 / currentVoltage);

        double finalPower = Math.max(-1, Math.min(1, power));


        flywheelLeft.setPower(finalPower);
        flywheelRight.setPower(finalPower);

        telemetry.addData("Target TPS", targetTPS);
        telemetry.addData("Current TPS", currentTPS);
        telemetry.addData("Current Error", error);
        telemetry.update();
    }



        public double VELO_TOLERANCE = 60;


        public void shoot(double targetTPS) {

            // Run the feedback/feedforward control loop
            updateFlywheelControl(targetTPS);

            // Check if we are at the correct speed
            double currentTPS = (flywheelLeft.getVelocity() + flywheelRight.getVelocity()) / 2.0;


            if (Math.abs(targetTPS - currentTPS) < 60) {
                RunBelt(.6);
            } else {

                stopBelt();
            }
        }


        public void spinUp(double targetTPS) {
            updateFlywheelControl(targetTPS);
        }



    public void RunBelt(double speed) {
        intakeSide.setPower(speed);
        intakeFront.setPower(speed);
    }

    public void stopBelt(){
        intakeFront.setPower(0);
        intakeSide.setPower(0);
    }

    public void StopMotors(){
        flywheelLeft.setPower(0);
        flywheelRight.setPower(0);
        stopBelt();
        //angleAdjuster.setPosition(0.15);
    }

    public void gateOpen(){
        flipR.setPosition(0.48);
        flipL.setPosition(0.56);
    }

    public void gateClose(){
        flipR.setPosition(0.2);
        flipL.setPosition(0.2);
    }



}
