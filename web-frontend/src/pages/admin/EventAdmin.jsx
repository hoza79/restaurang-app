import { useState } from 'react'

// exempeldata som matchar backend Event-modellen (title, description, eventTime, imagePath)
const initialEvents = [
  {
    id: 1,
    title: 'Wine Tasting Evening',
    description: 'Exclusive Italian wine tasting experience.',
    eventTime: '2026-03-10T19:00',
    imagePath: '',
  },
  {
    id: 2,
    title: 'Live Jazz Night',
    description: 'Dinner and live jazz performance.',
    eventTime: '2026-03-15T20:00',
    imagePath: '',
  },
  {
    id: 3,
    title: 'Rock n roll week',
    description: 'Brutal death metal from morning til night all week.',
    eventTime: '2026-03-20T17:00',
    imagePath: '',
  },
]

// formaterar datetime-strängen till läsbar form
function formatDateTime(dt) {
  if (!dt) return ''
  const d = new Date(dt)
  return d.toLocaleString('sv-SE', { dateStyle: 'short', timeStyle: 'short' })
}

function EventAdmin() {
  const [events, setEvents] = useState(initialEvents)
  const [showAdd, setShowAdd] = useState(false)
  const [newEvent, setNewEvent] = useState({ title: '', description: '', eventTime: '', imagePath: '' })
  const [editingId, setEditingId] = useState(null)
  const [editValues, setEditValues] = useState({})

  const handleAdd = () => {
    if (!newEvent.title) return
    setEvents([
      ...events,
      {
        id: Date.now(),
        title: newEvent.title,
        description: newEvent.description,
        eventTime: newEvent.eventTime,
        imagePath: newEvent.imagePath,
      }
    ])
    setNewEvent({ title: '', description: '', eventTime: '', imagePath: '' })
    setShowAdd(false)
  }

  const handleDelete = (id) => {
    setEvents(events.filter(e => e.id !== id))
  }

  const handleEditStart = (event) => {
    setEditingId(event.id)
    setEditValues({
      title: event.title,
      description: event.description,
      eventTime: event.eventTime,
      imagePath: event.imagePath || '',
    })
  }

  const handleEditSave = (id) => {
    setEvents(events.map(e =>
      e.id === id ? { ...e, ...editValues } : e
    ))
    setEditingId(null)
  }

  return (
    <>
      <div className="main-header">
        <h1>Veckans <em>Events</em></h1>
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Events</h3>
          <button className="btn btn-outline btn-sm" onClick={() => setShowAdd(!showAdd)}>
            + Lägg till event
          </button>
        </div>

        {showAdd && (
          <div className="inline-form">
            <div className="form-row">
              <div>
                <label>Titel</label>
                <input
                  type="text"
                  placeholder="Eventets namn"
                  value={newEvent.title}
                  onChange={(e) => setNewEvent({ ...newEvent, title: e.target.value })}
                />
              </div>
              <div>
                <label>Datum & tid</label>
                <input
                  type="datetime-local"
                  value={newEvent.eventTime}
                  onChange={(e) => setNewEvent({ ...newEvent, eventTime: e.target.value })}
                />
              </div>
            </div>
            <div style={{ marginBottom: '1rem' }}>
              <label>Beskrivning</label>
              <input
                type="text"
                placeholder="Kort beskrivning av eventet"
                value={newEvent.description}
                onChange={(e) => setNewEvent({ ...newEvent, description: e.target.value })}
              />
            </div>
            <div style={{ marginBottom: '1rem' }}>
              <label>Bild-URL</label>
              <input
                type="text"
                placeholder="https://..."
                value={newEvent.imagePath}
                onChange={(e) => setNewEvent({ ...newEvent, imagePath: e.target.value })}
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
            <tr>
              <th>Bild</th>
              <th>Titel</th>
              <th>Datum & tid</th>
              <th>Beskrivning</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {events.map((event) => (
              <tr key={event.id}>
                {editingId === event.id ? (
                  // redigeringsläge
                  <>
                    <td>
                      <input
                        type="text"
                        placeholder="Bild-URL"
                        value={editValues.imagePath}
                        onChange={(e) => setEditValues({ ...editValues, imagePath: e.target.value })}
                      />
                    </td>
                    <td>
                      <input
                        type="text"
                        value={editValues.title}
                        onChange={(e) => setEditValues({ ...editValues, title: e.target.value })}
                      />
                    </td>
                    <td>
                      <input
                        type="datetime-local"
                        value={editValues.eventTime}
                        onChange={(e) => setEditValues({ ...editValues, eventTime: e.target.value })}
                      />
                    </td>
                    <td>
                      <input
                        type="text"
                        value={editValues.description}
                        onChange={(e) => setEditValues({ ...editValues, description: e.target.value })}
                      />
                    </td>
                    <td className="actions">
                      <button className="btn btn-outline btn-sm" onClick={() => setEditingId(null)}>Avbryt</button>
                      <button className="btn btn-gold btn-sm" onClick={() => handleEditSave(event.id)}>Spara</button>
                    </td>
                  </>
                ) : (
                  // normalläge
                  <>
                    <td>
                      {event.imagePath
                        ? <img src={event.imagePath} alt={event.title} style={{ width: '48px', height: '48px', objectFit: 'cover', borderRadius: '4px' }} />
                        : <span style={{ color: 'var(--text-muted-on-light)', fontSize: '0.8rem' }}>Ingen bild</span>
                      }
                    </td>
                    <td>{event.title}</td>
                    <td>{formatDateTime(event.eventTime)}</td>
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
      </div>
    </>
  )
}

export default EventAdmin
