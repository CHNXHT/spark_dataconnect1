package com.idata;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import java.sql.SQLException;
import static com.idata.ReadData.getRawDF;
import static com.idata.utils.connectionUtil.hhm_mysqlProperties;
import static com.idata.utils.tableUtil.createMySQLTable;

public class RawDataSync {
    public static void main(String[] args) throws SQLException {

        SparkSession spark = SparkSession.builder()
                .appName("raw data sync")
                .master("local[2]")
                .getOrCreate();

        String dataSourceName = args[0];
        String tableName = args[1];

        Dataset<Row> rawDF = getRawDF(spark, dataSourceName, tableName);

        /*
          获取Oracle表的字段结构
         */
        StructType rawTableSchema = rawDF.schema();

        /*
          创建MySQL表的字段结构
         */
        createMySQLTable(spark, tableName, rawTableSchema);

        /*
          将原始表数据写入MySQL表中
         */
        rawDF.repartition(20)
                .write()
                .mode("append")
                .jdbc(DataSource.HHM.getUrl(), tableName, hhm_mysqlProperties());

//        Dataset<Row> vZd = spark
//                .read()
//                .jdbc(origin_url, "V_ZD", origin_properties)
//                .toDF();
//
//        vZd.persist();
//        Dataset<Row> mz = vZd.where("LXJP = 'MZ'").select("DM", "MC");
//        Dataset<Row> mzDF = vSjgxr.join(mz, vSjgxr.col("MZ").equalTo(mz.col("DM")), "LEFT");
//
//        String target_url = "jdbc:oracle:thin:@//10.27.13.140:1521/zjkdb";
//        Properties target_properties = new Properties();
//        target_properties.setProperty("user", "U_MT");
//        target_properties.setProperty("password", "WYD5QzdzyyXG");
//
//        mzDF
//                .write()
//                .mode(SaveMode.Overwrite)
//                .jdbc(target_url, "MZ", target_properties);

        spark.close();
    }

}
