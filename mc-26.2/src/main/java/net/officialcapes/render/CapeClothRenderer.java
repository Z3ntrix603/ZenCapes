package net.officialcapes.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.officialcapes.physics.CapeSimulation;
import org.joml.Vector3f;

public class CapeClothRenderer {
    private static final float HALF_WIDTH = 0.3125f;
    private static final float HALF_THICKNESS = 0.03125f;

    public static void render(
        PoseStack poseStack,
        SubmitNodeCollector collector,
        int light,
        Identifier texture,
        CapeSimulation simulation
    ) {
        if (texture == null || simulation == null) return;

        collector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(texture), (pose, consumer) -> {
            int segments = CapeSimulation.NUM_SEGMENTS;

            for (int i = 0; i < segments - 1; i++) {
                Vector3f p0 = simulation.getPoint(i);
                Vector3f p1 = simulation.getPoint(i + 1);

                float v0 = (1.0f + 16.0f * ((float) i / (segments - 1))) / 32.0f;
                float v1 = (1.0f + 16.0f * ((float) (i + 1) / (segments - 1))) / 32.0f;

                float dy = p1.y - p0.y;
                float dz = p1.z - p0.z;
                float len = (float) Math.sqrt(dy * dy + dz * dz);
                float ny = len > 0.0001f ? dz / len : 0.0f;
                float nz = len > 0.0001f ? -dy / len : 1.0f;

                // 1. Outside Face (facing away from player, +Z)
                float uOut0 = 1.0f / 64.0f;
                float uOut1 = 11.0f / 64.0f;

                addVertex(consumer, pose, p0.x - HALF_WIDTH, p0.y, p0.z + HALF_THICKNESS, uOut0, v0, 0.0f, ny, nz, light);
                addVertex(consumer, pose, p0.x + HALF_WIDTH, p0.y, p0.z + HALF_THICKNESS, uOut1, v0, 0.0f, ny, nz, light);
                addVertex(consumer, pose, p1.x + HALF_WIDTH, p1.y, p1.z + HALF_THICKNESS, uOut1, v1, 0.0f, ny, nz, light);
                addVertex(consumer, pose, p1.x - HALF_WIDTH, p1.y, p1.z + HALF_THICKNESS, uOut0, v1, 0.0f, ny, nz, light);

                // 2. Inside Face (facing towards player, -Z)
                float uIn0 = 12.0f / 64.0f;
                float uIn1 = 22.0f / 64.0f;

                addVertex(consumer, pose, p0.x + HALF_WIDTH, p0.y, p0.z - HALF_THICKNESS, uIn0, v0, 0.0f, -ny, -nz, light);
                addVertex(consumer, pose, p0.x - HALF_WIDTH, p0.y, p0.z - HALF_THICKNESS, uIn1, v0, 0.0f, -ny, -nz, light);
                addVertex(consumer, pose, p1.x - HALF_WIDTH, p1.y, p1.z - HALF_THICKNESS, uIn1, v1, 0.0f, -ny, -nz, light);
                addVertex(consumer, pose, p1.x + HALF_WIDTH, p1.y, p1.z - HALF_THICKNESS, uIn0, v1, 0.0f, -ny, -nz, light);

                // 3. Right Edge (facing -X)
                float uRight0 = 0.0f / 64.0f;
                float uRight1 = 1.0f / 64.0f;

                addVertex(consumer, pose, p0.x - HALF_WIDTH, p0.y, p0.z - HALF_THICKNESS, uRight0, v0, -1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, pose, p0.x - HALF_WIDTH, p0.y, p0.z + HALF_THICKNESS, uRight1, v0, -1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, pose, p1.x - HALF_WIDTH, p1.y, p1.z + HALF_THICKNESS, uRight1, v1, -1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, pose, p1.x - HALF_WIDTH, p1.y, p1.z - HALF_THICKNESS, uRight0, v1, -1.0f, 0.0f, 0.0f, light);

                // 4. Left Edge (facing +X)
                float uLeft0 = 11.0f / 64.0f;
                float uLeft1 = 12.0f / 64.0f;

                addVertex(consumer, pose, p0.x + HALF_WIDTH, p0.y, p0.z + HALF_THICKNESS, uLeft0, v0, 1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, pose, p0.x + HALF_WIDTH, p0.y, p0.z - HALF_THICKNESS, uLeft1, v0, 1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, pose, p1.x + HALF_WIDTH, p1.y, p1.z - HALF_THICKNESS, uLeft1, v1, 1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, pose, p1.x + HALF_WIDTH, p1.y, p1.z + HALF_THICKNESS, uLeft0, v1, 1.0f, 0.0f, 0.0f, light);
            }

            // Top Cap (at point 0)
            Vector3f top = simulation.getPoint(0);
            addVertex(consumer, pose, top.x - HALF_WIDTH, top.y, top.z - HALF_THICKNESS, 1.0f/64.0f, 0.0f, 0.0f, -1.0f, 0.0f, light);
            addVertex(consumer, pose, top.x + HALF_WIDTH, top.y, top.z - HALF_THICKNESS, 11.0f/64.0f, 0.0f, 0.0f, -1.0f, 0.0f, light);
            addVertex(consumer, pose, top.x + HALF_WIDTH, top.y, top.z + HALF_THICKNESS, 11.0f/64.0f, 1.0f/32.0f, 0.0f, -1.0f, 0.0f, light);
            addVertex(consumer, pose, top.x - HALF_WIDTH, top.y, top.z + HALF_THICKNESS, 1.0f/64.0f, 1.0f/32.0f, 0.0f, -1.0f, 0.0f, light);

            // Bottom Cap (at last point)
            Vector3f bot = simulation.getPoint(segments - 1);
            addVertex(consumer, pose, bot.x - HALF_WIDTH, bot.y, bot.z + HALF_THICKNESS, 11.0f/64.0f, 1.0f/32.0f, 0.0f, 1.0f, 0.0f, light);
            addVertex(consumer, pose, bot.x + HALF_WIDTH, bot.y, bot.z + HALF_THICKNESS, 21.0f/64.0f, 1.0f/32.0f, 0.0f, 1.0f, 0.0f, light);
            addVertex(consumer, pose, bot.x + HALF_WIDTH, bot.y, bot.z - HALF_THICKNESS, 21.0f/64.0f, 0.0f, 0.0f, 1.0f, 0.0f, light);
            addVertex(consumer, pose, bot.x - HALF_WIDTH, bot.y, bot.z - HALF_THICKNESS, 11.0f/64.0f, 0.0f, 0.0f, 1.0f, 0.0f, light);
        });
    }

    private static void addVertex(
        VertexConsumer consumer,
        PoseStack.Pose pose,
        float x, float y, float z,
        float u, float v,
        float nx, float ny, float nz,
        int light
    ) {
        consumer.addVertex(pose, x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, nx, ny, nz);
    }
}
