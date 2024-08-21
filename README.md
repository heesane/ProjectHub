# ProjectHub

## 프로젝트 공유 서비스

### 자신이 진행한 프로젝트를 등록 및 관리하고, 다른 사용자들과 공유할 수 있는 서비스를 제작하고자 합니다.

**Tech**
* Lang & Framework : Java, Spring Boot 3, JDK 17
* Database : Mysql
* Cache : Redis
* Search : Elastic Search

# Service

## User

### Login & Register

* JWT, OAuth(Google REST OAuth)를 통해 구현
* ID, Password, 닉네임을 입력받아 구현
  
### My Profile
* 닉네임, 주 사용기술, 등록한 프로젝트 관리
* 주 사용기술 수정 가능
* 등록한 유저에 한하여 해당 프로젝트를 공개/비공개 전환 가능

* 서비스 이용
** 로그인 시에만 댓글과 좋아요 가능
  
### Project
* 전체 조회 및 필터링
  * 해당 프로젝트의 프로젝트 명과 주제만 반환
  * @Cacheable을 통해 Redis 캐싱 적용 -> sort 고려 -> 최신순 캐싱, 별, 댓글 수를 고려해서 캐싱 적용
  * 검색 시, Elastic Search를 통해 리스트 반환
* 단건 조회
  * 해당 프로젝트의 상세 정보 반환, 좋아요 갯수, 댓글 수, 댓글 내용
* 등록
  * 프로젝트 이름, 주제, Github Link, 기능, 기여 내용, System Architecture, Skill, Tool
  * System Architecture는 S3를 통해서 저장 및 링크 반환
* 수정
  * 프로젝트 주제, 기능, 기여내용, System Architecture, Skill, Tool
* 삭제
  * 프로젝트 삭제

### Like
* 단건 프로젝트에 대해 사용자별 한번의 좋아요 기능 → 중복 안됨 → 재요청시 좋아요 취소

### Comment
* 한 서비스에 여러 댓글/대댓글 작성가능
  * 한 댓글/대댓글에 좋아요 기능 가능 → 위의 좋아요 기능과 동일하게 toggle 방식으로 구현

### Badge
* 사용자의 댓글 수와 받은 좋아요 수를 기준으로 배지를 부여
  * 댓글 혹은 좋아요 요청 시, 사용자의 댓글과 좋아요의 합계를 통해서 배지를 부여

# ERD

# System Architecture
