package org.firstinspires.ftc.teamcode.teleop;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.FlyWheelSystem;
import org.firstinspires.ftc.teamcode.subsystems.FlywheelLogic;

@TeleOp(name = "Dihvid")
public class Dihvid extends OpMode {



    FlyWheelSystem shooter;

    private Follower fol;
    private DcMotor FL;
    private DcMotor BL;
    private DcMotor FR;
    private DcMotor BR;



    public void init(){
        shooter = new FlyWheelSystem(hardwareMap, telemetry);

        FL = hardwareMap.get(DcMotor.class, "FL");
        BL = hardwareMap.get(DcMotor.class, "BL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BR = hardwareMap.get(DcMotor.class, "BR");

        fol = Constants.createFollower(hardwareMap);
//        fol.startTeleOpDrive();



    }

    public void loop(){

        double my_1;
        my_1 = 0.6;

        fol.update();

//        fol.setTeleOpDrive(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x);

        shooter.powerSwap(gamepad1);

        if(gamepad1.a){
            shooter.Shoot();
        }

        shooter.intake(gamepad1);
//        shooter.intake(gamepad2);

        shooter.flips(gamepad1);

        if(gamepad1.left_bumper)
            shooter.StopMotors();

        shooter.strafe(gamepad1);

    }





}

