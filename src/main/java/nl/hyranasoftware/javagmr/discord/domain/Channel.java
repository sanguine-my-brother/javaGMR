package nl.hyranasoftware.javagmr.discord.domain;

import java.util.List;

import org.joda.time.DateTime;

public class Channel{

    private long id;
    private Channel_type type;
    private long guild_id;
    private int position;
    private List<Overwrite> permission_overwrites;
    private String name;
    private String topic;
    private boolean nsfw;
    private long last_message_id;
    private int bitrate;
    private int user_limit;
    private List<DiscordUser> recipients;
    private String icon;
    private long owner_id;
    private long application_id;
    private long parent_id;
    private DateTime last_pin_timestamp;


    public enum Channel_type{
        GUILD_TEXT, DM, GUILD_VOICE, GROUP_DM, GUILD_CATEGORY
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Channel_type getType() {
        return type;
    }

    public void setType(Channel_type type) {
        this.type = type;
    }

    public long getGuild_id() {
        return guild_id;
    }

    public void setGuild_id(long guild_id) {
        this.guild_id = guild_id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<Overwrite> getPermission_overwrites() {
        return permission_overwrites;
    }

    public void setPermission_overwrites(List<Overwrite> permission_overwrites) {
        this.permission_overwrites = permission_overwrites;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    public long getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(long last_message_id) {
        this.last_message_id = last_message_id;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getUser_limit() {
        return user_limit;
    }

    public void setUser_limit(int user_limit) {
        this.user_limit = user_limit;
    }

    public List<DiscordUser> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<DiscordUser> recipients) {
        this.recipients = recipients;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(long owner_id) {
        this.owner_id = owner_id;
    }

    public long getApplication_id() {
        return application_id;
    }

    public void setApplication_id(long application_id) {
        this.application_id = application_id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public DateTime getLast_pin_timestamp() {
        return last_pin_timestamp;
    }

    public void setLast_pin_timestamp(DateTime last_pin_timestamp) {
        this.last_pin_timestamp = last_pin_timestamp;
    }


}
