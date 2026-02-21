import { useState, useEffect } from 'react'
import { getLunchMenu } from '../../api/menuApi'

const DAY_NAMES = ['Måndag', 'Tisdag', 'Onsdag', 'Torsdag', 'Fredag']

function LunchAdmin() {
  const [days, setDays] = useState([])
  const [loading, setLoading] = useState(true)
  const [showAddForm, setShowAddForm] = useState(false)
  const [newItem, setNewItem] = useState({ name: '', description: '', price: '', dayNum: 1 })

  useEffect(() => {
    getLunchMenu()
      .then(data => {
        setDays(data.days)
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }, [])

  if (loading) return <p>Laddar...</p>

  const handleAdd = () => {
    if (!newItem.name) return
    const dayNum = Number(newItem.dayNum)
    const item = {
      id: Date.now(),
      name: newItem.name,
      description: newItem.description,
      price: parseInt(newItem.price) || 0,
      available: true,
    }

    setDays(prev => {
      const existing = prev.find(d => d.dayNum === dayNum)
      if (existing) {
        return prev.map(d =>
          d.dayNum === dayNum ? { ...d, items: [...d.items, item] } : d
        )
      }
      return [...prev, { dayNum, label: DAY_NAMES[dayNum - 1], items: [item] }].sort(
        (a, b) => a.dayNum - b.dayNum
      )
    })
    setNewItem({ name: '', description: '', price: '', dayNum: 1 })
    setShowAddForm(false)
  }

  const handleDelete = (dayNum, itemId) => {
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
        <button className="btn btn-gold" onClick={() => alert('Sparad! (mockup)')}>
          Spara alla ändringar
        </button>
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
                <label>Dag</label>
                <select
                  value={newItem.dayNum}
                  onChange={(e) => setNewItem({ ...newItem, dayNum: e.target.value })}
                >
                  {DAY_NAMES.map((name, i) => (
                    <option key={i + 1} value={i + 1}>{name}</option>
                  ))}
                </select>
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
            <div className="form-actions">
              <button className="btn btn-outline btn-sm" onClick={() => setShowAddForm(false)}>Avbryt</button>
              <button className="btn btn-gold btn-sm" onClick={handleAdd}>Lägg till</button>
            </div>
          </div>
        )}

        {days.length === 0 && <p style={{ padding: '1.5rem' }}>Inga lunchrätter hittades.</p>}

        {days.map(day => (
          <div key={day.dayNum}>
            <h4 style={{ padding: '1.5rem 1.5rem 0.5rem', margin: 0, borderTop: '1px solid rgba(255,255,255,0.06)' }}>
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
                      <button className="btn btn-outline btn-sm">Redigera</button>
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
