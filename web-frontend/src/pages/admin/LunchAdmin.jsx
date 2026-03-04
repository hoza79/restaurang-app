import { useState, useEffect } from 'react'
import { getLunchMenu, addLunchItem, deleteLunchItem, updateLunchItem } from '../../api/menuApi'

function LunchAdmin() {
  const [days, setDays] = useState([])
  const [loading, setLoading] = useState(true)
  const [showAddForm, setShowAddForm] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const [newItem, setNewItem] = useState({ name: '', description: '', price: '', availableDate: '' })
  // håller reda på vilken rad som redigeras
  const [editingId, setEditingId] = useState(null)
  const [editValues, setEditValues] = useState({})

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
    } catch {
      setError('Kunde inte spara rätten, försök igen')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (itemId) => {
    try {
      await deleteLunchItem(itemId)
      fetchMenu()
    } catch {
      setError('Kunde inte ta bort rätten')
    }
  }

  const handleEditStart = (item, day) => {
    setEditingId(item.id)
    setEditValues({
      name: item.name,
      description: item.description,
      price: item.price,
      availableDate: day.date || '',
    })
  }

  const handleEditSave = async (itemId) => {
    setSaving(true)
    setError(null)
    try {
      await updateLunchItem(itemId, editValues)
      setEditingId(null)
      fetchMenu()
    } catch {
      setError('Kunde inte uppdatera rätten')
    } finally {
      setSaving(false)
    }
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

        {days.length > 0 && (
          <table className="admin-table">
            <thead>
              <tr><th>Nr</th><th>Rätt</th><th>Beskrivning</th><th>Pris</th><th></th></tr>
            </thead>
            <tbody>
              {days.map(day => (
                <>
                  {/* dagnamn som separatorrad */}
                  <tr key={`day-${day.dayNum}`}>
                    <td colSpan={5} style={{ padding: '1rem 1.5rem 0.4rem', fontWeight: 600, borderTop: '1px solid rgba(58,51,44,0.10)', background: 'transparent' }}>
                      {day.label}
                    </td>
                  </tr>
                  {day.items.map((item, i) => (
                    <tr key={item.id}>
                      {editingId === item.id ? (
                        // redigeringsläge
                        <>
                          <td>{String(i + 1).padStart(2, '0')}</td>
                          <td>
                            <input
                              type="text"
                              value={editValues.name}
                              onChange={(e) => setEditValues({ ...editValues, name: e.target.value })}
                            />
                          </td>
                          <td>
                            <input
                              type="text"
                              value={editValues.description}
                              onChange={(e) => setEditValues({ ...editValues, description: e.target.value })}
                            />
                          </td>
                          <td>
                            <input
                              type="text"
                              value={editValues.price}
                              onChange={(e) => setEditValues({ ...editValues, price: e.target.value })}
                              style={{ width: '70px' }}
                            />
                          </td>
                          <td className="actions">
                            <button className="btn btn-outline btn-sm" onClick={() => setEditingId(null)}>Avbryt</button>
                            <button className="btn btn-gold btn-sm" onClick={() => handleEditSave(item.id)} disabled={saving}>
                              {saving ? '...' : 'Spara'}
                            </button>
                          </td>
                        </>
                      ) : (
                        // normalläge
                        <>
                          <td>{String(i + 1).padStart(2, '0')}</td>
                          <td>{item.name}</td>
                          <td>{item.description}</td>
                          <td>{item.price} kr</td>
                          <td className="actions">
                            <button className="btn btn-outline btn-sm" onClick={() => handleEditStart(item, day)}>Redigera</button>
                            <button className="btn btn-danger btn-sm" onClick={() => handleDelete(item.id)}>Ta bort</button>
                          </td>
                        </>
                      )}
                    </tr>
                  ))}
                </>
              ))}
            </tbody>
          </table>
        )}

        {error && <p style={{ padding: '1rem', color: 'red' }}>{error}</p>}
      </div>
    </>
  )
}

export default LunchAdmin
