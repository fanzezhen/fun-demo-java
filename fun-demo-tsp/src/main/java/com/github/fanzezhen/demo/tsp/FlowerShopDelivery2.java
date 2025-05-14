package com.github.fanzezhen.demo.tsp;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 软窗口计算最短路线+时间惩罚
 */
@Slf4j
public class FlowerShopDelivery2 {

    // 成都市坐标点（模拟数据）
    static class Location {
        String name;
        double lat;
        double lng;
        int[] timeWindow; // [开始时间, 结束时间]（分钟表示，如540=9:00）
        int earlyPenalty; // 早到每分钟惩罚成本
        int latePenalty;  // 迟到每分钟惩罚成本

        Location(String name, double lat, double lng, int[] timeWindow,
                 int earlyPenalty, int latePenalty) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
            this.timeWindow = timeWindow;
            this.earlyPenalty = earlyPenalty;
            this.latePenalty = latePenalty;
        }
    }

    public static void main(String[] args) {
        // 加载OR-Tools本地库
        Loader.loadNativeLibraries();

        // 1. 创建配送点数据（成都市模拟坐标和时间窗）
        List<Location> locations = new ArrayList<>();
        // 配送中心（春熙路花店）
        locations.add(new Location("春熙路花店", 30.660083, 104.077774, new int[]{0, 1439}, 0, 0)); // 全天开放，无惩罚
        // 配送点1：天府广场
        locations.add(new Location("天府广场", 30.661939, 104.065861, new int[]{540, 720}, 1, 3)); // 9:00-12:00，早到1元/分钟，迟到3元/分钟
        // 配送点2：锦里古街
        locations.add(new Location("锦里古街", 30.650534, 104.049828, new int[]{660, 840}, 1, 5)); // 11:00-14:00，早到1元/分钟，迟到5元/分钟
        // 配送点3：宽窄巷子
        locations.add(new Location("宽窄巷子", 30.66737, 104.056006, new int[]{780, 960}, 1, 2)); // 13:00-16:00，早到1元/分钟，迟到2元/分钟
        // 配送点4：环球中心
        locations.add(new Location("环球中心", 30.573292, 104.063403, new int[]{900, 1080}, 1, 10)); // 15:00-18:00，早到1元/分钟，迟到10元/分钟

        // 2. 计算距离矩阵（使用曼哈顿距离模拟）
        long[][] distanceMatrix = createDistanceMatrix(locations);

        // 3. 创建路由模型
        RoutingIndexManager manager = new RoutingIndexManager(
                locations.size(), 1, 0); // 1辆车，起点是索引0
        RoutingModel routing = new RoutingModel(manager);

        // 4. 注册距离回调
        final int transitCallbackIndex = routing.registerTransitCallback(
                (long fromIndex, long toIndex) -> {
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    return distanceMatrix[fromNode][toNode];
                });

        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        // 5. 添加时间维度约束
        routing.addDimension(transitCallbackIndex,
                60,  // 允许等待时间（分钟）
                1440, // 全天最大时间（1440分钟=24小时）
                false, // 不强制开始时间
                "Time");
        RoutingDimension timeDimension = routing.getMutableDimension("Time");

        // 6. 设置出发时间(10:20 AM)
        long departureTime = 620;
        timeDimension.cumulVar(routing.start(0)).setRange(departureTime, departureTime);

        // 7. 添加软时间窗约束
        IntVar totalPenalty = routing.solver().makeIntVar(0, Integer.MAX_VALUE, "total_penalty");
        List<IntVar> penaltyTerms = new ArrayList<>();

        for (int i = 1; i < locations.size(); ++i) {
            long index = manager.nodeToIndex(i);
            Location loc = locations.get(i);

            IntVar arrivalTime = timeDimension.cumulVar(index);

            // 早到惩罚（如果早于时间窗开始）
            if (loc.earlyPenalty > 0) {
                IntVar earlyVar = routing.solver().makeIntVar(0, loc.timeWindow[0], "early_"+i);
                // earlyVar = max(0, timeWindow[0] - arrivalTime)
                routing.solver().addConstraint(
                        routing.solver().makeGreaterOrEqual(
                                routing.solver().makeSum(earlyVar, arrivalTime),
                                loc.timeWindow[0]
                        )
                );
                routing.addVariableMinimizedByFinalizer(earlyVar);
                penaltyTerms.add(routing.solver().makeProd(earlyVar, loc.earlyPenalty).var());
            }

            // 迟到惩罚（如果晚于时间窗结束）
            if (loc.latePenalty > 0) {
                IntVar lateVar = routing.solver().makeIntVar(0, 1440 - loc.timeWindow[1], "late_"+i);
                // lateVar = max(0, arrivalTime - timeWindow[1])
                routing.solver().addConstraint(
                        routing.solver().makeLessOrEqual(
                                routing.solver().makeDifference(arrivalTime, lateVar),
                                loc.timeWindow[1]
                        )
                );
                routing.addVariableMinimizedByFinalizer(lateVar);
                penaltyTerms.add(routing.solver().makeProd(lateVar, loc.latePenalty).var());
            }
        }

        // 将惩罚项加入总目标函数
        if (!penaltyTerms.isEmpty()) {
            routing.solver().addConstraint(
                    routing.solver().makeEquality(
                            totalPenalty,
                            routing.solver().makeSum(penaltyTerms.toArray(new IntVar[0])).var()
                    )
            );
            routing.addVariableMinimizedByFinalizer(totalPenalty);
        }

        // 8. 设置搜索参数
        RoutingSearchParameters searchParameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                        .setTimeLimit(Duration.newBuilder().setSeconds(10).build())
                        .build();

        // 9. 求解问题
        Assignment solution = routing.solveWithParameters(searchParameters);

        // 10. 打印解决方案
        printSolution(locations, manager, routing, solution);
    }

    // 创建距离矩阵（模拟曼哈顿距离）
    private static long[][] createDistanceMatrix(List<Location> locations) {
        long[][] matrix = new long[locations.size()][locations.size()];
        for (int i = 0; i < locations.size(); ++i) {
            Location from = locations.get(i);
            for (int j = 0; j < locations.size(); ++j) {
                Location to = locations.get(j);
                // 模拟曼哈顿距离（实际应用应使用真实地图API）
                double latDiff = Math.abs(from.lat - to.lat) * 111000; // 1度≈111km
                double lngDiff = Math.abs(from.lng - to.lng) * 85000;  // 成都纬度约0.85比例
                matrix[i][j] = (long) (latDiff + lngDiff) / 100; // 转换为分钟（假设车速≈16.6m/s）
            }
        }
        return matrix;
    }

    // 打印解决方案
    private static void printSolution(
            List<Location> locations, RoutingIndexManager manager,
            RoutingModel routing, Assignment solution) {
        if (solution == null) {
            log.warn("未找到解决方案！");
            return;
        }

        RoutingDimension timeDimension = routing.getMutableDimension("Time");

        System.out.println("=== 花店配送最优路线(软时间窗) ===");
        System.out.printf("总行驶时间: %d 分钟\n", solution.objectiveValue());

        // 计算总惩罚成本
        long totalPenaltyCost = 0;

        System.out.println("序号 | 配送点      | 到达时间 | 时间窗        | 早到 | 迟到 | 惩罚成本 | 状态");
        System.out.println("----|------------|----------|--------------|------|------|----------|------");

        long index = routing.start(0);
        int stopNumber = 1;
        while (!routing.isEnd(index)) {
            int node = manager.indexToNode(index);
            Location loc = locations.get(node);

            long arrivalTime = solution.min(timeDimension.cumulVar(index));
            long earlyTime = Math.max(0, loc.timeWindow[0] - arrivalTime);
            long lateTime = Math.max(0, arrivalTime - loc.timeWindow[1]);
            int penalty = (int)(earlyTime * loc.earlyPenalty + lateTime * loc.latePenalty);
            totalPenaltyCost += penalty;

            System.out.printf("%2d | %-10s | %02d:%02d   | %02d:%02d-%02d:%02d | %3d分钟 | %3d分钟 | %5d元 | %s%n",
                    stopNumber++,
                    loc.name,
                    arrivalTime / 60, arrivalTime % 60,
                    loc.timeWindow[0] / 60, loc.timeWindow[0] % 60,
                    loc.timeWindow[1] / 60, loc.timeWindow[1] % 60,
                    earlyTime,
                    lateTime,
                    penalty,
                    getDeliveryStatus(arrivalTime, loc.timeWindow));

            index = solution.value(routing.nextVar(index));
        }

        System.out.printf("\n总惩罚成本: %d 元\n", totalPenaltyCost);

        // 打印返回信息
        long returnTime = solution.min(timeDimension.cumulVar(index));
        System.out.printf("返回花店时间: %02d:%02d\n", returnTime / 60, returnTime % 60);
    }

    private static String getDeliveryStatus(long arrivalTime, int[] timeWindow) {
        if (arrivalTime < timeWindow[0]) return "提前";
        if (arrivalTime > timeWindow[1]) return "迟到";
        return "准时";
    }
}