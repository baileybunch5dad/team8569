package org.firstinspires.ftc.teamcode.team.Merlin1819.api.iterative;

/**
 * Defines the state of the state machine.
 *
 * @author Zigy Lim
 *
 * @version 1.0
 * @since 1.0
 *
 * @see IterativeActionPool
 * @see IterativeAction
 * @see IterativeActionOpMode
 */
public final class IterativeState
{
    /**
     *
     * The queue counter to determine
     * which action will be returned
     * to be executed next.
     *
     * @since 1.0
     *
     * @see #actions
     */
    private int counter;

    /**
     *
     * The actions to be executed in a (usually)
     * linear fashion.
     *
     * @since 1.0
     *
     * @see #counter
     */
    private IterativeAction[] actions;

    /**
     * Constructs a new {@code IterativeState}
     *
     * The constructor will throw an IllegalArgumentException
     * if a zero length or null array reference is passed.
     *
     * @param actions the actions to be passed
     *
     * @author Zigy Lim
     *
     * @since 1.0
     */
    public IterativeState(IterativeAction[] actions)
    {
        if (actions == null || actions.length == 0) {
            throw new IllegalArgumentException();
        } else {
            this.counter = -1;
            this.actions = actions;
        }
    }

    /**
     *
     * Returns if this operation is isFinished so that
     * a user of this class can define some type of shutdown
     * state.
     *
     * @return is this operation finished.
     *
     * @author Zigy Lim
     *
     * @since 1.0
     *
     */
    public boolean isFinished()
    {
        return (this.counter + 1 == this.actions.length);
    }

    /**
     *
     * Restarts the series of actions, allowing for the action(s)
     * to be repeated.
     *
     * @author Zigy Lim
     *
     * @since 1.0
     */
    public void restart()
    {
        this.counter = -1;
    }

    /**
     *
     * Skips over every action, meaning that this
     * Action will end every other action, assuming
     * that a call to {@link #restart()}
     * wasn't made.
     *
     * @author Zigy Lim
     *
     * @since 1.0
     */
    public void stop()
    {
        while (this.getNextAction() != null);
    }

    /**
     *
     * Restarts the action chain at
     * the action that called it. Therefore,
     * if an action has called this method, the
     * next iteration this method will be executed again.
     *
     * @author Zigy Lim
     *
     * @since 1.0
     */
    public void restartFromMethod()
    {
        counter--;
    }

    /**
     *
     * Returns the next linear operation of execution.
     * Note that this function could also act as a skipping
     * function, if you need to skip over an operation.
     *
     * @return the next action if there is one.
     *
     * @author Zigy Lim
     *
     * @since 1.0
     */
    public IterativeAction getNextAction()
    {
        if (this.isFinished()) return null;
        else {
            return this.actions[++counter];
        }
    }

    /**
     *
     * Skips over a certain number of actions.
     * If an attempt to skip over zero or less actions
     * is made, this method will throw an exception.
     *
     * @param number the number of instructions to skip
     *
     * @author Zigy Lim
     *
     * @since 1.0
     *
     * @see #getNextAction()
     */
    public void skipActions(int number)
    {
        if (number < 1) throw new IllegalArgumentException("Cannot skip below 1 actions.");
        else if (this.actions.length - 1 < this.counter + number)
            throw new IllegalArgumentException("Cannot skip past actions.");
        else {
            for (int i = 0; i < number; i++) {
                this.getNextAction();
            }
        }
    }
}
