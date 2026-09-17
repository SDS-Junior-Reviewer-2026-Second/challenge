import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;


class AssembleTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws Exception {
        originalOut = System.out;
        System.setOut(new PrintStream(output));

        Field stackField = Assemble.class.getDeclaredField("stack");
        stackField.setAccessible(true);
        stackField.set(null, new int[5]);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    /*
     * ============================================================
     * Reflection Helper
     * ============================================================
     */

    private Object invokePrivateMethod(
            String methodName,
            Class<?>[] parameterTypes,
            Object... args
    ) throws Exception {

        Method method = Assemble.class.getDeclaredMethod(
                methodName,
                parameterTypes
        );

        method.setAccessible(true);

        return method.invoke(null, args);
    }

    private boolean invokeBooleanMethod(
            String methodName,
            Class<?>[] parameterTypes,
            Object... args
    ) throws Exception {

        return (boolean) invokePrivateMethod(
                methodName,
                parameterTypes,
                args
        );
    }

    private void setStack(
            int carType,
            int engine,
            int brake,
            int steering
    ) throws Exception {

        Field stackField = Assemble.class.getDeclaredField("stack");
        stackField.setAccessible(true);

        int[] stack = (int[]) stackField.get(null);

        stack[0] = carType;
        stack[1] = engine;
        stack[2] = brake;
        stack[3] = steering;
    }

    private String getOutput() {
        return output.toString();
    }


    /*
     * ============================================================
     * 입력값 범위 검증 테스트
     * ============================================================
     */

    @Nested
    @DisplayName("입력값 범위 검증")
    class RangeValidationTest {

        @Test
        @DisplayName("차량 타입은 1부터 3까지 입력하면 정상이다")
        void carType_WhenInputIsBetween1And3_ReturnsTrue() throws Exception {

            assertTrue(invokeBooleanMethod(
                    "isValidRange",
                    new Class[]{int.class, int.class},
                    0, 1
            ));

            assertTrue(invokeBooleanMethod(
                    "isValidRange",
                    new Class[]{int.class, int.class},
                    0, 2
            ));

            assertTrue(invokeBooleanMethod(
                    "isValidRange",
                    new Class[]{int.class, int.class},
                    0, 3
            ));
        }


        @Test
        @DisplayName("차량 타입이 1보다 작으면 입력 오류이다")
        void carType_WhenInputIsLessThan1_ReturnsFalse() throws Exception {

            boolean result = invokeBooleanMethod(
                    "isValidRange",
                    new Class[]{int.class, int.class},
                    0, 0
            );

            assertFalse(result);
            assertTrue(
                    getOutput().contains(
                            "ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능"
                    )
            );
        }


        @Test
        @DisplayName("차량 타입이 3보다 크면 입력 오류이다")
        void carType_WhenInputIsGreaterThan3_ReturnsFalse() throws Exception {

            boolean result = invokeBooleanMethod(
                    "isValidRange",
                    new Class[]{int.class, int.class},
                    0, 4
            );

            assertFalse(result);
        }


        @Test
        @DisplayName("엔진은 0부터 4까지 입력하면 정상이다")
        void engine_WhenInputIsBetween0And4_ReturnsTrue() throws Exception {

            for (int input = 0; input <= 4; input++) {

                assertTrue(invokeBooleanMethod(
                        "isValidRange",
                        new Class[]{int.class, int.class},
                        1, input
                ));
            }
        }


        @Test
        @DisplayName("엔진 번호가 4보다 크면 입력 오류이다")
        void engine_WhenInputIsGreaterThan4_ReturnsFalse() throws Exception {

            boolean result = invokeBooleanMethod(
                    "isValidRange",
                    new Class[]{int.class, int.class},
                    1, 5
            );

            assertFalse(result);
            assertTrue(
                    getOutput().contains(
                            "ERROR :: 엔진은 1 ~ 4 범위만 선택 가능"
                    )
            );
        }


        @Test
        @DisplayName("제동장치는 0부터 3까지 입력하면 정상이다")
        void brake_WhenInputIsBetween0And3_ReturnsTrue() throws Exception {

            for (int input = 0; input <= 3; input++) {

                assertTrue(invokeBooleanMethod(
                        "isValidRange",
                        new Class[]{int.class, int.class},
                        2, input
                ));
            }
        }


        @Test
        @DisplayName("제동장치 번호가 3보다 크면 입력 오류이다")
        void brake_WhenInputIsGreaterThan3_ReturnsFalse() throws Exception {

            boolean result = invokeBooleanMethod(
                    "isValidRange",
                    new Class[]{int.class, int.class},
                    2, 4
            );

            assertFalse(result);
            assertTrue(
                    getOutput().contains(
                            "ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능"
                    )
            );
        }


        @Test
        @DisplayName("조향장치는 0부터 2까지 입력하면 정상이다")
        void steering_WhenInputIsBetween0And2_ReturnsTrue() throws Exception {

            for (int input = 0; input <= 2; input++) {

                assertTrue(invokeBooleanMethod(
                        "isValidRange",
                        new Class[]{int.class, int.class},
                        3, input
                ));
            }
        }


        @Test
        @DisplayName("조향장치 번호가 2보다 크면 입력 오류이다")
        void steering_WhenInputIsGreaterThan2_ReturnsFalse() throws Exception {

            boolean result = invokeBooleanMethod(
                    "isValidRange",
                    new Class[]{int.class, int.class},
                    3, 3
            );

            assertFalse(result);
            assertTrue(
                    getOutput().contains(
                            "ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능"
                    )
            );
        }


        @Test
        @DisplayName("RUN TEST 메뉴는 0부터 2까지 입력하면 정상이다")
        void runTest_WhenInputIsBetween0And2_ReturnsTrue() throws Exception {

            for (int input = 0; input <= 2; input++) {

                assertTrue(invokeBooleanMethod(
                        "isValidRange",
                        new Class[]{int.class, int.class},
                        4, input
                ));
            }
        }


        @Test
        @DisplayName("RUN TEST 메뉴에서 2보다 큰 값은 입력 오류이다")
        void runTest_WhenInputIsGreaterThan2_ReturnsFalse() throws Exception {

            boolean result = invokeBooleanMethod(
                    "isValidRange",
                    new Class[]{int.class, int.class},
                    4, 3
            );

            assertFalse(result);

            assertTrue(
                    getOutput().contains(
                            "ERROR :: Run 또는 Test 중 하나를 선택 필요"
                    )
            );
        }
    }


    /*
     * ============================================================
     * 차량 타입 선택
     * ============================================================
     */

    @Nested
    @DisplayName("차량 타입 선택")
    class CarTypeSelectionTest {

        @Test
        @DisplayName("1을 선택하면 Sedan이 선택된다")
        void selectCarType_WhenInputIs1_SelectsSedan() throws Exception {

            invokePrivateMethod(
                    "selectCarType",
                    new Class[]{int.class},
                    1
            );

            assertTrue(
                    getOutput().contains(
                            "차량 타입으로 Sedan을 선택하셨습니다."
                    )
            );
        }


        @Test
        @DisplayName("2를 선택하면 SUV가 선택된다")
        void selectCarType_WhenInputIs2_SelectsSUV() throws Exception {

            invokePrivateMethod(
                    "selectCarType",
                    new Class[]{int.class},
                    2
            );

            assertTrue(
                    getOutput().contains(
                            "차량 타입으로 SUV을 선택하셨습니다."
                    )
            );
        }


        @Test
        @DisplayName("3을 선택하면 Truck이 선택된다")
        void selectCarType_WhenInputIs3_SelectsTruck() throws Exception {

            invokePrivateMethod(
                    "selectCarType",
                    new Class[]{int.class},
                    3
            );

            assertTrue(
                    getOutput().contains(
                            "차량 타입으로 Truck을 선택하셨습니다."
                    )
            );
        }
    }


    /*
     * ============================================================
     * 엔진 선택
     * ============================================================
     */

    @Nested
    @DisplayName("엔진 선택")
    class EngineSelectionTest {

        @Test
        @DisplayName("1을 선택하면 GM 엔진이 선택된다")
        void selectEngine_WhenInputIs1_SelectsGM() throws Exception {

            invokePrivateMethod(
                    "selectEngine",
                    new Class[]{int.class},
                    1
            );

            assertTrue(
                    getOutput().contains(
                            "GM 엔진을 선택하셨습니다."
                    )
            );
        }


        @Test
        @DisplayName("2를 선택하면 TOYOTA 엔진이 선택된다")
        void selectEngine_WhenInputIs2_SelectsToyota() throws Exception {

            invokePrivateMethod(
                    "selectEngine",
                    new Class[]{int.class},
                    2
            );

            assertTrue(
                    getOutput().contains(
                            "TOYOTA 엔진을 선택하셨습니다."
                    )
            );
        }


        @Test
        @DisplayName("3을 선택하면 WIA 엔진이 선택된다")
        void selectEngine_WhenInputIs3_SelectsWia() throws Exception {

            invokePrivateMethod(
                    "selectEngine",
                    new Class[]{int.class},
                    3
            );

            assertTrue(
                    getOutput().contains(
                            "WIA 엔진을 선택하셨습니다."
                    )
            );
        }


        @Test
        @DisplayName("4를 선택하면 고장난 엔진이 선택된다")
        void selectEngine_WhenInputIs4_SelectsBrokenEngine() throws Exception {

            invokePrivateMethod(
                    "selectEngine",
                    new Class[]{int.class},
                    4
            );

            assertTrue(
                    getOutput().contains(
                            "고장난 엔진 엔진을 선택하셨습니다."
                    )
            );
        }
    }


    /*
     * ============================================================
     * 제동장치 선택
     * ============================================================
     */

    @Nested
    @DisplayName("제동장치 선택")
    class BrakeSelectionTest {

        @Test
        @DisplayName("1을 선택하면 MANDO 제동장치가 선택된다")
        void selectBrake_WhenInputIs1_SelectsMando() throws Exception {

            invokePrivateMethod(
                    "selectBrakeSystem",
                    new Class[]{int.class},
                    1
            );

            assertTrue(
                    getOutput().contains(
                            "MANDO 제동장치를 선택하셨습니다."
                    )
            );
        }


        @Test
        @DisplayName("2를 선택하면 CONTINENTAL 제동장치가 선택된다")
        void selectBrake_WhenInputIs2_SelectsContinental() throws Exception {

            invokePrivateMethod(
                    "selectBrakeSystem",
                    new Class[]{int.class},
                    2
            );

            assertTrue(
                    getOutput().contains(
                            "CONTINENTAL 제동장치를 선택하셨습니다."
                    )
            );
        }


        @Test
        @DisplayName("3을 선택하면 BOSCH 제동장치가 선택된다")
        void selectBrake_WhenInputIs3_SelectsBosch() throws Exception {

            invokePrivateMethod(
                    "selectBrakeSystem",
                    new Class[]{int.class},
                    3
            );

            assertTrue(
                    getOutput().contains(
                            "BOSCH 제동장치를 선택하셨습니다."
                    )
            );
        }
    }


    /*
     * ============================================================
     * 조향장치 선택
     * ============================================================
     */

    @Nested
    @DisplayName("조향장치 선택")
    class SteeringSelectionTest {

        @Test
        @DisplayName("1을 선택하면 BOSCH 조향장치가 선택된다")
        void selectSteering_WhenInputIs1_SelectsBosch() throws Exception {

            invokePrivateMethod(
                    "selectSteeringSystem",
                    new Class[]{int.class},
                    1
            );

            assertTrue(
                    getOutput().contains(
                            "BOSCH 조향장치를 선택하셨습니다."
                    )
            );
        }


        @Test
        @DisplayName("2를 선택하면 MOBIS 조향장치가 선택된다")
        void selectSteering_WhenInputIs2_SelectsMobis() throws Exception {

            invokePrivateMethod(
                    "selectSteeringSystem",
                    new Class[]{int.class},
                    2
            );

            assertTrue(
                    getOutput().contains(
                            "MOBIS 조향장치를 선택하셨습니다."
                    )
            );
        }
    }


    /*
     * ============================================================
     * 부품 조합 유효성 검사
     * ============================================================
     */

    @Nested
    @DisplayName("자동차 부품 조합 검증")
    class CarCombinationValidationTest {

        @Test
        @DisplayName("Sedan에 Continental 제동장치를 사용하면 조합이 유효하지 않다")
        void sedanWithContinentalBrake_IsInvalid() throws Exception {

            setStack(
                    1,  // Sedan
                    1,  // GM
                    2,  // Continental
                    1   // Bosch
            );

            boolean result = invokeBooleanMethod(
                    "isValidCheck",
                    new Class[]{}
            );

            assertFalse(result);
        }


        @Test
        @DisplayName("SUV에 TOYOTA 엔진을 사용하면 조합이 유효하지 않다")
        void suvWithToyotaEngine_IsInvalid() throws Exception {

            setStack(
                    2,  // SUV
                    2,  // Toyota
                    1,  // Mando
                    1
            );

            boolean result = invokeBooleanMethod(
                    "isValidCheck",
                    new Class[]{}
            );

            assertFalse(result);
        }


        @Test
        @DisplayName("Truck에 WIA 엔진을 사용하면 조합이 유효하지 않다")
        void truckWithWiaEngine_IsInvalid() throws Exception {

            setStack(
                    3,
                    3,
                    2,
                    1
            );

            boolean result = invokeBooleanMethod(
                    "isValidCheck",
                    new Class[]{}
            );

            assertFalse(result);
        }


        @Test
        @DisplayName("Truck에 MANDO 제동장치를 사용하면 조합이 유효하지 않다")
        void truckWithMandoBrake_IsInvalid() throws Exception {

            setStack(
                    3,
                    1,
                    1,
                    1
            );

            boolean result = invokeBooleanMethod(
                    "isValidCheck",
                    new Class[]{}
            );

            assertFalse(result);
        }


        @Test
        @DisplayName("BOSCH 제동장치에 MOBIS 조향장치를 사용하면 조합이 유효하지 않다")
        void boschBrakeWithMobisSteering_IsInvalid() throws Exception {

            setStack(
                    1,
                    1,
                    3,  // Bosch Brake
                    2   // Mobis Steering
            );

            boolean result = invokeBooleanMethod(
                    "isValidCheck",
                    new Class[]{}
            );

            assertFalse(result);
        }


        @Test
        @DisplayName("허용된 부품 조합이면 유효한 자동차이다")
        void validPartsCombination_ReturnsTrue() throws Exception {

            setStack(
                    1,  // Sedan
                    1,  // GM
                    1,  // Mando
                    2   // Mobis
            );

            boolean result = invokeBooleanMethod(
                    "isValidCheck",
                    new Class[]{}
            );

            assertTrue(result);
        }
    }


    /*
     * ============================================================
     * 자동차 RUN
     * ============================================================
     */

    @Nested
    @DisplayName("자동차 실행")
    class RunProducedCarTest {

        @Test
        @DisplayName("유효하지 않은 부품 조합이면 자동차가 동작하지 않는다")
        void run_WhenPartsCombinationIsInvalid_CarDoesNotRun()
                throws Exception {

            // Sedan + Continental
            setStack(
                    1,
                    1,
                    2,
                    1
            );

            invokePrivateMethod(
                    "runProducedCar",
                    new Class[]{}
            );

            assertTrue(
                    getOutput().contains(
                            "자동차가 동작되지 않습니다"
                    )
            );
        }


        @Test
        @DisplayName("고장난 엔진을 선택하면 자동차가 움직이지 않는다")
        void run_WhenEngineIsBroken_CarDoesNotMove()
                throws Exception {

            setStack(
                    1,
                    4,  // 고장난 엔진
                    1,
                    1
            );

            invokePrivateMethod(
                    "runProducedCar",
                    new Class[]{}
            );

            String output = getOutput();

            assertAll(
                    () -> assertTrue(
                            output.contains(
                                    "엔진이 고장나있습니다."
                            )
                    ),
                    () -> assertTrue(
                            output.contains(
                                    "자동차가 움직이지 않습니다."
                            )
                    )
            );
        }


        @Test
        @DisplayName("정상적인 부품 조합이면 자동차 정보가 출력되고 동작한다")
        void run_WhenPartsCombinationIsValid_CarRunsSuccessfully()
                throws Exception {

            setStack(
                    1,  // Sedan
                    1,  // GM
                    1,  // Mando
                    2   // Mobis
            );

            invokePrivateMethod(
                    "runProducedCar",
                    new Class[]{}
            );

            String output = getOutput();

            assertAll(
                    () -> assertTrue(
                            output.contains("Car Type : Sedan")
                    ),

                    () -> assertTrue(
                            output.contains("Engine   : GM")
                    ),

                    () -> assertTrue(
                            output.contains("Brake    : Mando")
                    ),

                    () -> assertTrue(
                            output.contains("Steering : Mobis")
                    ),

                    () -> assertTrue(
                            output.contains(
                                    "자동차가 동작됩니다."
                            )
                    )
            );
        }


        @Test
        @DisplayName("SUV와 Continental 및 Bosch 조향장치로 정상 실행할 수 있다")
        void run_WhenValidSuvCombination_PrintsCorrectParts()
                throws Exception {

            setStack(
                    2,  // SUV
                    1,  // GM
                    2,  // Continental
                    1   // Bosch
            );

            invokePrivateMethod(
                    "runProducedCar",
                    new Class[]{}
            );

            String output = getOutput();

            assertAll(
                    () -> assertTrue(
                            output.contains("Car Type : SUV")
                    ),
                    () -> assertTrue(
                            output.contains("Brake    : Continental")
                    ),
                    () -> assertTrue(
                            output.contains("Steering : Bosch")
                    ),
                    () -> assertTrue(
                            output.contains(
                                    "자동차가 동작됩니다."
                            )
                    )
            );
        }


        @Test
        @DisplayName("Truck과 Bosch 제동 및 Bosch 조향장치로 정상 실행할 수 있다")
        void run_WhenValidTruckCombination_PrintsBoschBrake()
                throws Exception {

            setStack(
                    3,  // Truck
                    1,
                    3,  // Bosch Brake
                    1   // Bosch Steering
            );

            invokePrivateMethod(
                    "runProducedCar",
                    new Class[]{}
            );

            String output = getOutput();

            assertAll(
                    () -> assertTrue(
                            output.contains("Car Type : Truck")
                    ),
                    () -> assertTrue(
                            output.contains("Brake    : Bosch")
                    ),
                    () -> assertTrue(
                            output.contains(
                                    "자동차가 동작됩니다."
                            )
                    )
            );
        }
    }


    /*
     * ============================================================
     * 자동차 조합 Test
     * ============================================================
     */

    @Nested
    @DisplayName("자동차 부품 조합 테스트")
    class TestProducedCarTest {

        @Test
        @DisplayName("Sedan에 Continental 제동장치를 사용하면 테스트가 실패한다")
        void test_WhenSedanUsesContinentalBrake_Fails()
                throws Exception {

            setStack(
                    1,
                    1,
                    2,
                    1
            );

            invokePrivateMethod(
                    "testProducedCar",
                    new Class[]{}
            );

            String output = getOutput();

            assertAll(
                    () -> assertTrue(
                            output.contains(
                                    "자동차 부품 조합 테스트 결과 : FAIL"
                            )
                    ),
                    () -> assertTrue(
                            output.contains(
                                    "Sedan에는 Continental제동장치 사용 불가"
                            )
                    )
            );
        }


        @Test
        @DisplayName("SUV에 TOYOTA 엔진을 사용하면 테스트가 실패한다")
        void test_WhenSuvUsesToyotaEngine_Fails()
                throws Exception {

            setStack(
                    2,
                    2,
                    1,
                    1
            );

            invokePrivateMethod(
                    "testProducedCar",
                    new Class[]{}
            );

            assertTrue(
                    getOutput().contains(
                            "SUV에는 TOYOTA엔진 사용 불가"
                    )
            );
        }


        @Test
        @DisplayName("Truck에 WIA 엔진을 사용하면 테스트가 실패한다")
        void test_WhenTruckUsesWiaEngine_Fails()
                throws Exception {

            setStack(
                    3,
                    3,
                    2,
                    1
            );

            invokePrivateMethod(
                    "testProducedCar",
                    new Class[]{}
            );

            assertTrue(
                    getOutput().contains(
                            "Truck에는 WIA엔진 사용 불가"
                    )
            );
        }


        @Test
        @DisplayName("Truck에 MANDO 제동장치를 사용하면 테스트가 실패한다")
        void test_WhenTruckUsesMandoBrake_Fails()
                throws Exception {

            setStack(
                    3,
                    1,
                    1,
                    1
            );

            invokePrivateMethod(
                    "testProducedCar",
                    new Class[]{}
            );

            assertTrue(
                    getOutput().contains(
                            "Truck에는 Mando제동장치 사용 불가"
                    )
            );
        }


        @Test
        @DisplayName("BOSCH 제동장치에 MOBIS 조향장치를 사용하면 테스트가 실패한다")
        void test_WhenBoschBrakeUsesMobisSteering_Fails()
                throws Exception {

            setStack(
                    1,
                    1,
                    3,
                    2
            );

            invokePrivateMethod(
                    "testProducedCar",
                    new Class[]{}
            );

            assertTrue(
                    getOutput().contains(
                            "Bosch제동장치에는 Bosch조향장치 이외 사용 불가"
                    )
            );
        }


        @Test
        @DisplayName("사용 가능한 부품 조합이면 테스트 결과가 PASS이다")
        void test_WhenPartsCombinationIsValid_Passes()
                throws Exception {

            setStack(
                    1,
                    1,
                    1,
                    1
            );

            invokePrivateMethod(
                    "testProducedCar",
                    new Class[]{}
            );

            assertEquals(
                    "자동차 부품 조합 테스트 결과 : PASS",
                    getOutput().trim()
            );
        }
    }


    /*
     * ============================================================
     * 메뉴 출력
     * ============================================================
     */

    @Nested
    @DisplayName("메뉴 출력")
    class MenuTest {

        @Test
        @DisplayName("차량 타입 메뉴에는 Sedan SUV Truck이 표시된다")
        void showCarTypeMenu_PrintsAllCarTypes()
                throws Exception {

            invokePrivateMethod(
                    "showCarTypeMenu",
                    new Class[]{}
            );

            String output = getOutput();

            assertAll(
                    () -> assertTrue(output.contains("1. Sedan")),
                    () -> assertTrue(output.contains("2. SUV")),
                    () -> assertTrue(output.contains("3. Truck"))
            );
        }


        @Test
        @DisplayName("엔진 메뉴에는 GM TOYOTA WIA 고장난 엔진이 표시된다")
        void showEngineMenu_PrintsAllEngines()
                throws Exception {

            invokePrivateMethod(
                    "showEngineMenu",
                    new Class[]{}
            );

            String output = getOutput();

            assertAll(
                    () -> assertTrue(output.contains("1. GM")),
                    () -> assertTrue(output.contains("2. TOYOTA")),
                    () -> assertTrue(output.contains("3. WIA")),
                    () -> assertTrue(output.contains("4. 고장난 엔진"))
            );
        }


        @Test
        @DisplayName("제동장치 메뉴에는 MANDO CONTINENTAL BOSCH가 표시된다")
        void showBrakeMenu_PrintsAllBrakeSystems()
                throws Exception {

            invokePrivateMethod(
                    "showBrakeMenu",
                    new Class[]{}
            );

            String output = getOutput();

            assertAll(
                    () -> assertTrue(output.contains("1. MANDO")),
                    () -> assertTrue(output.contains("2. CONTINENTAL")),
                    () -> assertTrue(output.contains("3. BOSCH"))
            );
        }


        @Test
        @DisplayName("조향장치 메뉴에는 BOSCH와 MOBIS가 표시된다")
        void showSteeringMenu_PrintsAllSteeringSystems()
                throws Exception {

            invokePrivateMethod(
                    "showSteeringMenu",
                    new Class[]{}
            );

            String output = getOutput();

            assertAll(
                    () -> assertTrue(output.contains("1. BOSCH")),
                    () -> assertTrue(output.contains("2. MOBIS"))
            );
        }


        @Test
        @DisplayName("RUN TEST 메뉴에는 RUN과 Test가 표시된다")
        void showRunTestMenu_PrintsRunAndTest()
                throws Exception {

            invokePrivateMethod(
                    "showRunTestMenu",
                    new Class[]{}
            );

            String output = getOutput();

            assertAll(
                    () -> assertTrue(output.contains("1. RUN")),
                    () -> assertTrue(output.contains("2. Test"))
            );
        }
    }


    /*
     * ============================================================
     * fail 메서드
     * ============================================================
     */

    @Test
    @DisplayName("fail을 호출하면 FAIL과 실패 사유가 출력된다")
    void fail_WhenCalled_PrintsFailAndReason()
            throws Exception {

        invokePrivateMethod(
                "fail",
                new Class[]{String.class},
                "테스트 실패 원인"
        );

        String output = getOutput();

        assertAll(
                () -> assertTrue(
                        output.contains(
                                "자동차 부품 조합 테스트 결과 : FAIL"
                        )
                ),
                () -> assertTrue(
                        output.contains(
                                "테스트 실패 원인"
                        )
                )
        );
    }

    @Test
    @DisplayName("엔진 번호가 0보다 작으면 입력 오류이다")
    void engine_WhenInputIsNegative_ReturnsFalse() throws Exception {
        assertFalse(invokeBooleanMethod(
                "isValidRange",
                new Class[]{int.class, int.class},
                1, -1
        ));
    }

    @Test
    @DisplayName("제동장치 번호가 0보다 작으면 입력 오류이다")
    void brake_WhenInputIsNegative_ReturnsFalse() throws Exception {
        assertFalse(invokeBooleanMethod(
                "isValidRange",
                new Class[]{int.class, int.class},
                2, -1
        ));
    }

    @Test
    @DisplayName("조향장치 번호가 0보다 작으면 입력 오류이다")
    void steering_WhenInputIsNegative_ReturnsFalse() throws Exception {
        assertFalse(invokeBooleanMethod(
                "isValidRange",
                new Class[]{int.class, int.class},
                3, -1
        ));
    }

    @Test
    @DisplayName("RUN TEST 입력이 0보다 작으면 입력 오류이다")
    void runTest_WhenInputIsNegative_ReturnsFalse() throws Exception {
        assertFalse(invokeBooleanMethod(
                "isValidRange",
                new Class[]{int.class, int.class},
                4, -1
        ));
    }

    @Test
    @DisplayName("Sedan이지만 Continental이 아니면 첫 번째 제한 조건을 통과한다")
    void sedanWithNonContinentalBrake_PassesFirstCondition() throws Exception {
        setStack(
                1,  // Sedan
                1,  // GM
                1,  // Mando
                1
        );

        assertTrue(invokeBooleanMethod(
                "isValidCheck",
                new Class[]{}
        ));
    }

    @Test
    @DisplayName("SUV이지만 Toyota 엔진이 아니면 SUV 엔진 제한을 통과한다")
    void suvWithNonToyotaEngine_IsValid() throws Exception {
        setStack(
                2, // SUV
                1, // GM
                1,
                1
        );

        assertTrue(invokeBooleanMethod(
                "isValidCheck",
                new Class[]{}
        ));
    }

    @Test
    @DisplayName("Truck이지만 WIA 엔진이 아니고 Mando 제동장치도 아니면 유효하다")
    void truckWithNonWiaAndNonMando_IsValid() throws Exception {
        setStack(
                3, // Truck
                1, // GM
                2, // Continental
                1
        );

        assertTrue(invokeBooleanMethod(
                "isValidCheck",
                new Class[]{}
        ));
    }

    @Test
    @DisplayName("Bosch 제동장치와 Bosch 조향장치를 함께 사용하면 유효하다")
    void boschBrakeWithBoschSteering_IsValid() throws Exception {
        setStack(
                1,
                1,
                3, // Bosch brake
                1  // Bosch steering
        );

        assertTrue(invokeBooleanMethod(
                "isValidCheck",
                new Class[]{}
        ));
    }




}