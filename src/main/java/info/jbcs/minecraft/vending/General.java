package info.jbcs.minecraft.vending;

import net.minecraft.entity.Entity;

public class General {

    public static void propelTowards(Entity what, Entity whereTo, @SuppressWarnings("SameParameterValue") double force) {
        double dx = whereTo.posX - what.posX;
        double dy = whereTo.posY - what.posY;
        double dz = whereTo.posZ - what.posZ;
        double total = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (total == 0) {
            what.motionX = 0;
            what.motionY = 0;
            what.motionZ = 0;
        } else {
            what.motionX = dx / total * force;
            what.motionY = dy / total * force;
            what.motionZ = dz / total * force;
        }
    }

    public static boolean isInRange(double distance, double x1, double y1, double z1, double x2, double y2, double z2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double z = z1 - z2;

        return x * x + y * y + z * z < distance * distance;
    }
}
