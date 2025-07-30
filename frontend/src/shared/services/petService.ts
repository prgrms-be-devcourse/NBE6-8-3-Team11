import { apiClient } from './apiClient';
import { Pet } from '../types';

export interface GetPetsParams {
  page?: number;
  limit?: number;
  type?: string;
  size?: string;
  gender?: string;
}

export interface GetPetsResponse {
  pets: Pet[];
  total: number;
  page: number;
  limit: number;
}

export const petService = {
  // 모든 동물 조회
  async getPets(params: GetPetsParams = {}): Promise<GetPetsResponse> {
    const queryParams = new URLSearchParams();
    
    if (params.page) queryParams.append('page', params.page.toString());
    if (params.limit) queryParams.append('limit', params.limit.toString());
    if (params.type) queryParams.append('type', params.type);
    if (params.size) queryParams.append('size', params.size);
    if (params.gender) queryParams.append('gender', params.gender);
    
    const queryString = queryParams.toString();
    const endpoint = `/pets${queryString ? `?${queryString}` : ''}`;
    
    const response = await apiClient.get<GetPetsResponse>(endpoint);
    return response.data;
  },

  // 특정 동물 조회
  async getPet(id: string): Promise<Pet> {
    const response = await apiClient.get<Pet>(`/pets/${id}`);
    return response.data;
  },

  // 동물 검색
  async searchPets(query: string): Promise<Pet[]> {
    const response = await apiClient.get<Pet[]>(`/pets/search?q=${encodeURIComponent(query)}`);
    return response.data;
  },

  // 입양 신청
  async applyForAdoption(petId: string, applicationData: unknown): Promise<unknown> {
    const response = await apiClient.post(`/pets/${petId}/apply`, applicationData);
    return response.data;
  },
}; 