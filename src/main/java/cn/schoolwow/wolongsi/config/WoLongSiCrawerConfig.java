package cn.schoolwow.wolongsi.config;

import cn.schoolwow.quickdao.QuickDAO;
import cn.schoolwow.quickdao.dao.DAO;
import com.zaxxer.hikari.HikariDataSource;

public class WoLongSiCrawerConfig {
    public static DAO dao;
    static{
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1/wolongsi?characterEncoding=utf8&useSSL=true&serverTimezone=Hongkong");
        dataSource.setUsername("wolongsi");
        dataSource.setPassword("123456");

        dao = QuickDAO.newInstance().dataSource(dataSource)
                .packageName("cn.schoolwow.wolongsi.entity")
                .build();
    }
}
