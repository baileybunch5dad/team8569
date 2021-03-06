package org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.worlds;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.team.Merlin1819.api.iterative.IterativeActionOpMode;
import org.firstinspires.ftc.teamcode.team.Merlin1819.api.iterative.IterativeState;
import org.firstinspires.ftc.teamcode.team.Merlin1819.api.robot.Robot;
import org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.robot.CubeSampler;
import org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.robot.ExtendedMecanumController;
import org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.robot.MecanumHardwareMap;
import org.firstinspires.ftc.teamcode.team.Merlin1819.opmode.robot.MecanumRobot;

import java.util.Locale;

@Autonomous(name = "AutonomousWorlds", group = "Worlds")
@Disabled
public class AutonomousWorlds extends IterativeActionOpMode {
    private CubeSampler sampler;
    private CubeSampler.CubePosition yellowPosition = null;
    private Robot robot;
    private MecanumHardwareMap hardwareMap;
    private ExtendedMecanumController controller;
    private ElapsedTime elapsedTime;
    private ElapsedTime lastTime;

    //These all in milliseconds.
    private static final long DOWN_TIME    = 4600,
            UNLATCH_TIME = 1000,
            FORWARD_TIME = 2000,
            BACK_TIME    = 800,
            AWAY_FROM_CRATER_TIME = 1000;

    private static final float LOWERING_POWER = 0.5F,
            UNLATCH_POWER  = 0.125F,
            FORWARD_POWER  = 0.125F,
            BACK_POWER     = 0.125F,
            AWAY_FROM_CRATER_POWER = 0.25F;

    @Override
    protected void initState() {
        super.initState();
        assert (this.hardwareMap != null);


        //Getting the position before the robot lands
        //TODO verify that this camera angle is optimal
        this.robot = new MecanumRobot(super.hardwareMap);
        this.hardwareMap = new MecanumHardwareMap(super.hardwareMap);
        this.controller = this.robot.getRobotComponentController(ExtendedMecanumController.class);


//        this.telemetry.setAutoClear();
    }


    private boolean completed = false;

    private final void resetTime()
    {
        this.lastTime.reset();
    }

    private final boolean timePassed(long milliseconds)
    {
        return (this.lastTime.milliseconds() >= milliseconds);
    }

    private IterativeState state;

//    private void status() {
//        this.telemetry.addData("", this.state.getCounter() + this.lastTime.milliseconds());
//    }

    @Action(order = 0)
    public void init(IterativeState state, HardwareMap map)
    {
        this.state = state;
        this.sampler = new CubeSampler(super.hardwareMap, 0.40);
        this.sampler.activateTfod();

    }

    @Action(order = 1)
    public void startLowering(IterativeState state, HardwareMap map)
    {
        //this.elapsedTime = new ElapsedTime();
        this.lastTime = new ElapsedTime();
//        this.status();

        if(yellowPosition != null || timePassed(5000)) {
            this.controller.startMovingLiftMotor(LOWERING_POWER);
            state.setCompleted(true);
            yellowPosition = this.sampler.detectCubePosition();
            if(yellowPosition == null) yellowPosition = CubeSampler.CubePosition.CENTER;
        } else state.setCompleted(false);

    }

    @Action(order = 2)
    public void checkLowered(IterativeState state, HardwareMap map)
    {

//        this.status();
        if (this.timePassed(DOWN_TIME +  5000)) {
            this.controller.stopMovingLiftMotor();

            //setCompleted(boolean) makes it loop around this method if it is false.
            state.setCompleted(true);
        } else state.setCompleted(false);
    }

    @Action(order = 3)
    public void startUnlatching(IterativeState state, HardwareMap map)
    {
//        this.status();
        this.resetTime();

        //This will move us right, unlatching us in respect to our orientation.
        this.robot.startMoving(Robot.MovementDirection.FORWARD, UNLATCH_POWER);
    }

    @Action(order = 4)
    public void checkIfUnlatched(IterativeState state, HardwareMap map)
    {
        if (this.timePassed(UNLATCH_TIME)) {
            this.robot.stopMoving();
            state.setCompleted(true);
        } else state.setCompleted(false);
    }

    @Action(order = 5)
    public void startMovingOut(IterativeState state, HardwareMap map)
    {
        this.resetTime();

        //This will move us away from the lander in respect to our orientation.
        this.robot.startMoving(Robot.MovementDirection.RIGHT, FORWARD_POWER);
    }

    @Action(order = 6)
    public void checkIfMovedOut(IterativeState state, HardwareMap map)
    {
        if (timePassed(FORWARD_TIME)) {
            this.robot.stopMoving();
            state.setCompleted(true);
        } else state.setCompleted(false);
    }

    @Action(order = 7)
            public void recenter(IterativeState state, HardwareMap map)
    {
        this.resetTime();

        this.robot.startMoving(Robot.MovementDirection.BACKWARD, BACK_POWER);
    }

    @Action(order = 8)
            public void checkIfRecentered(IterativeState state, HardwareMap map)
    {
        if(timePassed(BACK_TIME)) {
            this.robot.stopMoving();
            state.setCompleted(true);
        } else state.setCompleted(false);
    }

    @Action(order = 9)
            public void goAwayFromLander(IterativeState state, HardwareMap map) {
        this.resetTime();

        this.robot.startMoving(Robot.MovementDirection.RIGHT, AWAY_FROM_CRATER_POWER);
    }

    @Action(order = 10)
            public void checkIfAwayFromCrater(IterativeState state, HardwareMap map)
    {
        if(timePassed(AWAY_FROM_CRATER_TIME)) {
            this.robot.stopMoving();
            state.setCompleted(true);
        } else state.setCompleted(false);
    }

    long[] counter = new long[3];

    @Action(order = 11)
    public void nudge(IterativeState state, HardwareMap map) {

        this.telemetry.addData("Cube Position", "Cube Position " + this.sampler.detectCubePosition());
//        this.telemetry.addData("Distance to wall", String.format(Locale.US, "%.01f in", this.hardwareMap.getDistanceSensor().getDistance(DistanceUnit.INCH)));

        switch (this.yellowPosition) {
            case LEFT:
                if (sideways(1, counter, 0) &&
                        dislodgeSample(counter, 1) &&
                        sideways(-1, counter, 2)) {
                    stop();
                }
                break;

                default:
            case CENTER:
                if (dislodgeSample(counter, 0)) stop();
                break;
            case RIGHT:
                if (sideways(-1, counter, 0) &&
                        dislodgeSample(counter, 1) &&
                        sideways(1, counter, 2))
                    stop();
                break;
        }

//        state.setCompleted(false);
    }


    public boolean dislodgeSample(long[] v, int idx) {
        v[idx]++;
        boolean isDone = false;
        if (v[idx] < 20) {
            goRight(.2);
        } else if (v[idx] < 40) {
            goLeft(.2);
        } else {
            isDone = true;
        }
        return isDone;

    }

    public boolean goToNeutral(long[] v, int idx) {
        v[idx]++;

        boolean isDone = false;

        if (v[idx] < 40) {
            goRight(0.2);
        } else {
            isDone = true;
        }

        return isDone;
    }

    public boolean sideways(int whichDir, long[] v, int idx) {
        v[idx]++;

        boolean isDone = false;

        if (v[idx] < 20) {
            if (whichDir == 1)
                goForward(.2);
            else goBackward(.2);
        } else {
            isDone = true;
        }

        return isDone;
    }


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
