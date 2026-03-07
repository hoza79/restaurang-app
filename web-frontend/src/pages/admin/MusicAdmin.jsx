import { useState, useEffect } from 'react'
import { getMusicEvents, addMusicEvent, deleteMusicEvent, updateMusicEvent } from '../../api/menuApi'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return d.toLocaleString('sv-SE', { dateStyle: 'short', timeStyle: 'short' })
}

function MusicAdmin() {
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [showAdd, setShowAdd] = useState(false)
  const [newEvent, setNewEvent] = useState({ title: '', description: '', date: '', imgPath: '' })
  const [imagePreview, setImagePreview] = useState(null)
  const [editPreview, setEditPreview] = useState(null)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const [editingId, setEditingId] = useState(null)
  const [editValues, setEditValues] = useState({})

  useEffect(() => { fetchEvents() }, [])

  function fetchEvents() {
    setLoading(true)
    getMusicEvents()
      .then(data => { setEvents(data); setLoading(false) })
      .catch(() => setLoading(false))
  }

  const handleAdd = async () => {
    if (!newEvent.title) return
    setSaving(true)
    setError(null)
    try {
      await addMusicEvent(newEvent)
      setNewEvent({ title: '', description: '', date: '', imgPath: '' })
      setImagePreview(null)
      setShowAdd(false)
      fetchEvents()
    } catch {
      setError('Kunde inte lägga till evenemanget')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (id) => {
    setError(null)
    try {
      await deleteMusicEvent(id)
      fetchEvents()
    } catch {
      setError('Kunde inte ta bort evenemanget')
    }
  }

  const handleEditStart = (event) => {
    setEditingId(event.id)
    setEditPreview(event.imgPath || null)
    setEditValues({
      title: event.title,
      description: event.description || '',
      date: event.date ? event.date.slice(0, 16) : '',
      imgPath: event.imgPath || '',
    })
  }

  const handleEditSave = async (id) => {
    setSaving(true)
    setError(null)
    try {
      await updateMusicEvent(id, editValues)
      setEditingId(null)
      setEditPreview(null)
      fetchEvents()
    } catch {
      setError('Kunde inte uppdatera evenemanget')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <p>Laddar...</p>

  return (
    <>
      <div className="main-header">
        <h1>Veckans <em>Musik</em></h1>
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Musikevenemang</h3>
          <button className="btn btn-outline btn-sm" onClick={() => setShowAdd(!showAdd)}>
            + Lägg till
          </button>
        </div>

        {showAdd && (
          <div className="inline-form">
            <div className="form-row">
              <div>
                <label>Titel</label>
                <input type="text" placeholder="Artistnamn / titel" value={newEvent.title} onChange={(e) => setNewEvent({ ...newEvent, title: e.target.value })} />
              </div>
              <div>
                <label>Datum &amp; tid</label>
                <input type="datetime-local" value={newEvent.date} onChange={(e) => setNewEvent({ ...newEvent, date: e.target.value })} />
              </div>
            </div>
            <div style={{ marginBottom: '1rem' }}>
              <label>Beskrivning</label>
              <input type="text" placeholder="Kort beskrivning" value={newEvent.description} onChange={(e) => setNewEvent({ ...newEvent, description: e.target.value })} style={{ width: '100%' }} />
            </div>
            <div style={{ marginBottom: '1rem' }}>
              <label>Bild</label>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <label className="btn btn-outline btn-sm" style={{ cursor: 'pointer', marginBottom: 0 }}>
                  Välj bild
                  <input
                    type="file"
                    accept="image/*"
                    style={{ display: 'none' }}
                    onChange={(e) => {
                      const file = e.target.files[0]
                      if (!file) return
                      setImagePreview(URL.createObjectURL(file))
                      setNewEvent({ ...newEvent, imgPath: file.name })
                    }}
                  />
                </label>
                {imagePreview
                  ? <img src={imagePreview} alt="Förhandsvisning" style={{ width: '64px', height: '64px', objectFit: 'cover', borderRadius: '4px' }} />
                  : <span style={{ color: 'var(--text-muted-on-light)', fontSize: '0.85rem' }}>Ingen bild vald</span>
                }
              </div>
            </div>
            {error && <p style={{ color: 'red', margin: '0.5rem 0' }}>{error}</p>}
            <div className="form-actions">
              <button className="btn btn-outline btn-sm" onClick={() => { setShowAdd(false); setImagePreview(null) }}>Avbryt</button>
              <button className="btn btn-gold btn-sm" onClick={handleAdd} disabled={saving}>
                {saving ? 'Sparar...' : 'Lägg till'}
              </button>
            </div>
          </div>
        )}

        <table className="admin-table">
          <thead>
            <tr><th>Bild</th><th>Datum</th><th>Titel</th><th>Beskrivning</th><th></th></tr>
          </thead>
          <tbody>
            {events.map((event) => (
              <tr key={event.id}>
                {editingId === event.id ? (
                  <>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <label className="btn btn-outline btn-sm" style={{ cursor: 'pointer', marginBottom: 0, fontSize: '0.75rem' }}>
                          Välj bild
                          <input
                            type="file"
                            accept="image/*"
                            style={{ display: 'none' }}
                            onChange={(e) => {
                              const file = e.target.files[0]
                              if (!file) return
                              setEditPreview(URL.createObjectURL(file))
                              setEditValues({ ...editValues, imgPath: file.name })
                            }}
                          />
                        </label>
                        {editPreview
                          ? <img src={editPreview} alt="Förhandsvisning" style={{ width: '40px', height: '40px', objectFit: 'cover', borderRadius: '4px' }} />
                          : <span style={{ color: 'var(--text-muted-on-light)', fontSize: '0.75rem' }}>Ingen bild</span>
                        }
                      </div>
                    </td>
                    <td>
                      <input type="datetime-local" value={editValues.date} onChange={(e) => setEditValues({ ...editValues, date: e.target.value })} style={{ width: '180px' }} />
                    </td>
                    <td>
                      <input type="text" value={editValues.title} onChange={(e) => setEditValues({ ...editValues, title: e.target.value })} />
                    </td>
                    <td>
                      <input type="text" value={editValues.description} onChange={(e) => setEditValues({ ...editValues, description: e.target.value })} />
                    </td>
                    <td className="actions">
                      <button className="btn btn-outline btn-sm" onClick={() => { setEditingId(null); setEditPreview(null) }}>Avbryt</button>
                      <button className="btn btn-gold btn-sm" onClick={() => handleEditSave(event.id)} disabled={saving}>
                        {saving ? '...' : 'Spara'}
                      </button>
                    </td>
                  </>
                ) : (
                  <>
                    <td>
                      {event.imgPath
                        ? <img src={event.imgPath} alt={event.title} style={{ width: '48px', height: '48px', objectFit: 'cover', borderRadius: '4px' }} />
                        : <span style={{ color: 'var(--text-muted-on-light)', fontSize: '0.8rem' }}>Ingen bild</span>
                      }
                    </td>
                    <td>{formatDateTime(event.date)}</td>
                    <td>{event.title}</td>
                    <td>{event.description}</td>
                    <td className="actions">
                      <button className="btn btn-outline btn-sm" onClick={() => handleEditStart(event)}>Redigera</button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(event.id)}>Ta bort</button>
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

export default MusicAdmin
