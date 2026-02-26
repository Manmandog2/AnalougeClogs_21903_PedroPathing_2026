package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.ShootSystem.kP;
import static org.firstinspires.ftc.teamcode.subsystems.ShootSystem.kV;

import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class FlywheelLogic {


    private double targetVelocity = 0;
    public double kS = 0.02, kV = 0.00045, kP = 0.007;

    private DcMotorEx SL;
    private DcMotorEx SR;

    private double launchPower;


    public String shootMode = "Middle";
    public double launchPow;




    public double currentPower = middleVelo;



    public static double middleVelo = 0;
    public static double backVelo = 0;
    public static double topBoxVelo = 0;
    public static double closeBoxVelo = 0;





    public FlywheelLogic(HardwareMap hwMap, Telemetry telemetry) {

        SL = hwMap.get(DcMotorEx.class, "SL");
        SR = hwMap.get(DcMotorEx.class, "SR");
//        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(6.4, 0, 0, 13);
//        SL.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
//        SR.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        SL.setDirection(DcMotorSimple.Direction.FORWARD);
        SR.setDirection(DcMotorSimple.Direction.REVERSE);

    }


    public void updateFeedforward () {
        SL.setPower((kV * targetVelocity) + (kP * (targetVelocity - SL.getVelocity())) + kS);
        SR.setPower((kV * targetVelocity) + (kP * (targetVelocity - SR.getVelocity())) + kS);
    }

    public void setVelocity ( double target){
        targetVelocity = target;
    }

//    public void updateTelemetry (TelemetryManager telemetryManager) {
//        telemetryManager.addData("Velocity", SL.getVelocity());
//    }



    public void updateLaunchers() {
        SL.setPower(-launchPower);
        SR.setPower(launchPower);
    }


}
