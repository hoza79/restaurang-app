import { useState } from 'react'
import { artists as initialArtists } from '../../data/mockData'

function MusicAdmin() {
  const [artistList, setArtistList] = useState(initialArtists)
  const [showAdd, setShowAdd] = useState(false)
  const [newArtist, setNewArtist] = useState({ name: '', genre: '', description: '', date: '', time: '' })

  const handleAdd = () => {
    if (!newArtist.name) return
    setArtistList([
      ...artistList,
      {
        id: Date.now(),
        name: newArtist.name,
        genre: newArtist.genre,
        description: newArtist.description,
        date: { day: '?', month: newArtist.date, weekday: '' },
        time: newArtist.time,
      }
    ])
    setNewArtist({ name: '', genre: '', description: '', date: '', time: '' })
    setShowAdd(false)
  }

  const handleDelete = (id) => {
    setArtistList(artistList.filter(a => a.id !== id))
  }

  return (
    <>
      <div className="main-header">
        <h1>Veckans <em>Musik</em></h1>
        <button className="btn btn-gold" onClick={() => alert('Sparad! (mockup)')}>
          Spara ändringar
        </button>
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Artister</h3>
          <button className="btn btn-outline btn-sm" onClick={() => setShowAdd(!showAdd)}>
            + Lägg till artist
          </button>
        </div>

        {showAdd && (
          <div className="inline-form">
            <div className="form-row">
              <div>
                <label>Artistnamn</label>
                <input type="text" placeholder="Namn" value={newArtist.name} onChange={(e) => setNewArtist({ ...newArtist, name: e.target.value })} />
              </div>
              <div>
                <label>Genre</label>
                <input type="text" placeholder="Jazz, Blues..." value={newArtist.genre} onChange={(e) => setNewArtist({ ...newArtist, genre: e.target.value })} />
              </div>
            </div>
            <div className="form-row">
              <div>
                <label>Datum</label>
                <input type="date" value={newArtist.date} onChange={(e) => setNewArtist({ ...newArtist, date: e.target.value })} />
              </div>
              <div>
                <label>Tid</label>
                <input type="text" placeholder="Kl. 19:00" value={newArtist.time} onChange={(e) => setNewArtist({ ...newArtist, time: e.target.value })} />
              </div>
            </div>
            <div style={{ marginBottom: '1rem' }}>
              <label>Beskrivning</label>
              <input type="text" placeholder="Kort beskrivning" value={newArtist.description} onChange={(e) => setNewArtist({ ...newArtist, description: e.target.value })} />
            </div>
            <div className="form-actions">
              <button className="btn btn-outline btn-sm" onClick={() => setShowAdd(false)}>Avbryt</button>
              <button className="btn btn-gold btn-sm" onClick={handleAdd}>Lägg till</button>
            </div>
          </div>
        )}

        <table className="admin-table">
          <thead>
            <tr><th>Datum</th><th>Artist</th><th>Genre</th><th>Tid</th><th></th></tr>
          </thead>
          <tbody>
            {artistList.map((artist) => (
              <tr key={artist.id}>
                <td>{artist.date.weekday} {artist.date.day} {artist.date.month}</td>
                <td>{artist.name}</td>
                <td>{artist.genre}</td>
                <td>{artist.time}</td>
                <td className="actions">
                  <button className="btn btn-outline btn-sm">Redigera</button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(artist.id)}>Ta bort</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}

export default MusicAdmin
