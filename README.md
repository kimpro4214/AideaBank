# 🧠 Multi-modal Dementia Prediction AI

다중 모달리티 치매 코호트 데이터를 활용하여  
**치매 여부 및 고위험군을 예측하는 AI 모델**을 개발하는 연구 프로젝트입니다.  
정형 데이터 기반 머신러닝 모델과 PET 영상 기반 딥러닝 모델을 결합하여  
임상적 의사결정을 지원하는 것을 목표로 합니다.

---

## 📌 Project Overview

- **목표**
  - 치매 코호트 데이터를 기반으로 치매 여부 예측
  - 치매 고위험군 조기 식별
  - F1-score **0.8 이상** 달성

- **핵심 특징**
  - 다중 모달리티 데이터 활용 (정형 + 영상)
  - 전처리 파이프라인 조합 기반 성능 비교
  - 앙상블 및 하이브리드 딥러닝 모델 적용
  - 엄격한 검증 (Stratified / Nested Cross Validation)

---

## 📊 Dataset

아시안 치매 재단(Asian Dementia Foundation) 코호트 데이터 활용 :contentReference[oaicite:0]{index=0}

### 데이터 구성 (총 1,001명)

| 데이터 유형 | 건수 |
|------------|------|
| Screening (인구통계, MMSE, K-AIDL 등) | 1,001 |
| SNSB 신경심리검사 | 1,000 |
| MRI ROI | 992 |
| PET 영상 | 200 |
| APOE 유전자 | 982 |

### 진단군 분포

- 인지정상(CN): 607
- 주관적 인지장애(SCD): 36
- 경도인지장애(MCI): 303
- 치매(Dementia): 44

➡️ **분석 시 재정의**
- CN + SCD → 정상 대조군
- MCI
- Dementia
- 진단 미상/기타 11명 제외 :contentReference[oaicite:1]{index=1}

---

## 🧹 Data Preprocessing

### 1. 정형 데이터 통합
- Demography, MMSE, K-AIDL, SNSB, MRI ROI, APOE
- Patient ID 기준 병합

### 2. PET 영상 정량화 (ML용)
- SPM 기반 전처리
- MNI152 템플릿 정렬
- ROI별 수치 특징 추출 후 테이블화 :contentReference[oaicite:2]{index=2}

### 3. PET 영상 전처리 (DL용)
- Spatial Normalization
- Intensity Normalization (Z-score)
- 2D Slice 추출 (Axial / Sagittal / Coronal)
- Data Augmentation (flip, rotation 등) :contentReference[oaicite:3]{index=3}

---

## ⚙️ Experimental Design

### 전처리 조합 (총 24개)

- 결측치 처리: Median / MICE
- 스케일링: StandardScaler / RobustScaler
- 클래스 불균형: SMOTE / SMOTEENN
- 특징 처리:
  - PCA
  - RFE
  - Autoencoder

➡️ **2 × 2 × 2 × 3 = 24 datasets**

---

## 🤖 Modeling Strategy

### Strategy A: 개별 앙상블 모델
- Random Forest
- XGBoost
- LightGBM
- CatBoost  
  ➡️ 24 datasets × 4 models = 96 experiments

### Strategy B: Stacking Ensemble
- 상위 2개 모델을 Base Learner로 사용
- Softmax Regression 기반 Meta Learner

### Strategy C: Hybrid Deep Learning
- Tabular Branch: Best ML model (ex. XGBoost)
- Image Branch: CNN (ResNet18 / VGG16)
- Feature Fusion 후 최종 분류 :contentReference[oaicite:4]{index=4}

---

## 📈 Evaluation

- **Metric**
  - F1-score (Primary)
  - Accuracy
- **Validation**
  - Stratified 5-Fold Cross Validation
  - Nested Cross Validation (최종 모델)

---

## 🔍 Hyperparameter Optimization

- Bayesian Optimization
  - Optuna / Hyperopt 사용
- Inner Loop: Hyperparameter Tuning
- Outer Loop: Generalization Performance Evaluation :contentReference[oaicite:5]{index=5}

---

## 🧪 MMSE-K Reference

- 한국판 MMSE-K 문항 및 채점 기준 사용
- 무학 보정 규칙 반영 :contentReference[oaicite:6]{index=6}

---

## 🏁 Expected Outcome

- 치매 예측 F1-score ≥ **0.8**
- 최적의 전처리 파이프라인 도출
- 임상 적용 가능성 있는 AI 진단 보조 모델 제시

---

## 🔗 Resources

- Dementia Multi-modal Big Data Platform  
  https://db.rexsoft.org/

---

## 👨‍💻 Environment

- Python (ML / DL)
- GPU Server
- scikit-learn, PyTorch, Optuna
- SPM (PET Processing)

