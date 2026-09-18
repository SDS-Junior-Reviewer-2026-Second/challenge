# Car Assembly Challenge — 리팩토링 & 테스트

자동차 조립(부품 선택 → 조립 완료 → RUN/TEST) 콘솔 애플리케이션에 대한 리팩토링 및 단위 테스트 작업 내역입니다.

## 리팩토링 내역

### 1. 책임 분리 (`Assemble.java` 축소)

기존 `Assemble` 클래스가 **입력 루프 + 메뉴 출력 + 부품 선택 상태 관리 + RUN/TEST 실행 로직**을 한 곳에서 모두 처리하고 있어, 두 가지 책임을 별도 클래스로 분리했습니다.

| 클래스 | 책임 |
|---|---|
| `CarRunner` (신규) | 완성된 `Car`를 RUN / TEST 했을 때의 동작 (기존 `runProducedCar` / `testProducedCar`) |
| `AssemblyProgress` (신규) | 부품 선택 단계의 상태(현재 단계, `CarBuilder`, 완료 여부) 관리 및 전이 |
| `Assemble` | 입력 루프를 돌며 위 두 협력 객체에 위임하는 얇은 컨트롤러로 축소 |

### 2. `CarBuilder.build()` 유효성 검사 추가 (버그 방지)

기존에는 부품 하나가 비어있어도 `build()`가 `Car` 객체를 그대로 생성해서, 이후 `describe()` 등을 호출하는 시점에야 `NullPointerException`이 발생할 수 있었습니다. `build()` 시점에 **바로 `IllegalStateException`을 던지도록 fail-fast**하게 수정했습니다.

### 3. 출력 방식 일관성 정리

`Assemble`에서 한 곳만 `System.out.println(...)`을 직접 호출하던 부분을 `ConsoleView.printLine(...)`로 통일했습니다.

### 4. 매직 넘버 상수화

`pause(2000)`, `pause(1500)` 같은 숫자를 `RUN_RESULT_DISPLAY_MS`, `TEST_STARTING_DELAY_MS` 등 의미 있는 이름의 상수로 추출했습니다.

### 5. `pom.xml` 보완 (테스트 실행 불가 문제 수정)

`junit-jupiter-api`만 있고 `junit-jupiter-engine`이 없어 `mvn test`를 실행해도 테스트가 **0건**으로 잡히는 문제가 있었습니다. `junit-jupiter-engine` 의존성과 `maven-surefire-plugin`(JUnit5 인식이 가능한 3.5.2 버전)을 추가했습니다.

### 6. 코드 스타일 개선

`CarTest`의 문자열 비교 값을 `+` 연결 대신 Java 텍스트 블록(`"""`)으로 변경 (IntelliJ 인스펙션 반영).

---

## 테스트 내역

- **프레임워크**: JUnit 5 (`junit-jupiter-api` + `junit-jupiter-engine`) + AssertJ
- **총 38개 테스트, 전부 PASS** (실제 JDK 17 + Maven 환경에서 컴파일·실행하여 확인)

| 테스트 클래스 | 대상 | 주요 검증 내용 |
|---|---|---|
| `CarTest` | `Car` | getter, `hasBrokenEngine()`, `describe()` 출력 형식 |
| `CarBuilderTest` | `CarBuilder` | 정상 빌드, 부품 누락 시 `IllegalStateException` 발생 |
| `CarValidatorTest` | `CarValidator` | 5개 호환성 규칙 각각의 위반 여부, 다중 위반 시 첫 번째 규칙만 반환되는지 |
| `AssemblyProgressTest` | `AssemblyProgress` | 초기 상태, 단계 전이(`selectAndAdvance`), 뒤로가기(`goToPreviousStep`), 완료 시점, `reset()` |
| `CarRunnerTest` | `CarRunner` | RUN/TEST 시 규칙 위반·고장 엔진·정상 조합 각각에 대한 출력 메시지 (`System.out` 캡처로 검증) |
| `CarCompatibilityRuleTest` | `CarCompatibilityRule` (enum) | 5개 규칙 각각의 `isViolatedBy()` 참/거짓 케이스 |
| `AssembleStepTest` | `AssembleStep` (enum) | 단계 순서(`next`/`previous`), 뒤로가기 가능 여부, 입력 범위 검증, `select()`가 `CarBuilder`에 실제 반영되는지 |

### 실행 방법

```bash
mvn test
```

> ⚠️ 테스트 파일은 반드시 패키지와 동일한 하위 폴더에 위치해야 합니다.
> - `CarAssembly` 패키지 테스트 → `src/test/java/CarAssembly/`
> - `CarEnums` 패키지 테스트 → `src/test/java/CarEnums/`
