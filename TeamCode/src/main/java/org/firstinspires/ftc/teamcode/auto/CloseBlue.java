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


@Autonomous(name = "CloseBlue", group = "autonomous")
public class CloseBlue extends OpMode {

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


    private final Pose startPose = new Pose(18, 119, Math.toRadians(144));
    private final Pose preScorePose = new Pose(48, 97, Math.toRadians(135));

    private final Pose row1Line = new Pose(50, 83, Math.toRadians(180));
    private final Pose row1Grab = new Pose(16, 83, Math.toRadians(180));
    private final Pose row1Score = new Pose(48, 97, Math.toRadians(135));

    private final Pose row2Line = new Pose(48, 60, Math.toRadians(180));
    private final Pose row2Grab = new Pose(15, 59, Math.toRadians(184));
    private final Pose row2Score = new Pose(48, 97, Math.toRadians(135));

    private final Pose parkPose = new Pose(62, 70, Math.toRadians(135));


    private DcMotorEx SR;
    private DcMotorEx SL;
    private DcMotor IS;

    private Servo flipL;
    private Servo flipR;



    // SERVO VARS
//    private double upPos = 0.88;
//    private double downPos = 0.59;

    // TIMER VARS
    private ElapsedTime shootTimer;
    private double intakeDur = 600;




    private double botDur = 1650;
    private boolean firing = false;
    private boolean doneShooting;


    private PathChain pathPreScore, pathRow1Line, pathRow1Grab, pathRow1Score, pathRow2Line, pathRow2Grab, pathRow2Score, pathParkPose;


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

        pathRow1Line = fol.pathBuilder()
                .addPath(new BezierCurve(preScorePose, row1Line))
                .setLinearHeadingInterpolation(preScorePose.getHeading(), row1Line.getHeading())
                //.setTangentHeadingInterpolation()
                .build();

        pathRow1Grab = fol.pathBuilder()
                .addPath(new BezierLine(row1Line, row1Grab))
                .setLinearHeadingInterpolation(row1Line.getHeading(), row1Grab.getHeading())
                .build();

        pathRow1Score = fol.pathBuilder()
                .addPath(new BezierCurve(row1Grab, row1Score))
                .setLinearHeadingInterpolation(row1Grab.getHeading(), row1Score.getHeading())
                .build();

        pathRow2Line = fol.pathBuilder()
                .addPath(new BezierLine(row1Score, row2Line))
                .setLinearHeadingInterpolation(row1Score.getHeading(), row2Line.getHeading())
                .build();

        pathRow1Grab = fol.pathBuilder()
                .addPath(new BezierLine(row2Line, row2Grab))
                .setLinearHeadingInterpolation(row2Line.getHeading(), row2Grab.getHeading())
                .build();

        pathRow2Score = fol.pathBuilder()
                .addPath(new BezierLine(row2Grab, row2Score))
                .setLinearHeadingInterpolation(row2Grab.getHeading(), row2Score.getHeading())
                .build();

        pathParkPose = fol.pathBuilder()
                .addPath(new BezierLine(row2Score, parkPose))
                .setLinearHeadingInterpolation(row2Score.getHeading(), parkPose.getHeading())
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
                shootClose(3);
                break;

            case 3:
                if (!fol.isBusy()) {
                    fol.followPath(pathRow1Line);
                    setPathState(4);
                }
                break;

            case 4:
                if (!fol.isBusy()) {
                    runBelt(.8);
                    fol.setMaxPower(.6);
                    fol.followPath(pathRow1Grab);
                    setPathState(7);
                }
                break;

            case 7:
                if (!fol.isBusy()) {
                    fol.setMaxPower(1);
                    fol.followPath(pathRow1Score);
                    setPathState(8);
                }
                break;

            case 8:
                if (!fol.isBusy()) {
                    shootTimer.reset();
                    setPathState(9);
                }
                break;

            case 9:
                shootClose(50);
                break;

            case 50:
                if (!fol.isBusy()) {
                    fol.followPath(pathRow2Line);
                    setPathState(40);
                }
                break;

            case 40:
                if (!fol.isBusy()) {
                    runBelt(.8);
                    fol.setMaxPower(.6);
                    fol.followPath(pathRow2Grab);
                    setPathState(70);
                }
                break;

            case 70:
                if (!fol.isBusy()) {
                    fol.setMaxPower(1);
                    fol.followPath(pathRow2Score);
                    setPathState(80);
                }
                break;

            case 80:
                if (!fol.isBusy()) {
                    shootTimer.reset();
                    setPathState(90);
                }
                break;

            case 90:
                shootClose(5);
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



    private void runBelt(double speed){
        IS.setPower(speed);
    }

    @Override
    public void loop() {

        fol.update();
        autonomousPathUpdate();


    }

    public void shootClose(int nextState){
        shooter.shoot(140);

//        if (shootTimer.milliseconds() > 1200) {
//            shooter.gateOpen();
//        } else {
//            shooter.gateClose();
//        }

//        if (shootTimer.milliseconds() < 500) {
//            shooter.stopBelt();
//        }


        if (Math.abs(shooter.flywheelRight.getVelocity()) >= 1100 || shootTimer.milliseconds() > 1200) {
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