package com.aura.spark.core

import com.aura.config.Config
import com.aura.util.SparkUtil
import org.apache.spark.sql.SparkSession

object SogouTest {
  def main(args: Array[String]): Unit = {
    /**
      * 获得SparkContext
      */
    val sc = SparkUtil.getSparkContext(this.getClass)
    /**
      * 读取日志
      */
    val rdd = sc.textFile("spark-project/analysis/data/test/SogouQ.sample")
        .map(_.split("\t"))
      .map(x=>(x(0).toString.take(5),x(1)));
//    rdd.foreach(println)
//    val result = rdd.map(x=>(x._1,(Set(x._2),1))).reduceByKey((m, n) => {
//      (m._1.toSet.++(n._1.toSet),m._2+n._2)
//    }).map(x=>{(x._1,x._2._1.toSet.size,x._2._2)})
//    result.foreach(println)


    //老师的实现
      val resutlt11=rdd.groupByKey().mapValues(x=>{(x.size,x.toSet.size)})
    resutlt11.foreach(println)

    //=====================使用df ds==================
    val spark = SparkSession
      .builder()
      .appName("sogouTest")
      .config(SparkUtil.getSparkConf(this.getClass))
      .getOrCreate()
    import spark.implicits._
    val df=spark.read.textFile("spark-project/analysis/data/test/SogouQ.sample")
      .map(_.split("\t"))
      .map(x=>(x(0).toString.take(5),x(1)))
        .toDF("time","userid")
    df.createOrReplaceTempView("sogoutest")
    val result2=spark.sql("select time, count(userid) ,count(distinct(userid)) from sogoutest group by time");
    result2.show()

    //老师的版本----使用【registerTempTable】
//    df.registerTempTable("sogoutest")
    //========createOrReplaceTempView和他的二者的区别：用完即毁掉；create可以存到hive中的。

//    val result3=df.groupBy("time").agg("userid"->"count").show();
    import org.apache.spark.sql.functions._
      val result4=df.groupBy("time").agg(count("userid"),countDistinct("userid")).show();
      //小技巧：按住啊ctrl键，查看源代码；在源代码中可以看到每个方法的使用示例。
    // 这里之前就有问题：写了agg，但是一直报错；因为需要导入引用的包。通过查看源代码，找到的包。

  }
}
