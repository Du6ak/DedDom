package DedDom;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
import java.awt.Color;
import java.awt.geom.*;

/**
* DedDom — a robot by Vladislav Samarin
*/

public class DedDom extends AdvancedRobot {

    public int Way1 = 0;
    public long WayTime = 1;
    public static int MoveWay = 1;
    public static double LastShellSpeed = 15.0;
    public double WallFollow = 120;
    
    public void run() {

        setColors(Color.pink.darker(), Color.pink.darker(), Color.pink.darker());

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            if (getRadarTurnRemaining() == 0.0)
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        WallFollow = 120 + Math.random()*40;
        
        double ModuleBearing = e.getBearingRadians() + getHeadingRadians();
        double Length = e.getDistance();

        double RadarRotation = Utils.normalRelativeAngle(ModuleBearing - getRadarHeadingRadians() );

        double ScanRange = (18.0 + 36.0*Math.random());

        double SecondTurn = Math.min(Math.atan(ScanRange / Length), Math.PI/4.0);
        setTurnRadarRightRadians(RadarRotation + (RadarRotation < 0 ? -SecondTurn : SecondTurn));

        if(--WayTime <= 0) {
            Length = Math.max(Length, 100 + Math.random()*50) * 1.25;
            WayTime = 50 + (long)(Length / LastShellSpeed);

            ++ Way1;
            if(Math.random() < 0.5 || Way1 > 16) {
                MoveWay = -MoveWay;
                Way1 = 0;
            }
        }

        double TargetWay = ModuleBearing-Math.PI/2.0*MoveWay;

        TargetWay += (Math.random()-0.5) * (Math.random()*2.0 + 1.0);

        double x = getX();
        double y = getY();
        double smooth = 0;

        Rectangle2D fieldRectangle = new Rectangle2D.Double(18, 18, getBattleFieldWidth()-36, getBattleFieldHeight()-36);
        while (!fieldRectangle.contains(x+Math.sin(TargetWay)*WallFollow, y+ Math.cos(TargetWay)*WallFollow)) {

            TargetWay += MoveWay*0.1;
            smooth += 0.1;
        }

        if(smooth > 0.5) {
            MoveWay = -MoveWay;
            Way1 = 0;
        }

        double Rotation = Utils.normalRelativeAngle(TargetWay - getHeadingRadians());

        if (Math.abs(Rotation) > Math.PI/2) {
            Rotation = Utils.normalRelativeAngle(Rotation + Math.PI);
            setBack(100);
        } else {
            setAhead(100);
        }

        setTurnRightRadians(Rotation);

        double ShellPower = Math.min(3.0, getEnergy());
        if (Length > 200) {
            ShellPower = Math.min(1.5 / (Length / 200.0), getEnergy());
        }
        else if (Length < 50) {
            ShellPower = Math.min(1.5 / (Length / 50.0), getEnergy());
        }
        else if (Length > 50 && Length < 200) {
            ShellPower = Math.min(1.5 / (Length / 150.0), getEnergy());
        }

        double ShellSpeed = 20 - 3 * ShellPower;

        double EnemySpeed = e.getVelocity()*Math.sin(e.getHeadingRadians() - ModuleBearing);
        double escapeAngle = Math.asin(8.0 / ShellSpeed);

        double enemyDirection = Math.signum(EnemySpeed);
        double angleOffset = escapeAngle * enemyDirection * Math.random();
        setTurnGunRightRadians(Utils.normalRelativeAngle(ModuleBearing + angleOffset - getGunHeadingRadians()));

        if(getEnergy() > ShellPower) {
            setFire(ShellPower);
        }
    }

    public void onBulletHit(BulletHitEvent event) {
		setBodyColor(Color.GREEN);
        setBulletColor(Color.GREEN);
    }

    public void onHitByBullet(HitByBulletEvent e){
        setBodyColor(Color.RED);
        setBulletColor(Color.RED);
        LastShellSpeed = e.getVelocity();   
    }

    public void onWin(WinEvent event){
            turnRight(15);
        while(true) {
            turnLeft(30);
            turnRight(30);
        }   
    }
}