// API 클라이언트 설정
// 팀원들의 기존 환경과 호환성을 위한 우선순위 설정
const API_BASE_URL = 
  process.env.NEXT_PUBLIC_API_URL ||  // 환경변수 우선
  'http://localhost:8080';             // Docker 환경 기본값

// 디버깅을 위한 로그
console.log('🔧 API_BASE_URL 설정:', {
  envValue: process.env.NEXT_PUBLIC_API_URL,
  finalValue: API_BASE_URL,
  hasEnv: !!process.env.NEXT_PUBLIC_API_URL
});

if (!API_BASE_URL) {
  throw new Error('API BASE URL이 설정되지 않았습니다!');
}

interface ApiResponse<T> {
  content: T;
  message: string;
  success: boolean;
  code: string;
}

class ApiClient {
  private baseURL: string;

  constructor(baseURL: string) {
    this.baseURL = baseURL;
  }

  private getAuthHeaders(): Record<string, string> {
    const token = localStorage.getItem('accessToken');
    console.log('🔑 Auth Token Check:', {
      hasToken: !!token,
      tokenLength: token ? token.length : 0,
      tokenPreview: token ? `${token.substring(0, 20)}...` : 'none'
    });
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<ApiResponse<T>> {
    // 엔드포인트가 이미 /api로 시작하는지 확인
    const normalizedEndpoint = endpoint.startsWith('/api') ? endpoint : `/api${endpoint}`;
    const url = `${this.baseURL}${normalizedEndpoint}`;
    
    console.log('🌐 API Request:', {
      method: options.method || 'GET',
      url: url,
      baseURL: this.baseURL,
      endpoint: endpoint,
      normalizedEndpoint: normalizedEndpoint,
      envCheck: {
        NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL,
        NODE_ENV: process.env.NODE_ENV
      }
    });
    
    const config: RequestInit = {
      headers: {
        'Content-Type': 'application/json',
        ...this.getAuthHeaders(),
        ...options.headers,
      },
      // CORS 문제 해결을 위해 credentials 제거
      // credentials: 'include', 
      ...options,
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        console.error('❌ API Error:', {
          status: response.status,
          statusText: response.statusText,
          url: url
        });
        
        // 401 Unauthorized 에러 처리 - 로그인 요청이 아닌 경우에만 토큰 갱신 시도
        if (response.status === 401) {
          // 로그인 요청인 경우 토큰 갱신을 시도하지 않고 오류를 그대로 전달
          if (normalizedEndpoint.includes('/auth/login')) {
            // 로그인 실패 시 친절한 메시지 전달
            throw new Error('이메일 또는 비밀번호가 올바르지 않습니다.');
          }
          
          // reissue 엔드포인트가 아닌 경우에만 토큰 갱신 시도
          if (!normalizedEndpoint.includes('/auth/reissue')) {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
              try {
                // 토큰 갱신 시도
                const reissueResponse = await this.request<{accessToken: string; refreshToken: string}>('/auth/reissue', {
                  method: 'POST',
                  body: JSON.stringify({ refreshToken }),
                });
                
                if (reissueResponse.success && reissueResponse.content) {
                  // 새 토큰 저장
                  localStorage.setItem('accessToken', reissueResponse.content.accessToken);
                  localStorage.setItem('refreshToken', reissueResponse.content.refreshToken);
                  
                  // 원본 요청 재시도
                  const retryConfig = {
                    ...config,
                    headers: {
                      ...config.headers,
                      'Authorization': `Bearer ${reissueResponse.content.accessToken}`
                    }
                  };
                  
                  const retryResponse = await fetch(url, retryConfig);
                  if (retryResponse.ok) {
                    const retryText = await retryResponse.text();
                    if (!retryText.trim()) {
                      return {
                        content: undefined as T,
                        message: 'Success',
                        success: true,
                        code: '200'
                      };
                    }
                    const retryData = JSON.parse(retryText);
                    console.log('✅ API Success after token refresh:', { url: url, data: retryData });
                    return retryData;
                  }
                }
              } catch (reissueError) {
                console.error('Token reissue failed:', reissueError);
              }
            }
          }
          
          // 토큰 갱신 실패하거나 reissue 엔드포인트인 경우 로그아웃 처리
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('userId');
          localStorage.removeItem('userEmail');
          localStorage.removeItem('userName');
          
          // 로그인 페이지로 리다이렉트
          if (typeof window !== 'undefined') {
            window.location.href = '/login';
          }
          
          // 401 오류는 이미 처리했으므로 여기서 종료
          throw new Error('인증이 필요합니다. 다시 로그인해주세요.');
        }
        
        // [수정!] 서버가 보낸 상세 에러 메시지를 읽어서 반환 (401이 아닌 경우)
        try {
          const errorData = await response.json();
          console.log('📄 Error Response Data:', errorData);
          console.log('📄 Error Response Status:', response.status);
          console.log('📄 Error Response Headers:', Object.fromEntries(response.headers.entries()));
          
          // 백엔드에서 보낸 message 필드가 있으면 사용, 없으면 기본 메시지
          if (errorData && errorData.message) {
            throw new Error(errorData.message);
          } else {
            throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`);
          }
        } catch (jsonError) {
          console.log('📄 JSON Parse Error:', jsonError);
          // Response body를 이미 읽었으므로 다시 읽지 않음
          throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`);
        }
      }
      
      // 응답이 비어있는지 확인
      const responseText = await response.text();
      
      // 응답이 비어있으면 빈 객체 반환
      if (!responseText.trim()) {
        console.log('✅ API Success (Empty Response):', url);
        return {
          content: undefined as T,
          message: 'Success',
          success: true,
          code: '200'
        };
      }
      
      // JSON 파싱
      const data = JSON.parse(responseText);
      console.log('✅ API Success:', { url: url, data: data });
      return data;
    } catch (error) {
      console.error('🚨 API Request Failed:', {
        url: url,
        error: error,
        message: error instanceof Error ? error.message : 'Unknown error'
      });
      
      // Network error에 대한 더 구체적인 메시지
      if (error instanceof TypeError && error.message === 'Failed to fetch') {
        throw new Error(`서버에 연결할 수 없습니다. 백엔드 서버(${this.baseURL})가 실행 중인지 확인해주세요.`);
      }
      
      throw error;
    }
  }

  async get<T>(endpoint: string): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: 'GET' });
  }

  async post<T>(endpoint: string, data?: unknown): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async put<T>(endpoint: string, data?: unknown): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async delete<T>(endpoint: string): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: 'DELETE' });
  }
}

export const apiClient = new ApiClient(API_BASE_URL);