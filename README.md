# 💌 dooingle: 질문을 굴려봐!🙂🙃🙂

## 프로젝트 소개
- ### 프로젝트 개요
  - (프로젝트 이름) dooingle 뒹글!
  - (개발 기간) 2024.02.26 ~ 2024.04.05 (예정)
  - (프로젝트 설명)  
    뒹글은 익명 Q&A SNS 입니다. 익명으로 질문(dooingle)을 굴려보내고 굴러온 뒹글 중 원하는 것을 잡아 답변(catch) 할 수 있습니다.
  - [ERD](https://www.notion.so/rugii913/ERD-ddebf6e68ba24f948d02073e1616dedf)
  - [API 명세](https://www.notion.so/rugii913/API-2cad8d39288f4b73b0b684f8323acfd7)
  - [노션 팀 페이지](https://www.notion.so/rugii913/da051119c2634c068e66964e1a47dade)
    - [코드 및 깃 컨벤션](https://www.notion.so/rugii913/550df0ee1fef4ddd914e5796c0128f1c)
    - [5분 기록 보드](https://www.notion.so/rugii913/5-10c8d36e05394b95bb67407ca656fc32)
    - [뒹글 프로젝트 WBS 및 진행 일정(구글 시트)](https://docs.google.com/spreadsheets/d/1jgkpkn0jP7iAF277EAHumw7O-3PPpjvyzj0hoMFfzFo/edit?usp=sharing)


- ### 팀 소개(역할분배)
  - 최유민(리더)❤️ 뜨거운 뒹글러, 새 뒹글러, 팔로우 뒹글 피드 조회 공지사항 C, 소셜 로그인, 알림
  - 곽준선(부리더)💚 뒹글 피드 조회, CI, 테스트 초안, 화면 퍼블리싱 및 프론트엔드 전반
  - 노하영(팀원)💙 프로필 조회, 수정, 프로필 이미지 처리, 뒹글 신고 및 블락 기능
  - 김다진(팀원)💜 뒹글, 캐치 관련 CRUD, 팔로우 CRD, 공지사항(RUD) / 조회 커서 기반 페이지네이션 포함

- ### 주요 제공 서비스
  - **실시간 알림과 지난 알림보기** 🔔  
    뒹글러들의 이용 편의를 위해 뒹글이 굴러왔을때, 내가 굴린 뒹글이 캐치되었을 때 알림 서비스를 제공합니다.
  - **새로운 뒹글러와 뜨거운 뒹글러** 🔥  
    하루동안 새로 가입한 뒹글러와 가장 뒹글을 많이 받을 뒹글러를 확인 할 수 있습니다.
  - **팔로우한 뒹글러** 🏃  
    내가 자주 찾는 뒹글러를 등록하여 그 뒹글들만 모아놓은 피드를 볼 수 있습니다. 익명성 보장을 위해 내가 팔로우한 뒹글러의 목록은 자신만 볼 수 있으며 팔로워는 숫자로만 표시 됩니다.
  - **악의적 뒹글러 신고, 차단** 🚨  
    익명 서비스의 특성상 존재할 수 있는 악의적 이용자로부터 뒹글러들을 보호하기 위해 서비스 운영자가 관리, 감독할 수 있도록 신고, 차단 서비스를 제공합니다.

## 기술적 의사결정
- [기술 및 서비스 의사 결정](https://www.notion.so/rugii913/47139fe7a2d948ff830caa625cf742f6)
  - [뜨거운 뒹글러 목록 조회](https://www.notion.so/rugii913/2024-02-28-38ec48a64e5c4d0494f14cde992d20ba)
  - [개인 뒹글 페이지 조회 쿼리 개션](https://www.notion.so/rugii913/2024-03-08-1edf38fa8e5947b78729bd492f3bca78)
  - [프론트엔드 로그인 구현 리다이렉트](https://www.notion.so/rugii913/2024-03-11-4762e20bbde64b6895fb4323abf08b60)

## 트러블 슈팅
- [트러블 슈팅](https://www.notion.so/rugii913/42340aebdb3d4d0ea2b606e5bbbc6739)

## 아키텍쳐
<img src="./readme-images/architecture-2024-03-18.png" width="full" alt="2024년 3월 18일 기준 서버 아키텍처">

## 빌드 방법
- (백엔드) GitHub Actions를 이용한 jar 빌드 및 AWS S3 전송, AWS CodeDeploy를 이용하여 AWS S3에 올라온 jar 파일 배포 
- (프론트엔드) GitHub Actions를 이용한 빌드 및 Vercel 자동 배포
