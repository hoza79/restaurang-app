import { useState, useEffect } from 'react'
import { getBookings, addBooking, deleteBooking, updateBooking, getTables } from '../../api/menuApi'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return d.toLocaleString('sv-SE', { dateStyle: 'short', timeStyle: 'short' })
}

function BookingAdmin() {
  const [bookingList, setBookingList] = useState([])
  const [tables, setTables] = useState([])
  const [loading, setLoading] = useState(true)
  const [showAdd, setShowAdd] = useState(false)
  const [newBooking, setNewBooking] = useState({ firstName: '', lastName: '', phoneNumber: '', guestCount: '', date: '', tableId: '' })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const [editingId, setEditingId] = useState(null)
  const [editValues, setEditValues] = useState({})

  useEffect(() => {
    fetchBookings()
    getTables().then(setTables)
  }, [])

  function fetchBookings() {
    setLoading(true)
    getBookings()
      .then(data => { setBookingList(data); setLoading(false) })
      .catch(() => setLoading(false))
  }

  const totalGuests = bookingList.reduce((sum, b) => sum + (b.guestCount || 0), 0)

  const handleAdd = async () => {
    if (!newBooking.firstName) return
    setSaving(true)
    setError(null)
    try {
      await addBooking({
        ...newBooking,
        guestCount: parseInt(newBooking.guestCount) || 0,
        tableId: newBooking.tableId ? parseInt(newBooking.tableId) : null,
      })
      setNewBooking({ firstName: '', lastName: '', phoneNumber: '', guestCount: '', date: '', tableId: '' })
      setShowAdd(false)
      fetchBookings()
    } catch {
      setError('Kunde inte lägga till bokningen')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (id) => {
    setError(null)
    try {
      await deleteBooking(id)
      fetchBookings()
    } catch {
      setError('Kunde inte ta bort bokningen')
    }
  }

  const handleEditStart = (b) => {
    setEditingId(b.bookingId)
    setEditValues({
      firstName: b.firstName,
      lastName: b.lastName,
      phoneNumber: b.phoneNumber || '',
      guestCount: b.guestCount,
      date: b.date ? b.date.slice(0, 16) : '',
      tableId: b.tableId ?? '',
    })
  }

  const handleEditSave = async (id) => {
    setSaving(true)
    setError(null)
    try {
      await updateBooking(id, {
        ...editValues,
        guestCount: parseInt(editValues.guestCount) || 0,
        tableId: editValues.tableId ? parseInt(editValues.tableId) : null,
      })
      setEditingId(null)
      fetchBookings()
    } catch {
      setError('Kunde inte uppdatera bokningen')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <p>Laddar...</p>

  return (
    <>
      <div className="main-header">
        <h1>Kvällens <em>Bokningar</em></h1>
      </div>

      {/* statistikrutor */}
      <div className="booking-stats">
        <div className="stat-card">
          <div className="number">{bookingList.length}</div>
          <div className="label">Bokningar</div>
        </div>
        <div className="stat-card">
          <div className="number">{totalGuests}</div>
          <div className="label">Förväntade gäster</div>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Bordöversikt</h3>
          <button className="btn btn-outline btn-sm" onClick={() => setShowAdd(!showAdd)}>
            + Lägg till bokning
          </button>
        </div>

        {showAdd && (
          <div className="inline-form">
            <div className="form-row">
              <div>
                <label>Förnamn</label>
                <input type="text" placeholder="Förnamn" value={newBooking.firstName} onChange={(e) => setNewBooking({ ...newBooking, firstName: e.target.value })} />
              </div>
              <div>
                <label>Efternamn</label>
                <input type="text" placeholder="Efternamn" value={newBooking.lastName} onChange={(e) => setNewBooking({ ...newBooking, lastName: e.target.value })} />
              </div>
            </div>
            <div className="form-row">
              <div>
                <label>Telefon</label>
                <input type="text" placeholder="070-000 00 00" value={newBooking.phoneNumber} onChange={(e) => setNewBooking({ ...newBooking, phoneNumber: e.target.value })} />
              </div>
              <div>
                <label>Gäster</label>
                <input type="number" placeholder="2" min="1" value={newBooking.guestCount} onChange={(e) => setNewBooking({ ...newBooking, guestCount: e.target.value })} />
              </div>
            </div>
            <div className="form-row">
              <div>
                <label>Datum &amp; tid</label>
                <input type="datetime-local" value={newBooking.date} onChange={(e) => setNewBooking({ ...newBooking, date: e.target.value })} />
              </div>
            </div>
            {error && <p style={{ color: 'red', margin: '0.5rem 0' }}>{error}</p>}
            <div className="form-actions">
              <button className="btn btn-outline btn-sm" onClick={() => setShowAdd(false)}>Avbryt</button>
              <button className="btn btn-gold btn-sm" onClick={handleAdd} disabled={saving}>
                {saving ? 'Sparar...' : 'Lägg till'}
              </button>
            </div>
          </div>
        )}

        <table className="admin-table">
          <thead>
            <tr><th style={{ display: 'none' }}>Bord</th><th>Namn</th><th>Telefon</th><th>Gäster</th><th>Tid</th><th></th></tr>
          </thead>
          <tbody>
            {bookingList.map((b) => (
              <tr key={b.bookingId}>
                {editingId === b.bookingId ? (
                  <>
                    <td style={{ display: 'none' }}>
                      <select value={editValues.tableId} onChange={(e) => setEditValues({ ...editValues, tableId: e.target.value })}>
                        <option value="">Inget bord</option>
                        {tables.map(t => (
                          <option key={t.tableId} value={t.tableId}>Bord {t.tableNumber}</option>
                        ))}
                      </select>
                    </td>
                    <td>
                      <input type="text" value={editValues.firstName} onChange={(e) => setEditValues({ ...editValues, firstName: e.target.value })} style={{ width: '90px' }} />
                      {' '}
                      <input type="text" value={editValues.lastName} onChange={(e) => setEditValues({ ...editValues, lastName: e.target.value })} style={{ width: '90px' }} />
                    </td>
                    <td>
                      <input type="text" value={editValues.phoneNumber} onChange={(e) => setEditValues({ ...editValues, phoneNumber: e.target.value })} style={{ width: '120px' }} />
                    </td>
                    <td>
                      <input type="number" min="1" value={editValues.guestCount} onChange={(e) => setEditValues({ ...editValues, guestCount: e.target.value })} style={{ width: '60px' }} />
                    </td>
                    <td>
                      <input type="datetime-local" value={editValues.date} onChange={(e) => setEditValues({ ...editValues, date: e.target.value })} style={{ width: '180px' }} />
                    </td>
                    <td className="actions">
                      <button className="btn btn-outline btn-sm" onClick={() => setEditingId(null)}>Avbryt</button>
                      <button className="btn btn-gold btn-sm" onClick={() => handleEditSave(b.bookingId)} disabled={saving}>
                        {saving ? '...' : 'Spara'}
                      </button>
                    </td>
                  </>
                ) : (
                  <>
                    <td style={{ display: 'none' }}>{b.tableNumber ? `Bord ${b.tableNumber}` : '-'}</td>
                    <td>{b.firstName} {b.lastName}</td>
                    <td>{b.phoneNumber || '-'}</td>
                    <td>{b.guestCount}</td>
                    <td>{formatDateTime(b.date)}</td>
                    <td className="actions">
                      <button className="btn btn-outline btn-sm" onClick={() => handleEditStart(b)}>Redigera</button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(b.bookingId)}>Ta bort</button>
                    </td>
                  </>
                )}
              </tr>
            ))}
          </tbody>
        </table>

        {error && <p style={{ padding: '1rem', color: 'red' }}>{error}</p>}
      </div>
    </>
  )
}

export default BookingAdmin
