package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.NotEnoughPointException;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @Mock
    private UserPointTable userPointTable;
    @Mock
    private PointHistoryTable pointHistoryTable;
    private PointService pointService;

    @BeforeEach
    public void setUp() {
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    @Test
    @DisplayName("유저 ID를 가져오는 테스트 코드")
    public void testGetUserId() throws InterruptedException {
        long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 100L, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        UserPoint result = pointService.getUserId(userId);

        verify(userPointTable).selectById(userId);
        assertEquals(userPoint, result);
    }

    @Test
    @DisplayName("충전금액이 0이거나 음수일때 충전 실패하는 경우")
    public void ChargeFailure() {
        long userId = 1L;
        long chargeAmount = -50L; // 음수 금액

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.charge(userId, chargeAmount);
        });

        assertEquals("충전 금액을 다시 지정해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("포인트가 부족하여 구매가 실패하는 경우")
    public void PayFailure() {
        long userId = 1L;
        long purchaseAmount = 150L;
        when(userPointTable.selectById(userId)).thenReturn(new UserPoint(userId, 100L, System.currentTimeMillis()));

        Exception exception = assertThrows(NotEnoughPointException.class, () -> {
            pointService.usePoint(userId, purchaseAmount);
        });

        assertEquals("포인트가 부족하여 구매가 불가합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("포인트 사용시 이력 기록")
    public void PayThePointRecordHistory() throws InterruptedException {
        long userId = 1L;
        long purchaseAmount = 50L;
        when(userPointTable.selectById(userId)).thenReturn(new UserPoint(userId, 100L, System.currentTimeMillis()));

        pointService.usePoint(userId, purchaseAmount);

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> amountCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<TransactionType> typeCaptor = ArgumentCaptor.forClass(TransactionType.class);
        ArgumentCaptor<Long> timeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(pointHistoryTable).insert(userIdCaptor.capture(), amountCaptor.capture(), typeCaptor.capture(), timeCaptor.capture());

        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(-purchaseAmount, amountCaptor.getValue().longValue());
        assertEquals(TransactionType.PURCHASE, typeCaptor.getValue());
    }

    @Test
    @DisplayName("포인트 충전시 이력 기록")
    public void ChargeThePointRecordHistory() throws InterruptedException {
        long userId = 1L;
        long chargeAmount = 50L;
        when(userPointTable.selectById(userId)).thenReturn(new UserPoint(userId, 100L, System.currentTimeMillis()));
        when(userPointTable.insertOrUpdate(userId, 150L)).thenReturn(new UserPoint(userId, 150L, System.currentTimeMillis()));

        pointService.charge(userId, chargeAmount);

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> amountCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<TransactionType> typeCaptor = ArgumentCaptor.forClass(TransactionType.class);
        ArgumentCaptor<Long> timeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(pointHistoryTable).insert(userIdCaptor.capture(), amountCaptor.capture(), typeCaptor.capture(), timeCaptor.capture());

        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(chargeAmount, amountCaptor.getValue().longValue());
        assertEquals(TransactionType.CHARGE, typeCaptor.getValue());
    }
}