package org.firstinspires.ftc.teamcode.team.Merlin1819.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.teamcode.team.Merlin1819.api.robot.Robot;
import org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.robot.ExtendedMecanumController;
import org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.robot.MecanumHardwareMap;
import org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.robot.MecanumIMU;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.robot.MecanumRobot;

import java.util.Locale;

@TeleOp(name = "MasterTeleOp")
public class MasterTeleOp extends OpMode
{
    private static final float GAMEPAD_DEAD_ZONE = 0.2F;

    private static final float COLLECTOR_POWER = 1;

    private static final float SPEED_MULTIPLIER = 1;

    private MecanumHardwareMap hardwareMap;
    private MecanumIMU imu;
    private Robot robot;
    private ExtendedMecanumController controller;


    @Override
    public void init() {
        this.hardwareMap = new MecanumHardwareMap(super.hardwareMap);
        this.imu = new MecanumIMU(super.hardwareMap);
    }

    @Override
    public void loop()
    {
//      this.telemetry.addData("Enter ",new java.util.Date());

        //Gunner Section

        //this controls the curl arm motor setting it based on the y axis of the left joy stick.

            double power = 0;
            if (Math.abs(this.gamepad2.left_stick_y) >= GAMEPAD_DEAD_ZONE) {
                final double leftStickY = this.gamepad2.left_stick_y;
                power = leftStickY * 0.8;
                this.hardwareMap.getCurlArmMotor().setPower(power);
            } else {
                this.hardwareMap.getCurlArmMotor().setPower(0);
            }


//        this.telemetry.addData("Left Stick", this.gamepad2.left_stick_y);
//        this.telemetry.addData("Curl Arm Power", power);

        // this is where the driver controls the extension of the arm on the gunner right joystick.
        if (Math.abs(this.gamepad2.right_stick_y) >= GAMEPAD_DEAD_ZONE) {
            this.hardwareMap.getRetractArmMotor().setPower(this.gamepad2.right_stick_y);
        } else {
            this.hardwareMap.getRetractArmMotor().setPower(0);
        }

        Servo servo = this.hardwareMap.getCollectorServo();

        boolean leftTriggerPressed = this.gamepad2.left_trigger >= GAMEPAD_DEAD_ZONE;
        boolean rightTriggerPressed = this.gamepad2.right_trigger >= GAMEPAD_DEAD_ZONE;

        if (leftTriggerPressed || rightTriggerPressed) {
            servo.setDirection(leftTriggerPressed ? Servo.Direction.REVERSE : Servo.Direction.FORWARD);
            servo.setPosition(1);
        } else {
            servo.setPosition(.5);
        }

        this.imu.updateAngle();

        final double leftStickY = this.gamepad1.left_stick_y,
                leftStickX = this.gamepad1.left_stick_x,
                leftTrigger = this.gamepad1.left_trigger,
                rightTrigger = this.gamepad1.right_trigger;

        double forward = -leftStickY,
                right  =  leftStickX,
                clockwise = (leftTrigger >= rightTrigger) ?
                        leftTrigger : -rightTrigger;
        double degrees = this.imu.getAngle();
        double radians = Math.toRadians(degrees); /* conv 0-360 to 0-2 pi */
        double  temp   = forward * Math.cos(radians) - right * Math.sin(radians);
        right  = forward * Math.sin(radians) + right * Math.cos(radians);
        forward = temp;

        /*this.telemetry.addData("Debug INFO", "leftStickX = " +
                leftStickX + ", leftStickY = " + leftStickY +
                ", leftTrigger = " + leftTrigger + ", rightTrigger = " +
                rightTrigger + ", forward = " + forward + ", right = " +
                right + ", clockwise = " + clockwise + ", degrees = " +
                degrees);*/

        double  frontLeft  = forward + clockwise + right,
                frontRight = forward - clockwise - right,
                backLeft   = forward + clockwise - right,
                backRight  = forward - clockwise + right,
                max = Math.abs(frontLeft);

        if (frontRight > max) max = Math.abs(frontRight);
        if (backLeft   > max) max = Math.abs(backLeft);
        if (backRight  > max) max = Math.abs(backRight);
        //if (frontLeft  > max) max = Math.abs(frontLeft);

        if (max > 1) {
            frontLeft  /= max;
            frontRight /= max;
            backLeft   /= max;
            backRight  /= max;
        }

        this.hardwareMap.getFrontLeftMotor().setPower(frontLeft);
        this.hardwareMap.getFrontRightMotor().setPower(frontRight);
        this.hardwareMap.getBackLeftMotor().setPower(backLeft);
        this.hardwareMap.getBackRightMotor().setPower(backRight);

//        this.telemetry.setAutoClear(false);
//        this.telemetry.addData("Front Left",  this.hardwareMap.getFrontLeftMotor().getPower()).
//                       addData("Front Right", this.hardwareMap.getFrontRightMotor().getPower()).
//                       addData("Back Left",   this.hardwareMap.getBackLeftMotor().getPower()).
//                       addData("Back Right",  this.hardwareMap.getBackRightMotor().getPower());
//        this.telemetry.setAutoClear(true);
        //this.telemetry.addData("Encoder", this.hardwareMap.getRetractArmMotor().getCurrentPosition());

        //Driver controls for the lift. The driver controls the lift because that makes lifting easier because the driv can do it with less coordination.

        if (Math.abs(this.gamepad1.right_stick_y) >= GAMEPAD_DEAD_ZONE) {
            this.hardwareMap.getLiftMotor().setPower(-this.gamepad1.right_stick_y);
        } else {
            this.hardwareMap.getLiftMotor().setPower(0);
        }

        //Gyro reset button for the gunner because after Autonomous, the robot is not facing the right way

        if (this.gamepad1.y) {
            this.imu.initializeIMU();
            this.telemetry.addData("Notification", "You reset the IMU angle.");
        }

//        double inches = this.hardwareMap.getDistanceSensor().getDistance(DistanceUnit.INCH);
//        String easyInches = String.format(Locale.US, "%.01f in", inches);
//
//        telemetry.addData("distance: ", easyInches);

//      this.telemetry.addData("Angle", this.imu.getAngle());

        //color sensor telemetry
       // final ColorSensor sensor = this.hardwareMap.getColorSensor();
//        telemetry.addData("Red", sensor.red());
//        telemetry.addData("Green", sensor.green());
//        telemetry.addData("Blue", sensor.blue());
//        telemetry.addData("Alpha", sensor.alpha());
//        telemetry.addData("Combined", sensor.argb());
//        telemetry.addData("Combined Hex", "0x" + Integer.toHexString(sensor.argb()).toUpperCase());
//        this.telemetry.addData("Exit ",new java.util.Date());

//        this.telemetry.addData("Front Left Encoder", this.hardwareMap.getFrontLeftMotor().getCurrentPosition());
//        this.telemetry.addData("Front Right Encoder", this.hardwareMap.getFrontRightMotor().getCurrentPosition());
//        this.telemetry.addData("Back Left Encoder", this.hardwareMap.getBackLeftMotor().getCurrentPosition());
//        this.telemetry.addData("Back Right Encoder", this.hardwareMap.getBackRightMotor().getCurrentPosition());
//
//        this.telemetry.addData("Linear Actuator Encoder", this.hardwareMap.getLiftMotor().getCurrentPosition());
//        telemetry.update();
    }
}
