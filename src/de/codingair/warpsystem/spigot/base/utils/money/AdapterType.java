package de.codingair.warpsystem.spigot.base.utils.money;

import de.codingair.warpsystem.spigot.base.utils.money.adapters.Essentials;
import de.codingair.warpsystem.spigot.base.utils.money.adapters.Vault;
import org.bukkit.Bukkit;

public enum AdapterType {
    ESSENTIALS(Bukkit.getPluginManager().isPluginEnabled("Essentials") ? new Essentials() : null),
    VAULT(Bukkit.getPluginManager().isPluginEnabled("Vault") ? new Vault() : null);

    private Adapter adapter;

    AdapterType(Adapter adapter) {
        this.adapter = adapter;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public static Adapter getActive() {
        for(AdapterType adapterType : values()) {
            if(adapterType.getAdapter() != null) return adapterType.getAdapter();
        }

        return null;
    }

    public static boolean canEnable() {
        return getActive() != null;
    }
}
