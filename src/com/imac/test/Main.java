package com.imac.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// if (args.length < 2) {
		// System.exit(1);
		// }

		SparkConf conf = new SparkConf();
		conf.setAppName("HWSpark");
//		conf.setMaster("yarn-cluster");

		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> file = sc.textFile(args[0]);

		// TODO:使用map找出測試資料所有英文字母，並存至/spark/homework/map
		JavaRDD<String> map = file.map(new Function<String, String>() {

			public String call(String arg0) throws Exception {
				return arg0.split(",")[0];
			}
		});

		map.saveAsTextFile("/spark/homework/map");

		// TODO:使用flatmap找出測試資料所有以”,”切割的資料，並存至/spark/homework/flatMap
		JavaRDD<String> flatmap = file
				.flatMap(new FlatMapFunction<String, String>() {

					public Iterable<String> call(String arg0) throws Exception {
						return Arrays.asList(arg0.split(","));
					}
				});

		flatmap.saveAsTextFile("/spark/homework/flatMap");

		// TODO:使用filter找出測試資料所有以123與456的資料，並存至/spark/homework/filter
		JavaRDD<String> filter = flatmap
				.filter(new Function<String, Boolean>() {

					public Boolean call(String arg0) throws Exception {
						if (arg0.contains("123") || arg0.contains("456")) {
							return true;
						}
						return false;
					}
				});

		filter.saveAsTextFile("/spark/homework/filter");

		// TODO:使用mapToPair將測試資料轉換成(str, 1)，並存至/spark/homework/mapPair
		JavaPairRDD<String, Integer> mapToPair = flatmap
				.mapToPair(new PairFunction<String, String, Integer>() {

					public Tuple2<String, Integer> call(String arg0)
							throws Exception {
						return new Tuple2<String, Integer>(arg0, 1);
					}
				});

		mapToPair.saveAsTextFile("/spark/homework/mapPair");

		// TODO:使用flatMapToPair將測試資料轉換成(字母, 所有後面數字的sum)，並存至/spark/homework/filter_output
		JavaPairRDD<String, Integer> flatMapToPair = file
				.flatMapToPair(new PairFlatMapFunction<String, String, Integer>() {

					public Iterable<Tuple2<String, Integer>> call(String arg0)
							throws Exception {
						String[] word = arg0.split(",");
						int sum = 0;

						ArrayList<Tuple2<String, Integer>> arrayList = new ArrayList<Tuple2<String, Integer>>();

						for (int i = 0; i < word.length - 1; i++) {
							sum = sum + Integer.valueOf(word[i + 1]);
						}
						arrayList
								.add(new Tuple2<String, Integer>(word[0], sum));
						return arrayList;
					}
				});

		flatMapToPair.saveAsTextFile("/spark/homework/filter_output");

		// TODO:使用groupBy找出測試資料中大於500的資料，若無法辨識分到”None”，並存至/spark/homework/groupBy (for迴圈會遇到特殊邏輯，碰到一次return即會當作結束)
		JavaPairRDD<String, Iterable<String>> groupBy = flatmap
				.groupBy(new Function<String, String>() {

					public String call(String arg0) throws Exception {
						if (!checkType(arg0)) {
							return "None";
						} else {
							return (Integer.parseInt(arg0) < 500) ? "小於500"
									: "大於500";
						}
					}
				});

		groupBy.saveAsTextFile("/spark/homework/groupBy");

		// TODO:使用reduce找出測試資料所有英文字母，並用reduce將之append成一個字串
		String reduce = map.reduce(new Function2<String, String, String>() {

			public String call(String arg0, String arg1) throws Exception {
				// TODO Auto-generated method stub
				return arg0 + arg1;
			}
		});

		List<String> list = new ArrayList();
		list.add(reduce);
		JavaRDD<String> reduceResult = sc.parallelize(list, 1);
		reduceResult.saveAsTextFile("/spark/homework/reduce");

		// TODO:使用reduceByKey找出以”,”切割的所有wordcount，並存至/spark/homework/reduceByKey
		JavaPairRDD<String, Integer> reduceByKey = mapToPair
				.reduceByKey(new Function2<Integer, Integer, Integer>() {

					public Integer call(Integer arg0, Integer arg1)
							throws Exception {
						return arg0 + arg1;
					}
				});

		reduceByKey.saveAsTextFile("/spark/homework/reduceByKey");
	}

	private static boolean checkType(String arg0) {
		// if(java.lang.Character.isDigit(arg0.charAt(0))){
		// return true;
		// }else{
		// return false;
		// }
		try {
			Integer.parseInt(arg0);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
