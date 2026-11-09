package net.officialcapes.physics;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import net.officialcapes.config.OfficialCapesConfig;
import org.joml.Vector3f;

public class CapeSimulation {
    public static final int NUM_SEGMENTS = 16;
    private static final float TOTAL_LENGTH = 0.96f;
    private static final float SEGMENT_LENGTH = TOTAL_LENGTH / (NUM_SEGMENTS - 1);

    private final CapePoint[] points = new CapePoint[NUM_SEGMENTS];
    private float prevBodyYaw = 0.0f;
    private int tickCount = 0;

    public CapeSimulation() {
        for (int i = 0; i < NUM_SEGMENTS; i++) {
            points[i] = new CapePoint(0.0f, -i * SEGMENT_LENGTH, 0.0f);
        }
    }

    public void update(AbstractClientPlayer player, OfficialCapesConfig config) {
        tickCount++;

        for (CapePoint p : points) {
            p.updatePrev();
        }

        boolean sneaking = player.isCrouching();
        boolean swimming = player.isSwimming();
        boolean fallFlying = player.isFallFlying();
        boolean hasChestplate = !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();

        float anchorZ = hasChestplate ? 0.15f : 0.125f;
        float anchorY = sneaking ? -0.15f : 0.0f;
        points[0].x = 0.0f;
        points[0].y = anchorY;
        points[0].z = anchorZ;

        Vec3 delta = player.getDeltaMovement();
        double dx = delta.x;
        double dy = delta.y;
        double dz = delta.z;
        float speed = (float) Math.sqrt(dx * dx + dz * dz);

        float bodyYaw = player.yBodyRot;
        float yawDelta = Mth.wrapDegrees(bodyYaw - prevBodyYaw);
        prevBodyYaw = bodyYaw;

        float windFactor = config.windStrength;
        float gravityFactor = config.gravity;
        float waveFactor = config.waveIntensity;

        float gravity = 0.045f * gravityFactor;
        float airResistance = 0.88f;

        float forwardDrag = Math.min(0.55f, speed * 2.8f) * windFactor;

        float walkProgress = player.walkAnimation.position();
        float wavePhase = walkProgress * 0.85f;

        for (int i = 1; i < NUM_SEGMENTS; i++) {
            CapePoint p = points[i];
            float progress = (float) i / (NUM_SEGMENTS - 1);

            p.vy -= gravity;
            float speedLift = forwardDrag * progress;
            p.vz += speedLift * 0.12f;
            p.vx += (yawDelta * 0.015f) * progress;

            if (speed > 0.01f) {
                float wave = (float) Math.sin(wavePhase - i * 0.45f) * (speed * 0.18f) * waveFactor;
                p.vz += wave * progress;
            } else if (config.idleBreeze) {
                float idleWave = (float) Math.sin(tickCount * 0.08f - i * 0.35f) * 0.015f * windFactor;
                p.vz += idleWave;
            }

            if (fallFlying || swimming) {
                p.vy += gravity * 0.9f;
                p.vz += 0.08f;
            }

            p.vx *= airResistance;
            p.vy *= airResistance;
            p.vz *= airResistance;

            p.x += p.vx;
            p.y += p.vy;
            p.z += p.vz;
        }

        for (int iteration = 0; iteration < 4; iteration++) {
            for (int i = 1; i < NUM_SEGMENTS; i++) {
                CapePoint p1 = points[i - 1];
                CapePoint p2 = points[i];

                float diffX = p2.x - p1.x;
                float diffY = p2.y - p1.y;
                float diffZ = p2.z - p1.z;

                float currentDist = (float) Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
                if (currentDist > 0.0001f) {
                    float factor = (currentDist - SEGMENT_LENGTH) / currentDist;

                    p2.x -= diffX * factor * 0.95f;
                    p2.y -= diffY * factor * 0.95f;
                    p2.z -= diffZ * factor * 0.95f;
                }

                float minZ = hasChestplate ? 0.14f : 0.11f;
                if (sneaking) {
                    minZ += (float) i * 0.02f;
                }
                if (p2.z < minZ) {
                    p2.z = minZ;
                    p2.vz = 0.0f;
                }
            }
        }
    }

    public Vector3f getPoint(int index, float tickDelta) {
        if (index < 0 || index >= NUM_SEGMENTS) {
            return new Vector3f();
        }
        return points[index].getLerped(tickDelta);
    }

    public void reset() {
        for (int i = 0; i < NUM_SEGMENTS; i++) {
            points[i] = new CapePoint(0.0f, -i * SEGMENT_LENGTH, 0.125f);
        }
    }
}
