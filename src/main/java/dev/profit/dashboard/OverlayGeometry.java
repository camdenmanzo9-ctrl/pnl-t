/*
 * Decompiled with CFR 0.153-local.
 */
package dev.profit.dashboard;

final class OverlayGeometry {
    OverlayGeometry() {
    }

    static float scale(float f) {
        return Float.isFinite(f) ? Math.max(0.25f, Math.min(3.0f, f)) : 1.0f;
    }

    static int raw(double d, float f) {
        return (int)Math.round(d / (double)Math.max(0.01f, f));
    }

    static int clamp(int n, int n2, int n3) {
        return Math.max(0, Math.min(Math.max(0, n3 - n2), n));
    }

    static int snap(double d, boolean bl) {
        return bl ? (int)Math.round(d / 8.0) * 8 : (int)Math.round(d);
    }

    static float resized(Box box, float f, double d, double d2) {
        return OverlayGeometry.scale(f * (float)Math.max((d - (double)box.x) / (double)Math.max(1, box.w), (d2 - (double)box.y) / (double)Math.max(1, box.h)));
    }

    record Box(int x, int y, int w, int h) {
        boolean contains(double d, double d2) {
            return d >= (double)this.x && d < (double)(this.x + this.w) && d2 >= (double)this.y && d2 < (double)(this.y + this.h);
        }

        boolean handle(double d, double d2) {
            return d >= (double)(this.x + this.w - 11) && d < (double)(this.x + this.w + 3) && d2 >= (double)(this.y + this.h - 11) && d2 < (double)(this.y + this.h + 3);
        }
    }
}

