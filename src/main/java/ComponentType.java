public class ComponentType {}

class CarType extends ComponentType {

    public static final CarType SEDAN = new CarType();
    public static final CarType SUV = new CarType();
    public static final CarType TRUCK = new CarType();

    private CarType() {}

    public static CarType fromInt(int type) {
        switch (type) {
            case 1:
                return SEDAN;
            case 2:
                return SUV;
            case 3:
                return TRUCK;
            default:
                throw new IllegalArgumentException("invalid type: " + type);
        }
    }
}

class EngineType extends ComponentType {

    public static final EngineType GM = new EngineType();
    public static final EngineType TOYOTA = new EngineType();
    public static final EngineType WIA = new EngineType();

    private EngineType() {}

    public static EngineType fromInt(int type) {
        switch (type) {
            case 1:
                return GM;
            case 2:
                return TOYOTA;
            case 3:
                return WIA;
            default:
                throw new IllegalArgumentException("invalid type: " + type);
        }
    }
}

class BrakeType extends ComponentType {

    public static final BrakeType MANDO = new BrakeType();
    public static final BrakeType CONTINENTAL = new BrakeType();
    public static final BrakeType BOSCH = new BrakeType();

    private BrakeType() {}

    public static BrakeType fromInt(int type) {
        switch (type) {
            case 1:
                return MANDO;
            case 2:
                return CONTINENTAL;
            case 3:
                return BOSCH;
            default:
                throw new IllegalArgumentException("invalid type: " + type);
        }
    }
}

class SteeringType extends ComponentType {

    public static final SteeringType BOSCH = new SteeringType();
    public static final SteeringType MOBIS = new SteeringType();

    private SteeringType() {}

    public static SteeringType fromInt(int type) {
        switch (type) {
            case 1:
                return BOSCH;
            case 2:
                return MOBIS;
            default:
                throw new IllegalArgumentException("invalid type: " + type);
        }
    }
}
