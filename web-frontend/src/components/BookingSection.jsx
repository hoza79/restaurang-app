import { useState } from 'react'
import { addBooking } from '../api/menuApi'

function BookingSection() {
  const [form, setForm] = useState({
    name: '', phone: '', date: '', time: '18:00', guests: '2 personer'
  })
  const [submitted, setSubmitted] = useState(false)
  const [error, setError] = useState(null)

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    const guestCount = parseInt(form.guests) || 2
    const dateTime = form.date && form.time ? `${form.date}T${form.time}:00` : form.date
    const nameParts = form.name.trim().split(' ')
    const firstName = nameParts[0] || ''
    const lastName = nameParts.slice(1).join(' ') || ''
    try {
      await addBooking({ firstName, lastName, phoneNumber: form.phone, guestCount, date: dateTime })
      setSubmitted(true)
      setForm({ name: '', phone: '', date: '', time: '18:00', guests: '2 personer', message: '' })
    } catch {
      setError('Något gick fel, försök igen.')
    }
  }

  return (
    <section className="booking" id="boka">
      <div className="container">
        <p className="section-label">Reservera</p>
        <h2 className="section-title">Boka <em>Bord</em></h2>
        <div className="gold-line"></div>
        <p className="section-intro">Säkra din plats, speciellt på kvällar med livemusik.</p>

        <div className="booking-layout">
          <div className="booking-left">
            <h3>Praktisk information</h3>
            <p>
              Lunch serveras drop-in, men vi rekommenderar bokning för kvällens à la carte.
              Större sällskap ber vi kontakta oss direkt.
            </p>
            <div className="info-item">
              <span className="label">Lunch</span>
              <span className="value">Mån-Fre 11:00-14:00</span>
            </div>
            <div className="info-item">
              <span className="label">Middag</span>
              <span className="value">Mån-Lör 17:00-22:00</span>
            </div>
            <div className="info-item">
              <span className="label">Telefon</span>
              <span className="value">08-123 456 78</span>
            </div>
            <div className="info-item">
              <span className="label">E-post</span>
              <span className="value">boka@antonsskafferi.se</span>
            </div>
            <div className="info-item">
              <span className="label">Adress</span>
              <span className="value">Storgatan 12, Sundsvall</span>
            </div>
          </div>

          <form className="booking-form" onSubmit={handleSubmit}>
            <h3>Reservera online</h3>
            <div className="form-row">
              <div className="form-group">
                <label>Namn</label>
                <input type="text" name="name" placeholder="Ditt namn" value={form.name} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>Telefon</label>
                <input type="tel" name="phone" placeholder="070-123 45 67" value={form.phone} onChange={handleChange} />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Datum</label>
                <input type="date" name="date" value={form.date} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>Tid</label>
                <select name="time" value={form.time} onChange={handleChange}>
                  <option>17:00</option>
                  <option>17:30</option>
                  <option>18:00</option>
                  <option>18:30</option>
                  <option>19:00</option>
                  <option>19:30</option>
                  <option>20:00</option>
                  <option>20:30</option>
                </select>
              </div>
            </div>
            <div className="form-group">
              <label>Antal gäster</label>
              <select name="guests" value={form.guests} onChange={handleChange}>
                <option>1 person</option>
                <option>2 personer</option>
                <option>3 personer</option>
                <option>4 personer</option>
                <option>5 personer</option>
                <option>6+ personer</option>
              </select>
            </div>

            {error && <p style={{ color: 'red', marginBottom: '0.5rem' }}>{error}</p>}
            {submitted && <p style={{ color: 'green', marginBottom: '0.5rem' }}>Tack för din bokning!</p>}
            <button type="submit" className="btn-book">Reservera</button>
          </form>
        </div>
      </div>
    </section>
  )
}

export default BookingSection
