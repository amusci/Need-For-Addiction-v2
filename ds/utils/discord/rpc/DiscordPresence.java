/*
 * Discord RPC integration for Need for Madness Multiplayer.
 */
package ds.utils.discord.rpc;

/**
 *
 * @author DragShot
 */
public interface DiscordPresence {
    public void load(String applicationId, String steamId);
    
    public void start();
    
    public void stop();
    
    public void showTimer();
    
    public void hideTimer();
    
    public default void setPresence(String details, String state,
            String thumbKey, String iconKey) {
        this.setPresence(details, state, 0, 0,
                thumbKey, null, iconKey, null,
                null, 0, 0, null, null);
    }
    
    public default void setPresence(String details, String state,
            String thumbKey, String thumbHint, String iconKey, String iconHint) {
        this.setPresence(details, state, 0, 0,
                thumbKey, thumbHint, iconKey, iconHint,
                null, 0, 0, null, null);
    }
    
    public default void setPresence(String details, String state, long startTime, long endTime,
            String thumbKey, String thumbHint, String iconKey, String iconHint) {
        this.setPresence(details, state, startTime, endTime,
                thumbKey, thumbHint, iconKey, iconHint,
                null, 0, 0, null, null);
    }
    
    public default void setPresence(String details, String state, long startTime, long endTime,
            String thumbKey, String thumbHint, String iconKey, String iconHint,
            String partyID, int partySize, int partyMax) {
        this.setPresence(details, state, startTime, endTime,
                thumbKey, thumbHint, iconKey, iconHint,
                partyID, partySize, partyMax, null, null);
    }
    
    public void setPresence(String details, String state, long startTime, long endTime,
            String thumbKey, String thumbHint, String iconKey, String iconHint,
            String partyID, int partySize, int partyMax, String specKey, String joinKey);
    
    public void clearPresence();
}
