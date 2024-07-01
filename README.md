# Issue Tracking System

## 프로젝트 소개

### 프로젝트 목적

- 깃허브와 같은 이슈 관리 프로그램 제작
- 유스케이스 명세부터 도메인 모델 설계, SSD, Operation Contract 등의 설계과정 구체화
- OOAD/GRASP 원칙에 따른 아키텍쳐 설계(도메인형 아키텍쳐 적용)

### 프로젝트 구현
![소공 로그인](https://github.com/pstar987/SE_ITS_Back-End/assets/63050966/6af3d082-dd0e-4c19-8a3c-4b7b7d13a062)

![소공 관리자 홈](https://github.com/pstar987/SE_ITS_Back-End/assets/63050966/0989e8ee-f1d6-42e0-90b4-78aae9a5bd13)

![소공 관리자 프로젝트](https://github.com/pstar987/SE_ITS_Back-End/assets/63050966/29bd7242-1685-4aa3-ad6e-fe8ed3a8163f)

![소공 관리자 이슈](https://github.com/pstar987/SE_ITS_Back-End/assets/63050966/a33b945a-55a9-40f2-a83d-8ef7a048ba84)

![소공 개발자 이슈](https://github.com/pstar987/SE_ITS_Back-End/assets/63050966/df2af723-3b49-40e6-ba1d-825ae6ae97ba)

### 프로젝트 설계 - SSD
![소공 SSD](https://github.com/pstar987/SE_ITS_Back-End/assets/63050966/19d1e2d9-59b3-469d-8bb4-696536f3749b)

### 프로젝트 설계 - 유스케이스 다이어그램
![소공 유스케이스](https://github.com/pstar987/SE_ITS_Back-End/assets/63050966/c95fd0d8-e2ef-44a5-a97b-1ba9e5813e43)


### Infrastructure
![소공 인프라](https://github.com/pstar987/SE_ITS_Back-End/assets/63050966/198e8829-b2ae-4dac-9a22-258c18e94d05)

### 참여 인원
| 역할 | 인원수 |
| --- | --- |
| Back-End | 2명 |
| React | 1명 |
| Swing | 1명 |
| Machine Learning | 1명 |


# 프로그램 사용 방법
- 전체 프로젝트 zip 파일 압축 해제 후, SE_ITS_Back-End\src\main\java\com\se\its\ITSApplication.java 의 main Method 실행 시 BE Server 실행과 동시에 Java Swing GUI 실행
- 단, local에 MySQL이 설치되어 있고 가동하고 있어야 함. MySQL 버전은 최신 버전(8.3.0)이여야 하고 포트는 기본 포트, user name은 root, 비밀번호는 1234 이여야 하며 database의 이름이 db_se_ITS 인 database가 있어야 함.
- jdk 17이 설치되어 있어야 함.
- src/main/resources 하위에 src/main/resources/application.yml 파일이 있어야 함. 파일 내용은 아래와 같음


```
spring:
  application:
    name: ITS
  output:
    ansi:
      enabled: always
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost/db_se_ITS?serverTimezone=Asia/Seoul&useSSL=false
    username: root
    password: 1234

  sql:
    init:
      mode: never

  jpa:
    # database-platform: org.hibernate.dialect.MYSQLDialect
    hibernate:
      ddl-auto: update
    # 배포 시엔 open-in-view false 설정
    open-in-view: true
    properties:
      hibernate:
        show_sql: true
        format_sql: true
        # dialect: org.hibernate.dialect.MYSQLDialect
        hbm2ddl:
          auto: update
        default_batch_fetch_size: 1000



server:
  error:
    include-exception: true
    include-stacktrace: always
  port: 8080

logging:
  level:
    org:
      hibernate:
        type:
          descriptor:
            sql: debug
```
