import { useState, useEffect } from 'react'
import { getLunchMenu, addLunchItem } from '../../api/menuApi'

function LunchAdmin() {
  const [days, setDays] = useState([])
  const [loading, setLoading] = useState(true)
  const [showAddForm, setShowAddForm] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const [newItem, setNewItem] = useState({ name: '', description: '', price: '', availableDate: '' })

  // hämtar menyn när sidan laddas
  useEffect(() => {
    fetchMenu()
  }, [])

  function fetchMenu() {
    setLoading(true)
    getLunchMenu()
      .then(data => {
        setDays(data.days)
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }

  if (loading) return <p>Laddar...</p>

  // skickar ny rätt till backend och laddar om menyn
  const handleAdd = async () => {
    if (!newItem.name || !newItem.availableDate) return
    setSaving(true)
    setError(null)
    try {
      await addLunchItem(newItem)
      setNewItem({ name: '', description: '', price: '', availableDate: '' })
      setShowAddForm(false)
      fetchMenu()
    } catch (e) {
      setError('Kunde inte spara rätten, försök igen')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = (dayNum, itemId) => {
    // tar bort lokalt, inget delete-API finns ännu
    setDays(prev =>
      prev
        .map(d =>
          d.dayNum === dayNum ? { ...d, items: d.items.filter(i => i.id !== itemId) } : d
        )
        .filter(d => d.items.length > 0)
    )
  }

  return (
    <>
      <div className="main-header">
        <h1><em>Lunchmeny</em></h1>
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Lunchrätter</h3>
          <button className="btn btn-outline btn-sm" onClick={() => setShowAddForm(!showAddForm)}>
            + Lägg till rätt
          </button>
        </div>

        {showAddForm && (
          <div className="inline-form">
            <div className="form-row-3">
              <div>
                <label>Datum</label>
                <input
                  type="date"
                  value={newItem.availableDate}
                  onChange={(e) => setNewItem({ ...newItem, availableDate: e.target.value })}
                />
              </div>
              <div>
                <label>Namn</label>
                <input
                  type="text"
                  placeholder="Rättens namn"
                  value={newItem.name}
                  onChange={(e) => setNewItem({ ...newItem, name: e.target.value })}
                />
              </div>
              <div>
                <label>Pris</label>
                <input
                  type="text"
                  placeholder="135"
                  value={newItem.price}
                  onChange={(e) => setNewItem({ ...newItem, price: e.target.value })}
                />
              </div>
            </div>
            <div style={{ marginTop: '0.5rem' }}>
              <label>Beskrivning</label>
              <input
                type="text"
                placeholder="Kort beskrivning"
                value={newItem.description}
                onChange={(e) => setNewItem({ ...newItem, description: e.target.value })}
                style={{ width: '100%' }}
              />
            </div>
            {error && <p style={{ color: 'red', margin: '0.5rem 0' }}>{error}</p>}
            <div className="form-actions">
              <button className="btn btn-outline btn-sm" onClick={() => setShowAddForm(false)}>Avbryt</button>
              <button className="btn btn-gold btn-sm" onClick={handleAdd} disabled={saving}>
                {saving ? 'Sparar...' : 'Lägg till'}
              </button>
            </div>
          </div>
        )}

        {days.length === 0 && <p style={{ padding: '1.5rem' }}>Inga lunchrätter hittades.</p>}

        {days.map(day => (
          <div key={day.dayNum}>
            <h4 style={{ padding: '1.5rem 1.5rem 0.5rem', margin: 0, borderTop: '1px solid rgba(58,51,44,0.10)' }}>
              {day.label}
            </h4>
            <table className="admin-table">
              <thead>
                <tr><th>Nr</th><th>Rätt</th><th>Beskrivning</th><th>Pris</th><th></th></tr>
              </thead>
              <tbody>
                {day.items.map((item, i) => (
                  <tr key={item.id}>
                    <td>{String(i + 1).padStart(2, '0')}</td>
                    <td>{item.name}</td>
                    <td>{item.description}</td>
                    <td>{item.price} kr</td>
                    <td className="actions">
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(day.dayNum, item.id)}>Ta bort</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ))}
      </div>
    </>
  )
}

export default LunchAdmin
