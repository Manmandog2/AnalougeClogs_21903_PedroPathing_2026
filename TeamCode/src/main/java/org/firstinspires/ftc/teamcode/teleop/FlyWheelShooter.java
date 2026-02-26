package org.firstinspires.ftc.teamcode.teleop;


import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

@Configurable
@TeleOp(name = "FlyWheelShooter")
public class FlyWheelShooter extends OpMode {


    public static double kP = 0.000;
    public static double kS = 0.00;
    public static double kV = 0.0000; // was 0.00035
    public static double targetTPS = 300;


    private DcMotorEx cannon, belt;

    private DcMotor IS;
    private DcMotorEx flywheelLeft;
    private DcMotorEx flywheelRight;

    public Servo flipR, flipL;
    private VoltageSensor battery;

    @Override
    public void init() {

        this.battery = hardwareMap.voltageSensor.iterator().next();


        IS = hardwareMap.get(DcMotor.class, "IS");
        flywheelLeft = hardwareMap.get(DcMotorEx.class, "SL");
        flywheelRight = hardwareMap.get(DcMotorEx.class, "SR");

        flipR = hardwareMap.get(Servo.class, "flipR");
        flipL = hardwareMap.get(Servo.class, "flipL");

        flipL.setDirection(Servo.Direction.REVERSE);
        flywheelRight.setDirection(DcMotorEx.Direction.REVERSE);
        flywheelLeft.setDirection(DcMotorEx.Direction.FORWARD);

        flywheelLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheelRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheelLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheelRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        IS.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flywheelLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flywheelRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {

        double currentTPS = flywheelLeft.getVelocity();
        double currentVoltage = battery.getVoltage();

        double ff = (kV * targetTPS) + (kS * Math.signum(targetTPS));

        double error = targetTPS - currentTPS;
        double fb = kP * error;


        double power = (ff + fb) * (12.0 / currentVoltage);

        flywheelLeft.setPower(power);
        flywheelRight.setPower(power);



        if (gamepad1.right_trigger > 0) {
            IS.setPower(.9);
        } else if (gamepad1.left_trigger > 0) {
            IS.setPower(-.8);
        } else {
            IS.setPower(0);
        }


        telemetry.addData("Target TPS", targetTPS);
        telemetry.addData("Actual TPS", currentTPS);
        telemetry.addData("Error", error);
        telemetry.addData("Voltage", currentVoltage);
        telemetry.addData("kP", kP);
        telemetry.update();
    }
}