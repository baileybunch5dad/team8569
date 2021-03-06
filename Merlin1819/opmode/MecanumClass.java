package org.firstinspires.ftc.teamcode.team.Merlin1819.opmode;

/**
 * Created by Zachary Ireland on 11/24/2018.
 */

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;


public class MecanumClass {
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backRightMotor;
    private DcMotor backLeftMotor;


    MecanumClass(DcMotor frontLeftMotor, DcMotor frontRightMotor, DcMotor backRightMotor,
                 DcMotor backLeftMotor) {
        this.frontLeftMotor = frontLeftMotor;
        this.frontRightMotor = frontRightMotor;
        this.backRightMotor = backRightMotor;
        this.backLeftMotor = backLeftMotor;

    }

    public void drive(double frontLeftPower, double frontRightPower, double backRightPower, double backLeftPower) {
        //this method rangeclips the motorpower to be from 1 to -1
        frontLeftMotor.setPower(Range.clip(frontLeftPower, -1, 1));
        frontRightMotor.setPower(Range.clip(frontRightPower, -1, 1));
        backRightMotor.setPower(Range.clip(backRightPower, -1, 1));
        backLeftMotor.setPower(Range.clip(backLeftPower, -1, 1));
    }

    public class JoyValues {
        double x;
        double y;
        double z;

        JoyValues(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public JoyValues joyValues(Gamepad g) {
        JoyValues joyXYZ = new JoyValues(0, 0, 0);
        //Set value x to the x axis on the left joystick if the value is above .01
        if (Math.abs(g.left_stick_x) > .01) {
            joyXYZ.x = g.left_stick_x;
            //otherwise set vaue to 0
        }
        //Set value y to the y axis on the left joystick if the value is above .01
        if (Math.abs(g.left_stick_y) > .01) {
            joyXYZ.y = -g.left_stick_y;
            //otherwise set value to 0
        }
        //Set value z to the z axis on the left trigger if the value is above .01
        if (g.left_trigger > .01) {
            joyXYZ.z = -g.left_trigger;
            //Set value z to the z axis on the right trigger if the value is above .01
        } else if (g.right_trigger > .01) {
            joyXYZ.z = g.right_trigger;
            //if the value is not above .01 set the value to 0
        }
        return joyXYZ;

    }
}

