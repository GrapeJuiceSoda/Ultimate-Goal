package org.firstinspires.ftc.teamcode.tele;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.button.Button;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.arcrobotics.ftclib.gamepad.ButtonReader;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.RevIMU;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import org.firstinspires.ftc.teamcode.commands.FieldCentricDriveComand;
import org.firstinspires.ftc.teamcode.commands.SetShootPower;
import org.firstinspires.ftc.teamcode.experimental.NewSetShootPower;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystemFieldCentric;
import org.firstinspires.ftc.teamcode.experimental.NewBevelShooterSubsystem;
import org.firstinspires.ftc.teamcode.experimental.NewMotorSubsystem;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Shooter and Drive Test")
public class NewShooterTest extends CommandOpMode {

    private MotorEx m_frontLeft, m_frontRight, m_bottomLeft, m_bottomRight;
    private RevIMU gyro;
    private GamepadEx driverController;

    private DriveSubsystemFieldCentric driveSubsys;
    private FieldCentricDriveComand driveCommand;

    private NewBevelShooterSubsystem shooter;
    private NewMotorSubsystem m_shooterMotor;
    private NewMotorSubsystem m_reverse_shooterMotor;
    private Button m_shooter_button;

    static double kP = 30.00;
    static double kI = 0.00;
    static double kD = 7.00;
    static double kS = 0.10;
    static double kV = 0.0003;
    static double power = 0.5;


    @Override
    public void initialize() {
        m_frontLeft = new MotorEx(hardwareMap, "frontLeft", Motor.GoBILDA.RPM_435);
        m_frontRight = new MotorEx(hardwareMap, "frontRight", Motor.GoBILDA.RPM_435);
        m_bottomLeft = new MotorEx(hardwareMap, "bottomLeft", Motor.GoBILDA.RPM_435);
        m_bottomRight = new MotorEx(hardwareMap, "bottomRight", Motor.GoBILDA.RPM_435);

        m_shooterMotor = new NewMotorSubsystem(hardwareMap, new MotorEx(hardwareMap, "motor", Motor.GoBILDA.RPM_435), "velo");
        m_reverse_shooterMotor = new NewMotorSubsystem(hardwareMap, new MotorEx(hardwareMap, "inverted", Motor.GoBILDA.RPM_435), "velo");
        shooter = new NewBevelShooterSubsystem(m_shooterMotor, m_reverse_shooterMotor);

        gyro = new RevIMU(hardwareMap, "imu");
        driveSubsys = new DriveSubsystemFieldCentric(
                m_frontLeft,
                m_frontRight,
                m_bottomLeft,
                m_bottomRight,
                gyro
        );
        driveCommand = new FieldCentricDriveComand(
                driveSubsys,
                () -> driverController.getLeftX(),
                () -> driverController.getLeftY(),
                () -> -driverController.getRightX(),
                () -> gyro.getHeading()
        );

        driverController = new GamepadEx(gamepad1);
        GamepadButton buttonA = new GamepadButton(
                driverController, GamepadKeys.Button.A
        );
        gyro.init();
        m_shooterMotor.setVelo(kP, kI, kD);
        m_shooterMotor.setFF(kS, kV);
        m_reverse_shooterMotor.setVelo(kP, kI, kD);
        m_reverse_shooterMotor.setFF(kS, kV);

        buttonA.whenPressed(new NewSetShootPower(shooter, power))
                .whenReleased(new NewSetShootPower(shooter, 0));
        schedule(driveCommand);
        register(driveSubsys, shooter, m_reverse_shooterMotor, m_shooterMotor);
    }
}

