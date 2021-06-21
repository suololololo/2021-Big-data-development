package com.jiajun

/**
  * @Author: jiajun
  * @Date: 2021-06-15 10:40
  */

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
object Main {
  val target="b"
  def main(args: Array[String]) {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //Linux or Mac:nc -l 9999
    //Windows:nc -l -p 9999
    val text = env.socketTextStream("localhost", 9999)
//    val stream = text.windowAll(TumblingEventTimeWindows.of(Time.minutes(1)))
////    println(stream)
    val count = 0
//    val stream = text.flatMap {
//      _.toLowerCase.split("\\W+") filter {
//        _.contains(target)
//      }
//    }.map {
//      ("发现目标："+_)
//    }.windowAll(TumblingEventTimeWindows.of(Time.minutes(1))).sum(0).print()

    val stream = text.flatMap(_.toLowerCase().split("\\W+").filter(_.contains(target)
    )).map((_,1)).keyBy(0).timeWindow(Time.minutes(1)).sum(1).print().setParallelism(1)

    env.execute("Window Stream WordCount")
  }
}

