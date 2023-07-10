package com.idata.plugin.jmlt;

import com.idata.DataSource;
import com.idata.model.hhm.TMediationCase;
import com.idata.model.jmlt.VSjxx;
import com.idata.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import scala.Function1;
import java.io.Serializable;
import java.text.ParseException;

import static com.idata.ReadData.getRawDF;
import static com.idata.utils.connectionUtil.hhm_mysqlProperties;
public class vSjxx {
    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder()
                .appName("raw data sync")
                .master("local[2]")
                .getOrCreate();

        // 定义自定义 UDF
//        spark.udf().register("decimalToLong", (Decimal decimal) -> decimal.toLong(), DataTypes.LongType);
        /*
          dataSourceName包括如下
          1、JMLT
          2、CF
          3、HHM
         */
        String dataSourceName = "JMLT";//args[0];
        String tableName = "V_SJXX";//args[1];
        String targetTableName = "t_mediation_case_test";

        //获取来源表数据
        Dataset<Row> rawDF = getRawDF(spark, tableName, dataSourceName);
        Dataset<Row> rowDataset = rawDF.withColumn("SJRS", rawDF.col("SJRS").cast(DataTypes.LongType));
//        rawDF.printSchema();
        //定义数据源对象
        Dataset<VSjxx> vsjxxDF = rowDataset.as(Encoders.bean(VSjxx.class));
//        Dataset<Row> rowDataset = vsjxxDF.withColumn("SJRS", vsjxxDF.col("SJRS").cast(DataTypes.LongType));
        vsjxxDF.printSchema();
//        vsjxxDF.show(10);

        //转化为目标表结构
        Dataset<TMediationCase> tcDF = vsjxxDF
                .map(new ConvertToTMediationCase(), Encoders.bean(TMediationCase.class));

        tcDF.show(10);
        tcDF.write().mode(SaveMode.Append).jdbc(DataSource.HHM.getUrl(), targetTableName, hhm_mysqlProperties());
    }

    public static TMediationCase convertToTMediationCase(VSjxx vsjxx) {
        TMediationCase tMediationCase = new TMediationCase();
        tMediationCase.setResource_id(vsjxx.getID());
        //创建时间
        tMediationCase.setCreate_time(DateUtils.strToTsSFM(vsjxx.getCJSJ()));
        //修改时间
        tMediationCase.setUpdate_time(DateUtils.strToTsSFM(vsjxx.getGXSJ()));
        //纠纷编号
        tMediationCase.setCase_num(vsjxx.getAJBH());
        //纠纷描述
        tMediationCase.setCase_description(vsjxx.getAJMS());
        //纠纷诉求
        tMediationCase.setRequest("-");
        //调解方式
        tMediationCase.setMethod(2);
        //证据材料描述
        tMediationCase.setEvidence_description("-");
        //纠纷类型
        if(StringUtils.isAllBlank(vsjxx.getAJXL())){
            tMediationCase.setCase_type(vsjxx.getAJXL());
        }else{
            tMediationCase.setCase_type("-");
        }
        //纠纷发生地
        tMediationCase.setPlace_detail(vsjxx.getFSDD());
        //纠纷发生日期
        tMediationCase.setOccurrence_time(DateUtils.strToTsSF(vsjxx.getFSSJ()));
        //创建人ID
        tMediationCase.setCreate_user_id(10101L);
        //创建人姓名
        tMediationCase.setCreate_user_name(vsjxx.getCJRXM());
        //文书状态
        tMediationCase.setDoc_status(0);
        //调解结果
        if(StringUtils.isAllBlank(vsjxx.getTJZT())){
            tMediationCase.setResult(Integer.valueOf(vsjxx.getTJZT()));
        }else{
            tMediationCase.setResult(1);
        }
        //纠纷状态 先处理办理状态 再处理调整状态
        String blzt = vsjxx.getBLZT();
        String sjzt = vsjxx.getSJZT();
        if(StringUtils.isAllBlank(blzt)){
            if(0 == Integer.parseInt(blzt)){
                tMediationCase.setStatus(1);
            } else if(2 == Integer.parseInt(blzt)){
                tMediationCase.setStatus(4);
            }else{
                if(StringUtils.isAllBlank(sjzt)){
                    if(1 == Integer.parseInt(sjzt) || 2 == Integer.parseInt(sjzt) || 3 == Integer.parseInt(sjzt)){
                        tMediationCase.setStatus(2);
                    }else if(4 == Integer.parseInt(sjzt)){
                        tMediationCase.setStatus(7);
                    }else if(5 == Integer.parseInt(sjzt) || 6 == Integer.parseInt(sjzt) || 7 == Integer.parseInt(sjzt) || 8 == Integer.parseInt(sjzt)){
                        tMediationCase.setStatus(4);
                    }
                }
            }
        }
        //纠纷来源 1为警民联调
        tMediationCase.setCase_source(1);

        // 设置其他属性
        return tMediationCase;
    }
    public static class ConvertToTMediationCase implements Function1<VSjxx, TMediationCase>, Serializable {
        @Override
        public TMediationCase apply(VSjxx vsjxx) {
            TMediationCase tMediationCase = new TMediationCase();
            tMediationCase.setResource_id(vsjxx.getID());
            //创建时间
            tMediationCase.setCreate_time(DateUtils.strToTsSFM(vsjxx.getCJSJ()));
            //修改时间
            tMediationCase.setUpdate_time(DateUtils.strToTsSFM(vsjxx.getGXSJ()));
            //纠纷编号
            tMediationCase.setCase_num(vsjxx.getAJBH());
            //纠纷描述
            tMediationCase.setCase_description(vsjxx.getAJMS());
            //纠纷诉求
            tMediationCase.setRequest("-");
            //调解方式
            tMediationCase.setMethod(2);
            //证据材料描述
            tMediationCase.setEvidence_description("-");
            //纠纷类型
            if(StringUtils.isAllBlank(vsjxx.getAJXL())){
                tMediationCase.setCase_type(vsjxx.getAJXL());
            }else{
                tMediationCase.setCase_type("-");
            }
            //纠纷发生地
            tMediationCase.setPlace_detail(vsjxx.getFSDD());
            //纠纷发生日期
            tMediationCase.setOccurrence_time(DateUtils.strToTsSF(vsjxx.getFSSJ()));
            //创建人ID
            tMediationCase.setCreate_user_id(10101L);
            //创建人姓名
            tMediationCase.setCreate_user_name(vsjxx.getCJRXM());
            //文书状态
            tMediationCase.setDoc_status(0);
            //调解结果
//            if(StringUtils.isAllBlank(vsjxx.getTJZT())){
//                tMediationCase.setResult(Integer.valueOf(vsjxx.getTJZT()));
//            }else{
//                tMediationCase.setResult(1);
//            }
            if (StringUtils.isBlank(vsjxx.getTJZT())) {
                tMediationCase.setResult(1);  //todo 检查确认
            } else {
                try {
                    tMediationCase.setResult(Integer.parseInt(vsjxx.getTJZT()));
                } catch (NumberFormatException e) {
                    // 处理转换异常，例如设定一个默认值或者抛出自定义异常
                }
            }

            //纠纷状态 先处理办理状态 再处理调整状态
            String blzt = vsjxx.getBLZT();
            String sjzt = vsjxx.getSJZT();
            if(StringUtils.isAllBlank(blzt)){
                if(0 == Integer.parseInt(blzt)){
                    tMediationCase.setStatus(1);
                } else if(2 == Integer.parseInt(blzt)){
                    tMediationCase.setStatus(4);
                }else{
                    if(StringUtils.isAllBlank(sjzt)){
                        if(1 == Integer.parseInt(sjzt) || 2 == Integer.parseInt(sjzt) || 3 == Integer.parseInt(sjzt)){
                            tMediationCase.setStatus(2);
                        }else if(4 == Integer.parseInt(sjzt)){
                            tMediationCase.setStatus(7);
                        }else if(5 == Integer.parseInt(sjzt) || 6 == Integer.parseInt(sjzt) || 7 == Integer.parseInt(sjzt) || 8 == Integer.parseInt(sjzt)){
                            tMediationCase.setStatus(4);
                        }
                    }
                }
            }
            //纠纷来源 1为警民联调
            tMediationCase.setCase_source(1);

            // 设置其他属性
            return tMediationCase;
        }
    }

}
