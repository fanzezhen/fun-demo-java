package com.github.fanzezhen.demo.tsp;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 硬窗口
 */
@Slf4j
public class FlowerShopDelivery1 {
    static final long SLACK_MAX = 60; // 允许等待时间（分钟）

    static class Location {
        String name;
        double lat;
        double lng;
        int[] timeWindow; // [开始时间, 结束时间]（分钟表示，如540=9:00）

        Location(String name, double lat, double lng, int[] timeWindow) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
            this.timeWindow = timeWindow;
        }
    }

    public static void main(String[] args) {
        // 加载OR-Tools本地库
        Loader.loadNativeLibraries();

        // 1. 创建配送点数据（成都市模拟坐标和时间窗）
        List<Location> locations = new ArrayList<>();
        // 配送中心（春熙路花店）
        locations.add(new Location("春熙路花店", 30.660083, 104.077774, new int[]{0, 1439})); // 全天开放
        // 配送点1：天府广场
        locations.add(new Location("天府广场", 30.661939, 104.065861, new int[]{540, 720})); // 9:00-12:00
        // 配送点2：锦里古街
        locations.add(new Location("锦里古街", 30.650534, 104.049828, new int[]{660, 840})); // 11:00-14:00
        // 配送点3：宽窄巷子
        locations.add(new Location("宽窄巷子", 30.66737, 104.056006, new int[]{780, 960})); // 13:00-16:00
        // 配送点4：环球中心
        locations.add(new Location("环球中心", 30.573292, 104.063403, new int[]{900, 1080})); // 15:00-18:00

        // 2. 计算距离矩阵（使用曼哈顿距离模拟）
        long[][] distanceMatrix = createDistanceMatrix(locations);
        int[][] timeWindows = locations.stream()
                .map(loc -> loc.timeWindow)
                .toArray(int[][]::new);

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
                SLACK_MAX,  // 允许等待时间（分钟）
                1440, // 全天最大时间（1440分钟=24小时）
                false, // 不强制开始时间
                "Time");
        RoutingDimension timeDimension = routing.getMutableDimension("Time");
        // 6. 为各配送点设置时间窗，跳过配送中心，设置出发时间(上午8:00出发，即480分钟)
        long departureTime = 620; // 10:20 AM
        timeDimension.cumulVar(routing.start(0)).setRange(departureTime, departureTime);
        for (int i = 1; i < locations.size(); ++i) {
            long index = manager.nodeToIndex(i);
            timeDimension.cumulVar(index).setRange(timeWindows[i][0], timeWindows[i][1]);
        }

        // 7. 设置搜索参数
        RoutingSearchParameters searchParameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                        .setTimeLimit(Duration.newBuilder().setSeconds(10).build())
                        .build();

        // 8. 求解问题
        Assignment solution = routing.solveWithParameters(searchParameters);

        // 9. 打印解决方案
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

        System.out.println("=== 花店配送最优路线 ===");
        System.out.printf("总行驶时间: %d 分钟\n", solution.objectiveValue());

        long index = routing.start(0);
        RoutingDimension timeDimension = routing.getMutableDimension("Time");
        while (!routing.isEnd(index)) {
            int node = manager.indexToNode(index);
            Location loc = locations.get(node);
            System.out.printf("到达: %-10s (坐标: %.4f,%.4f) 时间窗: %02d:%02d-%02d:%02d | ",
                    loc.name, loc.lat, loc.lng,
                    loc.timeWindow[0] / 60, loc.timeWindow[0] % 60,
                    loc.timeWindow[1] / 60, loc.timeWindow[1] % 60);

            System.out.printf("实际到达: %02d:%02d\n",
                    (int)solution.min(timeDimension.cumulVar(index)) / 60,
                    (int)solution.min(timeDimension.cumulVar(index)) % 60);

            index = solution.value(routing.nextVar(index));
        }

        // 打印返回配送中心的时间
        int node = manager.indexToNode(index);
        Location loc = locations.get(node);
        System.out.printf("返回: %-10s | 最终时间: %02d:%02d\n",
                loc.name,
                (int)solution.min(timeDimension.cumulVar(index)) / 60,
                (int)solution.min(timeDimension.cumulVar(index)) % 60);
    }
}