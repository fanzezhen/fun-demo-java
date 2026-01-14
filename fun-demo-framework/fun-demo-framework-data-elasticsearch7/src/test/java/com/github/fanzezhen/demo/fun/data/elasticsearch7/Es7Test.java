package com.github.fanzezhen.demo.fun.data.elasticsearch7;

import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.RangeAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.alibaba.fastjson2.JSON;
import com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.dao.EnterpriseEsDao;
import com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.entity.EnterpriseAggregation;
import com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.entity.EnterpriseDocument;
import com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.entity.RegCapAggregation;
import com.github.fanzezhen.demo.fun.data.elasticsearch7.enums.RegCapRangeEnum;
import com.github.fanzezhen.fun.framework.core.data.template.ITemplate;
import com.github.fanzezhen.fun.framework.core.model.result.PageResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.model.ISearchResult;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.template.IElasticsearchTemplate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单元测试
 */
@Rollback
@Slf4j
@Disabled("手动触发")
@SpringBootTest
class Es7Test {
    @Resource
    private EnterpriseEsDao enterpriseEsDao;
    @Resource
    protected IElasticsearchTemplate elasticsearchTemplate;

    @Test
    void testGet() {
        EnterpriseDocument document = enterpriseEsDao.get(EnterpriseDocument::getName, "中国工商银行股份有限公司肥城高余支行");
        log.debug("result for get is {}", JSON.toJSONString(document));
        Assertions.assertNotNull(document);
    }

    @Test
    void testGetById() {
        EnterpriseDocument document = enterpriseEsDao.getById("ed771bd6-3120-450c-a0a2-b2763140ea98");
        log.debug("result for getById is {}", JSON.toJSONString(document));
        Assertions.assertNotNull(document);
    }

    @Test
    void testListByIds() {
        List<String> idList = List.of("ed771bd6-3120-450c-a0a2-b2763140ea98", "be5f69b3-e391-481b-a395-6d83c03d3ae6");
        List<EnterpriseDocument> result = enterpriseEsDao.listByIds(idList);
        log.debug("result for listByIds is {}", JSON.toJSONString(result));
        Assertions.assertNotNull(result);
    }

    @Test
    void testScrollSearch() {
        // 创建 SearchRequest.Builder 对象用于游标查询
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        TermQuery.Builder builder = new TermQuery.Builder()
            .field(ITemplate.getColumnName(EnterpriseDocument::getBusinessStatusCode))
            .value("1");
        TermQuery termQuery = builder.build();
        boolQuery.must(termQuery._toQuery());
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
            .query(q -> q.bool(boolQuery.build()))
            .size(3);
        // 第一次滚动查询
        ISearchResult<EnterpriseDocument> result = enterpriseEsDao.scrollSearch(searchBuilder, 60L, null);
        log.debug("First scroll result: {}", JSON.toJSONString(result.asList()));
        // 如果有滚动ID，可以继续获取下一批数据
        if (result.getScrollId() != null) {
            ISearchResult<EnterpriseDocument> nextResult = enterpriseEsDao.scrollSearch(null, 1L, result.getScrollId());
            log.debug("Second scroll result: {}", JSON.toJSONString(nextResult.asList()));
            Assertions.assertEquals(result.getTotalHits(), nextResult.getTotalHits());
        }
    }

    /**
     * 测试高亮和普通搜索 构造相同的请求分别请求普通search和mSearch 对比结果应该是一致的
     */
    @Test
    void mSearch() {

        SearchRequest.Builder builder = xiaomiHighlightSearch();
        SearchRequest.Builder builder2 = xiaomiHighlightSearch();

        List<ISearchResult<EnterpriseDocument>> searchResults = enterpriseEsDao.mSearch(Arrays.asList(builder, builder2));
        Assertions.assertNotNull(searchResults);
        List<EnterpriseDocument> documents = new ArrayList<>();
        for (ISearchResult<EnterpriseDocument> searchResult : searchResults) {
            EnterpriseDocument enterpriseDocument = searchResult.asDocument();
            documents.add(enterpriseDocument);
        }
        for (EnterpriseDocument document : documents) {
            Assertions.assertNotNull(document);
            Assertions.assertNotNull(document.getHighlightFieldMap());
        }
        log.info("企业信息:{}", documents);
    }

    /**
     * 测试聚合搜索和普通搜索应该是完全一致的
     */
    @Test
    void mSearchAgg() {
        SearchRequest.Builder searchBuilder1 = getRegRmbAggSearch(RegCapRangeEnum.values());

        SearchRequest.Builder searchBuilder2 = getRegRmbAggSearch(RegCapRangeEnum.values());
        SearchRequest.Builder searchBuilder3 = getRegRmbAggSearch(RegCapRangeEnum.values());

        ISearchResult<RegCapAggregation> search = elasticsearchTemplate.search(searchBuilder1, RegCapAggregation.class);
        RegCapAggregation regCapAggregation1 = search.asAggregations();

        List<ISearchResult<RegCapAggregation>> searchResults = elasticsearchTemplate.mSearch(
            Arrays.asList(searchBuilder2, searchBuilder3),
            RegCapAggregation.class);
        if (regCapAggregation1 != null && searchResults != null) {
            for (ISearchResult<RegCapAggregation> searchResult : searchResults) {
                RegCapAggregation regCapAggregation2 = searchResult.asAggregations();
                log.debug("RegCapAggregation = {}", JSON.toJSONString(regCapAggregation2));
                Assertions.assertEquals(regCapAggregation1, regCapAggregation2);
            }
        }
    }

    /**
     * 测试分页搜索，构造相同的请求分别请求普通search和mSearch 对比结果应该是一致的
     */
    @Test
    void mSearchPageTest() {
        SearchRequest.Builder search1 = xiaomiHighlightSearch();
        search1.from(1).size(3);

        SearchRequest.Builder search2 = xiaomiHighlightSearch();
        search2.from(1).size(3);

        SearchRequest.Builder search3 = xiaomiHighlightSearch();
        search3.from(1).size(3);

        ISearchResult<EnterpriseDocument> pageResult = elasticsearchTemplate.search(search1, EnterpriseDocument.class);

        List<SearchRequest.Builder> requestBuilders = Arrays.asList(search2, search3);
        List<ISearchResult<EnterpriseDocument>> searchResults = elasticsearchTemplate.mSearch(
            requestBuilders,
            EnterpriseDocument.class);
        if (pageResult != null && searchResults != null) {
            PageResult<EnterpriseDocument> asPageResult = pageResult.asPageResult(1L, 3L);
            for (ISearchResult<EnterpriseDocument> mSearchResult : searchResults) {
                PageResult<EnterpriseDocument> mOnePage = mSearchResult.asPageResult(1L, 3L);
                Assertions.assertEquals(asPageResult.getTotal(), mOnePage.getTotal());
                List<EnterpriseDocument> aRows = asPageResult.getRowList();
                List<EnterpriseDocument> bRows = mOnePage.getRowList();
                Assertions.assertEquals(aRows, bRows);
                if (aRows != null && bRows != null) {
                    int size = aRows.size();
                    for (int i = 0; i < size; i++) {
                        Assertions.assertEquals(aRows.get(i), bRows.get(i));
                    }
                }
            }
        }
    }

    @Test
    void agg() {
        final SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
            .size(0)
            .aggregations("business_status", agg -> agg
                .terms(t -> t.field("business_status")));
        ISearchResult<EnterpriseAggregation> result = this.elasticsearchTemplate.search(searchRequestBuilder, EnterpriseAggregation.class);
        log.info("asAggregation: {}", JSON.toJSONString(result.asAggregations()));
    }


    private static SearchRequest.Builder xiaomiHighlightSearch() {
        Map<String, HighlightField> highLightMap = new HashMap<>();
        highLightMap.put("name_st", new HighlightField.Builder().fragmentSize(1000).numberOfFragments(0).build());

        SearchRequest.Builder builder = new SearchRequest.Builder();

        builder.size(1).query(q ->
                q.bool(
                    b -> b.mustNot(
                        m -> m.term(t -> t.field("deprecated").value("D")
                        )
                    ).should(
                        s -> s.match(t -> t.field("name").query("平安银行股份有限公司深圳深圳湾支行"))
                    )
                )
            ).source(e -> e.filter(f -> f.includes(Arrays.asList("name", "name_st"))))
            .highlight(h -> h
                .preTags("<em>")
                .postTags("</em>")
                .fields(highLightMap)
            );
        return builder;
    }

    private static SearchRequest.Builder getRegRmbAggSearch(RegCapRangeEnum[] values) {
        SearchRequest.Builder searchBuilder1 = new SearchRequest.Builder();
        searchBuilder1.size(0);
        //聚合查询区间
        for (RegCapRangeEnum rangeEnum : values) {
            RangeAggregation.Builder rb = new RangeAggregation.Builder();
            rb.field("reg_cap_rmb").ranges(r -> {
                AggregationRange.Builder builder = r.key(rangeEnum.name());
                if (rangeEnum.getFrom() != null) {
                    builder.from(rangeEnum.getFrom());
                }
                if (rangeEnum.getTo() != null) {
                    builder.to(rangeEnum.getTo());
                }
                return builder;
            });
            searchBuilder1.aggregations(rangeEnum.name(), agg -> agg.range(rb.build()));
        }
        searchBuilder1.aggregations("allValue", t -> t.valueCount(s -> s.field("reg_cap_rmb")));
        return searchBuilder1;
    }

}
