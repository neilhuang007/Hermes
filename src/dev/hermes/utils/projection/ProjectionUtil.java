package dev.hermes.utils.projection;

import dev.hermes.event.events.impl.render.EventRender2D;
import dev.hermes.utils.TimerUtil;
import dev.hermes.utils.vector.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector4d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjectionUtil {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static ConcurrentMap<Entity, Vector4d> finalisedProjections = new ConcurrentHashMap<>();
    public static final ConcurrentMap<Entity, Vector4d> concurrentProjections = new ConcurrentHashMap<>();


    public static boolean progress;
    public static TimerUtil stopWatch = new TimerUtil();

    public static ScaledResolution scaledResolution = new ScaledResolution(mc);
    static Executor tp = Executors.newFixedThreadPool(4);

    public static void Render2D(EventRender2D event) {
        if (mc.theWorld == null) return;

        tp.execute(() -> {
            if (progress && !stopWatch.finished(1000)) {
                return;
            }

            TimerUtil.resetTimer();

            progress = true;

            final double renderX = mc.getRenderManager().renderPosX;
            final double renderY = mc.getRenderManager().renderPosY;
            final double renderZ = mc.getRenderManager().renderPosZ;
            final int factor = scaledResolution.getScaleFactor();
            final float partialTicks = event.getPartialTicks();

            List<Entity> Entitylist = new ArrayList<>(mc.theWorld.loadedEntityList);
            List<TileEntity> TileEntitylist = new ArrayList<>(mc.theWorld.loadedTileEntityList);

            for (Entity entity : Entitylist) {
                try {
                    final Vector4d position = projectEntity(entity, renderX, renderY, renderZ, partialTicks, factor);
                    if (position != null) {
                        concurrentProjections.put(entity, position);
                    }
                } catch (Exception ignored) {
                    concurrentProjections.remove(entity);
                }
            }

            finalisedProjections = new ConcurrentHashMap<>(concurrentProjections);

            progress = false;
        });
    }

    private static Vector4d projectEntity(Entity entity, double renderX, double renderY, double renderZ, float partialTicks, int factor) {
        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderX;
        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) - renderY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderZ;
        final double width = (entity.width + 0.2) / 2;
        final double height = entity.height + (entity.isSneaking() ? -0.3D : 0.2D) + 0.05;
        final AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
        return projectAABB(aabb, factor);
    }



    private static Vector4d projectAABB(AxisAlignedBB aabb, int factor) {
        final List<Vector3d> vectors = Arrays.asList(
                new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
                new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
                new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
                new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));

        Vector4d position = null;
        for (Vector3d vector : vectors) {
            Vector3d projectedVector = project(factor, vector.getX(), vector.getY(), vector.getZ());
            if (projectedVector != null && projectedVector.getZ() >= 0.0D && projectedVector.getZ() < 1.0D) {
                if (position == null) {
                    position = new Vector4d(projectedVector.getX(), projectedVector.getY(), projectedVector.getZ(), 0.0D);
                }

                position = new Vector4d(
                        Math.min(projectedVector.getX(), position.x), Math.min(projectedVector.getY(), position.y),
                        Math.max(projectedVector.getX(), position.z), Math.max(projectedVector.getY(), position.w));
            }
        }
        return position;
    }

    private static Vector3d project(final int factor, final double x, final double y, final double z) {
        if (GLU.gluProject((float) x, (float) y, (float) z, ActiveRenderInfo.MODELVIEW, ActiveRenderInfo.PROJECTION, ActiveRenderInfo.VIEWPORT, ActiveRenderInfo.OBJECTCOORDS)) {
            return new Vector3d((ActiveRenderInfo.OBJECTCOORDS.get(0) / factor), ((Display.getHeight() - ActiveRenderInfo.OBJECTCOORDS.get(1)) / factor), ActiveRenderInfo.OBJECTCOORDS.get(2));
        }
        return null;
    }

    public static Vector4d get(Entity entity) {
        if (entity == null) return null;
        return finalisedProjections.get(entity);
    }

}
