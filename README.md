# Simple Was Server
------------------------------------
## 1. 개요
- http 요청 처리를 수행하는 간단한 was 서버

## 2. 사용 가능 환경
- java 1.7+ 이상의 환경

## 3. 사용법
- 실행 파일은 두가지가 제공된다 (Maven 빌드시 생성)
```sh
# 오리지널 소스만 패키징 된 jar
java -jar was.jar

# dependency 라이브러리 포함된 jar
java -jar was-jar-with-dependencies.jar
```
- jar 파일과 동일 폴더에 Config.json 를 위치시킨다 (mvn 컴파일시 자동 복사됨)

- jar와 동일 경로에 Config.json 파일을 위치시키면 해당 설정 파일을 읽어서 서버가 기동된다.
- Config.json 파일의 구성 예제는 아래와 같다
```js
{
	"listenPort": 8080,
	
	"requestReaderThreadMaxCount"    : 5,
	"requestProcessorThreadMaxCount" : 5,
	"responseWriterThreadMaxCount"   : 5,
	
	"resourcePattren" : [
		"html","css", "js", "jpg", "png", "ico"
	],
	"webRoot"	: "C:/httpserver/web",
	"documentRoot" : {
		"a.com"	: "www/a.com",
		"b.com"	: "www/b.com",
		"*"		: "www/default"
	},
	
	"errorPage" : {
		"403": "error/403.html",
		"404": "error/404.html",
		"500": "error/500.html",
		"*"  : "error/unknown.html"
	}
}
```
- Config.json 항목 구성은 아래와 같다.
  |항목                               |설명|
  |:----                              |:----|
  |listenPort                         |서버가 요청을 받기 위해 사용하는 포트
  |requestReaderThreadMaxCount        |요청 처리(SocketRead/Http 파싱) 수행하는 프로세서의 스레드 수
  |requestProcessorThreadMaxCount     |요청 처리(필터링, static file/servlet) 실행 작업을 수행하는 프로세서의 스레드 수
  |responseWriterThreadMaxCount       |응답 처리(SocketWrite/Http 응답 포맷 생성) 수행하는 프로세서의 스레드 수
  |resourcePattren                    |static resource로 식별할 파일 패턴
  |webRoot                            |static resource가 위치하는 경로
  |documentRoot                       |각 도메인별 기본 Resource file 위치(webRoot 하위) 
  |errorPage                          |에러페이지 경로. 각 도메인별 기본위치 하위에 파일이 있어야 한다
  
- 파일 경로 예시
 	- Static file Root 경로
 		- C:/httpserver/web
 	- a.com 도메인이 사용할 Static file 최상위 경로
 		- C:/httpserver/web/www/a.com
 	- a.com 도메인이 사용할 Error page file 경로
 		- C:/httpserver/web/www/a.com/error/403.html

- 로그 설정
	- src\main\resources 내의 logback.xml 설정 파일 위치
	- mvn compile시 포함됨
	
- Maven 컴파일 사항
 	- mvn clean package
 		- was.jar 생성(소스 파일만 포함)
 	- mvn clean package assembly:assembly
 		- was-jar-with-dependencies.jar (의존성 jar 파일도 포함)

- 1.0.0 RELEASE 구현 사항
	- host 도메인에 따른 다른 페이지 제공
	- 설정 파일을 json 포맷으로 관리
	- 요청 수신 포트 지정 (설정 파일에서 관리)
	- http status 오류 페이지 출력 기능 (설정에서 경로 관리)
	- 올바르지 않은 경로 호출(상위경로) 시 403 forbidden 결과 리턴
	- Logback 하루 단위 파일 롤링 수행
	- 오류 Stacktrace 로깅 수행
	- SimpleServlet 을 이용한 서블릿 호출 및 처리 기능
	- package 경로기반의 servlet 호출 기능
	- JUnit Test 포함
