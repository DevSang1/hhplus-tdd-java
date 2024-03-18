package io.hhplus.tdd.point;

public record UserPoint(
        Long id,
        Long point,
        Long updateMillis
) {
    public long getPoint() {
        return 0;
    }
}
