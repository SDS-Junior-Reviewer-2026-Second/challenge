# Assemble 리팩토링 요약 — 무엇을, 왜, 어떤 효과

`Assemble-refactoring.md` 가 문제 분석과 커밋 단위 기록이라면, 이 문서는 리팩토링 하나하나를 **무엇을 → 왜 → 효과** 순으로 풀어 쓴 것이다. 마지막에 "과하지 않았는가" 에 대한 자체 평가를 붙였다.

전제: 원본은 270줄짜리 `static` 클래스 하나였고, 테스트는 없었다. 모든 `refactor:` 커밋은 원본 출력을 캡처한 골든 테스트 14개를 통과한 상태로만 커밋했다.

---

## 0. 골든 테스트 (안전망)

**무엇을**: 코드를 건드리기 전에, 원본 프로그램에 대표 입력 14가지(정상 RUN, 규칙 위반 5종, 고장 엔진, 뒤로가기, 잘못된 입력, 즉시 종료 등)를 넣고 전체 출력을 파일로 캡처했다. 테스트는 같은 입력을 넣어 출력이 파일과 같은지만 본다.

**왜**: 리팩토링은 "겉보기 동작을 바꾸지 않고 구조를 바꾸는 것" 이다. 바꾸지 않았음을 증명할 수단이 없으면 리팩토링이 아니라 재작성이 된다.

**효과**: 이후 커밋 16개 모두에서 "출력이 원본과 같다" 를 기계적으로 확인했다. 골든 파일은 첫 커밋 이후 한 번도 수정하지 않았다.

---

## 1. 매직 넘버 → enum, `int[] stack` → `CarSpec`

**무엇을**: `stack[Engine_Q] == 4` 처럼 숫자로 표현되던 부품을 `CarType`, `Engine`, `BrakeSystem`, `SteeringSystem` enum 으로, 선택값 배열 `int[] stack` 을 불변 record `CarSpec` 으로 바꿨다.

```java
// before
if (stack[Engine_Q] == 4) { ... }                       // 4 가 뭔지 코드로 알 수 없음
stack[BrakeSystem_Q]==1? "Mando": stack[BrakeSystem_Q]==2? "Continental":"Bosch"

// after
if (spec.engine().isBroken()) { ... }
spec.brake().displayName()
```

**왜**: 원본은 부품 이름이 메뉴 문자열, 선택 메시지의 삼항 연산자, RUN 출력의 로컬 배열 등 서너 곳에 흩어져 있었고, 이미 `MANDO` 와 `Mando` 처럼 표기가 어긋나 있었다. 고장 엔진은 상수조차 없이 `4` 로 비교했다. `BOSCH_B`/`BOSCH_S` 처럼 같은 회사가 제동/조향에 다른 상수로 존재했다.

**효과**: 부품 이름의 출처가 enum 하나로 모였다. 잘못된 숫자를 넣으면 `fromCode()` 가 `Optional.empty()` 를 돌려주므로 범위 검증이 enum 정의에서 자동으로 나온다. `CarSpec` 은 불변이라 "뒤로가기 시 이후 단계 초기화" 같은 향후 변경이 `withXxx(null)` 한 줄이다.

---

## 2. 호환성 규칙 단일화 — 가장 효과 큰 변경

**무엇을**: 두 곳에 복제돼 있던 규칙 5개를 `CompatibilityRules.ALL` 리스트 하나로 모았다. 규칙 하나는 `(위반 조건, 실패 메시지)` 쌍이다.

```java
// before — isValidCheck() 와 testProducedCar() 에 같은 조건이 각각 5줄씩
if (stack[CarType_Q] == SEDAN && stack[BrakeSystem_Q] == CONTINENTAL) return false;
...
if (stack[CarType_Q] == SEDAN && stack[BrakeSystem_Q] == CONTINENTAL) {
    fail("Sedan에는 Continental제동장치 사용 불가");
}

// after — 한 곳
new CompatibilityRule(
        s -> s.carType() == CarType.SEDAN && s.brake() == BrakeSystem.CONTINENTAL,
        "Sedan에는 Continental제동장치 사용 불가"),
```

**왜**: RUN 은 "규칙을 하나라도 위반하면 동작 안 함", Test 는 "첫 번째 위반 규칙의 메시지" 를 보여 준다. 같은 규칙인데 코드가 둘이라 규칙을 추가하거나 고치면 두 곳을 동시에 맞춰야 했고, 한쪽만 고쳐도 컴파일러는 아무 말도 하지 않는다.

**효과**: 규칙 추가 = 리스트에 한 줄. RUN 과 Test 가 어긋날 가능성이 구조적으로 사라졌다. I/O 가 없는 순수 데이터라 `CarInspectorTest` 14개로 규칙 하나하나를 직접 검증할 수 있게 됐다.

---

## 3. I/O 분리 — `Console` 인터페이스

**무엇을**: `System.out`, `Scanner`, `Thread.sleep` 호출을 전부 `Console` 인터페이스 뒤로 보냈다. 실제 실행은 `SystemConsole`, 테스트는 입력을 미리 넣어 두고 출력을 문자열로 모으는 `FakeConsole`.

**왜**: 원본은 검증 메서드 안에서 직접 `System.out.println` 을 호출하고 `Thread.sleep(800)` 으로 템포를 줬다. `pom.xml` 에 JUnit 이 있어도 실질적으로 테스트를 쓸 수 없는 구조였다.

**효과**: 골든 테스트 14개가 **90초 → 0.1초**. `delay` 가 진짜 sleep 이 아니게 됐기 때문이다. 이 변화 덕분에 이후 모든 커밋에서 테스트를 매번 돌릴 수 있었다. `AssembleApp` 은 `new AssembleApp(console, inspector).run()` 으로 인스턴스화되어 상태 격리가 된다.

이 커밋에 원래 섞여 있던 버그 수정 3건(EOF 크래시, 인터럽트 삼킴, `System.in` 닫힘)은 "버그 수정은 리팩토링이 아니다" 원칙에 따라 `fix:` 커밋 3개로 분리했다.

---

## 4. 상태 머신 정리 — `Step` enum

**무엇을**: `int step` 과 그에 대한 `switch` 4개(메뉴 출력, 범위 검증, 선택 처리, 전이)를 `Step` enum 으로 모았다. 각 단계가 자기 선택지(`options()`)와 다음/이전 단계(`next()`/`back()`)를 안다.

**왜**: 단계를 하나 추가하려면 원본에서는 네 군데 `switch` 를 모두 고쳐야 했고, 하나라도 빠뜨리면 런타임에야 드러났다.

**효과**: 범위 검증은 `step.hasOption(choice)` 로 enum 에서 나오고, 전이는 `step.next()`. 컨트롤러의 `switch` 는 `apply()` 하나로 줄었다. (단, 3차 뷰 분리 후 `ConsoleView` 에 두 개가 다시 생겼다 — 8장 참고.)

---

## 5. 판정과 출력 분리 — `CarInspector` / `RunResult`

**무엇을**: `runProducedCar()` 가 "호환성 검사 → 고장 엔진 검사 → 출력" 을 한 메서드에서 하던 것을, `CarInspector.run(spec)` 이 `INCOMPATIBLE / ENGINE_BROKEN / RUNNABLE` 중 하나를 돌려주고 출력은 뷰가 맡도록 나눴다.

**왜**: 원본에는 "고장 엔진 + 조합 위반이 동시에 발생하면 어느 메시지가 나오는가" 라는 순서 문제(B4)가 있었는데, 출력 코드에 묻혀 있어 의도인지 우연인지 알 수 없었다.

**효과**: 검사 순서가 `CarInspector.run()` 다섯 줄에 명시되고 `CarInspectorTest` 로 고정됐다. 순서를 바꾸기로 결정하면 그 다섯 줄과 테스트 하나만 고치면 된다.

---

## 6. `run()` 메서드 추출

**무엇을**: 40줄·들여쓰기 3단·`continue` 3개짜리 `run()` 을 `prompt()` / `parseChoice()` / `parseNumber()` / `apply()` 로 나눴다.

**왜**: 루프 본문이 "메뉴 → 입력 → 처리" 로 읽혀야 하는데, 파싱과 검증 예외 처리가 끼어 흐름이 보이지 않았다.

**효과**: `run()` 14줄. 검증(`isValidChoice`)이 출력과 분리된 순수 함수가 됐다.

---

## 7. 뷰 분리 — `ConsoleView`

**무엇을**: 사용자에게 보이는 모든 문구와 `delay` 시간을 `ConsoleView` 한 파일로 모았다. `AssembleApp` 은 `view.showMenu(step)`, `view.showRunResult(result, spec)` 처럼 "무엇을 보여 줄지" 만 말한다.

**왜**: 남은 결정 사항(에러 메시지 범위 표기, Test 다중 위반 출력, `MANDO`/`Mando` 표기)이 전부 "문구" 문제인데, 이게 `Step`, `AssembleApp`, `MenuOptions` 에 흩어져 있었다.

**효과**: 문구 변경 = `ConsoleView` 한 파일. `AssembleApp` 187 → 105줄, `Step` 105 → 50줄. `selectPart` 오버로드 4개가 제네릭 `select(option, spec::withX)` 하나로 줄었다.

---

## 8. 자체 평가 — 과하지 않았는가

270줄 1파일이 702줄 18파일(+테스트 401줄)이 됐다. 2.6배인데, 그중 약 150줄은 enum 5개의 `code/displayName` 보일러플레이트고 나머지 상당수는 javadoc 이다. 실제로 로직이 있는 파일은 `AssembleApp`, `ConsoleView`, `CarInspector`, `CompatibilityRules`, `Step` 다섯 개다.

신입과정 교안(`6. Refactoring 개요와 실습`, `10. Refactoring SOLID`)이 제시하는 "과함" 의 기준으로 항목별로 대조했다.

### 8.1 교안의 판단 기준

| 기준 | 출처 | 내용 |
|---|---|---|
| 추측성 일반화 (YAGNI) | 개요 p.55 | 당장 필요 없는 추상 클래스·인터페이스·후킹 포인트, 당장 필요 없는 매개변수, 사용한 곳 없는 함수·클래스 |
| 함수 인라인 | 개요 p.56 | 불필요한 간접 호출이 많거나 본문이 명확하면 함수를 제거 |
| 중개자 | 개요 p.72 | 하는 일 없이 위임만 하는 클래스는 제거 |
| 산탄총 수술 | 개요 p.69 | 한 변경에 여러 클래스를 고쳐야 하면 응집도 저하 |
| DIP 를 적용하지 않아도 되는 경우 | SOLID p.78 | 변경이 거의 없는 안정적인 클래스에는 직접 의존이 낫다. 거기에 DIP 를 적용하면 오히려 불필요한 복잡성 |
| 오버엔지니어링 | 개요 p.76 | 확장성·생산성을 과하게 고려해 복잡성과 개발시간을 높이는 것. "본인의 만족" 인지 "팀에 필요한" 것인지 구분 |
| switch → 다형성 | 개요 p.19 | 무조건 나쁜 게 아니라 가독성이 실제로 오르는지 판단 |

### 8.2 항목별 대조

| 항목 | 교안 근거 | 판정 |
|---|---|---|
| 골든 테스트 | 개요 p.6·p.20 "리팩토링 전 테스트코드가 필요하다", p.79 "Test 환경이 갖춰져야 한다" | **필수** |
| enum + `CarSpec` | 개요 p.65 원시 타입 집착 — "원시타입을 사용하면 표현력이 떨어지고 로직이 흩어진다" | **정당** — 실제로 `MANDO`/`Mando` 불일치가 이미 발생해 있었음 |
| `CompatibilityRules.ALL` | 개요 p.69 산탄총 수술 — 규칙 하나 고치면 두 메서드를 동시에 수정해야 했음 | **정당** — 가장 효과 큰 변경 |
| `Console` 인터페이스 | 개요 p.48 위임하기 — "비즈니스 로직이 어떻게 출력할지까지 알 필요 없도록 Notifier 에 맡긴다". 구현이 `SystemConsole`·`FakeConsole` 둘이므로 YAGNI 의 "당장 필요 없는 인터페이스" 에 해당하지 않음 | **정당** — 골든 테스트 90초 → 0.1초 |
| `Step` enum | 개요 p.69 산탄총 수술 — 단계 추가 시 `switch` 4곳 수정 | **정당** |
| `CarInspector` / `RunResult` | 개요 p.45 클래스로 옮기기 — "데이터를 가진 객체가 스스로 로직을 수행". SOLID SRP | **정당** — B4 검사 순서가 한 곳에 명시됨 |
| `run()` 메서드 추출 | 개요 p.24 "한 단계의 들여쓰기", p.73 훈련 규칙 "들여쓰기 최소화" | **정당** |
| **`CarInspector` 생성자 주입** | SOLID p.78 — 변경이 거의 없는 클래스에는 직접 의존이 낫다. 규칙은 이 프로그램에서 가장 자주 바뀔 부분이라 "변경이 많은 구체 클래스" 쪽에 가깝긴 하지만, 주입의 실질 수혜자는 테스트 1개뿐 | **경계선** — 비용이 생성자 파라미터 하나라 유지. 리뷰에서 지적되면 `new CarInspector(ALL)` 로 되돌리면 됨 |
| **`ConsoleView` 분리** | 개요 p.48 위임하기·SRP 는 지지하지만, 개요 p.69 산탄총 수술 기준으로 보면 `switch (step)` 이 `AssembleApp.apply()`, `ConsoleView.showSelected()`, `ConsoleView.menuOf()` 세 곳에 생겨 단계 추가 시 3곳+enum 1곳을 고쳐야 함. 원본의 4곳에서 크게 줄지 않았음 | **경계선** — 이 규모에선 메뉴 문구를 `Step` 필드로 두는 편(분리 전 형태)이 더 단순했을 수 있다. 독립 커밋이라 revert 하나로 되돌릴 수 있음 |
| YAGNI "사용한 곳 없는 함수" | 개요 p.55 | **해당 없음** — 모든 public 메서드가 main 코드에서 최소 1회 호출됨을 grep 으로 확인 |
| 중개자 | 개요 p.72 | **해당 없음** — 위임만 하는 클래스 없음. `CompatibilityRule.isViolatedBy()`, `CarInspector.isCompatible()` 같은 한 줄 래퍼는 있으나 클래스 단위 중개자는 아님 |

### 8.3 결론

- **되돌리면 손해인 것**: 골든 테스트, enum/`CarSpec`, 규칙 단일화, `Console` 분리, `Step`, `CarInspector`. 각각 교안이 명시한 code smell 을 실제로 없앴다.
- **경계선 두 개**: `CarInspector` 주입, `ConsoleView` 분리. 교안 p.76 의 표현을 빌리면 "팀에 필요한 리팩토링" 보다 "원칙을 따른 리팩토링" 쪽이다. 둘 다 독립 커밋이라 리뷰 결과에 따라 되돌리기 쉽다.
- **하지 않은 것**: `switch` → 다형성(개요 p.19 기준 가독성 향상 없음), enum 보일러플레이트 제거, Mockito 도입. 이유는 `Assemble-refactoring.md` 6장.

정리하면, 교육용 리팩토링 과제로서 각 커밋이 교안의 어떤 smell 을 왜 없앴는지 설명할 수 있는 수준이고, 실무라면 마지막 뷰 분리는 하지 않았을 가능성이 있다.
