package com.github.fanzezhen.demo.tsp;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 软窗口+时间窗口影响路线
 */
@Slf4j
public class FlowerShopDelivery3 {

    static class Location {
        String name;
        double lat;
        double lng;
        int[] timeWindow;
        int earlyPenalty;
        int latePenalty;

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
        Loader.loadNativeLibraries();

        List<Location> locations = createLocations();
        long[][] distanceMatrix = createDistanceMatrix(locations);

        RoutingIndexManager manager = new RoutingIndexManager(locations.size(), 1, 0);
        RoutingModel routing = new RoutingModel(manager);

        // 距离回调
        final int transitCallbackIndex = routing.registerTransitCallback(
                (long fromIndex, long toIndex) -> distanceMatrix[
                        manager.indexToNode(fromIndex)][manager.indexToNode(toIndex)]
        );
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        // 时间维度
        routing.addDimension(transitCallbackIndex, 30, 1440, false, "Time");
        RoutingDimension timeDimension = routing.getMutableDimension("Time");

        // 设置出发时间
        long departureTime = 620; // 10:20 AM
        timeDimension.cumulVar(routing.start(0)).setRange(departureTime, departureTime);

        // 软时间窗约束
        IntVar totalPenalty = routing.solver().makeIntVar(0, Integer.MAX_VALUE, "total_penalty");
        List<IntVar> penaltyTerms = new ArrayList<>();

        for (int i = 1; i < locations.size(); ++i) {
            long index = manager.nodeToIndex(i);
            Location loc = locations.get(i);
            IntVar arrivalTime = timeDimension.cumulVar(index);

            // 早到惩罚
            IntVar earlyVar = routing.solver().makeIntVar(0, loc.timeWindow[0], "early_" + i);
            routing.solver().addConstraint(
                    routing.solver().makeGreaterOrEqual(
                            routing.solver().makeSum(earlyVar, arrivalTime),
                            loc.timeWindow[0]
                    )
            );
//            routing.addVariableMinimizedByFinalizer(earlyVar);
            penaltyTerms.add(buildQuadraticPenalty(routing.solver(), earlyVar, loc.earlyPenalty).var());

            // 迟到惩罚
            IntVar lateVar = routing.solver().makeIntVar(0, 1440 - loc.timeWindow[1], "late_"+i);
            routing.solver().addConstraint(
                    routing.solver().makeLessOrEqual(
                            routing.solver().makeDifference(arrivalTime, lateVar),
                            loc.timeWindow[1]
                    )
            );
//            routing.addVariableMinimizedByFinalizer(lateVar);
            penaltyTerms.add(buildQuadraticPenalty(routing.solver(), lateVar, loc.latePenalty));
        }

        // 计算总惩罚
        if (!penaltyTerms.isEmpty()) {
            routing.solver().addConstraint(
                    routing.solver().makeEquality(
                            totalPenalty,
                            routing.solver().makeSum(penaltyTerms.toArray(new IntVar[0])).var()
                    )
            );
        }

        // 获取基础成本变量（修复 costVar 为 null 的问题）
        IntVar baseCostVar;
        try {
            baseCostVar = routing.costVar();
            if (baseCostVar == null) {
                baseCostVar = timeDimension.cumulVar(routing.end(0));
            }
        } catch (Exception e) {
            log.error("", e);
            baseCostVar = timeDimension.cumulVar(routing.end(0));
        }

        // 创建总成本变量
        IntVar totalCost = routing.solver().makeIntVar(0, Integer.MAX_VALUE, "total_cost");
        if (!penaltyTerms.isEmpty()) {
            routing.solver().addConstraint(
                    routing.solver().makeEquality(
                            totalCost,
                            routing.solver().makeSum(baseCostVar, routing.solver().makeProd(totalPenalty, 800000)).var()
                    )
            );
        } else {
            routing.solver().addConstraint(
                    routing.solver().makeEquality(totalCost, baseCostVar)
            );
        }
        routing.addVariableMinimizedByFinalizer(totalCost);

        // 搜索参数
        RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
                .toBuilder()
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                .setTimeLimit(Duration.newBuilder().setSeconds(10).build())
                .build();

        Assignment solution = routing.solveWithParameters(searchParameters);
        printSolution(locations, manager, routing, solution);
    }

    private static List<Location> createLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location("春熙路花店", 30.660083, 104.077774, new int[]{0, 1439}, 0, 0));
        locations.add(new Location("天府广场", 30.661939, 104.065861, new int[]{540, 720}, 1, 3));
        locations.add(new Location("锦里古街", 30.650534, 104.049828, new int[]{660, 840}, 1, 5));
        locations.add(new Location("宽窄巷子", 30.66737, 104.056006, new int[]{780, 960}, 1, 2));
        locations.add(new Location("环球中心", 30.573292, 104.063403, new int[]{900, 1080}, 1, 10));
        return locations;
    }

    private static long[][] createDistanceMatrix(List<Location> locations) {
        long[][] matrix = new long[locations.size()][locations.size()];
        for (int i = 0; i < locations.size(); ++i) {
            Location from = locations.get(i);
            for (int j = 0; j < locations.size(); ++j) {
                Location to = locations.get(j);
                double latDiff = Math.abs(from.lat - to.lat) * 111000;
                double lngDiff = Math.abs(from.lng - to.lng) * 85000;
                matrix[i][j] = (long) (latDiff + lngDiff) / 100;
            }
        }
        return matrix;
    }

    private static void printSolution(
            List<Location> locations, RoutingIndexManager manager,
            RoutingModel routing, Assignment solution) {
        if (solution == null) {
            log.warn("未找到解决方案！");
            return;
        }

        RoutingDimension timeDimension = routing.getMutableDimension("Time");
        System.out.println("=== 花店配送最优路线 ===");

        // 计算各项成本
        long travelTime = solution.objectiveValue();
        long totalPenalty = calculateTotalPenalty(locations, manager, routing, solution);
        System.out.printf("总行驶时间: %d 分钟\n", travelTime);
        System.out.printf("总惩罚成本: %d 元\n", totalPenalty);
        System.out.printf("综合成本: %d (时间+惩罚)\n\n", travelTime + totalPenalty);

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
            int penalty = (int) (earlyTime * loc.earlyPenalty + lateTime * loc.latePenalty);

            System.out.printf("%2d | %-10s | %02d:%02d   | %02d:%02d-%02d:%02d | %3d分钟 | %3d分钟 | %5d | %s%n",
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

        System.out.printf("\n返回花店时间: %02d:%02d\n",
                solution.min(timeDimension.cumulVar(index)) / 60,
                solution.min(timeDimension.cumulVar(index)) % 60);
    }

    private static long calculateTotalPenalty(
            List<Location> locations, RoutingIndexManager manager,
            RoutingModel routing, Assignment solution) {

        RoutingDimension timeDimension = routing.getMutableDimension("Time");
        long totalPenalty = 0;

        long index = routing.start(0);
        while (!routing.isEnd(index)) {
            int node = manager.indexToNode(index);
            if (node == 0) {
                index = solution.value(routing.nextVar(index));
                continue;
            }

            Location loc = locations.get(node);
            long arrivalTime = solution.min(timeDimension.cumulVar(index));
            long earlyTime = Math.max(0, loc.timeWindow[0] - arrivalTime);
            long lateTime = Math.max(0, arrivalTime - loc.timeWindow[1]);
            totalPenalty += earlyTime * loc.earlyPenalty + lateTime * loc.latePenalty;

            index = solution.value(routing.nextVar(index));
        }

        return totalPenalty;
    }

    private static String getDeliveryStatus(long arrivalTime, int[] timeWindow) {
        if (arrivalTime < timeWindow[0]) return "提前";
        if (arrivalTime > timeWindow[1]) return "迟到";
        return "准时";
    }
    /**
     * 通用二次方惩罚构建器（早到/迟到统一处理）
     * @param solver 求解器实例
     * @param deviationVar 时间偏差变量（早到或迟到的分钟数）
     * @param basePenalty 基础惩罚系数
     * @return 返回 deviationVar² × basePenalty 的惩罚变量
     */
    static IntVar buildQuadraticPenalty(Solver solver, IntVar deviationVar, int basePenalty) {
        // deviation² = deviation × deviation
        IntVar squaredDeviation = solver.makeIntVar(0, 1440*1440, "squared_deviation");
        solver.addConstraint(
                solver.makeEquality(
                        squaredDeviation,
                        solver.makeProd(deviationVar, deviationVar).var()
                )
        );

        // 最终惩罚 = squaredDeviation × basePenalty
        return solver.makeProd(squaredDeviation, basePenalty).var();
    }
}