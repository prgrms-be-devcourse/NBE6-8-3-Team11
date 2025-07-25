import Image from 'next/image';
import { User } from '../types';
import { formatDate } from '../../../shared/utils';

interface ProfileInfoProps {
  user: User | null;
}

export default function ProfileInfo({ user }: ProfileInfoProps) {
  if (!user) {
    return (
      <div className="text-center py-8">
        <p className="text-gray-500">사용자 정보를 불러올 수 없습니다.</p>
      </div>
    );
  }

  const getMemberTypeLabel = (type: string) => {
    return type === 'adopter' ? '입양 희망자' : '보호소';
  };

  return (
    <div className="space-y-6">
      {/* 프로필 이미지 및 기본 정보 */}
      <div className="flex items-start space-x-6">
        <div className="relative">
          {user.profileImage ? (
            <Image
              src={user.profileImage}
              alt={user.name}
              width={120}
              height={120}
              className="rounded-full object-cover"
            />
          ) : (
            <div className="w-30 h-30 bg-gray-200 rounded-full flex items-center justify-center">
              <span className="text-4xl">👤</span>
            </div>
          )}
        </div>
        
        <div className="flex-1">
          <h2 className="text-2xl font-bold text-gray-900 mb-2">{user.name}</h2>
          <p className="text-gray-600 mb-4">{user.email}</p>
          
          <div className="flex items-center space-x-4 text-sm text-gray-500">
            <span className="bg-orange-100 text-orange-800 px-3 py-1 rounded-full">
              {getMemberTypeLabel(user.memberType)}
            </span>
            <span>가입일: {formatDate(user.createdAt)}</span>
          </div>
        </div>
      </div>

      {/* 상세 정보 */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="space-y-4">
          <h3 className="text-lg font-semibold text-gray-900">기본 정보</h3>
          
          <div className="space-y-3">
            <div className="flex justify-between py-2 border-b border-gray-100">
              <span className="text-gray-600">전화번호</span>
              <span className="font-medium">{user.phone}</span>
            </div>
            
            <div className="flex justify-between py-2 border-b border-gray-100">
              <span className="text-gray-600">주소</span>
              <span className="font-medium">{user.address}</span>
            </div>
            
            <div className="flex justify-between py-2 border-b border-gray-100">
              <span className="text-gray-600">가입일</span>
              <span className="font-medium">{formatDate(user.createdAt)}</span>
            </div>
          </div>
        </div>

        <div className="space-y-4">
          <h3 className="text-lg font-semibold text-gray-900">소개</h3>
          <p className="text-gray-700 leading-relaxed">
            {user.bio || '소개글이 없습니다.'}
          </p>
        </div>
      </div>

      {/* 선호도 정보 */}
      {user.preferences && (
        <div className="space-y-4">
          <h3 className="text-lg font-semibold text-gray-900">선호도</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-gray-50 p-4 rounded-lg">
              <h4 className="font-medium text-gray-900 mb-2">선호 동물</h4>
              <div className="flex flex-wrap gap-2">
                {user.preferences.preferredSpecies.map((species) => (
                  <span key={species} className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
                    {species === 'dog' ? '강아지' : species === 'cat' ? '고양이' : species}
                  </span>
                ))}
              </div>
            </div>
            
            <div className="bg-gray-50 p-4 rounded-lg">
              <h4 className="font-medium text-gray-900 mb-2">선호 나이</h4>
              <span className="text-sm text-gray-700">
                {user.preferences.preferredAge === 'young' ? '어린 동물' : 
                 user.preferences.preferredAge === 'adult' ? '성체' : '모든 나이'}
              </span>
            </div>
            
            <div className="bg-gray-50 p-4 rounded-lg">
              <h4 className="font-medium text-gray-900 mb-2">선호 크기</h4>
              <span className="text-sm text-gray-700">
                {user.preferences.preferredSize === 'small' ? '소형' :
                 user.preferences.preferredSize === 'medium' ? '중형' : '대형'}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
} 