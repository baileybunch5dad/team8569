/*
 * This is an old template and is no longer proper because we have switched to the OpMode
  * style rather than the LinerOpMode
 */

package org.firstinspires.ftc.teamcode.team.Other;//This might need to changed to be in a differnt folder like Merlin1 or K9Robo


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.team.Merlin1617.Merlin1.Merlin1Hardware; //More Import statements may be needed
import org.firstinspires.ftc.teamcode.team.Merlin1617.Merlin3.RevTouchHardware;

@TeleOp(name = "Touch", group = "Other")//This NEEDS to be changed tp the name of the code
public class RevTouchNoise extends LinearOpMode { //The name after public class needs to be the same as the file name


    /* Declare OpMode members. */
    RevTouchHardware robot = new RevTouchHardware();//The hardware map needs to be the hardware map of the robot we are using



    @Override
    public void runOpMode() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);
        //init other variables.

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //It is ready to run
        telemetry.update();//Updates and displays data on the screen.
        // run until the end of the match (driver presses STOP)
        waitForStart();

        while (opModeIsActive()) {

            telemetry.addData("state",robot.touch.getState());




            telemetry.update();
            // Pause for metronome tick.  40 mS each cycle = update 25 times a second.
            robot.waitForTick(40);
        }
    }

}