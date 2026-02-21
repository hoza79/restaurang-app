import { useState } from 'react'
import { bookings as initialBookings } from '../../data/mockData'

function BookingAdmin() {
  const [bookingList] = useState(initialBookings)

  const bookedCount = bookingList.filter(b => b.status === 'booked').length
  const freeCount = bookingList.filter(b => b.status === 'free').length
  const totalGuests = bookingList.reduce((sum, b) => sum + b.guests, 0)

  return (
    <>
      <div className="main-header">
        <h1>Kvällens <em>Bokningar</em></h1>
      </div>

      <div className="booking-stats">
        <div className="stat-card">
          <div className="number">{bookedCount}</div>
          <div className="label">Bokade bord</div>
        </div>
        <div className="stat-card">
          <div className="number">{freeCount}</div>
          <div className="label">Lediga bord</div>
        </div>
        <div className="stat-card">
          <div className="number">{totalGuests}</div>
          <div className="label">Förväntade gäster</div>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Bordöversikt - Ikväll</h3>
        </div>
        <table className="admin-table">
          <thead>
            <tr><th>Bord</th><th>Status</th><th>Namn</th><th>Gäster</th><th>Tid</th></tr>
          </thead>
          <tbody>
            {bookingList.map((b) => (
              <tr key={b.id}>
                <td>Bord {b.table}</td>
                <td>
                  <span className={`table-status status-${b.status}`}>
                    {b.status === 'booked' ? 'Bokad' : b.status === 'free' ? 'Ledig' : 'Ej tillgänglig'}
                  </span>
                </td>
                <td>{b.name || '-'}</td>
                <td>{b.guests || '-'}</td>
                <td>{b.time || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}

export default BookingAdmin
