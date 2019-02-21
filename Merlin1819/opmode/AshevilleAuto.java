package org.firstinspires.ftc.teamcode.team.Merlin1819.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.robot.MecanumHardwareMap;
import org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.robot.MecanumIMU;

public abstract class AshevilleAuto extends OpMode {

    private MecanumHardwareMap hardwareMap;
    private MecanumIMU imu;
    private long startTime;
    private long curTime;
    double elapsedTime;
    private boolean motorShutdown;
    /*DcMotor liftMotor = this.hardwareMap.getLiftMotor();
    DcMotor frontRightMotor = this.hardwareMap.getFrontRightMotor();
    DcMotor frontLeftMotor = this.hardwareMap.getFrontLeftMotor();
    DcMotor backLeftMotor = this.hardwareMap.getBackLeftMotor();
    DcMotor backRightMotor = this.hardwareMap.getBackRightMotor();*/

    static double DOWNTIME = 5;
    static double FWDTIME = DOWNTIME + 1.5;
    static double RIGHTTIME = FWDTIME + 2;

    public void init() {
        motorShutdown = false;
        this.hardwareMap = new MecanumHardwareMap(super.hardwareMap);
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
    }


    @Override
    public void loop() {
        curTime = System.currentTimeMillis();
        elapsedTime = (curTime - startTime) / 1000.0;

        telemetry.addData("Start Time", startTime);
        telemetry.addData("Current Time", curTime);
        telemetry.addData("Elapsed Time", elapsedTime);
        telemetry.update();

        if (elapsedTime < DOWNTIME) {
            this.hardwareMap.getLiftMotor().setPower(.5);
        } else if (elapsedTime < FWDTIME) {
            this.hardwareMap.getLiftMotor().setPower(0);

            goForward(.125); //Robot starts sideways and this gets our hook out of the bracket
        } else if (elapsedTime < RIGHTTIME) {

            goRight(.25); //This moves us away from the lander since the robot is sideways

        } else {
            if (!motorShutdown) {

                stopMotors();
                motorShutdown = true;

            } else {

                afterLanding();

            }
        }
    }

    protected abstract void afterLanding();

    protected final void goForward(double power) {
        this.hardwareMap.getFrontLeftMotor().setPower(power);
        this.hardwareMap.getFrontRightMotor().setPower(power);
        this.hardwareMap.getBackLeftMotor().setPower(power);
        this.hardwareMap.getBackRightMotor().setPower(power);
    }
    protected final void goLeft(double power) {
        this.hardwareMap.getFrontLeftMotor().setPower(power);
        this.hardwareMap.getFrontRightMotor().setPower(-power);
        this.hardwareMap.getBackLeftMotor().setPower(-power);
        this.hardwareMap.getBackRightMotor().setPower(power);
    }
    protected final void goRight(double power) {
        this.hardwareMap.getFrontLeftMotor().setPower(-power);
        this.hardwareMap.getFrontRightMotor().setPower(power);
        this.hardwareMap.getBackLeftMotor().setPower(power);
        this.hardwareMap.getBackRightMotor().setPower(-power);
    }
    protected final void goBackward(double power) {
        this.hardwareMap.getFrontLeftMotor().setPower(-power);
        this.hardwareMap.getFrontRightMotor().setPower(-power);
        this.hardwareMap.getBackLeftMotor().setPower(-power);
        this.hardwareMap.getBackRightMotor().setPower(-power);
    }

    protected final void stopMotors() {
        this.hardwareMap.getFrontLeftMotor().setPower(0);
        this.hardwareMap.getFrontRightMotor().setPower(0);
        this.hardwareMap.getBackLeftMotor().setPower(0);
        this.hardwareMap.getBackRightMotor().setPower(0);


        this.hardwareMap.getLiftMotor().setPower(0);
    }
}
