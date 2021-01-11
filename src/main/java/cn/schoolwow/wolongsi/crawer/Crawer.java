package cn.schoolwow.wolongsi.crawer;

import java.io.IOException;

public interface Crawer {
    /**抓取元数据*/
    void getMetaData() throws IOException;

    /**抓取今日发布视频列表数据*/
    void getTodayVideoList() throws IOException;

    /**
     * 发送评论
     * @param aid 视频id
     * @param content 评论内容
     * */
    boolean postComment(long aid, String content) throws IOException;

    /**
     * 生成评论内容
     * @param aid 视频aid
     * */
    String generateComment(long aid);
}
