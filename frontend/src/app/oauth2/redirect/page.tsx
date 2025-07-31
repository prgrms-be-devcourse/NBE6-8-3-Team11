'use client'; // 클라이언트 컴포넌트임을 명시합니다.

import { useSearchParams } from 'next/navigation';

export default function MyClientComponent() {
  const searchParams = useSearchParams();

  // URL에서 'accessToken' 값을 가져옵니다.
  const accessToken = searchParams.get('accessToken');

  // URL에서 'refreshToken' 값을 가져옵니다.
  const refreshToken = searchParams.get('refreshToken');

  return (
    <div>
      <p>Access Token: {accessToken}</p>
      <p>Refresh Token: {refreshToken}</p>
    </div>
  );
}