package org.firstinspires.ftc.teamcode.teleop;



import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.ShootSystem;

@TeleOp(name = "FlyWheel Tuner")
public class FlyWheelTuner extends OpMode{


    // This is the thing you will be calling throughout your code whenever you want to use a function from that class
    private ShootSystem shooter;

    // Heres a timer for shooting
    private ElapsedTime shootStateTimer = new ElapsedTime();

    // Here is your pedro components youll need cause I am NOT tryna write teleop drive code rn
    private Follower fol;
    private final Pose startingPose = new Pose(72, 72, Math.toRadians(0));

    // vars for lat
    private double targetTPS = 0;




    @Override
    public void init(){
        shooter = new ShootSystem(hardwareMap, telemetry);

        // Teleop drive stuff
        fol = Constants.createFollower(hardwareMap);
        fol.setStartingPose(startingPose);
        fol.update();
        fol.startTeleOpDrive();
    }


    @Override
    public void loop(){

        if(gamepad1.b)
            shooter.shoot(targetTPS);
        else
            shooter.StopMotors();

        if (gamepad1.x)
            shooter.RunBelt(.7);
        else if(gamepad1.y)
            shooter.RunBelt(-.7);
        else
            shooter.stopBelt();

        // Make this your servo case system thinge
        if(gamepad1.y)
            shooter.gateOpen();
        else
            shooter.gateClose();


        if (gamepad1.right_bumper)
            targetTPS += 50;
        else if (gamepad1.left_bumper)
            targetTPS -= 50;


        telemetry.addData("TARGET TPS", targetTPS);
        telemetry.addData("ACTUAL TPS", shooter.flywheelLeft.getVelocity());
        telemetry.addData("VOLTAGE", hardwareMap.voltageSensor.iterator().next().getVoltage());
        telemetry.update();
    }

}
