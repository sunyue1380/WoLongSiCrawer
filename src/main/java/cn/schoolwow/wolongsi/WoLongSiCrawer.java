package cn.schoolwow.wolongsi;

import cn.schoolwow.quickdao.dao.DAO;
import cn.schoolwow.wolongsi.config.WoLongSiCrawerConfig;
import cn.schoolwow.wolongsi.crawer.AbstractCrawer;
import cn.schoolwow.wolongsi.crawer.Crawer;
import cn.schoolwow.wolongsi.entity.Reply;
import cn.schoolwow.wolongsi.entity.Video;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**卧龙寺视频数据统计*/
public class WoLongSiCrawer {
    private static Crawer crawer = new AbstractCrawer();

    public static void main(String[] args){
        getMeta();
        getVideoList();
        postComment();
        while(true){
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 定时抓取元数据信息
     * */
    private static void getMeta(){
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(()->{
            try {
                crawer.getMetaData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 3, 10, TimeUnit.SECONDS);
    }

    /**
     * 定时抓取视频信息
     * */
    private static void getVideoList(){
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(()->{
            try {
                crawer.getTodayVideoList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 5, 30, TimeUnit.SECONDS);
    }

    /**
     * 定时发评论
     * */
    private static void postComment(){
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(()->{
            DAO dao = WoLongSiCrawerConfig.dao;
            //查找最近5分钟未发表过评论的视频
            LocalDateTime now = LocalDateTime.now();

            List<Long> aidList = dao.query(Video.class)
                    .tableAliasName("v")
                    .addColumn("aid")
                    .addBetweenQuery("created",now.minusMinutes(5),now)
                    .orderByDesc("created")
                    .addNotExistSubQuery(
                            dao.query(Reply.class)
                                    .tableAliasName("r")
                                    .addColumn("aid")
                                    .addRawQuery("v.aid = r.aid")
                    )
                    .execute()
                    .getSingleColumnList(Long.class);
            for(Long aid:aidList){
                String content = crawer.generateComment(aid);
                try {
                    crawer.postComment(aid,content);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        Thread.sleep(1000+Math.round(Math.random()*3000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }
}
