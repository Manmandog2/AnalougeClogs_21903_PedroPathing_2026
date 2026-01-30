package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class colorsensor {

    NormalizedColorSensor eye1;

    public enum DetectedColor {

        Red,
        Green,
        Blue,
        Unknown

    }

    public void init(HardwareMap hwMap) {
        eye1 = hwMap.get(NormalizedColorSensor.class, "eye1");

    }


    public DetectedColor getDetectedColor(Telemetry telemetry) {
        NormalizedRGBA colors = eye1.getNormalizedColors(); //return 4 values

        float normRed, normGreen, normBlue;
        normRed = colors.blue / colors.alpha;
        normGreen = colors.green / colors.alpha;
        normBlue = colors.blue / colors.alpha;

        telemetry.addData("red", normRed);
        telemetry.addData("green", normGreen);
        telemetry.addData("blue", normBlue);

        //add if fro specific colors added

        return DetectedColor.Unknown;
    }


}
