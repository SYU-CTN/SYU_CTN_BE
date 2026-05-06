import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const SignupPage = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        userType: 'STUDENT',
        name: '',
        department: '',
        grade: '',
        loginId: '',
        password: '',
        passwordConfirm: '',
        email: '',
        phone: ''
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const setUserType = (type) => {
        setFormData(prev => ({ ...prev, userType: type }));
    };

    // 1. 학번/사번 중복체크 (명세서 1.2 반영)
    const checkDuplicateId = async () => {
        if (!formData.loginId) {
            alert('아이디(학번/사번)를 입력해주세요.');
            return;
        }
        try {
            const response = await api.get(`/api/v1/users/check-id?id=${formData.loginId}`);
            alert(response.data.message);
        } catch (error) {
            alert('중복 체크 중 오류가 발생했습니다.');
        }
    };

    // 2. 이메일 인증 함수 추가 (학번/사번 인증과 동일한 로직)
    const verifyEmail = async () => {
        if (!formData.email) {
            alert('이메일 주소를 입력해주세요.');
            return;
        }
        try {
            // 이메일 인증 API 호출 (주소는 백엔드 구현에 맞춰 수정 필요)
            const response = await api.post('/api/v1/users/verify-email', { email: formData.email });
            alert(response.data.message || '인증 이메일이 발송되었습니다.');
        } catch (error) {
            alert('이메일 인증 요청 중 오류가 발생했습니다.');
        }
    };

    const submitSignup = async (e) => {
        e.preventDefault();
        if (formData.password !== formData.passwordConfirm) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

        const { passwordConfirm, ...signupData } = formData;

        try {
            const response = await api.post('/api/v1/users/register', signupData);
            alert(response.data.message || '회원가입 완료!');
            navigate('/login');
        } catch (error) {
            alert(error.response?.data?.message || '회원가입 처리에 실패했습니다.');
        }
    };

    return (
        <div style={styles.pageContainer}>
            <div style={styles.card}>
                <h2 style={styles.title}>회원가입</h2>

                <form onSubmit={submitSignup} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <label style={styles.label}>회원 구분</label>
                        <div style={styles.toggleContainer}>
                            <button type="button" style={formData.userType === 'STUDENT' ? styles.toggleActive : styles.toggleInactive} onClick={() => setUserType('STUDENT')}>학생</button>
                            <button type="button" style={formData.userType === 'STAFF' ? styles.toggleActive : styles.toggleInactive} onClick={() => setUserType('STAFF')}>교직원</button>
                        </div>
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>이름</label>
                        <input name="name" style={styles.input} placeholder="이름을 입력하세요" onChange={handleInputChange} required />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>학과</label>
                        <input name="department" style={styles.input} placeholder="학과를 입력하세요" onChange={handleInputChange} required />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>학년</label>
                        <input name="grade" type="number" style={styles.input} placeholder="학년을 입력하세요" onChange={handleInputChange} required />
                    </div>

                    {/* 학번/사번 인증 영역 */}
                    <div style={styles.inputGroup}>
                        <label style={styles.label}>ID : 학번/사번</label>
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <input name="loginId" style={{ ...styles.input, flex: 1 }} placeholder="학번 또는 사번을 입력하세요" onChange={handleInputChange} required />
                            <button type="button" onClick={checkDuplicateId} style={styles.verifyButton}>중복체크</button>
                        </div>
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>비밀번호</label>
                        <input name="password" type="password" style={styles.input} placeholder="비밀번호를 입력하세요" onChange={handleInputChange} required />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>비밀번호 확인</label>
                        <input name="passwordConfirm" type="password" style={styles.input} placeholder="비밀번호를 다시 입력하세요" onChange={handleInputChange} required />
                    </div>

                    {/* 이메일 인증 영역 (학번/사번과 동일한 UI 적용) */}
                    <div style={styles.inputGroup}>
                        <label style={styles.label}>이메일</label>
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <input name="email" type="email" style={{ ...styles.input, flex: 1 }} placeholder="test@univ.ac.kr" onChange={handleInputChange} required />
                            <button type="button" onClick={verifyEmail} style={styles.verifyButton}>인증</button>
                        </div>
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>전화번호</label>
                        <input name="phone" style={styles.input} placeholder="010-1234-5678" onChange={handleInputChange} required />
                    </div>

                    <div style={styles.actionButtons}>
                        <button type="button" onClick={() => navigate(-1)} style={styles.cancelButton}>취소</button>
                        <button type="submit" style={styles.confirmButton}>확인</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

const styles = {
    pageContainer: { display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', width: '100%', backgroundColor: '#f9f9f9', padding: '40px 0' },
    card: { width: '450px', backgroundColor: '#ffffff', padding: '40px', borderRadius: '16px', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' },
    title: { fontSize: '24px', fontWeight: 'bold', color: '#333', textAlign: 'center', marginBottom: '30px' },
    form: { display: 'flex', flexDirection: 'column', gap: '15px' },
    inputGroup: { display: 'flex', flexDirection: 'column', gap: '8px' },
    label: { fontSize: '12px', fontWeight: 'bold', color: '#555' },
    input: { padding: '12px 15px', fontSize: '14px', border: '1px solid #ddd', borderRadius: '8px', outline: 'none' },
    toggleContainer: { display: 'flex', gap: '10px' },
    toggleActive: { flex: 1, padding: '12px', backgroundColor: '#eaf2ff', color: '#2d73f5', fontWeight: 'bold', border: '1px solid #cbe0ff', borderRadius: '8px', cursor: 'pointer' },
    toggleInactive: { flex: 1, padding: '12px', backgroundColor: '#ffffff', color: '#555', border: '1px solid #ddd', borderRadius: '8px', cursor: 'pointer' },
    verifyButton: { padding: '0 15px', backgroundColor: '#ffffff', border: '1px solid #ddd', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold', fontSize: '12px' },
    actionButtons: { display: 'flex', gap: '15px', marginTop: '20px' },
    cancelButton: { flex: 1, padding: '15px', backgroundColor: '#ffffff', border: '1px solid #ddd', borderRadius: '8px', fontWeight: 'bold', cursor: 'pointer' },
    confirmButton: { flex: 1, padding: '15px', backgroundColor: '#2d73f5', color: '#ffffff', border: 'none', borderRadius: '8px', fontWeight: 'bold', cursor: 'pointer' }
};

export default SignupPage;