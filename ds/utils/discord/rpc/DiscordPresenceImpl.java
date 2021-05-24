/*
 * Modularized Discord RPC integration.
 */
package ds.utils.discord.rpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import club.minnced.discord.rpc.DiscordUser;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author DragShot
 */
public final class DiscordPresenceImpl implements DiscordPresence {
    DiscordRPC lib;
    String applicationId = "846448896938213387";
    String steamId = "";
    DiscordUser user;
    DiscordRichPresence lastData;
    
    ScheduledExecutorService executor;
    
    public DiscordPresenceImpl() {
        lib = DiscordRPC.INSTANCE;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void load(String applicationId, String steamId) {
        this.applicationId = applicationId == null ? "" : applicationId;
        this.steamId = steamId == null ? "" : steamId;
    }

    @Override
    public void start() {
        executor.submit(this::initRPC);
    }
    
    protected void initRPC() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = this::registerUser;
        //handlers.errored = (errorCode, message) -> jlblStatus.setText("Error #" + errorCode + ": " + message);
        //handlers.disconnected = (errorCode, message) -> jlblStatus.setText("Disconnected");
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        executor.schedule(lib::Discord_RunCallbacks, 2, TimeUnit.SECONDS);
    }
    
    protected void registerUser(DiscordUser user) {
        this.user = user;
    }

    @Override
    public void stop() {
        try {
            executor.submit(this::shutdownRPC).get();
        } catch (InterruptedException | ExecutionException ex) {}
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {}
    }
    
    protected void shutdownRPC() {
        this.clearPresence();
        lib.Discord_Shutdown();
    }
    
    @Override
    public void showTimer() {
        this.lastData.startTimestamp = System.currentTimeMillis() / 1000;
        executor.submit(() -> lib.Discord_UpdatePresence(this.lastData));
    }
    
    @Override
    public void hideTimer() {
        this.lastData.startTimestamp = 0;
        executor.submit(() -> lib.Discord_UpdatePresence(this.lastData));
    }

    @Override
    public void setPresence(String details, String state, long startTime, long endTime,
            String thumbKey, String thumbHint, String iconKey, String iconHint,
            String partyID, int partySize, int partyMax, String specKey, String joinKey) {
        DiscordRichPresence presence = new DiscordRichPresence();
        if (state != null && !state.isEmpty()) presence.state = state;
        if (details != null && !details.isEmpty()) presence.details = details;
        if (startTime >= 0) presence.startTimestamp = (int)(startTime / 1000);
        if (endTime >= 0) presence.endTimestamp = (int)(endTime / 1000);
        if (thumbKey != null && !thumbKey.isEmpty()) presence.largeImageKey = thumbKey;
        if (thumbHint != null && !thumbHint.isEmpty()) presence.largeImageText = thumbHint;
        if (iconKey != null && !iconKey.isEmpty()) presence.smallImageKey = iconKey;
        if (iconHint != null && !iconHint.isEmpty()) presence.smallImageText = iconHint;
        if (partyID != null && !partyID.isEmpty()) presence.partyId = partyID;
        if (partySize >= 0) presence.partySize = partySize;
        if (partyMax >= 0) presence.partyMax = partyMax;
        if (specKey != null && !specKey.isEmpty()) presence.spectateSecret = specKey;
        if (joinKey != null && !joinKey.isEmpty()) presence.joinSecret = joinKey;
        this.lastData = presence;
        executor.submit(() -> lib.Discord_UpdatePresence(presence));
    }

    @Override
    public void clearPresence() {
        executor.submit(lib::Discord_ClearPresence);
    }
     
}
