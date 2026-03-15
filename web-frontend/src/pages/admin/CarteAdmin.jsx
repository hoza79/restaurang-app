import { useState, useEffect } from 'react'
import { getCarteMenu, addCarteItem, deleteCarteItem, updateCarteItem } from '../../api/menuApi'

function CarteAdmin() {
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [addingTo, setAddingTo] = useState(null)
  const [newItem, setNewItem] = useState({ name: '', description: '', price: '', beef: false })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  // håller reda på vilken rad som redigeras just nu
  const [editingId, setEditingId] = useState(null)
  const [editValues, setEditValues] = useState({ name: '', description: '', price: '', options: false })

  useEffect(() => {
    fetchMenu()
  }, [])

  function fetchMenu() {
    setLoading(true)
    getCarteMenu()
      .then(data => {
        setCategories(data)
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }

  const handleAdd = async (catIndex) => {
    if (!newItem.name) return
    setSaving(true)
    setError(null)
    try {
      await addCarteItem(categories[catIndex].category, {
        name: newItem.name,
        description: newItem.description,
        price: newItem.price,
        options: newItem.beef,
      })
      setNewItem({ name: '', description: '', price: '', beef: false })
      setAddingTo(null)
      fetchMenu()
    } catch {
      setError('Kunde inte lägga till rätten')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (itemId) => {
    setError(null)
    try {
      await deleteCarteItem(itemId)
      fetchMenu()
    } catch {
      setError('Kunde inte ta bort rätten')
    }
  }

  const handleEditStart = (item) => {
    setEditingId(item.id)
    setEditValues({ name: item.name, description: item.description, price: item.price, options: item.options ?? false })
  }

  // uppdaterar rätten via API
  const handleEditSave = async (itemId) => {
    setSaving(true)
    setError(null)
    try {
      await updateCarteItem(itemId, editValues)
      setEditingId(null)
      fetchMenu()
    } catch {
      setError('Kunde inte uppdatera rätten')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <p>Laddar...</p>

  return (
    <>
      <div className="main-header">
        <h1>À la <em>Carte</em></h1>
      </div>

      {error && <p style={{ padding: '1rem', color: 'red' }}>{error}</p>}

      {categories.map((cat, catIndex) => (
        <div className="card" key={cat.category}>
          <div className="card-header">
            <h3>{cat.category}</h3>
            <button
              className="btn btn-outline btn-sm"
              onClick={() => { setAddingTo(addingTo === catIndex ? null : catIndex); setNewItem({ name: '', description: '', price: '', beef: false }) }}
            >
              + Lägg till
            </button>
          </div>

          {addingTo === catIndex && (
            <div className="inline-form">
              <div className="form-row-3">
                <div>
                  <label>Namn</label>
                  <input type="text" placeholder="Rättens namn" value={newItem.name} onChange={(e) => setNewItem({ ...newItem, name: e.target.value })} />
                </div>
                <div>
                  <label>Beskrivning</label>
                  <input type="text" placeholder="Kort beskrivning" value={newItem.description} onChange={(e) => setNewItem({ ...newItem, description: e.target.value })} />
                </div>
                <div>
                  <label>Pris</label>
                  <input type="text" placeholder="145" value={newItem.price} onChange={(e) => setNewItem({ ...newItem, price: e.target.value })} />
                </div>
              </div>
              {!['Efterrätt', 'Dryck'].includes(cat.category) && (
                <div style={{ marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                  <label style={{ margin: 0 }}>Nötkött</label>
                  <button
                    type="button"
                    className={`btn btn-sm ${newItem.beef ? 'btn-gold' : 'btn-outline'}`}
                    onClick={() => setNewItem({ ...newItem, beef: !newItem.beef })}
                  >
                    {newItem.beef ? 'Ja' : 'Nej'}
                  </button>
                </div>
              )}
              <div className="form-actions">
                <button className="btn btn-outline btn-sm" onClick={() => setAddingTo(null)}>Avbryt</button>
                <button className="btn btn-gold btn-sm" onClick={() => handleAdd(catIndex)} disabled={saving}>
                  {saving ? 'Sparar...' : 'Lägg till'}
                </button>
              </div>
            </div>
          )}

          <table className="admin-table">
            <thead>
              <tr><th>Rätt</th><th>Beskrivning</th><th>Pris</th>{!['Efterrätt', 'Dryck'].includes(cat.category) && <th>Nötkött</th>}<th></th></tr>
            </thead>
            <tbody>
              {cat.items.map((item) => (
                <tr key={item.id}>
                  {editingId === item.id ? (
                    // redigeringsläge
                    <>
                      <td><input type="text" value={editValues.name} onChange={(e) => setEditValues({ ...editValues, name: e.target.value })} /></td>
                      <td><input type="text" value={editValues.description} onChange={(e) => setEditValues({ ...editValues, description: e.target.value })} /></td>
                      <td><input type="text" value={editValues.price} onChange={(e) => setEditValues({ ...editValues, price: e.target.value })} style={{ width: '70px' }} /></td>
                      {!['Efterrätt', 'Dryck'].includes(cat.category) && (
                        <td>
                          <button
                            type="button"
                            className={`btn btn-sm ${editValues.options ? 'btn-gold' : 'btn-outline'}`}
                            onClick={() => setEditValues({ ...editValues, options: !editValues.options })}
                          >
                            {editValues.options ? 'Ja' : 'Nej'}
                          </button>
                        </td>
                      )}
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
                      <td>{item.name}</td>
                      <td>{item.description}</td>
                      <td>{item.price} kr</td>
                      {!['Efterrätt', 'Dryck'].includes(cat.category) && <td>{item.options ? 'Ja' : 'Nej'}</td>}
                      <td className="actions">
                        <button className="btn btn-outline btn-sm" onClick={() => handleEditStart(item)}>Redigera</button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(item.id)}>Ta bort</button>
                      </td>
                    </>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}
    </>
  )
}

export default CarteAdmin
