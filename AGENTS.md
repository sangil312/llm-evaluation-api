## 일반 사항
- 설명은 한글을 사용한다
- 해당 프로젝트는 아직 규모가 작기 때문에 헥사고날을 사용하지 않는다

## 아키텍처 기본 규칙
- 큰 틀의 계층은 3개의 계층이나, 명시적인 분리를 위해 4개의 영역으로 관리한다
  1. Presentation Layer (*Controller, *UseCase)
  2. Business Layer (*Service)
  3. Implement Layer (*Reader, *Writer, *Validator 등 상황에 따라 추가가능하고, 서비스의 규모가 작다면 해당 레이어는 생략가능하다)
  4. Data Access Layer (*Repository, *Client 등)

[4가지 계층 규칙]
- 규칙1. 레이어는 위에서 아래로 순방향으로만 참조 되어야한다
- 규칙2. 레이어는 참조 방향이 역류 되지 않아야한다
- 규칙3. 레이어의 참조가 하위 레이어를 건너 뛰지 않아야한다
- 규칙4. 동일 레이어간에는 서로 참조하지 않아야한다
    - Implement Layer는 예외적으로 서로 참조가 가능하다.

## 리팩토링 및 신규 코드에 대한 규칙
### API 스팩 도메인 조합 전략
- Controller가 Service를 직접 조합하지 않는다
- Service 조합을 위한 `UseCase` 클래스를 구성한다

### UseCase 클래스 구현 규칙
- UseCase는 요구사항 중 UI를 위한 처리나, 비즈니스 로직 응집이 아닌 도메인 조합을 위해 사용한다
- 패키지 경로는 `api.controller.v1.*.usecase`이다
- 오용을 없애기 위해 다른 패키지 경로에서 UseCase를 명시적으로 사용하지 않는다
- UseCase는 Presentation Layer에 존재한다
- UseCase는 *Service 클래스만을 참조 가능하다
- UseCase는 *Finder, *Reader, *Manager 등 Implement Layer의 클래스를 절대 참조할 수 없다
- UseCase는 JPA Repository 등 Data Access Layer 클래스를 절대 참조 할 수 없다
- UseCase는 API 스팩에 따른 서비스 조합이 필요한 경우에만 도입한다. 요구사항상 조합이 불필요한 경우 도입하지 않는다
- 도입하지 않더라도 기존 제약(위치: Presentation Layer, 참조: Service만 허용)은 유지된다.

### 클래스 네이밍 규칙
- Business Layer = *Service
- Implement Layer = *Handler, *Processor, *Manager, *Writer, *Reader 등 행위 기반의 명명을 사용한다

### @Transactional 적용 규칙
- Service에는 @Transactional을 사용하지 않는다
  - @Transactional은 Implement Layer에만 사용한다
  - 다만 서비스 크기에 따라 Implement Layer로 분리되지 않았다면 예외적으로 허용한다
- 읽기 함수는 필요하지 않으면 @Transactional을 사용하지 않는다
- 쓰기 함수더라도 @Transactional 이 필요 없다면 사용하지 않는다
    - 단일 JPA Entity 의 *Repository.save() 등

### API Response DTO의 변환 규칙
- 로직이 필요한 변환의 경우 `*Response.of(...)` 함수를 사용한다
    - 복수 파라미터가 들어갈 수 있다
    - 해당 함수는 도메인 엔티티 객체를 활용하여 API 스펙을 맞추기위한 Converter 역할을 하고있다

### 컨트롤러 패키지 위치 규칙
- v1 API: `api.controller.v1` 패키지

### Component Annotation 규칙
- Business Layer = @Service
- Implement Layer = @Component