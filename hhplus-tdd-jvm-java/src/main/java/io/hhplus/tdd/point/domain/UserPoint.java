package io.hhplus.tdd.point.domain;

public record UserPoint(
        Long id,
        Long point,
        Long updateMillis
) {
    public UserPoint use(Long amount) {
        if (this.point < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        return new UserPoint(this.id, this.point - amount, System.currentTimeMillis());
    }

    public UserPoint charge(Long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("충전하실 금액은 양수여야 합니다.");
        }

        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }
}
