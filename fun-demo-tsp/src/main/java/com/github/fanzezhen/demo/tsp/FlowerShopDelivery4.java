package com.github.fanzezhen.demo.tsp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FlowerShopDelivery4 {
    private static final double EARTH_RADIUS = 6371.0; // 地球半径，单位：公里
    private static final double SERVICE_TIME = 0.1; // 每个订单的服务时间，单位：小时（6分钟）
    private static final LocalTime START_TIME = LocalTime.of(8, 0); // 开始配送时间
    private static final LocalTime END_TIME = LocalTime.of(16, 0); // 结束配送时间
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // 市中心区域定义（以成都市中心为例）
    private static final double DOWNTOWN_LAT = 30.6570;
    private static final double DOWNTOWN_LON = 104.0650;
    private static final double DOWNTOWN_RADIUS = 5.0; // 市中心半径（公里）

    public static void main(String[] args) {
        // 创建订单数据（使用成都市热门地点经纬度）
        List<Order> orders = createSampleOrders();

        // 配送中心位置（成都市中心：天府广场）
        Location depot = new Location(30.657000, 104.065000, "天府广场");

        // 指定车辆配置
        List<VehicleConfig> vehicleConfigs = createVehicleConfigs();

        // 规划配送路线
        List<List<Order>> routes = planDeliveryRoutes(orders, depot, vehicleConfigs);

        // 输出结果
        printRoutes(routes, depot, vehicleConfigs);
    }

    // 创建车辆配置
    private static List<VehicleConfig> createVehicleConfigs() {
        List<VehicleConfig> configs = new ArrayList<>();
        // 车辆1需要返回配送中心
        configs.add(new VehicleConfig(false, "车辆1"));
        // 车辆2不需要返回配送中心
        configs.add(new VehicleConfig(false, "车辆2"));
        // 车辆3需要返回配送中心
        configs.add(new VehicleConfig(false, "车辆3"));
        // 车辆4不需要返回配送中心
        configs.add(new VehicleConfig(false, "车辆4"));
        // 车辆5需要返回配送中心
        configs.add(new VehicleConfig(false, "车辆5"));
        return configs;
    }

    // 创建示例订单数据
    private static List<Order> createSampleOrders() {
        List<Order> orders = new ArrayList<>();

        // 添加成都市热门地点作为订单
        orders.add(new Order("春熙路", new Location(30.659967, 104.074033, "春熙路"), true, LocalTime.of(9, 30)));
        orders.add(new Order("锦里", new Location(30.647505, 104.047495, "锦里"), true, LocalTime.of(10, 30)));
        orders.add(new Order("宽窄巷子", new Location(30.661472, 104.050000, "宽窄巷子"), true, LocalTime.of(11, 0)));
        orders.add(new Order("成都大熊猫繁育研究基地", new Location(30.707222, 104.137778, "成都大熊猫繁育研究基地"), false, null));
        orders.add(new Order("武侯祠", new Location(30.648056, 104.047500, "武侯祠"), false, null));
        orders.add(new Order("杜甫草堂", new Location(30.649167, 104.038611, "杜甫草堂"), false, null));
        orders.add(new Order("环球中心", new Location(30.598333, 104.067500, "环球中心"), false, null));
        orders.add(new Order("成都东站", new Location(30.640833, 104.112500, "成都东站"), true, LocalTime.of(12, 0)));
        orders.add(new Order("成都南站", new Location(30.598611, 104.076944, "成都南站"), false, null));
        orders.add(new Order("天府国际机场", new Location(30.440556, 104.537778, "天府国际机场"), true, LocalTime.of(14, 0)));

        // 新增订单 - 锦江区
        orders.add(new Order("太古里", new Location(30.6599, 104.0772, "太古里"), true, LocalTime.of(9, 45)));
        orders.add(new Order("九眼桥", new Location(30.6487, 104.0785, "九眼桥"), false, null));
        orders.add(new Order("四川大学", new Location(30.6357, 104.0881, "四川大学"), false, null));

        // 新增订单 - 青羊区
        orders.add(new Order("人民公园", new Location(30.6570, 104.0485, "人民公园"), true, LocalTime.of(10, 0)));
        orders.add(new Order("金沙遗址", new Location(30.6783, 104.0238, "金沙遗址"), false, null));
        orders.add(new Order("文化宫", new Location(30.6438, 104.0412, "文化宫"), false, null));

        // 新增订单 - 武侯区
        orders.add(new Order("四川大学华西医院", new Location(30.6423, 104.0642, "华西医院"), true, LocalTime.of(10, 15)));
        orders.add(new Order("来福士广场", new Location(30.6535, 104.0523, "来福士广场"), false, null));
        orders.add(new Order("桐梓林", new Location(30.6298, 104.0677, "桐梓林"), false, null));

        // 新增订单 - 金牛区
        orders.add(new Order("欢乐谷", new Location(30.7032, 104.0351, "欢乐谷"), false, null));
        orders.add(new Order("西南交通大学", new Location(30.7043, 104.0707, "西南交通大学"), false, null));
        orders.add(new Order("茶店子", new Location(30.6924, 104.0358, "茶店子"), false, null));

        // 新增订单 - 成华区
        orders.add(new Order("东郊记忆", new Location(30.6685, 104.1015, "东郊记忆"), false, null));
        orders.add(new Order("万象城", new Location(30.6497, 104.1235, "万象城"), false, null));
        orders.add(new Order("青龙场", new Location(30.7013, 104.1025, "青龙场"), false, null));

        // 新增订单 - 高新区
        orders.add(new Order("软件园", new Location(30.5797, 104.0788, "软件园"), false, null));
        orders.add(new Order("世纪城", new Location(30.5857, 104.0569, "世纪城"), false, null));
        orders.add(new Order("金融城", new Location(30.5937, 104.0519, "金融城"), false, null));

        // 新增订单 - 郫都区
        orders.add(new Order("电子科技大学", new Location(30.7422, 104.0567, "电子科技大学"), false, null));
        orders.add(new Order("望丛祠", new Location(30.7053, 104.0141, "望丛祠"), false, null));
        orders.add(new Order("犀浦", new Location(30.7132, 104.0025, "犀浦"), false, null));

        // 新增订单 - 双流区
        orders.add(new Order("黄龙溪古镇", new Location(30.4858, 104.0687, "黄龙溪古镇"), false, null));
        orders.add(new Order("双流机场", new Location(30.5785, 103.9470, "双流机场"), true, LocalTime.of(13, 30)));
        orders.add(new Order("棠湖公园", new Location(30.5702, 103.9975, "棠湖公园"), false, null));

        // 新增订单 - 温江区
        orders.add(new Order("国色天乡", new Location(30.6125, 103.8872, "国色天乡"), false, null));
        orders.add(new Order("大学城", new Location(30.6302, 103.8958, "大学城"), false, null));

        // 新增订单 - 龙泉驿区
        orders.add(new Order("洛带古镇", new Location(30.7138, 104.2608, "洛带古镇"), false, null));
        orders.add(new Order("蔚然花海", new Location(30.6575, 104.3033, "蔚然花海"), false, null));

        // 新增订单 - 新都区
        orders.add(new Order("宝光寺", new Location(30.8311, 104.1206, "宝光寺"), false, null));
        orders.add(new Order("桂湖公园", new Location(30.8292, 104.1244, "桂湖公园"), false, null));

        // 新增订单 - 青白江区
        orders.add(new Order("凤凰湖", new Location(30.8200, 104.3383, "凤凰湖"), false, null));

        // 新增订单 - 都江堰市
        orders.add(new Order("青城山", new Location(30.9297, 103.5692, "青城山"), true, LocalTime.of(13, 0)));
        orders.add(new Order("都江堰景区", new Location(31.0075, 103.6186, "都江堰景区"), false, null));

        return orders;
    }

    // 规划配送路线
    public static List<List<Order>> planDeliveryRoutes(List<Order> orders, Location depot, List<VehicleConfig> vehicleConfigs) {
        // 初始化车辆路线
        List<List<Order>> routes = new ArrayList<>();
        for (int i = 0; i < vehicleConfigs.size(); i++) {
            routes.add(new ArrayList<>());
        }

        // 步骤1：将订单分为严格时间窗口和宽松时间窗口
        List<Order> timeCriticalOrders = orders.stream()
                .filter(Order::isTimeCritical)
                .sorted(Comparator.comparing(Order::getDeadline))
                .collect(Collectors.toList());

        List<Order> nonCriticalOrders = orders.stream()
                .filter(order -> !order.isTimeCritical())
                .sorted(Comparator.comparingDouble(o -> calculateEnhancedUrbanDistance(depot, o.getLocation())))
                .collect(Collectors.toList());

        // 步骤2：使用节约算法为严格时间窗口订单分配车辆
        applyClarkeWrightSavings(timeCriticalOrders, routes, depot, vehicleConfigs);

        // 步骤3：为宽松时间窗口订单分配车辆
        assignNonCriticalOrders(nonCriticalOrders, routes, depot, vehicleConfigs);

        // 步骤4：使用3-opt算法进一步优化路径
        optimizeRoutesWith3Opt(routes, depot, vehicleConfigs);

        return routes;
    }

    // 节约算法实现
    private static void applyClarkeWrightSavings(List<Order> orders, List<List<Order>> routes, Location depot, List<VehicleConfig> vehicleConfigs) {
        // 初始化：每个订单单独成一条路线
        List<List<Order>> tempRoutes = new ArrayList<>();
        for (Order order : orders) {
            List<Order> route = new ArrayList<>();
            route.add(order);
            tempRoutes.add(route);
        }

        // 计算所有订单对的节约值
        PriorityQueue<SavingsPair> savingsQueue = new PriorityQueue<>(Comparator.reverseOrder());

        for (int i = 0; i < orders.size(); i++) {
            for (int j = i + 1; j < orders.size(); j++) {
                Order o1 = orders.get(i);
                Order o2 = orders.get(j);

                // 计算节约值: d(depot, o1) + d(depot, o2) - d(o1, o2)
                double saving = calculateEnhancedUrbanDistance(depot, o1.getLocation()) +
                        calculateEnhancedUrbanDistance(depot, o2.getLocation()) -
                        calculateEnhancedUrbanDistance(o1.getLocation(), o2.getLocation());

                savingsQueue.add(new SavingsPair(o1, o2, saving));
            }
        }

        // 合并路线
        while (!savingsQueue.isEmpty()) {
            SavingsPair pair = savingsQueue.poll();
            Order o1 = pair.getOrder1();
            Order o2 = pair.getOrder2();

            List<Order> route1 = findRouteContainingOrder(tempRoutes, o1);
            List<Order> route2 = findRouteContainingOrder(tempRoutes, o2);

            // 如果两个订单不在同一路线且合并后可行，则合并
            if (route1 != route2 && canMergeRoutes(route1, route2, depot, vehicleConfigs)) {
                mergeRoutes(route1, route2, depot);
                tempRoutes.remove(route2);
            }
        }

        // 将临时路线分配到车辆
        for (List<Order> tempRoute : tempRoutes) {
            if (!tempRoute.isEmpty()) {
                // 找到最适合的车辆
                int bestVehicleIndex = findBestVehicleForRoute(tempRoute, routes, depot, vehicleConfigs);
                if (bestVehicleIndex != -1) {
                    routes.get(bestVehicleIndex).addAll(tempRoute);
                }
            }
        }
    }

    // 查找最适合的车辆
    private static int findBestVehicleForRoute(List<Order> route, List<List<Order>> routes, Location depot, List<VehicleConfig> vehicleConfigs) {
        int bestIndex = -1;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < routes.size(); i++) {
            List<Order> existingRoute = routes.get(i);
            List<Order> combinedRoute = new ArrayList<>(existingRoute);
            combinedRoute.addAll(route);

            if (isRouteFeasible(combinedRoute, depot, vehicleConfigs.get(i))) {
                double distance = calculateRouteDistance(combinedRoute, depot, vehicleConfigs.get(i));
                if (distance < minDistance) {
                    minDistance = distance;
                    bestIndex = i;
                }
            }
        }

        return bestIndex;
    }

    // 检查两条路线是否可以合并
    private static boolean canMergeRoutes(List<Order> route1, List<Order> route2, Location depot, List<VehicleConfig> vehicleConfigs) {
        List<Order> mergedRoute = new ArrayList<>(route1);
        mergedRoute.addAll(route2);

        // 找到最适合的车辆
        int bestVehicleIndex = findBestVehicleForRoute(mergedRoute, new ArrayList<>(), depot, vehicleConfigs);

        return bestVehicleIndex != -1;
    }

    // 合并两条路线
    private static void mergeRoutes(List<Order> route1, List<Order> route2, Location depot) {
        // 找到最佳合并方式（首尾相连或尾首相连）
        double dist1 = calculateEnhancedUrbanDistance(
                route1.get(route1.size()-1).getLocation(),
                route2.get(0).getLocation()
        );

        double dist2 = calculateEnhancedUrbanDistance(
                route2.get(route2.size()-1).getLocation(),
                route1.get(0).getLocation()
        );

        // 选择距离较短的合并方式
        if (dist1 < dist2) {
            route1.addAll(route2);
        } else {
            List<Order> reversedRoute2 = new ArrayList<>(route2);
            Collections.reverse(reversedRoute2);
            route1.addAll(0, reversedRoute2);
        }
    }

    // 查找包含指定订单的路线
    private static List<Order> findRouteContainingOrder(List<List<Order>> routes, Order order) {
        for (List<Order> route : routes) {
            if (route.contains(order)) {
                return route;
            }
        }
        return null;
    }

    // 节约对类
    static class SavingsPair implements Comparable<SavingsPair> {
        private Order order1;
        private Order order2;
        private double saving;

        public SavingsPair(Order order1, Order order2, double saving) {
            this.order1 = order1;
            this.order2 = order2;
            this.saving = saving;
        }

        public Order getOrder1() {
            return order1;
        }

        public Order getOrder2() {
            return order2;
        }

        public double getSaving() {
            return saving;
        }

        @Override
        public int compareTo(SavingsPair other) {
            return Double.compare(this.saving, other.saving);
        }
    }

    // 3-opt算法优化路径
    private static void optimizeRoutesWith3Opt(List<List<Order>> routes, Location depot, List<VehicleConfig> vehicleConfigs) {
        for (int i = 0; i < routes.size(); i++) {
            List<Order> route = routes.get(i);
            boolean improved = true;

            while (improved) {
                improved = false;

                for (int j = 0; j < route.size() - 2; j++) {
                    for (int k = j + 1; k < route.size() - 1; k++) {
                        for (int l = k + 1; l < route.size(); l++) {
                            // 尝试所有可能的3-opt交换
                            List<List<Order>> newRoutes = generate3OptVariants(route, j, k, l);

                            for (List<Order> newRoute : newRoutes) {
                                if (newRoute != null &&
                                        isRouteFeasible(newRoute, depot, vehicleConfigs.get(i)) &&
                                        calculateRouteDistance(newRoute, depot, vehicleConfigs.get(i)) <
                                                calculateRouteDistance(route, depot, vehicleConfigs.get(i))) {
                                    route.clear();
                                    route.addAll(newRoute);
                                    improved = true;
                                    break;
                                }
                            }

                            if (improved) break;
                        }

                        if (improved) break;
                    }

                    if (improved) break;
                }
            }
        }
    }

    // 生成3-opt的所有可能变体
    private static List<List<Order>> generate3OptVariants(List<Order> route, int i, int j, int k) {
        List<List<Order>> variants = new ArrayList<>();
        List<Order> original = new ArrayList<>(route);

        // 变体1: 反转j到k之间的路径段
        List<Order> variant1 = new ArrayList<>(original);
        Collections.reverse(variant1.subList(j, k + 1));
        variants.add(variant1);

        // 变体2: 反转i到j之间的路径段
        List<Order> variant2 = new ArrayList<>(original);
        Collections.reverse(variant2.subList(i, j + 1));
        variants.add(variant2);

        // 变体3: 反转i到k之间的路径段
        List<Order> variant3 = new ArrayList<>(original);
        Collections.reverse(variant3.subList(i, k + 1));
        variants.add(variant3);

        // 变体4: 交换i到j和j到k之间的路径段
        List<Order> variant4 = new ArrayList<>();
        variant4.addAll(original.subList(0, i));
        variant4.addAll(original.subList(j, k + 1));
        variant4.addAll(original.subList(i, j));
        variant4.addAll(original.subList(k + 1, original.size()));
        variants.add(variant4);

        // 变体5: 反转i到j和j到k之间的路径段
        List<Order> variant5 = new ArrayList<>();
        variant5.addAll(original.subList(0, i));

        List<Order> reversedIJ = new ArrayList<>(original.subList(i, j));
        Collections.reverse(reversedIJ);
        variant5.addAll(reversedIJ);

        List<Order> reversedJK = new ArrayList<>(original.subList(j, k + 1));
        Collections.reverse(reversedJK);
        variant5.addAll(reversedJK);

        variant5.addAll(original.subList(k + 1, original.size()));
        variants.add(variant5);

        // 变体6: 反转i到j，然后交换i到j和j到k之间的路径段
        List<Order> variant6 = new ArrayList<>();
        variant6.addAll(original.subList(0, i));

        List<Order> reversedIJ2 = new ArrayList<>(original.subList(i, j));
        Collections.reverse(reversedIJ2);
        variant6.addAll(reversedJK);
        variant6.addAll(reversedIJ2);

        variant6.addAll(original.subList(k + 1, original.size()));
        variants.add(variant6);

        // 变体7: 反转j到k，然后交换i到j和j到k之间的路径段
        List<Order> variant7 = new ArrayList<>();
        variant7.addAll(original.subList(0, i));

        List<Order> reversedJK2 = new ArrayList<>(original.subList(j, k + 1));
        Collections.reverse(reversedJK2);
        variant7.addAll(reversedJK2);
        variant7.addAll(original.subList(i, j));

        variant7.addAll(original.subList(k + 1, original.size()));
        variants.add(variant7);

        return variants;
    }

    // 确定是否需要返回配送中心
    private static boolean shouldReturnToDepot(int vehicleIndex, List<Order> route, Location depot, List<VehicleConfig> vehicleConfigs) {
        if (route.isEmpty()) return false;

        // 优先使用车辆配置
        VehicleConfig config = vehicleConfigs.get(vehicleIndex);
        if (!config.isReturnToDepot()) {
            return false;
        }

        // 如果配置为返回，再根据时间判断
        Order lastOrder = route.get(route.size() - 1);
        double distanceToDepot = calculateEnhancedUrbanDistance(lastOrder.getLocation(), depot);
        double travelTimeToDepot = calculateEnhancedUrbanTravelTime(lastOrder.getLocation(), depot);

        LocalTime currentTime = calculateRouteEndTime(route, depot, false);
        LocalTime returnTime = currentTime.plusMinutes((long) (travelTimeToDepot * 60));

        // 如果返回配送中心的时间超过工作时间，则不需要返回
        return !returnTime.isAfter(END_TIME);
    }

    // 为宽松时间窗口订单分配车辆（增强版）
    private static void assignNonCriticalOrders(List<Order> nonCriticalOrders, List<List<Order>> routes, Location depot, List<VehicleConfig> vehicleConfigs) {
        // 计算每辆车当前已分配的订单数、总距离和总耗时
        List<Integer> ordersPerVehicle = new ArrayList<>();
        List<Double> distancePerVehicle = new ArrayList<>();
        List<Double> timePerVehicle = new ArrayList<>();

        for (int i = 0; i < routes.size(); i++) {
            ordersPerVehicle.add(routes.get(i).size());
            distancePerVehicle.add(calculateRouteDistance(routes.get(i), depot, vehicleConfigs.get(i)));
            timePerVehicle.add(calculateRouteTime(routes.get(i), depot, vehicleConfigs.get(i)));
        }

        // 对非紧急订单按距离配送中心由远到近排序
        nonCriticalOrders.sort((o1, o2) ->
                Double.compare(
                        calculateEnhancedUrbanDistance(o2.getLocation(), depot),
                        calculateEnhancedUrbanDistance(o1.getLocation(), depot)
                )
        );

        // 为每个订单寻找最佳车辆
        for (Order order : nonCriticalOrders) {
            boolean assigned = false;

            // 尝试分配给负载最小且地理上最接近的车辆
            int bestVehicleIndex = findBestBalancedVehicle(ordersPerVehicle, distancePerVehicle, timePerVehicle,
                    routes, order, depot, vehicleConfigs);

            if (bestVehicleIndex != -1) {
                insertOrder(routes.get(bestVehicleIndex), order, depot, vehicleConfigs.get(bestVehicleIndex));
                assigned = true;

                // 更新车辆负载信息
                ordersPerVehicle.set(bestVehicleIndex, ordersPerVehicle.get(bestVehicleIndex) + 1);
                distancePerVehicle.set(bestVehicleIndex, calculateRouteDistance(routes.get(bestVehicleIndex), depot, vehicleConfigs.get(bestVehicleIndex)));
                timePerVehicle.set(bestVehicleIndex, calculateRouteTime(routes.get(bestVehicleIndex), depot, vehicleConfigs.get(bestVehicleIndex)));
            }

            // 如果仍未分配，尝试其他车辆
            if (!assigned) {
                // 尝试将订单分配给能够最小化总距离的车辆
                bestVehicleIndex = -1;
                double minDistanceIncrease = Double.MAX_VALUE;

                for (int i = 0; i < routes.size(); i++) {
                    double distanceIncrease = calculateBestInsertionDistance(routes.get(i), order, depot, vehicleConfigs.get(i));

                    if (distanceIncrease < minDistanceIncrease &&
                            canInsertOrder(routes.get(i), order, depot, vehicleConfigs.get(i))) {
                        minDistanceIncrease = distanceIncrease;
                        bestVehicleIndex = i;
                    }
                }

                // 如果找到合适的车辆，分配订单
                if (bestVehicleIndex != -1) {
                    insertOrder(routes.get(bestVehicleIndex), order, depot, vehicleConfigs.get(bestVehicleIndex));
                    assigned = true;

                    // 更新车辆负载信息
                    ordersPerVehicle.set(bestVehicleIndex, ordersPerVehicle.get(bestVehicleIndex) + 1);
                    distancePerVehicle.set(bestVehicleIndex, calculateRouteDistance(routes.get(bestVehicleIndex), depot, vehicleConfigs.get(bestVehicleIndex)));
                    timePerVehicle.set(bestVehicleIndex, calculateRouteTime(routes.get(bestVehicleIndex), depot, vehicleConfigs.get(bestVehicleIndex)));
                }
            }

            // 如果仍未分配，记录未分配订单
            if (!assigned) {
                System.err.println("警告：订单 " + order.getName() + " 无法分配给任何车辆（超出工作时间）");
            }
        }
    }

    // 找到最佳平衡的车辆（考虑负载和地理接近性）
    private static int findBestBalancedVehicle(List<Integer> ordersPerVehicle, List<Double> distancePerVehicle,
                                               List<Double> timePerVehicle, List<List<Order>> routes,
                                               Order order, Location depot, List<VehicleConfig> vehicleConfigs) {
        int bestIndex = -1;
        double minLoad = Double.MAX_VALUE;

        for (int i = 0; i < routes.size(); i++) {
            // 计算车辆负载（综合考虑订单数、距离和时间）
            double load = calculateVehicleLoad(ordersPerVehicle.get(i), distancePerVehicle.get(i), timePerVehicle.get(i));

            // 计算地理接近性（到车辆当前路线中最近订单的距离）
            double proximity = calculateProximity(routes.get(i), order, depot);

            // 综合负载和接近性评估
            double score = load * 0.7 + proximity * 0.3;

            if (score < minLoad && canInsertOrder(routes.get(i), order, depot, vehicleConfigs.get(i))) {
                minLoad = score;
                bestIndex = i;
            }
        }

        return bestIndex;
    }

    // 计算车辆负载（综合考虑订单数、距离和时间）
    private static double calculateVehicleLoad(int orderCount, double totalDistance, double totalTime) {
        // 标准化各指标
        double normalizedOrders = orderCount / 10.0; // 假设最多10个订单
        double normalizedDistance = totalDistance / 100.0; // 假设最大距离100公里
        double normalizedTime = totalTime / 8.0; // 假设最大工作时间8小时

        // 加权综合评分
        return normalizedOrders * 0.4 + normalizedDistance * 0.3 + normalizedTime * 0.3;
    }

    // 计算订单与车辆当前路线的地理接近性
    private static double calculateProximity(List<Order> route, Order newOrder, Location depot) {
        if (route.isEmpty()) {
            return calculateEnhancedUrbanDistance(depot, newOrder.getLocation());
        }

        double minDistance = Double.MAX_VALUE;
        for (Order order : route) {
            double distance = calculateEnhancedUrbanDistance(order.getLocation(), newOrder.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
            }
        }

        return minDistance;
    }

    // 计算路线总耗时
    private static double calculateRouteTime(List<Order> route, Location depot, VehicleConfig vehicleConfig) {
        double travelTime = 0;
        double serviceTime = route.size() * SERVICE_TIME;
        Location currentLocation = depot;

        for (Order order : route) {
            travelTime += calculateEnhancedUrbanTravelTime(currentLocation, order.getLocation());
            currentLocation = order.getLocation();
        }

        // 如果需要返回配送中心
        if (shouldReturnToDepot(0, route, depot, Collections.singletonList(vehicleConfig))) {
            travelTime += calculateEnhancedUrbanTravelTime(currentLocation, depot);
        }

        return travelTime + serviceTime;
    }

    // 计算订单插入到路线后的到达时间
    private static LocalTime calculateArrivalTime(List<Order> route, Order newOrder, Location depot) {
        LocalTime currentTime = START_TIME;
        Location currentLocation = depot;

        for (Order order : route) {
            double travelTime = calculateEnhancedUrbanTravelTime(currentLocation, order.getLocation());
            currentTime = currentTime.plusMinutes((long) (travelTime * 60));
            currentTime = currentTime.plusMinutes((long) (SERVICE_TIME * 60));
            currentLocation = order.getLocation();
        }

        double travelTimeToNewOrder = calculateEnhancedUrbanTravelTime(currentLocation, newOrder.getLocation());
        return currentTime.plusMinutes((long) (travelTimeToNewOrder * 60));
    }

    // 计算订单插入到路线后最小的距离增加
    private static double calculateBestInsertionDistance(List<Order> route, Order newOrder, Location depot, VehicleConfig vehicleConfig) {
        double minDistanceIncrease = Double.MAX_VALUE;

        for (int i = 0; i <= route.size(); i++) {
            double distanceIncrease = calculateDistanceIncrease(route, newOrder, i, depot, vehicleConfig);
            if (distanceIncrease < minDistanceIncrease) {
                minDistanceIncrease = distanceIncrease;
            }
        }

        return minDistanceIncrease;
    }

    // 计算插入订单后的距离增加
    private static double calculateDistanceIncrease(List<Order> route, Order newOrder, int position, Location depot, VehicleConfig vehicleConfig) {
        double originalDistance = calculateRouteDistance(route, depot, vehicleConfig);
        List<Order> newRoute = new ArrayList<>(route);
        newRoute.add(position, newOrder);
        double newDistance = calculateRouteDistance(newRoute, depot, vehicleConfig);
        return newDistance - originalDistance;
    }

    // 计算路径总距离
    private static double calculateRouteDistance(List<Order> route, Location depot, VehicleConfig vehicleConfig) {
        double distance = 0;
        Location currentLocation = depot;

        for (Order order : route) {
            distance += calculateEnhancedUrbanDistance(currentLocation, order.getLocation());
            currentLocation = order.getLocation();
        }

        // 如果路线最后需要返回配送中心，则加上返回的距离
        if (shouldReturnToDepot(0, route, depot, Collections.singletonList(vehicleConfig))) {
            distance += calculateEnhancedUrbanDistance(currentLocation, depot);
        }

        return distance;
    }

    // 将订单插入到路径中
    private static void insertOrder(List<Order> route, Order newOrder, Location depot, VehicleConfig vehicleConfig) {
        // 找到最佳插入位置（最小化总距离增加）
        int bestPosition = 0;
        double minDistanceIncrease = Double.MAX_VALUE;

        for (int i = 0; i <= route.size(); i++) {
            double distanceIncrease = calculateDistanceIncrease(route, newOrder, i, depot, vehicleConfig);
            if (distanceIncrease < minDistanceIncrease) {
                minDistanceIncrease = distanceIncrease;
                bestPosition = i;
            }
        }

        route.add(bestPosition, newOrder);
    }

    // 检查订单是否可以插入到现有路径中
    private static boolean canInsertOrder(List<Order> route, Order newOrder, Location depot, VehicleConfig vehicleConfig) {
        LocalTime currentTime = START_TIME;
        Location currentLocation = depot;

        // 先计算原路径的时间
        for (Order order : route) {
            double travelTime = calculateEnhancedUrbanTravelTime(currentLocation, order.getLocation());
            currentTime = currentTime.plusMinutes((long) (travelTime * 60));
            currentTime = currentTime.plusMinutes((long) (SERVICE_TIME * 60));
            currentLocation = order.getLocation();

            // 检查是否满足时间窗口约束
            if (order.isTimeCritical() && currentTime.isAfter(order.getDeadline())) {
                return false;
            }
        }

        // 计算从最后一个订单到新订单的时间
        double travelTimeToNewOrder = calculateEnhancedUrbanTravelTime(currentLocation, newOrder.getLocation());
        LocalTime newOrderArrivalTime = currentTime.plusMinutes((long) (travelTimeToNewOrder * 60));

        // 检查新订单是否满足时间窗口约束
        if (newOrder.isTimeCritical() && newOrderArrivalTime.isAfter(newOrder.getDeadline())) {
            return false;
        }

        // 计算服务时间
        currentTime = newOrderArrivalTime.plusMinutes((long) (SERVICE_TIME * 60));
        currentLocation = newOrder.getLocation();

        // 计算从新订单返回配送中心的时间（如果需要返回）
        if (vehicleConfig.isReturnToDepot()) {
            double travelTimeToDepot = calculateEnhancedUrbanTravelTime(currentLocation, depot);
            LocalTime returnTime = currentTime.plusMinutes((long) (travelTimeToDepot * 60));

            // 检查是否超过工作时间
            return !returnTime.isAfter(END_TIME);
        }

        return true;
    }

    // 检查路径是否可行（不超过工作时间且不违反时间窗口约束）
    private static boolean isRouteFeasible(List<Order> route, Location depot, VehicleConfig vehicleConfig) {
        LocalTime currentTime = START_TIME;
        Location currentLocation = depot;

        for (Order order : route) {
            double travelTime = calculateEnhancedUrbanTravelTime(currentLocation, order.getLocation());
            currentTime = currentTime.plusMinutes((long) (travelTime * 60));
            currentTime = currentTime.plusMinutes((long) (SERVICE_TIME * 60));
            currentLocation = order.getLocation();

            // 检查是否满足时间窗口约束
            if (order.isTimeCritical() && currentTime.isAfter(order.getDeadline())) {
                return false;
            }
        }

        // 如果需要返回配送中心，计算返回时间
        if (vehicleConfig.isReturnToDepot()) {
            double travelTimeToDepot = calculateEnhancedUrbanTravelTime(currentLocation, depot);
            currentTime = currentTime.plusMinutes((long) (travelTimeToDepot * 60));
        }

        // 检查是否超过工作时间
        return !currentTime.isAfter(END_TIME);
    }

    // 市中心网格道路距离模型
    private static double calculateGridDistance(Location loc1, Location loc2) {
        // 计算经纬度差值（单位：公里）
        double latDiff = (loc2.getLatitude() - loc1.getLatitude()) * 111.32;
        double lonDiff = (loc2.getLongitude() - loc1.getLongitude()) * 111.32 * Math.cos(Math.toRadians(loc1.getLatitude()));

        // 曼哈顿距离（南北+东西方向）
        double manhattanDistance = Math.abs(latDiff) + Math.abs(lonDiff);

        // 考虑对角线道路和绕行的修正因子
        double gridFactor = 1.2; // 通常比曼哈顿距离多20%

        return manhattanDistance * gridFactor;
    }

    // 计算两点之间的距离（Haversine公式）
    private static double calculateDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS * c;
    }

    // 判断是否为市中心区域
    private static boolean isDowntown(Location loc) {
        return calculateDistance(loc, new Location(DOWNTOWN_LAT, DOWNTOWN_LON, "市中心")) <= DOWNTOWN_RADIUS;
    }

    // 考虑市中心特殊道路因素的距离计算
    private static double calculateUrbanDistance(Location loc1, Location loc2) {
        double baseDistance = calculateGridDistance(loc1, loc2);

        // 考虑单行道绕行（约10%额外距离）
        double oneWayFactor = 1.1;

        // 考虑步行街/禁行区绕行（市中心核心区域更高）
        double downtownCoreFactor = 1.0;
        if (isDowntownCore(loc1) || isDowntownCore(loc2)) {
            downtownCoreFactor = 1.15;
        }

        // 考虑高架桥/隧道缩短距离（主干道附近）
        double viaductFactor = 1.0;
        if (isNearMajorRoad(loc1) && isNearMajorRoad(loc2)) {
            viaductFactor = 0.9;
        }

        return baseDistance * oneWayFactor * downtownCoreFactor * viaductFactor;
    }

    // 判断是否为市中心核心区域
    private static boolean isDowntownCore(Location loc) {
        // 以成都市中心天府广场为中心，半径1公里范围
        double distToCore = calculateDistance(loc, new Location(DOWNTOWN_LAT, DOWNTOWN_LON, "市中心"));
        return distToCore < 1.0;
    }

    // 判断是否靠近主干道
    private static boolean isNearMajorRoad(Location loc) {
        // 示例主干道：人民南路、蜀都大道等
        double distToRoad1 = distanceToLine(loc, 30.6570, 104.0650, 30.6300, 104.0650); // 人民南路
        double distToRoad2 = distanceToLine(loc, 30.6570, 104.0400, 30.6570, 104.1000); // 蜀都大道

        return distToRoad1 < 0.3 || distToRoad2 < 0.3;
    }

    // 计算点到线的距离
    private static double distanceToLine(Location point, double lat1, double lon1, double lat2, double lon2) {
        // 简化计算，使用平面近似
        double A = point.getLatitude() - lat1;
        double B = point.getLongitude() - lon1;
        double C = lat2 - lat1;
        double D = lon2 - lon1;

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = -1;
        if (lenSq != 0) param = dot / lenSq;

        double xx, yy;
        if (param < 0) {
            xx = lat1;
            yy = lon1;
        } else if (param > 1) {
            xx = lat2;
            yy = lon2;
        } else {
            xx = lat1 + param * C;
            yy = lon1 + param * D;
        }

        double dx = point.getLatitude() - xx;
        double dy = point.getLongitude() - yy;

        return Math.sqrt(dx * dx + dy * dy) * 111.32; // 转换为公里
    }

    // 综合市中心距离计算
    private static double calculateEnhancedUrbanDistance(Location loc1, Location loc2) {
        double gridDistance = calculateGridDistance(loc1, loc2);
        double roadNetworkDistance = calculateUrbanDistance(loc1, loc2);

        // 加权平均，侧重考虑道路网络因素
        return gridDistance * 0.4 + roadNetworkDistance * 0.6;
    }

    // 市中心动态速度模型
    private static double calculateUrbanTravelTime(Location loc1, Location loc2) {
        double distance = calculateEnhancedUrbanDistance(loc1, loc2);

        // 根据距离分段计算速度
        double speed;
        if (distance < 0.5) {
            speed = 10; // 超短途，频繁启停，低速
        } else if (distance < 2) {
            speed = 18; // 短途，含红绿灯等待
        } else if (distance < 5) {
            speed = 25; // 中距离，可能有部分主干道
        } else if (distance < 10) {
            speed = 30; // 较长距离，更多主干道
        } else {
            speed = 35; // 长距离，可能包含高速路段
        }

        // 考虑市中心交通拥堵因素
        if (isDowntown(loc1) || isDowntown(loc2)) {
            speed *= 0.75; // 市中心速度降低25%
        }

        // 考虑早晚高峰拥堵（假设9:00-10:00和17:00-19:00为高峰）
        LocalTime currentTime = LocalTime.now();
        if ((currentTime.isAfter(LocalTime.of(8, 30)) && currentTime.isBefore(LocalTime.of(10, 0))) ||
                (currentTime.isAfter(LocalTime.of(16, 30)) && currentTime.isBefore(LocalTime.of(19, 0)))) {
            speed *= 0.6; // 高峰时段速度降低40%
        }

        // 返回旅行时间（小时）
        return distance / speed;
    }

    // 计算路线结束时间
    private static LocalTime calculateRouteEndTime(List<Order> route, Location depot, boolean returnToDepot) {
        LocalTime currentTime = START_TIME;
        Location currentLocation = depot;

        for (Order order : route) {
            double travelTime = calculateEnhancedUrbanTravelTime(currentLocation, order.getLocation());
            currentTime = currentTime.plusMinutes((long) (travelTime * 60));
            currentTime = currentTime.plusMinutes((long) (SERVICE_TIME * 60));
            currentLocation = order.getLocation();
        }

        // 如果需要返回配送中心，加上返回时间
        if (returnToDepot) {
            double travelTimeToDepot = calculateEnhancedUrbanTravelTime(currentLocation, depot);
            currentTime = currentTime.plusMinutes((long) (travelTimeToDepot * 60));
        }

        return currentTime;
    }

    // 输出规划结果
    private static void printRoutes(List<List<Order>> routes, Location depot, List<VehicleConfig> vehicleConfigs) {
        System.out.println("花店配送路线规划结果：");
        System.out.println("========================================");

        for (int i = 0; i < routes.size(); i++) {
            List<Order> route = routes.get(i);
            if (route.isEmpty()) {
                System.out.println("车辆 " + vehicleConfigs.get(i).getName() + " 没有分配订单");
                continue;
            }

            System.out.println("车辆 " + vehicleConfigs.get(i).getName() + " 配送路线：");
            System.out.println("起点：" + depot.getName());

            LocalTime currentTime = START_TIME;
            Location currentLocation = depot;
            double totalDistance = 0;

            for (Order order : route) {
                double travelTime = calculateEnhancedUrbanTravelTime(currentLocation, order.getLocation());
                currentTime = currentTime.plusMinutes((long) (travelTime * 60));
                totalDistance += calculateEnhancedUrbanDistance(currentLocation, order.getLocation());

                System.out.printf("  - %s (预计到达时间: %s, 截止时间: %s)%n",
                        order.getName(),
                        currentTime.format(TIME_FORMATTER),
                        order.isTimeCritical() ? order.getDeadline().format(TIME_FORMATTER) : "无");

                currentTime = currentTime.plusMinutes((long) (SERVICE_TIME * 60));
                currentLocation = order.getLocation();
            }

            // 如果需要返回配送中心
            boolean returnToDepot = shouldReturnToDepot(i, route, depot, vehicleConfigs);
            if (returnToDepot) {
                double travelTimeToDepot = calculateEnhancedUrbanTravelTime(currentLocation, depot);
                currentTime = currentTime.plusMinutes((long) (travelTimeToDepot * 60));
                totalDistance += calculateEnhancedUrbanDistance(currentLocation, depot);

                System.out.printf("  - 返回 %s (预计到达时间: %s)%n",
                        depot.getName(),
                        currentTime.format(TIME_FORMATTER));
            }

            System.out.printf("总行驶距离: %.2f 公里%n", totalDistance);
            System.out.printf("总行驶时间: %.2f 小时%n", totalDistance / 30); // 平均速度假设为30公里/小时
            System.out.printf("完成时间: %s%n", currentTime.format(TIME_FORMATTER));
            System.out.println("----------------------------------------");
        }

        // 计算总体统计信息
        double totalDistance = 0;
        int totalOrders = 0;

        for (List<Order> route : routes) {
            totalOrders += route.size();
            totalDistance += calculateRouteDistance(route, depot, new VehicleConfig(true, ""));
        }

        System.out.println("总体统计信息：");
        System.out.printf("总订单数: %d%n", totalOrders);
        System.out.printf("总行驶距离: %.2f 公里%n", totalDistance);
        System.out.printf("使用车辆数: %d%n", routes.size());
        System.out.println("========================================");
    }

    // 计算考虑多种因素的城市旅行时间
    private static double calculateEnhancedUrbanTravelTime(Location loc1, Location loc2) {
        double distance = calculateEnhancedUrbanDistance(loc1, loc2);

        // 基础速度（公里/小时）
        double baseSpeed;
        if (distance < 0.5) {
            baseSpeed = 10; // 超短途，频繁启停，低速
        } else if (distance < 2) {
            baseSpeed = 18; // 短途，含红绿灯等待
        } else if (distance < 5) {
            baseSpeed = 25; // 中距离，可能有部分主干道
        } else if (distance < 10) {
            baseSpeed = 30; // 较长距离，更多主干道
        } else {
            baseSpeed = 35; // 长距离，可能包含高速路段
        }

        // 考虑市中心交通拥堵因素
        if (isDowntown(loc1) || isDowntown(loc2)) {
            baseSpeed *= 0.75; // 市中心速度降低25%
        }

        // 考虑早晚高峰拥堵（假设9:00-10:00和17:00-19:00为高峰）
        LocalTime currentTime = LocalTime.now();
        if ((currentTime.isAfter(LocalTime.of(8, 30)) && currentTime.isBefore(LocalTime.of(10, 0))) ||
                (currentTime.isAfter(LocalTime.of(16, 30)) && currentTime.isBefore(LocalTime.of(19, 0)))) {
            baseSpeed *= 0.6; // 高峰时段速度降低40%
        }

        // 考虑天气因素（简化处理，假设当前天气为默认晴天）
        double weatherFactor = getWeatherFactor();
        baseSpeed *= weatherFactor;

        // 考虑道路类型（主干道、次干道、支路）
        double roadTypeFactor = getRoadTypeFactor(loc1, loc2);
        baseSpeed *= roadTypeFactor;

        // 考虑特殊事件（如演唱会、体育赛事等导致的局部拥堵）
        double eventFactor = getEventFactor(loc1, loc2);
        baseSpeed *= eventFactor;

        // 返回旅行时间（小时）
        return distance / baseSpeed;
    }

    // 获取天气因素对速度的影响
    private static double getWeatherFactor() {
        // 这里简化处理，实际应用中可以调用天气API获取实时天气
        // 晴天: 1.0, 小雨: 0.9, 大雨: 0.7, 雪: 0.6, 雾: 0.7
        return 1.0; // 默认晴天
    }

    // 获取道路类型对速度的影响
    private static double getRoadTypeFactor(Location loc1, Location loc2) {
        // 这里简化处理，实际应用中可以使用地图API获取道路类型
        if (isNearMajorRoad(loc1) && isNearMajorRoad(loc2)) {
            return 1.1; // 主干道速度提升10%
        } else if (isNearSubRoad(loc1) || isNearSubRoad(loc2)) {
            return 0.95; // 次干道正常速度
        } else {
            return 0.85; // 支路速度降低15%
        }
    }

    // 判断是否靠近次干道
    private static boolean isNearSubRoad(Location loc) {
        // 示例次干道：红星路、武侯祠大街等
        double distToRoad1 = distanceToLine(loc, 30.6570, 104.0800, 30.6400, 104.0800); // 红星路
        double distToRoad2 = distanceToLine(loc, 30.6475, 104.0400, 30.6475, 104.0600); // 武侯祠大街

        return distToRoad1 < 0.3 || distToRoad2 < 0.3;
    }

    // 获取特殊事件对速度的影响
    private static double getEventFactor(Location loc1, Location loc2) {
        // 这里简化处理，实际应用中可以获取实时事件数据
        // 检查是否靠近体育中心、会展中心等可能有大型活动的地点
        if (isNearEventVenue(loc1) || isNearEventVenue(loc2)) {
            // 检查是否在活动时间内
            if (isEventTime()) {
                return 0.7; // 活动期间速度降低30%
            }
        }
        return 1.0; // 正常情况
    }

    // 判断是否靠近活动场馆
    private static boolean isNearEventVenue(Location loc) {
        // 示例活动场馆：成都体育中心、世纪城新国际会展中心
        double distToStadium = calculateDistance(loc, new Location(30.6531, 104.0748, "成都体育中心"));
        double distToExpo = calculateDistance(loc, new Location(30.5857, 104.0569, "世纪城新国际会展中心"));

        return distToStadium < 1.0 || distToExpo < 1.0;
    }

    // 判断当前是否是活动时间
    private static boolean isEventTime() {
        // 简化处理，实际应用中应该查询活动时间表
        LocalTime currentTime = LocalTime.now();
        // 假设周末18:00-22:00可能有活动
        return currentTime.isAfter(LocalTime.of(18, 0)) &&
                currentTime.isBefore(LocalTime.of(22, 0));
    }
}

// 订单类
class Order {
    private String name;
    private Location location;
    private boolean timeCritical;
    private LocalTime deadline;

    public Order(String name, Location location, boolean timeCritical, LocalTime deadline) {
        this.name = name;
        this.location = location;
        this.timeCritical = timeCritical;
        this.deadline = deadline;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isTimeCritical() {
        return timeCritical;
    }

    public LocalTime getDeadline() {
        return deadline;
    }

    @Override
    public String toString() {
        return name;
    }
}

// 位置类
class Location {
    private double latitude;
    private double longitude;
    private String name;

    public Location(double latitude, double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

// 车辆配置类
class VehicleConfig {
    private boolean returnToDepot;
    private String name;

    public VehicleConfig(boolean returnToDepot, String name) {
        this.returnToDepot = returnToDepot;
        this.name = name;
    }

    public boolean isReturnToDepot() {
        return returnToDepot;
    }

    public String getName() {
        return name;
    }
}