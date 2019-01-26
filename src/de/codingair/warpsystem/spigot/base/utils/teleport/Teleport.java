package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.codingapi.particles.animations.Animation;
import de.codingair.codingapi.particles.animations.playeranimations.CircleAnimation;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.effects.RotatingParticleSpiral;
import de.codingair.warpsystem.spigot.base.utils.money.AdapterType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Teleport {
    private Player player;
    private Animation animation;
    private BukkitRunnable runnable;

    private Sound finishSound = Sound.ENDERMAN_TELEPORT;
    private Sound cancelSound = Sound.ITEM_BREAK;

    private long startTime = 0;

    private Destination destination;

    private String displayName;
    private int seconds;
    private double costs;
    private boolean showMessage;
    private boolean canMove;
    private String message;
    private boolean silent;
    private Callback<TeleportResult> callback;

    public Teleport(Player player, Destination destination, String displayName, int seconds, double costs, String message, boolean canMove, boolean silent, Callback<TeleportResult> callback) {
        this.player = player;
        this.destination = destination;
        this.displayName = displayName;
        this.seconds = seconds;
        this.costs = costs;
        this.message = message;
        this.canMove = canMove;
        this.silent = silent;
        this.callback = callback;

        if(player.hasPermission(WarpSystem.PERMISSION_ByPass_Teleport_Costs)) this.costs = 0;

        this.animation = new CircleAnimation(WarpSystem.getInstance().getTeleportManager().getParticle(), player, WarpSystem.getInstance(), WarpSystem.getInstance().getTeleportManager().getRadius());
        this.runnable = new BukkitRunnable() {
            private int left = seconds;
            private String msg = Lang.get("Teleporting_Info");

            @Override
            public void run() {
                if(left == 0) {
                    teleport();
                    MessageAPI.sendActionBar(player, null);
                    return;
                }

                player.playSound(player.getLocation(), Sound.NOTE_PIANO.bukkitSound(), 1.5F, 0.5F);
                MessageAPI.sendActionBar(player, msg.replace("%seconds%", left + ""));

                left--;
            }
        };
    }

    public void start() {
        if(!animation.isRunning()) {
            this.startTime = System.currentTimeMillis();
            this.animation.setRunning(true);
            this.runnable.runTaskTimer(WarpSystem.getInstance(), 0L, 20L);
        }
    }

    public void cancel(boolean sound, boolean finished) {
        if(animation.isRunning()) {
            this.startTime = 0;
            this.animation.setRunning(false);
            this.runnable.cancel();
            MessageAPI.sendActionBar(player, null);
        }
        if(sound && cancelSound != null) cancelSound.playSound(player);

        if(!finished) {
            payBack();
            if(callback != null) callback.accept(TeleportResult.CANCELLED);
        }
    }

    public void teleport() {
        WarpSystem.getInstance().getTeleportManager().getTeleports().remove(this);

        cancel(false, true);
        if(destination == null) return;

        if(message != null) {
            if(this.costs > 0) {
                message = (message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", costs + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName));
            } else if(showMessage) {
                message = (message.startsWith(Lang.getPrefix()) ? "" : Lang.getPrefix()) + message.replace("%AMOUNT%", costs + "").replace("%warp%", ChatColor.translateAlternateColorCodes('&', displayName));
            }
        }

        if(!destination.teleport(player, message, displayName, silent, costs, callback)) {
            return;
        }

        if(player.isOnline()) {
            playAfterEffects(player);
            if(finishSound != null) finishSound.playSound(player);
        }
    }

    private void payBack() {
        if(AdapterType.getActive() != null) {
            AdapterType.getActive().setMoney(player, AdapterType.getActive().getMoney(player) + this.costs);
        }
    }

    public void playAfterEffects(Player player) {
        if(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Teleport.Animation_After_Teleport.Enabled", true)) {
            new RotatingParticleSpiral(player, player.getLocation()).runTaskTimer(WarpSystem.getInstance(), 1, 1);
        }
    }

    public SimulatedTeleportResult simulate(Player player) {
        if(this.destination == null) throw new IllegalArgumentException("Destination cannot be null!");
        return this.destination.simulate(player);
    }

    public Player getPlayer() {
        return player;
    }

    public Destination getDestination() {
        return destination;
    }

    public Animation getAnimation() {
        return animation;
    }

    public BukkitRunnable getRunnable() {
        return runnable;
    }

    public Sound getFinishSound() {
        return finishSound;
    }

    public void setFinishSound(Sound finishSound) {
        this.finishSound = finishSound;
    }

    public Sound getCancelSound() {
        return cancelSound;
    }

    public void setCancelSound(Sound cancelSound) {
        this.cancelSound = cancelSound;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    public int getSeconds() {
        return seconds;
    }
}