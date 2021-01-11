package cn.schoolwow.wolongsi.entity;

import cn.schoolwow.quickdao.annotation.*;

import java.time.LocalDateTime;

@Comment("视频列表")
public class Video {
    @Id
    private long id;

    @Comment("标题")
    private String title;

    @Comment("描述")
    private String description;

    @Comment("分类")
    private int typeid;

    @Comment("视频发布时间")
    private LocalDateTime created;

    @Comment("视频长度(秒)")
    private int lengthSeconds;

    @Comment("视频长度格式化")
    private String length;

    @Comment("av号")
    @Constraint(notNull = true, unique = true)
    @Index
    private long aid;

    @Comment("bv号")
    private String bvid;

    @Comment("评论数")
    private int comment;

    @Comment("播放量")
    private long play;

    @Comment("是否付费")
    @ColumnType("TINYINT")
    private int isPay;

    @Comment("是否联合投稿")
    @ColumnType("TINYINT")
    private int isUnionVideo;

    @Comment("is_steins_gate")
    @ColumnType("TINYINT")
    private int isSteinsGate;

    @Comment("hide_click")
    @ColumnType("TINYINT")
    private boolean hideClick;

    @Comment("video_review")
    @ColumnType("TINYINT")
    private int videoReview;

    @TableField(createdAt = true)
    private LocalDateTime createdTime;

    @TableField(updatedAt = true)
    private LocalDateTime updatedTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public int getLengthSeconds() {
        return lengthSeconds;
    }

    public void setLengthSeconds(int lengthSeconds) {
        this.lengthSeconds = lengthSeconds;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

    public String getBvid() {
        return bvid;
    }

    public void setBvid(String bvid) {
        this.bvid = bvid;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public long getPlay() {
        return play;
    }

    public void setPlay(long play) {
        this.play = play;
    }

    public int getIsPay() {
        return isPay;
    }

    public void setIsPay(int isPay) {
        this.isPay = isPay;
    }

    public int getIsUnionVideo() {
        return isUnionVideo;
    }

    public void setIsUnionVideo(int isUnionVideo) {
        this.isUnionVideo = isUnionVideo;
    }

    public int getIsSteinsGate() {
        return isSteinsGate;
    }

    public void setIsSteinsGate(int isSteinsGate) {
        this.isSteinsGate = isSteinsGate;
    }

    public boolean isHideClick() {
        return hideClick;
    }

    public void setHideClick(boolean hideClick) {
        this.hideClick = hideClick;
    }

    public int getVideoReview() {
        return videoReview;
    }

    public void setVideoReview(int videoReview) {
        this.videoReview = videoReview;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}

