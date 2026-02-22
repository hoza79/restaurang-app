import { useState } from 'react'
import { carteMenu } from '../../data/mockData'

function CarteAdmin() {
  const [categories, setCategories] = useState(carteMenu)
  const [addingTo, setAddingTo] = useState(null)
  const [newItem, setNewItem] = useState({ name: '', description: '', price: '' })
  // håller reda på vilken rad som redigeras just nu
  const [editingId, setEditingId] = useState(null)
  const [editValues, setEditValues] = useState({ name: '', description: '', price: '' })

  const handleAdd = (catIndex) => {
    if (!newItem.name) return
    const updated = [...categories]
    updated[catIndex] = {
      ...updated[catIndex],
      items: [
        ...updated[catIndex].items,
        { id: Date.now(), name: newItem.name, description: newItem.description, price: parseInt(newItem.price) || 0 }
      ]
    }
    setCategories(updated)
    setNewItem({ name: '', description: '', price: '' })
    setAddingTo(null)
  }

  const handleDelete = (catIndex, itemId) => {
    const updated = [...categories]
    updated[catIndex] = {
      ...updated[catIndex],
      items: updated[catIndex].items.filter(i => i.id !== itemId)
    }
    setCategories(updated)
  }

  // startar redigering av en rad
  const handleEditStart = (item) => {
    setEditingId(item.id)
    setEditValues({ name: item.name, description: item.description, price: item.price })
  }

  // sparar ändringarna i raden
  const handleEditSave = (catIndex, itemId) => {
    const updated = [...categories]
    updated[catIndex] = {
      ...updated[catIndex],
      items: updated[catIndex].items.map(i =>
        i.id === itemId
          ? { ...i, name: editValues.name, description: editValues.description, price: parseInt(editValues.price) || 0 }
          : i
      )
    }
    setCategories(updated)
    setEditingId(null)
  }

  return (
    <>
      <div className="main-header">
        <h1>À la <em>Carte</em></h1>
      </div>

      {categories.map((cat, catIndex) => (
        <div className="card" key={cat.category}>
          <div className="card-header">
            <h3>{cat.category}</h3>
            <button
              className="btn btn-outline btn-sm"
              onClick={() => { setAddingTo(addingTo === catIndex ? null : catIndex); setNewItem({ name: '', description: '', price: '' }) }}
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
              <div className="form-actions">
                <button className="btn btn-outline btn-sm" onClick={() => setAddingTo(null)}>Avbryt</button>
                <button className="btn btn-gold btn-sm" onClick={() => handleAdd(catIndex)}>Lägg till</button>
              </div>
            </div>
          )}

          <table className="admin-table">
            <thead>
              <tr><th>Rätt</th><th>Beskrivning</th><th>Pris</th><th></th></tr>
            </thead>
            <tbody>
              {cat.items.map((item) => (
                <tr key={item.id}>
                  {editingId === item.id ? (
                    // redigeringsläge - visar inputfält istället för text
                    <>
                      <td><input type="text" value={editValues.name} onChange={(e) => setEditValues({ ...editValues, name: e.target.value })} /></td>
                      <td><input type="text" value={editValues.description} onChange={(e) => setEditValues({ ...editValues, description: e.target.value })} /></td>
                      <td><input type="text" value={editValues.price} onChange={(e) => setEditValues({ ...editValues, price: e.target.value })} style={{ width: '70px' }} /></td>
                      <td className="actions">
                        <button className="btn btn-outline btn-sm" onClick={() => setEditingId(null)}>Avbryt</button>
                        <button className="btn btn-gold btn-sm" onClick={() => handleEditSave(catIndex, item.id)}>Spara</button>
                      </td>
                    </>
                  ) : (
                    // normalläge
                    <>
                      <td>{item.name}</td>
                      <td>{item.description}</td>
                      <td>{item.price} kr</td>
                      <td className="actions">
                        <button className="btn btn-outline btn-sm" onClick={() => handleEditStart(item)}>Redigera</button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(catIndex, item.id)}>Ta bort</button>
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
