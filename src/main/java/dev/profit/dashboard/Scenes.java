/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 */
package dev.profit.dashboard;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class Scenes {
    /** The only background that moves; the others are drawn as a still picture. */
    public static final int SAKURA = 4;
    public static final String[] NAMES = new String[]{"Midnight Pines", "Winter Pine & Lights", "Sunset Palms", "Dusk", "Sakura", "Maple", "Snowpine", "Savanna", "Moonbloom"};
    private static int clipL = Integer.MIN_VALUE;
    private static int clipR = Integer.MAX_VALUE;

    private Scenes() {
    }

    private static double rnd(int i, int salt) {
        double v = Math.sin((double)i * 12.9898 + (double)salt * 78.233) * 43758.5453;
        return v - Math.floor(v);
    }

    private static int mix(int a, int b, double t) {
        t = Math.max(0.0, Math.min(1.0, t));
        int r = (int)((double)(a >> 16 & 0xFF) + (double)((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * t);
        int g = (int)((double)(a >> 8 & 0xFF) + (double)((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * t);
        int bl = (int)((double)(a & 0xFF) + (double)((b & 0xFF) - (a & 0xFF)) * t);
        return 0xFF000000 | r << 16 | g << 8 | bl;
    }

    private static int alpha(int c, int a) {
        return Math.max(0, Math.min(255, a)) << 24 | c & 0xFFFFFF;
    }

    public static void draw(GuiGraphicsExtractor g, int x, int y, int w, int h, int idx, long now) {
        if (w <= 0 || h <= 0) {
            return;
        }
        if (Math.floorMod(idx, NAMES.length) != SAKURA) {
            now = 0L;   // still picture
        }
        double t = (double)(now % 3600000L) / 1000.0;
        switch (Math.floorMod(idx, NAMES.length)) {
            case 0: {
                Scenes.midnightPines(g, x, y, w, h, t);
                break;
            }
            case 1: {
                Scenes.winterPine(g, x, y, w, h, t);
                break;
            }
            case 2: {
                Scenes.sunsetPalms(g, x, y, w, h, t);
                break;
            }
            case 3: {
                Scenes.dusk(g, x, y, w, h, t);
                break;
            }
            case 4: {
                Scenes.sakura(g, x, y, w, h, t);
                break;
            }
            case 5: {
                Scenes.maple(g, x, y, w, h, t);
                break;
            }
            case 6: {
                Scenes.snowpine(g, x, y, w, h, t);
                break;
            }
            case 7: {
                Scenes.savanna(g, x, y, w, h, t);
                break;
            }
            default: {
                Scenes.moonbloom(g, x, y, w, h, t);
            }
        }
    }

    private static void midnightPines(GuiGraphicsExtractor g, int x, int y, int w, int h, double t) {
        Scenes.sky(g, x, y, w, h, 855572, 855572);
        Scenes.pineRow(g, x, w, y + h, h * 42 / 100, 11, -15460827, -15460827, 7, t);
        Scenes.pineRow(g, x, w, y + h + h / 20, h * 50 / 100, 7, -15724003, -15724003, 8, t);
        for (int i = 0; i < 24; ++i) {
            double sy = (Scenes.rnd(i, 91) * (double)h + t * (3.0 + Scenes.rnd(i, 92) * 4.0)) % (double)h;
            int px = x + (int)(Scenes.rnd(i, 93) * (double)w);
            int py = y + (int)sy;
            g.fill(px, py, px + 1, py + 1, Scenes.alpha(0xFFFFFF, 50 + (int)(Scenes.rnd(i, 94) * 60.0)));
        }
    }

    private static void winterPine(GuiGraphicsExtractor g, int x, int y, int w, int h, double t) {
        Scenes.sky(g, x, y, w, h, 1708608, 3878016);
        Scenes.stars(g, x, y, w, h * 2 / 3, 40, t, 0xFFFFFF);
        Scenes.sleigh(g, x, y, w, h, t);
        int ground = y + h * 82 / 100;
        Scenes.hills(g, x, ground - h / 14, w, y + h, -7435836, h / 30, 0.012, 1);
        Scenes.pineRow(g, x, w, ground, h * 30 / 100, 9, -14016160, -14016160, 2, t);
        Scenes.pineRow(g, x, w, ground + h / 30, h * 38 / 100, 7, -11910776, -11910776, 3, t);
        g.fill(x, ground + h / 12, x + w, y + h, -4672292);
        Scenes.pineRow(g, x, w, y + h, h * 48 / 100, 5, -3159066, -724230, 4, t);
        Scenes.treeLights(g, x, w, y + h, h * 48 / 100, 5, 4, t);
        Scenes.snow(g, x, y, w, h, 90, t, 1);
        Scenes.stringLights(g, x, y, w, t);
    }

    private static void sunsetPalms(GuiGraphicsExtractor g, int x, int y, int w, int h, double t) {
        int ly;
        int horizon = y + h * 62 / 100;
        Scenes.sky(g, x, y, w, horizon - y, 2757712, 15241328);
        int sunR = Math.max(10, h / 9);
        int sunX = x + w / 2;
        Scenes.disc(g, sunX, horizon, sunR + 6, 1090504848);
        Scenes.disc(g, sunX, horizon, sunR, -12150);
        Scenes.sky(g, x, horizon, w, y + h - horizon, 4860506, 1707818);
        for (int i = 0; i < 14 && (ly = horizon + 3 + i * Math.max(2, (y + h - horizon) / 18)) < y + h; ++i) {
            double s = 0.6 + 0.4 * Math.sin(t * 2.2 + (double)i * 1.3);
            int lw = (int)(((double)sunR * 2.2 - (double)(i * sunR) * 0.09) * s);
            if (lw <= 2) continue;
            g.fill(sunX - lw / 2, ly, sunX + lw / 2, ly + 1, Scenes.alpha(16767392, 200 - i * 12));
        }
        g.fill(x, horizon, x + w, horizon + 1, 1627379888);
        Scenes.birds(g, x, y, w, h * 45 / 100, 5, t);
        Scenes.islandWithPalms(g, x + w / 7, horizon + h / 18, w / 5, h, -15069398, 3, t);
        Scenes.islandWithPalms(g, x + w * 6 / 7, horizon + h / 22, w / 6, h, -15069398, 2, t + 1.0);
    }

    private static void dusk(GuiGraphicsExtractor g, int x, int y, int w, int h, double t) {
        Scenes.sky(g, x, y, w, h, 922160, 8011358);
        Scenes.stars(g, x, y, w, h / 2, 60, t, 0xFFFFFF);
        Scenes.disc(g, x + w * 3 / 4, y + h * 55 / 100, Math.max(8, h / 14), -871798);
        Scenes.birds(g, x, y, w, h * 50 / 100, 4, t);
        Scenes.hills(g, x, y + h * 62 / 100, w, y + h, -12967096, h / 12, 0.008, 1);
        Scenes.hills(g, x, y + h * 72 / 100, w, y + h, -14280902, h / 14, 0.011, 2);
        Scenes.hills(g, x, y + h * 84 / 100, w, y + h, -15463390, h / 18, 0.015, 3);
    }

    private static void sakura(GuiGraphicsExtractor g, int x, int y, int w, int h, double t) {
        Scenes.sky(g, x, y, w, h, 2364462, 9062002);
        Scenes.disc(g, x + w / 5, y + h / 4, Math.max(8, h / 16), -468764);
        Scenes.hills(g, x, y + h * 74 / 100, w, y + h, -12969414, h / 16, 0.01, 1);
        g.fill(x, y + h * 88 / 100, x + w, y + h, -14807520);
        for (int i = 0; i < 5; ++i) {
            int tx = x + (int)((double)w * (0.08 + (double)i * 0.22 + Scenes.rnd(i, 3) * 0.06));
            Scenes.blossomTree(g, tx, y + h * 90 / 100, (int)((double)h * (0.32 + Scenes.rnd(i, 4) * 0.12)), -14807520, -1533248, i);
        }
        Scenes.petals(g, x, y, w, h, 60, t, -739630, 0.6);
    }

    private static void maple(GuiGraphicsExtractor g, int x, int y, int w, int h, double t) {
        Scenes.sky(g, x, y, w, h, 2363406, 10111518);
        Scenes.disc(g, x + w * 7 / 10, y + h * 60 / 100, Math.max(10, h / 10), -874918);
        Scenes.hills(g, x, y + h * 70 / 100, w, y + h, -11919852, h / 14, 0.009, 1);
        g.fill(x, y + h * 88 / 100, x + w, y + h, -14676984);
        int[] cols = new int[]{-4705762, -2595798, -7461864};
        for (int i = 0; i < 6; ++i) {
            int tx = x + (int)((double)w * (0.06 + (double)i * 0.18 + Scenes.rnd(i, 7) * 0.05));
            Scenes.roundTree(g, tx, y + h * 90 / 100, (int)((double)h * (0.26 + Scenes.rnd(i, 8) * 0.12)), -14676984, cols[i % 3]);
        }
        Scenes.petals(g, x, y, w, h, 45, t, -2074582, 0.9);
    }

    private static void snowpine(GuiGraphicsExtractor g, int x, int y, int w, int h, double t) {
        Scenes.sky(g, x, y, w, h, 660528, 3824266);
        Scenes.stars(g, x, y, w, h / 2, 50, t, 14543103);
        Scenes.disc(g, x + w * 4 / 5, y + h / 5, Math.max(8, h / 15), -1511169);
        Scenes.hills(g, x, y + h * 70 / 100, w, y + h, -9796432, h / 16, 0.01, 1);
        Scenes.pineRow(g, x, w, y + h * 86 / 100, h * 34 / 100, 8, -15062456, -2562828, 5, t);
        g.fill(x, y + h * 86 / 100, x + w, y + h, -3614996);
        Scenes.pineRow(g, x, w, y + h, h * 44 / 100, 5, -15853008, -1511174, 6, t);
        Scenes.snow(g, x, y, w, h, 110, t, 2);
    }

    private static void savanna(GuiGraphicsExtractor g, int x, int y, int w, int h, double t) {
        int horizon = y + h * 78 / 100;
        Scenes.sky(g, x, y, w, horizon - y, 2756640, 14182954);
        int r = Math.max(14, h / 6);
        Scenes.disc(g, x + w / 2, horizon - r / 3, r + 8, 822067312);
        Scenes.disc(g, x + w / 2, horizon - r / 3, r, -20400);
        Scenes.birds(g, x, y, w, h * 45 / 100, 6, t);
        g.fill(x, horizon, x + w, y + h, -14020088);
        for (int i = 0; i < 4; ++i) {
            int tx = x + (int)((double)w * (0.12 + (double)i * 0.26 + Scenes.rnd(i, 11) * 0.06));
            Scenes.acacia(g, tx, horizon + 1, (int)((double)h * (0.18 + Scenes.rnd(i, 12) * 0.1)), -14020088);
        }
        for (int gx = 0; gx < w; gx += 3) {
            int gh = 2 + (int)(Scenes.rnd(gx, 13) * 4.0);
            int sway = (int)Math.round(Math.sin(t * 1.5 + (double)gx * 0.05));
            g.fill(x + gx + sway, horizon - gh, x + gx + sway + 1, horizon, -14020088);
        }
    }

    private static void moonbloom(GuiGraphicsExtractor g, int x, int y, int w, int h, double t) {
        int i;
        Scenes.sky(g, x, y, w, h, 525852, 2759248);
        Scenes.stars(g, x, y, w, h * 2 / 3, 80, t, 0xFFFFFF);
        int mr = Math.max(14, h / 7);
        int mx = x + w * 3 / 4;
        int my = y + h / 4;
        Scenes.disc(g, mx, my, mr + 10, 415807743);
        Scenes.disc(g, mx, my, mr + 5, 684243199);
        Scenes.disc(g, mx, my, mr, -1186049);
        Scenes.disc(g, mx + mr / 3, my - mr / 4, mr / 4, -2568208);
        Scenes.hills(g, x, y + h * 80 / 100, w, y + h, -15594458, h / 18, 0.012, 1);
        for (i = 0; i < 22; ++i) {
            int fx = x + (int)(Scenes.rnd(i, 21) * (double)w);
            int base = y + h - (int)(Scenes.rnd(i, 22) * (double)h / 14.0);
            int fh = 8 + (int)(Scenes.rnd(i, 23) * (double)h / 10.0);
            int sway = (int)Math.round(Math.sin(t * 1.2 + (double)i) * 1.5);
            g.fill(fx, base - fh, fx + 1, base, -15594458);
            double pulse = 0.6 + 0.4 * Math.sin(t * 2.0 + (double)i * 0.7);
            int bc = i % 3 == 0 ? 13148415 : (i % 3 == 1 ? 15769824 : 10148095);
            Scenes.disc(g, fx + sway, base - fh, 3, Scenes.alpha(bc, (int)(90.0 * pulse)));
            g.fill(fx + sway - 1, base - fh - 1, fx + sway + 2, base - fh + 2, Scenes.alpha(bc, 230));
        }
        for (i = 0; i < 26; ++i) {
            double px = (double)x + (Scenes.rnd(i, 31) * (double)w + Math.sin(t * 0.4 + (double)i) * 30.0) % (double)w;
            double py = (double)y + (double)h * 0.5 + Scenes.rnd(i, 32) * (double)h * 0.45 + Math.cos(t * 0.6 + (double)i * 1.7) * 10.0;
            double blink = 0.5 + 0.5 * Math.sin(t * 3.0 + (double)i * 2.1);
            int ix = (int)px;
            int iy = (int)py;
            g.fill(ix - 1, iy - 1, ix + 2, iy + 2, Scenes.alpha(15269770, (int)(60.0 * blink)));
            g.fill(ix, iy, ix + 1, iy + 1, Scenes.alpha(16056256, (int)(255.0 * blink)));
        }
    }

    private static void sky(GuiGraphicsExtractor g, int x, int y, int w, int h, int top, int bottom) {
        for (int i = 0; i < h; i += 3) {
            g.fill(x, y + i, x + w, y + Math.min(h, i + 3), Scenes.mix(top, bottom, (double)i / (double)Math.max(1, h)));
        }
    }

    private static void disc(GuiGraphicsExtractor g, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; ++dy) {
            int half = (int)Math.round(Math.sqrt(Math.max(0, r * r - dy * dy)));
            g.fill(cx - half, cy + dy, cx + half + 1, cy + dy + 1, color);
        }
    }

    private static void stars(GuiGraphicsExtractor g, int x, int y, int w, int h, int n, double t, int color) {
        for (int i = 0; i < n; ++i) {
            int sx = x + (int)(Scenes.rnd(i, 1) * (double)w);
            int sy = y + (int)(Scenes.rnd(i, 2) * (double)h);
            double tw = 0.5 + 0.5 * Math.sin(t * (1.0 + Scenes.rnd(i, 5) * 2.0) + (double)i);
            g.fill(sx, sy, sx + 1, sy + 1, Scenes.alpha(color, (int)(60.0 + 160.0 * tw)));
        }
    }

    private static void hills(GuiGraphicsExtractor g, int x, int base, int w, int bottom, int color, int amp, double freq, int seed) {
        for (int px = 0; px < w; px += 2) {
            double v = Math.sin((double)px * freq + (double)seed * 1.7) * 0.6 + Math.sin((double)px * freq * 2.3 + (double)seed) * 0.4;
            int top = base - (int)(v * (double)amp);
            g.fill(x + px, top, x + px + 2, bottom, color);
        }
    }

    private static void pineRow(GuiGraphicsExtractor g, int x, int w, int baseY, int height, int count, int color, int snow, int seed, double t) {
        for (int i = 0; i < count; ++i) {
            double jitter = (Scenes.rnd(i, seed) - 0.5) * 0.5;
            int cx = x + (int)((double)w * (((double)i + 0.5 + jitter) / (double)count));
            int th = (int)((double)height * (0.75 + Scenes.rnd(i, seed + 1) * 0.25));
            Scenes.pine(g, cx, baseY, th, color, snow);
        }
    }

    private static void pine(GuiGraphicsExtractor g, int cx, int baseY, int th, int color, int snow) {
        int top = baseY - th;
        int trunkH = Math.max(2, th / 10);
        g.fill(cx - Math.max(1, th / 30), baseY - trunkH, cx + Math.max(1, th / 30) + 1, baseY, color);
        int bodyH = th - trunkH;
        for (int tier = 0; tier < 3; ++tier) {
            int ty = top + tier * bodyH / 4;
            int tH = bodyH / 2;
            int maxHalf = (int)((double)th * (0.16 + (double)tier * 0.06));
            for (int r = 0; r < tH; r += 2) {
                int half = Math.max(1, maxHalf * r / Math.max(1, tH));
                int yy = ty + r;
                if (snow == color) {
                    g.fill(cx - half, yy, cx + half + 1, yy + 2, color);
                    continue;
                }
                g.fill(cx - half, yy, cx + half + 1, yy + 2, color);
                g.fill(cx - half, yy, cx + half / 3, yy + 2, snow);
            }
        }
    }

    private static void treeLights(GuiGraphicsExtractor g, int x, int w, int baseY, int height, int count, int seed, double t) {
        int[] cols = new int[]{-42386, -11870592, -10777345, -11686};
        for (int i = 0; i < count; ++i) {
            double jitter = (Scenes.rnd(i, seed) - 0.5) * 0.5;
            int cx = x + (int)((double)w * (((double)i + 0.5 + jitter) / (double)count));
            int th = (int)((double)height * (0.75 + Scenes.rnd(i, seed + 1) * 0.25));
            for (int k = 0; k < 8; ++k) {
                double fy = 0.2 + (double)k * 0.09;
                int by = baseY - th + (int)((double)th * fy);
                int half = (int)((double)th * 0.2 * fy);
                int bx = cx + (int)((Scenes.rnd(i * 8 + k, 9) - 0.5) * 2.0 * (double)half);
                boolean on = Math.sin(t * 2.0 + (double)(i * 3) + (double)k) > -0.3;
                int c = cols[(i + k) % cols.length];
                if (on) {
                    g.fill(bx - 2, by - 2, bx + 3, by + 3, Scenes.alpha(c, 60));
                }
                g.fill(bx - 1, by - 1, bx + 2, by + 2, on ? c : Scenes.alpha(c, 120));
            }
        }
    }

    public static void stringLights(GuiGraphicsExtractor g, int x, int y, int w, double t) {
        int[] cols = new int[]{-42386, -11870592, -10777345, -11686, -4158721};
        int span = 64;
        for (int px = 0; px < w; px += 2) {
            double f = (double)(px % span) / (double)span;
            int sag = (int)(Math.sin(f * Math.PI) * 3.0);
            g.fill(x + px, y + 1 + sag, x + px + 2, y + 2 + sag, -14015424);
        }
        int i = 0;
        while (i * 16 < w) {
            int bx = x + i * 16 + 8;
            double f = (double)((i * 16 + 8) % span) / (double)span;
            int by = y + 2 + (int)(Math.sin(f * Math.PI) * 3.0);
            double tw = 0.55 + 0.45 * Math.sin(t * 2.5 + (double)i * 1.9);
            int c = cols[i % cols.length];
            g.fill(bx - 3, by - 1, bx + 4, by + 6, Scenes.alpha(c, (int)(55.0 * tw)));
            g.fill(bx - 1, by, bx + 2, by + 4, Scenes.alpha(c, (int)(140.0 + 115.0 * tw)));
            g.fill(bx - 1, by - 1, bx + 2, by, -12962736);
            ++i;
        }
    }

    private static void snow(GuiGraphicsExtractor g, int x, int y, int w, int h, int n, double t, int seed) {
        for (int i = 0; i < n; ++i) {
            double speed = 8.0 + Scenes.rnd(i, seed + 40) * 14.0;
            double sx = Scenes.rnd(i, seed + 41) * (double)w + Math.sin(t * 0.8 + (double)i) * 8.0;
            double sy = (Scenes.rnd(i, seed + 42) * (double)h + t * speed) % (double)h;
            int sz = Scenes.rnd(i, seed + 43) > 0.7 ? 2 : 1;
            int px = x + (int)((sx % (double)w + (double)w) % (double)w);
            int py = y + (int)sy;
            g.fill(px, py, px + sz, py + sz, Scenes.alpha(0xFFFFFF, 140 + (int)(Scenes.rnd(i, seed + 44) * 110.0)));
        }
    }

    private static void petals(GuiGraphicsExtractor g, int x, int y, int w, int h, int n, double t, int color, double drift) {
        for (int i = 0; i < n; ++i) {
            double speed = 10.0 + Scenes.rnd(i, 51) * 12.0;
            double sy = (Scenes.rnd(i, 52) * (double)h + t * speed) % (double)h;
            double sx = Scenes.rnd(i, 53) * (double)w + t * speed * drift + Math.sin(t + (double)i) * 10.0;
            int px = x + (int)((sx % (double)w + (double)w) % (double)w);
            int py = y + (int)sy;
            boolean flat = Math.sin(t * 3.0 + (double)i) > 0.0;
            int c = i % 3 == 0 ? Scenes.mix(color, -1, 0.25) : color;
            g.fill(px, py, px + (flat ? 3 : 1), py + (flat ? 1 : 2), Scenes.alpha(c, 220));
        }
    }

    private static void birds(GuiGraphicsExtractor g, int x, int y, int w, int h, int n, double t) {
        for (int i = 0; i < n; ++i) {
            double speed = 12.0 + Scenes.rnd(i, 61) * 10.0;
            int bx = x + (int)((Scenes.rnd(i, 62) * (double)w + t * speed) % (double)(w + 40)) - 20;
            int by = y + (int)((double)h * (0.25 + Scenes.rnd(i, 63) * 0.6) + Math.sin(t * 1.5 + (double)i) * 3.0);
            boolean up = Math.sin(t * 8.0 + (double)(i * 2)) > 0.0;
            int c = -535557600;
            g.fill(bx - 3, by + (up ? -1 : 1), bx - 1, by + (up ? 0 : 2), c);
            g.fill(bx - 1, by, bx + 1, by + 1, c);
            g.fill(bx + 1, by + (up ? -1 : 1), bx + 3, by + (up ? 0 : 2), c);
        }
    }

    private static void sleigh(GuiGraphicsExtractor g, int x, int y, int w, int h, double t) {
        Scenes.sleighAcross(g, x, y + h / 8, w, t, -15462870, 40.0);
    }

    private static void cfill(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int c) {
        x0 = Math.max(x0, clipL);
        if ((x1 = Math.min(x1, clipR)) > x0) {
            g.fill(x0, y0, x1, y1, c);
        }
    }

    public static void sleighAcross(GuiGraphicsExtractor g, int x, int y, int w, double t, int c, double cycle) {
        double p = t % cycle / 14.0;
        if (p > 1.0) {
            return;
        }
        clipL = x;
        clipR = x + w;
        int sx = x + w + 60 - (int)(p * (double)(w + 140));
        int sy = y + (int)(Math.sin(p * Math.PI * 2.0) * 6.0);
        Scenes.cfill(g, sx + 22, sy - 4, sx + 38, sy + 3, c);
        Scenes.cfill(g, sx + 20, sy - 1, sx + 23, sy + 3, c);
        Scenes.cfill(g, sx + 34, sy - 8, sx + 38, sy - 4, c);
        Scenes.cfill(g, sx + 20, sy + 5, sx + 40, sy + 6, c);
        Scenes.cfill(g, sx + 27, sy - 9, sx + 32, sy - 4, c);
        Scenes.cfill(g, sx + 28, sy - 12, sx + 31, sy - 9, c);
        for (int k = 0; k < 3; ++k) {
            int rx = sx - k * 16;
            int ry = sy - 2 + (int)(Math.sin(t * 6.0 + (double)k) * 1.5);
            Scenes.cfill(g, rx + 2, ry - 3, rx + 12, ry + 1, c);
            Scenes.cfill(g, rx, ry - 7, rx + 3, ry - 2, c);
            Scenes.cfill(g, rx - 1, ry - 10, rx, ry - 7, c);
            Scenes.cfill(g, rx + 2, ry - 10, rx + 3, ry - 7, c);
            boolean step = Math.sin(t * 10.0 + (double)k) > 0.0;
            Scenes.cfill(g, rx + 3, ry + 1, rx + 4, ry + (step ? 4 : 3), c);
            Scenes.cfill(g, rx + 10, ry + 1, rx + 11, ry + (step ? 3 : 4), c);
            Scenes.cfill(g, rx + 12, ry - 2, rx + 20, ry - 1, Scenes.alpha(c, 160));
        }
        clipL = Integer.MIN_VALUE;
        clipR = Integer.MAX_VALUE;
    }

    private static void islandWithPalms(GuiGraphicsExtractor g, int cx, int baseY, int iw, int h, int color, int palms, double t) {
        int half;
        for (int dy = 0; dy < 10 && (half = iw / 2 - dy * dy * iw / 260) > 0; ++dy) {
            g.fill(cx - half, baseY - dy, cx + half, baseY - dy + 1, color);
        }
        g.fill(cx - iw / 2, baseY, cx + iw / 2, baseY + 2, color);
        for (int i = 0; i < palms; ++i) {
            int px = cx + (int)(((double)i - (double)(palms - 1) / 2.0) * (double)iw / ((double)palms + 0.5));
            Scenes.palm(g, px, baseY - 6, (int)((double)h * (0.22 + Scenes.rnd(i, 71) * 0.1)), color, i % 2 == 0 ? 1 : -1, t + (double)i);
        }
    }

    private static void palm(GuiGraphicsExtractor g, int bx, int baseY, int ph, int color, int lean, double t) {
        int topX = bx;
        int topY = baseY - ph;
        for (int k = 0; k <= ph; k += 2) {
            double f = (double)k / (double)ph;
            int tx = bx + (int)((double)lean * Math.pow(f, 2.0) * (double)ph * 0.25);
            int thick = f < 0.5 ? 2 : 1;
            g.fill(tx - thick, baseY - k, tx + thick, baseY - k + 2, color);
            topX = tx;
            topY = baseY - k;
        }
        double sway = Math.sin(t * 1.3) * 0.08;
        for (int fr = 0; fr < 7; ++fr) {
            double ang = -1.5707963267948966 + (double)(fr - 3) * 0.55 + sway;
            int len = (int)((double)ph * 0.42);
            for (int s = 0; s < len; s += 2) {
                double f = (double)s / (double)len;
                int fx = topX + (int)(Math.cos(ang) * (double)s);
                int fy = topY + (int)(Math.sin(ang) * (double)s + f * f * (double)len * 0.7);
                g.fill(fx - 1, fy, fx + 2, fy + 2, color);
            }
        }
    }

    private static void blossomTree(GuiGraphicsExtractor g, int cx, int baseY, int th, int trunk, int bloom, int seed) {
        g.fill(cx - 2, baseY - th / 2, cx + 2, baseY, trunk);
        g.fill(cx - th / 6, baseY - th * 5 / 8, cx - th / 6 + 2, baseY - th / 2, trunk);
        for (int b = 0; b < 7; ++b) {
            int bx = cx + (int)((Scenes.rnd(seed * 7 + b, 81) - 0.5) * (double)th * 0.7);
            int by = baseY - th / 2 - (int)(Scenes.rnd(seed * 7 + b, 82) * (double)th * 0.45);
            Scenes.disc(g, bx, by, Math.max(4, th / 7), Scenes.alpha(bloom, 200));
        }
    }

    private static void roundTree(GuiGraphicsExtractor g, int cx, int baseY, int th, int trunk, int crown) {
        g.fill(cx - 2, baseY - th / 2, cx + 2, baseY, trunk);
        Scenes.disc(g, cx, baseY - th * 2 / 3, Math.max(5, th / 3), crown);
        Scenes.disc(g, cx - th / 5, baseY - th / 2, Math.max(4, th / 4), Scenes.mix(crown, -16777216, 0.15));
        Scenes.disc(g, cx + th / 5, baseY - th / 2, Math.max(4, th / 4), Scenes.mix(crown, -16777216, 0.25));
    }

    private static void acacia(GuiGraphicsExtractor g, int cx, int baseY, int th, int color) {
        for (int k = 0; k < th; k += 2) {
            int off = (int)(k > th / 2 ? (double)(k - th / 2) * 0.5 : 0.0);
            g.fill(cx - off - 1, baseY - k - 2, cx - off + 1, baseY - k, color);
            g.fill(cx + off - 1, baseY - k - 2, cx + off + 1, baseY - k, color);
        }
        int cy = baseY - th;
        int half = th;
        for (int dy = -th / 6; dy <= th / 10; ++dy) {
            int w = (int)((double)half * Math.sqrt(Math.max(0.0, 1.0 - Math.pow((double)dy / ((double)th / 6.0), 2.0))));
            g.fill(cx - w, cy + dy, cx + w, cy + dy + 1, color);
        }
    }
}

