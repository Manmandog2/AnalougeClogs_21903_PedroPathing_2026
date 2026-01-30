package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class FlywheelLogic {
    private Servo flipL;
    private Servo flipR;

    private DcMotorEx SL;
    private DcMotorEx SR;

    private ElapsedTime stateTimer = new ElapsedTime();

    private enum FLywheelState {
        IDLE,
        SPIN_UP,
        LAUNCH,
        RESET_GATE
    }

    private FLywheelState flywheelState;

    // gate constnats
    private double GATE_CLOSE_ANGLE = 0;
    private double GATE_OPEN_ANGLE = 90;
    private double GATE_OPEN_TIME = 0.5;
    private double GATE_CLOSE_TIME = 0.5;

    //flywheel constants
    private int shotsRemaning = 0;
    private double flywheelVelocity = 0;
    private double MIN_FLRYWHEEL_RPM = 800;
    private double TARGET_FLYWHEEL_RPM = 1300;
    private double FLYWHEEL_MAX_SPINUP_TIME = 3;


    public void init(HardwareMap hwMap) {

        flipL = hwMap.get(Servo.class, "flipL");
        flipR = hwMap.get(Servo.class, "flipR");

        SL = hwMap.get(DcMotorEx.class, "SL");
        SR = hwMap.get(DcMotorEx.class, "SR");
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(6.4, 0, 0, 13);
        SL.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        SR.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        SL.setDirection(DcMotorSimple.Direction.FORWARD);
        SR.setDirection(DcMotorSimple.Direction.REVERSE);


        flywheelState = FLywheelState.IDLE;

        SL.setVelocity(flywheelVelocity);
        SR.setVelocity(flywheelVelocity);
        flipL.setPosition(GATE_CLOSE_ANGLE);
        flipR.setPosition(GATE_OPEN_ANGLE);

    }

    public void update() {

        switch (flywheelState) {
            case IDLE:
                if (shotsRemaning > 0) {
                    flipL.setPosition(GATE_CLOSE_ANGLE);
                    flipR.setPosition(GATE_OPEN_ANGLE);

                    SL.setVelocity(flywheelVelocity);
                    SR.setVelocity(flywheelVelocity);
                    SL.setPower(TARGET_FLYWHEEL_RPM);
                    SR.setPower(TARGET_FLYWHEEL_RPM);

                    stateTimer.reset();
                    flywheelState = FLywheelState.SPIN_UP;
                }
                break;
            case SPIN_UP:
                //velocity posible
                if (flywheelVelocity > MIN_FLRYWHEEL_RPM || stateTimer.seconds() > FLYWHEEL_MAX_SPINUP_TIME) {
                    flipR.setPosition(GATE_CLOSE_ANGLE);
                    flipL.setPosition(GATE_OPEN_ANGLE);
                    stateTimer.reset();

                    flywheelState = FLywheelState.LAUNCH;
                }
                break;
            case LAUNCH:
                if (stateTimer.seconds() > GATE_OPEN_TIME) {
                    shotsRemaning --;

                    //flipL.setPosition(GATE_CLOSE_ANGLE);
                    //flipR.setPosition(GATE_CLOSE_ANGLE);
                    stateTimer.reset();

                    flywheelState = FLywheelState.RESET_GATE;
                }
            case RESET_GATE:
                if (stateTimer.seconds() > GATE_CLOSE_TIME) {
                    if (shotsRemaning > 0) {
                        stateTimer.reset();
                        flywheelState = FLywheelState.SPIN_UP;
                    }
                    else {
                        SL.setPower(0);
                        SR.setPower(0);

                        flywheelState = FLywheelState.IDLE;
                    }
                    break;
                }
        }


    }

    public void fireShots (int numberOfShots) {
        if (flywheelState == FLywheelState.IDLE) {
            shotsRemaning = numberOfShots;
        }
    }

    public boolean isBusy() {
        return flywheelState != FLywheelState.IDLE;
    }

}
