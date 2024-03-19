package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.TransactionType;

public record PointHistory(
        Long id,
        Long userId,
        TransactionType type,
        Long amount,
        Long timeMillis
) {
    //포인트 사용 히스토리
    public static PointHistory usePoint(Long id, Long userId, Long amount) {
        return new PointHistory(id, userId, TransactionType.USE, amount, System.currentTimeMillis());
    }
    //포인트 충전 히스토리
    public static PointHistory chargePoint(Long id, Long userId, Long amount) {
        return new PointHistory(id, userId, TransactionType.USE, amount, System.currentTimeMillis());
    }
}
