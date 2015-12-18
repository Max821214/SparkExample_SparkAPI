## SparkExample SparkAPI IMAC - BigData Team - 2015/12/16

###問題描述
利用範例資料測試並使用幾項Spark API

###資料格式

>1.先在HDFS建立```/spark/homework```目錄
>2.利用```touch test.txt```，建立test.txt，再用```vim test.txt```將資料輸入，如下:

```
a,123,456,789,11344,2142,123  
b,1234,124,1234,123,123  
c,123,4123,5435,1231,5345
d,123,456,789,113,2142,143  
e,123,446,789,14,2142,113  
f,123,446,789,14,2142,1113,323
```

>2.在利用```hadoop fs -put test.txt /spark/homework```將資料上傳至HDFS

###問題分析

>1.使用map找出測試資料所有英文字母，並存至/spark/homework/map，結果如下:

```
a
b
c
d
e
f
```

***
>2.使用flatmap找出測試資料所有以”,”切割的資料，並存至/spark/homework/flatMap,結果如下:

```
a
123
456
789
11344
2142
123
b
1234
…
```

***
>3.使用filter找出測試資料所有以123與456的資料，並存至/spark/homework/filter，結果如下:

```
123
456
123
1234
1234
…
```

***

>4.使用mapToPair將測試資料轉換成(str, 1)，並存至/spark/homework/mapPair，結果如下:

```
(a,1)
(123,1)
(456,1)
(789,1)
(11344,1)
(2142,1)
(123,1)
…
```

***
>5.使用flatMapToPair將測試資料轉換成(字母, 所有後面數字的sum)，並存至/spark/homework/filter_output，結果如下：

```
(a,14977)
(b,2838)
(c,16257)
(d,3766)
(e,3627)
(f,4950)
…
```

***
>6.使用groupBy找出測試資料中大於500的資料，若無法辨識分到”None”，並存至/spark/homework/groupBy，結果如下：

```
(None,[a, b, c, d, e, f])
(小於 500,[123, 456, 123, 124, 123, 123, 123, 123,
456, 113, 143, 123, 446, 14, 113, 123, 446, 14, 323])
(大於 500,[789, 11344, 2142, 1234, 1234, 4123, 5435,
1231, 5345, 789, 2142, 789, 2142, 789, 2142, 1113])
…
```

***
>7.使用reduce找出測試資料所有英文字母，並用reduce將之append成一個字串，並存至/spark/homework/reduce，結果如下：

```
abcdef
```

**
當中因為reduce輸出為String，需要先加這筆字串加到類別為ArrayList裡，在利用JavaSparkContext中有項parallelize方法將結果轉成JavaRDD輸出
**

```java
	List<String> list = new ArrayList();
	list.add(reduce);
	JavaRDD<String> reduceResult = sc.parallelize(list, 1);
```
***
>8.使用reduceByKey找出以”,”切割的所有wordcount，並存至/spark/homework/reduceByKey，結果如下：

```
(d,1)
(1113,1)
(1231,1)
(e,1)
(14,2)
(113,2)
…
```

###執行分析

```
spark-submit --class com.imac.test.Main \
--master yarn-cluster HW.jar \
/spark/homework/test.txt \
```
> 第一行```--class```後面接```Java```的```package name```和```class name```  
> 第二行--master 為使用叢集模式，這邊採用yarn-cluster，後面接```Jar```  
> 第三行為```輸入資料```

###輸出結果
>分析成功後，可以使用```hadoop fs -cat /輸出路徑```指令列出結果，輸出路徑如下:

```
/spark/homework/map
/spark/homework/flatMap
/spark/homework/filter
/spark/homework/mapPair
/spark/homework/filter_output
/spark/homework/groupBy
/spark/homework/reduce
/spark/homework/reduceByKey
```