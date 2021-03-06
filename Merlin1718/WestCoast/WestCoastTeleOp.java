/**
 * This is the competition teleOp and has limited controls to that necessary in completing the
 * objectives.
 */

package org.firstinspires.ftc.teamcode.team.Merlin1718.WestCoast;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Disabled
@TeleOp(name = "Tele", group = "Cardinal")
//@Disabled //Uncomment this if it is not wanted on the phone
public class WestCoastTeleOp extends OpMode {

    public WestCoastHardware robot = new WestCoastHardware();//The hardware map needs to be the hardware map of the robot we are using

    public void init() {
        robot.init(hardwareMap);
        robot.glyphCollector.topGrasper.close();
        robot.glyphCollector.bottomGrasper.open();
        robot.jewelSorter.raise();
    }

    @Override
    public void init_loop() {}

    @Override
    public void start() {}//This runs when the start button is pressed

    boolean isPressed = false;
    boolean relic = false;
    @Override
    public void loop() {//This runs while opmode is active
        boolean pressed = gamepad1.left_trigger > .9 && gamepad1.right_trigger > .9;
        if (pressed && !isPressed) {
            isPressed = true;
        } else if (!pressed&& isPressed) {
            isPressed = false;
            relic = !relic;
        }
        if(!relic) {
            robot.westCoast.teleOp(gamepad1, true);
            robot.glyphCollector.teleOp(gamepad2);
        } else {
            robot.westCoast.teleOp(gamepad1, false);
            robot.relic.teleOp(gamepad2);
        }
    }

    @Override
    public void stop() {}
}