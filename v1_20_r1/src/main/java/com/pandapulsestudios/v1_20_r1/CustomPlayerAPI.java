package com.pandapulsestudios.v1_20_r1;

import com.pandapulsestudios.api.Interface.PlayerAPI;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class CustomPlayerAPI implements PlayerAPI {
    @Override
    public String[] GetPlayerTexture(Player player) {
        var currentProfile = ((CraftPlayer) player).getHandle().getGameProfile();
        var currentProfileProp = currentProfile.getProperties();
        var textureProp = currentProfileProp.get("textures").iterator().next();
        return new String[]{textureProp.getValue(), textureProp.getSignature()};
    }
}
