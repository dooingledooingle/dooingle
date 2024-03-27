import {Link} from "react-router-dom";

export default function NoticeItem({id, title, createdAt}) {
  return (
    <div className="flex justify-between">
      <div>
        <Link to={`./${id}`} className="font-bold text-[1rem]">{title}</Link>
      </div>
      <div>
        <p className="font-bold text-[0.875rem]">{createdAt}</p>
      </div>
    </div>
  );
}