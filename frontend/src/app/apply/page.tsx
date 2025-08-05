'use client';

import { useState, useEffect, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Header from '../../shared/components/layout/Header';
import Footer from '../../shared/components/layout/Footer';
import { Pet, PetStatusType } from '../../shared/types';
import { formatAnimalAge, formatAnimalGender, formatAnimalSpecies } from '../../shared/utils';
import { petService } from '../../shared/services/petService';
import { adoptionService } from '../../shared/services/adoptionService';
import Image from 'next/image';

interface AdoptionFormData {
  petId: number;
  title: string;
  contactPhone: string;
  contactEmail: string;
  address: string;
  experience: string;
  familyMembers: string;
  anotherPets: string;
  message: string;
  type: 'AVAILABLE_FOR_ADOPTION' | 'AVAILABLE_FOR_CARE';
  careStartDate?: string;
  careEndDate?: string;
}

// 로딩 컴포넌트
const LoadingSpinner = () => (
  <div className="min-h-screen bg-gray-50">
    <Header />
    <div className="flex items-center justify-center min-h-[60vh]">
      <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-orange-500"></div>
    </div>
    <Footer />
  </div>
);

// 에러 컴포넌트
const ErrorPage = ({ onBackToGallery }: { onBackToGallery: () => void }) => (
  <div className="min-h-screen bg-gray-50">
    <Header />
    <div className="flex flex-col items-center justify-center min-h-[60vh]">
      <div className="text-6xl mb-4">🐾</div>
      <h2 className="text-2xl font-bold mb-2">동물 정보가 없습니다</h2>
      <p className="text-gray-500 mb-6">올바른 경로로 접근해주세요.</p>
      <button 
        onClick={onBackToGallery} 
        className="bg-orange-500 text-white px-6 py-2 rounded-lg hover:bg-orange-600 transition-colors"
      >
        갤러리로 돌아가기
      </button>
    </div>
    <Footer />
  </div>
);

// 폼 입력 필드 컴포넌트
const FormField = ({ 
  label, 
  name, 
  type = 'text', 
  value, 
  onChange, 
  placeholder, 
  required = false,
  rows = undefined 
}: {
  label: string;
  name: string;
  type?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
  placeholder?: string;
  required?: boolean;
  rows?: number;
}) => (
  <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">
      {label}
    </label>
    {type === 'textarea' ? (
      <textarea
        name={name}
        value={value}
        onChange={onChange}
        rows={rows || 3}
        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
        placeholder={placeholder}
        required={required}
      />
    ) : (
      <input
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
        placeholder={placeholder}
        required={required}
      />
    )}
  </div>
);

function ApplyPageContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const petIdFromUrl = searchParams.get('petId');
  
  const [selectedPet, setSelectedPet] = useState<Pet | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitMessage, setSubmitMessage] = useState('');

  const [formData, setFormData] = useState<AdoptionFormData>({
    petId: 0,
    title: '',
    contactPhone: '',
    contactEmail: '',
    address: '',
    experience: '',
    familyMembers: '',
    anotherPets: '',
    message: '',
    type: 'AVAILABLE_FOR_ADOPTION',
    careStartDate: '',
    careEndDate: '',
  });

  useEffect(() => {
    const loadPetData = async () => {
      try {
        setIsLoading(true);
        
        if (petIdFromUrl) {
          const petData = await petService.getPet(petIdFromUrl);
          setSelectedPet(petData);
          
          // petStatuses에 따라 기본 선택값 설정
          let defaultApplicationType: 'AVAILABLE_FOR_ADOPTION' | 'AVAILABLE_FOR_CARE' = 'AVAILABLE_FOR_ADOPTION';
          
          if (petData.petStatuses && petData.petStatuses.length > 0) {
            const canAdopt = petData.petStatuses.some((status) => 
              status === 'AVAILABLE_FOR_ADOPTION' || status === 'AVAILABLE_BOTH'
            );
            const canCare = petData.petStatuses.some((status) => 
              status === 'AVAILABLE_FOR_CARE' || status === 'AVAILABLE_BOTH'
            );
            
            // 입양 및 돌봄 모두 가능하면 입양을 기본값으로 설정
            if (canAdopt && canCare) {
              defaultApplicationType = 'AVAILABLE_FOR_ADOPTION';
            } else if (canAdopt) {
              defaultApplicationType = 'AVAILABLE_FOR_ADOPTION';
            } else if (canCare) {
              defaultApplicationType = 'AVAILABLE_FOR_CARE';
            }
          }
          
          setFormData(prev => ({ 
            ...prev, 
            petId: petData.id,
            type: defaultApplicationType
          }));
        }
      } catch (error) {
        console.error('Failed to load pet data:', error);
        setSubmitMessage('동물 정보를 불러오는데 실패했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    loadPetData();
  }, [petIdFromUrl]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!selectedPet) {
      setSubmitMessage('동물 정보가 없습니다. 다시 시도해주세요.');
      return;
    }

    // 필수 필드 검증 (contactName 제거)
    if (!formData.contactEmail || !formData.contactPhone || !formData.message) {
      setSubmitMessage('필수 항목을 모두 입력해주세요.');
      return;
    }

    // 돌봄 신청 시 날짜 필드 검증
    if (formData.type === 'AVAILABLE_FOR_CARE' && (!formData.careStartDate || !formData.careEndDate)) {
      setSubmitMessage('돌봄 시작일과 종료일을 모두 입력해주세요.');
      return;
    }

    // 돌봄 종료일이 시작일보다 이전인지 검증
    if (formData.type === 'AVAILABLE_FOR_CARE' && formData.careStartDate && formData.careEndDate) {
      if (formData.careEndDate < formData.careStartDate) {
        setSubmitMessage('돌봄 종료일은 시작일보다 늦어야 합니다.');
        return;
      }
    }

    setIsSubmitting(true);
    setSubmitMessage('');

    try {
      if (formData.type === 'AVAILABLE_FOR_ADOPTION') {
        // 입양 신청 API 호출
        await adoptionService.createAdoption({
          petId: selectedPet.id.toString(),
          title: formData.title,
          message: formData.message,
          anotherPets: formData.anotherPets,
          experience: formData.experience
        });
        setSubmitMessage('입양 신청이 성공적으로 제출되었습니다!');
      } else if (formData.type === 'AVAILABLE_FOR_CARE') {
        // 돌봄 신청 API 호출
        await adoptionService.createCare({
          petId: selectedPet.id.toString(),
          message: formData.message,
          title: formData.title,
          desiredStartDate: new Date(formData.careStartDate!),
          desiredEndDate: new Date(formData.careEndDate!),
          anotherPets: formData.anotherPets,
          experience: formData.experience
        });
        setSubmitMessage('돌봄 신청이 성공적으로 제출되었습니다!');
      }
      
      setTimeout(() => {
        router.push('/profile');
      }, 3000);
      
    } catch (error) {
      console.error('Application failed:', error);
      setSubmitMessage('신청에 실패했습니다. 다시 시도해주세요.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleBackToGallery = () => {
    router.push('/gallery');
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (!selectedPet) {
    return <ErrorPage onBackToGallery={handleBackToGallery} />;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="max-w-4xl mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">입양/돌봄 신청</h1>
          <p className="text-gray-600">입양/돌봄 신청서를 작성해주세요.</p>
        </div>

        <div className="bg-white rounded-lg shadow-lg p-8">
          {/* 선택된 동물 정보 */}
          <div className="mb-8 pb-6 border-b border-gray-200">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">입양/돌봄 신청 동물</h2>
            <div className="flex items-center space-x-4">
              {selectedPet.imageUrl && (
                <div className="w-24 h-24 relative rounded-lg overflow-hidden">
                  <Image
                    src={selectedPet.imageUrl.split('?')[0]}
                    alt={selectedPet.name}
                    fill
                    className="object-cover"
                  />
                </div>
              )}
              <div className="flex-1">
                <h3 className="text-lg font-semibold text-gray-900 mb-1">{selectedPet.name}</h3>
                <p className="text-sm text-gray-600 mb-2">
                  {formatAnimalSpecies(selectedPet.species)} • {formatAnimalAge(selectedPet.age)} • {formatAnimalGender(selectedPet.gender)}
                </p>
                {selectedPet.shelterName ? (
                  <p className="text-sm text-gray-500">보호소: {selectedPet.shelterName}</p>
                ) : (
                  <p className="text-sm text-gray-500">보호소 정보 없음</p>
                )}
              </div>
            </div>
          </div>

          {/* 입양/돌봄 신청서 폼 */}
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* 신청 유형 선택 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                신청 유형
              </label>
              <div className="flex space-x-6">
                <label className="flex items-center">
                  <input
                    type="radio"
                    name="type"
                    value="AVAILABLE_FOR_ADOPTION"
                    checked={formData.type === 'AVAILABLE_FOR_ADOPTION'}
                    onChange={(e) => setFormData({...formData, type: e.target.value as 'AVAILABLE_FOR_ADOPTION' | 'AVAILABLE_FOR_CARE'})}
                    className="mr-2"
                  />
                  <span>입양</span>
                </label>
                <label className="flex items-center">
                  <input
                    type="radio"
                    name="type"
                    value="AVAILABLE_FOR_CARE"
                    checked={formData.type === 'AVAILABLE_FOR_CARE'}
                    onChange={(e) => setFormData({...formData, type: e.target.value as 'AVAILABLE_FOR_ADOPTION' | 'AVAILABLE_FOR_CARE'})}
                    className="mr-2"
                  />
                  <span>돌봄</span>
                </label>
              </div>
            </div>

            {/* 돌봄 기간 선택 (돌봄 선택 시에만 표시) */}
            {formData.type === 'AVAILABLE_FOR_CARE' && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <FormField
                  label="돌봄 시작일"
                  name="careStartDate"
                  type="date"
                  value={formData.careStartDate || ''}
                  onChange={handleInputChange}
                  required
                />
                <FormField
                  label="돌봄 종료일"
                  name="careEndDate"
                  type="date"
                  value={formData.careEndDate || ''}
                  onChange={handleInputChange}
                  required
                />
              </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <FormField
                label="연락처 (전화번호)"
                name="contactPhone"
                type="tel"
                value={formData.contactPhone}
                onChange={handleInputChange}
                placeholder="010-1234-5678"
                required
              />

              <FormField
                label="이메일"
                name="contactEmail"
                type="email"
                value={formData.contactEmail}
                onChange={handleInputChange}
                placeholder="example@email.com"
                required
              />
            </div>

            <FormField
              label="주소"
              name="address"
              value={formData.address}
              onChange={handleInputChange}
              placeholder="서울시 강남구..."
            />

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <FormField
                label="가족 구성원"
                name="familyMembers"
                value={formData.familyMembers}
                onChange={handleInputChange}
                placeholder="성인 2명, 아이 1명"
              />

              <FormField
                label="현재 키우고 있는 다른 반려동물"
                name="anotherPets"
                value={formData.anotherPets}
                onChange={handleInputChange}
                placeholder="없음 또는 현재 키우고 있는 동물"
              />
            </div>

            <FormField
              label="반려동물 키우는 경험"
              name="experience"
              type="textarea"
              value={formData.experience}
              onChange={handleInputChange}
              placeholder="이전에 반려동물을 키운 경험이 있다면 간단히 설명해주세요."
              rows={3}
            />

            <FormField
              label={formData.type === 'AVAILABLE_FOR_ADOPTION' ? "입양하고 싶은 이유" : "돌봄하고 싶은 이유"}
              name="message"
              type="textarea"
              value={formData.message}
              onChange={handleInputChange}
              placeholder={formData.type === 'AVAILABLE_FOR_ADOPTION' ? "이 동물을 입양하고 싶은 이유를 설명해주세요." : "이 동물을 돌봄하고 싶은 이유를 설명해주세요."}
              required
              rows={3}
            />

            {submitMessage && (
              <div className={`p-4 rounded-lg ${
                submitMessage.includes('성공') 
                  ? 'bg-green-50 text-green-800' 
                  : 'bg-red-50 text-red-800'
              }`}>
                {submitMessage}
              </div>
            )}

            <div className="pt-6 border-t border-gray-200 space-y-4">
              <button
                type="button"
                onClick={() => router.push(`/chat?petId=${selectedPet.id}`)}
                className="w-full py-3 px-6 rounded-lg font-semibold text-lg border-2 border-orange-500 text-orange-500 hover:bg-orange-50 transition-colors"
              >
                상담하기
              </button>
              
              <button
                type="submit"
                disabled={isSubmitting}
                className={`w-full py-4 px-6 rounded-lg font-semibold text-lg transition-colors ${
                  isSubmitting
                    ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                    : 'bg-orange-500 text-white hover:bg-orange-600'
                }`}
              >
                {isSubmitting ? '제출 중...' : `${formData.type === 'AVAILABLE_FOR_ADOPTION' ? '입양' : '돌봄'} 신청하기`}
              </button>
            </div>
          </form>
        </div>
      </main>
      <Footer />
    </div>
  );
}

export default function ApplyPage() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <ApplyPageContent />
    </Suspense>
  );
}
