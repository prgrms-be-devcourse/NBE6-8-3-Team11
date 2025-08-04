import { apiClient } from './apiClient';
import { Pet, PetStatusType } from '../types';

// 테스트용 mock 데이터
const MOCK_PETS: Pet[] = [
  {
    id: 1,
    name: '멍멍이',
    species: 'dog',
    age: 3,
    gender: 'MALE',
    description: '활발하고 친근한 강아지입니다. 산책을 좋아하고 사람들과 잘 어울립니다.',
    imageUrl: 'https://images.unsplash.com/photo-1552053831-71594a27632d?w=400',
    shelterName: '행복한 보호소',
    memberIdCreatedBy: 1,
    createdAt: new Date('2024-01-15'),
    petStatus: 'AVAILABLE_FOR_ADOPTION' as PetStatusType,
  },
  {
    id: 2,
    name: '나비',
    species: 'cat',
    age: 2,
    gender: 'FEMALE',
    description: '조용하고 우아한 고양이입니다. 창가에서 햇빛을 즐기는 것을 좋아합니다.',
    imageUrl: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=400',
    shelterName: '사랑의 보호소',
    memberIdCreatedBy: 2,
    createdAt: new Date('2024-01-20'),
    petStatus: 'AVAILABLE_FOR_CARE' as PetStatusType,
  },
  {
    id: 3,
    name: '토토',
    species: 'rabbit',
    age: 1,
    gender: 'MALE',
    description: '귀엽고 순한 토끼입니다. 당근을 좋아하고 깔끔한 환경을 선호합니다.',
    imageUrl: 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=400',
    shelterName: '따뜻한 보호소',
    memberIdCreatedBy: 3,
    createdAt: new Date('2024-01-25'),
    petStatus: 'ADOPTED' as PetStatusType,
  },
  {
    id: 4,
    name: '치치',
    species: 'bird',
    age: 2,
    gender: 'FEMALE',
    description: '예쁜 노래를 부르는 새입니다. 사람과의 상호작용을 즐깁니다.',
    imageUrl: 'https://images.unsplash.com/photo-1444464666168-49d633b86797?w=400',
    shelterName: '행복한 보호소',
    memberIdCreatedBy: 1,
    createdAt: new Date('2024-01-30'),
    petStatus: 'CARE_IN_PROGRESS' as PetStatusType,
  },
  {
    id: 5,
    name: '바둑이',
    species: 'dog',
    age: 5,
    gender: 'MALE',
    description: '성숙하고 안정적인 강아지입니다. 가족과 함께하는 시간을 소중히 합니다.',
    imageUrl: 'https://images.unsplash.com/photo-1546527868-ccb7ee7dfa6a?w=400',
    shelterName: '사랑의 보호소',
    memberIdCreatedBy: 2,
    createdAt: new Date('2024-02-01'),
    petStatus: 'CARE_COMPLETED' as PetStatusType,
  },
  {
    id: 6,
    name: '미미',
    species: 'cat',
    age: 4,
    gender: 'FEMALE',
    description: '독립적이고 지적인 고양이입니다. 조용한 환경에서 편안함을 느낍니다.',
    imageUrl: 'https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=400',
    shelterName: '따뜻한 보호소',
    memberIdCreatedBy: 3,
    createdAt: new Date('2024-02-05'),
    petStatus: 'AVAILABLE_FOR_ADOPTION' as PetStatusType,
  },
];

export const petService = {
  // 모든 동물 조회
  async getPets(): Promise<Pet[]> {
    // 테스트를 위해 mock 데이터 반환
    return MOCK_PETS;
    
    // 실제 API 호출 (주석 처리)
    // const response = await apiClient.get<Pet[]>('/api/pets');
    // return response.content;
  },

  // 특정 동물 조회
  async getPet(petId: string): Promise<Pet> {
    // 테스트를 위해 mock 데이터에서 찾기
    const pet = MOCK_PETS.find(p => p.id === parseInt(petId));
    if (!pet) {
      throw new Error('Pet not found');
    }
    return pet;
    
    // 실제 API 호출 (주석 처리)
    // const response = await apiClient.get<Pet>(`/api/pets/${petId}`);
    // return response.content;
  },

  // 동물 생성
  async createPet(petData: Omit<Pet, 'id'>): Promise<Pet> {
    const response = await apiClient.post<Pet>('/api/pets', petData);
    return response.content;
  },

  // 특정 동물 정보 수정
  async updatePet(petId: string, petData: Partial<Pet>): Promise<Pet> {
    const response = await apiClient.put<Pet>(`/api/pets/${petId}`, petData);
    return response.content;
  },

  // 특정 동물 삭제
  async deletePet(petId: string): Promise<void> {
    await apiClient.delete(`/api/pets/${petId}`);
  },
};