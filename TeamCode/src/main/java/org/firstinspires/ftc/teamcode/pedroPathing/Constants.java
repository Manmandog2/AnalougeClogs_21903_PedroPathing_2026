package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .translationalPIDFCoefficients(new PIDFCoefficients(0.07, 0, 0.0001, 0.008))
            .headingPIDFCoefficients(new PIDFCoefficients(.8, 0, 0.0001, 0.03))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.08, 0, 0.0001, 0.6, 0.08))
            .centripetalScaling(0.0008)
            .forwardZeroPowerAcceleration(-27)
            .lateralZeroPowerAcceleration(-65.5)
            .mass(10.6);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .useBrakeModeInTeleOp(true)
            .xVelocity(62)
            .yVelocity(52.5)
            .maxPower(1.0)
            .rightFrontMotorName("FR")
            .rightRearMotorName("BR")
            .leftRearMotorName("BL")
            .leftFrontMotorName("FL")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.REVERSE);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);
    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(4.4)
            .strafePodX(-4)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("odd")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .pinpointLocalizer(localizerConstants)
                .mecanumDrivetrain(driveConstants)
                .build();




    }
}
