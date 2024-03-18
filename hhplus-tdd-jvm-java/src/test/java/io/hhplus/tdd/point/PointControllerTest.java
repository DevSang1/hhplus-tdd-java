package io.hhplus.tdd.point;

import io.hhplus.tdd.point.controller.PointController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class PointControllerTest {
    private PointController pointController = new PointController();
    private Long userId = 1L;
    // 동시성에 대한 테스트 케이스
    // 포인트 충전중 동시에 여러건들이 들어올 경우를 파악해야함
    @Test
    void 포인트충전_후_부족으로_구매불가() {
        //given
        pointController.charge(userId, 50L);

        //when & then
        assertThrows(InterruptedException.class, () -> {
            pointController.use(userId, 50L);
        });
    }

    @Test
    void 포인트로_구매_성공() {
        //given
        pointController.charge(userId, 200L); // 사용자에게 200포인트 충전

        //when
        UserPoint result = pointController.use(userId, 150L); // 150포인트 사용

        //then
        assertEquals(50L, result.getPoint());
    }

    @Test
    void history() {
    }

    @Test
    void charge() {
    }

    @Test
    void use() {
    }
}