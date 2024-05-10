package com.pandapulsestudios.pulsecore.BossBarAPI.Object;

import com.pandapulsestudios.pulsecore.ChatAPI.API.MessageAPI;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public record BarData(String barTitle, BarColor barColor, BarStyle barStyle, double barProgress) {
    public void DisplayBarData(BossBar bossBar){
        if(bossBar == null) return;
        bossBar.setTitle(MessageAPI.FormatMessage(barTitle, true, true, null));
        bossBar.setColor(barColor);
        bossBar.setStyle(barStyle);
        bossBar.setProgress(Math.max(0, Math.min(barProgress, 1)));
    }
}
