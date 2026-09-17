# Assemble.java 분석 및 리팩토링 제안

대상: `src/main/java/assemble/Assemble.java` (270줄, 단일 클래스, 전부 `static`)
빌드: Maven, Java 17, JUnit 5 + AssertJ 의존성 선언됨 (테스트 코드는 아직 없음)

---

## 1. 현재 코드가 하는 일

콘솔 기반의 자동차 조립 시뮬레이터. 다섯 단계의 상태 머신을 `int step` 으로 돌린다.

| step | 상수 | 화면 | 입력 범위 | 다음 |
|---|---|---|---|---|
| 0 | `CarType_Q` | 차량 타입 (Sedan/SUV/Truck) | 1~3 | 1 |
| 1 | `Engine_Q` | 엔진 (GM/TOYOTA/WIA/고장난 엔진) | 0~4 | 2 |
| 2 | `BrakeSystem_Q` | 제동장치 (MANDO/CONTINENTAL/BOSCH) | 0~3 | 3 |
| 3 | `SteeringSystem_Q` | 조향장치 (BOSCH/MOBIS) | 0~2 | 4 |
| 4 | `Run_Test` | RUN / Test | 0~2 | 0 (0 입력 시) |

- `0` 은 "뒤로가기" (Run_Test 에서는 "처음으로").
- 선택값은 전역 `static int[] stack` 에 인덱스 = step 으로 저장.
- RUN: 호환성 규칙 5개를 통과하고 엔진이 고장(4)이 아니면 사양을 출력.
- Test: 같은 규칙 5개 중 첫 번째 위반을 메시지와 함께 FAIL 로 출력, 없으면 PASS.

호환성 규칙 (`isValidCheck`, `testProducedCar` 양쪽에 동일하게 존재):

1. Sedan + Continental 제동장치 불가
2. SUV + TOYOTA 엔진 불가
3. Truck + WIA 엔진 불가
4. Truck + Mando 제동장치 불가
5. Bosch 제동장치 + (Bosch 이외) 조향장치 불가

---

## 2. 문제점 분석

### 2.1 구조 / 설계

| # | 문제 | 위치 | 영향 |
|---|---|---|---|
| S1 | **모든 것이 `static` + 전역 가변 상태** (`stack`) | `Assemble.java:17` | 인스턴스화 불가, 테스트 시 상태 격리 불가, 재사용 불가 |
| S2 | **`main()` 이 너무 많은 책임**을 가짐: 화면 출력, 입력 파싱, 범위 검증, 뒤로가기, 상태 전이, 부품 선택, 실행 | `Assemble.java:19-107` | 한 곳을 바꾸면 전체 흐름을 다시 읽어야 함 |
| S3 | **I/O 가 로직에 박혀 있음** — `System.out`, `Scanner`, `Thread.sleep` 이 검증/실행 메서드 안에 직접 호출됨 | 거의 모든 메서드 | JUnit/AssertJ 가 pom 에 있지만 실질적으로 단위 테스트 작성 불가 |
| S4 | **호환성 규칙이 두 군데에 복제**됨 (`isValidCheck` ↔ `testProducedCar`) | `Assemble.java:212-219`, `244-258` | 규칙 하나 추가/수정 시 두 곳을 동시에 고쳐야 하고, 어긋나도 컴파일러가 잡아주지 않음 |
| S5 | **부품 이름이 3~4곳에 중복** — 메뉴 문자열, `select*` 의 삼항 연산자, `runProducedCar` 의 로컬 배열/삼항 | `109-153`, `191-209`, `232-240` | 표기 불일치 이미 발생 (`MANDO` vs `Mando`, `BOSCH` vs `Bosch`) |
| S6 | **`step` 별 분기가 `switch` 로 4번 반복** (메뉴 출력, 범위 검증, 선택 처리, 전이) | `27-38`, `71-103`, `155-189` | 단계 하나 추가하면 4곳 수정 |
| S7 | 패키지 선언 없음 (default package) | `Assemble.java:1` | 다른 패키지에서 import 불가, Maven 관례 위배 |

### 2.2 네이밍 / 가독성

| # | 문제 | 예 |
|---|---|---|
| N1 | Java 상수 관례(`UPPER_SNAKE_CASE`) 위반, 혼합 표기 | `CarType_Q`, `BrakeSystem_Q`, `Run_Test`, `BOSCH_B`/`BOSCH_S` |
| N2 | `stack` 은 스택이 아니라 "선택값 배열". 크기 5인데 인덱스 4(`Run_Test`)는 사용되지 않음 | `Assemble.java:17` |
| N3 | 의미 없는 파라미터명 | `a`, `ans`, `buf`, `msg` |
| N4 | `isValidCheck()` — 무엇이 valid 한지 이름으로 알 수 없음 | `Assemble.java:212` |
| N5 | 상수를 정의해 두고도 안 씀 — 매직 넘버 잔존 | `a == 1 ? "Sedan"`, `stack[Engine_Q] == 4` (고장난 엔진 상수 없음), `stack[BrakeSystem_Q]==1? "Mando"` |
| N6 | `printf("...\n")` — 플랫폼 줄바꿈은 `%n` | 여러 곳 |

### 2.3 동작상 결함 / 잠재 버그

| # | 문제 | 위치 | 설명 |
|---|---|---|---|
| B1 | **에러 메시지와 실제 허용 범위 불일치** | `isValidRange`, `163-186` | 엔진은 0~4 허용인데 메시지는 "1 ~ 4", 제동/조향/Run 도 마찬가지로 0(뒤로가기)을 메시지에 안내하지 않음 |
| B2 | **EOF 처리 없음** | `Assemble.java:41` | 파이프 입력이나 Ctrl+D/Ctrl+Z 시 `sc.nextLine()` 이 `NoSuchElementException` 으로 프로그램이 죽음 |
| B3 | **`InterruptedException` 을 삼킴** | `delay()`, `266-270` | 인터럽트 플래그 복원(`Thread.currentThread().interrupt()`) 없음 |
| B4 | RUN 시 검사 순서 문제 | `runProducedCar`, `222-230` | 호환성 검사 → 고장 엔진 검사 순서라서, "고장 엔진 + 조합 위반" 이면 엔진 고장 안내 대신 "동작되지 않습니다" 만 출력. 의도인지 확인 필요 |
| B5 | 뒤로가기 시 이후 단계 선택값이 남아 있음 | `62-69` | 앞으로 진행하면 덮어쓰므로 현재는 문제 없지만, 향후 "선택 요약 표시" 같은 기능 추가 시 잘못된 값 노출 가능 |
| B6 | `sc.close()` 가 `System.in` 을 닫음 | `Assemble.java:106` | 단독 실행 시 무해하지만 다른 코드에 임베드되면 이후 표준 입력을 못 읽음 |
| B7 | ANSI 클리어 시퀀스 하드코딩 | `CLEAR_SCREEN` | 구형 Windows 콘솔에서 깨진 문자로 출력될 수 있음 (선택적) |

### 2.4 테스트 인프라

- `pom.xml` 에 `junit-jupiter-api` 만 있고 **`junit-jupiter-engine`(또는 `junit-jupiter` aggregate) 과 `maven-surefire-plugin` 3.x 가 없어** 실제로 `mvn test` 로 JUnit 5 테스트가 실행되지 않는다.
- `src/test` 디렉터리가 없다.

---

## 3. 리팩토링 방향

핵심 원칙: **"규칙과 상태는 순수 객체로, I/O 는 바깥 껍데기로"**. 동작(출력 문구)은 그대로 유지하면서 구조만 바꾼다.

### 3.1 목표 구조

```
src/main/java/assemble/
├── Assemble.java                 // main: 의존성 조립 후 AssembleApp.run()
├── AssembleApp.java              // 입력 루프 + 상태 전이 (I/O 는 Console 인터페이스로만)
├── model/
│   ├── CarType.java              // enum SEDAN, SUV, TRUCK
│   ├── Engine.java               // enum GM, TOYOTA, WIA, BROKEN
│   ├── BrakeSystem.java          // enum MANDO, CONTINENTAL, BOSCH
│   ├── SteeringSystem.java       // enum BOSCH, MOBIS
│   └── CarSpec.java              // 선택값 묶음 (불변 record 또는 빌더)
├── rule/
│   ├── CompatibilityRule.java    // 규칙 1개 = (조건, 실패 메시지)
│   └── CompatibilityRules.java   // 규칙 5개의 단일 정의 + violations(CarSpec)
├── flow/
│   └── Step.java                 // enum CAR_TYPE, ENGINE, BRAKE, STEERING, RUN_TEST + next()/back()
└── io/
    ├── Console.java              // interface: readLine(), print(), clear(), delay()
    └── SystemConsole.java        // Scanner/System.out/Thread.sleep 구현
```

파일 수가 늘어나는 게 부담이면 `model` 의 enum 들과 `rule` 만 분리해도 S4/S5/N5 의 대부분이 해결된다.

### 3.2 단계별 계획

#### Phase 0 — 안전망 만들기 (코드 변경 전)

1. `pom.xml` 보강
   ```xml
   <dependency>
       <groupId>org.junit.jupiter</groupId>
       <artifactId>junit-jupiter</artifactId>   <!-- api + engine + params -->
       <version>5.13.4</version>
       <scope>test</scope>
   </dependency>
   ...
   <build>
     <plugins>
       <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-surefire-plugin</artifactId>
         <version>3.5.3</version>
       </plugin>
     </plugins>
   </build>
   ```
2. **골든(스냅샷) 테스트** 작성: `System.setIn` / `System.setOut` 을 바꿔서 대표 시나리오(정상 RUN, 각 규칙 위반 Test, 고장 엔진, 뒤로가기, 잘못된 입력, exit)의 전체 출력을 캡처해 두고, 이후 리팩토링마다 출력이 동일한지 비교한다. `delay()` 때문에 느리므로 이 단계에서 `Thread.sleep` 만 먼저 주입 가능하게 빼는 것을 권장.

#### Phase 1 — 매직 넘버를 enum 으로 (S5, N1, N5 해결)

```java
public enum Engine {
    GM(1, "GM"), TOYOTA(2, "TOYOTA"), WIA(3, "WIA"), BROKEN(4, "고장난 엔진");

    private final int code;
    private final String displayName;
    Engine(int code, String displayName) { this.code = code; this.displayName = displayName; }

    public int code() { return code; }
    public String displayName() { return displayName; }
    public boolean isBroken() { return this == BROKEN; }

    public static Optional<Engine> fromCode(int code) {
        return Arrays.stream(values()).filter(e -> e.code == code).findFirst();
    }
}
```

- 메뉴 출력은 `for (Engine e : Engine.values()) println(e.code() + ". " + e.displayName())` 으로 생성 → 메뉴/선택 메시지/RUN 출력이 한 소스에서 나온다 (`Mando`/`MANDO` 불일치 자동 해소).
- 입력 범위 검증도 `fromCode(code).isPresent()` 로 대체 → `isValidRange` 의 `switch` 제거.
- `BOSCH_B`/`BOSCH_S` 는 `BrakeSystem.BOSCH`/`SteeringSystem.BOSCH` 로 자연스럽게 구분된다.

#### Phase 2 — `int[] stack` 을 `CarSpec` 으로 (S1, N2 해결)

```java
public record CarSpec(CarType carType, Engine engine,
                      BrakeSystem brake, SteeringSystem steering) {
    public boolean isComplete() {
        return carType != null && engine != null && brake != null && steering != null;
    }
    public CarSpec withCarType(CarType t)  { return new CarSpec(t, engine, brake, steering); }
    public CarSpec withEngine(Engine e)    { return new CarSpec(carType, e, brake, steering); }
    // ... withBrake, withSteering
}
```

- 불변이므로 뒤로가기 시 이후 단계를 `null` 로 되돌리기도 쉽다 (B5 대응).
- 빌더가 더 편하면 mutable `CarSpecBuilder` 로 해도 무방하다.

#### Phase 3 — 호환성 규칙 단일화 (S4 해결, 가장 효과 큼)

```java
public record CompatibilityRule(Predicate<CarSpec> violatedWhen, String failMessage) {}

public final class CompatibilityRules {
    public static final List<CompatibilityRule> ALL = List.of(
        new CompatibilityRule(s -> s.carType() == CarType.SEDAN && s.brake() == BrakeSystem.CONTINENTAL,
                              "Sedan에는 Continental제동장치 사용 불가"),
        new CompatibilityRule(s -> s.carType() == CarType.SUV && s.engine() == Engine.TOYOTA,
                              "SUV에는 TOYOTA엔진 사용 불가"),
        new CompatibilityRule(s -> s.carType() == CarType.TRUCK && s.engine() == Engine.WIA,
                              "Truck에는 WIA엔진 사용 불가"),
        new CompatibilityRule(s -> s.carType() == CarType.TRUCK && s.brake() == BrakeSystem.MANDO,
                              "Truck에는 Mando제동장치 사용 불가"),
        new CompatibilityRule(s -> s.brake() == BrakeSystem.BOSCH && s.steering() != SteeringSystem.BOSCH,
                              "Bosch제동장치에는 Bosch조향장치 이외 사용 불가")
    );

    /** 위반한 규칙의 메시지 목록. 비어 있으면 PASS. */
    public static List<String> violations(CarSpec spec) {
        return ALL.stream()
                  .filter(r -> r.violatedWhen().test(spec))
                  .map(CompatibilityRule::failMessage)
                  .toList();
    }
}
```

- `isValidCheck()` → `violations(spec).isEmpty()`
- `testProducedCar()` → `violations(spec)` 의 첫 항목을 FAIL 로 출력 (현재 동작 유지). 전부 보여주고 싶으면 목록 전체 출력으로 확장 가능.
- **규칙 추가 = 리스트에 한 줄 추가**. 이 클래스는 I/O 가 전혀 없어서 그대로 단위 테스트 대상이 된다.

#### Phase 4 — 상태 머신 정리 (S2, S6 해결)

```java
public enum Step {
    CAR_TYPE, ENGINE, BRAKE, STEERING, RUN_TEST;

    public Step next() { return this == RUN_TEST ? RUN_TEST : values()[ordinal() + 1]; }
    public Step back() { return this == RUN_TEST ? CAR_TYPE : values()[Math.max(0, ordinal() - 1)]; }
    public boolean allowsBack() { return this != CAR_TYPE; }
}
```

- `main()` 의 4개 `switch` 를 `Step` 하나에 대한 `switch` (또는 Step 별 핸들러 맵) 하나로 축소.
- 메뉴 제목/화면 문구도 `Step` 에 붙이거나 `MenuRenderer` 로 모아서 출력 계층으로 밀어낸다.

#### Phase 5 — I/O 분리 (S3, B2, B3, B6 해결)

```java
public interface Console {
    Optional<String> readLine();          // EOF 이면 empty → 루프 종료 (B2)
    void print(String s);
    void println(String s);
    void clear();
    void delay(int millis);
}
```

- `SystemConsole` 에서 `Scanner`, `System.out`, `Thread.sleep` 을 구현하고, `delay` 에서는 인터럽트 플래그를 복원한다 (B3).
- `Scanner` 는 `SystemConsole` 이 소유하고 `System.in` 을 닫지 않는다 (B6).
- 테스트에서는 `FakeConsole(List<String> scriptedInputs)` 로 입력을 주입하고 출력을 `StringBuilder` 에 모아 검증 → `System.setIn/Out` 트릭이 필요 없어진다.
- 결과적으로 `AssembleApp` 은 `new AssembleApp(console).run()` 형태가 되고, `Assemble.main` 은 조립만 담당한다.

#### Phase 6 — 마무리

- 패키지 선언 추가 (`package assemble;`) 및 디렉터리 이동 (S7).
- 파라미터명 정리 (`a` → `choice`, `buf` → `input`, `msg` → `reason`).
- `printf("\n")` → `%n` 또는 `println` (N6).
- B1 메시지 정정: "엔진은 0 ~ 4 범위만 선택 가능" 처럼 실제 범위와 맞추거나, 뒤로가기(0)를 별도로 처리한 뒤 검증하도록 순서를 바꾼다.
- B4 순서 결정: "엔진 고장" 을 먼저 검사할지, 호환성 위반을 먼저 검사할지 정하고 테스트로 고정한다.

### 3.3 우선순위 요약

| 순서 | 항목 | 해결되는 문제 | 난이도 | 효과 |
|---|---|---|---|---|
| 1 | Phase 0 테스트 인프라 + 골든 테스트 | 회귀 방지 | 낮음 | 높음 (이후 모든 작업의 전제) |
| 2 | Phase 3 규칙 단일화 | S4 | 낮음 | **매우 높음** (중복 제거, 테스트 가능) |
| 3 | Phase 1 enum 도입 | S5, N1, N5 | 중간 | 높음 |
| 4 | Phase 2 CarSpec | S1, N2, B5 | 낮음 | 중간 |
| 5 | Phase 5 Console 분리 | S3, B2, B3, B6 | 중간 | 높음 (앱 전체 테스트 가능) |
| 6 | Phase 4 Step enum | S2, S6 | 중간 | 중간 |
| 7 | Phase 6 마무리 | S7, N3, N6, B1, B4 | 낮음 | 낮음~중간 |

Phase 3 → 1 → 2 순으로 하면 각 단계가 독립적으로 커밋 가능하고, 기존 `Assemble.java` 는 마지막까지 컴파일되는 상태를 유지할 수 있다.

---

## 4. 리팩토링 후 테스트 예시

```java
class CompatibilityRulesTest {
    @Test
    void sedanWithContinentalBrakeIsRejected() {
        CarSpec spec = new CarSpec(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.MOBIS);
        assertThat(CompatibilityRules.violations(spec))
            .containsExactly("Sedan에는 Continental제동장치 사용 불가");
    }

    @Test
    void boschBrakeRequiresBoschSteering() {
        CarSpec ok  = new CarSpec(CarType.SUV, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH);
        CarSpec bad = ok.withSteering(SteeringSystem.MOBIS);
        assertThat(CompatibilityRules.violations(ok)).isEmpty();
        assertThat(CompatibilityRules.violations(bad)).hasSize(1);
    }
}

class AssembleAppTest {
    @Test
    void fullRunPrintsSpec() {
        FakeConsole console = new FakeConsole("1", "1", "1", "1", "1", "exit");
        new AssembleApp(console).run();
        assertThat(console.output())
            .contains("Car Type : Sedan")
            .contains("자동차가 동작됩니다.")
            .contains("바이바이");
    }
}
```

---

## 5. 리팩토링 전에 확인할 것 (동작 변경 여부 결정 필요)

1. **B1** — 에러 메시지의 범위 표기를 실제 범위(0 포함)로 고칠지, 아니면 0 은 "뒤로가기" 로 안내만 하고 메시지는 유지할지.
2. **B4** — RUN 에서 고장 엔진 + 조합 위반 동시 발생 시 어떤 메시지를 우선할지.
3. **Test 결과** — 위반이 여러 개일 때 첫 번째만 출력(현재) vs 전부 출력.
4. **뒤로가기** — 뒤로 갔을 때 이후 단계 선택을 초기화할지(권장) vs 유지할지(현재).

위 4가지는 "기존 동작 유지" 로 두면 순수 리팩토링이고, 바꾸면 기능 변경이므로 골든 테스트를 함께 갱신해야 한다.
