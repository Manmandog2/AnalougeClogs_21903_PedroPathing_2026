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


@Autonomous(name = "CloseBlue", group = "autonomous")
public class CloseBlue extends OpMode {

    private Follower fol;
    private int pathState; // Current path #

    private void setPathState(int num) {
        pathState = num;

    }

    private Timer pathTimer, opmodeTimer; // Game timer
    private int chainNum;
    private int ballNum = 3;
    private int shootPos = 1;


    private final Pose startPose = new Pose(18, 119, Math.toRadians(325));
    private final Pose preScorePose = new Pose(48, 97, Math.toRadians(320));
    private final Pose row1Line = new Pose(48, 80, Math.toRadians(180));
    private final Pose row1Grab = new Pose(12, 80, Math.toRadians(180));
    private final Pose row1Score = new Pose(48, 97, Math.toRadians(320));
    private final Pose row2Line = new Pose(48, 60, Math.toRadians(180));
    private final Pose row2Grab = new Pose(15, 59, Math.toRadians(180));
    private final Pose row2Score = new Pose(48, 97, Math.toRadians(320));

    private final Pose parkPose = new Pose(48, 60, Math.toRadians(315));


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


    private PathChain pathPreScore, pathRow1Line, pathRow1Grab, pathRow1Score, pathRow2Line, pathRow2Grab, pathRow2Score, pathParkPose;


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


        // TIMER INIT
        feedTimer = new ElapsedTime();

        // PATH INIT
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();


        buildPaths(0);
        setPathState(1);

    }


    @Override
    public void loop() {


        fol.update();
        autonomousPathUpdate();

        Pose finalPose = fol.getPose();


    }


    public void buildPaths(int obNum) {

        pathPreScore = fol.pathBuilder()
                .addPath(new BezierLine(startPose, preScorePose))
                .setLinearHeadingInterpolation(startPose.getHeading(), preScorePose.getHeading())
                .build();

        pathRow1Line = fol.pathBuilder()
                .addPath(new BezierLine(preScorePose, row1Line))
                .setLinearHeadingInterpolation(preScorePose.getHeading(), row1Line.getHeading())
                //.setTangentHeadingInterpolation()
                .build();

        pathRow1Grab = fol.pathBuilder()
                .addPath(new BezierLine(row1Line, row1Grab))
                .setLinearHeadingInterpolation(row1Line.getHeading(), row1Grab.getHeading())
                .build();

        pathRow1Score = fol.pathBuilder()
                .addPath(new BezierLine(row1Grab, row1Score))
                .setLinearHeadingInterpolation(row1Grab.getHeading(), row1Score.getHeading())
                .build();

        pathRow2Line = fol.pathBuilder()
                .addPath(new BezierLine(row1Score, row2Line))
                .setLinearHeadingInterpolation(row1Score.getHeading(), row2Line.getHeading())
                .build();

        pathRow2Grab = fol.pathBuilder()
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
                if (!fol.isBusy()) {
                    fol.followPath(pathPreScore);
                    shootBall(.37);
                    fol.setMaxPower(1);
                    setPathState(2);
                }
                break;

            case 2:
                if (!fol.isBusy()) {
                    runBelt(.5);
                    botTimer.reset();
                    ballSequence(3);

                    if (doneShooting) {
                        shootBall(0);
                        runBelt(0);
                        if (!fol.isBusy() && !SL.isBusy())
                            setPathState(3);
                    }
                }
                break;


            case 3:
                if (!fol.isBusy() && pathState == 3) {
                    fol.followPath(pathRow1Line);
                    setPathState(4);
                }
                break;


            case 4:
                if (!fol.isBusy()) {
                    runBelt(.42);
                    fol.setMaxPower(.485);
                    fol.followPath(pathRow1Grab);
                    setPathState(5);
                    shootBall(.37);


                }
                break;


            case 5:
                if (!fol.isBusy()) {
                    fol.setMaxPower(1);
                    runBelt(0);
                    fol.followPath(pathRow1Score);
                    setPathState(6);
                }
                break;


            case 6:
                if (!fol.isBusy()) {
                    runBelt(.7);
                    botTimer.reset();
                    ballSequence(3);

                    if (doneShooting) {
                        setPathState(7);
                        shootBall(0);
                        runBelt(0);
                        if (!fol.isBusy() && !SL.isBusy()) ;

                    }
                }
                break;


            case 7:
                if (!fol.isBusy() && pathState == 7) {
                    fol.followPath(pathParkPose);
                    setPathState(8);
                }
                break;



//            case 6:
//                if (!fol.isBusy()) {
//                    fol.setMaxPower(.25);
//                    fol.followPath(pathRow2Grab);
////                    runBelt(beltSpeed);
//                    //ballNum = 3;
//                    setPathState(7);
//                }
//                break;
//
//            case 7:
//                if (!fol.isBusy()){
//                    fol.setMaxPower(1);
//                    fol.followPath(pathRow2Score);
////                    setShootPos(row2Score.getX(), row2Score.getY(), fx, fy);
////                    runBelt(0);
//                    setPathState(10);
//                }
//                break;

//            case 8:
//                if (!fol.isBusy()){
//                    setPathState(10);
//                }
//                break;

//            case 9:
//                if (shootTimerCount != 2)
//                    shoot();
//                else {
//                    shootTimerCount = -1;
//                    setPathState(10);
//                }
//                break;

//            case 10:
//                if (!fol.isBusy() && pathState == 10){
//                    fol.followPath(pathParkPose);
//                    setPathState(11);
//                }
//                break;
//
//            case 11:
//                break;

        }

    }




    private void ballSequence(int count){
        botTimer.reset();
        while (botTimer.milliseconds() < botDur){}
        for (int i = 0; i < count; i++) {
            if (!firing) fireBall();
        }
        doneShooting = true;
    }

    private void fireBall(){
        firing = true;
        botTimer.reset();
        while (botTimer.milliseconds() < botDur){
            floorUp();
        }
        botTimer.reset();
        while (botTimer.milliseconds() < botDur){
            floorDown();
        }
        firing = false;
    }
    //
    private void runBelt(double speed){
        IF.setPower(speed);
        IS.setPower(speed);
    }

    private void shootBall(double speed){
        SL.setPower(-speed);
        SR.setPower(speed);
    }

    private void floorUp(){
        flipL.setPosition(.56);
        flipR.setPosition(.48);
    }

    private void floorDown(){
        flipL.setPosition(.2);
        flipR.setPosition(.2);
    }
//
//    private void feedLauncher(){
//        if (feedTimer.milliseconds() < feedDur && feeding == 0){
//            Floor.setPosition(0);
//            runBelt(0);
//        }
//        else if (feedTimer.milliseconds() < retDur && feeding == 1){
//            Floor.setPosition(1);
//        }
//        else if (feedTimer.milliseconds() < beltDur && feeding == 2) {
//            Floor.setPosition(1);
//            runBelt(beltSpeed);
//        }
//        else {
//            if (SL.getVelocity() >= velToPow(shootVel) - 30 && rs.getVelocity() >= velToPow(shootVel) - 30) {
//                if (feeding == 2)
//                    feeding = 0;
//                else
//                    feeding++;
//                fcount++;
//            }
//            feedTimer.reset();
//        }
//    }



}
