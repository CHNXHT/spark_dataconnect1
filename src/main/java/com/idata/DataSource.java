package com.idata;

public enum DataSource {
    //警民联调
    JMLT( "jdbc:oracle:thin:@//10.27.13.140:1521/zjkdb", "U_MT", "WYD5QzdzyyXG"),
    //促法
    CF("","",""),
    //合和码
    HHM( "jdbc:mysql://172.16.16.32:3306/contradiction?useSSL=false", "root", "idata@2023");

    private String url;
    private String user;
    private String password;

    DataSource(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
