'use client'; // 클라이언트 컴포넌트임을 명시합니다.

import { useEffect } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';

export default function OAuth2RedirectHandler() {
  const searchParams = useSearchParams();
  const router = useRouter();

  useEffect(() => {
    const accessToken = searchParams.get('accessToken');
    const refreshToken = searchParams.get('refreshToken');

    let userInfo = null;

    if (accessToken) {
      try {
        const base64Url = accessToken.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const binaryString = atob(base64);

        // 2. 바이너리 문자열을 Uint8Array로 변환 (바이트 배열)
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
          bytes[i] = binaryString.charCodeAt(i);
        }

        // 3. TextDecoder를 사용하여 Uint8Array를 UTF-8 문자열로 변환
        const decodedString = new TextDecoder('utf-8').decode(bytes);
        const decodedPayload = JSON.parse(decodedString);

        // JWT 페이로드에서 필요한 정보들을 추출하여 userInfo 객체에 저장합니다.
        // 예시 토큰에는 'nickname'과 'email'이 없으므로, 있다고 가정하고 추가했습니다.
        // 실제 토큰에 따라 필드명을 조정하거나, 없는 경우 'N/A' 등으로 처리할 수 있습니다.
        userInfo = {
          sub: decodedPayload.sub,
          auth: decodedPayload.auth,
          exp: decodedPayload.exp,
          // 예시: 닉네임과 이메일 필드가 토큰에 있다면 사용
          nickname: decodedPayload.nickname || null, // 토큰에 없으면 null
          email: decodedPayload.email || null,     // 토큰에 없으면 null
          // 다른 필요한 정보가 있다면 여기에 추가
        };

        // 1. accessToken 저장
        localStorage.setItem('accessToken', accessToken);
        console.log('Access Token 저장 완료');

        // 2. refreshToken 저장
        if (refreshToken) {
          localStorage.setItem('refreshToken', refreshToken);
          console.log('Refresh Token 저장 완료');
        }

        // 3. 디코딩된 회원 정보 저장 (JSON 문자열로 변환하여 저장)
        localStorage.setItem('userInfo', JSON.stringify(userInfo));
        console.log('회원 정보 저장 완료:', userInfo);

      } catch (error) {
        console.error("토큰 디코딩 또는 저장 중 오류 발생:", error);
        // 오류 발생 시에도 메인 페이지로 리다이렉트하거나, 오류 페이지로 리다이렉트할 수 있습니다.
      }
    } else {
      console.warn("URL에 accessToken이 없습니다.");
      // accessToken이 없는 경우 처리 로직 (예: 로그인 페이지로 리다이렉트)
    }

    // 모든 정보 저장 후 메인 페이지로 이동
    // `replace`를 사용하여 뒤로 가기 버튼으로 현재 페이지로 돌아오지 않도록 합니다.
    router.replace('/');
  }, [searchParams, router]); // searchParams나 router가 변경될 때마다 훅이 다시 실행되도록 의존성 배열에 추가

  // 이 컴포넌트는 UI를 렌더링할 필요가 없으므로 null을 반환합니다.
  return null;
}