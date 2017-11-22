create external table if not exists sougoulog (
 visittime STRING,
 userid STRING,
 keyword STRING,
 rankresult BIGINT,
 rankclick INT,
 URL STRING
)ROW FORMAT DELIMITED
 FIELDS TERMINATED BY ' '
 location 'hdfs://master:9000/test/sparkTest'
 ;


select substring(visittime,5) , count(distinct(userid)),count(userid) from sougoulog group by substring(visittime,5);