package dev.profit.dashboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

/**
 * Old DonutSMP scoreboard: the server's own sidebar, but only the Money, Shards, Deaths and
 * Playtime lines (icons and colors come from the server). Only changes what you see.
 */
final class OldScoreboard {
    private OldScoreboard() { }

    private static final String[] KEEP = {"money", "shards", "deaths", "playtime"};
    private static final Comparator<PlayerScoreEntry> ORDER = Comparator.comparing(PlayerScoreEntry::value).reversed()
            .thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);

    private record Line(Component name, Component score, int scoreWidth) { }

    /** Draws the trimmed scoreboard. Returns false when there's nothing to show (then the normal one is drawn). */
    static boolean draw(GuiGraphicsExtractor g, Minecraft mc) {
        if (mc.level == null || mc.player == null) return false;
        Scoreboard sb = mc.level.getScoreboard();
        Objective obj = null;
        PlayerTeam myTeam = sb.getPlayersTeam(mc.player.getScoreboardName());
        if (myTeam != null) {
            DisplaySlot slot = DisplaySlot.teamColorToSlot(myTeam.getColor());
            if (slot != null) obj = sb.getDisplayObjective(slot);
        }
        if (obj == null) obj = sb.getDisplayObjective(DisplaySlot.SIDEBAR);
        if (obj == null) return false;

        Font font = mc.font;
        NumberFormat fmt = obj.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
        List<PlayerScoreEntry> entries = new ArrayList<>(sb.listPlayerScores(obj));
        entries.sort(ORDER);
        List<Line> lines = new ArrayList<>();
        for (PlayerScoreEntry e : entries) {
            if (e.isHidden()) continue;
            Component name = PlayerTeam.formatNameForTeam(sb.getPlayersTeam(e.owner()), e.ownerName());
            Component score = e.formatValue(fmt);
            String text = (name.getString() + " " + score.getString()).toLowerCase(Locale.ROOT);
            for (String k : KEEP) {
                if (text.contains(k)) {
                    lines.add(new Line(name, score, font.width(score)));
                    break;
                }
            }
        }
        if (lines.isEmpty()) return false;

        // same layout as vanilla's sidebar
        Component title = obj.getDisplayName();
        int titleW = font.width(title);
        int widest = titleW;
        int spacer = font.width(": ");
        for (Line l : lines) widest = Math.max(widest, font.width(l.name()) + (l.scoreWidth() > 0 ? spacer + l.scoreWidth() : 0));
        int count = lines.size();
        int bottom = g.guiHeight() / 2 + count * 9 / 3;
        int left = g.guiWidth() - widest - 3;
        int right = g.guiWidth() - 3 + 2;
        int headerY = bottom - count * 9;
        g.fill(left - 2, headerY - 9 - 1, right, headerY - 1, mc.options.getBackgroundColor(0.4F));
        g.fill(left - 2, headerY - 1, right, bottom, mc.options.getBackgroundColor(0.3F));
        g.text(font, title, left + widest / 2 - titleW / 2, headerY - 9, -1, false);
        for (int i = 0; i < count; i++) {
            Line l = lines.get(i);
            int y = bottom - (count - i) * 9;
            g.text(font, l.name(), left, y, -1, false);
            g.text(font, l.score(), right - l.scoreWidth(), y, -1, false);
        }
        return true;
    }
}
