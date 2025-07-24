import Header from '../components/Header';
import HeroSection from '../components/HeroSection';
import StatsSection from '../components/StatsSection';
import ServicesSection from '../components/ServicesSection';
import GalleryPreview from '../components/GalleryPreview';
import CTASection from '../components/CTASection';
import Footer from '../components/Footer';

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 to-yellow-50">
      <Header />
      <HeroSection />
      <StatsSection />
      <ServicesSection />
      <GalleryPreview />
      <CTASection />
      <Footer />
    </div>
  );
}
