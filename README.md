# 2024-1학기 중앙대학교 소프트웨어공학 team01 Back-end Repository
## 주제 : Issue Tracking System


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
