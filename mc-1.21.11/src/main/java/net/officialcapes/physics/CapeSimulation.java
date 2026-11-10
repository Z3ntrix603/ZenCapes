package net.officialcapes.physics;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.officialcapes.config.OfficialCapesConfig;
import org.joml.Vector3f;

public class CapeSimulation {
    public static final int NUM_SEGMENTS = 16;
    private static final float SEGMENT_LENGTH = 0.96f / (NUM_SEGMENTS - 1);

    private final CapePoint[] points = new CapePoint[NUM_SEGMENTS];

    public CapeSimulation() {
        for (int i = 0; i < NUM_SEGMENTS; i++) {
            points[i] = new CapePoint(0.0f, i * SEGMENT_LENGTH, 0.0f);
        }
    }

    public void updateFromState(PlayerEntityRenderState state, OfficialCapesConfig config) {
        float flap = state.field_53536;
        float lean = state.field_53537;
        float roll = state.field_53538;
        boolean isGliding = state.getGlidingProgress() > 0.0f;

        updateAngles(flap, lean, roll, isGliding, config);
    }

    public void updateAngles(float flap, float lean, float roll, boolean isGliding, OfficialCapesConfig config) {
        float speedRatio = Math.min(1.0f, Math.max(0.0f, lean / 80.0f));
        float basePitch = 6.0f + lean * 0.5f * config.windStrength + flap * 0.6f;
        if (isGliding) {
            basePitch = 82.0f;
        }
        basePitch = Math.max(4.0f, Math.min(84.0f, basePitch));

        float time = (float) (System.currentTimeMillis() % 10000000L) * 0.005f;

        points[0].x = 0.0f;
        points[0].y = 0.0f;
        points[0].z = 0.0f;

        for (int i = 0; i < NUM_SEGMENTS - 1; i++) {
            float progress = (float) i / (NUM_SEGMENTS - 1);

            // 1. Dawn / NoRisk traveling wave ripple
            float wave = 0.0f;
            if (speedRatio > 0.02f) {
                float waveMotion = (float) Math.sin(time * 3.5f - i * 0.52f);
                float amp = (0.04f + speedRatio * 0.18f) * config.waveIntensity;
                wave = waveMotion * amp * progress;
            } else if (config.idleBreeze) {
                float idleMotion = (float) Math.sin(time * 1.2f - i * 0.38f);
                wave = idleMotion * 0.03f * config.windStrength * progress;
            }

            // 2. Cloth gravity sag
            float sag = (1.0f - speedRatio) * (0.08f * config.gravity) * (progress * progress);

            // 3. Segment pitch
            float segmentPitchDeg = basePitch - sag * 57.29578f + wave * 57.29578f;
            segmentPitchDeg = Math.max(2.0f, Math.min(86.0f, segmentPitchDeg));
            float pitchRad = (float) Math.toRadians(segmentPitchDeg);

            // 4. Centrifugal roll
            float segmentRollDeg = roll * (0.5f + progress * 0.7f);
            float rollRad = (float) Math.toRadians(segmentRollDeg);

            // 5. Delta vector
            float dx = (float) -Math.sin(rollRad) * SEGMENT_LENGTH;
            float dy = (float) (Math.cos(pitchRad) * Math.cos(rollRad)) * SEGMENT_LENGTH;
            float dz = (float) (Math.sin(pitchRad) * Math.cos(rollRad)) * SEGMENT_LENGTH;

            points[i + 1].x = points[i].x + dx;
            points[i + 1].y = points[i].y + dy;
            points[i + 1].z = points[i].z + dz;
        }
    }

    public Vector3f getPoint(int index) {
        if (index < 0 || index >= NUM_SEGMENTS) {
            return new Vector3f();
        }
        return new Vector3f(points[index].x, points[index].y, points[index].z);
    }
}
