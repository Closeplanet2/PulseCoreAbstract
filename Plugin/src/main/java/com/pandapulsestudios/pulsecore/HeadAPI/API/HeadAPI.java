package com.pandapulsestudios.pulsecore.HeadAPI.API;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Base64;
import java.util.UUID;

public class HeadAPI {
    private static boolean IS_TYPE(String variable) {
        try {
            var uuid = UUID.fromString(variable);
            return true;
        } catch (Exception ex) { return false; }
    }

    public static ItemStack ReturnPlayerHead(OfflinePlayer player){
        if(player == null) return null;

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static ItemStack ReturnPlayerHead(String uuid){
        if(uuid == null) return null;
        if(IS_TYPE(uuid)) return null;

        var offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        return ReturnPlayerHead(offlinePlayer);
    }

    public static ItemStack ReturnPlayerHead(String name, int amount, String url) {
        if(name == null || url == null) return null;
        if(amount <=0) amount = 1;

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        if (url.length() < 16) {
            skullMeta.setOwner(url);
            skullMeta.setDisplayName(name);
            skull.setItemMeta(skullMeta);
            return skull;
        }

        var s_url = new StringBuilder();
        s_url.append("https://textures.minecraft.net/texture/").append(url);
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", s_url.toString()).getBytes());
        gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

        try {
            var field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, gameProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.setDisplayName(name);
        skull.setItemMeta(skullMeta);

        return skull;
    }
}
