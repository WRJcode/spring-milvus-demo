package org.alvin.utils;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.partition.ShowPartitionsParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("ALL")
@Component
public class MivusUtils {


    @Autowired
    MilvusServiceClient milvusServiceClient;

    /**
     * 同步搜索milvus
     * @param collectionName 表名
     * @param vectors 查询向量
     * @param topK 最相似的向量个数
     * @return
     */
    public List<Long> search(String collectionName, List<List<Float>> vectors, Integer topK) {

        Assert.notNull(collectionName, "collectionName  is null");
        Assert.notNull(vectors, "vectors is null");
        Assert.notEmpty(vectors, "vectors is empty");
        Assert.notNull(topK, "topK is null");
        int nprobeVectorSize = vectors.get(0).size();
        String paramsInJson = "{\"nprobe\": " + nprobeVectorSize + "}";
        SearchParam searchParam =
                SearchParam.newBuilder().withCollectionName(collectionName)
                        .withParams(paramsInJson)
                        .withMetricType(MetricType.L2)
                        .withVectors(vectors)
                        .withVectorFieldName("vector")
                        .withTopK(topK)
                        .build();

        R<SearchResults> searchResultsR = milvusServiceClient.search(searchParam);
        SearchResults searchResultsRData = searchResultsR.getData();
        List<Long> topksList = searchResultsRData.getResults().getIds().getIntId().getDataList();
        return topksList;
    }

    /**
     * 同步搜索milvus
     * @param collectionName 表名
     * @param vectors 查询向量
     * @param topK 最相似的向量个数
     * @return
     */
    public List<Long> search1(String collectionName, List<List<Float>> vectors, Integer topK) {

        Assert.notNull(collectionName, "collectionName  is null");
        Assert.notNull(vectors, "vectors is null");
        Assert.notEmpty(vectors, "vectors is empty");
        Assert.notNull(topK, "topK is null");
        int nprobeVectorSize = vectors.get(0).size();
        String paramsInJson = "{\"nprobe\": " + nprobeVectorSize + "}";
        SearchParam searchParam =
                SearchParam.newBuilder().withCollectionName(collectionName)
                        .withParams(paramsInJson)
                        .withMetricType(MetricType.IP)
                        .withVectors(vectors)
                        .withVectorFieldName("embedding")
                        .withTopK(topK)
                        .build();

        R<SearchResults> searchResultsR = milvusServiceClient.search(searchParam);
        SearchResults searchResultsRData = searchResultsR.getData();
        List<Long> topksList = searchResultsRData.getResults().getIds().getIntId().getDataList();
        return topksList;
    }


    /**
     * 同步搜索milvus，增加过滤条件搜索
     *
     * @param collectionName 表名
     * @param vectors 查询向量
     * @param topK 最相似的向量个数
     * @param exp 过滤条件：status=1
     * @return
     */
    public List<Long> search2(String collectionName, List<List<Float>> vectors, Integer topK, String exp) {
        Assert.notNull(collectionName, "collectionName  is null");
        Assert.notNull(vectors, "vectors is null");
        Assert.notEmpty(vectors, "vectors is empty");
        Assert.notNull(topK, "topK is null");
        Assert.notNull(exp, "exp is null");
        int nprobeVectorSize = vectors.get(0).size();
        String paramsInJson = "{\"nprobe\": " + nprobeVectorSize + "}";
        SearchParam searchParam =
                SearchParam.newBuilder().withCollectionName(collectionName)
                        .withParams(paramsInJson)
                        .withMetricType(MetricType.IP)
                        .withVectors(vectors)
                        .withExpr(exp)
                        .withVectorFieldName("embedding")
                        .withTopK(topK)
                        .build();

        R<SearchResults> searchResultsR = milvusServiceClient.search(searchParam);
        SearchResults searchResultsRData = searchResultsR.getData();
        List<Long> topksList = searchResultsRData.getResults().getIds().getIntId().getDataList();
        return topksList;
    }

    /**
     * 异步搜索milvus
     *
     * @param collectionName 表名
     * @param vectors 查询向量
     * @param partitionList 最相似的向量个数
     * @param topK
     * @return
     */
    public List<Long> searchAsync(String collectionName, List<List<Float>> vectors,
                                  List<String> partitionList, Integer topK) throws ExecutionException, InterruptedException {

        Assert.notNull(collectionName, "collectionName  is null");
        Assert.notNull(vectors, "vectors is null");
        Assert.notEmpty(vectors, "vectors is empty");
        Assert.notNull(partitionList, "partitionList is null");
        Assert.notEmpty(partitionList, "partitionList is empty");
        Assert.notNull(topK, "topK is null");
        int nprobeVectorSize = vectors.get(0).size();
        String paramsInJson = "{\"nprobe\": " + nprobeVectorSize + "}";
        SearchParam searchParam =
                SearchParam.newBuilder().withCollectionName(collectionName)
                        .withParams(paramsInJson)
                        .withVectors(vectors)
                        .withTopK(topK)
                        .withPartitionNames(partitionList)
                        .build();
        ListenableFuture<R<SearchResults>> listenableFuture = milvusServiceClient.searchAsync(searchParam);

        List<Long> resultIdsList = listenableFuture.get().getData().getResults().getTopksList();

        return resultIdsList;
    }

    /**
     * 获取分区集合
     * @param collectionName 表名
     * @return
     */
    public List<String> getPartitionsList(String collectionName) {
        Assert.notNull(collectionName, "collectionName  is null");
        ShowPartitionsParam searchParam = ShowPartitionsParam.newBuilder().withCollectionName(collectionName).build();
        List<ByteString> byteStrings = milvusServiceClient.showPartitions(searchParam).getData().getPartitionNamesList().asByteStringList();
        List<String> partitionList = Lists.newLinkedList();
        byteStrings.forEach(s -> {
            partitionList.add(s.toStringUtf8());
        });
        return partitionList;
    }



}
