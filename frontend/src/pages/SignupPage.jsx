import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const SignupPage = () => {
    const navigate = useNavigate();
    const [userType, setUserType] = useState('학생');

    const [formData, setFormData] = useState({
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
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleUserTypeChange = (type) => {
        setUserType(type);
        if (type === '교직원') {
            setFormData((prev) => ({ ...prev, grade: '' }));
        }
    };

    // 🌟 백엔드 반환 값(boolean)을 올바르게 체크하도록 수정한 함수
    const handleCheckId = async () => {
        if (!formData.loginId) {
            alert('학번/사번을 먼저 입력해주세요.');
            return;
        }
        try {
            const response = await api.get(`/auth/check-id?loginId=${formData.loginId}`);

            // 백엔드에서 중복일 때 true, 사용 가능일 때 false를 반환하므로 조건 분기 처리
            if (response.data === true) {
                alert('이미 사용 중인 아이디입니다.');
            } else {
                alert('사용 가능한 아이디입니다!');
            }
        } catch (error) {
            console.error('ID 중복 체크 에러:', error);
            alert('중복 체크 통신 중 오류가 발생했습니다.');
        }
    };

    const handleSignup = async (e) => {
        e.preventDefault();

        if (formData.password !== formData.passwordConfirm) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

        try {
            const { passwordConfirm, ...rest } = formData;

            const finalGrade = (userType === '학생' && rest.grade) ? parseInt(rest.grade, 10) : null;
            const mappedUserType = userType === '학생' ? 'STUDENT' : 'STAFF';

            const submitData = {
                ...rest,
                grade: finalGrade,
                userType: mappedUserType
            };

            await api.post('/auth/signup', submitData);
            alert('회원가입이 완료되었습니다!');
            navigate('/login');
        } catch (error) {
            console.error('회원가입 에러:', error);

            let errorMessage = '회원가입에 실패했습니다.';
            if (error.response?.data) {
                const data = error.response.data;
                errorMessage = data.message || (typeof data === 'object' ? JSON.stringify(data) : data);
            }
            alert(errorMessage);
        }
    };

    return (
        <div style={styles.pageContainer}>
            <div style={styles.card}>
                <h2 style={styles.title}>회원가입</h2>

                <form onSubmit={handleSignup} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <label style={styles.label}>회원 구분</label>
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <button
                                type="button"
                                onClick={() => handleUserTypeChange('학생')}
                                style={{
                                    ...styles.typeButton,
                                    backgroundColor: userType === '학생' ? '#eef2ff' : '#fff',
                                    color: userType === '학생' ? '#2d73f5' : '#888',
                                    border: userType === '학생' ? '1px solid #2d73f5' : '1px solid #ddd',
                                }}
                            >
                                학생
                            </button>
                            <button
                                type="button"
                                onClick={() => handleUserTypeChange('교직원')}
                                style={{
                                    ...styles.typeButton,
                                    backgroundColor: userType === '교직원' ? '#eef2ff' : '#fff',
                                    color: userType === '교직원' ? '#2d73f5' : '#888',
                                    border: userType === '교직원' ? '1px solid #2d73f5' : '1px solid #ddd',
                                }}
                            >
                                교직원
                            </button>
                        </div>
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>이름</label>
                        <input name="name" style={styles.input} placeholder="이름을 입력하세요" onChange={handleInputChange} required />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>학과/부서</label>
                        <input name="department" style={styles.input} placeholder="학과 또는 부서를 입력하세요" onChange={handleInputChange} required />
                    </div>

                    {userType === '학생' && (
                        <div style={styles.inputGroup}>
                            <label style={styles.label}>학년</label>
                            <input name="grade" type="number" style={styles.input} placeholder="학년을 숫자로 입력하세요" onChange={handleInputChange} required={userType === '학생'} />
                        </div>
                    )}

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>ID : 학번/사번</label>
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <input name="loginId" style={{ ...styles.input, flex: 1 }} placeholder="학번 또는 사번을 입력하세요" onChange={handleInputChange} required />
                            <button type="button" onClick={handleCheckId} style={styles.checkButton}>중복체크</button>
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

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>이메일</label>
                        <input name="email" type="email" style={styles.input} placeholder="이메일을 입력하세요" onChange={handleInputChange} required />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>전화번호</label>
                        <input name="phone" style={styles.input} placeholder="010-0000-0000" onChange={handleInputChange} required />
                    </div>

                    <button type="submit" style={styles.submitButton}>가입하기</button>
                </form>
            </div>
        </div>
    );
};

const styles = {
    pageContainer: { display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', backgroundColor: '#f9f9f9', padding: '40px 0' },
    card: { width: '500px', backgroundColor: '#ffffff', padding: '50px', borderRadius: '16px', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' },
    title: { fontSize: '24px', fontWeight: 'bold', color: '#333', textAlign: 'center', marginBottom: '30px' },
    form: { display: 'flex', flexDirection: 'column', gap: '20px' },
    inputGroup: { display: 'flex', flexDirection: 'column', gap: '8px' },
    label: { fontSize: '14px', fontWeight: 'bold', color: '#555' },
    input: { padding: '15px', fontSize: '14px', border: '1px solid #ddd', borderRadius: '8px', outline: 'none' },
    typeButton: { flex: 1, padding: '15px', borderRadius: '8px', fontSize: '15px', fontWeight: 'bold', cursor: 'pointer', transition: 'all 0.2s' },
    checkButton: { padding: '0 20px', backgroundColor: '#fff', border: '1px solid #ddd', borderRadius: '8px', fontSize: '14px', fontWeight: 'bold', cursor: 'pointer' },
    submitButton: { padding: '15px', backgroundColor: '#2d73f5', color: '#ffffff', border: 'none', borderRadius: '8px', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer', marginTop: '10px' }
};

export default SignupPage;