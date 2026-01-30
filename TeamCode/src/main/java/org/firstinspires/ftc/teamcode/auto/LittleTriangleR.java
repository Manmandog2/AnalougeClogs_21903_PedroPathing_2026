//package org.firstinspires.ftc.teamcode.auto;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.BezierLine;
//import com.pedropathing.geometry.Pose;
//import com.pedropathing.paths.PathChain;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.pedropathing.util.Timer;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Servo;
//
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
//
//
//
//@Autonomous
//public class LittleTriangleR extends OpMode {
//    private DcMotor IF;
//    private DcMotor IS;
//    private DcMotor SL;
//    private DcMotor SR;
//    private Servo floor;
//    private Follower follower;
//    private Timer pathTimer, OpModeTimer;
//
//    public Pose getFirstLoadPose() {
//        return firstLoadPose;
//    }
//
//    private int pathState = 0;
//    private void setPathState(int num){
//        pathState = num;
//    }
//
//    private final Pose startPose = new Pose(88.95348837209302, 8.790697674418604, Math.toRadians(90));
//    private final Pose shootPrePose = new Pose(127.88372093023256, 14.441860465116273, Math.toRadians(90));
//    private final Pose firstLoadPose = new Pose(47.302325581395344, 74.09302325581396, Math.toRadians(320));
//    private final Pose firstIntakePose = new Pose(122.30232558139537, 84.13953488372093, Math.toRadians(0));
//    private final Pose secondLoadPose = new Pose(96.69767441860465, 60.279069767441854, Math.toRadians(0));
//    private final Pose secondIntakePose = new Pose(128.09302325581396, 60.279069767441854, Math.toRadians(0));
//    private final Pose shootFirstPose = new Pose(96.27906976744185, 99, Math.toRadians(225));
//
//    private PathChain pathStartPosShootPrePos, pathShootPrePosFirstLoadPos, pathFirstLoadPosFirstIntakePos,
//            pathFirstIntakePosShootFirstPos;
//
//    public void buildPaths() {
//        //put in cords for starting pos - ending pos
//        pathStartPosShootPrePos = follower.pathBuilder()
//                .addPath(new BezierLine(startPose, shootPrePose))
//                .setLinearHeadingInterpolation(startPose.getHeading(), shootPrePose.getHeading())
//                .build();
////        pathShootPrePosFirstLoadPos = follower.pathBuilder()
////                .addPath(new BezierLine(shootPrePose, firstLoadPose))
////                .setLinearHeadingInterpolation(shootPrePose.getHeading(), firstLoadPose.getHeading())
////                .build();
////    pathFirstLoadPosFirstIntakePos = follower.pathBuilder()
////            .addPath(new BezierLine(firstLoadPose, firstIntakePose))
////            .setLinearHeadingInterpolation(firstLoadPose.getHeading(), firstIntakePose.getHeading())
////            .build();
////    pathFirstIntakePosShootFirstPos = follower.pathBuilder()
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
////            case 1:
////                //check is follower done it's path
////                if (!follower.isBusy()) {
////                    follower.followPath(pathShootPrePosFirstLoadPos, true);
////                    setPathState(2);
////                }
////            break;
////        case 2:
////            if (!follower.isBusy()) {
////                follower.followPath(pathFirstLoadPosFirstIntakePos, true);
////                setPathState(3);
////            }
////            break;
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
//
//        buildPaths();
//        follower.setPose(startPose);
//
//    }
//
//    public void start() {
//        OpModeTimer.resetTimer();
//        setPathState(pathState);
//    }
//
//    @Override
//    public void loop() {
//        follower.update();
//        statePathUpdate();
//
//        telemetry.addData("path state", pathState);
//        telemetry.addData("x", follower.getPose().getX());
//        telemetry.addData("y", follower.getPose().getY());
//        telemetry.addData("heading", follower.getPose().getHeading());
//        telemetry.addData("Path time", pathTimer.getElapsedTimeSeconds());
//    }
//
//    private void Intake (double speed){
//        IF.setPower(.7);
//        IS.setPower(-.7);
//    }
//    private void Shoot (double speed){
//        SL.setPower(-0.4);
//        SR.setPower(0.4);
//    }
//    private void FloorUP (){
//        floor.setPosition(0.88);
//    }
//    private void FloorDOWN(){
//        floor.setPosition(0.59);
//    }
//
//
//
//    public Pose getSecondLoadPose() {
//        return secondLoadPose;
//    }
//
//    public Pose getSecondIntakePose() {
//        return secondIntakePose;
//    }
//}
