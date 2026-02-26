package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.PathChain;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.FlywheelLogic;
import org.firstinspires.ftc.teamcode.subsystems.ShootSystem;


@Autonomous(name = "FarBlue", group = "autonomous")
public class FarBlue extends OpMode {

    private Follower fol;
    private Timer pathTimer, opmodeTimer; // Game timer

    // flywheel setup

    private ShootSystem shooter;
    private boolean shotstriggered = false;



    private int pathState; // Current path #
    private void setPathState(int num){
        pathState = num;
    }

    private int chainNum;
    private int ballNum = 3;
    private int shootPos = 1;


    private final Pose startPose = new Pose(56, 8, Math.toRadians(90));
    private final Pose preScorePose = new Pose(59, 21, Math.toRadians(112));

    private final Pose row1Line = new Pose(8, 25, Math.toRadians(270));
    private final Pose row1Grab = new Pose(9, 9, Math.toRadians(270));
    private final Pose row1Score = new Pose(59, 21, Math.toRadians(112));

    private final Pose cornerLine = new Pose(11, 24, Math.toRadians(250));
    private final Pose cornerGrab = new Pose(9, 9, Math.toRadians(270));
    private final Pose cornerScore = new Pose(48, 97, Math.toRadians(320));

    private final Pose parkPose = new Pose(59, 21, Math.toRadians(112));


    private DcMotorEx SR;
    private DcMotorEx SL;
    private DcMotor IS;

    private Servo flipL;
    private Servo flipR;



    // SERVO VARS
    private double upPos = 0.88;
    private double downPos = 0.59;

    // TIMER VARS
    private ElapsedTime shootTimer;
    private double intakeDur = 600;




    private double botDur = 1650;
    private boolean firing = false;
    private boolean doneShooting;


    private PathChain pathPreScore, pathRow1Line, pathRow1Grab, pathRow1Score, pathParkPose, pathcornerLine, pathcornerGrab, pathcornerScore;


    @Override
    public void init() {

        fol = Constants.createFollower(hardwareMap);
        fol.setStartingPose(startPose);

        SL = hardwareMap.get(DcMotorEx.class, "SL");
        SR = hardwareMap.get(DcMotorEx.class, "SR");
        IS = hardwareMap.get(DcMotor.class, "IS");

        flipL = hardwareMap.get(Servo.class, "flipL");
        flipR = hardwareMap.get(Servo.class, "flipR");


        MotorConfigurationType configSL = SL.getMotorType().clone();
        configSL.setAchieveableMaxRPMFraction(1.0);
        SL.setMotorType(configSL);
        SL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        SR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        MotorConfigurationType configSR = SR.getMotorType().clone();
        configSR.setAchieveableMaxRPMFraction(1.0);
        SR.setMotorType(configSR);
        SR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        SR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        MotorConfigurationType configIS = IS.getMotorType().clone();
        configIS.setAchieveableMaxRPMFraction(1.0);
        IS.setMotorType(configIS);
        IS.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        IS.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


//        Floor.scaleRange(upPos, downPos);
//        Floor.setPosition(.59);

        shooter = new ShootSystem(hardwareMap, telemetry, fol);
        // TIMER INIT
        shootTimer = new ElapsedTime();

        // PATH INIT
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();


        buildPaths();
        setPathState(1);

    }


    public void buildPaths() {

        pathPreScore = fol.pathBuilder()
                .addPath(new BezierLine(startPose, preScorePose))
                .setLinearHeadingInterpolation(startPose.getHeading(), preScorePose.getHeading())
                .build();

        pathcornerLine = fol.pathBuilder()
                .addPath(new BezierCurve(preScorePose, cornerLine))
                .setLinearHeadingInterpolation(preScorePose.getHeading(), row1Line.getHeading())
                //.setTangentHeadingInterpolation()
                .build();

        pathcornerGrab = fol.pathBuilder()
                .addPath(new BezierLine(cornerLine, cornerGrab))
                .setLinearHeadingInterpolation(row1Line.getHeading(), row1Grab.getHeading())
                .build();


        pathcornerScore = fol.pathBuilder()
                .addPath(new BezierCurve(cornerGrab, cornerScore))
                .setLinearHeadingInterpolation(row1Grab.getHeading(), row1Score.getHeading())
                .build();



        pathParkPose = fol.pathBuilder()
                .addPath(new BezierLine(cornerScore, parkPose))
                .setLinearHeadingInterpolation(row1Grab.getHeading(), parkPose.getHeading())
                .build();

    }


    public void autonomousPathUpdate() {
        switch (pathState) {
            case 1:
                fol.followPath(pathPreScore);
                fol.setMaxPower(1);
                setPathState(100);
                break;

            case 100:
                if (!fol.isBusy()) {
                    shootTimer.reset();
                    setPathState(2);
                }
                break;

            case 2:
                shootFar(3);
                break;


            case 3:
                if (!fol.isBusy()) {
                    fol.followPath(pathcornerLine);
                    setPathState(4);
                }
                break;


            case 4:
                if (!fol.isBusy()) {
                    runBelt(.75);
                    fol.setMaxPower(.5);
                    fol.followPath(pathcornerGrab);
                    setPathState(50);
                }
                break;

            case 50:
                if (!fol.isBusy()) {
                    runBelt(0);
                    fol.setMaxPower(.8);
                    fol.followPath(pathcornerScore);
                    setPathState(60);
                }
                break;

            case 60:
                if (!fol.isBusy()) {
                    shootTimer.reset();
                    setPathState(20);
                }
                break;

            case 20:
                shootFar(5);
                break;

            case 5:
                if (!fol.isBusy()) {
                    fol.setMaxPower(1);
                    runBelt(0);
                    fol.followPath(pathParkPose);
                    setPathState(6);
                }
                break;

        }

    }



    //
    private void runBelt(double speed){
        IS.setPower(speed);
    }

    @Override
    public void loop() {

        fol.update();
        autonomousPathUpdate();


    }

    public void shootFar(int nextState){
        shooter.shoot(500);

//        if (shootTimer.milliseconds() > 1200) {
//            shooter.gateOpen();
//        } else {
//            shooter.gateClose();
//        }

//        if (shootTimer.milliseconds() < 500) {
//            shooter.stopBelt();
//        }


        if (Math.abs(shooter.flywheelRight.getVelocity()) >= 1500 || shootTimer.milliseconds() > 1200) {
            shooter.gateOpen();
            shooter.RunBelt(0.12);
        }

        if (shootTimer.milliseconds() > 4000) {
            shooter.StopMotors();
            shooter.gateClose();
            setPathState(nextState);
        }
    }

//    private void shoot(int nextState) {
//        // updates and sets motors to power
//        shooter.Shoot();
//
//        if (shootTimer.milliseconds() > 900) {
//            shooter.feeder.setPosition(FeedBackShootSystem.closePos);
//        } else {
//            shooter.feeder.setPosition(FeedBackShootSystem.openPos);
//        }
//
//        // lets the flywheel spin up for a bit might need to make bigger
//        if (shootTimer.milliseconds() < 500) {
//            shooter.stopBelt();
//        }
//
//        // after that checks if the flywheel is at the velocity or if we have spun for over 3 seconds
//        else if (Math.abs(shooter.shootVel - shooter.flywheel.getVelocity()) < 50 || shootTimer.milliseconds() > 700) {
//            shooter.RunBelt(0.8);
//
//        }
//
//        // After 4 seconds stop everything and move to the next path state incase sum gets messed up
//        if (shootTimer.milliseconds() > 1900) {
//            shooter.StopMotors();
//            shooter.feeder.setPosition(FeedBackShootSystem.openPos);
//            setPathState(nextState);
//        }
//    }

}