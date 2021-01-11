package cn.schoolwow.wolongsi.crawer;

import cn.schoolwow.quickdao.dao.DAO;
import cn.schoolwow.quickdao.transaction.Transaction;
import cn.schoolwow.quickhttp.QuickHttp;
import cn.schoolwow.quickhttp.connection.Connection;
import cn.schoolwow.quickhttp.response.Response;
import cn.schoolwow.wolongsi.config.WoLongSiCrawerConfig;
import cn.schoolwow.wolongsi.entity.Meta;
import cn.schoolwow.wolongsi.entity.Video;
import cn.schoolwow.wolongsi.entity.Reply;
import cn.schoolwow.wolongsi.util.WoLongSiUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractCrawer implements Crawer{
    Logger logger = LoggerFactory.getLogger(AbstractCrawer.class);
    static{
        String cookie = "登陆以后的bilibili的Cookie信息";
        QuickHttp.addCookie(cookie,"bilibili.com");
    }

    @Override
    public void getMetaData() throws IOException {
        Meta meta = new Meta();
        //获取粉丝赞评
        {
            Response response = QuickHttp.connect("https://api.bilibili.com/x/relation/stat?vmid=258457966&jsonp=jsonp")
                    .connectTimeout(1000)
                    .readTimeout(2000)
                    .execute();
            JSONObject data = response.bodyAsJSONObject().getJSONObject("data");
            meta.setFollowing(data.getLongValue("following"));
            meta.setFollower(data.getLongValue("follower"));
        }
        //播放数 阅读数
        {
            Response response = QuickHttp.connect("https://api.bilibili.com/x/space/upstat?mid=258457966&jsonp=jsonp")
                    .connectTimeout(1000)
                    .readTimeout(2000)
                    .execute();
            JSONObject data = response.bodyAsJSONObject().getJSONObject("data");
            meta.setArchive(data.getJSONObject("archive").getLongValue("view"));
            meta.setArticle(data.getJSONObject("article").getLongValue("view"));
            meta.setLikes(data.getLongValue("likes"));
        }
        WoLongSiCrawerConfig.dao.insert(meta);
    }

    @Override
    public void getTodayVideoList() throws IOException {
        long endTime = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long lastVideoCreated = endTime+1000;
        int pn = 1;
        while(lastVideoCreated>=endTime){
            Response response = QuickHttp.connect("https://api.bilibili.com/x/space/arc/search?mid=258457966&ps=50&order=pubdate&index=1&jsonp=jsonp")
                    .parameter("pn",(pn++)+"")
                    .execute();
            JSONArray array = response.bodyAsJSONObject().getJSONObject("data").getJSONObject("list").getJSONArray("vlist");
            Transaction transaction = WoLongSiCrawerConfig.dao.startTransaction();
            for(int i=0;i<array.size();i++){
                JSONObject o = array.getJSONObject(i);

                Video video = new Video();
                video.setComment(o.getIntValue("comment"));
                video.setTypeid(o.getIntValue("typeid"));
                video.setDescription(o.getString("description"));
                video.setTitle(o.getString("title"));
                video.setCreated(LocalDateTime.ofEpochSecond(o.getLongValue("created"),0,ZoneOffset.of("+8")));
                video.setLength(o.getString("length"));
                //计算长度
                String[] tokens = video.getLength().split(":");
                switch(tokens.length){
                    case 1:{
                        video.setLengthSeconds(Integer.parseInt(tokens[0]));
                    }break;
                    case 2:{
                        video.setLengthSeconds(Integer.parseInt(tokens[0])*60+Integer.parseInt(tokens[1]));
                    }break;
                    case 3:{
                        video.setLengthSeconds(Integer.parseInt(tokens[0])*3600+Integer.parseInt(tokens[1])*60+Integer.parseInt(tokens[2]));
                    }break;
                }
                video.setAid(o.getLongValue("aid"));
                video.setBvid(o.getString("bvid"));
                video.setPlay(o.getIntValue("play"));
                video.setVideoReview(o.getIntValue("video_review"));
                video.setIsPay(o.getIntValue("is_pay"));
                video.setIsUnionVideo(o.getIntValue("is_union_video"));
                video.setIsSteinsGate(o.getIntValue("is_steins_gate"));
                video.setHideClick(o.getBoolean("hide_click"));

                transaction.save(video);
                if(i==array.size()-1){
                    lastVideoCreated = o.getLongValue("created")*1000;
                }
            }
            transaction.commit();
            transaction.endTransaction();
            try {
                Thread.sleep(Math.round(Math.random()*3000)+1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean postComment(long aid, String content) throws IOException {
        DAO dao = WoLongSiCrawerConfig.dao;
        if(null!=dao.fetch(Reply.class,"aid",aid)){
            logger.warn("[评论已经发送过]aid:{}",aid);
            return false;
        }
        String csrf = QuickHttp.getCookie("bilibili.com","bili_jct").getValue();
        Response response = QuickHttp.connect("https://api.bilibili.com/x/v2/reply/add")
                .method(Connection.Method.POST)
                .data("oid",aid+"")
                .data("type","1")
                .data("message",content)
                .data("plat","1")
                .data("ordering","heat")
                .data("jsonp","jsonp")
                .data("csrf",csrf)
                .execute();
        JSONObject data = response.bodyAsJSONObject().getJSONObject("data");
        if(0==data.getIntValue("success_action")){
            Reply reply = new Reply();
            reply.setAid(aid);
            reply.setContent(content);
            WoLongSiCrawerConfig.dao.insert(reply);
            return true;
        }
        logger.warn("[发送评论失败]{}",response.body());
        return false;
    }

    @Override
    public String generateComment(long aid) {
        DAO dao = WoLongSiCrawerConfig.dao;
        Video video = dao.fetch(Video.class,"aid",aid);

        LocalDateTime today = LocalDate.now().atStartOfDay();
        StringBuilder builder = new StringBuilder();
        //计算截止目前投稿数
        int numberOfVideo = (int) dao.query(Video.class)
                .addBetweenQuery("created", today,video.getCreated())
                .execute()
                .count();
        builder.append("这是今日第"+numberOfVideo+"个投稿,av号:"+video.getAid()+",bv号:"+video.getBvid()+"\n");
        //计算和上个视频发布的间隔时间
        {
            Video previousVideo = (Video) dao.query(Video.class)
                    .addQuery("created","<",video.getCreated())
                    .orderByDesc("created")
                    .limit(0,1)
                    .execute()
                    .getOne();
            long seconds = Duration.between(previousVideo.getCreated(),video.getCreated()).getSeconds();
            builder.append("距离上个视频发布间隔"+ WoLongSiUtil.formatSeconds(seconds) +"\n");
        }
        //计算平均投递间隔
        {
            long postSeconds = (Duration.between(today,video.getCreated()).getSeconds())/numberOfVideo;
            builder.append("今日平均视频投递间隔时间:"+WoLongSiUtil.formatSeconds(postSeconds)+"\n");
        }
        //统计视频总长度
        {
            JSONObject result = dao.query(Video.class)
                    .addColumn("sum(comment) commentCount")
                    .addColumn("sum(play) playCount")
                    .addColumn("sum(length_seconds) secondsCount")
                    .addBetweenQuery("created", LocalDate.now().atStartOfDay(),video.getCreated())
                    .execute()
                    .getObject();
            builder.append("今日发布视频总长度:"+WoLongSiUtil.formatSeconds(result.getLongValue("secondsCount"))+"\n");
            builder.append("今日总评论数:"+result.getLongValue("commentCount")+"\n");
            builder.append("今日总播放量:"+result.getLongValue("playCount")+"\n");
        }
        //统计视频来源
        {
            List<String> descriptionList = dao.query(Video.class)
                    .addBetweenQuery("created", LocalDate.now().atStartOfDay(),video.getCreated())
                    .addColumn("description")
                    .execute()
                    .getSingleColumnList(String.class);
            Map<String,Integer> sourceCountMap = new HashMap<>();
            int unknown = 0;
            for(String description:descriptionList){
                if(!description.startsWith("http")){
                    unknown++;
                    continue;
                }
                String host = URI.create(description).getHost();
                if(!sourceCountMap.containsKey(host)){
                    sourceCountMap.put(host,0);
                }
                sourceCountMap.put(host,sourceCountMap.get(host)+1);
            }
            builder.append("视频来源:\n");
            Set<Map.Entry<String,Integer>> entrySet = sourceCountMap.entrySet();
            for(Map.Entry<String,Integer> entry:entrySet){
                builder.append(entry.getKey()+":"+entry.getValue()+"\n");
            }
            if(unknown>0){
                builder.append("未知:"+unknown);
            }
        }
        return builder.toString();
    }
}
