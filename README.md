# 차량 조립 프로그램 리팩토링

Java 17 / Maven / JUnit 5 프로젝트입니다. 첨부한 원본 `Assemble`의 메뉴, 선택 번호,
출력 문구, 부품 호환성 규칙, 대기 시간을 유지하면서 실제 프로그램을 10개 파일로 나눴습니다.

## 실행하기

IntelliJ에서 이 폴더의 `pom.xml`을 프로젝트로 열고, Project SDK를 JDK 17 이상으로 지정하세요.
Maven 의존성을 불러온 후 `src/main/java/Assemble.java`의 `main()`을 실행하면 됩니다.

명령줄에서는 다음과 같이 실행할 수 있습니다.

```bash
mvn clean package
java -Dfile.encoding=UTF-8 -jar target/assemble-refactored-1.0.0.jar
```

Maven/JUnit 의존성은 최초 사용 시 다운로드가 필요합니다. 실행 프로그램 자체는 외부 라이브러리를 사용하지 않습니다.

기존 프로젝트에 적용하려면 `src/main/java`의 `Assemble.java`와 `carassembly` 폴더를 함께 복사하고,
기존의 중복 `Assemble.java`를 교체하세요. 테스트는 `src/test/java`와 `src/test/resources`를 함께 복사합니다.
기존 `pom.xml`이 있다면 덮어쓰지 말고 Java 17 설정, JUnit 의존성, 테스트 실행 플러그인 설정을 반영하세요.

## 파일별 역할

| 파일/폴더 | 담당 역할 |
| --- | --- |
| `src/main/java/Assemble.java` | 실행 진입점, 콘솔과 컨트롤러 연결, 실제 대기 |
| `carassembly/application/AssemblyController.java` | 입력 해석, 부품 선택 상태, 진행과 뒤로가기, RUN/Test 호출 |
| `carassembly/application/AssemblyStep.java` | 메뉴 정보, 입력 범위, 단계 이동 |
| `carassembly/console/ConsoleView.java` | 콘솔 입력, 메뉴·선택·결과 출력 |
| `carassembly/domain/Car.java` | 불변 차량 데이터, 호환성 규칙, 주행 가능 여부 |
| `carassembly/domain/CarType.java` | 차량 종류 |
| `carassembly/domain/Engine.java` | 엔진 종류와 고장난 엔진 |
| `carassembly/domain/BrakeSystem.java` | 제동장치 종류 |
| `carassembly/domain/SteeringSystem.java` | 조향장치 종류 |
| `carassembly/domain/RunStatus.java` | 정상 주행, 부품 조합 오류, 엔진 고장 결과 |

위 표의 `carassembly` 경로는 `src/main/java` 기준입니다.

## 리팩토링 포인트

- `static int[] stack` 대신 컨트롤러가 타입이 있는 부품 필드로 선택 상태를 관리합니다.
  컨트롤러 객체마다 상태가 독립적입니다.
- 숫자 상수는 `enum`으로, 완성된 차량은 불변 `record Car`로 표현합니다.
- 원본의 `isValidCheck()`와 `testProducedCar()`에 중복되어 있던 호환성 규칙을
  `Car.findCompatibilityError()` 한곳으로 합쳤습니다.
- 차량 규칙에서는 콘솔을 사용하지 않습니다. 컨트롤러가 규칙의 결과를 받아 화면에 전달합니다.
- 메뉴와 선택은 같은 enum 순서를 사용하므로 메뉴 번호와 선택 부품을 따로 관리하지 않습니다.
- `Scanner`, `PrintStream`, `IntConsumer`를 외부에서 전달합니다.
  테스트에서는 메모리 입력·출력과 대기 기록 함수를 전달하므로 실제 입력이나 `sleep`이 필요 없습니다.

## 유지한 동작과 보완한 경계 처리

원본의 다섯 가지 호환성 규칙과 검사 순서는 그대로입니다. 여러 문제가 있어도 첫 번째 오류만 표시합니다.
차량 종류 화면에서는 `0`이 오류이고, 중간 단계에서는 이전 화면, RUN/Test 화면에서는 처음 화면으로 이동합니다.
RUN/Test 후에는 같은 차량으로 계속 실행할 수 있습니다.

**화면의 `Test`는 부품 호환성 검사입니다.** 원본과 동일하게 고장난 엔진도 다른 부품과 호환되면
Test 결과가 PASS입니다. RUN에서는 엔진 고장으로 주행이 거절됩니다.
호환성 문제까지 있는 경우에는 부품 조합 오류가 우선합니다.

아래 두 경계 동작은 보완했습니다.

- 입력 스트림이 끝나면 `NoSuchElementException` 대신 정상 종료합니다.
- 대기 중 인터럽트가 발생하면 인터럽트 상태를 유지하고 다음 입력을 처리하지 않고 종료합니다.

## JUnit 테스트

IntelliJ에서 `src/test/java` 폴더를 실행하거나 다음 명령을 사용하세요.

```bash
mvn test
```

| 테스트 파일 | 검증 대상 |
| --- | --- |
| `CarTest` | 72가지 전체 조합, 개별 금지 규칙, 오류 우선순위, 고장난 엔진, 누락 부품 |
| `AssemblyStepTest` | 단계별 입력 범위의 경계, 다음 단계와 뒤로가기 |
| `AssemblyControllerTest` | 전체 조합의 RUN/Test, 비정상 입력 후 재선택, 뒤로가기 후 변경, 재조립, 반복 실행, exit, EOF, 공백, 상태 분리, 인터럽트 |
| `ConsoleViewTest` | 메뉴 순서와 명칭, 빈 입력과 EOF 구분 |

`src/test/resources/car-cases.csv`는 새 구현으로 계산한 값이 아니라 **첨부 원본을 실행해서 얻은 72개 조합의 기대값**입니다.
호환성 규칙을 의도적으로 변경할 때에만 기준 코드와 이 파일을 함께 검토하세요.

## Maven 없이 원본과 비교하기

JDK 17 이상만 있어도 실행할 수 있는 별도 회귀 검증을 포함했습니다.

Windows PowerShell:

```powershell
.\verify.cmd
```

macOS/Linux:

```bash
sh verify.sh
```

예상 결과:

```text
PASS: 326 regression checks
```

이 검증은 전체 콘솔 출력과 대기 요청까지 원본과 비교합니다. 정상 부품 조합뿐 아니라
비정상 입력, 뒤로가기, 재조립, 고정된 난수 시드의 입력 시나리오도 확인합니다.
테스트 중 실제로 기다리지 않습니다.

`verification/LegacyAssemble.java`는 비교 기준을 보존한 원본입니다. 클래스 이름을 바꾸고
실제 대기를 기록으로 대체한 코드이며, Maven의 프로그램/테스트 소스에 포함되지 않습니다.
현재 프로그램은 `src/main/java` 안의 파일만 사용합니다.

보조 검증 후에는 Maven 없이도 다음 명령으로 프로그램을 실행할 수 있습니다.

```bash
java -Dfile.encoding=UTF-8 -cp target/regression-classes Assemble
```

## 이 환경에서 확인한 결과

- Java 17로 프로그램과 보조 검증 코드 컴파일 성공.
- 보조 회귀 검증 **326개 통과**.
- Maven/JUnit 도구가 없고 의존성 다운로드에 접근할 수 없어 **JUnit 엔진을 통한 테스트 실행과 Maven 빌드는 수행하지 못했습니다**.
  포함한 JUnit 테스트는 개발 환경에서 `mvn test`로 실행하세요.
- `verify.sh`는 Linux에서 실행했습니다. Windows의 `verify.cmd`는 실행 환경이 없어 직접 실행하지 못했습니다.

자세한 검증 구분은 `VALIDATION.md`에 정리했습니다.
