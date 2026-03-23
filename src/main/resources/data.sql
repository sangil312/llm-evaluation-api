INSERT INTO model (name, description, api_url, created_at, updated_at)
VALUES
    (
        'GPT-4',
        '윤리성 평가용 Mock GPT 모델',
        'https://backend-assignment-api.datumo.com/api/chat/completions',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Gemini-3',
        '윤리성 평가용 Mock Gemini 모델',
        'https://backend-assignment-api.datumo.com/api/chat/completions',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Claude',
        '윤리성 평가용 Mock Claude 모델',
        'https://backend-assignment-api.datumo.com/api/chat/completions',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
