package net.officialcapes.physics;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.MathHelper;
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

    public void update(AbstractClientPlayerEntity player, OfficialCapesConfig config) {
        tickCount++;

        // Store previous positions for interpolation
        for (CapePoint p : points) {
            p.updatePrev();
        }

        boolean sneaking = player.isInSneakingPose();
        boolean swimming = player.isSwimming();
        boolean fallFlying = player.isGliding();
        boolean hasChestplate = !player.getEquippedStack(EquipmentSlot.CHEST).isEmpty();

        // 1. Anchor position (point 0) at shoulders
        float anchorZ = hasChestplate ? 0.15f : 0.125f;
        float anchorY = sneaking ? -0.15f : 0.0f;
        points[0].x = 0.0f;
        points[0].y = anchorY;
        points[0].z = anchorZ;

        // Player movement velocity calculation
        double dx = player.getX() - player.lastRenderX;
        double dy = player.getY() - player.lastRenderY;
        double dz = player.getZ() - player.lastRenderZ;
        float speed = (float) Math.sqrt(dx * dx + dz * dz);

        // Body yaw rotation delta (centrifugal cloth flare)
        float bodyYaw = player.bodyYaw;
        float yawDelta = MathHelper.wrapDegrees(bodyYaw - prevBodyYaw);
        prevBodyYaw = bodyYaw;

        // Physics parameters from config
        float windFactor = config.windStrength;
        float gravityFactor = config.gravity;
        float waveFactor = config.waveIntensity;

        // Base physics forces
        float gravity = 0.045f * gravityFactor;
        float airResistance = 0.88f;

        // Backward horizontal drag from forward motion
        float forwardDrag = Math.min(0.55f, speed * 2.8f) * windFactor;

        // Wave propagation frequency and phase
        float walkProgress = player.limbAnimator.getAnimationProgress();
        float wavePhase = walkProgress * 0.85f;

        // 2. Integration step for segments 1..15
        for (int i = 1; i < NUM_SEGMENTS; i++) {
            CapePoint p = points[i];
            float progress = (float) i / (NUM_SEGMENTS - 1);

            // Gravity
            p.vy -= gravity;

            // Horizontal push backward (Z-axis in local space)
            float speedLift = forwardDrag * progress;
            p.vz += speedLift * 0.12f;

            // Inertia from turning (X-axis centrifugal flare)
            p.vx += (yawDelta * 0.015f) * progress;

            // Dawn / NoRisk traveling wave ripple
            if (speed > 0.01f) {
                float wave = (float) Math.sin(wavePhase - i * 0.45f) * (speed * 0.18f) * waveFactor;
                p.vz += wave * progress;
            } else if (config.idleBreeze) {
                float idleWave = (float) Math.sin(tickCount * 0.08f - i * 0.35f) * 0.015f * windFactor;
                p.vz += idleWave;
            }

            // Swimming / Elytra posture adjustment
            if (fallFlying || swimming) {
                p.vy += gravity * 0.9f;
                p.vz += 0.08f;
            }

            // Apply velocity with damping
            p.vx *= airResistance;
            p.vy *= airResistance;
            p.vz *= airResistance;

            p.x += p.vx;
            p.y += p.vy;
            p.z += p.vz;
        }

        // 3. Distance constraint relaxation (Verlet style)
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

                // 4. Collision back-plane with player body
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
