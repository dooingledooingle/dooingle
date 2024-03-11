import { Link } from "react-router-dom";
import Header from "../components/Header.jsx";
import Dooingle from "../components/Dooingle.jsx";
import ProfileImageFrame from "../components/ProfileImageFrame.jsx";
import Navigation from "../components/Navigation.jsx";
import DooinglerListAside from "../components/DooinglerListAside.jsx";
import {useState} from "react";
import { EventSourcePolyfill } from 'event-source-polyfill';
import axios from "axios";

const BASE_URL = "http://localhost:8080"

export default function FeedPage() {

  const [notification, setNotification] = useState(null);
  const [feed, setFeed] = useState(null);

  const handleConnect = () => {

    const sse = new EventSourcePolyfill(`${BASE_URL}/api/notifications/connect?userId=2`);
    // TODO: headers 에 토큰 넣어서 보내야 함

    sse.addEventListener('connect', (e) => {
      const { data: receivedConnectData } = e;

      console.log('connect event data: ',receivedConnectData);
    });

    sse.addEventListener('notification', e => {
      const { data: receivedNotification } = e;

      console.log(receivedNotification);
      setNotification(receivedNotification)

      // 전달받는 텍스트 데이터는 메세지-dooingleId 형식. 예) 새로운 뒹글이 굴러왔어요!-5
      // TODO : 이벤트 발생 시 알림 팝업 띄워서 메세지 보여주고, 팝업 클릭 시 UserDooingle 페이지로 이동(cursor로 dooingleId 보내기)
    });

    sse.addEventListener('feed', e => {
      const { data: receivedFeed } = e;

      console.log(receivedFeed);
      setFeed(receivedFeed)

      // TODO : 피드 새로운 글 알림(새로고침이나 화살표 같은..) 버튼을 위에 뜨게 함
    });
  }

  const [testData, setTestData] = useState(null);

  const handleTestConnect = () => {

    // SSE 연결 요청. EventSource 라는 인터페이스를 써야 하는데 헤더 전달을 지원하는 Event-Source-Polyfill 사용
    const sse = new EventSourcePolyfill(`${BASE_URL}/connect`);

    // test-connect 라는 이름의 이벤트가 발생할 때 콘솔에 데이터 출력하는 이벤트 리스너 등록
    sse.addEventListener('test-connect', (e) => {
      // 첫 연결 시 만료 시간까지 아무런 데이터도 보내지 않으면 에러 발생하기 때문에 더미 데이터 보냄
      const { data: receivedConnectData } = e;

      console.log('connect event data: ',receivedConnectData);
    });

    // test 라는 이름의 이벤트가 발생할 때 콘솔에 변경된 데이터 출력하고, testData 업데이트하는 이벤트 리스너 등록
    // 다른 브라우저에서 test 호출 시 내 브라우저에 서버에서 변경된 testData가 찍히게 됨
    sse.addEventListener('test', e => {

      const { data: receivedTestData } = e;

      console.log("test event data", receivedTestData);
      setTestData(receivedTestData);
    });
  }

  // test 요청 버튼 클릭 시 localhost/test 로 요청 보냄
  const handleTestClick = async () => {
    await axios.post(`${BASE_URL}/test`)
        .then(function (response) {
          console.log('handleTestClick',response);
        })
        .catch(function (error) {
          console.log('error',error);
        });
  }

  return (
    <>
      <Header />

      <div className="grid grid-cols-12 gap-x-[2.5rem] mx-[8.75rem] h-[4.5rem] ml-40px">
        <nav className="col-start-1 col-span-3 flex justify-center text-[#5f6368]">
          <div className="flex flex-col items-center py-[3.75rem] gap-[1.25rem]">
            <ProfileImageFrame/>
            <Navigation/>
            <div className="flex flex-col items-center pt-10">
              <div className="text-xl text-red-500">알림 관련 임시</div>
              <button onClick={handleConnect}>connect 요청</button>
              <div>{notification}</div>
              <div>{feed}</div>
              <button onClick={handleTestConnect}>test connect 요청</button>
              <button onClick={handleTestClick}>test 요청</button>
              <div>{testData}</div>
            </div>
          </div>
        </nav>

        <section className="col-start-4 col-span-6 flex flex-col py-[2.75rem] text-[#5f6368]">
          <div className="flex px-[2rem] gap-[1.75rem] shadow-[inset_0_-0.125rem_0_0_#9aa1aa]">
            <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
              <button className="py-[0.5rem]">
                <div>
                  전체
                </div>
              </button>
            </div>
            <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
              <button className="py-[0.5rem]">
                <div>
                  팔로우
                </div>
              </button>
            </div>
          </div>

          <div className="py-[1rem]">
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
            <Dooingle></Dooingle>
          </div>
        </section>

        <DooinglerListAside />

        <div className="col-start-1 col-span-12 mt-10">
          <Link to={"/"}>웰컴 페이지로</Link>
        </div>
      </div>
    </>
  )
}
