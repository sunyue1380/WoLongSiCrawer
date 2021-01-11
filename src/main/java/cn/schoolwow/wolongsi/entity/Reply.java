package cn.schoolwow.wolongsi.entity;

import cn.schoolwow.quickdao.annotation.*;

import java.time.LocalDateTime;

@Comment("帖子回复")
public class Reply {
    @Id
    private long id;

    @Comment("视频aid")
    @Constraint(notNull = true,unique = true)
    @Index
    private long aid;

    @Comment("评论内容")
    private String content;

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

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

