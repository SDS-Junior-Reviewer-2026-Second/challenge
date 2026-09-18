# Assemble.java 리팩토링

대상: `src/main/java/assemble/Assemble.java` — 리팩토링 전 270줄, 단일 클래스, 전부 `static`, 테스트 없음
빌드: Maven, Java 17, JUnit 5 + AssertJ

원칙: **"규칙과 상태는 순수 객체로, I/O 는 바깥 껍데기로"**. 화면에 보이는 동작(문구·순서)은 그대로 두고 구조만 바꾼다. 동작을 바꾼 곳은 `fix:` 커밋으로 분리한다.

---

## 1. 프로그램이 하는 일

콘솔 기반 자동차 조립 시뮬레이터. 다섯 단계를 돌며 부품을 고르고 RUN / Test 를 수행한다.

| 단계 | 화면 | 입력 | 다음 |
|---|---|---|---|
| 차량 타입 | Sedan / SUV / Truck | 1~3 | 엔진 |
| 엔진 | GM / TOYOTA / WIA / 고장난 엔진 | 0~4 | 제동장치 |
| 제동장치 | MANDO / CONTINENTAL / BOSCH | 0~3 | 조향장치 |
| 조향장치 | BOSCH / MOBIS | 0~2 | RUN/Test |
| RUN/Test | RUN / Test | 0~2 | (0 이면 처음으로) |

- `0` 은 뒤로가기, `exit` 는 종료.
- RUN: 호환성 규칙 5개를 통과하고 엔진이 고장이 아니면 사양을 출력한다.
- Test: 첫 번째 위반 규칙을 FAIL 메시지로 출력하고, 없으면 PASS.

호환성 규칙:

1. Sedan + Continental 제동장치 불가
2. SUV + TOYOTA 엔진 불가
3. Truck + WIA 엔진 불가
4. Truck + Mando 제동장치 불가
5. Bosch 제동장치 + Bosch 이외 조향장치 불가

---

## 2. 리팩토링 전 문제점

### 2.1 구조

| # | 문제 | 영향 | 처리 |
|---|---|---|---|
| S1 | 모든 것이 `static` + 전역 가변 상태 `int[] stack` | 인스턴스화·상태 격리·재사용 불가 | `AssembleApp` 인스턴스 + 불변 `CarSpec` |
| S2 | `main()` 이 화면 출력·입력 파싱·범위 검증·상태 전이·부품 선택·실행을 전부 담당 | 한 곳을 바꾸면 전체를 다시 읽어야 함 | `AssembleApp`(흐름) / `ConsoleView`(출력) / `CarInspector`(판정) 로 분리 |
| S3 | `System.out`, `Scanner`, `Thread.sleep` 이 로직 안에 직접 호출됨 | 단위 테스트 불가 | `Console` 인터페이스 + 테스트용 `FakeConsole` |
| S4 | 호환성 규칙이 `isValidCheck` 와 `testProducedCar` 두 곳에 복제됨 | 규칙 변경 시 두 곳을 맞춰야 하고 어긋나도 컴파일러가 못 잡음 | `CompatibilityRules.ALL` 한 곳으로 단일화 |
| S5 | 부품 이름이 메뉴 문자열·삼항 연산자·로컬 배열 등 3~4곳에 중복 | 이미 표기 불일치 발생 (`MANDO` vs `Mando`) | 부품별 enum 의 `displayName` |
| S6 | `step` 별 `switch` 가 4번 반복 (메뉴, 검증, 선택, 전이) | 단계 추가 시 4곳 수정 | `Step` enum 이 선택지·전이를 소유, `switch` 는 `apply()` 하나 |
| S7 | 패키지 선언 없음 | Maven 관례 위배, 다른 패키지에서 import 불가 | `package assemble` |

### 2.2 네이밍

| # | 문제 | 처리 |
|---|---|---|
| N1 | 상수 관례 위반·혼합 표기 (`CarType_Q`, `BOSCH_B`/`BOSCH_S`) | enum 상수로 대체 (`BrakeSystem.BOSCH` / `SteeringSystem.BOSCH`) |
| N2 | `stack` 은 스택이 아니라 선택값 배열, 인덱스 4 는 미사용 | `CarSpec` record |
| N3 | 의미 없는 파라미터명 (`a`, `ans`, `buf`, `msg`) | `choice`, `input`, `option` 등으로 정리 |
| N4 | `isValidCheck()` — 무엇이 valid 한지 알 수 없음 | `CarInspector.violations()` / `run()` |
| N5 | 상수를 정의해 두고 매직 넘버 사용 (`== 4` 고장 엔진 등) | `Engine.BROKEN.isBroken()` |
| N6 | `printf("\n")`(LF) 와 `println`(플랫폼) 줄바꿈 혼용 | 전부 `println` |

### 2.3 동작상 결함

| # | 문제 | 처리 |
|---|---|---|
| B1 | 에러 메시지 범위 표기가 실제와 다름 (엔진은 0~4 허용인데 "1 ~ 4") | **원문 유지** — 5장 결정 대기 |
| B2 | EOF(파이프 입력 종료, Ctrl+Z) 시 `NoSuchElementException` 으로 크래시 | **수정** — `fix:` 커밋 |
| B3 | `delay()` 가 `InterruptedException` 을 삼킴 | **수정** — `fix:` 커밋 |
| B4 | RUN 에서 호환성 → 고장 엔진 순으로 검사해, 둘 다 해당하면 엔진 고장 안내가 안 나옴 | **원문 유지** — 순서를 `CarInspector.run()` 한 곳에 명시하고 테스트로 고정. 5장 결정 대기 |
| B5 | 뒤로가기 시 이후 단계 선택값이 남음 | **원문 유지** — 5장 결정 대기 |
| B6 | `sc.close()` 가 `System.in` 을 닫음 | **수정** — `fix:` 커밋 |
| B7 | ANSI 클리어 시퀀스 하드코딩 | 유지 (`SystemConsole.CLEAR_SCREEN`) |

### 2.4 테스트 인프라

`pom.xml` 에 `junit-jupiter-api` 만 있고 엔진과 surefire 3.x 가 없어 `mvn test` 로 JUnit 5 가 실행되지 않았다. → `junit-jupiter` aggregate + surefire 3.5.3 + JaCoCo 추가.

---

## 3. 결과 구조

```
assemble/
├── Assemble.java            main: new AssembleApp(new SystemConsole(), CarInspector.standard()).run()
├── AssembleApp.java         입력 루프 + 상태 전이 (컨트롤러, 105줄)
├── flow/   Step, RunAction  단계별 선택지와 next()/back()
├── model/  CarType, Engine, BrakeSystem, SteeringSystem  (MenuOption 구현 enum)
│           CarSpec          불변 record, withXxx() 로 갱신
│           MenuOption(s)    "번호로 고르는 항목" 추상화와 fromCode 헬퍼
├── rule/   CompatibilityRule(s)  규칙 = (위반 조건, 메시지), ALL 리스트
│           CarInspector     violations(spec), run(spec) → RunResult
│           RunResult        INCOMPATIBLE / ENGINE_BROKEN / RUNNABLE
├── ui/     ConsoleView      사용자에게 보이는 모든 문구와 delay
└── io/     Console, SystemConsole  (+ 테스트의 FakeConsole)
```

핵심 흐름: `AssembleApp.run()` 이 `view.showMenu → view.prompt → parseChoice → apply` 를 반복한다. `apply` 는 0 이면 `step.back()`, 아니면 `select(option, spec::withX)` 로 사양을 갱신하고 `step.next()`. RUN/Test 는 `inspector` 결과를 `view` 에 넘기기만 한다.

**규칙 추가 = `CompatibilityRules.ALL` 에 한 줄 추가.** 문구 변경 = `ConsoleView` 한 파일.

---

## 4. 커밋 내역

`master..kanghyun`, 시간순.

| 커밋 | 내용 |
|---|---|
| `test: 리팩토링 안전망용 골든 테스트 및 JUnit 실행 환경 추가` | 원본 프로그램의 출력을 캡처한 골든 시나리오 14개 (`src/test/resources/golden/`) |
| `refactor: Assemble 클래스를 assemble 패키지로 이동` | S7 |
| `refactor: 부품 매직 넘버와 int[] stack 을 enum 과 CarSpec 으로 대체` | S1, S5, N1, N2, N5 |
| `refactor: 중복된 부품 호환성 규칙을 CompatibilityRules 로 단일화` | S4 |
| `refactor: 입출력을 Console 인터페이스로 분리하고 AssembleApp 을 인스턴스화` | S3. 골든 테스트가 `delay` 없이 돌아 90초 → 0.1초 |
| `fix: 입력이 끝나면(EOF) 예외 대신 조용히 종료` | B2 |
| `fix: delay 중 인터럽트 발생 시 인터럽트 플래그 복원` | B3 |
| `fix: 종료 시 System.in 을 닫지 않도록 Scanner close 제거` | B6 |
| `refactor: 단계 전이와 메뉴 정의를 Step enum 으로 통합` | S2, S6 — `main()` 의 `switch` 4개 → 1개 |
| `refactor: Part 인터페이스를 실제 역할에 맞게 MenuOption 으로 이름 변경` | `RunAction` 도 구현하므로 "부품" 이 아니라 "메뉴 항목" |
| `test: JaCoCo 리포트 추가 및 미커버 분기(SUV+GM, stdin EOF) 테스트 보강` | `mvn test` 마다 `target/site/jacoco/` 생성 |
| `refactor: run() 의 입력 읽기·파싱·검증을 별도 메서드로 추출` | `run()` 40줄 → 14줄, 검증은 순수 함수로 |
| `refactor: RUN 판정 로직을 CarInspector 와 RunResult 로 분리` | 판정과 출력 분리. B4 순서가 한 곳에 고정됨 |
| `refactor: CarInspector 를 AssembleApp 생성자로 주입` | 규칙을 바꿔도 앱은 그대로. 테스트에서 가짜 규칙 주입 가능 |
| `chore: .gitattributes 로 줄바꿈 정규화 및 골든 파일 LF 고정` | 골든 `.in`/`.out` 은 `eol=lf` |
| `refactor: 화면 문구와 템포를 ConsoleView 로 분리하고 Step 을 순수 흐름으로 정리` | `AssembleApp` 187 → 105줄, `Step` 105 → 50줄. `selectPart` 오버로드 4개 → 제네릭 `select` 하나 |
| `refactor: CarInspector.standard() 팩토리 추가 및 미완성 CarSpec 검사 시 명시적 예외` | 세 곳의 `new CarInspector(ALL)` 중복 제거. 미완성 사양은 NPE 대신 `IllegalStateException` |

`refactor:` 커밋은 골든 14개를 전부 통과한 상태로만 커밋했다. `fix:` 3개는 원래 `Console` 분리 커밋에 섞여 있던 것을 push 전에 분리한 것이다 (분리 전 히스토리: `backup/kanghyun-pre-split`).

---

## 5. 검증

```
mvn test
```

37개 테스트: 골든 14 · `CarInspectorTest` 14 · `StepTest` 4 · `AssembleAppTest` 3 · `AssembleMainTest` 2.

**원본과 동일함의 근거**: 골든 파일 14개는 리팩토링 전 원본 프로그램의 출력을 캡처한 것이고, 첫 커밋(`4dcd810`) 이후 한 번도 수정되지 않았다 (`git log -- src/test/resources/golden`). 이 14개가 최종 코드에서 통과하므로 사용자에게 보이는 출력은 원본과 같다. 골든 테스트는 CRLF/LF 만 정규화해 비교한다.

의도적으로 바꾼 동작 3건은 각각 테스트로 고정되어 있다:

| 항목 | 이전 | 이후 | 테스트 |
|---|---|---|---|
| B2 EOF | `NoSuchElementException` | 루프를 조용히 빠져나감 | `AssembleAppTest.endOfInputStopsTheLoopWithoutError`, `AssembleMainTest.mainStopsQuietlyWhenStdinIsClosed` |
| B3 인터럽트 | 예외 삼킴 | `Thread.currentThread().interrupt()` | `SystemConsole.delay` |
| B6 `System.in` | `sc.close()` | 닫지 않음 | — |

브랜치 커버리지: 미커버는 exhaustive `switch` 의 암묵적 default 2개뿐 (`target/site/jacoco/index.html`).

---

## 6. 검토했지만 하지 않은 것

| 항목 | 이유 |
|---|---|
| `apply()` 의 `switch` 를 `Step` 별 다형성으로 교체 | 5줄짜리 exhaustive switch 라 바꿔도 가독성이 오르지 않음. 단계가 실제로 추가될 때 재검토 |
| enum 5개의 `code/displayName` 보일러플레이트 제거 | Java enum 은 상속이 안 되므로 남는 방법이 리플렉션/ordinal 트릭뿐. 지금이 가장 단순함 |
| `FakeConsole` 을 Mockito 로 교체 | 입력 시퀀스·출력 누적이라는 상태가 필요해 Fake 가 맞음. 호출 횟수 검증이 필요해지면 그때 |
| 골든 시나리오 목록을 디렉터리 스캔으로 자동화 | 하드코딩된 `@ValueSource` 가 시나리오 목록을 한눈에 보여 줌 |

---

## 7. 남은 결정 사항 (동작 변경 — 리팩토링 범위 밖)

전부 사용자에게 보이는 출력이 바뀌므로 `feat:`/`fix:` 로 진행하고 골든 파일을 함께 갱신해야 한다. 수정 위치는 이미 한 곳씩으로 좁혀져 있다.

| 항목 | 현재 | 대안 | 수정 위치 |
|---|---|---|---|
| B1 에러 메시지 범위 | "1 ~ 4" (0 뒤로가기 미안내) | 실제 범위 "0 ~ 4" 로 표기 | `ConsoleView.menuOf()` 의 `rangeError` |
| B4 RUN 검사 순서 | 호환성 → 고장 엔진 | 고장 엔진 먼저 | `CarInspector.run()` |
| Test 다중 위반 | 첫 번째만 출력 | 전부 출력 | `ConsoleView.showTestResult()` — `violations.get(0)` |
| B5 뒤로가기 | 이후 단계 선택값 유지 | 초기화 | `AssembleApp.apply()` 의 `step.back()` 옆에서 `spec` 되돌리기 |
| 표기 통일 | 메뉴 `MANDO` / RUN 출력 `Mando` | 한쪽으로 통일 | `ConsoleView.capitalized()` 삭제 |
