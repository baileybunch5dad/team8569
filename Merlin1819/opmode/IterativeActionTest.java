package org.firstinspires.ftc.teamcode.team.Merlin1819.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.team.Merlin1819.api.iterative.IterativeActionOpMode;
import org.firstinspires.ftc.teamcode.team.Merlin1819.api.iterative.IterativeState;

/**
 *
 * An example class to show how to control
 * state of an {@link IterativeActionOpMode} state machine.
 *
 * @author Zigy Lim
 * @version 1.0
 * @since 1.0
 */
@Autonomous(name = "zigyapitest")
@Disabled
public class IterativeActionTest extends IterativeActionOpMode
{
    private int ping,
                pong;

    public IterativeActionTest()
    {
        this.ping = this.pong = 0;
    }

    @Action(order = 0)
    public void updatePing(IterativeState state, HardwareMap map)
    {
        this.telemetry.addData("Ping", ++this.ping);
        this.telemetry.update();
    }

    @Action(order = 1)
    public void updatePong(IterativeState state, HardwareMap map)
    {
        this.telemetry.addData("Pong", ++this.pong);
        this.telemetry.update();
        state.restart();
    }
}
