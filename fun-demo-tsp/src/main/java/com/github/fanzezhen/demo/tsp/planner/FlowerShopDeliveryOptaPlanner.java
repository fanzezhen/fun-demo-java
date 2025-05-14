package com.github.fanzezhen.demo.tsp.planner;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * optaplanner 框架实现
 */
public class FlowerShopDeliveryOptaPlanner {

    public static void main(String[] args) {
        // 1. 创建配送点数据
        List<Location> locations = new ArrayList<>();
        locations.add(new Location("春熙路花店", 30.660083, 104.077774, new int[]{0, 1439}));
        locations.add(new Location("天府广场", 30.661939, 104.065861, new int[]{540, 720}));
        locations.add(new Location("锦里古街", 30.650534, 104.049828, new int[]{660, 840}));
        locations.add(new Location("宽窄巷子", 30.66737, 104.056006, new int[]{780, 960}));
        locations.add(new Location("环球中心", 30.573292, 104.063403, new int[]{900, 1080}));

        // 2. 创建问题实例
        VehicleRoutingSolution problem = new VehicleRoutingSolution();
        problem.setLocationList(locations);
        problem.setVehicleList(List.of(new Vehicle(0, locations.get(0), 620))); // 10:20 AM出发
        List<Customer> customers = locations.subList(1, locations.size()).stream().map(Customer::new).toList();
        problem.setCustomerList(customers);

        // 3. 创建距离矩阵
        long[][] distanceMatrix = createDistanceMatrix(locations);
        problem.setDistanceMatrix(distanceMatrix);

        // 4. 配置求解器
        // 修改求解器配置部分
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(VehicleRoutingSolution.class)
                .withEntityClasses(Vehicle.class, Customer.class)
                .withEasyScoreCalculatorClass(TimeWindowScoreCalculator.class)
                .withTerminationSpentLimit(Duration.ofSeconds(5L));

        // 使用正确的类加载器创建SolverFactory
        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.create(
                new SolverConfig()
                        .withSolutionClass(VehicleRoutingSolution.class)
                        .withEntityClasses(Vehicle.class, Customer.class)
                        .withEasyScoreCalculatorClass(TimeWindowScoreCalculator.class)
                        .withTerminationSpentLimit(Duration.ofSeconds(5L)));
        /*SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.createFromXmlResource(
                "solverConfig.xml",
                VehicleRoutingSolution.class.getClassLoader());*/
        Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();

        // 5. 求解问题
        VehicleRoutingSolution solution = solver.solve(problem);

        // 6. 打印解决方案
        printSolution(solution);
    }

    private static long[][] createDistanceMatrix(List<Location> locations) {
        long[][] matrix = new long[locations.size()][locations.size()];
        for (int i = 0; i < locations.size(); ++i) {
            Location from = locations.get(i);
            for (int j = 0; j < locations.size(); ++j) {
                Location to = locations.get(j);
                double latDiff = Math.abs(from.getLat() - to.getLat()) * 111000;
                double lngDiff = Math.abs(from.getLng() - to.getLng()) * 85000;
                matrix[i][j] = (long) (latDiff + lngDiff) / 100;
            }
        }
        return matrix;
    }

    private static void printSolution(VehicleRoutingSolution solution) {
        System.out.println("=== 花店配送最优路线 (OptaPlanner) ===");

        Vehicle vehicle = solution.getVehicleList().get(0);
        Customer current = vehicle.getNextCustomer();

        System.out.printf("车辆从 %s 出发 (时间: %02d:%02d)\n",
                vehicle.getDepot().getName(),
                vehicle.getDepartureTime() / 60,
                vehicle.getDepartureTime() % 60);

        while (current != null) {
            Location loc = current.getLocation();
            System.out.printf("到达: %-10s (坐标: %.4f,%.4f) 时间窗: %02d:%02d-%02d:%02d | ",
                    loc.getName(), loc.getLat(), loc.getLng(),
                    loc.getTimeWindow()[0] / 60, loc.getTimeWindow()[0] % 60,
                    loc.getTimeWindow()[1] / 60, loc.getTimeWindow()[1] % 60);

            System.out.printf("实际到达: %02d:%02d\n",
                    current.getArrivalTime() / 60,
                    current.getArrivalTime() % 60);

            current = current.getNextCustomer();
        }

        System.out.println("=== 配送完成 ===");
    }
}

class Location {
    private String name;
    private double lat;
    private double lng;
    private int[] timeWindow;

    public Location(String name, double lat, double lng, int[] timeWindow) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.timeWindow = timeWindow;
    }

    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public int[] getTimeWindow() { return timeWindow; }
}

@PlanningSolution
class VehicleRoutingSolution {
    private List<Location> locationList;
    private List<Vehicle> vehicleList;
    private List<Customer> customerList;
    private long[][] distanceMatrix;

    @ProblemFactCollectionProperty
    public List<Location> getLocationList() { return locationList; }
    public void setLocationList(List<Location> locationList) { this.locationList = locationList; }

    @PlanningEntityCollectionProperty
    public List<Vehicle> getVehicleList() { return vehicleList; }
    public void setVehicleList(List<Vehicle> vehicleList) { this.vehicleList = vehicleList; }

    @ProblemFactCollectionProperty
    public List<Customer> getCustomerList() { return customerList; }
    public void setCustomerList(List<Customer> customerList) { this.customerList = customerList; }

    @ProblemFactProperty
    public long[][] getDistanceMatrix() { return distanceMatrix; }
    public void setDistanceMatrix(long[][] distanceMatrix) { this.distanceMatrix = distanceMatrix; }
}

@PlanningEntity
class Vehicle {
    private int id;
    private Location depot;
    private int departureTime;
    private Customer nextCustomer;

    public Vehicle(int id, Location depot, int departureTime) {
        this.id = id;
        this.depot = depot;
        this.departureTime = departureTime;
    }

    @AnchorShadowVariable(sourceVariableName = "previousCustomer")
    public Customer getNextCustomer() { return nextCustomer; }
    public void setNextCustomer(Customer nextCustomer) { this.nextCustomer = nextCustomer; }

    public int getId() { return id; }
    public Location getDepot() { return depot; }
    public int getDepartureTime() { return departureTime; }
}

@PlanningEntity
class Customer {
    private Location location;
    private Customer previousCustomer;
    private Customer nextCustomer;
    private Integer arrivalTime;

    public Customer(Location location) {
        this.location = location;
    }

    @InverseRelationShadowVariable(sourceVariableName = "nextCustomer")
    public Customer getPreviousCustomer() { return previousCustomer; }
    public void setPreviousCustomer(Customer previousCustomer) { this.previousCustomer = previousCustomer; }

    @PlanningVariable(valueRangeProviderRefs = "customerRange", graphType = PlanningVariableGraphType.CHAINED)
    public Customer getNextCustomer() { return nextCustomer; }
    public void setNextCustomer(Customer nextCustomer) { this.nextCustomer = nextCustomer; }

    @ShadowVariable(
            variableListenerClass = ArrivalTimeUpdatingVariableListener.class,
            sourceVariableName = "previousCustomer")
    public Integer getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Integer arrivalTime) { this.arrivalTime = arrivalTime; }

    public Location getLocation() { return location; }
}

class ArrivalTimeUpdatingVariableListener implements VariableListener<VehicleRoutingSolution, Customer> {
    @Override
    public void beforeEntityAdded(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {}

    @Override
    public void afterEntityAdded(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {
        updateArrivalTime(scoreDirector, customer);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {}

    @Override
    public void afterVariableChanged(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {
        updateArrivalTime(scoreDirector, customer);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {}

    @Override
    public void afterEntityRemoved(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {}

    protected void updateArrivalTime(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {
        Customer previous = customer.getPreviousCustomer();
        VehicleRoutingSolution solution = scoreDirector.getWorkingSolution();
        long[][] distanceMatrix = solution.getDistanceMatrix();

        Integer arrivalTime = null;
        if (previous == null) {
            // 这是第一个客户，从车辆出发时间计算
            Vehicle vehicle = solution.getVehicleList().get(0);
            arrivalTime = vehicle.getDepartureTime() +
                    (int)distanceMatrix[0][solution.getLocationList().indexOf(customer.getLocation())];
        } else {
            arrivalTime = previous.getArrivalTime() +
                    (int)distanceMatrix[solution.getLocationList().indexOf(previous.getLocation())]
                            [solution.getLocationList().indexOf(customer.getLocation())];
        }

        scoreDirector.beforeVariableChanged(customer, "arrivalTime");
        customer.setArrivalTime(arrivalTime);
        scoreDirector.afterVariableChanged(customer, "arrivalTime");

        Customer next = customer.getNextCustomer();
        if (next != null) {
            updateArrivalTime(scoreDirector, next);
        }
    }
}

class TimeWindowScoreCalculator implements EasyScoreCalculator<VehicleRoutingSolution, HardSoftScore> {
    @Override
    public HardSoftScore calculateScore(VehicleRoutingSolution solution) {
        int hardScore = 0;
        int softScore = 0;

        for (Vehicle vehicle : solution.getVehicleList()) {
            Customer current = vehicle.getNextCustomer();
            while (current != null) {
                int[] timeWindow = current.getLocation().getTimeWindow();
                if (current.getArrivalTime() < timeWindow[0]) {
                    // 早到惩罚
                    hardScore -= (timeWindow[0] - current.getArrivalTime());
                } else if (current.getArrivalTime() > timeWindow[1]) {
                    // 迟到惩罚
                    hardScore -= (current.getArrivalTime() - timeWindow[1]);
                }

                // 总行驶时间作为软约束
                softScore -= current.getArrivalTime();

                current = current.getNextCustomer();
            }
        }

        return HardSoftScore.of(hardScore, softScore);
    }
}