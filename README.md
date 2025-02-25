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

- [X] Validation 처리를 통해 코드를 간결히 개선 해야 한다.

### Configuration

- [X] UserService의 changePassword()를 Validation을 통한 리팩토링
    - 비밀번호 요구 조건을 Validation Annotation 사용하여 추가
        - 길이 : 8글자 이상
            - @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
        - 요구 조건 : 숫자, 대문자 포함
            - @Pattern(regexp = "비밀번호 패턴", message = "새 비밀번호는 숫자와 대문자를 포함해야 합니다.")
    - Validation 에러인 MethodArgumentNotValidException을 처리하기위해 GlobalExceptionHandler에 ExceptionHandler 추가

---

## Lv 2. N + 1 문제

### Requirement

- [X] Todo와 연관된 데이터를 처리할 경우 N + 1 문제없이 처리해야 한다.

### Configuration

- [X] getTodos() 메서드에서 모든 Todo를 조회할 때, 각 Todo와 연관된 데이터를 개별적으로 가져오는 경우 N + 1 문제가 발생
    - 해당 문제를 해결하기 위해 작성된 fetch join 쿼리를 EntityGraph를 사용하여 처리하도록 변경

---

## Lv 3-1. 테스트 코드 연습 1

### Requirement

- [X] 테스트 코드를 수정하여 예상대로 성공하는지 확인해야 한다.

### Configuration

- [X] PassEncoderTest의 matches_메서드가_정상적으로_동작한다() 테스트가 의도대로 성공할 수 있게 수정

## Lv 3-2. 테스트 코드 연습 2

### Requirement

- [X] 테스트 코드를 수정하여 예상대로 예외처리하는지 확인해야 한다.

### Configuration

- [X] ManagerServiceTest의 manager_목록_조회_시_Todo가_없다면_NPE_에러를_던진다() 테스트가 의도대로 성공할 수 있게 수정
    - 테스트 메서드 이름을 manager_목록_조회_시_Todo가_없다면_IRE_에러를_던진다()로 수정
- [X] CommentServiceTest의 comment_등록_중_할일을_찾지_못해_에러가_발생한다() 테스트가 의도대로 성공할 수 있도록 테스트 코드를 수정
- [X] ManagerServiceTest의 todo의_user가_null인_경우_예외가_발생한다() 테스트가 의도대로 성공할 수 있도록 서비스 로직을 수정

---

## Lv 4. API 로깅

### Requirement

- [ ] Interceptor 또는 AOP를 활용하여 로깅 처리를 해야 한다.
- [ ] 어드민 사용자만 접근할 수 있는 특정 API에는 접근할 때 마다 접근 로그를 기록해야 한다.

### Configuration

- 어드민 사용자만 접근할 수 있는 컨트롤러 메서드는 다음 두가지
    - CommentAdminController의 deleteComment()
    - UserAdminController의 changeUserRole()

- [ ] Interceptor를 사용하여 구현하기
    - 어드민 인증 여부를 확인
    - 인증되지 않은 경우 예외를 발생 시킴
    - 인증 성공시, 요청 시각과 URL을 로깅

- [ ] AOP를 사용하여 구현하기
  - @Around 어노테이션을 사용하여 어드민 API 메서드 실행 전후에 요청/응답 데이터를 로깅
  - 요청 본문과 응답 본문은 JSON 형식을 사용
  - 로깅 내용은 다음과 같음
    - 요청한 사용자의 ID
    - API 요청 시각
    - API 요청 URL
    - 요청 본문(RequestBody)
    - 응답 본문(ResponseBody)

- 공통 : Logger 클래스를 활용하여 기록

- deleteComment()는 Interceptor 사용, changeUserRole()은 AOP 사용하여 구현해보려고 함.
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