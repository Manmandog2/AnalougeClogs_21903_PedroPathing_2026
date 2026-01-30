//package org.firstinspires.ftc.teamcode.teleop;
//
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Servo;
//
//@TeleOp(name = "PowerOfFriendship")
//public class PowerOfFriendship extends LinearOpMode {
//
//    private DcMotor FL;
//    private DcMotor BL;
//    private DcMotor FR;
//    private DcMotor BR;
//    private DcMotor IF;
//    private DcMotor IS;
//    private DcMotor SL;
//    private DcMotor SR;
//    private Servo floor;
//    private Servo ceiling;
//
//    /**
//     * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
//     * Comment Blocks show where to place Initialization code (runs once, after touching the
//     * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
//     * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
//     * Stopped).
//     */
//    @Override
//    public void runOpMode() {
//        double my_1;
//
//        FL = hardwareMap.get(DcMotor.class, "FL");
//        BL = hardwareMap.get(DcMotor.class, "BL");
//        FR = hardwareMap.get(DcMotor.class, "FR");
//        BR = hardwareMap.get(DcMotor.class, "BR");
//        IF = hardwareMap.get(DcMotor.class, "IF");
//        IS = hardwareMap.get(DcMotor.class, "IS");
//        SL = hardwareMap.get(DcMotor.class, "SL");
//        SR = hardwareMap.get(DcMotor.class, "SR");
//        floor = hardwareMap.get(Servo.class, "floor");
//        ceiling = hardwareMap.get(Servo.class, "ceiling");
//
//        FL.setDirection(DcMotor.Direction.FORWARD);
//        BL.setDirection(DcMotor.Direction.FORWARD);
//        FR.setDirection(DcMotor.Direction.FORWARD);
//        BR.setDirection(DcMotor.Direction.FORWARD);
//        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        IF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        IS.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        SL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        SR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        my_1 = 0.65;
//        waitForStart();
//        if (opModeIsActive()) {
//            while (opModeIsActive()) {
//                // Put loop blocks here.
//                strafe(my_1);
//                shootie();
//                intake();
//                if (gamepad2.y) {
//                    floor.setPosition(0.9);
//                } else {
//                    floor.setPosition(0.58);
//                }
//                if (gamepad2.a) {
//                    ceiling.setPosition(0.99);
//                } else {
//                    ceiling.setPosition(0.05);
//                }
//                if (gamepad1.dpad_up) {
//                    my_1 = 0.9;
//                }
//                if (gamepad1.dpad_down) {
//                    my_1 = 0.65;
//                }
//            }
//        }
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void strafe(double power) {
//        double ZoomYes;
//
//        ZoomYes = power;
//        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
//        // We negate this value so that the topmost position corresponds to maximum forward power.
//        FL.setPower(power * gamepad1.right_stick_x + power * gamepad1.left_stick_x + -power * gamepad1.left_stick_y);
//        FR.setPower(power * gamepad1.right_stick_x + power * gamepad1.left_stick_x + power * gamepad1.left_stick_y);
//        // The Y axis of a joystick ranges from -1 in its topmost position to +1 in its bottommost position.
//        // We negate this value so that the topmost position corresponds to maximum forward power.
//        BL.setPower(power * gamepad1.right_stick_x + -power * gamepad1.left_stick_x + -power * gamepad1.left_stick_y);
//        BR.setPower(power * gamepad1.right_stick_x + -power * gamepad1.left_stick_x + power * gamepad1.left_stick_y);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void shootie() {
//        if (gamepad1.right_bumper) {
//            SL.setPower(-0.39);
//            SR.setPower(0.39);
//            //medium boi
//        }
//        if (gamepad1.left_bumper) {
//            SL.setPower(0);
//            SR.setPower(0);
//        }
//        if (gamepad1.dpad_left) {
//            SL.setPower(-0.45);
//            SR.setPower(0.45);
//            //stonk
//        }
//        if (gamepad1.dpad_right) {
//            SL.setPower(-0.36);
//            SR.setPower(0.36);
//            //little twink
//        }
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void intake() {
//        if (0 < gamepad2.right_trigger) {
//            IF.setPower(1);
//            IS.setPower(-1);
//        } else if (0 < gamepad2.left_trigger) {
//            IF.setPower(-1);
//            IS.setPower(1);
//        } else {
//            IF.setPower(0);
//            IS.setPower(0);
//        }
//    }
//}
