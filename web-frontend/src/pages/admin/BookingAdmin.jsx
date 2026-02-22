import { useState } from 'react'
import { bookings as initialBookings } from '../../data/mockData'

const STATUS_LABELS = {
  booked: 'Bokad',
  free: 'Ledig',
  unavailable: 'Ej tillgänglig',
}

function BookingAdmin() {
  const [bookingList, setBookingList] = useState(initialBookings)
  const [showAdd, setShowAdd] = useState(false)
  const [newBooking, setNewBooking] = useState({ table: '', name: '', guests: '', time: '', status: 'booked' })
  const [editingId, setEditingId] = useState(null)
  const [editValues, setEditValues] = useState({})

  const bookedCount = bookingList.filter(b => b.status === 'booked').length
  const freeCount = bookingList.filter(b => b.status === 'free').length
  const totalGuests = bookingList.reduce((sum, b) => sum + b.guests, 0)

  const handleAdd = () => {
    if (!newBooking.table) return
    setBookingList([
      ...bookingList,
      {
        id: Date.now(),
        table: parseInt(newBooking.table) || 0,
        name: newBooking.name,
        guests: parseInt(newBooking.guests) || 0,
        time: newBooking.time,
        status: newBooking.status,
      }
    ])
    setNewBooking({ table: '', name: '', guests: '', time: '', status: 'booked' })
    setShowAdd(false)
  }

  const handleDelete = (id) => {
    setBookingList(bookingList.filter(b => b.id !== id))
  }

  const handleEditStart = (booking) => {
    setEditingId(booking.id)
    setEditValues({
      name: booking.name,
      guests: booking.guests,
      time: booking.time,
      status: booking.status,
    })
  }

  const handleEditSave = (id) => {
    setBookingList(bookingList.map(b =>
      b.id === id
        ? { ...b, name: editValues.name, guests: parseInt(editValues.guests) || 0, time: editValues.time, status: editValues.status }
        : b
    ))
    setEditingId(null)
  }

  return (
    <>
      <div className="main-header">
        <h1>Kvällens <em>Bokningar</em></h1>
      </div>

      {/* statistikrutor */}
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
          <button className="btn btn-outline btn-sm" onClick={() => setShowAdd(!showAdd)}>
            + Lägg till bokning
          </button>
        </div>

        {showAdd && (
          <div className="inline-form">
            <div className="form-row">
              <div>
                <label>Bordsnr</label>
                <input
                  type="number"
                  placeholder="11"
                  value={newBooking.table}
                  onChange={(e) => setNewBooking({ ...newBooking, table: e.target.value })}
                />
              </div>
              <div>
                <label>Status</label>
                <select
                  value={newBooking.status}
                  onChange={(e) => setNewBooking({ ...newBooking, status: e.target.value })}
                >
                  <option value="booked">Bokad</option>
                  <option value="free">Ledig</option>
                  <option value="unavailable">Ej tillgänglig</option>
                </select>
              </div>
            </div>
            <div className="form-row">
              <div>
                <label>Namn</label>
                <input
                  type="text"
                  placeholder="Efternamn"
                  value={newBooking.name}
                  onChange={(e) => setNewBooking({ ...newBooking, name: e.target.value })}
                />
              </div>
              <div>
                <label>Gäster</label>
                <input
                  type="number"
                  placeholder="2"
                  value={newBooking.guests}
                  onChange={(e) => setNewBooking({ ...newBooking, guests: e.target.value })}
                />
              </div>
            </div>
            <div style={{ marginBottom: '1rem' }}>
              <label>Tid</label>
              <input
                type="text"
                placeholder="19:00"
                value={newBooking.time}
                onChange={(e) => setNewBooking({ ...newBooking, time: e.target.value })}
              />
            </div>
            <div className="form-actions">
              <button className="btn btn-outline btn-sm" onClick={() => setShowAdd(false)}>Avbryt</button>
              <button className="btn btn-gold btn-sm" onClick={handleAdd}>Lägg till</button>
            </div>
          </div>
        )}

        <table className="admin-table">
          <thead>
            <tr><th>Bord</th><th>Status</th><th>Namn</th><th>Gäster</th><th>Tid</th><th></th></tr>
          </thead>
          <tbody>
            {bookingList.map((b) => (
              <tr key={b.id}>
                {editingId === b.id ? (
                  // redigeringsläge
                  <>
                    <td>Bord {b.table}</td>
                    <td>
                      <select
                        value={editValues.status}
                        onChange={(e) => setEditValues({ ...editValues, status: e.target.value })}
                      >
                        <option value="booked">Bokad</option>
                        <option value="free">Ledig</option>
                        <option value="unavailable">Ej tillgänglig</option>
                      </select>
                    </td>
                    <td>
                      <input
                        type="text"
                        value={editValues.name}
                        onChange={(e) => setEditValues({ ...editValues, name: e.target.value })}
                      />
                    </td>
                    <td>
                      <input
                        type="number"
                        value={editValues.guests}
                        onChange={(e) => setEditValues({ ...editValues, guests: e.target.value })}
                        style={{ width: '60px' }}
                      />
                    </td>
                    <td>
                      <input
                        type="text"
                        value={editValues.time}
                        onChange={(e) => setEditValues({ ...editValues, time: e.target.value })}
                        style={{ width: '80px' }}
                      />
                    </td>
                    <td className="actions">
                      <button className="btn btn-outline btn-sm" onClick={() => setEditingId(null)}>Avbryt</button>
                      <button className="btn btn-gold btn-sm" onClick={() => handleEditSave(b.id)}>Spara</button>
                    </td>
                  </>
                ) : (
                  // normalläge
                  <>
                    <td>Bord {b.table}</td>
                    <td>
                      <span className={`table-status status-${b.status}`}>
                        {STATUS_LABELS[b.status] || b.status}
                      </span>
                    </td>
                    <td>{b.name || '-'}</td>
                    <td>{b.guests || '-'}</td>
                    <td>{b.time || '-'}</td>
                    <td className="actions">
                      <button className="btn btn-outline btn-sm" onClick={() => handleEditStart(b)}>Redigera</button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(b.id)}>Ta bort</button>
                    </td>
                  </>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}

export default BookingAdmin
