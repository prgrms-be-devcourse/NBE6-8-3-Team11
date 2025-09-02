'use client';
import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { getUserInfoFromToken, type UserInfo } from '../shared/utils/jwt';
import { memberService } from '../shared/services/member';

interface AuthContextType {
  isLoggedIn: boolean;
  userInfo: UserInfo | null;
  isLoading: boolean;
  login: (accessToken: string, refreshToken: string, userData?: UserInfo) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const validateTokenOnLoad = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        const storedUserInfo = localStorage.getItem('userInfo');

        if (token && storedUserInfo) {
          // 토큰에서 사용자 정보 추출
          const parsedUserInfo = JSON.parse(storedUserInfo) as UserInfo;
          const userId = parsedUserInfo.id;

          if (!userId) {
            throw new Error('User ID not found in stored info.');
          }

          try {
            const user = await memberService.validateTokenAndGetCurrentUser(userId);
            if (user) {
              const combinedUserInfo = { ...parsedUserInfo, nickname: user.name, email: user.email };
              setUserInfo(combinedUserInfo);
              setIsLoggedIn(true);
              localStorage.setItem('userInfo', JSON.stringify(combinedUserInfo));
            } else {
              throw new Error('User not found from API');
            }
          } catch (apiError) {
            // API 호출 실패 시 기존 저장된 사용자 정보로 로그인 상태 유지
            console.warn('API call failed, using stored user info:', apiError);
            setUserInfo(parsedUserInfo);
            setIsLoggedIn(true);
          }
        } else {
          // 토큰이나 사용자 정보가 없으면 로그아웃 상태로 설정
          setIsLoggedIn(false);
          setUserInfo(null);
        }
      } catch (e) {
        console.error("Token validation failed", e);
        // 네트워크 에러가 아닌 경우에만 로그아웃 처리
        const isNetworkError = e instanceof TypeError && e.message === 'Failed to fetch';
        if (!isNetworkError) {
          // 인증 토큰 자체가 유효하지 않은 경우에만 localStorage 초기화
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('userInfo');
          localStorage.removeItem('userId');
          localStorage.removeItem('userEmail');
          localStorage.removeItem('userName');
        }
        setIsLoggedIn(false);
        setUserInfo(null);
      } finally {
        setIsLoading(false);
      }
    };

    validateTokenOnLoad();
  }, []); // 빈 배열: 컴포넌트 마운트 시 한 번만 실행

  const login = (accessToken: string, refreshToken: string, userData?: UserInfo) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    
    try {
      // userData가 제공되면 사용하고, 그렇지 않으면 토큰에서 추출
      const userInfoToUse = userData || getUserInfoFromToken(accessToken);
      
      if (userInfoToUse) {
        localStorage.setItem('userInfo', JSON.stringify(userInfoToUse));
        setUserInfo(userInfoToUse);
        setIsLoggedIn(true);
        console.log('User info set:', userInfoToUse); // 디버깅용
      } else {
        console.error('Failed to get user info from token');
      }
    } catch (error) {
      console.error('Error in login function:', error);
    }
  };

  const logout = () => {
    localStorage.clear(); // 모든 저장된 토큰 및 정보 삭제
    setIsLoggedIn(false);
    setUserInfo(null);
  };

  return (
    <AuthContext.Provider value={{ isLoggedIn, userInfo, isLoading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}