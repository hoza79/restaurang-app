import Navbar from '../components/Navbar'
import Hero from '../components/Hero'
import LunchSection from '../components/LunchSection'
import CarteSection from '../components/CarteSection'
import MusicSection from '../components/MusicSection'
import BookingSection from '../components/BookingSection'
import Footer from '../components/Footer'
import '../styles/public.css'

function HomePage() {
  return (
    <>
      <Navbar />
      <Hero />
      <LunchSection />
      <CarteSection />
      <MusicSection />
      <BookingSection />
      <Footer />
    </>
  )
}

export default HomePage
