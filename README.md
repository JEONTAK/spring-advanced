# SPRING ADVANCED

---

## Lv 1-1. 코드 개선(Early Return)

### Requirement

- [X] AuthService 클래스에 있는 signup() 메서드의 코드를 리팩토링하여 불필요한 동작이 일어나지 않게 해야한다.

### Configuration

- [X] AuthService의 singup() 메서드 리팩토링
    - 이메일 중복 체크를 가장 앞으로 옮겨 encode() 동작 수행 하지 않아도 되게 수정

## Lv 1-2. 코드 개선(불필요한 if-else 피하기)

### Requirement

- [X] 불필요한 else 블록을 없애 코드를 간결하게 유지해야 한다.

### Configuration

- [X] WeatherClient의 getTodayWeather() 메서드 리팩토링
    - 처음 if문의 조건에 걸릴경우 예외 처리 되므로, else 문 사용 불필요함. 따라서 else 문 제거
    - else문 안의 if문에 대한 조건문을 따로 메서드화 하여 이후 동일 코드 사용시 코드 재사용성 증가 효과 노림

## Lv 1-3. 코드 개선(Validation)

### Requirement

- [ ] Validation 처리를 통해 코드를 간결히 개선 해야 한다.

### Configuration

- [ ] UserService의 changePassword()를 Validation을 통한 리팩토링
    - 비밀번호 요구 조건을 Validation Annotation 사용하여 추가
        - 길이 : 8글자 이상
            - @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
        - 요구 조건 : 숫자, 대문자 포함
            - @Pattern(regexp = "비밀번호 패턴", message = "새 비밀번호는 숫자와 대문자를 포함해야 합니다.")

---

## Commit Convention

### 형식

```
type(scope) : short summary

[body]

[footer]
```

<br>type : 커밋의 목적
<br>scope : 변경된 코드의 범위
<br>short summary : 커밋의 간략한 설명
<br>body : 커밋의 자세한 설명
<br>footer : 부가 정보

### type

<br>feat: 새로운 기능 추가.
<br>impl: 기능 수정.
<br>fix: 버그 수정.
<br>docs: 문서 수정 (README.md, 주석 등).
<br>style: 코드 포맷팅, 세미콜론 누락 등 기능에 영향을 미치지 않는 변경.
<br>refactor: 코드 리팩토링 (기능 변화 없음).
<br>test: 테스트 코드 추가/수정.
<br>chore: 빌드 프로세스 또는 패키지 매니저 설정 수정.
<br>perf: 성능 개선을 위한 변경.
<br>ci: CI 설정 변경.
<br>build: 빌드 관련 파일 변경.
---