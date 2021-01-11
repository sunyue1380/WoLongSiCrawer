package cn.schoolwow.wolongsi.entity;

import cn.schoolwow.quickdao.annotation.Comment;
import cn.schoolwow.quickdao.annotation.Id;
import cn.schoolwow.quickdao.annotation.TableField;

import java.time.LocalDateTime;

@Comment("元数据")
public class Meta {
    @Id
    private long id;

    @Comment("关注数")
    private long following;

    @Comment("粉丝")
    private long follower;

    @Comment("总播放量")
    private long archive;

    @Comment("总阅读数")
    private long article;

    @Comment("点赞数")
    private long likes;

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

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getFollower() {
        return follower;
    }

    public void setFollower(long follower) {
        this.follower = follower;
    }

    public long getArchive() {
        return archive;
    }

    public void setArchive(long archive) {
        this.archive = archive;
    }

    public long getArticle() {
        return article;
    }

    public void setArticle(long article) {
        this.article = article;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
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
