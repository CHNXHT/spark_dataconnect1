package com.idata;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import java.util.Properties;

public class jmltconnect {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("MysqlSupport")
                .master("local[2]")
                .getOrCreate();


        String origin_url = "jdbc:oracle:thin:@//10.27.13.140:1521/zjkdb";
        Properties origin_properties = new Properties();
        origin_properties.setProperty("user", "U_MT");
        origin_properties.setProperty("password", "WYD5QzdzyyXG");
        Dataset<Row> vSjgxr = spark
                .read()
                .jdbc(origin_url, "V_SJGXR", origin_properties)
                .toDF();

        Dataset<Row> vZd = spark
                .read()
                .jdbc(origin_url, "V_ZD", origin_properties)
                .toDF();

        vZd.persist();
        Dataset<Row> mz = vZd.where("LXJP = 'MZ'").select("DM", "MC");
        Dataset<Row> mzDF = vSjgxr.join(mz, vSjgxr.col("MZ").equalTo(mz.col("DM")), "LEFT");

        String target_url = "jdbc:oracle:thin:@//10.27.13.140:1521/zjkdb";
        Properties target_properties = new Properties();
        target_properties.setProperty("user", "U_MT");
        target_properties.setProperty("password", "WYD5QzdzyyXG");
        mzDF
                .write()
                .mode(SaveMode.Overwrite)
                .jdbc(target_url, "MZ", target_properties);

        spark.close();
    }
}
