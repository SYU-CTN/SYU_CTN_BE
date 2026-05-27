import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import MyPage from './pages/MyPage'; // 👈 [확인 1] MyPage를 제대로 불러왔는지!

function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* 기본 주소('/')로 오면 로그인 페이지로 보내기 */}
                <Route path="/" element={<Navigate to="/login" replace />} />

                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignupPage />} />

                {/* 👇 [확인 2] /mypage 주소로 갈 때 MyPage를 띄워주라는 이 코드가 있는지! */}
                <Route path="/mypage" element={<MyPage />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;