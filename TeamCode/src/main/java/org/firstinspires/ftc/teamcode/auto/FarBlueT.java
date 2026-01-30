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


@Autonomous(name = "FarBlueT", group = "autonomous")
public class FarBlueT extends OpMode {

    private Follower fol;
    private Timer pathTimer, opmodeTimer; // Game timer

    // flywheel setup
    private FlywheelLogic shooter = new FlywheelLogic();

    private boolean shotstriggered = false;



    private int pathState; // Current path #
    private void setPathState(int num) {
        pathState = num;
        pathTimer.resetTimer();

        shotstriggered = false;

    }

    private int chainNum;
    private int ballNum = 3;
    private int shootPos = 1;


    private final Pose startPose = new Pose(56, 8, Math.toRadians(90));
    private final Pose preScorePose = new Pose(59, 21, Math.toRadians(112));
    private final Pose row1Line = new Pose(8, 25, Math.toRadians(270));
    private final Pose row1Grab = new Pose(9, 9, Math.toRadians(270));

    private final Pose row1Score = new Pose(48, 97, Math.toRadians(320));


    private final Pose parkPose = new Pose(62, 37, Math.toRadians(270));


    private DcMotorEx SR;
    private DcMotorEx SL;
    private DcMotor IF;
    private DcMotor IS;

    private Servo flipL;
    private Servo flipR;



    // SERVO VARS
    private double upPos = 0.88;
    private double downPos = 0.59;

    // TIMER VARS
    private ElapsedTime feedTimer;
    private double intakeDur = 600;


    private ElapsedTime botTimer = new ElapsedTime();
    private double botDur = 1650;
    private boolean firing = false;
    private boolean doneShooting;


    private PathChain pathPreScore, pathRow1Line, pathRow1Grab, pathRow1Score, pathParkPose;


    @Override
    public void init() {

        fol = Constants.createFollower(hardwareMap);
        fol.setStartingPose(startPose);

        SL = hardwareMap.get(DcMotorEx.class, "SL");
        SR = hardwareMap.get(DcMotorEx.class, "SR");
        IF = hardwareMap.get(DcMotor.class, "IF");
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

        MotorConfigurationType configIF = IF.getMotorType().clone();
        configIF.setAchieveableMaxRPMFraction(1.0);
        IF.setMotorType(configIF);
        IF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        IF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        MotorConfigurationType configIS = IS.getMotorType().clone();
        configIS.setAchieveableMaxRPMFraction(1.0);
        IS.setMotorType(configIS);
        IS.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        IS.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


//        Floor.scaleRange(upPos, downPos);
//        Floor.setPosition(.59);

        shooter.init(hardwareMap);

        // TIMER INIT
        feedTimer = new ElapsedTime();

        // PATH INIT
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();


        buildPaths(0);
        setPathState(1);

    }




    public void buildPaths(int obNum) {

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



        pathParkPose = fol.pathBuilder()
                .addPath(new BezierLine(row1Grab, parkPose))
                .setLinearHeadingInterpolation(row1Grab.getHeading(), parkPose.getHeading())
                .build();

    }


    public void autonomousPathUpdate() {

        switch (pathState) {

            case 1:
                if (!fol.isBusy()) {
                    fol.followPath(pathPreScore);
                    runBelt(.5);
                    if (!shotstriggered) {
                        shooter.fireShots(3);
                        shotstriggered = true;
                    }
                    else if (shotstriggered && !shooter.isBusy()) {
                        fol.setMaxPower(1);
                        setPathState(2);
                    }
                }
                break;

            case 2:
                if (!fol.isBusy()) {
                    botTimer.reset();
                    runBelt(0);

                            setPathState(3);
                }
                break;


            case 3:
                if (!fol.isBusy() && pathState == 3) {
                    fol.followPath(pathRow1Line);
                    setPathState(4);
                }
                break;


//            case 4:
//                if (!fol.isBusy()) {
//                    runBelt(.5);
//                    fol.setMaxPower(.485);
//                    fol.followPath(pathRow1Grab);
//                    setPathState(5);
//
//
//
//                }
//                break;
//
//
//            case 5:
//                if (!fol.isBusy()) {
//                    fol.setMaxPower(1);
//                    runBelt(0);
//                    fol.followPath(pathParkPose);
//                    setPathState(6);
//                }
//                break;


        }

    }



    //
    private void runBelt(double speed){
        IF.setPower(speed);
        IS.setPower(speed);
    }

    @Override
    public void loop() {

        fol.update();
        shooter.update();
        autonomousPathUpdate();


    }



}
