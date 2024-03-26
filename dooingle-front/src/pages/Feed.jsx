import {Link} from "react-router-dom";
import Header from "../components/Header.jsx";
import Dooingle from "../components/Dooingle.jsx";
import ProfileImageFrame from "../components/ProfileImageFrame.jsx";
import Navigation from "../components/Navigation.jsx";
import DooinglerListAside from "../components/DooinglerListAside.jsx";
import {useEffect, useState} from "react";
import {EventSourcePolyfill} from 'event-source-polyfill';
import axios from "axios";
import {BACKEND_SERVER_ORIGIN} from "../env.js"

/*
const sliceInitialState = {
  // initial state를 안 정해주면 에러 발생해서 렌더링이 안 됨
  size: 0,
  content: [],
  number: 0,
  sort: {},
  first: true,
  last: true,
  numberOfElements: 0,
  pageable: {},
  empty: true,
}
 */

const notificationInitialState = {
  notificationType: null,
  message: null,
  cursor: 0
}

async function fetchDooinglesFeed(lastDooingleId = null) {
  const queryParameter = lastDooingleId === null ? "" : `?cursor=${lastDooingleId}`

  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/dooingles`.concat(queryParameter), {
    withCredentials: true, // ajax 요청에서 withCredentials config 추가
  });
  return response.data?.content;
}

async function fetchDooinglesFeedOfFollowing(lastDooingleId = null) {
  const queryParameter = lastDooingleId === null ? "" : `?cursor=${lastDooingleId}`

  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/dooingles/follow`.concat(queryParameter), {
    withCredentials: true,
  });
  return response.data?.content;
}

async function fetchLoggedInUserLink() {
  const response = await axios.get(`${BACKEND_SERVER_ORIGIN}/api/users/current-dooingler`, {
    withCredentials: true, // ajax 요청에서 withCredentials config 추가
  });
  return response.data.userLink;
}

export default function FeedPage() {

  const [sseNotification, setSseNotification] = useState(notificationInitialState);
  const [newFeedNotification, setNewFeedNotification] = useState(null);
  const [currentUserLink, setCurrentUserLink] = useState(undefined);
  const [dooingles, setDooingles] = useState([]);
  const [isEntireFeed, setIsEntireFeed] = useState(true) // TODO isEntireFeed state가 정말 필요한지는 더 고민해볼 것

  useEffect(() => {
    fetchDooinglesFeed().then(newDooingles => setDooingles(newDooingles));
    fetchLoggedInUserLink().then(loggedInUserLink => {
      setCurrentUserLink(loggedInUserLink)
    })

    const eventSource = new EventSourcePolyfill(`${BACKEND_SERVER_ORIGIN}/api/notifications/connect`,
      {withCredentials: true}
    );

    addSseConnectionEventListenerToEventSource(eventSource)
    addNewFeedNotificationEventListenerToEventSource(eventSource);
    addPersonalNotificationEventListenerToEventSource(eventSource);

    return () => {
      eventSource.close(); // 컴포넌트 unmount할 때 eventSource 닫기
    }
  }, []);

  function addSseConnectionEventListenerToEventSource(eventSource) {
    eventSource.addEventListener("connect", (event) => {
      const { data: receivedConnectData } = event;
      console.log("connect event data: ", receivedConnectData);
    });
  }

  function addNewFeedNotificationEventListenerToEventSource(eventSource) {
    eventSource.addEventListener("feed", event => {
      const receivedFeed = JSON.parse(event.data);
      setNewFeedNotification(receivedFeed); // 전달받는 데이터는 DooingleResponse 형식
      // TODO : 전달받는 데이터로 뒹글 컴포넌트 만들어서 뒹글 목록 위에 추가
    });
  }

  function addPersonalNotificationEventListenerToEventSource(eventSource) {
    eventSource.addEventListener("notification", event => {
      const receivedNotification = JSON.parse(event.data);

      switch (receivedNotification.notificationType) {
        case "DOOINGLE": receivedNotification.message = "새 뒹글이 굴러왔어요!"; break;
        case "CATCH": receivedNotification.message = "내 뒹글에 캐치가 달렸어요!"; break;
      }
      setSseNotification(receivedNotification); // 전달받는 데이터는 NotificationResponse 형식. 예시- {notificationType:'DOOINGLE', cursor:5}
      // TODO : 전달받는 데이터로 알림 컴포넌트 만들어서 알림 목록에 추가 (+ 팝업 띄우기 또는 알림 버튼 빨간색으로 바꾸기)
      //  알림 컴포넌트 클릭 시 PersonalDooingle 페이지로 이동(cursor 함께 보내기)
    });
  }

  function handleMoreFeedButton(isEntireFeed) {
    const lastDooingleId = dooingles.slice(-1)[0]?.["dooingleId"]

    if (isEntireFeed === true) {
      fetchDooinglesFeed(lastDooingleId).then(newDooingles => {
        setDooingles(prevDooingles => {
          const uniqueNewDooingles = newDooingles?.filter(newDooingle => prevDooingles.every(prevDooingle => prevDooingle?.dooingleId !== newDooingle?.dooingleId))
          return [...prevDooingles, ...uniqueNewDooingles]
        })
      })
    } else {
      fetchDooinglesFeedOfFollowing(lastDooingleId).then(newDooingles => {
        setDooingles(prevDooingles => {
          const uniqueNewDooingles = newDooingles?.filter(newDooingle => prevDooingles.every(prevDooingle => prevDooingle?.dooingleId !== newDooingle?.dooingleId))
          return [...prevDooingles, ...uniqueNewDooingles]
        })
      })
    }
  }

  function handleEntireFeedButton() {
    fetchDooinglesFeed().then(newDooingles => setDooingles(newDooingles));
    setIsEntireFeed(true);
  }

  function handleFollowingFeedButton() {
    fetchDooinglesFeedOfFollowing().then(newDooingles => setDooingles(newDooingles));
    setIsEntireFeed(false);
  }

  function handleNewFeedNotificationButton() {
    setNewFeedNotification(null);
  }

  const handleConnect = () => {

    const sse = new EventSourcePolyfill(
        `${BACKEND_SERVER_ORIGIN}/api/notifications/connect`,
        {withCredentials: true});

    sse.addEventListener('connect', (e) => {
      const { data: receivedConnectData } = e;

      console.log('connect event data: ',receivedConnectData);
    });

    sse.addEventListener('notification', e => {
      const receivedNotification = JSON.parse(e.data);

      switch (receivedNotification.notificationType) {
        case 'DOOINGLE': receivedNotification.message = "새 뒹글이 굴러왔어요!"; break;
        case 'CATCH': receivedNotification.message = "내 뒹글에 캐치가 달렸어요!"; break;
      }
      setSseNotification(receivedNotification)

      // 전달받는 데이터는 NotificationResponse 형식. 예시- {notificationType:'DOOINGLE', cursor:5}
      // TODO : 전달받는 데이터로 알림 컴포넌트 만들어서 알림 목록에 추가 (+ 팝업 띄우기 또는 알림 버튼 빨간색으로 바꾸기)
      //  알림 컴포넌트 클릭 시 PersonalDooingle 페이지로 이동(cursor 함께 보내기)
    });

    sse.addEventListener('feed', e => {
      const receivedFeed = JSON.parse(e.data);
      setNewFeedNotification(receivedFeed);
      console.log(receivedFeed);
      // 전달받는 데이터는 DooingleResponse 형식
      // TODO : 전달받는 데이터로 뒹글 컴포넌트 만들어서 뒹글 목록 위에 추가
    });
  }

  const [testData, setTestData] = useState(null);

  const handleTestConnect = () => {

    // SSE 연결 요청. EventSource 라는 인터페이스를 써야 하는데 헤더 전달을 지원하는 Event-Source-Polyfill 사용
    const sse = new EventSourcePolyfill(`${BACKEND_SERVER_ORIGIN}/connect`);

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
    await axios.post(`${BACKEND_SERVER_ORIGIN}/test`)
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
            <ProfileImageFrame userLink={currentUserLink} />
            <Navigation/>
            <div className="flex flex-col items-center pt-10">
              <div className="text-xl text-red-500">알림 관련 임시</div>
              <button onClick={handleConnect}>connect 요청</button>
              <div className="py-[1rem]">
                {sseNotification.message}
                {sseNotification.cursor}
              </div>
              <button onClick={handleTestConnect}>test connect 요청</button>
              <button onClick={handleTestClick}>test 요청</button>
              <div>{testData}</div>
            </div>
          </div>
        </nav>

        <section className="col-start-4 col-span-6 flex flex-col py-[2.75rem] text-[#5f6368]">
          <div className="flex px-[2rem] gap-[1.75rem] shadow-[inset_0_-0.125rem_0_0_#9aa1aa]">
            <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
              <button onClick={handleEntireFeedButton} className="py-[0.5rem]">
                <div>
                  전체
                </div>
              </button>
            </div>
            <div className="hover:shadow-[inset_0_-0.125rem_0_0_#fa61bd]">
              <button onClick={handleFollowingFeedButton} className="py-[0.5rem]">
                <div>
                  팔로우
                </div>
              </button>
            </div>
          </div>

          {newFeedNotification && (
            <div className="border-2">
              <button onClick={handleNewFeedNotificationButton}>새 피드가 있어요!</button>
            </div>
          )}

          <div className="py-[1rem]">
            {dooingles.map(dooingle => (
                <Dooingle
                    key={dooingle.dooingleId}
                    ownerName={dooingle.ownerName}
                    content={dooingle.content}
                />
            ))}
          </div>
          <button onClick={() => handleMoreFeedButton(isEntireFeed)} className="bg-amber-50">뒹글 더 보기</button>
        </section>

        <DooinglerListAside/>

        <div className="col-start-1 col-span-12 mt-10">
          <Link to={"/"}>웰컴 페이지로</Link>
        </div>
      </div>
    </>
  )
}
