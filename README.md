Spring boot 기반 웹 개발 프로젝트로, 웹 개발을 공부하면서 배운 것을 적용하고자 만든 개인 프로젝트 입니다. 

추가적으로 공부하면서 기능을 하나씩 붙여나갈 생각입니다.

[프로젝트 적용기 블로그](https://devcabinet.tistory.com/category/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8)

- - - 

### 프로젝트 소개

멘토-멘티를 모집하고, 프로그램을 진행하는 웹 서비스 입니다.

멘토는 미션을, 멘티는 미션을 수행하는 게시판을 작성하여 멘토링 프로그램을 진행합니다. 

### 사용 기술

Front-end

\- JavaScript, Vue, Vuex, axios

Back-end

\- Java 11

\- Spring boot 2.5.3

\- JPA, MariaDB

\- Spring Security

\- Swagger

### 프로젝트 목표

백엔드를 지망하고 있지만, 프론트엔드가 어떻게 구성되어 있는지 알아두면 좋을 것 같아서 프론트, 백을 다 해보는 개인 프로젝트를 진행했습니다. 

한참 부족한 지금 단계에서 프론트, 백엔드 전부 다루는 것은 욕심일 것 같아 매우 기초적인 것만 찾아가면서 프론트를 적용했습니다. 

이전 프로젝트에서는 CRUD의 구현에 중점을 두었다면, 이번 프로젝트에서는 CRUD 이외 기능(시큐리티, 로그인, 에러핸들링 등)에 대해서 중점적으로 공부해보고 적용하는 것을 목표로 삼았습니다. 

(인프라 및 배포는 공부하면서 추가하면 추후 수정하도록 하겠습니다.)

### 프로젝트 진행

**\- ER Diagram**

![Mentoree](https://user-images.githubusercontent.com/65437310/155827097-b33c0d81-f933-4b6a-948e-d4ff98764a6c.png)


**\- API 명세서**

![image](https://user-images.githubusercontent.com/65437310/155827672-e34652a8-52e5-41e4-a89f-1f7ac5c6d70f.png)
![image](https://user-images.githubusercontent.com/65437310/155827687-524038c4-c69c-4325-849c-5c42a0cccc46.png)
![image](https://user-images.githubusercontent.com/65437310/155827692-40602f05-45be-4159-b88c-1a6ed78be929.png)


### 구현 기능

지속적으로 공부하며 부가적인 기능에 적용하여 추가하도록 하겠습니다. 

**회원**

\> 회원가입 (Oauth2.0, 폼 로그인 방식 - JWT 사용)

\> 회원정보 조회 / 수정

**프로그램**

\> 프로그램 생성

\> 프로그램 리스트 조회

\> 프로그램 참가 신청

\> 프로그램 참가 관리

**미션**

\> 미션 생성

\> 미션 및 게시글 조회

**게시글**

**\>** 게시글 작성

\> 게시글 조회

**댓글**

\> 댓글 작성

\> 댓글 조회
