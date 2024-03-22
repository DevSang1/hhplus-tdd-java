package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;


public class PointService {

    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    @Autowired
    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPoint getUserId(Long userId) throws InterruptedException{
        return userPointTable.selectById(userId);
    }

    public UserPoint charge(Long userId, Long amount) throws InterruptedException{
        UserPoint userPoint = userPointTable.selectById(userId);
        long result = userPoint.point() + amount;

        if (result < 0) {
            throw new RuntimeException();
        }
        return userPointTable.insertOrUpdate(userPoint.id(), result);
    }

    public UserPoint usePoint(Long userId, Long amount) throws InterruptedException {
        UserPoint userPoint = userPointTable.selectById(userId);
        long result = userPoint.point() - amount;

        if (userPoint.point() < amount) {
            throw new RuntimeException();
        }
        return userPointTable.insertOrUpdate(userPoint.id(), result);
    }

    public List<PointHistory> userPointHistory(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
