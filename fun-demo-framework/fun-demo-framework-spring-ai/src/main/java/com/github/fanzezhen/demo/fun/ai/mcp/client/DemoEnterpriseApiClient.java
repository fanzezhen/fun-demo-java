package com.github.fanzezhen.demo.fun.ai.mcp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Demo 企业信息 API 客户端
 * 负责与外部企业信息服务进行 HTTP 通信
 *
 * @author fanzezhen
 * @since 2026-05-12
 */
@Component
public class DemoEnterpriseApiClient {

    private static final Logger log = LoggerFactory.getLogger(DemoEnterpriseApiClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public DemoEnterpriseApiClient(
            @Qualifier("demoRestClient") RestClient restClient,
            ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 调用 Demo 企业信息 API
     *
     * @param api         API 端点
     * @param requestBody 请求体
     * @param apiKey      API 密钥（透传给下游 API）
     * @return 响应 JSON（已解包 envelope）
     */
    public JsonNode post(DemoEnterpriseApi api, Object requestBody, String apiKey) {
        if (log.isDebugEnabled()) {
            log.debug("API 请求: path={} body={}", api.path(), formatPayload(requestBody));
        }

        try {
            // 使用 String 类型接收响应，避免 Jackson 版本冲突
            var response = this.restClient.post()
                    .uri(api.path())
                    .header("x-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(String.class);

            if (log.isDebugEnabled()) {
                log.debug("API 响应: path={} status={} body={}",
                        api.path(),
                        response.getStatusCode().value(),
                        abbreviate(response.getBody(), 500));
            }

            // 手动解析 JSON 字符串
            String responseBody = response.getBody();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return null;
            }

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return unwrapEnvelope(jsonNode);
        } catch (RestClientResponseException ex) {
            var body = ex.getResponseBodyAsString();
            log.warn("API 调用失败: path={} status={} body={}",
                    api.path(),
                    ex.getStatusCode().value(),
                    abbreviate(body, 500));
            throw new ServiceException(
                    String.format("API 调用失败: %s (HTTP %d)", api.path(), ex.getStatusCode().value()),
                    ex);
        } catch (Exception ex) {
            log.error("API 调用异常: path={}", api.path(), ex);
            throw new ServiceException("API 调用失败: " + api.path(), ex);
        }
    }

    /**
     * 解包 API 响应的 envelope
     * 通常 API 返回格式: {"code": 200, "message": "success", "data": {...}}
     */
    private JsonNode unwrapEnvelope(JsonNode response) {
        if (response == null) {
            return null;
        }
        // 如果有 data 字段，返回 data；否则返回整个响应
        if (response.has("data")) {
            return response.get("data");
        }
        return response;
    }

    /**
     * 格式化负载用于日志输出
     */
    private String formatPayload(Object payload) {
        if (payload == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return payload.toString();
        }
    }

    /**
     * 缩略字符串（避免日志过长）
     */
    private String abbreviate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength) + "...";
    }
}
