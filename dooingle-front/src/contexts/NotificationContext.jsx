import {createContext, useEffect, useState} from "react";
import {EventSourcePolyfill} from "event-source-polyfill";
import {BACKEND_SERVER_ORIGIN} from "../env.js";

export const NotificationContext = createContext()

export default function NotificationProvider({children}) {

  const [newFeedNotification, setNewFeedNotification] = useState(null);
  const [personalNotification, setPersonalNotification] = useState(null);

  useEffect(() => {
    const eventSource = new EventSourcePolyfill(`${BACKEND_SERVER_ORIGIN}/api/notifications/connect`,
      {withCredentials: true}
    );

    addSseConnectionEventListenerToEventSource(eventSource)
    addNewFeedNotificationEventListenerToEventSource(eventSource);
    // addPersonalNotificationEventListenerToEventSource(eventSource); TODO 개인 알림 관련 별도 작업 필요

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

  /*  TODO 개인 알림 관련 별도 작업 필요
    function addPersonalNotificationEventListenerToEventSource(eventSource) {
      eventSource.addEventListener("notification", event => {
        const receivedNotification = JSON.parse(event.data);

        switch (receivedNotification.notificationType) {
          case "DOOINGLE": receivedNotification.message = "새 뒹글이 굴러왔어요!"; break;
          case "CATCH": receivedNotification.message = "내 뒹글에 캐치가 달렸어요!"; break;
        }

        // TODO : 전달받는 데이터로 알림 컴포넌트 만들어서 알림 목록에 추가 (+ 팝업 띄우기 또는 알림 버튼 빨간색으로 바꾸기)
        setPersonalNotification(receivedNotification);
      });
    }*/

  return (
    <NotificationContext.Provider value={{newFeedNotification, setNewFeedNotification, personalNotification, setPersonalNotification}}>
      {children}
    </NotificationContext.Provider>
  );
}
