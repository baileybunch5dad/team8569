package org.firstinspires.ftc.teamcode.team.Merlin1718.WestCoast;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Disabled
@Autonomous(name = "WestCoastBlueC", group = "Cardinal")
//@Disabled //Uncomment this if it is not wanted on the phone
public class WestCoastBlueC extends OpMode {

    public WestCoastHardware robot = new WestCoastHardware();//The hardware map needs to be the hardware map of the robot we are using

    public void init(){//This only runs once
        robot.init(hardwareMap);
        robot.motoG.initCamera();
        robot.glyphCollector.topGrasper.close();
        robot.glyphCollector.bottomGrasper.open();
        ;//Initializing everything needed
    }
    @Override
    public void init_loop(){//Tis runs many time during the init phase
    }
    @Override
    public void start(){}//This runs when the start button is pressed
    // distance of variables


    double versionNumber = 103;


    private String color = "blue";
    private double spinRightToKnockOffRightJewel = 7;
    private double spinLeftToKnockOffLeftJewel  = -7;
    private double driveDistanceToRightColumn = 36;//36
    private double driveDistanceToCenterColumn = 29;// 30
    private double driveDistanceToLeftColumn = 24;//
    private double driveForwardToCryptobox = 10;//9
    private double driveAwayFromCryptobox = 8;

    double angle=-90;
    double angleDirrection = -1;


    String lowerCase = "Spin180AwayFromCryptobox";
    private String currentCase = "DropSorter";
    private String redJewel;
    private String column = "null";
    @Override
    public void loop(){//This runs while opmode is active
        telemetry.addData("Version Number: ", versionNumber);
        telemetry.addData("CurrentCase", currentCase);
        telemetry.addData("jewel", redJewel);
        telemetry.addData("colum", column);
        telemetry.addData("power", robot.westCoast.leftMotor.getPower());
        double currentAngle = robot.revIMU.getCurrentOrientation();
        switch (currentCase){
            case "DropSorter": //startLowering sorter to sort jewl
                boolean doneYet;
                doneYet = robot.jewelSorter.lower();
                if(doneYet){
                    currentCase = "IDCryptographPicAndJewelColor";
                }
                break;
            case "IDCryptographPicAndJewelColor": //determine cryptograph and what color the jewl closest to the crytobo is and spin left or right based off color
                redJewel = robot.jewelSorter.auto();
                column = robot.motoG.key();
                if(!column.equals("null") && !redJewel.equals("null")){
                    if(redJewel.equals(color)){
                        currentCase = "SpinRightToKnockOffRightJewel";
                    }
                    else {
                        currentCase = "SpinLeftToKnockOffLeftJewel";
                    }
                }
                break;
            case "SpinRightToKnockOffRightJewel": //spin right to sort jewl, then raise sorter
                doneYet = robot.westCoast.turnToGyroHeading(true, spinRightToKnockOffRightJewel, currentAngle);
                if(doneYet){
                    currentCase = "RaiseSorter";
                }
                break;
            case "SpinLeftToKnockOffLeftJewel": //spin left to sort jewl, then raise sorter
                doneYet = robot.westCoast.turnToGyroHeading(true, spinLeftToKnockOffLeftJewel, currentAngle);
                if(doneYet){
                    currentCase = "RaiseSorter";
                }
                break;
            //raise sorter
            case "RaiseSorter":
                doneYet = robot.jewelSorter.raise();
                if(doneYet){
                    currentCase = "SpinBackToStartingPosition";
                }
                break;
            case "SpinBackToStartingPosition": //resenter robot
                doneYet = robot.westCoast.turnToGyroHeading(true, 0, currentAngle);
                if(doneYet){
                    currentCase = choseColumnCase(column);
                }
                break;

            case "ToRightColumn": //move to right collumn
                doneYet = robot.westCoast.driveBasedOnEncodersAndGyro(driveDistanceToRightColumn, -1, true, 0, currentAngle);//distance, direction, targetHeading, currentHeading
                telemetry.addData("start: ", robot.westCoast.startEncoder);
                telemetry.addData("current: ", robot.westCoast.getLeftCurrentMotorPosition());
                if(doneYet){
                    currentCase = "SpinTo90";

                }
                break;

            case "ToCenterColumn": //move to center column
                doneYet = robot.westCoast.driveBasedOnEncodersAndGyro(driveDistanceToCenterColumn, -1, true, 0, currentAngle);
                if(doneYet){
                    currentCase = "SpinTo90";
                }
                break;

            case "ToLeftColumn": //move to left column
                doneYet = robot.westCoast.driveBasedOnEncodersAndGyro(driveDistanceToLeftColumn, -1, true, 0, currentAngle);
                if(doneYet){
                    currentCase = "SpinTo90";
                }
                break;

            case "SpinTo90": //turn 90% to face cryptobox
                doneYet = robot.westCoast.turnToGyroHeading(true, 90, currentAngle);
                telemetry.addData("Current Orientation", currentAngle);
                if(doneYet){
                    currentCase = "ReleaseGripper";
                }
                break;
            case "ReleaseGripper": //release glyph
                robot.glyphCollector.topGrasper.open();
                doneYet = true;
                if(doneYet){
                    currentCase = "DriveForward";
                }
                break;
            case "DriveForward": //dive forward twards cryptobox
                doneYet = robot.westCoast.driveBasedOnEncoders(driveForwardToCryptobox, 1, true) || time();//fix and gyro
                telemetry.addData("navx orientation", currentAngle);

                //doneYet = robot.westCoast.driveBasedOnEncoders(driveForwardToCryptobox, 1) || time();
                if(doneYet){
                    currentCase = "DriveBack";
                }
                break;
            //back up
            case "DriveBack":
                doneYet = robot.westCoast.driveBasedOnEncoders(driveAwayFromCryptobox, -1, true);
                if(doneYet){
                    currentCase = "DriveForward2";
                }
                break;
            case "DriveForward2":
                //doneYet = robot.westCoast.driveBasedOnEncodersAndGyro(driveAwayFromCryptobox+2, 1, -175, currentAngle) || time();
                doneYet = robot.westCoast.driveBasedOnEncoders(driveForwardToCryptobox+2, 1, true) || time();
                if(doneYet){
                    currentCase = "DriveBack2";
                }
                break;
            case "DriveBack2":
                doneYet = robot.westCoast.driveBasedOnEncoders(1, -1, true);
                if(doneYet){
                    currentCase = "DriveBack3";
                }
                break;
            case "DriveBack3":
                doneYet = robot.westCoast.driveBasedOnEncoders(4, -1, true);
                if(doneYet){
                    currentCase = "End";
                }
                break;
            case "End":
                robot.westCoast.drive(0,0, true);
                break;
            default:
                robot.westCoast.drive(0,0, true);
                telemetry.addData("What Are You", " Doing");
                telemetry.update();
        }
    }
    @Override
    public void stop(){}








    private String choseColumnCase(String column){
        switch (column){
            case "RIGHT":
                return "ToRightColumn";
            case "CENTER":
                return "ToCenterColumn";
            case "LEFT":
                return "ToLeftColumn";
            default:
                telemetry.addData("Not Left Right Or", " Center");
                return "";
        }
    }
    long startTime = 0;
    boolean firstTime = true;
    private boolean time (){
        if(firstTime){
            firstTime = false;
            startTime = System.currentTimeMillis();
        }
        long duration = (System.currentTimeMillis()-startTime)/1000;
        if(duration > 2) {
            firstTime = true;
            return true;
        }
        else return false;

    }
}
