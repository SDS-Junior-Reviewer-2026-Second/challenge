package mission2;

public class Car {
    public static final String[] carTypeList = {"", "Sedan", "SUV", "Truck"};
    public static final String[] engineList = {"", "GM", "TOYOTA", "WIA", "고장난 엔진"};
    public static final String[] brakeList = {"", "Mando", "Continental", "Bosch"};
    public static final String[] steeringList = {"", "Bosch", "Mobis"};

    String carType = "";
    String engine = "";
    String brake = "";
    String steering = "";


    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getBrake() {
        return brake;
    }

    public void setBrake(String brake) {
        this.brake = brake;
    }

    public String getSteering() {
        return steering;
    }

    public void setSteering(String steering) {
        this.steering = steering;
    }
}
