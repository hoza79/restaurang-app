import { Routes, Route } from 'react-router-dom'
import HomePage from './pages/HomePage'
import LoginPage from './pages/admin/LoginPage'
import AdminLayout from './pages/admin/AdminLayout'
import LunchAdmin from './pages/admin/LunchAdmin'
import CarteAdmin from './pages/admin/CarteAdmin'
import MusicAdmin from './pages/admin/MusicAdmin'
import BookingAdmin from './pages/admin/BookingAdmin'

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/admin" element={<LoginPage />} />
      <Route path="/admin" element={<AdminLayout />}>
        <Route path="lunch" element={<LunchAdmin />} />
        <Route path="carte" element={<CarteAdmin />} />
        <Route path="musik" element={<MusicAdmin />} />
        <Route path="bokningar" element={<BookingAdmin />} />
      </Route>
    </Routes>
  )
}

export default App
