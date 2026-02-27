package org.firstinspires.ftc.teamcode.teleop;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.ShootSystem;

@TeleOp(name = "Dihvid_Full_Manual")
public class Dihvid extends OpMode {

    private ShootSystem shooter;
    private Follower fol;
    private DcMotor FL, BL, FR, BR;

    // Presets
    private final int PRESET_HIGH   = 2000;
    private final int PRESET_MID    = 1500;
    private final int PRESET_LOW     = 1200;
    private final int PRESET_EJECT   = 800;

    @Override
    public void init() {
        fol = Constants.createFollower(hardwareMap);
        shooter = new ShootSystem(hardwareMap, telemetry, fol);

        FL = hardwareMap.get(DcMotor.class, "FL");
        BL = hardwareMap.get(DcMotor.class, "BL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        BR = hardwareMap.get(DcMotor.class, "BR");

        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.REVERSE);

        shooter.shootVel = PRESET_MID;
    }

    @Override
    public void loop() {
        fol.update();

        // 1. DRIVETRAIN (Always Active)
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;
        FL.setPower(y + x + rx);
        BL.setPower(y - x + rx);
        FR.setPower(y - x - rx);
        BR.setPower(y + x - rx);

        // 2. PRESET SELECTION (D-Pad)
        if (gamepad1.dpad_up)    shooter.shootVel = PRESET_HIGH;
        if (gamepad1.dpad_left)  shooter.shootVel = PRESET_MID;
        if (gamepad1.dpad_right) shooter.shootVel = PRESET_LOW;
        if (gamepad1.dpad_down)  shooter.shootVel = PRESET_EJECT;

        // 3. FLYWHEEL MANUAL CONTROL
        // Hold A to spin the flywheels to the selected preset speed.
        if (gamepad1.a) {
            shooter.updateFlywheelControl(shooter.shootVel);
        } else {
            shooter.updateFlywheelControl(0);
        }

        // 4. GATE MANUAL CONTROL
        // Hold Right Bumper to open the gate, release to close.
        if (gamepad1.right_bumper) {
            shooter.gateOpen();
        } else {
            shooter.gateClose();
        }

        // 5. BELT/INTAKE MANUAL CONTROL
        // Right Trigger: Intake / Feed to shooter
        // Left Trigger: Reverse / Outtake
        if (gamepad1.right_trigger > 0.1) {
            shooter.RunBelt(0.9);
        } else if (gamepad1.left_trigger > 0.1) {
            shooter.RunBelt(-0.8);
        } else {
            shooter.StopBelt();
        }

        // 6. EMERGENCY STOP (Bumper kills everything)
        if (gamepad1.left_bumper) {
            shooter.StopMotors();
            shooter.shootVel = 0; // Reset preset to 0 as safety
        }

        telemetry.addData("Preset", shooter.shootVel);
        telemetry.addData("Flywheel Speed", (int)((shooter.flywheelLeft.getVelocity() + shooter.flywheelRight.getVelocity()) / 2.0));
        telemetry.update();
    }
}