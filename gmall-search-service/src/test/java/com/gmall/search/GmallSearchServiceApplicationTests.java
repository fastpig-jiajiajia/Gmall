package com.gmall.search;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.entity.PmsSearchSkuInfo;
import com.gmall.entity.PmsSkuInfo;
import com.gmall.service.SkuService;
import com.sun.media.sound.DLSInfo;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {

	@Reference
	SkuService skuService;// 查询mysql


	@Autowired
	JestClient jestClient;

	@Test
	public void contextLoads() throws IOException {
		put();
	}

	// 导入数据到 elastic search
	public void put() throws IOException {

		// 查询mysql数据
		List<PmsSkuInfo> pmsSkuInfoList = new ArrayList<>();

		pmsSkuInfoList = skuService.getAllSku("61");

		// 转化为es的数据结构
		List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

		for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
			PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();

			// 将一个类的值赋值到另一个类
			BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);

			pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));

			pmsSearchSkuInfos.add(pmsSearchSkuInfo);

		}

		// 导入es
		for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
			Index put = new Index.Builder(pmsSearchSkuInfo).index("gmall0105").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()+"").build();
			jestClient.execute(put);
		}

	}

	public void get() throws IOException {

		// jest的dsl工具
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// bool
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		// filter
		TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId","43");
		boolQueryBuilder.filter(termQueryBuilder);
		// must
		MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","华为");
		boolQueryBuilder.must(matchQueryBuilder);
		// query
		searchSourceBuilder.query(boolQueryBuilder);
		// from
		searchSourceBuilder.from(0);
		// size
		searchSourceBuilder.size(20);
		// highlight
		searchSourceBuilder.highlight(null);

		String dslStr = searchSourceBuilder.toString();

		System.err.println(dslStr);


		// 用api执行复杂查询
		List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

		Search search = new Search.Builder(dslStr).addIndex("gmall0105").addType("PmsSkuInfo").build();

		SearchResult execute = jestClient.execute(search);

		List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);

		for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
			PmsSearchSkuInfo source = hit.source;

			pmsSearchSkuInfos.add(source);
		}

		System.out.println(pmsSearchSkuInfos.size());
	}

	@Test
	public void searchTest() throws IOException {
		List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();
	try {
		String query = "{\n" +
				"  \"query\": {\n" +
				"    \"bool\": {\n" +
				"      \"filter\": [\n" +
				"        {\n" +
				"          \"terms\": {\n" +
				"            \"skuAttrValueList.valueId\": [\n" +
				"              \"39\",\n" +
				"              \"40\",\n" +
				"              \"41\"\n" +
				"            ]\n" +
				"          }\n" +
				"        },\n" +
				"        {\n" +
				"          \"term\": {\n" +
				"            \"skuAttrValueList.valueId\": \"43\"\n" +
				"          }\n" +
				"        }\n" +
				"      ],\n" +
				"      \"must\": [\n" +
				"        {\n" +
				"          \"match\": {\n" +
				"            \"skuName\": \"华为\"\n" +
				"          }\n" +
				"        }\n" +
				"      ]\n" +
				"    }\n" +
				"  }\n" +
				"}";

		// 拼写查询语句
		// jest 的 dsl 工具
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// bool
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		// filter
		TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", "43");
		boolQueryBuilder.filter(termQueryBuilder);
		//must
		MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "华为");
		boolQueryBuilder.must(matchQueryBuilder);
		// query
		searchSourceBuilder.query(boolQueryBuilder);
		// from
		searchSourceBuilder.from(0);
		// size
		searchSourceBuilder.size(20);
		// highlight
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field("skuName");
		searchSourceBuilder.highlight(highlightBuilder);

		// 转化成 string
		String dsl = searchSourceBuilder.toString();
		System.out.println(dsl);

		// 查询
		Search search = new Search.Builder(dsl).addIndex("gmall0105").addType("PmsSkuInfo").build();
		SearchResult searchResult = jestClient.execute(search);
		List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);

		for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
			PmsSearchSkuInfo source = hit.source;
			pmsSearchSkuInfoList.add(source);
		}
		System.out.println(pmsSearchSkuInfoList.size());
	}catch (Exception e){
		e.printStackTrace();
	}



	}

}
