package net.officialcapes.physics;

import org.joml.Vector3f;

public class CapePoint {
    public float x, y, z;
    public float prevX, prevY, prevZ;
    public float vx, vy, vz;

    public CapePoint(float x, float y, float z) {
        this.x = this.prevX = x;
        this.y = this.prevY = y;
        this.z = this.prevZ = z;
        this.vx = this.vy = this.vz = 0.0f;
    }

    public void updatePrev() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
    }

    public Vector3f getLerped(float delta) {
        return new Vector3f(
            prevX + (x - prevX) * delta,
            prevY + (y - prevY) * delta,
            prevZ + (z - prevZ) * delta
        );
    }
}
