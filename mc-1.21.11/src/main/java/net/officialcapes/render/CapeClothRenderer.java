package net.officialcapes.render;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.officialcapes.physics.CapeSimulation;
import org.joml.Vector3f;

public class CapeClothRenderer {
    private static final float HALF_WIDTH = 0.3125f; // 10 pixels / 32
    private static final float HALF_THICKNESS = 0.03125f; // 1 pixel / 32

    public static void render(
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        int light,
        Identifier texture,
        CapeSimulation simulation,
        float tickDelta
    ) {
        if (texture == null || simulation == null) return;

        queue.submitCustom(matrices, RenderLayers.entitySolid(texture), (entry, consumer) -> {
            int segments = CapeSimulation.NUM_SEGMENTS;

            for (int i = 0; i < segments - 1; i++) {
                Vector3f p0 = simulation.getPoint(i, tickDelta);
                Vector3f p1 = simulation.getPoint(i + 1, tickDelta);

                float v0 = (1.0f + (16.0f / (segments - 1)) * i) / 32.0f;
                float v1 = (1.0f + (16.0f / (segments - 1)) * (i + 1)) / 32.0f;

                float dy = p1.y - p0.y;
                float dz = p1.z - p0.z;
                float len = (float) Math.sqrt(dy * dy + dz * dz);
                float ny = len > 0.0001f ? dz / len : 0.0f;
                float nz = len > 0.0001f ? -dy / len : 1.0f;

                // 1. Front face
                float uFront0 = 1.0f / 64.0f;
                float uFront1 = 11.0f / 64.0f;

                addVertex(consumer, entry, p0.x - HALF_WIDTH, p0.y, p0.z + HALF_THICKNESS, uFront0, v0, 0.0f, ny, nz, light);
                addVertex(consumer, entry, p0.x + HALF_WIDTH, p0.y, p0.z + HALF_THICKNESS, uFront1, v0, 0.0f, ny, nz, light);
                addVertex(consumer, entry, p1.x + HALF_WIDTH, p1.y, p1.z + HALF_THICKNESS, uFront1, v1, 0.0f, ny, nz, light);
                addVertex(consumer, entry, p1.x - HALF_WIDTH, p1.y, p1.z + HALF_THICKNESS, uFront0, v1, 0.0f, ny, nz, light);

                // 2. Back face
                float uBack0 = 12.0f / 64.0f;
                float uBack1 = 22.0f / 64.0f;

                addVertex(consumer, entry, p0.x + HALF_WIDTH, p0.y, p0.z - HALF_THICKNESS, uBack0, v0, 0.0f, -ny, -nz, light);
                addVertex(consumer, entry, p0.x - HALF_WIDTH, p0.y, p0.z - HALF_THICKNESS, uBack1, v0, 0.0f, -ny, -nz, light);
                addVertex(consumer, entry, p1.x - HALF_WIDTH, p1.y, p1.z - HALF_THICKNESS, uBack1, v1, 0.0f, -ny, -nz, light);
                addVertex(consumer, entry, p1.x + HALF_WIDTH, p1.y, p1.z - HALF_THICKNESS, uBack0, v1, 0.0f, -ny, -nz, light);

                // 3. Right edge
                float uRight0 = 0.0f / 64.0f;
                float uRight1 = 1.0f / 64.0f;

                addVertex(consumer, entry, p0.x - HALF_WIDTH, p0.y, p0.z - HALF_THICKNESS, uRight0, v0, -1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, entry, p0.x - HALF_WIDTH, p0.y, p0.z + HALF_THICKNESS, uRight1, v0, -1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, entry, p1.x - HALF_WIDTH, p1.y, p1.z + HALF_THICKNESS, uRight1, v1, -1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, entry, p1.x - HALF_WIDTH, p1.y, p1.z - HALF_THICKNESS, uRight0, v1, -1.0f, 0.0f, 0.0f, light);

                // 4. Left edge
                float uLeft0 = 11.0f / 64.0f;
                float uLeft1 = 12.0f / 64.0f;

                addVertex(consumer, entry, p0.x + HALF_WIDTH, p0.y, p0.z + HALF_THICKNESS, uLeft0, v0, 1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, entry, p0.x + HALF_WIDTH, p0.y, p0.z - HALF_THICKNESS, uLeft1, v0, 1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, entry, p1.x + HALF_WIDTH, p1.y, p1.z - HALF_THICKNESS, uLeft1, v1, 1.0f, 0.0f, 0.0f, light);
                addVertex(consumer, entry, p1.x + HALF_WIDTH, p1.y, p1.z + HALF_THICKNESS, uLeft0, v1, 1.0f, 0.0f, 0.0f, light);
            }

            // Top cap
            Vector3f top = simulation.getPoint(0, tickDelta);
            addVertex(consumer, entry, top.x - HALF_WIDTH, top.y, top.z - HALF_THICKNESS, 1.0f/64.0f, 0.0f, 0.0f, 1.0f, 0.0f, light);
            addVertex(consumer, entry, top.x + HALF_WIDTH, top.y, top.z - HALF_THICKNESS, 11.0f/64.0f, 0.0f, 0.0f, 1.0f, 0.0f, light);
            addVertex(consumer, entry, top.x + HALF_WIDTH, top.y, top.z + HALF_THICKNESS, 11.0f/64.0f, 1.0f/32.0f, 0.0f, 1.0f, 0.0f, light);
            addVertex(consumer, entry, top.x - HALF_WIDTH, top.y, top.z + HALF_THICKNESS, 1.0f/64.0f, 1.0f/32.0f, 0.0f, 1.0f, 0.0f, light);

            // Bottom cap
            Vector3f bot = simulation.getPoint(segments - 1, tickDelta);
            addVertex(consumer, entry, bot.x - HALF_WIDTH, bot.y, bot.z + HALF_THICKNESS, 11.0f/64.0f, 1.0f/32.0f, 0.0f, -1.0f, 0.0f, light);
            addVertex(consumer, entry, bot.x + HALF_WIDTH, bot.y, bot.z + HALF_THICKNESS, 21.0f/64.0f, 1.0f/32.0f, 0.0f, -1.0f, 0.0f, light);
            addVertex(consumer, entry, bot.x + HALF_WIDTH, bot.y, bot.z - HALF_THICKNESS, 21.0f/64.0f, 0.0f, 0.0f, -1.0f, 0.0f, light);
            addVertex(consumer, entry, bot.x - HALF_WIDTH, bot.y, bot.z - HALF_THICKNESS, 11.0f/64.0f, 0.0f, 0.0f, -1.0f, 0.0f, light);
        });
    }

    private static void addVertex(
        VertexConsumer consumer,
        MatrixStack.Entry entry,
        float x, float y, float z,
        float u, float v,
        float nx, float ny, float nz,
        int light
    ) {
        consumer.vertex(entry, x, y, z)
            .color(255, 255, 255, 255)
            .texture(u, v)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal(entry, nx, ny, nz);
    }
}
