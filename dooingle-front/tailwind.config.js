/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        NotoSansKR: ["Noto Sans KR", "sans-serif"],
        NanumGothic: ["Nanum Gothic", "sans-serif"],
      },
    },
  },
  plugins: [],
}

