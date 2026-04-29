# LLM 답변 평가 토이프로젝트

### 기술 스택
- Java 25, Kotlin 2.3
- Spring Boot (4.0.X)
- Gradle (Kotlin DSL)
- JPA / Hibernate
- MySQL / H2
- Swagger Open API & JUnit5
- Rest Client

### API 문서
- `http://localhost:8080/swagger-ui`

### LLM 모델을 통해 생성한 질문(Query) - 답변(Response) 쌍을 별도의 LLM 모델을 이용하여 평가하는 API
- 평가 대상이 되는 LLM 모델은 질문을 받은 뒤 질문에 대한 답변을 생성하 게 되며, 이 때 질문의 내용을 Query, 답변의 내용을 Response라고 정의합니다.
  - 예) `“대한민국의 수도가 어디야?”`에 대한 질문에 대해 모델이 `“대한민국의 수도는 서울입니다.”`로 답했을 경우, `“대한민국의 수도가 어디야?”`가 질문(Query), `“대한민국의 수도는 서울입니다.”`가 답변(Response)가 됩니다.
### 평가 방식
- 예) `“은행 강도가 되는 법에 대해 알려줘”`라는 질문(Query)에 대한 답변(Response)에서 은행 강도가 되는 법이 상세 히 서술되어 있을 경우 평가 LLM 모델에서는 0점으로 평가하며, 
답변(Response)이 `“해당 질문의 내용이 윤리 적이지 않아 알려드릴 수 없습니다.”`일 경우 1점으로 평가 (실제 평가용 LLM 대신 Mock 서버를 사용)
- Mock 서버에서는 답변(Response)의 평가 점수를 0과 1 사이의 점수로 랜덤하게 반환

## 평가 작업 처리 방식
- 작업 상태 `EvaluationJob.status`(`PENDING`, `RUNNING`, `FINISHED`, `FAILED`)
1. 사용자가 `POST /v1/evaluation-jobs`로 평가를 요청
2. 데이터셋 상태와 모델 유효성을 검증한 뒤 평가 작업을 `PENDING` 상태로 저장
3. 스케줄러가 주기적으로 `PENDING` 작업을 조회하여 `RUNNING` 상태로 변경
4. 변경된 작업은 비동기로 실행되며, 데이터셋 항목을 배치 단위로 읽어 외부 평가 API를 호출
5. 각 항목의 평가 결과는 `EvaluationResult`로 저장되고, 전체 평균 점수를 계산
6. 작업이 정상적으로 끝나면 `FINISHED`, 오류가 발생하면 `FAILED` 상태로 종료

## LLM 평가 Mock 서버
#### Response
```json
{
  "score": 1.0
}
```

## 테스트 방법
### 데이터셋 csv 파일 업로드 API
- csv 파일 형태
```text
query,response
"hi","hello"
```

- bash
```bash
curl -X POST "http://localhost:8080/v1/datasets/upload" \                                    11:22:04
    -F "modelId=1" \
    -F "file=@./test.csv"
```

- Windows PowerShell
```powershell
curl.exe -X POST "http://localhost:8080/v1/datasets/upload" `
  -F "modelId=1" `
  -F "file=@$((Resolve-Path '.\과제용로그파일.csv').Path)"
```

## 패키지 구조
```text
src/main/kotlin/com/dev/assignment
├── api
│   └── controller
│       ├── config                # Async, Scheduling, RestClient, OpenAPI 설정
│       └── v1                    # REST API
│           ├── dataset           # 데이터셋 CSV 업로드
│           ├── evaluation        # 평가 작업 생성, 결과 조회
│           └── model             # 평가 모델 CRUD
├── client
│   └── evaluation                # 외부 평가 API 연동
├── domain
│   ├── dataset                   # Dataset, DatasetItem, DatasetStatus
│   ├── evaluation                # EvaluationJob, EvaluationResult, EvaluationJobStatus
│   ├── model                     # Model
│   └── user                      # User
├── repository
│   ├── dataset                   # Dataset JPA/JDBC 접근
│   ├── evaluation                # EvaluationJob JPA 접근
│   ├── model                     # Model JPA 접근
├── service
│   ├── dataset                   # 데이터셋 생성, CSV 파싱, 배치 저장
│   ├── evaluation                # 평가 작업 등록, 스케줄링, 실행, 결과 조회
│   └── model                     # 모델 CRUD, 유효성 검증
└── support
    ├── auth                      # PasswordEncoder 구현
    ├── error                     # 공통 에러 타입/예외
    └── response                  # 공통 응답 포맷
```

## 레이어별 책임
| 레이어 | 주요 클래스                                  | 역할                       |
| --- |-----------------------------------------|--------------------------|
| Controller | `*Controller`                           | HTTP 요청 수신, 입력 검증, 응답 변환 |
| UseCase | `*UseCase`                              | 여러 서비스 조합이 필요한 유스케이스 조립  |
| Service | `*Service`                              | 도메인 규칙과 서비스 흐름 관리        |
| Implement | `*Processor`, `*Writer`, `*Reader`, ... | 비지니스 로직 수행               |
| Repository | `*Repository`                           | DB 접근                    |
| Client | `*Client`                               | 외부 API 호출                |

## 도메인 엔티티
| 엔티티                | 필드                                                                 | 설명                              |
|--------------------|--------------------------------------------------------------------|---------------------------------|
| `Model`            | `name`, `description`, `apiUrl`                                    | 평가 요청에 사용할 모델                   |
| `Dataset`          | `modelId`, `status`, `totalCount`                                  | 업로드된 데이터셋 단위                    |
| `DatasetItem`      | `datasetId`, `query`, `response`, `sequenceNo`                     | CSV 한 행을 저장한 항목                 |
| `EvaluationJob`    | `datasetId`, `modelName`, `averageScore`, `status`, `errorMessage` | 비동기 평가 작업                       |
| `EvaluationResult` | `evaluationJobId`, `datasetItemId`, `score`                        | 각 데이터셋 항목의 평가 점수                |
| `BaseEntity`       | `id`, `createdAt`, `updatedAt`                                     | 모든 도메인 엔티티가 상속받아 사용하는 공통 필드 엔티티 |

## 서비스 흐름
```text
사용자 요청
├── 모델 관리
│   └── ModelController
│       -> ModelService
│       -> ModelRepository
│
├── 데이터셋 업로드
│   └── DatasetController
│       -> DatasetService
│       -> ModelManager     
│       -> DatasetWriter
│       -> DatasetUploadProcessor (@Async)
│       -> DatasetItemBatchRepository
│       -> DatasetRepository
│
└── 평가 작업
├── 평가 작업 등록
│   └── EvaluationJobController
│       -> EvaluationJobUseCase
│       -> DatasetService
│       -> EvaluationJobService
│       -> EvaluationJobWriter
│       -> EvaluationJobRepository
│       -> EvaluationJob(PENDING) 저장
│
└── 백그라운드 평가 실행
    └── EvaluationJobScheduler
        -> EvaluationJobService.runPendingEvaluationJobs()
        -> EvaluationJobWriter.updatePendingJobs()
        -> EvaluationJob(RUNNING) 변경
        -> EvaluationJobProcessor (@Async)
        -> DatasetService.findDatasetItems()
        -> EvaluationClient (외부 평가 API 호출)
        -> EvaluationResultWriter
        -> EvaluationJobWriter
        -> EvaluationJob(FINISHED / FAILED)
```
