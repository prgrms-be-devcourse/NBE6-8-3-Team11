import Image from "next/image";
import Link from "next/link";

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 to-yellow-50">
      {/* Header */}
      <header className="bg-white/80 backdrop-blur-sm border-b border-orange-100 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-2">
              <div className="w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center">
                <span className="text-white font-bold text-sm">🐾</span>
              </div>
              <span className="text-xl font-bold text-gray-800">펫프렌드</span>
            </div>
            <nav className="hidden md:flex space-x-8">
              <Link href="/" className="text-gray-700 hover:text-orange-500 transition-colors">
                홈
              </Link>
              <Link href="/gallery" className="text-gray-700 hover:text-orange-500 transition-colors">
                입양동물
              </Link>
              <Link href="/profile" className="text-gray-700 hover:text-orange-500 transition-colors">
                내정보
              </Link>
              <Link href="/apply" className="text-gray-700 hover:text-orange-500 transition-colors">
                입양신청
              </Link>
            </nav>
            <div className="flex items-center space-x-4">
              <button className="bg-orange-500 text-white px-4 py-2 rounded-lg hover:bg-orange-600 transition-colors">
                로그인
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="relative py-20 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div className="space-y-8">
              <h1 className="text-4xl md:text-6xl font-bold text-gray-800 leading-tight">
                사랑스러운 <span className="text-orange-500">반려동물</span>과<br />
                특별한 인연을 만들어보세요
              </h1>
              <p className="text-xl text-gray-600 leading-relaxed">
                유기동물들에게 새로운 가족이 되어주세요. 
                우리는 보호소와 입양희망자를 연결하여 
                모든 동물이 사랑받는 가정에서 살 수 있도록 도와드립니다.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <button className="bg-orange-500 text-white px-8 py-4 rounded-lg text-lg font-semibold hover:bg-orange-600 transition-all transform hover:scale-105">
                  입양동물 보기
                </button>
                <button className="border-2 border-orange-500 text-orange-500 px-8 py-4 rounded-lg text-lg font-semibold hover:bg-orange-500 hover:text-white transition-all">
                  입양신청하기
                </button>
              </div>
            </div>
            <div className="relative">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-4">
                  <div className="bg-white rounded-2xl p-4 shadow-lg transform rotate-3 hover:rotate-0 transition-transform">
                    <div className="w-full h-48 bg-gradient-to-br from-orange-200 to-yellow-200 rounded-xl flex items-center justify-center">
                      <span className="text-4xl">🐕</span>
                    </div>
                  </div>
                  <div className="bg-white rounded-2xl p-4 shadow-lg transform -rotate-2 hover:rotate-0 transition-transform">
                    <div className="w-full h-32 bg-gradient-to-br from-blue-200 to-purple-200 rounded-xl flex items-center justify-center">
                      <span className="text-3xl">🐱</span>
                    </div>
                  </div>
                </div>
                <div className="space-y-4 pt-8">
                  <div className="bg-white rounded-2xl p-4 shadow-lg transform -rotate-1 hover:rotate-0 transition-transform">
                    <div className="w-full h-32 bg-gradient-to-br from-green-200 to-teal-200 rounded-xl flex items-center justify-center">
                      <span className="text-3xl">🐰</span>
                    </div>
                  </div>
                  <div className="bg-white rounded-2xl p-4 shadow-lg transform rotate-2 hover:rotate-0 transition-transform">
                    <div className="w-full h-48 bg-gradient-to-br from-pink-200 to-red-200 rounded-xl flex items-center justify-center">
                      <span className="text-4xl">🐦</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 text-center">
            <div className="space-y-2">
              <div className="text-4xl font-bold text-orange-500">1,234</div>
              <div className="text-gray-600">성공한 입양</div>
            </div>
            <div className="space-y-2">
              <div className="text-4xl font-bold text-orange-500">567</div>
              <div className="text-gray-600">보호중인 동물</div>
            </div>
            <div className="space-y-2">
              <div className="text-4xl font-bold text-orange-500">89</div>
              <div className="text-gray-600">협력 보호소</div>
            </div>
            <div className="space-y-2">
              <div className="text-4xl font-bold text-orange-500">2,345</div>
              <div className="text-gray-600">입양희망자</div>
            </div>
          </div>
        </div>
      </section>

      {/* Services Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-800 mb-4">
              우리가 제공하는 서비스
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              유기동물과 입양희망자를 위한 다양한 서비스를 제공합니다
            </p>
          </div>
          
          <div className="grid md:grid-cols-3 gap-8">
            <div className="bg-white rounded-2xl p-8 shadow-lg hover:shadow-xl transition-shadow">
              <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mb-6">
                <span className="text-2xl">🔍</span>
              </div>
              <h3 className="text-xl font-semibold text-gray-800 mb-4">동물 검색</h3>
              <p className="text-gray-600">
                다양한 유기동물들을 갤러리 형태로 쉽게 찾아보고 
                상세 정보를 확인할 수 있습니다.
              </p>
            </div>
            
            <div className="bg-white rounded-2xl p-8 shadow-lg hover:shadow-xl transition-shadow">
              <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mb-6">
                <span className="text-2xl">📝</span>
              </div>
              <h3 className="text-xl font-semibold text-gray-800 mb-4">입양 신청</h3>
              <p className="text-gray-600">
                간편한 신청서를 통해 원하는 동물에 대한 
                입양을 신청할 수 있습니다.
              </p>
            </div>
            
            <div className="bg-white rounded-2xl p-8 shadow-lg hover:shadow-xl transition-shadow">
              <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mb-6">
                <span className="text-2xl">🤝</span>
              </div>
              <h3 className="text-xl font-semibold text-gray-800 mb-4">매칭 서비스</h3>
              <p className="text-gray-600">
                보호소와 입양희망자를 연결하여 
                최적의 매칭을 도와드립니다.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Gallery Preview */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-white">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-800 mb-4">
              입양 대기중인 동물들
            </h2>
            <p className="text-xl text-gray-600">
              새로운 가족을 기다리는 사랑스러운 친구들을 만나보세요
            </p>
          </div>
          
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {[1, 2, 3, 4].map((item) => (
              <div key={item} className="bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow cursor-pointer group">
                <div className="h-48 bg-gradient-to-br from-orange-200 to-yellow-200 flex items-center justify-center">
                  <span className="text-6xl">🐕</span>
                </div>
                <div className="p-6">
                  <h3 className="font-semibold text-gray-800 mb-2">멍멍이 {item}호</h3>
                  <p className="text-sm text-gray-600 mb-4">3살 • 수컷 • 중형견</p>
                  <button className="w-full bg-orange-500 text-white py-2 rounded-lg hover:bg-orange-600 transition-colors">
                    상세보기
                  </button>
                </div>
              </div>
            ))}
          </div>
          
          <div className="text-center mt-12">
            <button className="bg-orange-500 text-white px-8 py-4 rounded-lg text-lg font-semibold hover:bg-orange-600 transition-all">
              더 많은 동물 보기
            </button>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-gradient-to-r from-orange-500 to-yellow-500">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-3xl md:text-4xl font-bold text-white mb-6">
            지금 바로 입양을 시작해보세요
          </h2>
          <p className="text-xl text-orange-100 mb-8">
            작은 관심이 큰 변화를 만들어냅니다. 
            유기동물들에게 새로운 희망을 선물해주세요.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className="bg-white text-orange-500 px-8 py-4 rounded-lg text-lg font-semibold hover:bg-gray-100 transition-colors">
              입양동물 보기
            </button>
            <button className="border-2 border-white text-white px-8 py-4 rounded-lg text-lg font-semibold hover:bg-white hover:text-orange-500 transition-all">
              입양신청하기
            </button>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-800 text-white py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-4 gap-8">
            <div>
              <div className="flex items-center space-x-2 mb-4">
                <div className="w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center">
                  <span className="text-white font-bold text-sm">🐾</span>
                </div>
                <span className="text-xl font-bold">펫프렌드</span>
              </div>
              <p className="text-gray-400">
                유기동물과 입양희망자를 연결하는 
                따뜻한 플랫폼입니다.
              </p>
            </div>
            
            <div>
              <h3 className="font-semibold mb-4">서비스</h3>
              <ul className="space-y-2 text-gray-400">
                <li><Link href="/gallery" className="hover:text-white transition-colors">입양동물 보기</Link></li>
                <li><Link href="/apply" className="hover:text-white transition-colors">입양신청</Link></li>
                <li><Link href="/profile" className="hover:text-white transition-colors">내정보</Link></li>
              </ul>
            </div>
            
            <div>
              <h3 className="font-semibold mb-4">지원</h3>
              <ul className="space-y-2 text-gray-400">
                <li><Link href="/help" className="hover:text-white transition-colors">도움말</Link></li>
                <li><Link href="/contact" className="hover:text-white transition-colors">문의하기</Link></li>
                <li><Link href="/faq" className="hover:text-white transition-colors">자주묻는질문</Link></li>
              </ul>
            </div>
            
            <div>
              <h3 className="font-semibold mb-4">연락처</h3>
              <ul className="space-y-2 text-gray-400">
                <li>📧 info@petfriend.com</li>
                <li>📞 02-1234-5678</li>
                <li>📍 서울시 강남구 테헤란로 123</li>
              </ul>
            </div>
          </div>
          
          <div className="border-t border-gray-700 mt-8 pt-8 text-center text-gray-400">
            <p>&copy; 2024 펫프렌드. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
