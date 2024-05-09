import {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {fetchUserList} from "../fetch.js";

export default function DooinglerListAside() {
  const [newDooinglerList, setNewDooinglerList] = useState([])
  const [hotDooinglerList, setHotDooinglerList] = useState([])

  useEffect(() => {
    fetchUserList("new").then(list => setNewDooinglerList(list))
    fetchUserList("hot").then(list => setHotDooinglerList(list))
  }, []);

  return (
    <aside className="col-start-10 col-span-3 flex flex-col justify-end items-center text-[#5f6368]">
      <div className="fixed bottom-[4.5rem]">
        <div
          className="flex flex-col items-center gap-[0.25rem] rounded-br-[0.625rem] border-b-[0.0625rem] border-[#ef7ec2]">
          <div className="flex flex-col gap-[0.5rem] px-[1rem] py-[0.625rem]">
            <div className="font-bold text-[#5f6368] text-[1rem]">
              <p>새로운 뒹글 페이지</p>
            </div>
            <div className="flex flex-col gap-[0.25rem] px-[0.625rem]">
              {newDooinglerList.map(newDooingler =>
                <Link key={newDooingler?.userLink}
                      to={`../personal-dooingles/${newDooingler?.userLink}`}
                      className="hover:text-[#ef7ec2]">
                  {newDooingler?.nickname}
                </Link>)}
            </div>
          </div>
          <div className="flex flex-col gap-[0.5rem] px-[1rem] py-[0.625rem]">
            <div className="font-bold text-[#5f6368] text-[1rem]">
              <p>뜨거운 뒹글 페이지</p>
            </div>
            <div className="flex flex-col gap-[0.25rem] px-[0.625rem]">
              {hotDooinglerList.map(hotDooingler =>
                <Link key={hotDooingler?.userLink}
                      to={`../personal-dooingles/${hotDooingler?.userLink}`}
                      className="hover:text-[#ef7ec2]">
                  {hotDooingler?.nickname}
                </Link>)}
            </div>
          </div>
        </div>
      </div>
    </aside>
  );
}
