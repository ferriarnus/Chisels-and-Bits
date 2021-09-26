package mod.chiselsandbits.api.util;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

/**
 * Utility class for dealing with raytracing.
 */
public class RayTracingUtils
{

    private RayTracingUtils()
    {
        throw new IllegalStateException("Can not instantiate an instance of: RayTracingUtils. This is a utility class");
    }

    /**
     * Performs a raytrace result within the given reach distance of the given player.
     *
     * @param playerEntity The player to run a raytrace for.
     * @return The raytrace result.
     */
    public static HitResult rayTracePlayer(final Player playerEntity) {
        final AttributeInstance reachAttribute = playerEntity.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (reachAttribute == null)
        {
            return playerEntity.pick(5d, 0.5f, true);
        }

        final double reachAttributeValue = reachAttribute.getValue();
        final double reachDistance = playerEntity.isCreative() ? reachAttributeValue : reachAttributeValue - 0.5D;

        return playerEntity.pick(reachDistance, 0.5f, true);
    }

    public static Vec3i getFullFacingVector(final Player playerEntity) {
        final Vec3 facingVector = playerEntity.getLookAngle();

        return new Vec3i(
          facingVector.x() < 0 ? -1 : 1,
          facingVector.y() < 0 ? -1 : 1,
          facingVector.z() < 0 ? -1 : 1
        );
    }
}
