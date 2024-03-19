package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class PointControllerTest {
    // 동시성에 대한 테스트 케이스
    // 포인트 충전중 동시에 여러건들이 들어올 경우를 파악해야함

    private UserPointTable userPointTable;

    @BeforeEach
    void setUp(){
        userPointTable = new UserPointTable();
    }

    @Test
    void 잔고_부족으로_포인트사용_불가() {
        //given
        UserPoint userPoint = new UserPoint(1L, 100L, 0L);
        long paymentAmount = 150L;

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            userPoint.use(paymentAmount);
        });
    }

    @Test
    void 충전_성공() throws InterruptedException {
        //given
        Long userId = 1L;
        Long myAmount = 100L;
        Long chargeAmount = 200L;

        //when & then
        userPointTable.insertOrUpdate(userId, myAmount);
        UserPoint updatedUserPoint = userPointTable.insertOrUpdate(userId, myAmount + chargeAmount);

        assertEquals(myAmount + chargeAmount, updatedUserPoint.point());
    }

    @Test
    void 음수인경우_충전_실패() {
        //given
        Long userId = 1L;
        Long myAmount = 100L;
        Long chargeAmount = -10L;

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            userPointTable.insertOrUpdate(userId, myAmount + chargeAmount);
        });

    }
}