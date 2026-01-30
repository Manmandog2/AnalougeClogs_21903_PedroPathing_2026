//package org.firstinspires.ftc.teamcode.auto;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.BezierLine;
//import com.pedropathing.geometry.Pose;
//import com.pedropathing.paths.PathChain;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.pedropathing.util.Timer;
//
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
//import org.firstinspires.ftc.teamcode.subsystems.FlywheelLogic;
//
//
//@Autonomous
//public class TestingPedro extends OpMode {
//    private Follower follower;
//    private Timer pathTimer, OpModeTimer;
//
//    //-------------Flywheel Logic------------------
//    private FlywheelLogic shooter = new FlywheelLogic();
//
//    private boolean shotsTriggered = false;
//
//    public Pose getFirstLoadPose() {
//        return firstLoadPose;
//    }
//
//    private int pathState = 0;
//    public void setPathState(int num){
//        pathState = num;
//        pathTimer.resetTimer();
//
//        shotsTriggered = false;
//    }
//
//    private final Pose startPose = new Pose(123.27906976744185, 121.3953488372093, Math.toRadians(217));
//    private final Pose shootPrePose = new Pose(96.27906976744185, 99, Math.toRadians(225));
//    private final Pose firstLoadPose = new Pose(100.25581395348837, 83.51162790697674, Math.toRadians(0));
//    private final Pose firstIntakePose = new Pose(122.30232558139537, 84.13953488372093, Math.toRadians(0));
//    private final Pose secondLoadPose = new Pose(96.69767441860465, 60.279069767441854, Math.toRadians(0));
//    private final Pose secondIntakePose = new Pose(128.09302325581396, 60.279069767441854, Math.toRadians(0));
//    private final Pose shootFirstPose = new Pose(96.27906976744185, 99, Math.toRadians(225));
//
//    private PathChain pathStartPosShootPrePos, pathShootPrePosFirstLoadPos, pathFirstLoadPosFirstIntakePos, pathFirstIntakePosShootFirstPos;
//
//    public void buildPaths() {
//        //put in cords for starting pos - ending pos
//        pathStartPosShootPrePos = follower.pathBuilder()
//                .addPath(new BezierLine(startPose, shootPrePose))
//                .setLinearHeadingInterpolation(startPose.getHeading(), shootPrePose.getHeading())
//                .build();
//        pathShootPrePosFirstLoadPos = follower.pathBuilder()
//                .addPath(new BezierLine(shootPrePose, firstLoadPose))
//                .setLinearHeadingInterpolation(shootPrePose.getHeading(), firstLoadPose.getHeading())
//                .build();
////        pathFirstLoadPosFirstIntakePos = follower.pathBuilder()
////            .addPath(new BezierLine(firstLoadPose, firstIntakePose))
////            .setLinearHeadingInterpolation(firstLoadPose.getHeading(), firstIntakePose.getHeading())
////            .build();
////        pathFirstIntakePosShootFirstPos = follower.pathBuilder()
////            .addPath(new BezierLine(firstIntakePose, shootFirstPose))
////            .setLinearHeadingInterpolation(firstIntakePose.getHeading(), shootFirstPose.getHeading())
////            .build();
//    }
//
//
//
//    public void statePathUpdate() {
//        switch (pathState) {
//            case 0:
//                if (!follower.isBusy()) {
//                    follower.followPath(pathStartPosShootPrePos, true);
//                    setPathState(1); //rest timer + make new state
//                }
//                break;
//
//            case 1:
//                //shoot code start?
//                if (!follower.isBusy()) {
//                    //request shots yet
//                    if (!shotsTriggered) {
//                        shooter.fireShots(3);
//                        shotsTriggered = true;
//                    }
//                    else if (shotsTriggered && !shooter.isBusy()) {
//                        //shots are done fre to transition
//                        follower.followPath(pathShootPrePosFirstLoadPos, true);
//                        setPathState(2);
//                    }
//                }
//                //shoot code end?
//            break;
//
////        case 2:
////            if (!follower.isBusy()) {
////                follower.followPath(pathFirstLoadPosFirstIntakePos, true);
////                setPathState(3);
////            }
////            break;
////
////        case 3:
////            if (!follower.isBusy()) {
////                follower.followPath(pathFirstIntakePosShootFirstPos, true);
////                setPathState(4);
////                break;
////            }
//
//
//        }
//    }
//
//
//
//
//    @Override
//    public void init() {
//        pathState = 0;
//        pathTimer = new Timer();
//        OpModeTimer = new Timer();
//        follower = Constants.createFollower(hardwareMap);
//        //TODD add other init functions
//        shooter.init(hardwareMap);
//
//        buildPaths();
//        follower.setPose(startPose);
//
//    }
//
//    public void start() {
//        OpModeTimer.resetTimer();
//    }
//
//    @Override
//    public void loop() {
//        follower.update();
//        shooter.update();
//        statePathUpdate();
//
//        telemetry.addData("path state", pathState);
////        telemetry.addData("x", follower.getPose().getX());
////        telemetry.addData("y", follower.getPose().getY());
////        telemetry.addData("heading", follower.getPose().getHeading());
////        telemetry.addData("Path time", pathTimer.getElapsedTimeSeconds());
//    }
//
//    public static class auto2 {
//    }
//}
