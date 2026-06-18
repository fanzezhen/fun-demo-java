package com.github.fanzezhen.demo.lowcode.maginapi;

import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ssssssss.magicapi.core.config.Constants;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link MagicApiResultProvider} 单测。
 * <p>
 * 重点回归一处历史缺陷：buildResult 早期无视入参 code 一律返回成功，
 * 导致 buildException 上报的错误码被错误地包装成成功响应。
 * </p>
 *
 * @since 4.1.0
 */
class MagicApiResultProviderTest {

    private final MagicApiResultProvider provider = new MagicApiResultProvider();

    @Test
    @DisplayName("成功码应返回成功响应并携带数据")
    void testBuildResult_SuccessCode_ShouldReturnSuccess() {
        Object data = "ok";
        Object result = provider.buildResult(null, Constants.RESPONSE_CODE_SUCCESS, Constants.RESPONSE_MESSAGE_SUCCESS, data);

        ActionResult<?> actionResult = assertInstanceOf(ActionResult.class, result);
        assertAll(
            () -> assertTrue(actionResult.isSuccess()),
            () -> assertEquals(data, actionResult.getData())
        );
    }

    @Test
    @DisplayName("非成功码应返回失败响应并携带错误信息")
    void testBuildResult_ErrorCode_ShouldReturnFailed() {
        Object result = provider.buildResult(null, 500, "系统内部出现错误", null);

        ActionResult<?> actionResult = assertInstanceOf(ActionResult.class, result);
        assertAll(
            () -> assertFalse(actionResult.isSuccess()),
            () -> assertFalse(actionResult.getErrors().isEmpty()),
            () -> assertEquals("系统内部出现错误", actionResult.getErrors().get(0).getMessage())
        );
    }

    @Test
    @DisplayName("异常应级联为失败响应而非被吞成成功")
    void testBuildException_ShouldReturnFailed() {
        Object result = provider.buildException(null, new RuntimeException("boom"));

        ActionResult<?> actionResult = assertInstanceOf(ActionResult.class, result);
        assertFalse(actionResult.isSuccess());
    }
}
