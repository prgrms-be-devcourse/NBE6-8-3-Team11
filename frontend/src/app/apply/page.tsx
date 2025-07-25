'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Header from '../../shared/components/layout/Header';
import Footer from '../../shared/components/layout/Footer';
import { MOCK_PETS, MOCK_SHELTERS, MOCK_MEMBERS } from '../../shared/constants';
import { Pet, Shelter, Member } from '../../shared/types';
import { formatAnimalAge, formatAnimalGender, formatAnimalSpecies } from '../../shared/utils';
import Image from 'next/image';

interface AdoptionFormData {
  petId: number;
  message: string;
  contactPhone: string;
  contactEmail: string;
  address: string;
  experience: string;
  familyMembers: string;
  otherPets: string;
  reason: string;
}

export default function ApplyPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const petIdFromUrl = searchParams.get('petId');
  
  const [selectedPet, setSelectedPet] = useState<Pet | null>(null);
  const [availablePets, setAvailablePets] = useState<Pet[]>([]);
  const [shelters, setShelters] = useState<Shelter[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitMessage, setSubmitMessage] = useState('');

  const [formData, setFormData] = useState<AdoptionFormData>({
    petId: 0,
    message: '',
    contactPhone: '',
    contactEmail: '',
    address: '',
    experience: '',
    familyMembers: '',
    otherPets: '',
    reason: '',
  });

  useEffect(() => {
    // Mock 데이터 로드
    setAvailablePets(MOCK_PETS);
    setShelters(MOCK_SHELTERS);
    
    // URL에서 petId가 있으면 해당 동물 선택
    if (petIdFromUrl) {
      const pet = MOCK_PETS.find(p => p.id === Number(petIdFromUrl));
      if (pet) {
        setSelectedPet(pet);
        setFormData(prev => ({ ...prev, petId: pet.id }));
      }
    }
    
    setIsLoading(false);
  }, [petIdFromUrl]);

  const handlePetSelect = (pet: Pet) => {
    setSelectedPet(pet);
    setFormData(prev => ({ ...prev, petId: pet.id }));
  };

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
      setSubmitMessage('동물을 선택해주세요.');
      return;
    }

    if (!formData.message.trim()) {
      setSubmitMessage('메시지를 입력해주세요.');
      return;
    }

    setIsSubmitting(true);
    setSubmitMessage('');

    try {
      // Mock API 호출 시뮬레이션
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      // Mock 데이터에 추가
      const newAdoption = {
        id: Math.floor(Math.random() * 1000) + 100,
        memberId: 3, // Mock 사용자 ID
        petId: selectedPet.id,
        message: formData.message,
        status: 'pending' as const,
        createdAt: new Date(),
      };

      console.log('입양 신청 완료:', newAdoption);
      setSubmitMessage('입양 신청이 성공적으로 제출되었습니다!');
      
      // 3초 후 프로필 페이지로 이동
      setTimeout(() => {
        router.push('/profile');
      }, 3000);
      
    } catch (error) {
      setSubmitMessage('입양 신청에 실패했습니다. 다시 시도해주세요.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="flex items-center justify-center min-h-[60vh]">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-orange-500"></div>
        </div>
        <Footer />
      </div>
    );
  }

  const selectedShelter = selectedPet ? shelters.find(s => s.id === selectedPet.shelterId) : null;

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main className="max-w-4xl mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">입양 신청</h1>
          <p className="text-gray-600">원하는 동물을 선택하고 입양 신청서를 작성해주세요.</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* 동물 선택 영역 */}
          <div className="bg-white rounded-lg shadow-lg p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">동물 선택</h2>
            
            {selectedPet ? (
              <div className="mb-6">
                <div className="bg-orange-50 p-4 rounded-lg">
                  <h3 className="font-semibold text-gray-900 mb-2">선택된 동물</h3>
                  <div className="flex items-center space-x-4">
                    {selectedPet.imageUrl && (
                      <div className="w-20 h-20 relative rounded-lg overflow-hidden">
                        <Image
                          src={selectedPet.imageUrl.split('?')[0]}
                          alt={selectedPet.name}
                          fill
                          className="object-cover"
                        />
                      </div>
                    )}
                    <div>
                      <h4 className="font-semibold text-gray-900">{selectedPet.name}</h4>
                      <p className="text-sm text-gray-600">
                        {formatAnimalSpecies(selectedPet.species)} • {formatAnimalAge(selectedPet.age)} • {formatAnimalGender(selectedPet.gender)}
                      </p>
                      {selectedShelter && (
                        <p className="text-sm text-gray-500">{selectedShelter.name}</p>
                      )}
                    </div>
                  </div>
                  <button
                    onClick={() => setSelectedPet(null)}
                    className="mt-3 text-sm text-orange-600 hover:text-orange-700"
                  >
                    다른 동물 선택하기
                  </button>
                </div>
              </div>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 max-h-96 overflow-y-auto">
                {availablePets.map((pet) => (
                  <div
                    key={pet.id}
                    onClick={() => handlePetSelect(pet)}
                    className="bg-gray-50 p-4 rounded-lg cursor-pointer hover:bg-gray-100 transition-colors"
                  >
                    <div className="flex items-center space-x-3">
                      {pet.imageUrl && (
                        <div className="w-16 h-16 relative rounded-lg overflow-hidden">
                          <Image
                            src={pet.imageUrl.split('?')[0]}
                            alt={pet.name}
                            fill
                            className="object-cover"
                          />
                        </div>
                      )}
                      <div>
                        <h4 className="font-semibold text-gray-900">{pet.name}</h4>
                        <p className="text-sm text-gray-600">
                          {formatAnimalSpecies(pet.species)} • {formatAnimalAge(pet.age)}
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* 신청서 작성 영역 */}
          <div className="bg-white rounded-lg shadow-lg p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">입양 신청서</h2>
            
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  연락처 (전화번호)
                </label>
                <input
                  type="tel"
                  name="contactPhone"
                  value={formData.contactPhone}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  placeholder="010-1234-5678"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  이메일
                </label>
                <input
                  type="email"
                  name="contactEmail"
                  value={formData.contactEmail}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  placeholder="example@email.com"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  주소
                </label>
                <input
                  type="text"
                  name="address"
                  value={formData.address}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  placeholder="서울시 강남구..."
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  반려동물 키우는 경험
                </label>
                <textarea
                  name="experience"
                  value={formData.experience}
                  onChange={handleInputChange}
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  placeholder="이전에 반려동물을 키운 경험이 있다면 간단히 설명해주세요."
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  가족 구성원
                </label>
                <input
                  type="text"
                  name="familyMembers"
                  value={formData.familyMembers}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  placeholder="성인 2명, 아이 1명"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  현재 키우고 있는 다른 반려동물
                </label>
                <input
                  type="text"
                  name="otherPets"
                  value={formData.otherPets}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  placeholder="없음 또는 현재 키우고 있는 동물"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  입양하고 싶은 이유
                </label>
                <textarea
                  name="reason"
                  value={formData.reason}
                  onChange={handleInputChange}
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  placeholder="이 동물을 입양하고 싶은 이유를 설명해주세요."
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  보호소에 전할 메시지
                </label>
                <textarea
                  name="message"
                  value={formData.message}
                  onChange={handleInputChange}
                  rows={4}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  placeholder="보호소에 전하고 싶은 메시지를 자유롭게 작성해주세요."
                  required
                />
              </div>

              {submitMessage && (
                <div className={`p-3 rounded-lg ${
                  submitMessage.includes('성공') 
                    ? 'bg-green-50 text-green-800' 
                    : 'bg-red-50 text-red-800'
                }`}>
                  {submitMessage}
                </div>
              )}

              <button
                type="submit"
                disabled={isSubmitting || !selectedPet}
                className={`w-full py-3 px-4 rounded-lg font-semibold transition-colors ${
                  isSubmitting || !selectedPet
                    ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                    : 'bg-orange-500 text-white hover:bg-orange-600'
                }`}
              >
                {isSubmitting ? '제출 중...' : '입양 신청하기'}
              </button>
            </form>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
} 