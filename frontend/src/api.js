import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api', // 스프링 부트 서버 주소
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 5000,
});

export default api;