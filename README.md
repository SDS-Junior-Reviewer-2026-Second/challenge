# Baseball Game 리팩토링 기록

강의안(TDD Baseball SDS 신입과정) 8단계 TDD Cycle을 따라가며 수행한 Refactor 단계만 정리했습니다.
각 항목은 강의안 페이지 번호 기준이며, 첫 번째로 받은 코드(강의안 그대로 따라간 버전)를 기준으로 합니다.

## 1. Game 인스턴스 생성 중복 제거 (p.36)
- 모든 테스트 메서드에서 반복되던 `Game game = new Game();` 코드를 제거.
- `private Game game;` 필드로 선언하고 `@BeforeEach setUp()` 메서드에서 초기화하도록 변경.
- **의도**: 테스트마다 반복되는 준비 코드를 한 곳으로 모아 테스트 본문을 짧게 유지.

## 2. 예외 검증 코드 정리 (p.39 ~ 43)
- `assertThrows(IllegalArgumentException.class, () -> {...})` 방식을 `try ~ catch` 방식으로 변경.
- `Extract Variable`로 하드코딩된 입력값("12" 등)을 `guessNumber` 지역 변수로 추출.
- `Extract Method`로 try~catch 블록 전체를 `assertIllegalArgument(String guessNumber)` 메서드로 추출.
- **의도**: 여러 개의 "예외가 터져야 하는" 테스트 케이스가 같은 구조를 반복하고 있어서, 검증 로직을 재사용 가능한 헬퍼 메서드로 뽑아냄.

## 3. 예외 테스트 케이스 통합 (p.44)
- `throwExceptionWhenInputIsNull()`, `throwExceptionWhenInputLengthIsUnmatched()` 등 "예외가 발생해야 한다"는 같은 의도의 테스트들을 `throwIllegalArgumentExceptionInvalidInput()` 하나로 통합.
- 통합된 테스트 안에서 `assertIllegalArgument(null)`, `assertIllegalArgument("12")`, `assertIllegalArgument("1234")`, `assertIllegalArgument("12s")` 처럼 케이스를 나열.
- **의도**: 같은 관점(잘못된 입력이면 예외가 터진다)을 검증하는 테스트를 하나로 묶어 테스트 클래스가 불필요하게 길어지는 것을 방지.

## 4. Game 클래스 - 중복 숫자 검사 로직 추출 (p.50)
- `if (guessNumber.charAt(0) == guessNumber.charAt(1) || ...)` 조건문을 `isDuplicatedNumber(String guessNumber)` 메서드로 추출.
- **의도**: `guess()` 메서드 안에 검증 로직이 계속 쌓이는 것을 막고, "무엇을 검사하는지"가 메서드 이름으로 드러나도록 함.

## 5. Game 클래스 - 유효성 검사 전체 추출 (p.51)
- null 체크, 길이 체크, 숫자 여부 체크, 중복 체크를 모두 감싸는 `assertIllegalArgument(String guessNumber)` 메서드를 Game 클래스에도 동일하게 추출.
- `guess()` 메서드 최상단에서 `assertIllegalArgument(guessNumber);` 한 줄만 호출하도록 정리.
- **의도**: `guess()` 메서드가 "검증 + 판정" 두 가지 책임을 한 번에 갖지 않도록, 검증 책임을 별도 메서드로 분리(추후 단일 책임 원칙 적용의 시작점).

## 6. GuessResult 캡슐화 (p.60 ~ 61)
- `public boolean solved`, `public int strikes`, `public int balls` 필드를 모두 `private`으로 변경.
- 3개 필드를 초기화하는 생성자(Constructor)를 IDE 자동 생성 기능으로 추가.
- `isSolved()`, `getStrikes()`, `getBalls()` Getter를 IDE 자동 생성 기능으로 추가.
- **의도**: 결과 객체가 외부에서 임의로 값이 바뀔 수 없도록 캡슐화. 이후 테스트 코드도 필드 직접 접근(`result.solved`) 대신 Getter 호출(`result.isSolved()`)로 변경.

## 7. 하드코딩된 기대값 변수화 + 검증 메서드 추출 (p.65 ~ 68)
- 테스트 안에 하드코딩되어 있던 `true`, `3`, `0` 같은 기대값을 `boolean solved`, `int strikes`, `int balls` 지역 변수로 추출(Extract Variable).
- `assertThat(result.isSolved()).isEqualTo(solved); ...` 4줄을 `assertMatchedNumber(GuessResult result, boolean solved, int strikes, int balls)` 메서드로 추출.
- 이어서 `generateQuestion(String questionNumber)` 메서드도 추출하여 `game.question = "123";` 대신 의미가 드러나는 이름으로 호출.
- **의도**: "정답 세팅 → 추측 → 결과 검증"이라는 테스트의 흐름이 코드만 봐도 읽히도록, 각 단계를 의미 있는 이름의 메서드로 추출(추상화 레벨 통일).

## 8. Inline Variable로 불필요한 지역 변수 제거 (p.71)
- 스트라이크/볼 계산 로직에서 한 번만 쓰이던 `char charAt = guessNumber.charAt(i);`, `int indexOf = question.indexOf(charAt);` 지역 변수를 인라인 처리.
- `if (question.indexOf(guessNumber.charAt(i)) == i) { strikes++; }` 형태로 축약.
- **의도**: 재사용되지 않는 임시 변수를 없애 한 줄로 읽을 수 있게 만듦(과도한 지역 변수는 오히려 가독성을 해친다는 판단).

## 9. guess() 메서드 3단계 추출 (p.74 ~ 76)
- `if (guessNumber.equals(question))` 조건식을 `isSolved(String guessNumber)` 메서드로 추출.
- 정답일 때 리턴값 `new GuessResult(true, 3, 0)`을 `getGuessResult()` 메서드로 추출.
- 오답일 때의 스트라이크/볼 계산 + 리턴 블록 전체를 `createUnSolvedResult(String guessNumber)` 메서드로 추출.
- 최종 `guess()` 메서드:
  ```java
  public GuessResult guess(String guessNumber) {
      assertIllegalArgument(guessNumber);
      if (isSolved(guessNumber)) {
          return getGuessResult();
      } else {
          return createUnSolvedResult(guessNumber);
      }
  }
  ```
- **의도**: `guess()` 메서드 하나만 봤을 때 "검증하고, 맞았으면 이렇게, 틀렸으면 저렇게"라는 흐름만 보이도록 세부 구현을 각 메서드 뒤로 숨김(추상화 레벨 통일 + 가독성 개선).

## 리팩토링 전/후 요약

| 단계 | 리팩토링 종류 | 대상 |
|---|---|---|
| 1 | 중복 코드 제거 (Extract Field + @BeforeEach) | GameTest |
| 2 | Extract Variable, Extract Method | GameTest |
| 3 | 테스트 케이스 통합 | GameTest |
| 4~5 | Extract Method | Game |
| 6 | 캡슐화 (필드 private화, Getter/생성자 추가) | GuessResult |
| 7 | Extract Variable, Extract Method | GameTest |
| 8 | Inline Variable | Game |
| 9 | Extract Method (3회) | Game |

모든 단계는 커밋 시 `[refactoring] 페이지번호` 형식의 커밋 메시지 규칙을 따랐습니다(예: `[refactoring] 51p`).
Red 단계(실패하는 테스트를 만드는 시점)는 커밋하지 않고, Green/Refactor 단계에서만 커밋합니다.
