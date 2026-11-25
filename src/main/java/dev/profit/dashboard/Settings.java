/*
 * Decompiled with CFR 0.153-local.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.fabricmc.loader.api.FabricLoader
 */
package dev.profit.dashboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.profit.dashboard.Auction;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;

public final class Settings {
    public int settingsThemeAccent = 8313175;
    public boolean highSmoothness = true;
    public boolean slowSlideDown = false;
    public int accent = 9133302;
    public int scene = 0;
    public int scenicScene = 4;
    public boolean useScenicBackgrounds = true;
    public boolean auctionHud = true;
    public boolean skinFaces = true;
    public String displayName = "";
    public boolean chatColors = true;
    public boolean boldNames = false;
    public boolean boldAmounts = false;
    public int nameColor = 9356799;
    public int receivedColor = 5088255;
    public int sentColor = 0xFF4040;
    public boolean hudShow = true;
    public boolean hudBorder = true;
    public boolean hudAccentBar = true;
    public boolean hudShowPaused = true;
    public float hudSize = 1.0f;
    public int hudOpacity = 75;
    public int hudCorner = 3;
    public int hudTitle = 15328981;
    public int hudGain = 0x7FFF22;
    public int hudLoss = 0xFF4040;
    public int hudZero = 0xFFFFFF;
    public int hudBg = 4997931;
    public int hudAccentColor = 16761655;
    public int dailyHudStyleVersion = 0;
    public boolean popupShow = true;
    public boolean popupRainbow = false;
    public boolean popupGlow = true;
    public boolean popupSparkles = true;
    public boolean popupShine = true;
    public long popupMin = 1000000000L;
    public float popupDuration = 2.0f;
    public float popupSize = 1.0f;
    public int popupAmount = 5088255;
    public int popupName = 9356799;
    public int popupBorder = 5088255;
    public int popupBg = 726056;
    public boolean soundPopup = true;
    public boolean soundWhenQueued = true;
    public boolean soundConfirm = true;
    public int soundIndex = 0;
    public int confirmSoundIndex = 3;
    public int soundVolume = 60;
    public int soundRepeat = 1;
    public float soundPitch = 1.0f;
    public boolean payAsk = true;
    public boolean payShowTax = true;
    public boolean payRainbow = false;
    public long payFrom = 1000000000L;
    public float payDelay = 0.5f;
    public float payTax = 2.0f;
    public int payDim = 55;
    public int payAccent = 5088255;
    public int payName = 9356799;
    public int payAmount = 0xFF5555;
    public int payConfirm = 3111935;
    public int payCancel = 15158332;
    public int payBg = 791588;
    public boolean boardShow = false;
    public boolean settingsAnimatedBackground = true;
    public boolean settingsRainbowHeader = true;
    public boolean dashboardFullscreen = false;
    public boolean settingsBlackBackdrop = false;
    public boolean settingsColoredTabs = true;
    public int boardPos = 4;
    public int boardX = 8;
    public int boardY = 32;
    public float boardSize = 1.0f;
    public int boardOpacity = 60;
    public String boardIgn = "2amsnowy";
    public String boardMax = "2 BILLION MAX";
    public List<BoardTier> boardTiers = Settings.defaultTiers();
    public boolean bgGradient = true;
    public int bgTop = 0;
    public int bgBottom = 0;
    public int bgOpacity = 95;
    public int bgBrightness = 0;
    public int dimGame = 55;
    public boolean countTax = false;
    public boolean colorTax = true;
    public int taxColor = 0x9E9E9E;
    public float taxPct = 2.0f;
    public boolean filterOn = false;
    public boolean filterReceived = true;
    public boolean filterSent = true;
    public long filterUnder = 1000000L;
    public boolean streakShow = true;
    public boolean streakSeparate = true;
    public boolean streakCommands = true;
    public boolean streakAccent = false;
    public boolean streakBorder = true;
    public float streakSize = 0.9f;
    public int streakOpacity = 60;
    public int wins = 0;
    public int losses = 0;
    public int streak = 0;
    public int streakX = 8;
    public int streakY = 60;
    public int popupAnim = 0;
    public int popupOpacity = 92;
    public int popupBorderW = 1;
    public int popupGlowPct = 100;
    public int popupSparkleCount = 8;
    public boolean popupPlus = true;
    public boolean popupCountUp = true;
    public boolean popupFace = true;
    public boolean popupFaceBounce = true;
    public boolean popupTimerBar = true;
    public int popupTimerColor = 16766720;
    public int hudOffX = 0;
    public int hudOffY = 0;
    public int auctionOffX = 0;
    public int auctionOffY = 0;
    public float auctionHudSize = 1.0f;
    public int popupOffX = 0;
    public int popupOffY = 0;
    public boolean overlaySnap = false;
    public static final long[] SMALL_STEPS = new long[]{1L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 50000000L, 100000000L, 1000000000L};
    public int rakePct = 10;
    public long rakeMin = 1000000000L;
    public static Settings S = new Settings();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("bettors-settings.json");
    public static final long[] MONEY_STEPS = new long[]{1000000L, 10000000L, 50000000L, 100000000L, 250000000L, 500000000L, 1000000000L, 5000000000L, 10000000000L, 50000000000L, 100000000000L};
    public static final int[] PRESETS;
    public static final int[] BG_PRESETS;
    private static boolean colorEditing;
    private static boolean colorDirty;

    static {
        int[] nArray = new int[61];
        nArray[0] = 4906624;
        nArray[1] = 0xFF8800;
        nArray[2] = 5088255;
        nArray[3] = 0xFF6600;
        nArray[4] = 3111935;
        nArray[5] = 16753920;
        nArray[6] = 9356799;
        nArray[7] = 0xFFAA00;
        nArray[8] = 3858431;
        nArray[9] = 8191802;
        nArray[10] = 3066993;
        nArray[11] = 11894015;
        nArray[12] = 16735400;
        nArray[13] = 0xFF4040;
        nArray[14] = 15158332;
        nArray[15] = 0xFF5555;
        nArray[16] = 16753920;
        nArray[17] = 16761856;
        nArray[18] = 16766720;
        nArray[19] = 0xFFFFFF;
        nArray[20] = 0xAAAAAA;
        nArray[21] = 16758465;
        nArray[22] = 16738740;
        nArray[23] = 16716947;
        nArray[24] = 0xFF00FF;
        nArray[25] = 14315734;
        nArray[26] = 14202110;
        nArray[27] = 12616956;
        nArray[28] = 11032055;
        nArray[29] = 8141549;
        nArray[30] = 6514417;
        nArray[31] = 8490232;
        nArray[32] = 9684477;
        nArray[33] = 6333946;
        nArray[34] = 2450411;
        nArray[35] = 49151;
        nArray[36] = 65535;
        nArray[37] = 6220500;
        nArray[38] = 1357990;
        nArray[39] = 65407;
        nArray[40] = 8843180;
        nArray[41] = 2278750;
        nArray[42] = 10741301;
        nArray[43] = 0xD9F99D;
        nArray[44] = 16775885;
        nArray[45] = 16639626;
        nArray[46] = 16766720;
        nArray[47] = 16628340;
        nArray[48] = 16347926;
        nArray[49] = 16744272;
        nArray[50] = 16752762;
        nArray[51] = 16478597;
        nArray[52] = 14423100;
        nArray[53] = 14870768;
        nArray[54] = 9741240;
        nArray[55] = 6583435;
        nArray[56] = 13808780;
        nArray[57] = 12887172;
        nArray[58] = 9127187;
        nArray[59] = 0x404040;
        PRESETS = nArray;
        int[] nArray2 = new int[32];
        nArray2[0] = 725536;
        nArray2[1] = 0xFF8800;
        nArray2[2] = 726056;
        nArray2[3] = 0xCC5500;
        nArray2[4] = 791588;
        nArray2[5] = 8138002;
        nArray2[6] = 1054752;
        nArray2[7] = 723732;
        nArray2[8] = 0x1A1A1A;
        nArray2[10] = 791588;
        nArray2[11] = 988970;
        nArray2[12] = 1516884;
        nArray2[13] = 1973067;
        nArray2[14] = 2363962;
        nArray2[15] = 3018853;
        nArray2[16] = 3868516;
        nArray2[17] = 2822182;
        nArray2[18] = 4001828;
        nArray2[19] = 3151133;
        nArray2[20] = 3806485;
        nArray2[21] = 2956050;
        nArray2[22] = 3155216;
        nArray2[23] = 2371604;
        nArray2[24] = 1059356;
        nArray2[25] = 339478;
        nArray2[26] = 731944;
        nArray2[27] = 537412;
        nArray2[28] = 1059898;
        nArray2[29] = 1976635;
        nArray2[30] = 2696484;
        nArray2[31] = 0x303030;
        BG_PRESETS = nArray2;
    }

    public static List<BoardTier> defaultTiers() {
        ArrayList<BoardTier> arrayList = new ArrayList<BoardTier>();
        arrayList.add(new BoardTier("150M", new BoardGame("cards", "Blackjack Multihand", "Per Hand")));
        arrayList.add(new BoardTier("100M", new BoardGame("cards", "Blackjack", "Tie = DD"), new BoardGame("roulette", "Russian Roulette", ""), new BoardGame("cards", "Baccarat", "Tie = Loss")));
        arrayList.add(new BoardTier("50M", new BoardGame("paper", "Paper Game", "Tie = Loss"), new BoardGame("67", "Odd or Even", "Creeper = Loss"), new BoardGame("wheel", "45/45/10", ""), new BoardGame("cards", "MexicanJack", "Tie = DD"), new BoardGame("arrows", "Higher & Lower", "Tie = Loss"), new BoardGame("horse", "Horse game", "x2.5")));
        return arrayList;
    }

    public static long stepSmall(long l, int n) {
        int n2 = 0;
        int n3 = 0;
        while (n3 < SMALL_STEPS.length) {
            if (SMALL_STEPS[n3] <= l) {
                n2 = n3;
            }
            ++n3;
        }
        return SMALL_STEPS[Math.max(0, Math.min(SMALL_STEPS.length - 1, n2 + n))];
    }

    public static void applyAccent(int n) {
        Settings.S.accent = n &= 0xFFFFFF;
        Settings.S.hudAccentColor = n;
        Settings.S.popupBorder = n;
        Auction.color = 0xFF000000 | n;
    }

    public static void load() {
        try {
            if (Files.exists(FILE, new LinkOption[0])) {
                Settings settings = (Settings)GSON.fromJson(Files.readString(FILE), Settings.class);
                if (settings != null) {
                    S = settings;
                }
                if (Settings.S.accent == 8191802) {
                    Settings.applyAccent(9133302);
                    Settings.save();
                }
                if (Settings.S.hudGain == Settings.S.hudLoss || Settings.S.hudGain == Settings.S.hudZero || Settings.S.hudLoss == Settings.S.hudZero) {
                    Settings.S.hudGain = 4906624;
                    Settings.S.hudLoss = 0xFF4040;
                    Settings.S.hudZero = 0xFFFFFF;
                    Settings.save();
                }
            }
        } catch (Exception exception) {
            System.err.println("[Bettors] Could not read " + String.valueOf(FILE) + ": " + String.valueOf(exception));
        }
        if (Settings.S.dailyHudStyleVersion < 1) {
            Settings.S.hudTitle = 15328981;
            Settings.S.hudGain = 0x7FFF22;
            Settings.S.hudBg = 4997931;
            Settings.S.hudAccentColor = 16761655;
            Settings.S.dailyHudStyleVersion = 1;
            Settings.save();
        }
    }

    public static void beginColorEdit() {
        if (!colorEditing) {
            colorEditing = true;
            colorDirty = false;
        }
    }

    public static void endColorEdit() {
        if (!colorEditing) {
            return;
        }
        colorEditing = false;
        boolean bl = colorDirty;
        colorDirty = false;
        if (bl) {
            Settings.save();
        }
    }

    public static void save() {
        if (colorEditing) {
            colorDirty = true;
            return;
        }
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            Files.writeString(FILE, (CharSequence)GSON.toJson((Object)S), new OpenOption[0]);
        } catch (Exception exception) {
            System.err.println("[Bettors] Could not save " + String.valueOf(FILE) + ": " + String.valueOf(exception));
        }
    }

    public static void resetAll() {
        S = new Settings();
        Settings.save();
    }

    public static long stepMoney(long l, int n) {
        int n2 = 0;
        int n3 = 0;
        while (n3 < MONEY_STEPS.length) {
            if (MONEY_STEPS[n3] <= l) {
                n2 = n3;
            }
            ++n3;
        }
        n2 = Math.max(0, Math.min(MONEY_STEPS.length - 1, n2 + n));
        return MONEY_STEPS[n2];
    }

    public static int nextColor(int n, boolean bl) {
        int[] nArray = bl ? BG_PRESETS : PRESETS;
        int n2 = 0;
        while (n2 < nArray.length) {
            if (nArray[n2] == n) {
                return nArray[(n2 + 1) % nArray.length];
            }
            ++n2;
        }
        return nArray[0];
    }

    public static String hex(int n) {
        return String.format("#%06X", n & 0xFFFFFF);
    }

    public static class BoardGame {
        public String icon;
        public String name;
        public String note;

        public BoardGame() {
        }

        public BoardGame(String string, String string2, String string3) {
            this.icon = string;
            this.name = string2;
            this.note = string3;
        }
    }

    public static class BoardTier {
        public String min;
        public List<BoardGame> games = new ArrayList<BoardGame>();

        public BoardTier() {
        }

        public BoardTier(String string, BoardGame ... boardGameArray) {
            this.min = string;
            this.games = new ArrayList<BoardGame>(List.of(boardGameArray));
        }
    }
}

