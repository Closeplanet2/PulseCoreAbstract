package com.pandapulsestudios.v1_20_r3;

import com.pandapulsestudios.api.Interface.ActionBarDataNMS;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBarData implements ActionBarDataNMS {
    private String message;

    @Override
    public void PassMessage(String message) {
        this.message = message;
    }

    @Override
    public String ReturnMessage() {
        return message;
    }

    @Override
    public void DisplayActionBar(Player player) {
        if(player == null || !player.isOnline()) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}
