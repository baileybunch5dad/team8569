package org.firstinspires.ftc.teamcode.team.Merlin1819.api.iterative;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * Defines the behavior of a class that implements a linear set of operations.
 *
 * @author Zigy Lim
 *
 * @version 1.0
 * @since 1.0
 *
 * @see IterativeAction
 * @see OpMode
 * @see Action
 * @see IterativeState
 */
public abstract class IterativeActionOpMode extends OpMode implements IterativeActionPool
{
    /**
     * Defines the means for a subclass of
     * {@link IterativeActionOpMode} to mark
     * a method to be executed.
     * Methods must have the signature
     * <pre><b>void</b> (<b>IterativeState</b>, <b>HardwareMap</b>)</pre>
     * or else an {@link ActionAnnotationException} will be thrown.
     *
     * @author Zigy Lim
     *
     * @version 1.0
     * @since 1.0
     *
     * @see Action#order()
     * @see IterativeActionOpMode
     * @see IterativeState
     * @see ActionAnnotationException
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    protected @interface Action
    {
        /**
         *
         * Defines the order at which
         * actions are executed, meaning
         * that a method annotated with a value
         * of zero will be executed before a value of one.
         * If there are negative values, an {@link ActionAnnotationException}
         * will be thrown.
         *
         * {@code IterativeActionOpMode}'s least ordered value can be at any
         * non-negative number.
         * @since 1.0
         */
        int order() default 0;
    }



    /**
     *
     * The associated {@link IterativeState} object
     * to provide for control with the state machine.
     *
     *
     * @since 1.0
     *
     * @see #registered
     */
    private IterativeState state;

    /**
     *
     * A flag to indicate whether the actions associated
     * with this {@link OpMode} have been initialized.
     *
     * @since 1.0
     *
     * @see #state
     */
    private boolean registered;

    @Override
    public final void init()
    {
        if (!this.registered) registerActions();
        initState();
    }

    /**
     *
     * Constructs a new
     * {@code IterativeActionOpMode}.
     * In general, this class will
     * not initialize members until
     * the init method is called.
     *
     * @author Zigy Lim
     *
     * @since 1.0
     */
    protected IterativeActionOpMode()
    {
        this.registered = false;
    }

    /**
     *
     * This method is used to
     * initialize the
     * state of the robot.
     * It is recommended that initialization
     * be done in here, as every time the OpMode
     * is stopped and then started, the variables
     * will reset.
     *
     * @author Zigy Lim
     *
     * @since 1.0
     */
    protected void initState()
    {

    }

    @Override
    public final IterativeState getIterativeState()
    {
        return this.state;
    }

    /**
     *
     * Registers actions to be done. If
     * an attempt to register methods twice is
     * made, a {@link ActionsReregisterException}
     * is thrown.
     *
     * @author Zigy Lim
     *
     * @since 1.0
     *
     * @see ActionsReregisterException
     */
    @Override
    public void registerActions()
    {
        if (this.registered) {
            throw new ActionsReregisterException("Cannot re-register actions.");
        } else {
            final ArrayList<Method> methods = new ArrayList<>();
            final Class<?> clazz = this.getClass();
            boolean error = false;

            for (Method method: clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Action.class)) {
                    final Class<?>[] parameters = method.getParameterTypes();

                    if (!(parameters.length == 2 &&
                            parameters[0] == IterativeState.class &&
                            parameters[1] == HardwareMap.class)) {
                        try {
                            throw new ActionAnnotationException(
                                    "This method does not have the signature (IterativeState, HardwareMap).");
                        } catch (ActionAnnotationException e) {
                            e.printStackTrace();
                            error = true;
                        }
                    }

                    if (Modifier.isAbstract(method.getModifiers()) ||
                            Modifier.isStatic(method.getModifiers())) {
                        try {
                            throw new ActionAnnotationException(
                                    "This method cannot be abstract, static.");
                        } catch (ActionAnnotationException e) {
                            e.printStackTrace();
                            error = true;
                        }
                    }

                    if (!method.getReturnType().equals(Void.TYPE)) {
                        try {
                            throw new ActionAnnotationException("This must have a void return type.");
                        } catch (ActionAnnotationException e) {
                            e.printStackTrace();
                            error = true;
                        }
                    }

                    if (method.getAnnotation(Action.class).order() < 0) {
                        try {
                            throw new ActionAnnotationException("The method " + method.getName() +
                                    " has an invalid order value: " + method.getAnnotation(Action.class).order() + ".");
                        } catch (ActionAnnotationException e) {
                            e.printStackTrace();
                            error = true;
                        }
                    }

                    if (!error) {
                        methods.add(method);
                    }
                }
            }

            if (error) {
                throw new RuntimeException("There was an error while registering methods.");
            } else {
                final ArrayList<IterativeAction> actions = new ArrayList<>();

                /*
                Natural order sorting, meaning that an order of zero will
                come before an order of one.
                 */
                Collections.sort(methods, new Comparator<Method>() {

                    @Override
                    public int compare(Method m1, Method m2) {
                        return m1.getAnnotation(Action.class).order() -
                                m2.getAnnotation(Action.class).order();
                    }
                });

                for (final Method method: methods) {

                    /*
                    Wrap each methods reflective
                    invocation in the IterativeAction interface.
                     */
                    actions.add(new IterativeAction() {

                        @Override
                        public void execute(IterativeState state, HardwareMap map) {
                            try {
                                method.invoke(IterativeActionOpMode.this, state, map);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                this.state = new IterativeState(actions.toArray(new IterativeAction[0]));
            }
        }
    }

    @Override
    public final void loop()
    {

        if (!this.state.isFinished()) {
            this.state.setStateChangeAllowable(true);
            final IterativeAction nextAction = this.state.getNextAction();
            this.state.setStateChangeAllowable(true);
            this.state.setCompleted(this.isCompletedByDefault());
            this.state.setStateChangeAllowable(true);
            nextAction.execute(this.state, this.hardwareMap);
            if (!this.state.isCompleted()) {
                this.state.setStateChangeAllowable(true);
                this.state.restartFromMethod();
            }
        } else if (this.automaticallyStop()) {
            this.requestOpModeStop();
        }
    }

    @Override
    public final void stop()
    {
        this.state.restart();

        this.state.setCompleted(this.isCompletedByDefault());
    }

    /**
     * Returns if methods automatically complete on
     * their own. If subclasses want custom behavior, they
     * should override this class.
     *
     * @return if methods automatically complete on their own.
     *
     * @author Zigy Lim
     *
     * @since 1.0
     *
     * @see IterativeState#isCompleted()
     */
    protected boolean isCompletedByDefault()
    {
        return true;
    }

    /**
     * Returns if the {@link OpMode} automatically
     * stops when every operation is finished.
     *
     * @return if the {@link OpMode} automatically
     * stops when every operation is finished.
     *
     * @author Zigy Lim
     *
     * @since 1.0
     *
     * @see #stop()
     */
    protected boolean automaticallyStop()
    {
        return true;
    }
}
