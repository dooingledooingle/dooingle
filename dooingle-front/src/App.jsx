import {useState} from 'react'

export default function App() {
  const [count, setCount] = useState(0)

  return (
    <div className="card">
      <div>abc</div>
      <button onClick={() => setCount((count) => count + 1)}>
        count is {count}
      </button>
    </div>
  )
}
