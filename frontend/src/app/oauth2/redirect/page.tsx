'use client';

import { useSearchParams, useRouter } from 'next/navigation';
import { Suspense, useEffect, useState } from 'react';

// 동적 렌더링 강제
export const dynamic = 'force-dynamic';

function UserInfoDecoder() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const accessToken = searchParams.get('accessToken');
  const refreshToken = searchParams.get('refreshToken');

  useEffect(() => {
    const handleOAuthLogin = async () => {
      if (accessToken && refreshToken) {
        try {
          setIsLoading(true);
          
          // 토큰을 localStorage에 저장
          localStorage.setItem('accessToken', accessToken);
          localStorage.setItem('refreshToken', refreshToken);
          
          // JWT 토큰에서 사용자 정보 추출
          let userInfo = null;
          try {
            const base64Url = accessToken.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const decodedString = atob(base64);
            userInfo = JSON.parse(decodedString);
            console.log('JWT에서 추출한 사용자 정보:', userInfo);
          } catch (decodeError) {
            console.error('JWT 디코딩 실패:', decodeError);
          }

          // 사용자 정보를 localStorage에 저장
          if (userInfo) {
            // JWT에서 사용자 정보 추출 (토큰 구조에 따라 조정)
            const userId = userInfo.sub || userInfo.userId || userInfo.id || '1';
            const userEmail = userInfo.email || userInfo.userEmail || 'oauth@example.com';
            const userName = userInfo.name || userInfo.userName || userInfo.preferred_username || 'OAuth 사용자';
            
            localStorage.setItem('userId', userId.toString());
            localStorage.setItem('userEmail', userEmail);
            localStorage.setItem('userName', userName);
            
            console.log('저장된 사용자 정보:', {
              userId: localStorage.getItem('userId'),
              userEmail: localStorage.getItem('userEmail'),
              userName: localStorage.getItem('userName')
            });
          } else {
            // JWT 디코딩 실패 시 기본값 사용
            localStorage.setItem('userId', '1');
            localStorage.setItem('userEmail', 'oauth@example.com');
            localStorage.setItem('userName', 'OAuth 사용자');
          }
          
          // 홈으로 리다이렉트
          router.push('/');
          
        } catch (error) {
          console.error('OAuth 로그인 처리 중 오류:', error);
          setError('로그인 처리 중 오류가 발생했습니다.');
        } finally {
          setIsLoading(false);
        }
      } else {
        setError('토큰 정보가 없습니다.');
        setIsLoading(false);
      }
    };

    handleOAuthLogin();
  }, [accessToken, refreshToken, router]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500 mx-auto mb-4"></div>
          <p className="text-gray-600">로그인 처리 중...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error}</p>
          <button 
            onClick={() => router.push('/login')}
            className="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600"
          >
            로그인 페이지로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="text-center">
        <p className="text-gray-600">로그인 성공! 홈으로 이동 중...</p>
      </div>
    </div>
  );
}

export default function OAuthRedirectPage() {
  return (
    <Suspense fallback={
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500 mx-auto mb-4"></div>
          <p className="text-gray-600">토큰 정보를 로딩 중...</p>
        </div>
      </div>
    }>
      <UserInfoDecoder />
    </Suspense>
  );
}